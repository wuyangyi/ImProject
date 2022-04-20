package net.im_server.database.dao.impl;

import net.bean.db.SessionBean;
import net.im_server.database.dao.ImpSessionDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class EmpSessionDaoImpl implements ImpSessionDAO {
	private Connection conn=null;
	private PreparedStatement pstmt=null;
	public EmpSessionDaoImpl(Connection conn){
		this.conn=conn;
	}

	@Override
	public List<SessionBean> findCanSendTemporarySession(String userId, String peerId) throws Exception {
		List<SessionBean> all = new ArrayList<>();
		long nowTime = System.currentTimeMillis() / 1000;
		String sql = "select * from ent_session where userId = '" + userId + "' and peerId = '" + peerId + "' and type = 1 and status = 0 and (over_time = 0 or over_time > " + nowTime + ")";
		try{
			this.pstmt=this.conn.prepareStatement(sql);
			ResultSet rs=(ResultSet) this.pstmt.executeQuery();
			while(rs.next()){
				SessionBean sessionBean =new SessionBean();
				sessionBean.setId(rs.getInt(1));
				sessionBean.setUserId(rs.getString(2));
				sessionBean.setPeerId(rs.getString(3));
				sessionBean.setLast_msg_id(rs.getString(4));
				sessionBean.setType(rs.getInt(5));
				sessionBean.setStatus(rs.getInt(6));
				sessionBean.setOver_time(rs.getLong(7));
				sessionBean.setCreate_time(rs.getLong(8));
				sessionBean.setUpdate_time(rs.getLong(9));
				all.add(sessionBean);
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
		return all;
	}

	@Override
	public boolean updateLastMsgId(String userId, String peerId, String lastMsgId) throws Exception {
		int time = (int) (System.currentTimeMillis() / 1000);
		String sql = "update ent_session set last_msg_id='" + lastMsgId + "',update_time=" + time + " where userId='" + userId + "' and peerId='" + peerId + "' and (type=1 or type=2)";
		return doUpdate(sql);
	}

	@Override
	public boolean updateGroupLastMsgId(String groupId, String lastMsgId) throws Exception {
		int time = (int) (System.currentTimeMillis() / 1000);
		String sql = "update ent_session set last_msg_id='" + lastMsgId + "',update_time=" + time + " where (peerId='" + groupId + "' and type=3)";
		return doUpdate(sql);
	}

	@Override
	public boolean updateSystemLastMsgId(String userId, String lastMsgId) throws Exception {
		int time = (int) (System.currentTimeMillis() / 1000);
		String sql = "update ent_session set last_msg_id='" + lastMsgId + "',update_time=" + time + " where (userId='" + userId + "' and type=0)";
		return doUpdate(sql);
	}

	private boolean doUpdate(String sql) throws Exception {
		System.out.println(sql);
		boolean flag = false;
		try {
			this.pstmt=this.conn.prepareStatement(sql);
			int n=this.pstmt.executeUpdate();
			if(n>0){
				flag = true;
			}
		} catch (Exception e) {
			throw e;
		} finally {
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
