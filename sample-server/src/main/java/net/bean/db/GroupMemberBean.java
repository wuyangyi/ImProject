package net.bean.db;


import javax.persistence.*;
import java.sql.ResultSet;

/**
 * 群成员
 */
public class GroupMemberBean {

    private String id;

    private String user_id;

    private String alias;

    private int type;

    private String group_id;

    private int notify_level;

    private int status;

    private int can_send;

    private long create_time;

    private long update_time;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public int getNotify_level() {
        return notify_level;
    }

    public void setNotify_level(int notify_level) {
        this.notify_level = notify_level;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getCan_send() {
        return can_send;
    }

    public void setCan_send(int can_send) {
        this.can_send = can_send;
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

    public static GroupMemberBean build(ResultSet rs) throws Exception {
        GroupMemberBean groupMemberBean =new GroupMemberBean();
        groupMemberBean.setId(rs.getString(1));
        groupMemberBean.setUser_id(rs.getString(2));
        groupMemberBean.setAlias(rs.getString(3));
        groupMemberBean.setType(rs.getInt(4));
        groupMemberBean.setGroup_id(rs.getString(5));
        groupMemberBean.setNotify_level(rs.getInt(7));
        groupMemberBean.setStatus(rs.getInt(8));
        groupMemberBean.setCan_send(rs.getInt(9));
        groupMemberBean.setCreate_time(rs.getLong(10));
        groupMemberBean.setUpdate_time(rs.getLong(11));
        return groupMemberBean;
    }
}
