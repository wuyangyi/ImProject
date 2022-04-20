package net.im_server.database.dao.proxy;

import net.bean.db.AdminUser;
import net.bean.db.SensitiveWord;
import net.im_server.database.dao.ImpAdminUserDAO;
import net.im_server.database.dao.ImpSensitiveWordDAO;
import net.im_server.database.dao.impl.EmpAdminUserDaoImpl;
import net.im_server.database.dao.impl.EmpSensitiveWordDaoImpl;
import net.im_server.database.dbc.DatabaseConnection;

import java.util.List;


public class EmpAdminUserDaoProxy implements ImpAdminUserDAO {
	private DatabaseConnection dbc=null;
	private ImpAdminUserDAO dao=null;
	public EmpAdminUserDaoProxy() throws Exception{
		this.dbc=new DatabaseConnection();
		this.dao=new EmpAdminUserDaoImpl(this.dbc.getConnection());
	}


	@Override
	public AdminUser findAdminUser(String username, String password) throws Exception {
		AdminUser adminUser = null;
		try {
			adminUser = this.dao.findAdminUser(username, password);
		} catch (Exception e) {
			throw e;
		} finally {
			this.dbc.close();
		}
		return adminUser;
	}
}
