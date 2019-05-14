package server;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Paths;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import dao.ConnectionBD;
import model.LogRequest;

public class HttpRequest extends Thread {

	static final String BASE_PATH = Paths.get(".").toAbsolutePath().normalize().toString() + "\\public";
	static final String METHOD_GET = "GET";
	static final String METHOD_HEAD = "HEAD";
	static final String METHOD_POST = "POST";
	
	List<String> request = new ArrayList<>();
	private String httpMethod;
	private String requestedPath;
	final private String REGEX = "\"([/])\"";
	
	Connection conn = null;
	LogRequest log = null;
	
	String path= null;
	String ip = null;
	String extension = "error";
	HttpResponse response = null;
	Socket connectedClient = null;
	BufferedReader requestInformation = null;	
	
	public String getIp() {
		return ip;
	}
	
	public void setIp(String ip) {
		this.ip = ip;
	}
	
	public HttpRequest(Socket client) {
		response = new HttpResponse(client);
		connectedClient = client;
	}

	private void makeRequest(String lineRequest) {
		request.add(lineRequest);
		if(request.size() > 2) {
			String itemsRequest[] = request.get(0).split(" ");
			httpMethod = itemsRequest[0];
			requestedPath = itemsRequest[1];
		}
	}
	
	private String getHttpMethod() {
		return httpMethod;
	}
	
	private String getRequestedPath() {
		return requestedPath.replaceAll(REGEX, "\\");
	}
	
	private void setRequestedPath(String requestedPath) {
		this.requestedPath = requestedPath;
	}
	
	private void printRequest() {
		System.out.println("------------------------------------------------------");
		for(int i = 0; i < request.size(); i++) {
			System.out.println(request.get(i));
		}
		System.out.println("------------------------------------------------------");
	}
	
	public void run() {

		try {
			System.out.println("Client " + connectedClient.getInetAddress() + ":" + connectedClient.getPort() + " connected!");
			setIp(""+connectedClient.getInetAddress());
			requestInformation = new BufferedReader(new InputStreamReader(connectedClient.getInputStream()));
			makeRequest(requestInformation.readLine());
			while (requestInformation.ready()) {
				makeRequest(requestInformation.readLine());
			}
			
			printRequest();
			
			if(getRequestedPath().equals("/")) setRequestedPath("/index.html");
			
			int position = getRequestedPath().lastIndexOf(".");
			
			if(position != -1) extension = getRequestedPath().substring(position+1);
	
			switch (extension) {
			case "html":
				response.setContentType("text/html");
				path = BASE_PATH + getRequestedPath();
				break;

			case "css":
				response.setContentType("text/css");
				path = BASE_PATH + "\\assets\\css" + getRequestedPath();
				break;		
				
			case "js":
				response.setContentType("application/javascript");
				path = BASE_PATH + "\\assets\\js" + getRequestedPath();
				break;					
				
			case "png":
				response.setContentType("image/png");
				path = BASE_PATH + "\\assets\\image" + getRequestedPath();
				break;	

			case "jpg":
				response.setContentType("image/jpeg");
				path = BASE_PATH + "\\assets\\image" + getRequestedPath();
				break;				
				
			case "jpeg":
				response.setContentType("image/jpeg");
				path = BASE_PATH + "\\assets\\image" + getRequestedPath();
				break;					
				
			case "ico":
				response.setContentType("image/x-icon");
				path = BASE_PATH + "\\assets\\image" + getRequestedPath();
				break;					
								
			default:
				response.setContentType("text/html");
				path = BASE_PATH + "\\pages\\errors\\error-404.html";
				break;
			}
		
			System.out.println("FILE: " + path);
			FileInputStream file =  new FileInputStream(path);
			Scanner sc = new Scanner(file);
			String content = "";
			while(sc.hasNext()) content += sc.nextLine();
			sc.close();
			
			conn = ConnectionBD.conectar();
			
			log = new LogRequest(getRequestedPath(), getHttpMethod(), getIp(), 200);
			log.register(conn);
			
			switch (getHttpMethod()) {
			case METHOD_GET:						
//				if(response.getContentType().equals("image/jpeg")) {
//					System.out.println(content);
//				}
				response.send(content);
				
				break;
			case METHOD_HEAD:
				response.send("");
				
				break;
			case METHOD_POST:
				response.send(content);
				
				break;
			default:
				response.send(content);
		
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
