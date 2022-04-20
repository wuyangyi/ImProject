package net.im_server.database.dao.proxy;

import net.bean.db.SessionBean;
import net.im_server.database.dao.ImpSessionDAO;
import net.im_server.database.dao.impl.EmpSessionDaoImpl;
import net.im_server.database.dbc.DatabaseConnection;

import java.util.List;


public class EmpSessionDaoProxy implements ImpSessionDAO {
	private DatabaseConnection dbc=null;
	private ImpSessionDAO dao=null;
	public EmpSessionDaoProxy() throws Exception{
		this.dbc=new DatabaseConnection();
		this.dao=new EmpSessionDaoImpl(this.dbc.getConnection());
	}


	@Override
	public List<SessionBean> findCanSendTemporarySession(String userId, String peerId) throws Exception {
		List<SessionBean> all = null;
		try {
			all = this.dao.findCanSendTemporarySession(userId, peerId);
		} catch (Exception e) {
			throw e;
		} finally {
			this.dbc.close();
		}
		return all;
	}

	@Override
	public boolean updateLastMsgId(String userId, String peerId, String lastMsgId) throws Exception {
		boolean flag = false;
		try {
			flag = this.dao.updateLastMsgId(userId, peerId, lastMsgId);
		} catch (Exception e) {
			throw e;
		} finally {
			this.dbc.close();
		}
		return flag;
	}

	@Override
	public boolean updateGroupLastMsgId(String groupId, String lastMsgId) throws Exception {
		boolean flag = false;
		try {
			flag = this.dao.updateGroupLastMsgId(groupId, lastMsgId);
		} catch (Exception e) {
			throw e;
		} finally {
			this.dbc.close();
		}
		return flag;
	}

	@Override
	public boolean updateSystemLastMsgId(String userId, String lastMsgId) throws Exception {
		boolean flag = false;
		try {
			flag = this.dao.updateSystemLastMsgId(userId, lastMsgId);
		} catch (Exception e) {
			throw e;
		} finally {
			this.dbc.close();
		}
		return flag;
	}
}
