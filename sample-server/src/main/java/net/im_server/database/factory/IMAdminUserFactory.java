package net.im_server.database.factory;


import net.bean.db.AdminUser;
import net.im_server.database.dao.proxy.EmpAdminUserDaoProxy;
import net.utils.Hib;

/**
 * 管理员工厂类
 */
public class IMAdminUserFactory {

    public static AdminUser findAdminUser(String username, String password) {
        try {
            if (username != null && password != null) {
                return new EmpAdminUserDaoProxy().findAdminUser(username, password);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
