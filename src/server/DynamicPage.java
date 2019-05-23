package server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import dao.ConnectionBD;

public class DynamicPage {

	Connection conn = null;
	
	public DynamicPage() throws SQLException {
		conn = ConnectionBD.conectar();
	}
	
	public String mostAccessedPageReport() throws SQLException {
		
		String selectSQL = "select file, count(file) as contador from log_access group by file order by contador desc limit 3;";
		PreparedStatement preparedStatement = conn.prepareStatement(selectSQL);
		ResultSet rs = preparedStatement.executeQuery(selectSQL );
		String content = "";
		
		while (rs.next()) {
			content += "['" + rs.getString("file") +"',"+ rs.getInt("contador") + "],\r\n";
		}
		

		return "<!DOCTYPE html>\r\n" + 
		"<html lang=\"pt-br\">\r\n" + 
		"  <head>\r\n" + 
		"    <title>index</title>\r\n" + 
		"    <meta charset=\"utf-8\">\r\n" + 
		"	<link rel=\"stylesheet\" href=\"style.css\" />\r\n" + 
		"  <!-- Load c3.css -->\r\n" + 
		"  <link href=\"lib/c3-charts/c3.css\" rel=\"stylesheet\">\r\n" + 
		"  </head>\r\n" + 
		"  <body>\r\n" + 
		"    <h1>REPORTS</h1>\r\n" + 
		"    \r\n" + 
		"    <div id=\"chart\"></div>\r\n" + 
		"\r\n" + 
		"    <!-- Scripts -->\r\n" + 
		"			<script src=\"lib/jquery/jquery-3.4.0.min.js\"></script>\r\n" + 
		"      <script src=\"lib/d3-charts/d3.min.js\" charset=\"utf-8\"></script>\r\n" + 
		"      <script src=\"lib/c3-charts/c3.min.js\"></script>\r\n" + 
		"			<script src=\"home.js\"></script>\r\n" + 
		"      <script type=\"text/javascript\">\r\n" + 
		"        var chart = c3.generate({\r\n" + 
		"          data: {\r\n" + 
		"            columns: [" + content + "],\r\n" + 
        "            type : 'pie',\r\n" + 
        "            onclick: function (d, i) { console.log(\"onclick\", d, i); },\r\n" + 
        "            onmouseover: function (d, i) { console.log(\"onmouseover\", d, i); },\r\n" + 
        "            onmouseout: function (d, i) { console.log(\"onmouseout\", d, i); }\r\n" + 
		"          }\r\n" + 
		"      });\r\n" +  
		"      </script>\r\n" + 
		"  </body>\r\n" + 
		"</html>";
	}
	
	public String statusCodePageReport() throws SQLException {
		
		String selectSQL = "select file,\r\n" + 
				"    sum(case when status_code = 200 then 1 else 0 end) http200,\r\n" + 
				"    sum(case when status_code = 404 then 1 else 0 end) http404,\r\n" + 
				"	sum(case when status_code = 500 then 1 else 0 end) http500\r\n" + 
				"from log_access\r\n" + 
				"group by file limit 10;";
		PreparedStatement preparedStatement = conn.prepareStatement(selectSQL);
		ResultSet rs = preparedStatement.executeQuery(selectSQL );
		String content = "";
		
		while (rs.next()) {
			content += "['" + rs.getString("file") +"',"+ rs.getInt("http200") +","+ rs.getInt("http404") +","+ rs.getInt("http500") + "],\r\n";
		}
		
		System.out.println("TEEEEEEEEEEESTE " + content);
		
		return "<!DOCTYPE html>\r\n" + 
		"<html lang=\"pt-br\">\r\n" + 
		"  <head>\r\n" + 
		"    <title>index</title>\r\n" + 
		"    <meta charset=\"utf-8\">\r\n" + 
		"	<link rel=\"stylesheet\" href=\"style.css\" />\r\n" + 
		"  <!-- Load c3.css -->\r\n" + 
		"  <link href=\"lib/c3-charts/c3.css\" rel=\"stylesheet\">\r\n" + 
		"  </head>\r\n" + 
		"  <body>\r\n" + 
		"    <h1>REPORTS</h1>\r\n" + 
		"    \r\n" + 
		"    <div id=\"chart\"></div>\r\n" + 
		"\r\n" + 
		"    <!-- Scripts -->\r\n" + 
		"			<script src=\"lib/jquery/jquery-3.4.0.min.js\"></script>\r\n" + 
		"      <script src=\"lib/d3-charts/d3.min.js\" charset=\"utf-8\"></script>\r\n" + 
		"      <script src=\"lib/c3-charts/c3.min.js\"></script>\r\n" + 
		"			<script src=\"home.js\"></script>\r\n" + 
		"      <script type=\"text/javascript\">\r\n" + 
		"        var chart = c3.generate({\r\n" + 
		"          data: {\r\n" + 
		"            columns: [" + content + "],\r\n" + 
        "            type : 'bar',\r\n" + 
		"		   },\r\n" +
		"		   bar: {\r\n" +
        "				width: {\r\n"+
        "    				ratio: 0.5\r\n" +
        "				}\r\n" +
		"          }\r\n" + 
		"      });\r\n" +  
		"      </script>\r\n" + 
		"  </body>\r\n" + 
		"</html>";
	}
}
