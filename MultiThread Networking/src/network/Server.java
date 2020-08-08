package network;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Server {

	private HashMap<String, String> loginData;
	private ServerSocket ss;
	private boolean running;
	private int port;
	
	public Server(int port) {
		this.port = port;
		loginData = new HashMap<String, String>();
		try {
			ss  = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(ss != null) {
			running = true;
			parseConnections();
		}else {
			throw new IllegalStateException("Error: Failed to start server");
		}
	}
	
	private void parseConnections() {
		
		while(running){
			try {
				Socket clientSocket = ss.accept();
				System.out.println("Connection from " + clientSocket.getInetAddress());
				Thread clientThread = new Thread(new ClientHandler(clientSocket, this));
				clientThread.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	public boolean attemptLogin(String username, String password) {
		return loginData.get(username).equals(password);
	}
	
	public void addUser(String username, String password) {
		loginData.put(username, password);
		System.out.println("<Server> Adding new user with name: " + username);
	}
}
