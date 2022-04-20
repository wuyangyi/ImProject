package net.bean.api;

import com.google.gson.annotations.Expose;

public class CreateGroupModel {
    @Expose
    private String group_id;

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }
}
