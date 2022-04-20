package net.im_server.database.dao;

import net.bean.db.SensitiveWord;

import java.util.List;

public interface ImpSensitiveWordDAO {
    public List<SensitiveWord> findAllWord() throws Exception;
}
