package net.core;

import java.io.Closeable;
import java.nio.channels.SocketChannel;

public interface IoProvider extends Closeable {
    boolean registerInput(SocketChannel channel, HandleProviderCallback callback);

    boolean registerOutput(SocketChannel channel, HandleProviderCallback callback);

    void unRegisterInput(SocketChannel channel);

    void unRegisterOutput(SocketChannel channel);


    abstract class HandleProviderCallback implements Runnable {
        protected volatile IoArgs attach;

        @Override
        public final void run() {
            canProviderOutput(attach);
        }


        protected abstract void canProviderOutput(IoArgs args);

        public void checkAttachNull() {
            if (attach != null) {
                throw new IllegalStateException("Current attach is not empty!");
            }
        }
    }

}
