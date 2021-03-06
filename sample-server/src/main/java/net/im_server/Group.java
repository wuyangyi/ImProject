package net.im_server;

import com.google.gson.Gson;
import net.bean.card.MessageCard;
import net.bean.card.PushCard;
import net.bean.db.GroupMemberBean;
import net.bean.db.Message;
import net.bean.db.UnReadMessage;
import net.box.StringReceivePacket;
import net.handle.ConnectorHandler;
import net.handle.ConnectorStringPacketChain;
import net.im_server.database.factory.Factory;
import net.im_server.database.factory.IMGroupFactory;
import net.im_server.database.factory.IMMessageFactory;
import net.im_server.database.factory.IMSessionFactory;
import net.utils.AesServerUtil;
import net.utils.TextUtil;

import java.util.ArrayList;
import java.util.List;

public class Group {
    private final String name;
    private final GroupMessageAdapter adapter;
    private final List<ConnectorHandler> members = new ArrayList<>();

    public Group(String name, GroupMessageAdapter adapter) {
        this.name = name;
        this.adapter = adapter;
    }

    public String getName() {
        return name;
    }

    public boolean addMember(ConnectorHandler handler) {
        synchronized (members) {
            if (!members.contains(handler)) {
                members.add(handler);
                handler.getStringPacketChain()
                        .appendLast(new ForwardConnectorStringPacketChain(name));
                System.out.println("net.im_server.Group[" + name + "] add new member:" + handler.getClientInfo());
                return true;
            }
        }
        return false;
    }

    public boolean removeMember(ConnectorHandler handler) {
        synchronized (members) {
            if (members.remove(handler)) {
                handler.getStringPacketChain()
                        .remove(ForwardConnectorStringPacketChain.class, name);
                System.out.println("net.im_server.Group[" + name + "] leave member:" + handler.getClientInfo());
                return true;
            }
        }
        return false;
    }

    private class ForwardConnectorStringPacketChain extends ConnectorStringPacketChain {

        public ForwardConnectorStringPacketChain(String groupId) {
            this.groupId = groupId;
            setGroup(groupId != null && !groupId.isEmpty());
        }

        @Override
        protected boolean consume(ConnectorHandler handler, StringReceivePacket stringReceivePacket) {
            // ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
            try {
                // ??????
                String str = AesServerUtil.decrypt(stringReceivePacket.entity());
                if (str == null) {
                    throw new NullPointerException("str is null!");
                }
                MessageCard messageCard = new Gson().fromJson(str, MessageCard.class);
                if (messageCard != null && messageCard.getReceiverType() == Message.RECEIVER_TYPE_GROUP && messageCard.getGroupId().equalsIgnoreCase(name)) {
                    // 1????????????????????????????????????????????????'*'???????????????
                    messageCard.dealContentWord();
                    messageCard.setCreateAt(System.currentTimeMillis());
                    int canSendType = Factory.checkCanSendMessageToGroup(messageCard.getSenderId(), messageCard.getGroupId());
                    boolean canSend = canSendType == 0;
                    if (canSend) {
                        messageCard.setStatus(MessageCard.STATUS_DONE);
                        // 2????????????????????????
                        messageCard = IMMessageFactory.save(messageCard);
                    } else {
                        messageCard.setStatus(MessageCard.STATUS_FAILED);
                    }
                    //3???????????????????????????(??????)?????????
//                    PushCard pushCard = new PushCard(AesServerUtil.encrypt(TextUtil.toJson(messageCard)), PushCard.PUSH_TYPE_MESSAGE);
//                    String msg = AesServerUtil.encrypt(TextUtil.toJson(pushCard));
                    String msg = PushCard.buildMessage(messageCard);
                    // ???????????? ?????????????????????????????????
                    if (canSend) {
                        List<GroupMemberBean> list = IMGroupFactory.getGroupMember(name);
                        if (messageCard != null && !list.isEmpty()) {
                            List<UnReadMessage> unReadMessages = new ArrayList<>();
                            for (GroupMemberBean memberBean : list) {
                                if (!memberBean.getUser_id().equalsIgnoreCase(messageCard.getSenderId())) {
                                    unReadMessages.add(new UnReadMessage(
                                            messageCard.getId(),
                                            memberBean.getUser_id(),
                                            messageCard.getGroupId(),
                                            messageCard.getReceiverType()));
                                }
                            }
                            IMMessageFactory.saveUnReadMessage(unReadMessages);
                        }
                        if (messageCard != null) {
                            IMSessionFactory.updateGroupLastMsgId(groupId, messageCard.getId());
                        }

                        System.out.println("group-message:" + msg);
                        synchronized (members) {
                            for (ConnectorHandler member : members) {
                                // ?????????????????????????????????????????????????????????
                                adapter.sendMessageToClient(member, msg);
                            }
                        }
                    } else {
                        // ????????????????????????
                        adapter.sendMessageToClient(handler, msg);
                        // ??????????????????
                        String errorStr = "????????????";
                        // ??????????????????
                        if (canSendType == 1) {
                            errorStr = "??????????????????"; //????????????????????????????????????????????????????????????
                        } else if (canSendType == 2) {
                            errorStr = "?????????????????????";
                        } else if (canSendType == 3) {
                            errorStr = "???????????????";
                        }
                        MessageCard systemMessage = MessageCard.buildSystemMessage(messageCard.getSenderId(), messageCard.getGroupId(), messageCard.getReceiverType(), errorStr);
                        String systemStr = PushCard.buildMessage(systemMessage);
                        adapter.sendMessageToClient(handler, systemStr);
                    }
                    return true;
                }
            } catch (Exception e) {
                return false;
            }
            return false;
        }
    }

    interface GroupMessageAdapter {
        void sendMessageToClient(ConnectorHandler handler, String msg);
    }
}
