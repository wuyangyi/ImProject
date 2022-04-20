package net.core;

import java.io.Closeable;

/**
 * 发送数据调度者
 * 缓存所有需要发送的数据， 通过队列对数据进行发送
 * 并且在发送数据时，实现对数据的基本包装
 */
public interface SendDispatcher extends Closeable {

    /**
     * 发送一分数据
     * @param packet 数据
     */
    boolean send(SendPacket packet);

    /**
     * 发送一个心跳帧
     */
    void sendHeartbeat();

    /**
     * 取消发送
     * @param packet
     */
    void cancel(SendPacket packet);
}
