package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReportsModel {
	
	private String content = "";
	private String table = "log_access";
	
	public ReportsModel() {
		
	}
	
	private String getContent() {
		return this.content;
	}
	
	private void setContent(String content) {
		this.content += content;
	}
	

	public String mostAccessedPageReport(Connection conn) throws SQLException {
		
		String querySQL = "select file, count(file) as contador from " + this.table + " group by file order by contador desc limit 3;";
		PreparedStatement preparedStatement = conn.prepareStatement(querySQL);
		ResultSet rs = preparedStatement.executeQuery(querySQL );
		
		while (rs.next()) {
			setContent("['" + rs.getString("file") +"',"+ rs.getInt("contador") + "],\r\n");
		}
		
		return getContent();
	}
	
	public String statusCodePageReport(Connection conn) throws SQLException {
		
		String querySQL = "select file,\r\n" + 
				"    sum(case when status_code = 200 then 1 else 0 end) http200,\r\n" + 
				"    sum(case when status_code = 404 then 1 else 0 end) http404,\r\n" + 
				"	sum(case when status_code = 500 then 1 else 0 end) http500\r\n" + 
				"from log_access\r\n" + 
				"group by file limit 10;";
		PreparedStatement preparedStatement = conn.prepareStatement(querySQL);
		ResultSet rs = preparedStatement.executeQuery(querySQL);
		
		while (rs.next()) {
			setContent("['" + rs.getString("file") +"',"+ rs.getInt("http200") +","+ rs.getInt("http404") +","+ rs.getInt("http500") + "],\r\n");
		}
		
		return getContent();
	}
	
}
