package net.im_server.database.dao;

import net.bean.db.UserFollowBean;

public interface ImpFollowDAO {
    public UserFollowBean findFollow(String originId, String targetId, int isAtt) throws Exception;
}
