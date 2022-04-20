package net.im_server.database.dao.proxy;

import net.bean.db.GroupBean;
import net.bean.db.GroupMemberBean;
import net.im_server.database.dao.ImpGroupDAO;
import net.im_server.database.dao.impl.EmpGroupDaoImpl;
import net.im_server.database.dbc.DatabaseConnection;

import java.util.List;

public class EmpGroupDaoProxy implements ImpGroupDAO {
	private DatabaseConnection dbc=null;
	private ImpGroupDAO dao=null;
	public EmpGroupDaoProxy() throws Exception{
		this.dbc=new DatabaseConnection();
		this.dao=new EmpGroupDaoImpl(this.dbc.getConnection());
	}

	@Override
	public List<GroupBean> getAllNeedCreateGroup() throws Exception {
		List<GroupBean> all = null;
		try {
			all = this.dao.getAllNeedCreateGroup();
		} catch (Exception e) {
			throw e;
		} finally {
			this.dbc.close();
		}
		return all;
	}

	@Override
	public List<String> getJoinedGroup(String userId) throws Exception {
		List<String> all = null;
		try {
			all = this.dao.getJoinedGroup(userId);
		} catch (Exception e) {
			throw e;
		} finally {
			this.dbc.close();
		}
		return all;
	}

	@Override
	public List<GroupMemberBean> getGroupMemberByGroupId(String groupId) throws Exception {
		List<GroupMemberBean> all = null;
		try {
			all = this.dao.getGroupMemberByGroupId(groupId);
		} catch (Exception e) {
			throw e;
		} finally {
			this.dbc.close();
		}
		return all;
	}

	@Override
	public GroupBean findGroupById(String groupId) throws Exception {
		GroupBean groupBean = null;
		try {
			groupBean = this.dao.findGroupById(groupId);
		} catch (Exception e) {
			throw e;
		} finally {
			this.dbc.close();
		}
		return groupBean;
	}

	@Override
	public GroupMemberBean findGroupMemberByUserIdAndGroupId(String groupId, String userId) throws Exception {
		GroupMemberBean groupMemberBean = null;
		try {
			groupMemberBean = this.dao.findGroupMemberByUserIdAndGroupId(groupId, userId);
		} catch (Exception e) {
			throw e;
		} finally {
			this.dbc.close();
		}
		return groupMemberBean;
	}
}
