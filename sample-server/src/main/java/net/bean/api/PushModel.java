package net.bean.api;

import com.google.gson.annotations.Expose;

public class PushModel {
    @Expose
    private String user_id;
    @Expose
    private String group_id;
    @Expose
    private String message;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    @Override
    public String toString() {
        return "PushModel{" +
                "user_id='" + user_id + '\'' +
                ", group_id='" + group_id + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
