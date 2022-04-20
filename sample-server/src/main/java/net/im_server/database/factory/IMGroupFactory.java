package net.im_server.database.factory;


import net.bean.db.GroupBean;
import net.bean.db.GroupMemberBean;
import net.im_server.database.dao.proxy.EmpGroupDaoProxy;

import java.util.ArrayList;
import java.util.List;

/**
 * 群工厂类
 */
public class IMGroupFactory {

    /**
     * 获取所有有效群聊
     * @return
     */
    public static List<GroupBean> getAllNeedCreateGroup() {
        List<GroupBean> groupBeans = null;
        try {
            groupBeans = new EmpGroupDaoProxy().getAllNeedCreateGroup();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (groupBeans == null) {
            groupBeans = new ArrayList<>();
        }
        return groupBeans;
    }

    /**
     * 获取用户所加入的群聊id
     * @param userId 用户id
     * @return 群聊id列表
     */
    public static List<String> getJoinedGroup(String userId) {
        List<String> groupIds = null;
        try {
            groupIds = new EmpGroupDaoProxy().getJoinedGroup(userId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (groupIds == null) {
            groupIds = new ArrayList<>();
        }
        return groupIds;
    }

    /**
     * 获取群成员
     * @param groupId 群id
     * @return
     */
    public static List<GroupMemberBean> getGroupMember(String groupId) {
        List<GroupMemberBean> groupMemberBeans = null;
        try {
            groupMemberBeans = new EmpGroupDaoProxy().getGroupMemberByGroupId(groupId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (groupMemberBeans == null) {
            groupMemberBeans = new ArrayList<>();
        }
        return groupMemberBeans;
    }

    /**
     * 通过id找群
     * @param groupId
     * @return
     */
    public static GroupBean findGroupById(String groupId) {
        GroupBean groupBean = null;
        try {
            groupBean = new EmpGroupDaoProxy().findGroupById(groupId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return groupBean;
    }

    public static GroupMemberBean findGroupMemberByUserIdAndGroupId(String groupId, String userId) {
        GroupMemberBean groupMemberBean = null;
        try {
            groupMemberBean = new EmpGroupDaoProxy().findGroupMemberByUserIdAndGroupId(groupId, userId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return groupMemberBean;
    }
}
