package net.im_server.database.dao;

import net.bean.card.MessageCard;
import net.bean.db.Message;

import java.util.List;

public interface ImpMessageDAO {
	public boolean save(Message message) throws Exception;

	/**
	 * 查询某人所以未读消息
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public List<MessageCard> selectUnReadMessage(String userId) throws Exception;

	/**
	 * 根据id找消息
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public MessageCard findById(String id) throws Exception;
}
