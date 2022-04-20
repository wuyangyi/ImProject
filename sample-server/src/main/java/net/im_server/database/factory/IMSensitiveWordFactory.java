package net.im_server.database.factory;


import net.bean.db.SensitiveWord;
import net.im_server.database.dao.proxy.EmpSensitiveWordDaoProxy;
import net.utils.Hib;

import java.util.ArrayList;
import java.util.List;

/**
 * 敏感词工厂
 */
public class IMSensitiveWordFactory {

    /**
     * 获取所有铭感词
     * @return
     */
    public static List<SensitiveWord> findAllWord() {
        List<SensitiveWord> sensitiveWords = null;
        try {
            sensitiveWords = new EmpSensitiveWordDaoProxy().findAllWord();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (sensitiveWords == null) {
            sensitiveWords = new ArrayList<>();
        }
        return sensitiveWords;
    }
}
