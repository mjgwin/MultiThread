package network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;

public class ClientHandler implements Runnable {

	private Socket clientSocket;
	private boolean running, login;
	private Server parentServer;
	private InputStream inStream;
	private OutputStream outStream;
	private DataInputStream dataIn;
	private DataOutputStream dataOut;
	private String username, password;

	public ClientHandler(Socket clientSocket, Server parentServer) {
		this.clientSocket = clientSocket;
		this.parentServer = parentServer;
		login = false;
		init();
	}

	private void init() {
		try {
			inStream = clientSocket.getInputStream();
			outStream = clientSocket.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		dataIn = new DataInputStream(inStream);
		dataOut = new DataOutputStream(outStream);
	}

	public void run() {
		running = true;
		
		try {
			int newUser = dataIn.readInt();
			username = dataIn.readUTF();
			password = dataIn.readUTF();
			
			if(newUser == 0) {
				login = parentServer.attemptLogin(username, password);
			}else if(newUser == 1) {
				parentServer.addUser(username, password);
				login = true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(login == false) {
			System.out.println("<Server> Bad login from user: " + username);
			try {
				dataOut.writeUTF("<Server> Failed login, exiting...");
			} catch (IOException e) {
				e.printStackTrace();
			}
			shutDown();
			return;
		}else {
			System.out.println("<Server> Valid login from user: " + username);
			try {
				dataOut.writeUTF("<Server> Welcome " + username + "!");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		while(running) {
			try {
				String inString = dataIn.readUTF();
				if(inString.equals(null)) {
					continue;
				}else if(inString.equals("/exit")) {
					System.out.println("<Server> closing thread for user: " + username);
					dataOut.writeUTF("<Server> Connection closed");
					running = false;
					shutDown();
				}else {
					System.out.println("<Server> Unhandled command from user: " + username);
					dataOut.writeUTF("<Server> Command not recognized, please try again");
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
