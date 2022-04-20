package net.im_server.database.dao.proxy;

import net.bean.card.MessageCard;
import net.bean.db.Message;
import net.im_server.database.dao.ImpMessageDAO;
import net.im_server.database.dao.impl.EmpMessageDaoImpl;
import net.im_server.database.dbc.DatabaseConnection;

import java.util.List;


public class EmpMessageDaoProxy implements ImpMessageDAO {
	private DatabaseConnection dbc=null;
	private ImpMessageDAO dao=null;
	public EmpMessageDaoProxy() throws Exception{
		this.dbc=new DatabaseConnection();
		this.dao=new EmpMessageDaoImpl(this.dbc.getConnection());
	}


	@Override
	public boolean save(Message message) throws Exception {
		boolean flag = false;
		try {
			flag = this.dao.save(message);
		} catch (Exception e) {
			throw e;
		} finally {
			this.dbc.close();
		}
		return flag;
	}

	@Override
	public List<MessageCard> selectUnReadMessage(String userId) throws Exception {
		List<MessageCard> all = null;
		try {
			all = this.dao.selectUnReadMessage(userId);
		} catch (Exception e) {
			throw e;
		} finally {
			this.dbc.close();
		}
		return all;
	}

	@Override
	public MessageCard findById(String id) throws Exception {
		MessageCard messageCard = null;
		try {
			messageCard = this.dao.findById(id);
		} catch (Exception e) {
			throw e;
		} finally {
			this.dbc.close();
		}
		return messageCard;
	}
}
