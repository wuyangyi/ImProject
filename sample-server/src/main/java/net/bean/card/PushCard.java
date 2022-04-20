package net.bean.card;

import net.utils.AesServerUtil;
import net.utils.TextUtil;

/**
 * author: wuyangyi
 * time: 2021/2/4
 * desc:
 */
public class PushCard {
    // 退出登录
    public static final int PUSH_TYPE_OUT_LOGOUT = -1;
    // 默认通知 （这个全都能收到）
    public static final int PUSH_TYPE_NONE = 0;
    // 收到消息
    public static final int PUSH_TYPE_MESSAGE = 200;
    // 历史未读消息
    public static final int PUSH_TYPE_UNREAD_MESSAGE = 201;
    // 申请
    public static final int PUSH_TYPE_APPLY = 500;
    // 关注人
    public static final int PUSH_TYPE_FOLLOW_USER = 1000;
    // 取消关注某人
    public static final int PUSH_TYPE_CANCEL_FOLLOW_USER = 1001;

    private String content;
    private int type;
    private String attach;
    private long createTime;

    /**
     * 构建加密后的PushCard json字符串
     * @param messageCard
     * @return
     */
    public static String buildMessage(MessageCard messageCard) {
        if (messageCard == null) {
            return "";
        }
        String messageString = TextUtil.toJson(messageCard);
        PushCard pushCard = new PushCard(AesServerUtil.encrypt(messageString), PushCard.PUSH_TYPE_MESSAGE);
        String pushString = TextUtil.toJson(pushCard);
        return AesServerUtil.encrypt(pushString);
    }

    public PushCard(String content, int type) {
        this.content = content;
        this.type = type;
        this.createTime = System.currentTimeMillis();
    }

    public PushCard(String content, int type, String attach) {
        this(content, type);
        this.attach = attach;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }
}
