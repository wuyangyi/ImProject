package net.im_server.database.dao.proxy;

import net.bean.db.Message;
import net.bean.db.PushHistory;
import net.im_server.database.dao.ImpMessageDAO;
import net.im_server.database.dao.ImpPushHistoryDAO;
import net.im_server.database.dao.impl.EmpMessageDaoImpl;
import net.im_server.database.dao.impl.EmpPushHistoryDaoImpl;
import net.im_server.database.dbc.DatabaseConnection;


public class EmpPushHistoryDaoProxy implements ImpPushHistoryDAO {
	private DatabaseConnection dbc=null;
	private ImpPushHistoryDAO dao=null;
	public EmpPushHistoryDaoProxy() throws Exception{
		this.dbc=new DatabaseConnection();
		this.dao=new EmpPushHistoryDaoImpl(this.dbc.getConnection());
	}


	@Override
	public boolean save(PushHistory pushHistory) throws Exception {
		boolean flag = false;
		try {
			flag = this.dao.save(pushHistory);
		} catch (Exception e) {
			throw e;
		} finally {
			this.dbc.close();
		}
		return flag;
	}
}
