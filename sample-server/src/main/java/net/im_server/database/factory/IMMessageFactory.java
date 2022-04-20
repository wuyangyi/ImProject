package net.im_server.database.factory;

import net.bean.card.MessageCard;
import net.bean.db.Message;
import net.bean.db.UnReadMessage;
import net.im_server.database.dao.impl.EmpUnReadMessageDaoImpl;
import net.im_server.database.dao.proxy.EmpMessageDaoProxy;
import net.im_server.database.dao.proxy.EmpUnReadMessageDaoProxy;
import net.utils.Hib;

import java.util.List;

/**
 * 消息工厂
 */
public class IMMessageFactory {

    /**
     * 保存消息
     * @param message
     * @return
     */
    public static Message save(Message message) {
        boolean success = false;
        try {
            success = new EmpMessageDaoProxy().save(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return success ? message : null;
    }

    /**
     * 保存消息卡片
     * @param messageCard
     * @return
     */
    public static MessageCard save(MessageCard messageCard) {
        Message message = new Message(messageCard);
        message = save(message);
        if (message == null) {
            return null;
        }
        messageCard = new MessageCard(message);
        return messageCard;
    }

    /**
     * 保存未读
     * @param unReadMessage
     * @return
     */
    public static boolean saveUnReadMessage(UnReadMessage unReadMessage) {
        boolean success = false;
        try {
            success = new EmpUnReadMessageDaoProxy().save(unReadMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return success;
    }

    /**
     * 批量保存未读
     * @param unReadMessages
     * @return
     */
    public static boolean saveUnReadMessage(List<UnReadMessage> unReadMessages) {
        boolean success = false;
        try {
            success = new EmpUnReadMessageDaoProxy().save(unReadMessages);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return success;
    }

    /**
     * 移除未读消息
     * @param receiver_id 当前读取消息的用户
     * @param tag_id 发送消息方（群消息则为群id）
     * @param tag_type 发送方类型
     * @param time 已读时间（这个时间段之前的都判定为已读）
     * @return
     */
    public static boolean removeUnReadMessage(String receiver_id, String tag_id, int tag_type, long time) {
        boolean success = false;
        try {
            success = new EmpUnReadMessageDaoProxy().remove(receiver_id, tag_id, tag_type, time);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return success;
    }

    public static List<MessageCard> getAllUnReadMessage(String userId) {
        List<MessageCard> list = null;
        try {
            list = new EmpMessageDaoProxy().selectUnReadMessage(userId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 通过id找消息
     * @param id
     * @return
     */
    public static MessageCard findById(String id) {
        MessageCard messageCard = null;
        try {
            messageCard = new EmpMessageDaoProxy().findById(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return messageCard;
    }
}
