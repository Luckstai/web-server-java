package server;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.StringTokenizer;

public class HttpServer extends Thread {
	
	static final int PORT_NUMBER = 12345;
	static final String BASE_PATH = Paths.get(".").toAbsolutePath().normalize().toString() + "\\public";
	static final String METHOD_GET = "GET";
	static final String METHOD_HEAD = "HEAD";
	static final String METHOD_POST = "POST";
	
	String contentTypeLine = "text/html";

	Socket connectedClient = null;
	FileInputStream fis = null;
	BufferedReader inFromClient = null;
	BufferedInputStream bis = null;
	DataOutputStream outToClient = null;
	OutputStream os = null;
	
	public HttpServer(Socket client) {
		connectedClient = client;
	}

	public void run() {

		try {
			System.out.println("Cliente " + connectedClient.getInetAddress() + ":" + connectedClient.getPort() + " conectado!");

			inFromClient = new BufferedReader(new InputStreamReader(connectedClient.getInputStream()));
			outToClient = new DataOutputStream(connectedClient.getOutputStream());

			String requestString = inFromClient.readLine();
			String headerLine = requestString;

			StringTokenizer tokenizer = new StringTokenizer(headerLine);
			String httpMethod = tokenizer.nextToken();
			String httpQueryString = tokenizer.nextToken();
			StringBuffer responseBuffer = new StringBuffer();

			System.out.println("Request HTTP");
			while (inFromClient.ready()) {
				System.out.println(requestString);
				responseBuffer.append(requestString + "<BR>");
				requestString = inFromClient.readLine();
			}

			int position = httpQueryString.lastIndexOf(".");
			String extension = httpQueryString.substring(position+1);
			String path= "";
			
			switch (extension) {
			case "html":
				contentTypeLine = "text/html";
				path = BASE_PATH;
				break;

			case "css":
				contentTypeLine = "text/css";
				path = BASE_PATH + "\\assets\\css";
				break;		
				
			case "js":
				contentTypeLine = "application/javascript";
				path = BASE_PATH + "\\assets\\js";
				break;					
				
			case "ico":
				contentTypeLine = "image/x-icon";
				path = BASE_PATH + "\\assets\\image";
				break;					
				
			default:
				break;
			}
			
			path = BASE_PATH + httpQueryString.replaceAll("\"([/])\"", "\\");
			
			switch (httpMethod) {
			case METHOD_GET:			
				if (httpQueryString.equals("/")) path = BASE_PATH + "\\index.html";
				
				FileInputStream fis =  new FileInputStream(path);
				Scanner sc = new Scanner(fis);
				String conteudo = "";
				while(sc.hasNext()) conteudo += sc.nextLine();
				sc.close();
				
				sendResponse(200, conteudo);
				break;

			case METHOD_HEAD:
				sendResponse(200, "");
				
				break;
			case METHOD_POST:
				System.out.println(requestString);
				sendResponse(200,"");
				break;
			default:
				sendResponse(404,"<b>The Requested resource not found .... Usage: http://127.0.0.1:5000 or http://127.0.0.1:5000/</b>");
				
				break;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendResponse(int statusCode, String responseString) throws Exception {

		String statusLine = null;
		String serverdetails = "Server: Java HTTPServer \r\n";
		String contentLengthLine = null;
		String contentType = "Content-Type: " + contentTypeLine + "\r\n";

		if (statusCode == 200) {
			statusLine = "HTTP/1.1 200 OK" + "\r\n";
		}			
		else {
			statusLine = "HTTP/1.1 404 Not Found" + "\r\n";
		}
		
		contentLengthLine = "Content-Length: " + responseString.length() + "\r\n";

		outToClient.writeBytes(statusLine);
		outToClient.writeBytes(serverdetails);
		outToClient.writeBytes(contentType);
		outToClient.writeBytes(contentLengthLine);
		outToClient.writeBytes("Connection: close\r\n");
		outToClient.writeBytes("\r\n");
		outToClient.writeBytes(responseString);
		outToClient.close();
		interrupt();
	}

	public static void main(String args[]) throws Exception {

		ServerSocket socket = new ServerSocket(PORT_NUMBER);
		System.out.println("HTTPServer esperando por uma conexão na porta " + PORT_NUMBER);

		while (true) {
			Socket connected = socket.accept();
			(new HttpServer(connected)).start();
		}
	}
}