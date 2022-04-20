package net.foo;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class Foo {
    // 绑定Stream到一个命令链接（带参数）
    public static final String COMMAND_CONNECTOR_BIND = "--m c bind ";
    // 创建对话房间（附带参数，传入用户id或群id）
    public static final String COMMAND_AUDIO_CREATE_ROOM = "--m a create ";
    // 加入对话房间（带参数）
    public static final String COMMAND_AUDIO_JOIN_ROOM = "--m a join ";
    // 主动离开对话房间(带参数 房间号)
    public static final String COMMAND_AUDIO_LEAVE_ROOM = "--m a leave ";

    // 回送服务器上的唯一标志（带参数）
    public static final String COMMAND_INFO_NAME = "--i server ";
    // 发送语音通话请求（带参数，房间号 + 用户id）
    public static final String COMMAND_INFO_AUDIO_REQUEST = "--i a request ";
    // 回送语言群名（带参数）
    public static final String COMMAND_INFO_AUDIO_ROOM = "--i a room ";
    // 回送语言开始（带参数）
    public static final String COMMAND_INFO_AUDIO_START = "--i a start ";
    // 回送语音结束(带参数 房间号)
    public static final String COMMAND_INFO_AUDIO_STOP = "--i a stop";
    // 回送语音结束(带参数 房间号)
    public static final String COMMAND_INFO_AUDIO_REFUSE = "--i a refuse ";
    // 回送语音操作错误
    public static final String COMMAND_INFO_AUDIO_ERROR = "--i a error";

    // 退出部分
    public static final String COMMAND_EXIT = "00bye00";
    // 登录(传入userId)
    public static final String COMMAND_LOGIN = "--m u login ";
    // 退出登录
    public static final String COMMAND_LOGOUT = "--m u logout";
    // 群操作部分
    public static final String COMMAND_GROUP_JOIN = "--m g join";
    public static final String COMMAND_GROUP_LEAVE = "--m g leave";
    public static final String DEFAULT_GROUP_NAME = "im-hubang";

    // 消息已读操作(带参数：传入接收者id，发送者id（群消息为群id），发送者类型)
    public static final String COMMAND_MESSAGE_READ = "--m m read ";


    private static final String CACHE_DIR = "cache";

    public static File getCacheDir(String dir) {
        String path = System.getProperty("user.dir") + (File.separator + CACHE_DIR + File.separator + dir);
        File file = new File(path);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                path = System.getProperty("user.dir") + (CACHE_DIR + File.separator + dir);
                file = new File(path);
                if (!file.exists()) {
                    if (!file.mkdirs()) {
                        throw new RuntimeException("Create path error:" + path);
                    }
                }
            }
        }
        return file;
    }

    public static File createRandomTemp(File parent) {
        String string = UUID.randomUUID().toString() + ".tmp";
        File file = new File(parent, string);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
}