package net.impl.async;

import net.core.IoArgs;
import net.core.SendDispatcher;
import net.core.SendPacket;
import net.core.Sender;
import net.utils.CloseUtils;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;

public class AsyncSendDispatcher implements SendDispatcher, IoArgs.IoArgsEventProcessor, AsyncPacketReader.PacketProvider {
    private final Sender sender;
    private final Queue<SendPacket> queue = new ConcurrentLinkedDeque<>();
    private final AtomicBoolean isSending = new AtomicBoolean();
    private final AtomicBoolean isClosed = new AtomicBoolean(false);

    private final AsyncPacketReader reader = new AsyncPacketReader(this);

    public AsyncSendDispatcher(Sender sender) {
        this.sender = sender;
        sender.setSendListener(this);
    }

    @Override
    public boolean send(SendPacket packet) {
        queue.offer(packet);
        return requestSend();
    }

    /**
     * 发送心跳帧，将心跳帧放到帧发送队列进行发送
     */
    @Override
    public void sendHeartbeat() {
        if (queue.size() > 0) {
            return;
        }
        if (reader.requestSendHeartbeatFrame()) {
            requestSend();
        }
    }

    @Override
    public void cancel(SendPacket packet) {
        boolean ret = queue.remove(packet);
        if (ret) {
            packet.cancel();
            return;
        }

        reader.cancel(packet);
    }

    // 拿数据
    @Override
    public SendPacket takePacket() {
        SendPacket packet = queue.poll();
        if (packet == null) {
            return null;
        }

        if (packet.isCanceled()) {
            // 已取消 不用发送，取下一条
            return takePacket();
        }
        return packet;
    }

    /**
     * 完成Packet发送
     * @param isSucceed 是否成功
     */
    @Override
    public void completedPacket(SendPacket packet, boolean isSucceed) {
        CloseUtils.close(packet);
    }

    /**
     * 请求网络进行数据发送
     */
    private boolean requestSend() {
        synchronized (isSending) {
            if (isSending.get() || isClosed.get()) {
                return false;
            }

            if (reader.requestTakePacket()) {
                try {
                    boolean isSucceed = sender.postSendAsync();
                    if (isSucceed) {
                        isSending.set(true);
                        return true;
                    }
                } catch (IOException e) {
                    closeAndNotify();
                }
            }
            return false;
        }
    }

    private void closeAndNotify() {
        CloseUtils.close(this);
    }


    @Override
    public void close() {
        if (isClosed.compareAndSet(false, true)) {
            // reader关闭操作
            reader.close();
            // 清理队列
            queue.clear();
            synchronized (isSending) {
                isSending.set(false);
            }
        }
    }

    @Override
    public IoArgs provideIoArgs() {
        return isClosed.get() ? null : reader.fillData();
    }

    @Override
    public void onConsumeFailed(IoArgs args, Exception e) {
        e.printStackTrace();
        synchronized (isSending) {
            isSending.set(false);
        }
        // 继续请求发送当前的数据
        requestSend();
    }

    @Override
    public void onConsumeCompleted(IoArgs args) {
        synchronized (isSending) {
            isSending.set(false);
        }
        // 继续请求发送当前的数据
        requestSend();
    }
}
