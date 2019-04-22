package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogRequest {
	
	private int id;
	private String requestedFile;
	private String methodHttp;
	private String ip;
	private int statusCode;
	private Date dateTime;
	
	public LogRequest() {
		
	}
	
	public LogRequest(int id) {
		this.id = id;
	}
	
	public LogRequest(String file, String method, String ip, int statusCode) {
		this.requestedFile = file;
		this.methodHttp = method;
		this.ip = ip;
		this.statusCode = statusCode;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getRequestedFile() {
		return requestedFile;
	}
	public void setRequestedFile(String requestedFile) {
		this.requestedFile = requestedFile;
	}
	public String getMethodHttp() {
		return methodHttp;
	}
	public void setMethodHttp(String methodHttp) {
		this.methodHttp = methodHttp;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	public Date getDateTime() {
		return dateTime;
	}
	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}

	public void register(Connection conn) {
		
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");  
		System.out.println(formatter.format(date));  
		
		String sqlInsert = "INSERT INTO log_access (file, http_method, ip, status_code, date_time) VALUES (?, ?, ?, ?, ?)";
		try(PreparedStatement stm = conn.prepareStatement(sqlInsert);){
			stm.setString(1, getRequestedFile());
			stm.setString(2, getMethodHttp());
			stm.setString(3, getIp());
			stm.setInt(4, getStatusCode());
			stm.setString(5, formatter.format(date));
			stm.execute();
		}
		catch(Exception e) {
			e.printStackTrace();
			try {
				conn.rollback();
			}
			catch (SQLException e1) {
				System.out.println(e1.getStackTrace());
			}
		}
	}
	
}
