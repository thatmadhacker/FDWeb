package org.thatmadhacker.fdweb;

import java.io.File;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Scanner;

public class PageRequester {
	
	public static void main(String[] args) throws Exception{
		
		Scanner in = new Scanner(System.in);
		
		System.out.print("Cache folder: ");
		File cacheDir = new File(in.nextLine());
		System.out.print("Url: ");
		String url = in.nextLine();
		
		Network network = new Network(new ArrayList<InetAddress>());
		network.getPeers().add(InetAddress.getByName("localhost"));
		
		Page page = FDWeb.requestPage(url, network, cacheDir, true, -1,false);
		
		for(String line : page.getPage()) {
			
			System.out.println(line);
			
		}
		
		in.close();
		
	}
	
}
