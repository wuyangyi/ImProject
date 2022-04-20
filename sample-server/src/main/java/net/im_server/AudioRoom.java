package net.im_server;



import net.handle.ConnectorHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 房间基本封装
 */
public class AudioRoom {
    private final String roomCode;
    private volatile ConnectorHandler handler1;
    private volatile ConnectorHandler handler2;
    private String receiverId; //接听者id（打给谁）

    public AudioRoom() {
        this.roomCode = getRandomString(5);
    }

    public String getRoomCode() {
        return roomCode;
    }

    public ConnectorHandler[] getConnectors() {
        List<ConnectorHandler> handlers = new ArrayList<>(2);
        if (handler1 != null) {
            handlers.add(handler1);
        }
        if (handler2 != null) {
            handlers.add(handler2);
        }

        return handlers.toArray(new ConnectorHandler[0]);
    }

    /**
     * 获取对方
     */
    public ConnectorHandler getTheOtherHandler(ConnectorHandler handler) {
        return (handler1 == handler || handler1 == null) ? handler2 : handler1;
    }

    /**
     * 房间是否可以聊天，是否两个客户端都具有
     */
    public synchronized boolean isEnable() {
        return handler1 != null && handler2 != null;
    }

    /**
     * 加入房间
     * @param handler
     * @return 是否加入成功
     */
    public synchronized boolean enterRoom(ConnectorHandler handler) {
        if (handler1 == null) {
            handler1 = handler;
        } else if (handler2 == null) {
            handler2 = handler;
        } else {
            return false;
        }
        return true;
    }

    /**
     * 退出房间
     * @param handler
     * @return 退出后如果还有一个人剩余则返回剩余的人
     */
    public synchronized ConnectorHandler exitRoom(ConnectorHandler handler) {
        if (handler1 == handler) {
            handler1 = null;
        } else if (handler2 == handler) {
            handler2 = null;
        }
        return handler1 == null ? handler2 : handler1;
    }

    /**
     * 生成一个简单的随机字符串
     * @param length 长度
     * @return
     */
    private static String getRandomString(final int length) {
        final String str = "123456789abcdefghijkmlnopqrstuvwxyz";
        final Random random = new Random();
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; ++i) {
            int number = random.nextInt(str.length());
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }
}
