package net.im_server.database.dao.impl;

import net.bean.db.AdminUser;
import net.bean.db.GroupBean;
import net.bean.db.GroupMemberBean;
import net.im_server.Group;
import net.im_server.database.dao.ImpGroupDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class EmpGroupDaoImpl implements ImpGroupDAO {
	private Connection conn=null;
	private PreparedStatement pstmt=null;
	public EmpGroupDaoImpl(Connection conn){
		this.conn=conn;
	}


	@Override
	public List<GroupBean> getAllNeedCreateGroup() throws Exception {
		List<GroupBean> all = new ArrayList<GroupBean>();
		String sql="SELECT * FROM ent_group where status = 0";
		try{
			this.pstmt=this.conn.prepareStatement(sql);
			ResultSet rs=(ResultSet) this.pstmt.executeQuery();
			while(rs.next()){
				GroupBean groupBean = GroupBean.buildGroup(rs);
				all.add(groupBean);
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
	public List<String> getJoinedGroup(String userId) throws Exception {
		List<String> all = new ArrayList<String>();
		String sql = "SELECT * FROM ent_member where user_id = '"+ userId + "'";
		try{
			this.pstmt=this.conn.prepareStatement(sql);
			ResultSet rs=(ResultSet) this.pstmt.executeQuery();
			while(rs.next()){
				String id = rs.getString("group_id");
				if (id !=null && !id.isEmpty()) {
					all.add(id);
				}
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
	public List<GroupMemberBean> getGroupMemberByGroupId(String groupId) throws Exception {
		List<GroupMemberBean> all = new ArrayList<GroupMemberBean>();
		if (groupId == null || groupId.isEmpty()) {
			return all;
		}
		String sql ="select * from ent_member where group_id = '"+groupId+"'";
		try{
			this.pstmt=this.conn.prepareStatement(sql);
			ResultSet rs= this.pstmt.executeQuery();
			while(rs.next()){
				GroupMemberBean groupMemberBean = GroupMemberBean.build(rs);
				all.add(groupMemberBean);
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
	public GroupBean findGroupById(String groupId) throws Exception {
		GroupBean groupBean = null;
		String sql = "select * from ent_group where id = '" + groupId + "'";

		try{
			this.pstmt=this.conn.prepareStatement(sql);
			ResultSet rs=(ResultSet) this.pstmt.executeQuery();
			if(rs.next()){
				groupBean = GroupBean.buildGroup(rs);
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
		return groupBean;
	}

	@Override
	public GroupMemberBean findGroupMemberByUserIdAndGroupId(String groupId, String userId) throws Exception {
		GroupMemberBean groupMemberBean = null;
		String sql = "select * from ent_member where user_id = '" + userId + "' and group_id = '" + groupId + "'";

		try{
			this.pstmt=this.conn.prepareStatement(sql);
			ResultSet rs=(ResultSet) this.pstmt.executeQuery();
			if(rs.next()){
				groupMemberBean = GroupMemberBean.build(rs);
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
		return groupMemberBean;
	}
}
