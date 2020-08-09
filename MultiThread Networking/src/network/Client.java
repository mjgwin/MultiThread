package network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

public class Client {

	private Socket clientSocket;
	private String ip;
	private int port;
	private int newUser;
	private String username, password;
	private InputStream inStream;
	private OutputStream outStream;
	private DataInputStream dataIn;
	private DataOutputStream dataOut;
	private boolean running;
		
	
	public Client(String ip, int port) {
		this.ip = ip;
		this.port = port;
		prepare();
	}
	
	private void prepare() {
		Scanner sc = new Scanner(System.in);
		boolean needsInfo = true;
		while(needsInfo) {
			System.out.println("<Client> Enter 0 if existing user and 1 for new user");
			newUser = sc.nextInt();
			if(newUser == 0) {
				System.out.println("Welcome back! Enter your username and password");
				needsInfo = false;
			}else if (newUser == 1) {
				System.out.println("<Client> Welcome new user! Pick a username and password");
				needsInfo = false;
			}else {
				System.out.println("<Client> Bad args, retry");
				continue;
			}
			System.out.println("<Client> Enter your username: ");
			username = sc.next();
			System.out.println("<Client> Enter your password: ");
			password = sc.next();
		}
		try {
			clientSocket = new Socket(ip, port);
			setupStreams();
			dataOut.writeInt(newUser);
			dataOut.writeUTF(username);
			dataOut.writeUTF(password);
			processRequests();
		} catch (IOException e) {
			e.printStackTrace();
		}
		shutDown();
	}
	
	private void setupStreams() {
		if(clientSocket == null) {
			throw new IllegalStateException("Error: client socket was not setup properly");
		}
		try {
			inStream = clientSocket.getInputStream();
			outStream = clientSocket.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		dataIn = new DataInputStream(inStream);
		dataOut = new DataOutputStream(outStream);
	}
	
	private void processRequests() {
		running = true;
		Scanner sc = new Scanner(System.in);
		while(running) {
			try {
				String inString = dataIn.readUTF();
				if(inString.equals(null)) {
					//continue
				}else if(inString.equals("<Server> Connection closed")) {
					System.out.println(inString);
					System.out.println("<Client> Shutting down...");
					running = false;
					shutDown();
					break;
				}else {
					System.out.println(inString);
				}
				
				String outString = sc.next();
				if(outString.equals(null)) {
					continue;
				}else {
					dataOut.writeUTF(outString);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}
	}
	
	private void shutDown() {
		try {
			clientSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
