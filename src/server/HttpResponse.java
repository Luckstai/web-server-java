package server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class HttpResponse {

	Socket conn = null;
	DataOutputStream outToClient = null;
	
	private String contentType = "text/html";
	private String status = "OK";
	private Integer statusCode = 200;
	private String server = "Windows10";
	private int contentLength = 0;
	private String connection = "close";
	
	public HttpResponse(Socket conn) {
		this.conn = conn;
	}
	
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Integer getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(Integer statusCode) { 
		this.statusCode = statusCode;
	}
	public String getServer() {
		return server;
	}
	public void setServer(String server) {
		this.server = server;
	}
	public int getContentLength() {
		return contentLength;
	}
	public void setContentLength(int contentLength) {
		this.contentLength = contentLength;
	}
	
	public String getConnection() {
		return connection;
	}
	
	public void setConnection(String connection) {
		this.connection = connection;
	}
	
	private String getHeader() {
		return 
			"HTTP/1.1 " + getStatusCode() + " " + getStatus() +"\r\n"
			+ "Server: " + getServer() + "\r\n"
			+ "Content-Type: " + getContentType() + "\r\n"//+ "; charset=utf-8\r\n"
			+ "Content-Length: " + getContentLength() + "\r\n"
			+ "Connection: " + getConnection() + "\r\n"
			+"\r\n";
		
	}
	
	public void send(String content) throws IOException {
		outToClient = new DataOutputStream(conn.getOutputStream());
		setContentLength(content.length());
		outToClient.writeBytes(getHeader());
		outToClient.writeBytes(content);
		outToClient.close();
	}
}
