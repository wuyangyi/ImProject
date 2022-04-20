package net.im_server.database.factory;

import net.bean.db.PushHistory;
import net.im_server.database.dao.proxy.EmpPushHistoryDaoProxy;
import net.utils.Hib;

public class IMPushFactory {

    /**
     * 保存到历史记录表
     * @param pushHistory
     */
    public static void savePushHistory(PushHistory pushHistory) {
        try {
            new EmpPushHistoryDaoProxy().save(pushHistory);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
