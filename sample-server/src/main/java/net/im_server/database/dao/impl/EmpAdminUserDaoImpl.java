package net.im_server.database.dao.impl;

import net.bean.db.AdminUser;
import net.bean.db.SensitiveWord;
import net.im_server.database.dao.ImpAdminUserDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class EmpAdminUserDaoImpl implements ImpAdminUserDAO {
	private Connection conn=null;
	private PreparedStatement pstmt=null;
	public EmpAdminUserDaoImpl(Connection conn){
		this.conn=conn;
	}


	@Override
	public AdminUser findAdminUser(String username, String password) throws Exception {
		AdminUser adminUser = null;
		String sql = "select * from ent_admin_user where username = '" + username + "' and password = '" + password + "' and status != -1 and can_req_im = 1";

		try{
			this.pstmt=this.conn.prepareStatement(sql);
			ResultSet rs=(ResultSet) this.pstmt.executeQuery();
			if(rs.next()){
				adminUser = new AdminUser();
				adminUser.setId(rs.getInt(1));
				adminUser.setUsername(rs.getString(2));
				adminUser.setStatus(rs.getInt(7));
				adminUser.setCan_req_im(rs.getInt(8));
			}
		}catch (Exception e) {
			throw e;
		}finally{
			if(this.pstmt!=null){
				try{
					this.pstmt.close();
				}catch (Exception e) {
					throw e;
				}
			}
		}
		return adminUser;
	}
}
