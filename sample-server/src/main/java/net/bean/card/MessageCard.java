package net.bean.card;


import net.bean.db.Message;
import net.bean.db.SensitiveWord;
import net.im_server.database.factory.IMSensitiveWordFactory;
import net.utils.AesServerUtil;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class MessageCard {
    // 消息状态
    public static final int STATUS_FAILED = -1; // 发送失败状态
    public static final int STATUS_CREATED = 0; // 创建状态（发送中）
    public static final int STATUS_DONE = 1; // 正常状态
    public static final int STATUS_WITHDRAW = 2; // 已撤回
    // 消息类型
    public static final int TYPE_STR = 1; //文本
    public static final int TYPE_PIC = 2; //图片
    public static final int TYPE_FILE = 3; //文件
    public static final int TYPE_AUDIO = 4; //语言
    public static final int TYPE_ADDRESS = 5; //位置
    public static final int TYPE_SYSTEM = 7; //系统提示
    public static final int TYPE_SYSTEM_MESSAGE = 8; //系统消息（用于系统消息会话部分）

    private String id;
    private String content;
    private String attach;
    private int attach_type;
    private int type;
    private int receiverType;
    private long createAt;
    private int status;
    private String senderId;
    private String receiverId;
    private String groupId;
    private boolean isRead;

    // 构建系统消息
    public static MessageCard buildSystemMessage(String senderId, String receiverId, int receiverType, String msg) {
        MessageCard messageCard = new MessageCard();
        messageCard.setId(UUID.randomUUID().toString());
        messageCard.setContent(AesServerUtil.encrypt(msg));
        messageCard.setType(TYPE_SYSTEM);
        messageCard.setSenderId(senderId);
        messageCard.setReceiverType(receiverType);
        messageCard.setCreateAt(System.currentTimeMillis());
        messageCard.setStatus(STATUS_DONE);
        if (receiverType == Message.RECEIVER_TYPE_NONE) {
            messageCard.setReceiverId(receiverId);
        } else if (receiverType == Message.RECEIVER_TYPE_GROUP) {
            messageCard.setGroupId(receiverId);
        }
        messageCard.setRead(false);
        return messageCard;
    }

    public MessageCard() {}

    public MessageCard(Message message) {
        this.id = message.getId();
        this.content = message.getContent();
        this.attach = message.getAttach();
        this.attach_type = message.getAttach_type();
        this.type = message.getType();
        this.receiverType = message.getMsg_from();
        this.createAt = message.getCreate_time() * 1000;
        this.status = message.getStatus();
        this.senderId = message.getSender_id();
        if (message.getMsg_from() == Message.RECEIVER_TYPE_NONE) {
            this.receiverId = message.getReceive_id();
        } else if (message.getMsg_from() == Message.RECEIVER_TYPE_GROUP) {
            this.groupId = message.getReceive_id();
        }
        this.isRead = message.getIs_read() == 1;
    }

    /**
     * 处理敏感词
     */
    public void dealContentWord() {
        if (this.type == TYPE_STR && this.content != null && !this.content.isEmpty()) {
            String str = AesServerUtil.decrypt(content);
            if (str == null || str.isEmpty()) {
                return;
            }
            List<SensitiveWord> list = IMSensitiveWordFactory.findAllWord();
            for (SensitiveWord word : list) {
                if (str.contains(word.getWord())) {
                    str = str.replaceAll(word.getWord(), String.join("", Collections.nCopies(word.getWord().length(), "*")));
                }
            }
            this.content = AesServerUtil.encrypt(str);
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getReceiverType() {
        return receiverType;
    }

    public void setReceiverType(int receiverType) {
        this.receiverType = receiverType;
    }

    public long getCreateAt() {
        return createAt;
    }

    public void setCreateAt(long createAt) {
        this.createAt = createAt;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public int getAttach_type() {
        return attach_type;
    }

    public void setAttach_type(int attach_type) {
        this.attach_type = attach_type;
    }
}
