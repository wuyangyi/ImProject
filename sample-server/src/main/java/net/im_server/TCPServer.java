package net.im_server;

import com.google.gson.Gson;
import net.bean.card.MessageCard;
import net.bean.card.PushCard;
import net.bean.db.GroupBean;
import net.bean.db.Message;
import net.bean.db.PushHistory;
import net.bean.db.UnReadMessage;
import net.box.StringReceivePacket;
import net.core.Connector;
import net.core.ScheduleJob;
import net.core.schedule.IdleTimeoutScheduleJob;
import net.foo.Foo;
import net.handle.ConnectorCloseChain;
import net.handle.ConnectorHandler;
import net.handle.ConnectorStringPacketChain;
import net.im_server.database.factory.*;
import net.utils.AesServerUtil;
import net.utils.CloseUtils;
import net.utils.TextUtil;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class TCPServer implements ServerAcceptor.AcceptListener, Group.GroupMessageAdapter {
    private final int port;
    private final File cachePath;
    private final List<ConnectorHandler> connectorHandlerList = new ArrayList<>();
    private final Map<String, Group> groups = new HashMap<>();
    private ServerAcceptor acceptor;
    private ServerSocketChannel server;

    private final ServerStatistics statistics = new ServerStatistics();

    public TCPServer(int port, File cachePath) {
        this.port = port;
        this.cachePath = cachePath;
        // 根据数据库群聊创建所有群（这里是启动服务器，所有用户都不在线，故不需要加入用户到群）
        synchronized (groups) {
            List<GroupBean> groups = IMGroupFactory.getAllNeedCreateGroup();
            for (GroupBean groupBean : groups) {
                if (!this.groups.containsKey(groupBean.getId())) {
                    System.out.println("create group:" + groupBean.getId());
                    this.groups.put(groupBean.getId(), new Group(groupBean.getId(), this));
                }
            }
        }
    }

    public boolean start() {
        try {
            ServerAcceptor acceptor = new ServerAcceptor(this);

            ServerSocketChannel server = ServerSocketChannel.open();
            // 设置非阻塞模式
            server.configureBlocking(false);
            // 绑定本地断开
            server.socket().bind(new InetSocketAddress(port));
            // 创建客户端连接到达监听
            server.register(acceptor.getSelector(), SelectionKey.OP_ACCEPT);

            this.server = server;
            this.acceptor = acceptor;

            // 线程需要启动
            acceptor.start();
            if (acceptor.awaitRunning()) {
                System.out.println("服务器准备就绪~");
                System.out.println("服务器信息：" + server.getLocalAddress().toString());
                return true;
            } else {
                System.out.println("启动异常！");
                return false;
            }

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    void stop() {
        if (acceptor != null) {
            acceptor.exit();
        }

        ConnectorHandler[] connectorHandlers;
        synchronized (connectorHandlerList) {
            connectorHandlers = connectorHandlerList.toArray(new ConnectorHandler[0]);
            connectorHandlerList.clear();
        }

        for (ConnectorHandler connectorHandler : connectorHandlers) {
            connectorHandler.exit();
        }

        CloseUtils.close(server);
    }

    /**
     * 进行广播发送，发送给所有客户端
     * @param str 消息
     */
    public void broadcast(String str) {
//        str = "系统通知：" + str;
        ConnectorHandler[] connectorHandlers;
        synchronized (connectorHandlerList) {
            connectorHandlers = connectorHandlerList.toArray(new ConnectorHandler[0]);
        }
        for (ConnectorHandler connectorHandler : connectorHandlers) {
            sendMessageToClient(connectorHandler, str);
        }
    }

    /**
     * 给某用户推送消息
     * @param userId 用户id
     * @param str 消息
     */
    public void unicast(String userId, String str) {
        synchronized (connectorHandlerList) {
            ConnectorHandler[] connectorHandlers;
            connectorHandlers = connectorHandlerList.toArray(new ConnectorHandler[0]);
            for (ConnectorHandler connectorHandler : connectorHandlers) {
                if (connectorHandler.getUserId() != null && connectorHandler.getUserId().equalsIgnoreCase(userId)) {
                    sendMessageToClient(connectorHandler, str);
                }
            }
        }
    }

    /**
     * 给多个用户推送消息
     * @param userIds 用户id
     * @param str 消息
     */
    public void multicast(String[] userIds, String str) {
        if (userIds == null || userIds.length == 0) {
            return;
        }
        synchronized (connectorHandlerList) {
            ConnectorHandler[] connectorHandlers;
            connectorHandlers = connectorHandlerList.toArray(new ConnectorHandler[0]);
            List<String> users = Arrays.asList(userIds);
            for (ConnectorHandler connectorHandler : connectorHandlers) {
                if (connectorHandler.getUserId() != null && users.contains(connectorHandler.getUserId())) {
                    sendMessageToClient(connectorHandler, str);
                }
//                for (String userId : userIds) {
//                    if (connectorHandler.getUserId().equalsIgnoreCase(userId)) {
//                        sendMessageToClient(connectorHandler, str);
//                    }
//                }
            }
        }
    }

    /**
     * 发送消息给某个客户端
     * @param handler 客户端
     * @param msg 消息
     */
    @Override
    public void sendMessageToClient(ConnectorHandler handler, String msg) {
        if (handler == null) {
            System.out.println("send message-error:" + msg);
            return;
        }
        System.out.println("send message:" + msg);
        if (handler.getUserId() != null) {
            // 保存推送历史
            PushHistory pushHistory = new PushHistory(msg, handler.getUserId(), handler.getKey().toString(), 0);
            IMPushFactory.savePushHistory(pushHistory);
        }
        // 发送
        handler.send(msg);
        statistics.sendSize++;
    }

    Object[] getStatusString() {
        return new String[] {
                "客户端数量：" + connectorHandlerList.size(),
                "发送数量：" + statistics.sendSize,
                "接收数量：" + statistics.receiveSize
        };
    }

    /**
     * 新客户端链接时回调
     * @param channel 新客户端
     */
    @Override
    public void onNewSocketArrived(SocketChannel channel) {
        try {
            ConnectorHandler connectorHandler = new ConnectorHandler(channel, cachePath);
            System.out.println(connectorHandler.getClientInfo() + ":Connected!");

            connectorHandler.getStringPacketChain()
                    .appendLast(statistics.statisticsChain())
                    .appendLast(new ParseCommandConnectorStringPacketChain())
                    .appendLast(new ParseAudioStreamCommandStringPacketChain());

            connectorHandler.getCloseChain()
                    .appendLast(new RemoveAudioQueueOnConnectorClosedChain())
                    .appendLast(new RemoveQueueOnConnectorClosedChain());

            // 空闲任务调度
            ScheduleJob scheduleJob = new IdleTimeoutScheduleJob(5, TimeUnit.SECONDS, connectorHandler);
            connectorHandler.schedule(scheduleJob);

            synchronized (connectorHandlerList) {
                connectorHandlerList.add(connectorHandler);
                System.out.println("client number：" + connectorHandlerList.size());
            }

            // 回送客户端在服务器的唯一标志
            sendMessageToClient(connectorHandler, Foo.COMMAND_INFO_NAME + connectorHandler.getKey().toString());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("client error：" + e.getMessage());
        }
    }


    private class RemoveQueueOnConnectorClosedChain extends ConnectorCloseChain {
        @Override
        protected boolean consume(ConnectorHandler handler, Connector connector) {
            // 将用户移除所有群聊
            removeUserAllGroup(handler);
            synchronized (connectorHandlerList) {
                connectorHandlerList.remove(handler);
            }
            return true;
        }
    }

    /**
     * 加入群聊
     * @param groupId 群名称（群id）
     * @param handler 用户
     */
    private void joinGroup(String groupId, ConnectorHandler handler) {
        synchronized (groups) {
            Group group = groups.get(groupId);
            if (group != null) {
                group.addMember(handler);
            }
        }
    }

    /**
     * 用户移除所有群聊
     * @param handler 用户
     */
    private void removeUserAllGroup(ConnectorHandler handler) {
        synchronized (groups) {
            Iterator<Map.Entry<String, Group>> iterator = groups.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Group> entry = iterator.next();
                Group group = entry.getValue();
                if (group != null) {
                    // 移除群聊的客户端
                    group.removeMember(handler);
                }
            }
        }
    }

    /**
     * 创建群聊
     * @param groupId
     * @return
     */
    public boolean createGroup(String groupId) {
        synchronized (groups) {
            System.out.println("create group:" + groupId);
            this.groups.put(groupId, new Group(groupId, this));
        }
        return true;
    }

    /**
     * 将某人加入(移除)群聊
     * @param groupId 群聊id
     * @param userId 用户id
     * @return 是否加入成功
     */
    public boolean joinOrRemoveMemberToGroup(String groupId, String userId, boolean isJoin) {
        if (groupId == null || groupId.isEmpty() || userId == null || userId.isEmpty()) {
            return false;
        }
        Group group;
        synchronized (groups) {
            group = groups.get(groupId);
            if (group == null) {
                return false;
            }
        }
        synchronized (connectorHandlerList) {
            List<ConnectorHandler> handlers = new ArrayList<>();
            for (ConnectorHandler connectorHandler : connectorHandlerList) {
                if (connectorHandler.getUserId() != null && connectorHandler.getUserId().equalsIgnoreCase(userId)) {
                    handlers.add(connectorHandler);
                }
            }
            if (handlers.isEmpty()) {
                return false;
            }
            for (ConnectorHandler handler : handlers) {
                if (isJoin) {
                    group.addMember(handler);
                } else {
                    group.removeMember(handler);
                }
            }
        }
        return true;

    }


    private class ParseCommandConnectorStringPacketChain extends ConnectorStringPacketChain {

        @Override
        protected boolean consume(ConnectorHandler handler, StringReceivePacket stringReceivePacket) {
            String str = stringReceivePacket.entity();
            System.out.println(handler.getKey() + ":" + str);
            if (str.startsWith(Foo.COMMAND_LOGIN)) { //登录（绑定userId）
                String userId = str.substring(Foo.COMMAND_LOGIN.length());
                if (!userId.isEmpty()) {
                    handler.setUserId(userId);
                    // 加入已加入的群聊
                    // 1、数据库获取加入的群id
                    // 2、在此加入
                    // 3、推送未读消息
                    List<String> groupIds = IMGroupFactory.getJoinedGroup(userId);
                    if (!groupIds.isEmpty()) {
                        for (String groupId : groupIds) {
                            System.out.println("join group:" + groupId);
                            joinGroup(groupId, handler);
                        }
                    }
                    // 推送未读消息
                    List<MessageCard> list = IMMessageFactory.getAllUnReadMessage(userId);
                    if (list != null && !list.isEmpty()) {
                        String content = TextUtil.toJson(list);
                        PushCard pushCard = new PushCard(AesServerUtil.encrypt(content), PushCard.PUSH_TYPE_UNREAD_MESSAGE);
                        String pushString = TextUtil.toJson(pushCard);
                        String msg = AesServerUtil.encrypt(pushString);
                        sendMessageToClient(handler, msg);
                    }

                }
                return true;
            } else if (str.startsWith(Foo.COMMAND_LOGOUT)) { // 退出登录（解绑userId）
                if (handler.getUserId() != null) {
                    // 退出所有群聊和语音房间
                    removeUserAllGroup(handler);
                    // 找到了流链接才走断开语音房间
                    if (audioCmdToStreamMap.get(handler) != null) {
                        removeAudioRoom(handler);
                    }
                }
                handler.setUserId(null);
                return true;
            } else if (str.startsWith(Foo.COMMAND_GROUP_JOIN)) {
                // 加入群聊，这里不允许客户端自己加入，所有的都得通过接口调用这里的方法加群聊，这里直接返回false
                return false;
//                net.im_server.Group group = groups.get(Foo.DEFAULT_GROUP_NAME);
//                if (group.addMember(handler)) {
//                    sendMessageToClient(handler, "Join net.im_server.Group:" + group.getName());
//                }
//                return true;
            } else if (str.startsWith(Foo.COMMAND_GROUP_LEAVE)) {
                // 同上
                return false;
//                net.im_server.Group group = groups.get(Foo.DEFAULT_GROUP_NAME);
//                if (group.removeMember(handler)) {
//                    sendMessageToClient(handler, "Leave net.im_server.Group:" + group.getName());
//                }
//                return true;
            } else if (str.startsWith(Foo.COMMAND_MESSAGE_READ)) {
                String[] data = str.substring(Foo.COMMAND_MESSAGE_READ.length()).split(" ");
                if (data.length >= 3) {
                    try {
                        String receiverId = data[0];
                        String tagId = data[1];
                        int tagType = Integer.parseInt(data[2]);
                        IMMessageFactory.removeUnReadMessage(receiverId, tagId, tagType, System.currentTimeMillis() / 1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return true;
            }
            return false;
        }


        @Override
        protected boolean consumeAgain(ConnectorHandler handler, StringReceivePacket stringReceivePacket) {
            String str = stringReceivePacket.entity();
            // 这里是收到客户端的消息（消息解密，解析json消息，存储消息到数据库，然后转发消息给接收者，回送发送者消息发送成功）
            try {
                // 解密
                str = AesServerUtil.decrypt(str);
                if (str == null) {
                    throw new NullPointerException("str is null!");
                }
                /*
                 * 获取到消息实体后完整流程:
                 * 1、不合规消息拦截，替换敏感字符为'*'，长度对应 (这里暂不做)
                 * 2、消息存储数据库
                 * 3、回送消息发送成功的实体
                 * 4、消息发送给接收者(这里的消息是单聊)
                 */
                MessageCard messageCard = new Gson().fromJson(str, MessageCard.class);
                if (messageCard != null) {
                    if (messageCard.getReceiverType() == Message.RECEIVER_TYPE_NONE) {
                        System.out.println("consumeAgain:" + str);
                        // 1、不合规消息拦截，替换敏感字符为'*'，长度对应
                        messageCard.dealContentWord();
                        messageCard.setCreateAt(System.currentTimeMillis());
                        // 检查是否可以进行会话
                        boolean canSend = Factory.checkUserCanSendMessage(messageCard.getSenderId(), messageCard.getReceiverId());
                        if (!canSend) {
                            // 不能进行会话
                            messageCard.setStatus(MessageCard.STATUS_FAILED);
                        } else {
                            // 2、消息存储数据库
                            messageCard.setStatus(MessageCard.STATUS_DONE);
                            IMMessageFactory.save(messageCard);
                        }
                        //3、回送消息发送成功（失败）的实体
                        String msg = PushCard.buildMessage(messageCard);
                        // 若可以发送 优先根据用户回送，从而保证多端都会收到消息回送
                        if (handler.getUserId() != null && canSend) {
                            unicast(handler.getUserId(), msg);
                        } else {
                            sendMessageToClient(handler, msg);
                        }
                        if (canSend) {
                            // 保存消息未读
                            IMMessageFactory.saveUnReadMessage(new UnReadMessage(messageCard.getId(), messageCard.getReceiverId(), messageCard.getSenderId(), messageCard.getReceiverType()));
                            // 更改会话最后一条消息id
                            IMSessionFactory.updateLastMsgId(messageCard.getSenderId(), messageCard.getReceiverId(), messageCard.getId());
                            //4、消息发送给接收者(这里的消息是单聊)
                            if (messageCard.getReceiverId() != null) {
                                unicast(messageCard.getReceiverId(), msg);
                            }
                        } else {
                            // 回送非好友关系 (这里消息里面的接收者id还得是接收者id，因为客户端用receiverId作为sessionId)
                            MessageCard systemMessage = MessageCard.buildSystemMessage(messageCard.getSenderId(), messageCard.getReceiverId(), messageCard.getReceiverType(), "你们还不是好友关系~");
                            String systemStr = PushCard.buildMessage(systemMessage);
                            sendMessageToClient(handler, systemStr);
                        }
                        return true;
                    } else if (messageCard.getReceiverType() == Message.RECEIVER_TYPE_GROUP) {
                        // 这里若是群消息，表面没有创建群会话，这里基本可以确定群不存在
                        messageCard.dealContentWord();
                        messageCard.setCreateAt(System.currentTimeMillis());
                        messageCard.setStatus(MessageCard.STATUS_FAILED);
                        String msg = PushCard.buildMessage(messageCard);
                        sendMessageToClient(handler, msg);
                        // 回送群不存在
                        MessageCard systemMessage = MessageCard.buildSystemMessage(messageCard.getSenderId(), messageCard.getGroupId(), messageCard.getReceiverType(), "当前群不存在");
                        String systemStr = PushCard.buildMessage(systemMessage);
                        sendMessageToClient(handler, systemStr);
                    } else {
                        // 这里都不是直接返回失败
                        messageCard.dealContentWord();
                        messageCard.setCreateAt(System.currentTimeMillis());
                        messageCard.setStatus(MessageCard.STATUS_FAILED);
                        String msg = PushCard.buildMessage(messageCard);
                        sendMessageToClient(handler, msg);
                    }
                }

            } catch (Exception e) {
                // 捡漏的模式，当我们第一遍未消费，然后又没有加入到群满足然没有后续的节点消费
                // 此时我们进行二次消费，返回发送过来的消息
//                sendMessageToClient(handler, str);
                e.printStackTrace();
            }
            // 这里是最后消费的地方，直接返回true吧
            return false;
        }
    }

    /**
     * 从全部列表中通过Key查询到一个链接
     * @param key
     * @return
     */
    private ConnectorHandler findConnectorFromKey(String key) {
        synchronized (connectorHandlerList) {
            for (ConnectorHandler connectorHandler : connectorHandlerList) {
                if (connectorHandler.getKey().toString().equalsIgnoreCase(key)) {
                    return connectorHandler;
                }
            }
        }
        return null;
    }

    // 音频命令控制流与数据流传输链接印射表
    private final HashMap<ConnectorHandler, ConnectorHandler> audioCmdToStreamMap = new HashMap<>(100);
    private final HashMap<ConnectorHandler, ConnectorHandler> audioStreamToCmdMap = new HashMap<>(100);

    /**
     * 用户离开语音房间
     * @param handler
     */
    private void removeAudioRoom(ConnectorHandler handler) {
        ConnectorHandler audioStreamConnector = findAudioStreamConnector(handler);
        if (audioStreamConnector != null) {
            // 任意一人离开都销毁房间
            dissolveRoom(audioStreamConnector);
            // 发送离开消息
            sendMessageToClient(handler, Foo.COMMAND_INFO_AUDIO_STOP);
        }
    }

    /**
     * 通过音频命令控制链接寻找数据传输流链接，未找到则发送错误
     * @param handler
     * @return
     */
    private ConnectorHandler findAudioStreamConnector(ConnectorHandler handler) {
        ConnectorHandler connectorHandler = audioCmdToStreamMap.get(handler);
        if (connectorHandler == null) {
            sendMessageToClient(handler, Foo.COMMAND_INFO_AUDIO_ERROR);
            return null;
        } else {
            return connectorHandler;
        }
    }

    /**
     * 生成一个当前缓存列表中没有的房间
     * @return 当前创建的房间
     */
    private AudioRoom createNewRoom() {
        AudioRoom room;
        do {
            room = new AudioRoom();
        } while (audioRoomMap.containsKey(room.getRoomCode()));
        // 添加到缓存列表
        audioRoomMap.put(room.getRoomCode(), room);
        return room;
    }

    /**
     * 加入房间
     * @return 是否加入成功
     */
    private boolean joinRoom(AudioRoom room, ConnectorHandler streamConnector) {
        if (room.enterRoom(streamConnector)) {
            audioStreamRoomMap.put(streamConnector, room);
            return true;
        }
        return false;
    }

    /**
     * 解散房间
     * @param streamConnector 解散者
     */
    private void dissolveRoom(ConnectorHandler streamConnector) {
        AudioRoom room = audioStreamRoomMap.get(streamConnector);
        if (room == null) {
            return;
        }
        System.out.println("level room：" + room.getRoomCode());

        ConnectorHandler[] connectors = room.getConnectors();
        for (ConnectorHandler connector : connectors) {
            if (connector != streamConnector) {
                // 退出房间 并 获取对方
                sendStreamConnectorMessage(connector, Foo.COMMAND_INFO_AUDIO_STOP);
            }
            // 解除桥接
            connector.unBindToBridge();
            // 移除缓存
            audioStreamToCmdMap.remove(connector);
        }
        // 销毁房间
        audioRoomMap.remove(room.getRoomCode());
    }

    /**
     * 通过音频数据传输链接寻找命令控制链接
     */
    private ConnectorHandler findAudioCmdConnector(ConnectorHandler handler) {
        System.out.println("findAudioCmdConnector:" + audioStreamToCmdMap.size());
        return audioStreamToCmdMap.get(handler);
    }

    // 房间引射表，房间号-房间的映射
    private final HashMap<String, AudioRoom> audioRoomMap = new HashMap<>(50);
    // 链接与房间的映射表，音频链接-房间的映射
    private final HashMap<ConnectorHandler, AudioRoom> audioStreamRoomMap = new HashMap<>();

    /**
     * 音频命令解析
     */
    private class ParseAudioStreamCommandStringPacketChain extends ConnectorStringPacketChain {

        @Override
        protected boolean consume(ConnectorHandler handler, StringReceivePacket stringReceivePacket) {
            String str = stringReceivePacket.entity();
            if (str.startsWith(Foo.COMMAND_CONNECTOR_BIND)) {
                // 绑定命令，也就是将音频流绑定到当前的命令流上
                String key = str.substring(Foo.COMMAND_CONNECTOR_BIND.length());
                ConnectorHandler audioStreamConnector = findConnectorFromKey(key);
                System.out.println("bind:" + key);
                if (audioStreamConnector != null) {
                    // 添加绑定关系
                    audioCmdToStreamMap.put(handler, audioStreamConnector);
                    audioStreamToCmdMap.put(audioStreamConnector, handler);

                    // 转换为桥接模式
                    audioStreamConnector.changeToBridge();
                }
            } else if (str.startsWith(Foo.COMMAND_AUDIO_CREATE_ROOM)) {
                // 创建房间操作
                ConnectorHandler audioStreamConnector = findAudioStreamConnector(handler);
                if (audioStreamConnector != null) {
                    // 随机创建房间
                    AudioRoom room = createNewRoom();
                    // 加入一个客户端
                    joinRoom(room, audioStreamConnector);
                    // 发送成功消息
                    sendMessageToClient(handler, Foo.COMMAND_INFO_AUDIO_ROOM + room.getRoomCode());
                    // 通知接收者
                    String receiverId = str.substring(Foo.COMMAND_AUDIO_CREATE_ROOM.length());
                    room.setReceiverId(receiverId);
                    // 发送给接收者，请求语音通话
                    if (!receiverId.isEmpty()) {
                        unicast(receiverId, Foo.COMMAND_INFO_AUDIO_REQUEST + room.getRoomCode() + " " + handler.getUserId());
                    }
                }
            } else if (str.startsWith(Foo.COMMAND_AUDIO_LEAVE_ROOM)) {
                String roomCode = str.substring(Foo.COMMAND_AUDIO_LEAVE_ROOM.length());
                AudioRoom room = audioRoomMap.get(roomCode);
                if (room != null) {
                    if (!room.isEnable()) {
                        // 通知挂断
                        if (room.getReceiverId() != null) {
                            unicast(room.getReceiverId(), Foo.COMMAND_INFO_AUDIO_REFUSE + roomCode);
                        }
                        ConnectorHandler[] connectors = room.getConnectors();
                        for (ConnectorHandler connector : connectors) {
                            // 退出房间 并 获取对方
                            sendStreamConnectorMessage(connector, Foo.COMMAND_INFO_AUDIO_STOP);
                            // 解除桥接
                            connector.unBindToBridge();
                            // 移除缓存
                            audioStreamToCmdMap.remove(connector);
                        }
                    } else {
                        // 离开房间命令
                        removeAudioRoom(handler);
                    }
                }
            } else if (str.startsWith(Foo.COMMAND_AUDIO_JOIN_ROOM)) {
                // 加入房间操作
                ConnectorHandler audioStreamConnector = findAudioStreamConnector(handler);
                if (audioStreamConnector != null) {
                    // 取得房间号
                    String roomCode = str.substring(Foo.COMMAND_AUDIO_JOIN_ROOM.length());
                    AudioRoom room = audioRoomMap.get(roomCode);
                    // 如果找到了房间就走后面流程
                    if (room != null && joinRoom(room, audioStreamConnector)) {
                        // 对方
                        ConnectorHandler theOtherHandler = room.getTheOtherHandler(audioStreamConnector);

                        // 成功加入房间
                        sendMessageToClient(handler, Foo.COMMAND_INFO_AUDIO_START);
                        // 给对方发送可开始聊天的消息
                        sendStreamConnectorMessage(theOtherHandler, Foo.COMMAND_INFO_AUDIO_START);
                        // 互相搭建好桥
                        theOtherHandler.bindToBridge(audioStreamConnector.getSender());
                        audioStreamConnector.bindToBridge(theOtherHandler.getSender());

                    } else {
                        // 房间没找到，房间人员已满
                        sendMessageToClient(handler, Foo.COMMAND_INFO_AUDIO_ERROR);
                    }
                }
            } else {
                System.out.println("receiver-false:" + str);
                return false;
            }
            System.out.println("receiver-true:" + str);
            return true;
        }
    }

    /**
     * 链接关闭时退出音频房间等操作
     */
    private class RemoveAudioQueueOnConnectorClosedChain extends ConnectorCloseChain {

        @Override
        protected boolean consume(ConnectorHandler handler, Connector connector) {
            if (audioCmdToStreamMap.containsKey(handler)) {
                // 命令链接断开
                audioCmdToStreamMap.remove(handler);
            } else if (audioStreamToCmdMap.containsKey(handler)) {
                System.out.println("Stream disconnected：" + handler.getKey());
                // 流断开
                audioStreamToCmdMap.remove(handler);
                // 解散房间
                dissolveRoom(handler);
            }
            return false;
        }
    }

    /**
     * 给链接流对应的命令控制连接发送信息
     */
    private void sendStreamConnectorMessage(ConnectorHandler streamConnector, String msg) {
        if (streamConnector != null) {
            ConnectorHandler audioCmdConnector = findAudioCmdConnector(streamConnector);
            sendMessageToClient(audioCmdConnector, msg);
        }
    }
}
