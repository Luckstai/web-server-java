package server;

import dao.ConnectionBD;
import model.LogRequest;

import java.io.*;
import java.net.Socket;
import java.nio.file.Paths;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class HttpRequest extends Thread {

	private static final String BASE_PATH = Paths.get(".").toAbsolutePath().normalize().toString();
	private static final String BASE_PATH_PRIVATE_PAGES = BASE_PATH + "\\src\\pages";
	private static final String BASE_PATH_PUBLIC_PAGES = BASE_PATH + "\\public\\pages";
	private static final String BASE_PATH_PRIVATE = BASE_PATH + "\\src";
	private static final String BASE_PATH_PUBLIC = BASE_PATH + "\\public";
	private static final String METHOD_GET = "GET";
	private static final String METHOD_HEAD = "HEAD";
	private List<String> request = new ArrayList<>();
	private String httpMethod;
	private String requestedPath;
	private String requestedFile;
	private String requestedFileExtension = "html";
	private boolean accessPrivate = false;
	private String FILE_DEFAULT = "index.html";
	private int statusCode = 500;

	Connection conn = null;
	LogRequest log = null;
	
	String path= null;
	String ip = null;
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
		return requestedPath;
	}
	
	private void setRequestedPath(String requestedPath) {
		String REGEX = "\"([/])\"";
		this.requestedPath = requestedPath.replaceAll(REGEX, "\\");
	}

	private String getRequestedFile(){
		return this.requestedFile;
	}

	private void setRequestedFile(String requestedFile){
		this.requestedFile = requestedFile;
	}

	private String getRequestedFileExtension(){
		return this. requestedFileExtension;
	}

	private void setRequestedFileExtension(String extension){
		this.requestedFileExtension = extension;
	}

	private void setStatusCode(int statusCode){
		this.statusCode = statusCode;
	}

	private int getStatusCode(){
		return this.statusCode;
	}

	private void printRequest() {
		System.out.println("---------------------REQUEST---------------------------------");
		for (String s : request) {
			System.out.println(s);
		}
		System.out.println("------------------------------------------------------");
	}
	
	private void defineRender(String extension) {
		switch (extension) {
		case "html":
			response.setContentType("text/html");
			path = BASE_PATH_PUBLIC_PAGES + getRequestedPath();
			break;

		case "css":
			response.setContentType("text/css");
			path = BASE_PATH_PUBLIC_PAGES + "\\assets\\css" + getRequestedPath();
			break;		
			
		case "js":
			response.setContentType("application/javascript");
			path = BASE_PATH_PUBLIC_PAGES + "\\assets\\js" + getRequestedPath();
			break;					
			
		case "png":
			response.setContentType("image/png");
			path = BASE_PATH_PUBLIC_PAGES + "\\assets\\image" + getRequestedPath();
			break;	

		case "jpg":
			response.setContentType("image/jpeg");
			path = BASE_PATH_PUBLIC_PAGES + "\\assets\\image" + getRequestedPath();
			break;				
			
		case "jpeg":
			response.setContentType("image/jpeg");
			path = BASE_PATH_PUBLIC_PAGES + "\\assets\\image" + getRequestedPath();
			break;					
			
		case "ico":
			response.setContentType("image/x-icon");
			path = BASE_PATH_PUBLIC_PAGES + "\\assets\\image" + getRequestedPath();
			break;					
							
		default:
			response.setContentType("text/html");
			path = BASE_PATH_PUBLIC_PAGES + "\\pages\\errors\\error-404.html";
			break;
		}
	}
	
	public void run() {
		byte[] fileInBytes = new byte[0];
		int numOfBytes = 0;

		try {
			System.out.println("Client " + connectedClient.getInetAddress() + ":" + connectedClient.getPort() + " connected!");
			setIp(""+connectedClient.getInetAddress());
			requestInformation = new BufferedReader(new InputStreamReader(connectedClient.getInputStream()));
			makeRequest(requestInformation.readLine());
			while (requestInformation.ready()) {
				makeRequest(requestInformation.readLine());
			}
			
			printRequest();

			if(getRequestedPath().equals("/")){
				setRequestedPath(FILE_DEFAULT);
			}

			int positionPoint = getRequestedPath().lastIndexOf(".");
			int positionBarra = getRequestedPath().lastIndexOf("/");

			if(positionPoint != -1) {
				setRequestedFileExtension(getRequestedPath().substring(positionPoint+1));
				if(getRequestedFileExtension().equals("html")){
					setRequestedFile(getRequestedPath().substring(positionBarra+1,positionPoint));
				}
			} else{
				setRequestedFile(getRequestedPath().substring(positionBarra+1));
			}

			System.out.println(" ");
			System.out.println("URL: " + getRequestedPath());
			System.out.println("EXTENSION: " + getRequestedFileExtension());
			System.out.println("FILE OR DIRECTORY: " + getRequestedFile());

			File privateFile;
			privateFile = new File(BASE_PATH_PRIVATE_PAGES + getRequestedPath());

			File publicFile = new File(BASE_PATH_PUBLIC_PAGES + getRequestedPath());
			if(getRequestedPath().equals(FILE_DEFAULT)){
				publicFile = new File(BASE_PATH_PUBLIC + "\\" +getRequestedPath());
			}

			if(privateFile.exists()){
				if(privateFile.isDirectory()){
					System.out.println("ERRO 403 - ACESSO NEGADO (PRIVADO)");
					setStatusCode(403);
				}
			} else {
				setRequestedPath(getRequestedPath()+".html");
				privateFile = new File(BASE_PATH_PRIVATE_PAGES + getRequestedPath());

				if(privateFile.isFile()){
					System.out.println("ARQUIVO PRIVADO: " + privateFile.getName() + " ENCONTRADO");
					setStatusCode(200);

					this.defineRender(getRequestedFileExtension());
					DynamicPage dynamicPages = new DynamicPage();
					String teste = dynamicPages.mostAccessedPageReport(privateFile);

					numOfBytes = teste.length();
					fileInBytes = teste.getBytes();
				}
			}
			System.out.println("Existe: " + BASE_PATH_PUBLIC_PAGES + getRequestedPath());
			if(publicFile.exists()){
				if(publicFile.isDirectory()){
					System.out.println("ERRO 403 - ACESSO NEGADO (PUBLICO)");
					setStatusCode(403);
				} else {
					System.out.println("ERRO 404 - PÁGINA PUBLICA NÃO ENCONTRADA");
					setStatusCode(404);
				}

				if(publicFile.isFile()){
					System.out.println("ARQUIVO PUBLICO: " + publicFile.getName() + " ENCONTRADO");
					setStatusCode(200);
				} else {
					System.out.println("ERRO 404 - PÁGINA PUBLICA NÃO ENCONTRADA");
					setStatusCode(404);
				}

			} else {
				setRequestedPath(getRequestedPath()+".html");
				publicFile = new File(getRequestedPath() + ".html");

				if(publicFile.isFile()){
					System.out.println("ARQUIVO PUBLICO: " + publicFile.getName() + " ENCONTRADO");
					setStatusCode(200);
				}
			}
			
//			switch (getHttpMethod()) {
//			case METHOD_GET:
//
//				break;
//			case METHOD_HEAD:
//
//				break;
//			default:
//
//				break;
//			}
			
			conn = ConnectionBD.conectar();
			
			log = new LogRequest(getRequestedPath(), getHttpMethod(), getIp(), getStatusCode());
			log.register(conn);
			response.send(fileInBytes, numOfBytes);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
