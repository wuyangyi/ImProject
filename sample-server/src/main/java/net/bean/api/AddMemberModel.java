package net.bean.api;

import com.google.gson.annotations.Expose;

public class AddMemberModel {
    @Expose
    private String group_id;
    @Expose
    private String member_id;


    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getMember_id() {
        return member_id;
    }

    public void setMember_id(String member_id) {
        this.member_id = member_id;
    }

    @Override
    public String toString() {
        return "AddMemberModel{" +
                "group_id='" + group_id + '\'' +
                ", member_id='" + member_id + '\'' +
                '}';
    }
}
