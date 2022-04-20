package net;

import net.im_server.ImServer;
import net.provider.AuthRequestFilter;
import net.provider.GsonProvider;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.util.logging.Logger;

public class Application extends ResourceConfig {
    public Application(){
        // 注册我们的全局请求拦截器
        register(AuthRequestFilter.class);
        //注册json解析器
        register(GsonProvider.class);
        //注册日志打印输出
        register(Logger.class);

        try {
            System.out.println("start im service");
            ImServer.getInstance().run();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
