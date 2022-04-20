package net.im_server.database.factory;

import net.bean.db.SessionBean;
import net.im_server.database.dao.proxy.EmpSessionDaoProxy;
import net.utils.DataUtil;

import java.util.List;

public class IMSessionFactory {
    public static List<SessionBean> findCanSendTemporarySession(String userId, String peerId) {
        List<SessionBean> list = null;
        try {
            list = new EmpSessionDaoProxy().findCanSendTemporarySession(userId, peerId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean updateLastMsgId(String userId, String peerId, String lastMsgId) {
        boolean flag = false;
        try {
            flag = new EmpSessionDaoProxy().updateLastMsgId(DataUtil.getMaxUserId(userId, peerId), DataUtil.getMinUserId(userId, peerId), lastMsgId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    public static boolean updateGroupLastMsgId(String groupId, String lastMsgId) {
        boolean flag = false;
        try {
            flag = new EmpSessionDaoProxy().updateGroupLastMsgId(groupId, lastMsgId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    public static boolean updateSystemLastMsgId(String userId, String lastMsgId) {
        boolean flag = false;
        try {
            flag = new EmpSessionDaoProxy().updateSystemLastMsgId(userId, lastMsgId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }
}
