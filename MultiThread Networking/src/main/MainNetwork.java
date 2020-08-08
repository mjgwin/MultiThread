package main;

import java.util.Scanner;

import network.Client;
import network.Server;

public class MainNetwork {
	
	private static Server server;
	private static Client client;

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter 0 for server and 1 for client");
		int choice = sc.nextInt();
		if(choice == 0) {
			server = new Server(1234);
		}else if (choice == 1) {
			client = new Client("192.168.1.126", 1234);
		}else {
			System.out.println("Bad args, exiting...");
		}
	}

}
