package net.im_server.database.dao.impl;

import net.bean.db.Message;
import net.bean.db.PushHistory;
import net.im_server.database.dao.ImpMessageDAO;
import net.im_server.database.dao.ImpPushHistoryDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class EmpPushHistoryDaoImpl implements ImpPushHistoryDAO {
	private Connection conn=null;
	private PreparedStatement pstmt=null;
	public EmpPushHistoryDaoImpl(Connection conn){
		this.conn=conn;
	}

	@Override
	public boolean save(PushHistory pushHistory) throws Exception {
		boolean flag = false;
		try{
			String sql="INSERT INTO ent_push_history(entity,entity_type,receiver_id,status,push_id,create_time,update_time) VALUES(?,?,?,?,?,?,?)";
			this.pstmt=this.conn.prepareStatement(sql);
			this.pstmt.setString(1, pushHistory.getEntity());
			this.pstmt.setInt(2, pushHistory.getEntity_type());
			this.pstmt.setString(3, pushHistory.getReceiver_id());
			this.pstmt.setInt(4, pushHistory.getStatus());
			this.pstmt.setString(5, pushHistory.getPush_id());
			this.pstmt.setLong(6, pushHistory.getCreate_time());
			this.pstmt.setLong(7, pushHistory.getUpdate_time());
			int n=this.pstmt.executeUpdate();
			if (n > 0) {
				flag = true;
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
		return flag;
	}
}
