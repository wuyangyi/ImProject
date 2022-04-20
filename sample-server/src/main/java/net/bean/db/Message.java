package net.bean.db;

import net.bean.card.MessageCard;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.sql.ResultSet;

/**
 * 消息
 */
@Entity
@Table(name = "ENT_MESSAGE")
public class Message {
    // 接收者类型
    public static final int RECEIVER_TYPE_SYSTEM = 0; //系统消息
    public static final int RECEIVER_TYPE_NONE = 1; //人的消息
    public static final int RECEIVER_TYPE_GROUP = 2; //群的消息

    private String id;
    private String sender_id;
    private String receive_id;
    private int msg_from;
    private String content;
    private String attach;
    private int attach_type;
    private int type;
    private int status;
    private int is_read;
    private long create_time;
    private long update_time;

    public Message() {}

    public Message(MessageCard messageCard) {
        this.id = messageCard.getId();
        this.sender_id = messageCard.getSenderId();
        this.receive_id = messageCard.getReceiverType() == 2 ? messageCard.getGroupId() : messageCard.getReceiverId();
        this.msg_from = messageCard.getReceiverType();
        this.content = messageCard.getContent();
        this.attach = messageCard.getAttach();
        this.attach_type = messageCard.getAttach_type();
        this.type = messageCard.getType();
        this.status = messageCard.getStatus();
        this.is_read = messageCard.isRead() ? 1 : 0;
        this.create_time = messageCard.getCreateAt() / 1000;
        this.update_time = System.currentTimeMillis() / 1000;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSender_id() {
        return sender_id;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }

    public String getReceive_id() {
        return receive_id;
    }

    public void setReceive_id(String receive_id) {
        this.receive_id = receive_id;
    }

    public int getMsg_from() {
        return msg_from;
    }

    public void setMsg_from(int msg_from) {
        this.msg_from = msg_from;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getIs_read() {
        return is_read;
    }

    public void setIs_read(int is_read) {
        this.is_read = is_read;
    }

    public long getCreate_time() {
        return create_time;
    }

    public void setCreate_time(long create_time) {
        this.create_time = create_time;
    }

    public long getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(long update_time) {
        this.update_time = update_time;
    }

    public static Message build(ResultSet rs) throws Exception {
        Message message =new Message();
        message.setId(rs.getString(1));
        message.setSender_id(rs.getString(2));
        message.setReceive_id(rs.getString(3));
        message.setMsg_from(rs.getInt(4));
        message.setContent(rs.getString(5));
        message.setAttach(rs.getString(6));
        message.setAttach_type(rs.getInt(7));
        message.setType(rs.getInt(8));
        message.setStatus(rs.getInt(9));
        message.setIs_read(rs.getInt(10));
        message.setCreate_time(rs.getLong(11));
        message.setUpdate_time(rs.getLong(12));
        return message;
    }

    public int getAttach_type() {
        return attach_type;
    }

    public void setAttach_type(int attach_type) {
        this.attach_type = attach_type;
    }
}
