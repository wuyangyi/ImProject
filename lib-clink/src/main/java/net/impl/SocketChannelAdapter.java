package net.impl;


import net.core.IoArgs;
import net.core.IoProvider;
import net.core.Receiver;
import net.core.Sender;
import net.utils.CloseUtils;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicBoolean;

public class SocketChannelAdapter implements Sender, Receiver, Cloneable {
    private final AtomicBoolean isClosed = new AtomicBoolean(false);
    private final SocketChannel channel;
    private final IoProvider ioProvider;
    private final OnChannelStatusChangedListener listener;

    private IoArgs.IoArgsEventProcessor receiveIoEventProcessor;
    private IoArgs.IoArgsEventProcessor sendIoEventProcessor;

    private volatile long lastReadTime = System.currentTimeMillis();
    private volatile long lastWriteTime = System.currentTimeMillis();

    public SocketChannelAdapter(SocketChannel channel, IoProvider ioProvider,
                                OnChannelStatusChangedListener listener) throws IOException {
        this.channel = channel;
        this.ioProvider = ioProvider;
        this.listener = listener;

        channel.configureBlocking(false);
    }

    @Override
    public void setReceiveListener(IoArgs.IoArgsEventProcessor processor) {
        receiveIoEventProcessor = processor;
    }

    @Override
    public boolean postReceiveAsync() throws IOException {
        if (isClosed.get()) {
            throw new IOException("Current channel is closed!");
        }

        // 进行Callback状态检测，判断是否处于自循环
        inputCallback.checkAttachNull();
        return ioProvider.registerInput(channel, inputCallback);
    }

    /**
     * 获取最后读的时间点
     * @return
     */
    @Override
    public long getLastReadTime() {
        return lastReadTime;
    }

    @Override
    public void setSendListener(IoArgs.IoArgsEventProcessor processor) {
        sendIoEventProcessor = processor;
    }

    @Override
    public boolean postSendAsync() throws IOException {
        if (isClosed.get()) {
            throw new IOException("Current channel is closed!");
        }
        // 进行Callback状态检测，判断是否处于自循环
        outputCallback.checkAttachNull();
        // 当前发送的数据附加到回调中
        return ioProvider.registerOutput(channel, outputCallback);
    }

    /**
     * 获取当前最后写的时间点
     * @return
     */
    @Override
    public long getLastWriteTime() {
        return lastWriteTime;
    }

    @Override
    public void close() throws IOException {
        if (isClosed.compareAndSet(false, true)) {
            // 解除注册回调
            ioProvider.unRegisterInput(channel);
            ioProvider.unRegisterOutput(channel);
            // 关闭
            CloseUtils.close(channel);
            // 回调当前Channel已关闭
            listener.onChannelClosed(channel);
        }
    }

    private final IoProvider.HandleProviderCallback inputCallback = new IoProvider.HandleProviderCallback() {
        @Override
        protected void canProviderOutput(IoArgs args) {
            if (isClosed.get()) {
                return;
            }

            // 刷新读取时间
            lastReadTime = System.currentTimeMillis();

            final IoArgs.IoArgsEventProcessor processor = receiveIoEventProcessor;
            if (processor == null) {
                return;
            }

            if (args == null) {
                args = processor.provideIoArgs();
            }

            try {
                if (args == null) {
                    processor.onConsumeFailed(null, new IOException("ProvideIoArgs is null."));
                } else {
                    int count = args.readFrom(channel);
                    if (count == 0) {
                        System.out.println("Current read zero data!");
                    }

                    // 检测是否还有空闲区间，以及是否还需要填满空闲区间
                    if (args.remained() && args.isNeedConsumeRemaining()) {
                        // 附加当前未消费完成的args
                        attach = args;
                        // 再次注册主数据发送
                        ioProvider.registerInput(channel, this);
                    } else {
                        // 设置未null
                        attach = null;
                        // 读取数据完成的回调
                        processor.onConsumeCompleted(args);
                    }
                }
            } catch (IOException ignored) {
                CloseUtils.close(SocketChannelAdapter.this);
            }
        }
    };


    private final IoProvider.HandleProviderCallback outputCallback = new IoProvider.HandleProviderCallback() {
        @Override
        protected void canProviderOutput(IoArgs args) {
            if (isClosed.get()) {
                return;
            }

            lastWriteTime = System.currentTimeMillis();

            final IoArgs.IoArgsEventProcessor processor = sendIoEventProcessor;
            if (processor == null) {
                return;
            }

            if (args == null) {
                // 拿到一份新的IoArgs
                args = processor.provideIoArgs();
            }

            try {
                if (args == null) {
                    processor.onConsumeFailed(null, new IOException("ProvideIoArgs is null."));
                } else {
                    int count = args.writeTo(channel);
                    if (count == 0) {
                        System.out.println("Current write zero data!");
                    }
                    // 检查是否还有未消费数据，以及是否需要一次消费完全
                    if (args.remained() && args.isNeedConsumeRemaining()) {
                        // 附加当前未消费完成的args
                        attach = args;
                        // 再次注册主数据发送
                        ioProvider.registerOutput(channel, this);
                    } else {
                        // 设置未null
                        attach = null;
                        // 输出完成的回调
                        processor.onConsumeCompleted(args);
                    }
                }
            } catch (IOException ignored) {
                CloseUtils.close(SocketChannelAdapter.this);
            }

        }
    };

    public interface OnChannelStatusChangedListener {
        void onChannelClosed(SocketChannel channel);
    }
}
