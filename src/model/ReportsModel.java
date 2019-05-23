package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReportsModel {
	
	private int id;
	private String requestedFile;
	private String methodHttp;
	private String ip;
	private int statusCode;
	
	public ReportsModel() {
		
	}
	
	public ReportsModel(int id) {
		this.id = id;
	}
	
	public ReportsModel(String file, String method, String ip, int statusCode) {
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

	public void getAll(Connection conn) throws SQLException {
		
	
		String selectSQL = "select file, count(file) as contador from log_access group by file order by contador desc limit 3;";
		PreparedStatement preparedStatement = conn.prepareStatement(selectSQL);
		ResultSet rs = preparedStatement.executeQuery(selectSQL );
		while (rs.next()) {
			String userid = rs.getString("USER_ID");
			String username = rs.getString("USERNAME");	
		}
	}
	
}
