package server;

import dao.ConnectionBD;
import model.ReportsModel;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

public class DynamicPage {

	private Connection conn = null;
	private ReportsModel model;
	
	public DynamicPage() throws SQLException {
		conn = ConnectionBD.conectar();
		model = new ReportsModel();
	}
	
	public String mostAccessedPageReport(File file) throws SQLException, IOException {
		
		StringBuilder html = new StringBuilder();
		System.out.println(file.getAbsolutePath());
		System.out.println();

		File fileTemplate = new File(file.getAbsolutePath().replace(".html","-template.html"));

		Scanner scnr = new Scanner(fileTemplate);
        while(scnr.hasNextLine()){
            html.append(scnr.nextLine());
        }
		scnr.close();

        html = new StringBuilder(html.toString().replace("{{TAG-INFO}}", model.mostAccessedPageReport(conn)));

        //limpo o arquivo
        new FileWriter(file.getAbsoluteFile()).close();

//		//Escrevo no arquivo
//        FileOutputStream fileOut = new FileOutputStream(file);
//        fileOut.write(html.toString().getBytes(StandardCharsets.UTF_8));
//        fileOut.close();
		return html.toString();
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
