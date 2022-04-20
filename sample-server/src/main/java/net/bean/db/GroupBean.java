package net.bean.db;

import javax.persistence.*;
import java.sql.ResultSet;

/**
 * 群
 */
public class GroupBean {

    private String id;

    private String group_name;

    private int avatar;

    private String description;

    private String owner_id;

    private String circle_id;

    private int need_apply;

    private int type;

    private int status;

    private long create_time;

    private long update_time;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public int getAvatar() {
        return avatar;
    }

    public void setAvatar(int avatar) {
        this.avatar = avatar;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(String owner_id) {
        this.owner_id = owner_id;
    }

    public String getCircle_id() {
        return circle_id;
    }

    public void setCircle_id(String circle_id) {
        this.circle_id = circle_id;
    }

    public int getNeed_apply() {
        return need_apply;
    }

    public void setNeed_apply(int need_apply) {
        this.need_apply = need_apply;
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

    public static GroupBean buildGroup(ResultSet rs) throws Exception {
        GroupBean groupBean = new GroupBean();
        groupBean.setId(rs.getString(1));
        groupBean.setGroup_name(rs.getString(2));
        groupBean.setAvatar(rs.getInt(3));
        groupBean.setDescription(rs.getString(4));
        groupBean.setOwner_id(rs.getString(5));
        groupBean.setCircle_id(rs.getString(6));
        groupBean.setNeed_apply(rs.getInt(7));
        groupBean.setType(rs.getInt(8));
        groupBean.setStatus(rs.getInt(9));
        groupBean.setCreate_time(rs.getInt(10));
        groupBean.setUpdate_time(rs.getInt(11));
        return groupBean;
    }
}
