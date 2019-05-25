package server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Paths;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import dao.ConnectionBD;
import model.LogRequest;

public class HttpRequest extends Thread {

	static final String BASE_PATH = Paths.get(".").toAbsolutePath().normalize().toString() + "\\public";
	//static final String BASE_PATH = RAIZ
	//BASE_PATH = PUBLIC PAGES
	//BASE PATH = PRIVATE PAGES
	static final String METHOD_GET = "GET";
	static final String METHOD_HEAD = "HEAD";
	
	List<String> request = new ArrayList<>();
	
	List<StaticPage> staticPages = new ArrayList<>();
	
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
	
	private void defineRender(String extension) {
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
			path = BASE_PATH + "\\pages\\errors\\error-400.html";
			break;
		}
	}
	
	public void run() {
		List<String> pagesToRender = new ArrayList<>();
		String content = "";
		int statusCode = 500;
		byte[] fileInBytes = new byte[0];
		int numOfBytes = 0;
		
		pagesToRender.add("/reports.html");
//		pagesToRender.add("/reports2.html");
		
		
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
			
			int positionPoint = getRequestedPath().lastIndexOf(".");
			
			if(positionPoint != -1) extension = getRequestedPath().substring(positionPoint+1);

			
			switch (getHttpMethod()) {
			case METHOD_GET:						

					this.defineRender(extension);
					DynamicPage dynamicPages = new DynamicPage();
					
					if(this.getRequestedPath().equals("/reports.html")) {
						content = dynamicPages.mostAccessedPageReport();
					}

					if(this.getRequestedPath().equals("/reports2.html")) {
						content = dynamicPages.statusCodePageReport();
					}

					File file;
					if(this.getRequestedPath().equals("/reports.html")) {
						path = "C:\\Users\\lucas.silva.araujo\\Documents\\www\\web-server\\src\\pages\\test.html";
						file = new File ("C:\\Users\\lucas.silva.araujo\\Documents\\www\\web-server\\src\\pages\\test.html");
					} else {
						file = new File(path);
					}
					
					if(file.exists()) {
						numOfBytes = (int) file.length();
						FileInputStream inFile =  new FileInputStream(path);
						fileInBytes = new byte[numOfBytes];
						inFile.read(fileInBytes);
					} 
					else {
						
						for (String page : pagesToRender) {
							if(getRequestedPath().equals(page)) {
								System.out.println(getRequestedPath());
								numOfBytes = (int) content.length();
								fileInBytes = content.getBytes();
							} else {
								this.defineRender("error");
								numOfBytes = (int) file.length();
								FileInputStream inFile =  new FileInputStream(path);
								fileInBytes = new byte[numOfBytes];
								inFile.read(fileInBytes);
							}
						}
					}				
				
				break;
			case METHOD_HEAD:
				
				break;
			default:
		
				break;
			}
			
			conn = ConnectionBD.conectar();
			
			log = new LogRequest(getRequestedPath(), getHttpMethod(), getIp(), 200);
			log.register(conn);
			response.send(fileInBytes, numOfBytes);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
