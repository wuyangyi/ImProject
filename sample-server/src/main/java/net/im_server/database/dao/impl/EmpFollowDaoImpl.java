package net.im_server.database.dao.impl;

import net.bean.db.UserFollowBean;
import net.im_server.database.dao.ImpFollowDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class EmpFollowDaoImpl implements ImpFollowDAO {
	private Connection conn=null;
	private PreparedStatement pstmt=null;
	public EmpFollowDaoImpl(Connection conn){
		this.conn=conn;
	}


	@Override
	public UserFollowBean findFollow(String originId, String targetId, int isAtt) throws Exception {
		UserFollowBean username = null;
		String sql = "select * from ent_user_follow where originId = '" + originId + "' and targetId = '" + targetId + "' and isAtt = " + isAtt;

		try{
			this.pstmt=this.conn.prepareStatement(sql);
			ResultSet rs=(ResultSet) this.pstmt.executeQuery();
			if(rs.next()){
				username = new UserFollowBean();
				username.setId(rs.getInt(1));
				username.setOriginId(rs.getString(2));
				username.setTargetId(rs.getString(3));
				username.setAlias(rs.getString(4));
				username.setCreate_time(rs.getLong(5));
				username.setUpdate_time(rs.getLong(6));
				username.setIsAtt(rs.getInt(7));
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
		return username;
	}
}
