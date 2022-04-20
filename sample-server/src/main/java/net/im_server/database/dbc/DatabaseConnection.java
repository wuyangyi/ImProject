package net.im_server.database.dbc;
import java.sql.*;
public class DatabaseConnection {
	public static final String DBDRIVE="com.mysql.jdbc.Driver";
//	public static final String DBURL="jdbc:mysql://localhost:3306/hubang?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8&useSSL=false";
	public static final String DBURL="jdbc:mysql://127.0.0.1:3306/hubang?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8&useSSL=false";
	public static final String DBUSER="admin";
	public static final String DBPASSWORD="199755";
	private Connection conn=null;
	public DatabaseConnection() throws Exception{
		Class.forName(DBDRIVE);
		this.conn=DriverManager.getConnection(DBURL,DBUSER,DBPASSWORD);
	}
	public Connection getConnection(){
		return this.conn;
	}
	public void close() throws Exception{
		if(this.conn!=null){
			try{
				conn.close();
			}catch(SQLException e){
				e.getStackTrace();
			}
		}
	}
}
