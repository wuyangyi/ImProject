package net.im_server.database.dao;

import net.bean.db.PushHistory;
import net.bean.db.UnReadMessage;

import java.util.List;

public interface ImpUnReadMessageDAO {
    /**
     * 保存未读
     * @param unReadMessage
     * @return
     * @throws Exception
     */
    public boolean save(UnReadMessage unReadMessage) throws Exception;

    /**
     * 批量保存未读
     * @param unReadMessage
     * @return
     * @throws Exception
     */
    public boolean save(List<UnReadMessage> unReadMessage) throws Exception;

    /**
     * 移除一条记录
     * @param receiver_id
     * @param tag_id
     * @param tag_type
     * @param time
     * @return
     * @throws Exception
     */
    public boolean remove(String receiver_id, String tag_id, int tag_type, long time) throws Exception;
}
