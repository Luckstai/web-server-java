package server;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer extends Thread {

	static final int PORT_NUMBER = 12345;
	
	public static void main(String args[]) throws Exception {

		@SuppressWarnings("resource")
		ServerSocket socket = new ServerSocket(PORT_NUMBER);
		System.out.println("Server run in port " + PORT_NUMBER);

		while (true) {
			Socket connected = socket.accept();
			(new HttpRequest(connected)).start();
		}
	}
}