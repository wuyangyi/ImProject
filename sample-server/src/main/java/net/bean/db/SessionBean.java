package net.bean.db;

// 会话
public class SessionBean {
    public static final int TYPE_SYSTEM = 0; // 系统会话
    public static final int TYPE_TEMPORARY = 1; //临时会话
    public static final int TYPE_FRIEND = 2; //好友会话
    public static final int TYPE_GROUP = 3; //群会话

    public static final int STATUS_NONE = 0; //正常
    public static final int STATUS_DELETE = 1; //删除
    public static final int STATUS_OVER = 2; //结束

    private int id;
    private String userId;
    private String peerId;
    private int type;
    private int status;
    private String last_msg_id;
    private long over_time;
    private long create_time;
    private long update_time;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPeerId() {
        return peerId;
    }

    public void setPeerId(String peerId) {
        this.peerId = peerId;
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

    public String getLast_msg_id() {
        return last_msg_id;
    }

    public void setLast_msg_id(String last_msg_id) {
        this.last_msg_id = last_msg_id;
    }

    public long getOver_time() {
        return over_time;
    }

    public void setOver_time(long over_time) {
        this.over_time = over_time;
    }
}
