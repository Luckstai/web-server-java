package server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

import dao.ConnectionBD;
import model.ReportsModel;

public class DynamicPage {

	Connection conn = null;
	ReportsModel model;
	
	public DynamicPage() throws SQLException {
		conn = ConnectionBD.conectar();
		model = new ReportsModel();
	}
	
	public String mostAccessedPageReport() throws SQLException, IOException {
		
		String html = "";
		File file = new File ("C:\\Users\\lucas.silva.araujo\\Documents\\www\\web-server\\src\\pages\\test.html");
        
		Scanner scnr = new Scanner(file);
        while(scnr.hasNextLine()){
            html += scnr.nextLine();
        }
		scnr.close();
        html = html.replace("{{TAG-INFO}}", "['hoje',10],['amanha',50]");
     // write the new string with the replaced line OVER the same file
        FileOutputStream fileOut = new FileOutputStream("C:\\Users\\lucas.silva.araujo\\Documents\\www\\web-server\\src\\pages\\test.html");
        fileOut.write(html.getBytes(StandardCharsets.UTF_8));
        fileOut.close();
        return html;
	}
	
	public String statusCodePageReport() throws SQLException {
		
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
		"            columns: [" + model.statusCodePageReport(conn) + "],\r\n" + 
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
