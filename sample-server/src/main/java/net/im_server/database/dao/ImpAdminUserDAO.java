package net.im_server.database.dao;

import net.bean.db.AdminUser;
import net.bean.db.GroupBean;

import java.util.List;

public interface ImpAdminUserDAO {
    public AdminUser findAdminUser(String username, String password) throws Exception;
}
