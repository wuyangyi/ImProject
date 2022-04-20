package net.im_server.database.factory;

import net.bean.db.UserFollowBean;
import net.im_server.database.dao.proxy.EmpFollowDaoProxy;

public class IMUserFollowFactory {

    public static UserFollowBean getUserFollow(String originId, String targetId, int isAtt) {
        try {
            return new EmpFollowDaoProxy().findFollow(originId, targetId, isAtt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
