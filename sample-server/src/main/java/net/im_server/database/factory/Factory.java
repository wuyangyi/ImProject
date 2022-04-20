package net.im_server.database.factory;

import net.bean.card.MessageCard;
import net.bean.db.GroupBean;
import net.bean.db.GroupMemberBean;
import net.bean.db.SessionBean;
import net.bean.db.UserFollowBean;
import net.utils.DataUtil;

import java.util.List;

public class Factory {

    // 检查人与人之间是否可以聊天
    public static boolean checkUserCanSendMessage(String senderId, String receiverId) {
        if (senderId == null || receiverId == null) {
            return false;
        }
        // 1、判断是否是好友
        boolean isFriend = checkIsFriend(senderId, receiverId);
        if (isFriend) {
            return true;
        }
        // 2、判断是否是临时会话
        return checkIsTemporarySession(senderId, receiverId);
    }

    /**
     * 检查是否是好友关系
     * @param senderId
     * @param receiverId
     * @return
     */
    public static boolean checkIsFriend(String senderId, String receiverId) {
        if (senderId == null || receiverId == null) {
            return false;
        }
        UserFollowBean follow = IMUserFollowFactory.getUserFollow(senderId, receiverId, UserFollowBean.TYPE_ATT);
        UserFollowBean fan = IMUserFollowFactory.getUserFollow(receiverId, senderId, UserFollowBean.TYPE_ATT);
        if (follow != null && fan != null) {
            return true;
        }
        return false;
    }

    /**
     * 检查是否有临时会话
     * @param senderId
     * @param receiverId
     * @return
     */
    public static boolean checkIsTemporarySession(String senderId, String receiverId) {
        if (senderId == null || receiverId == null) {
            return false;
        }
        List<SessionBean> sendList = IMSessionFactory.findCanSendTemporarySession(DataUtil.getMaxUserId(senderId, receiverId), DataUtil.getMinUserId(senderId, receiverId));
        if (sendList != null && !sendList.isEmpty()) {
            return true;
        }
        return false;
    }


    /**
     * 检查是否可以发送消息到群
     * @param senderId
     * @param groupId
     * @return 0正常发送、1群已解散、2未加入群、3已禁言
     */
    public static int checkCanSendMessageToGroup(String senderId, String groupId) {
        // 1、群是否正常
        GroupBean groupBean = IMGroupFactory.findGroupById(groupId);
        if (groupBean == null || groupBean.getStatus() != 0) {
            return 1;
        }
        // 2、当前是否是群成员
        GroupMemberBean groupMemberBean = IMGroupFactory.findGroupMemberByUserIdAndGroupId(groupId, senderId);
        if (groupMemberBean == null || groupMemberBean.getStatus() != 0) {
            return 2;
        }
        // 3、当前成员是否可以发言
        if (groupMemberBean != null && groupMemberBean.getCan_send() != 0) {
            return 3;
        }
        return 0;
    }
}
