package net.im_server.database.dao.impl;

import net.bean.db.Message;
import net.bean.db.SensitiveWord;
import net.im_server.database.dao.ImpMessageDAO;
import net.im_server.database.dao.ImpSensitiveWordDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class EmpSensitiveWordDaoImpl implements ImpSensitiveWordDAO {
	private Connection conn=null;
	private PreparedStatement pstmt=null;
	public EmpSensitiveWordDaoImpl(Connection conn){
		this.conn=conn;
	}


	@Override
	public List<SensitiveWord> findAllWord() throws Exception {
		List<SensitiveWord> all = new ArrayList<SensitiveWord>();
		String sql = "select * from ent_sensitive_words";

		try{
			this.pstmt=this.conn.prepareStatement(sql);
			ResultSet rs=(ResultSet) this.pstmt.executeQuery();
			while(rs.next()){
				SensitiveWord sensitiveWord =new SensitiveWord();
				sensitiveWord.setId(rs.getInt(1));
				sensitiveWord.setWord(rs.getString(2));
				all.add(sensitiveWord);
			}
		}catch (Exception e) {
			throw e;
		}finally{
			if(this.pstmt!=null){
				try{
					this.pstmt.close();
				}catch (Exception e) {
					throw e;
				}
			}
		}
		return all;
	}
}
