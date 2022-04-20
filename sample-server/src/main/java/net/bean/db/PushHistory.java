package net.bean.db;


import javax.persistence.*;

//消息接收历史记录表
public class PushHistory {

    private int id;

    //推送的具体实体存储的都是JSON字符串
    private String entity;

    private int entity_type;

    private String receiver_id;

    private String push_id;

    private int status;

    private long create_time;

    private long update_time;

    public PushHistory() {}

    public PushHistory(String entity, String receiver_id, String push_id, int status) {
        this.entity = entity;
        this.receiver_id = receiver_id;
        this.push_id = push_id;
        this.status = status;
        this.create_time = System.currentTimeMillis() / 1000;
        this.update_time = System.currentTimeMillis() / 1000;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public String getReceiver_id() {
        return receiver_id;
    }

    public void setReceiver_id(String receiver_id) {
        this.receiver_id = receiver_id;
    }

    public String getPush_id() {
        return push_id;
    }

    public void setPush_id(String push_id) {
        this.push_id = push_id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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

    public int getEntity_type() {
        return entity_type;
    }

    public void setEntity_type(int entity_type) {
        this.entity_type = entity_type;
    }
}
