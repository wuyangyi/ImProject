package net.im_server.database.dao.proxy;

import net.bean.db.UnReadMessage;
import net.im_server.database.dao.ImpUnReadMessageDAO;
import net.im_server.database.dao.impl.EmpUnReadMessageDaoImpl;
import net.im_server.database.dbc.DatabaseConnection;

import java.util.List;


public class EmpUnReadMessageDaoProxy implements ImpUnReadMessageDAO {
	private DatabaseConnection dbc=null;
	private ImpUnReadMessageDAO dao=null;
	public EmpUnReadMessageDaoProxy() throws Exception{
		this.dbc=new DatabaseConnection();
		this.dao=new EmpUnReadMessageDaoImpl(this.dbc.getConnection());
	}


	@Override
	public boolean save(UnReadMessage unReadMessage) throws Exception {
		boolean flag = false;
		try {
			flag = this.dao.save(unReadMessage);
		} catch (Exception e) {
			throw e;
		} finally {
			this.dbc.close();
		}
		return flag;
	}

	@Override
	public boolean save(List<UnReadMessage> unReadMessage) throws Exception {
		boolean flag = false;
		try {
			flag = this.dao.save(unReadMessage);
		} catch (Exception e) {
			throw e;
		} finally {
			this.dbc.close();
		}
		return flag;
	}

	@Override
	public boolean remove(String receiver_id, String tag_id, int tag_type, long time) throws Exception {
		boolean flag = false;
		try {
			flag = this.dao.remove(receiver_id, tag_id, tag_type, time);
		} catch (Exception e) {
			throw e;
		} finally {
			this.dbc.close();
		}
		return flag;
	}
}
