package net.im_server.database.dao.impl;

import net.bean.card.MessageCard;
import net.bean.db.Message;
import net.bean.db.SensitiveWord;
import net.im_server.database.dao.ImpMessageDAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class EmpMessageDaoImpl implements ImpMessageDAO {
	private Connection conn=null;
	private PreparedStatement pstmt=null;
	public EmpMessageDaoImpl(Connection conn){
		this.conn=conn;
	}

	@Override
	public boolean save(Message message) throws Exception {
		Boolean flag = false;
		try{
			String sql="INSERT INTO ent_message(id,sender_id,receive_id,msg_from,content,attach,attach_type,type,status,is_read,create_time,update_time) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";
			this.pstmt=this.conn.prepareStatement(sql);
			this.pstmt.setString(1, message.getId());
			this.pstmt.setString(2, message.getSender_id());
			this.pstmt.setString(3, message.getReceive_id());
			this.pstmt.setInt(4, message.getMsg_from());
			this.pstmt.setString(5, message.getContent());
			this.pstmt.setString(6, message.getAttach());
			this.pstmt.setInt(7, message.getAttach_type());
			this.pstmt.setInt(8, message.getType());
			this.pstmt.setInt(9, message.getStatus());
			this.pstmt.setInt(10, message.getIs_read());
			this.pstmt.setLong(11, message.getCreate_time());
			this.pstmt.setLong(12, message.getUpdate_time());
			int n=this.pstmt.executeUpdate();
			if(n>0){
				flag=true;
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
		return flag;
	}

	@Override
	public List<MessageCard> selectUnReadMessage(String userId) throws Exception {
		List<MessageCard> all = new ArrayList<>();
		String sql = "select * from ent_message where id in (select message_id from ent_unread_message where receiver_id = '" + userId + "')";
		try{
			this.pstmt=this.conn.prepareStatement(sql);
			ResultSet rs=(ResultSet) this.pstmt.executeQuery();
			while(rs.next()){
				Message message = Message.build(rs);
				all.add(new MessageCard(message));
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

	@Override
	public MessageCard findById(String id) throws Exception {
		MessageCard messageCard = null;
		String sql = "select * from ent_message where id = '" + id + "'";
		try{
			this.pstmt=this.conn.prepareStatement(sql);
			ResultSet rs=(ResultSet) this.pstmt.executeQuery();
			if (rs.next()){
				Message message = Message.build(rs);
				messageCard = new MessageCard(message);
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
		return messageCard;
	}
}
