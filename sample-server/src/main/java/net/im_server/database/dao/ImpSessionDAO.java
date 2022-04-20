package net.im_server.database.dao;

import net.bean.card.MessageCard;
import net.bean.db.Message;
import net.bean.db.SessionBean;

import java.util.List;

public interface ImpSessionDAO {
	/**
	 * 查找可发送消息的临时会话
	 * @return
	 * @throws Exception
	 */
	public List<SessionBean> findCanSendTemporarySession(String userId, String peerId) throws Exception;

	/**
	 * 更新最后一条消息id
	 * @param lastMsgId
	 * @return
	 * @throws Exception
	 */
	public boolean updateLastMsgId(String userId, String peerId, String lastMsgId) throws Exception;

	/**
	 * 更新群会话最后一条消息id
	 * @param lastMsgId
	 * @return
	 * @throws Exception
	 */
	public boolean updateGroupLastMsgId(String groupId, String lastMsgId) throws Exception;

	/**
	 * 更新系统会话的最后一条消息id
	 * @param userId
	 * @param lastMsgId
	 * @return
	 * @throws Exception
	 */
	public boolean updateSystemLastMsgId(String userId, String lastMsgId) throws Exception;
}
