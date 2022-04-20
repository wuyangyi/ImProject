package net.im_server.database.dao.impl;

import net.bean.db.PushHistory;
import net.bean.db.UnReadMessage;
import net.im_server.database.dao.ImpUnReadMessageDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

public class EmpUnReadMessageDaoImpl implements ImpUnReadMessageDAO {
    private Connection conn=null;
    private PreparedStatement pstmt=null;
    public EmpUnReadMessageDaoImpl(Connection conn){
        this.conn=conn;
    }

    @Override
    public boolean save(UnReadMessage unReadMessage) throws Exception {
        boolean flag = false;
        try{
            String sql="INSERT INTO ent_unread_message(message_id,receiver_id,tag_id,tag_type,create_time,update_time) VALUES(?,?,?,?,?,?)";
            this.pstmt=this.conn.prepareStatement(sql);
            this.pstmt.setString(1, unReadMessage.getMessage_id());
            this.pstmt.setString(2, unReadMessage.getReceiver_id());
            this.pstmt.setString(3, unReadMessage.getTag_id());
            this.pstmt.setInt(4, unReadMessage.getTag_type());
            this.pstmt.setLong(5, unReadMessage.getCreate_time());
            this.pstmt.setLong(6, unReadMessage.getUpdate_time());
            int n=this.pstmt.executeUpdate();
            if (n > 0) {
                flag = true;
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
    public boolean save(List<UnReadMessage> unReadMessages) throws Exception {
        boolean flag = false;
        if (unReadMessages == null || unReadMessages.isEmpty()) {
            return false;
        }
        try{
            String sql="INSERT INTO ent_unread_message(message_id,receiver_id,tag_id,tag_type,create_time,update_time) VALUES(?,?,?,?,?,?)";
            this.pstmt=this.conn.prepareStatement(sql);
            for (UnReadMessage unReadMessage : unReadMessages) {
                this.pstmt.setString(1, unReadMessage.getMessage_id());
                this.pstmt.setString(2, unReadMessage.getReceiver_id());
                this.pstmt.setString(3, unReadMessage.getTag_id());
                this.pstmt.setInt(4, unReadMessage.getTag_type());
                this.pstmt.setLong(5, unReadMessage.getCreate_time());
                this.pstmt.setLong(6, unReadMessage.getUpdate_time());
                this.pstmt.addBatch();
            }
            int[] rows=this.pstmt.executeBatch();
            if (rows.length > 0) {
                flag = true;
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
    public boolean remove(String receiver_id, String tag_id, int tag_type, long time) throws Exception {
        boolean flag = false;
        try {
            String sql="DELETE FROM ent_unread_message WHERE receiver_id = '"+receiver_id+"' and tag_id = '" + tag_id + "' and tag_type = " + tag_type + " and create_time <= " + time;
            this.pstmt=this.conn.prepareStatement(sql);
            int n=this.pstmt.executeUpdate();
            if(n>0){
                flag=true;
            }
        } catch (Exception e) {
            throw e;
        } finally {
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
}
