package org.thatmadhacker.fdweb;

import java.io.File;
import java.util.Scanner;

public class PeerServerTest {
	
	@SuppressWarnings("resource")
	public static void main(String[] args) {
		
		Scanner in = new Scanner(System.in);
		System.out.print("Cache folder: ");
		File cacheFolder = new File(in.nextLine());
		
		PeerServer.startServer(cacheFolder);
		
	}
	
}
