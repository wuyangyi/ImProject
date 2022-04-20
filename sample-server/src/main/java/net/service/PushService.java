package net.service;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import net.bean.api.PushModel;
import net.bean.api.base.ResponseModel;
import net.bean.card.MessageCard;
import net.bean.card.PushCard;
import net.bean.db.GroupMemberBean;
import net.bean.db.Message;
import net.bean.db.UnReadMessage;
import net.im_server.ImServer;
import net.im_server.database.factory.IMGroupFactory;
import net.im_server.database.factory.IMMessageFactory;
import net.im_server.database.factory.IMSessionFactory;
import net.utils.AesServerUtil;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Path("/push")
public class PushService extends BaseService {

    /**
     * 推送
     * @return
     */
    @POST
    @Path("/send")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<String> push(PushModel model) {
        if (model == null || Strings.isNullOrEmpty(model.getUser_id()) || Strings.isNullOrEmpty(model.getMessage())) {
            return ResponseModel.buildParameterError();
        }
        ImServer.getInstance().getTcpServer().unicast(model.getUser_id(), model.getMessage());
        return ResponseModel.buildOk("推送成功");
    }

    /**
     * 消息推送
     * @return
     */
    @POST
    @Path("/send_msg")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<String> pushMsg(PushModel model) {
        if (model == null || Strings.isNullOrEmpty(model.getUser_id()) || Strings.isNullOrEmpty(model.getMessage())) {
            return ResponseModel.buildParameterError();
        }
        String[] userIds = saveMsg(model.getMessage()); // 消息的接收者和发送者
        if (userIds == null) {
            userIds = model.getUser_id().split(","); // 这个是接口那边提供的用户
        }
        ImServer.getInstance().getTcpServer().multicast(userIds, model.getMessage());
        return ResponseModel.buildOk("推送成功");
    }

    /**
     * 多用户推送
     * @return
     */
    @POST
    @Path("/send_multiple")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<String> pushMultiple(PushModel model) {
        if (model == null || Strings.isNullOrEmpty(model.getUser_id()) || Strings.isNullOrEmpty(model.getMessage())) {
            return ResponseModel.buildParameterError();
        }
        String[] userIds = model.getUser_id().split(",");
        if (userIds.length == 0) {
            return ResponseModel.buildParameterError();
        }
        ImServer.getInstance().getTcpServer().multicast(userIds, model.getMessage());
        return ResponseModel.buildOk("推送成功");
    }

    /**
     * 群消息推送
     * @return
     */
    @POST
    @Path("/send_multiple_msg")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<String> pushMultipleMsg(PushModel model) {
        if (model == null || Strings.isNullOrEmpty(model.getUser_id()) || Strings.isNullOrEmpty(model.getMessage())) {
            return ResponseModel.buildParameterError();
        }
        String[] userIds = model.getUser_id().split(",");
        if (userIds.length == 0) {
            return ResponseModel.buildParameterError();
        }
        saveGroupMsg(model.getMessage(), userIds);
        ImServer.getInstance().getTcpServer().multicast(userIds, model.getMessage());
        return ResponseModel.buildOk("推送成功");
    }

    /**
     * 消息推送到群聊
     * @return
     */
    @POST
    @Path("/sendGroup")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<String> pushToGroup(PushModel model) {
        System.out.println(model == null ? "model null~" : model.toString());
        if (model == null || Strings.isNullOrEmpty(model.getGroup_id()) || Strings.isNullOrEmpty(model.getMessage())) {
            return ResponseModel.buildParameterError();
        }
        String groupId = model.getGroup_id();
        // 群消息转发，这里只转发给在线的用户，不在线用户登录后根据上次获取消息的时间，获取剩余的关于自己的消息（未读消息数据由会话存入）
        try {
            // 解密
            String str = AesServerUtil.decrypt(model.getMessage());
            if (str == null) {
                System.out.println("str null~");
                return ResponseModel.buildOk("推送失败");
            }
            MessageCard messageCard = new Gson().fromJson(str, MessageCard.class);
            if (messageCard != null && messageCard.getReceiverType() == Message.RECEIVER_TYPE_GROUP && messageCard.getGroupId().equalsIgnoreCase(groupId)) {
                // 1、不合规消息拦截，替换敏感字符为'*'，长度对应
                messageCard.dealContentWord();
                if (messageCard.getCreateAt() <= 0) {
                    messageCard.setCreateAt(System.currentTimeMillis());
                }
//                boolean canSend = messageCard.getType() == MessageCard.TYPE_SYSTEM || messageCard.getType() == MessageCard.TYPE_SYSTEM_MESSAGE;

//                System.out.println("canSend：" + canSend);
//                if (canSend) {
                    messageCard.setStatus(MessageCard.STATUS_DONE);
                    // 2、消息存储数据库
                    messageCard = IMMessageFactory.save(messageCard);
                    String msg = PushCard.buildMessage(messageCard);
                    // 保存未读 所有群成员
                    List<GroupMemberBean> list = IMGroupFactory.getGroupMember(groupId);
                    System.out.println("list: " + list.size());
                    if (messageCard != null && !list.isEmpty()) {
                        List<UnReadMessage> unReadMessages = new ArrayList<>();
                        for (GroupMemberBean memberBean : list) {
                            unReadMessages.add(new UnReadMessage(
                                    messageCard.getId(),
                                    memberBean.getUser_id(),
                                    messageCard.getGroupId(),
                                    messageCard.getReceiverType()));
                            // 推送给其成员
                            ImServer.getInstance().getTcpServer().unicast(memberBean.getUser_id(), msg);
                        }
                        IMMessageFactory.saveUnReadMessage(unReadMessages);
                    }
                    if (messageCard != null) {
                        IMSessionFactory.updateGroupLastMsgId(groupId, messageCard.getId());
                    }
                } else {
                    return ResponseModel.buildOk("推送失败");
                }
//            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseModel.buildOk("推送失败");
        }
        return ResponseModel.buildOk("推送成功");
    }


    /**
     * 保存用户聊天消息
     * @param msg 消息
     * @return 接收者和发送者用户
     */
    private String[] saveMsg(String msg) {
        if (msg == null) {
            return null;
        }
        try {
            // 解密
            String str = AesServerUtil.decrypt(msg);
            if (str == null) {
                return null;
            }
            PushCard pushCard = new Gson().fromJson(str, PushCard.class);
            if (pushCard == null || pushCard.getContent() == null) {
                return null;
            }
            String msgStr = AesServerUtil.decrypt(pushCard.getContent());
            if (msgStr == null) {
                return null;
            }
            MessageCard messageCard = new Gson().fromJson(msgStr, MessageCard.class);
            if (messageCard != null) {
                IMMessageFactory.save(messageCard);
                IMSessionFactory.updateLastMsgId(messageCard.getSenderId(), messageCard.getReceiverId(), messageCard.getId());
                //这里保存未读消息
                List<UnReadMessage> list = new ArrayList<>();
                list.add(new UnReadMessage(
                        messageCard.getId(),
                        messageCard.getReceiverId(),
                        messageCard.getSenderId(),
                        messageCard.getReceiverType()));
                list.add(new UnReadMessage(
                        messageCard.getId(),
                        messageCard.getSenderId(),
                        messageCard.getReceiverId(),
                        messageCard.getReceiverType()));
                IMMessageFactory.saveUnReadMessage(list);
                return new String[]{messageCard.getSenderId(), messageCard.getReceiverId()};
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 保存群聊天消息
    private void saveGroupMsg(String msg, String[] users) {
        if (msg == null || users.length == 0) {
            return;
        }
        try {
            // 解密
            String str = AesServerUtil.decrypt(msg);
            if (str == null) {
                return;
            }
            PushCard pushCard = new Gson().fromJson(str, PushCard.class);
            if (pushCard == null || pushCard.getContent() == null) {
                return;
            }
            String msgStr = AesServerUtil.decrypt(pushCard.getContent());
            if (msgStr == null) {
                return;
            }
            MessageCard messageCard = new Gson().fromJson(msgStr, MessageCard.class);
            if (messageCard != null) {
                IMMessageFactory.save(messageCard);
                IMSessionFactory.updateLastMsgId(messageCard.getSenderId(), messageCard.getReceiverId(), messageCard.getId());
                //这里发送者不保存未读消息
                List<UnReadMessage> list = new ArrayList<>();
                for (String user : users) {
                    list.add(new UnReadMessage(
                            messageCard.getId(),
                            user,
                            messageCard.getSenderId(),
                            messageCard.getReceiverType()
                    ));
                }
                IMMessageFactory.saveUnReadMessage(list);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
