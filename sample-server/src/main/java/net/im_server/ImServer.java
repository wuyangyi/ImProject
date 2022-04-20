package net.im_server;


import net.constants.TCPConstants;
import net.core.IoContext;
import net.foo.Foo;
import net.impl.IoSelectorProvider;
import net.impl.SchedulerImpl;

import java.io.File;
import java.io.IOException;

public class ImServer {
    private static ImServer imServer;
    private File cacheFile;
    private TCPServer tcpServer;
    private boolean isRun = false;

    private ImServer() {
    }

    public static ImServer getInstance() {
        if (imServer == null) {
            synchronized (ImServer.class) {
                if (imServer == null) {
                    imServer = new ImServer();
                }
            }
        }
        return imServer;
    }

    public void run() throws IOException {
        if (isRun) {
            close();
        }
        cacheFile = Foo.getCacheDir("server");

        //初始化上下文
        IoContext.setup()
                .ioProvider(new IoSelectorProvider())
                .scheduler(new SchedulerImpl(1))
                .start();


        tcpServer = new TCPServer(TCPConstants.PORT_SERVER, cacheFile);

        boolean isSucceed = tcpServer.start();
        if (!isSucceed) {
            System.out.println("Start TCP server failed!");
            return;
        }

        UDPProvider.start(TCPConstants.PORT_SERVER);
        isRun = true;
//        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
//        String str;
//        do {
//            str = bufferedReader.readLine();
//            if (str == null || Foo.COMMAND_EXIT.equalsIgnoreCase(str)) {
//                break;
//            }
//            if (str.length() == 0) {
//                continue;
//            }
//            //发送字符串
//            broadcast(str);
//        } while (true);


    }

    public void close() throws IOException {
        UDPProvider.stop();
        tcpServer.stop();

        IoContext.close();
        isRun = false;
    }

    public boolean isRun() {
        return isRun;
    }

    public TCPServer getTcpServer() {
        return tcpServer;
    }
}
