package net.bean.db;

public class UserFollowBean {
    public static final int TYPE_NO_ATT = 0; //未关注
    public static final int TYPE_ATT = 1; // 已关注

    private int id;
    private String originId;
    private String targetId;
    private String alias;
    private long create_time;
    private long update_time;
    private int isAtt;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOriginId() {
        return originId;
    }

    public void setOriginId(String originId) {
        this.originId = originId;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
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

    public int getIsAtt() {
        return isAtt;
    }

    public void setIsAtt(int isAtt) {
        this.isAtt = isAtt;
    }
}
