package org.thatmadhacker.fdweb;

import java.io.File;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PeerServer extends Thread{
	
	public static void startServer(File cacheDir) {
		new PeerServer(cacheDir).start();
	}
	
	private File cacheDir;
	
	public PeerServer(File cacheDir) {
		super();
		this.cacheDir = cacheDir;
	}

	@Override
	public void run() {
		
		try {
			
			ServerSocket ss = new ServerSocket(11475);
			
			while(!ss.isClosed()) {
				
				Socket s = ss.accept();
				
				new PeerServerThread(s,cacheDir).start();
				
			}
			
			ss.close();
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private class PeerServerThread extends Thread{
		
		private Socket s;
		private File cacheDir;
		
		public PeerServerThread(Socket s, File cacheDir) {
			this.s = s;
			this.cacheDir = cacheDir;
		}
		
		@Override
		public void run() {
			
			try {
				
				PrintWriter out = new PrintWriter(s.getOutputStream(),true);
				Scanner in = new Scanner(s.getInputStream());
				
				int length = Integer.valueOf(in.nextLine().split(":")[1]);
				
				String[] request = new String[length];
				
				for(int i = 0; i < length; i++) {
					request[i] = in.nextLine();
				}
				
				String reqType = request[1];
				
				String domain = request[3];
				String page = request[4];
				
				if(reqType.equalsIgnoreCase("GET") || reqType.equalsIgnoreCase("POST")) {
					
					if(domain.contains("../") || domain.startsWith("/") || page.contains("../") || page.startsWith("/")) {
						
						out.println("Length:6");
						out.println("Version:1.0");
						out.println("Encoding:base64");
						out.println("Site:"+domain);
						out.println("Page:"+page);
						out.println("Status:NOT_FOUND");
						out.println("ContentLen:0");
						
						s.close();
						in.close();
						out.close();
						return;
					}
					
					File domainCacheDir = new File(cacheDir,domain);
					File pageCacheFile = new File(domainCacheDir,page);
					if(!pageCacheFile.exists()) {
						out.println("Length:6");
						out.println("Version:1.0");
						out.println("Encoding:base64");
						out.println("Site:"+domain);
						out.println("Page:"+page);
						out.println("Status:NOT_FOUND");
						out.println("ContentLen:0");
						
						s.close();
						in.close();
						out.close();
						return;
					}
					
					Scanner in2 = new Scanner(pageCacheFile);
					List<String> lines = new ArrayList<String>();
					
					while(in2.hasNextLine()) {
						lines.add(in2.nextLine());
					}
					
					in2.close();
					
					long expiryTime = Long.valueOf(lines.get(0));
					
					if(System.currentTimeMillis() > expiryTime && expiryTime != -1) {
						pageCacheFile.delete();
						out.println("Length:6");
						out.println("Version:1.0");
						out.println("Encoding:base64");
						out.println("Site:"+domain);
						out.println("Page:"+page);
						out.println("Status:NOT_FOUND");
						out.println("ContentLen:0");
						
						s.close();
						in.close();
						out.close();
						return;
					}
					lines.remove(0); //Removes the expiry timestamp
					
					out.println("Length:6");
					out.println("Version:1.0");
					out.println("Encoding:base64");
					out.println("Site:"+domain);
					out.println("Page:"+page);
					out.println("Status:SUCCESS");
					out.println("ContentLen:"+lines.size());
					
					for(String s : lines) {
						out.println(s);
					}
					
					s.close();
					in.close();
					out.close();
					return;
					
				}else if(reqType.equalsIgnoreCase("CHECKSUM")) {
					
					if(domain.contains("../") || domain.startsWith("/") || page.contains("../") || page.startsWith("/")) {
						
						out.println("Length:6");
						out.println("Version:1.0");
						out.println("Encoding:base64");
						out.println("Site:"+domain);
						out.println("Page:"+page);
						out.println("Status:NOT_FOUND");
						out.println("ContentLen:0");
						
						s.close();
						in.close();
						out.close();
						return;
					}
					
					File domainCacheFolder = new File(cacheDir,domain);
					File pageChecksum = new File(domainCacheFolder,page+".sum");
					
					Scanner in2 = new Scanner(pageChecksum);
					String checksum = "";
					while (in.hasNextLine()) {
						checksum += '\n' + in.nextLine();
					}
					in2.close();
					checksum = checksum.replaceFirst("\n", "");
					
					out.println("Length:6");
					out.println("Version:1.0");
					out.println("Encoding:base64");
					out.println("Site:"+domain);
					out.println("Page:"+page);
					out.println("Status:SUCCESS");
					out.println("ContentLen:"+checksum.split("\n").length);
					out.println(checksum);
					
					in.close();
					s.close();
					out.close();
					return;
				}
				
				out.println("Length:6");
				out.println("Version:1.0");
				out.println("Encoding:base64");
				out.println("Site:"+domain);
				out.println("Page:"+page);
				out.println("Status:BAD_REQUEST");
				out.println("ContentLen:0");
				
				in.close();
				s.close();
				out.close();
				return;
				
			}catch(Exception e) {
				e.printStackTrace();
			}
			
		}
		
	}
	
}
