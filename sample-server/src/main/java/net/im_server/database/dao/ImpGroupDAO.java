package net.im_server.database.dao;

import net.bean.db.GroupBean;
import net.bean.db.GroupMemberBean;

import java.util.List;

public interface ImpGroupDAO {
    /**
     * 获取所以有效群
     * @return
     * @throws Exception
     */
    public List<GroupBean> getAllNeedCreateGroup() throws Exception;

    /**
     * 获取用户加入的群聊id
     * @param userId
     * @return
     * @throws Exception
     */
    public List<String> getJoinedGroup(String userId) throws Exception;

    /**
     * 获取所以群成员
     * @param groupId
     * @return
     * @throws Exception
     */
    public List<GroupMemberBean> getGroupMemberByGroupId(String groupId) throws Exception;

    /**
     * 根据群id找群
     * @param groupId
     * @return
     * @throws Exception
     */
    public GroupBean findGroupById(String groupId) throws Exception;

    /**
     * 查找群成员
     * @param groupId
     * @param userId
     * @return
     * @throws Exception
     */
    public GroupMemberBean findGroupMemberByUserIdAndGroupId(String groupId, String userId) throws Exception;
}
