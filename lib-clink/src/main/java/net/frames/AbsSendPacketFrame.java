package net.frames;

import net.core.Frame;
import net.core.IoArgs;
import net.core.SendPacket;

import java.io.IOException;

public abstract class AbsSendPacketFrame extends AbsSendFrame {
    protected volatile SendPacket<?> packet;

    public AbsSendPacketFrame(int length, byte type, byte flag, short identifier, SendPacket packet) {
        super(length, type, flag, identifier);
        this.packet = packet;
    }

    /**
     * 获取当前对应的发送Packet
     * @return SendPacket
     */
    public synchronized SendPacket getPacket() {
        return packet;
    }

    @Override
    public synchronized boolean handle(IoArgs args) throws IOException {
        if (packet == null && !isSending()) {
            // 已取消，并且未发送任何数据，直接返回结束，发送下一帧
            return true;
        }
        return super.handle(args);
    }

    @Override
    public final synchronized Frame nextFrame() {
        return packet == null ? null : buildNextFrame();
    }

    // Ture: 当前没有发送任何数据
    public final synchronized boolean abort() {
        boolean isSending = isSending();
        if (isSending) {
            fillDirtyDataOnAbort();
        }

        packet = null;

        return !isSending;
    }

    protected void fillDirtyDataOnAbort() {

    }

    protected abstract Frame buildNextFrame();
}
