package net.im_server.database.dao.proxy;

import net.bean.db.SensitiveWord;
import net.im_server.database.dao.ImpSensitiveWordDAO;
import net.im_server.database.dao.impl.EmpSensitiveWordDaoImpl;
import net.im_server.database.dbc.DatabaseConnection;

import java.util.List;


public class EmpSensitiveWordDaoProxy implements ImpSensitiveWordDAO {
	private DatabaseConnection dbc=null;
	private ImpSensitiveWordDAO dao=null;
	public EmpSensitiveWordDaoProxy() throws Exception{
		this.dbc=new DatabaseConnection();
		this.dao=new EmpSensitiveWordDaoImpl(this.dbc.getConnection());
	}


	@Override
	public List<SensitiveWord> findAllWord() throws Exception {
		List<SensitiveWord> all = null;
		try {
			all = this.dao.findAllWord();
		} catch (Exception e) {
			throw e;
		} finally {
			this.dbc.close();
		}
		return all;
	}
}
