package net.im_server.database.dao;

import net.bean.db.Message;
import net.bean.db.PushHistory;

public interface ImpPushHistoryDAO {
	public boolean save(PushHistory pushHistory) throws Exception;
}
