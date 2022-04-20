package net.im_server.database.dao.proxy;

import net.bean.db.UserFollowBean;
import net.im_server.database.dao.ImpFollowDAO;
import net.im_server.database.dao.impl.EmpFollowDaoImpl;
import net.im_server.database.dbc.DatabaseConnection;


public class EmpFollowDaoProxy implements ImpFollowDAO {
	private DatabaseConnection dbc=null;
	private ImpFollowDAO dao=null;
	public EmpFollowDaoProxy() throws Exception{
		this.dbc=new DatabaseConnection();
		this.dao=new EmpFollowDaoImpl(this.dbc.getConnection());
	}


	@Override
	public UserFollowBean findFollow(String originId, String targetId, int isAtt) throws Exception {
		UserFollowBean userFollowBean = null;
		try {
			userFollowBean = this.dao.findFollow(originId, targetId, isAtt);
		} catch (Exception e) {
			throw e;
		} finally {
			this.dbc.close();
		}
		return userFollowBean;
	}
}
