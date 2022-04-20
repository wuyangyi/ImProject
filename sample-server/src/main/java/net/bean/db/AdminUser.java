package net.bean.db;

import javax.persistence.*;
import java.security.Principal;

// 管理员用户
public class AdminUser implements Principal {

    private int id;

    private String username;

    private String password;

    private int can_req_im;

    private int status;

    private long create_time;

    private long update_time;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getCan_req_im() {
        return can_req_im;
    }

    public void setCan_req_im(int can_req_im) {
        this.can_req_im = can_req_im;
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

    @Override
    public String getName() {
        return username;
    }
}
