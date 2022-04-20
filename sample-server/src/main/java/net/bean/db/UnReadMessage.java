package net.bean.db;

import net.bean.card.MessageCard;

import javax.persistence.*;

//未读消息表
public class UnReadMessage {

    private int id;
    private String message_id;
    private String receiver_id;
    private String tag_id;
    private int tag_type;
    private long create_time;
    private long update_time;

    public UnReadMessage(){}

    public UnReadMessage(String message_id, String receiver_id, String tag_id, int tag_type) {
        this.message_id = message_id;
        this.receiver_id = receiver_id;
        this.tag_id = tag_id;
        this.tag_type = tag_type;
        this.create_time = System.currentTimeMillis() / 1000;
        this.update_time = System.currentTimeMillis() / 1000;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage_id() {
        return message_id;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }

    public String getReceiver_id() {
        return receiver_id;
    }

    public void setReceiver_id(String receiver_id) {
        this.receiver_id = receiver_id;
    }

    public String getTag_id() {
        return tag_id;
    }

    public void setTag_id(String tag_id) {
        this.tag_id = tag_id;
    }

    public int getTag_type() {
        return tag_type;
    }

    public void setTag_type(int tag_type) {
        this.tag_type = tag_type;
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
}
