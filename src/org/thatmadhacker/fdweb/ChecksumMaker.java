package org.thatmadhacker.fdweb;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ChecksumMaker {
	
	public static void main(String[] args) throws Exception{
		
		Scanner in = new Scanner(System.in);
		
		System.out.print("Cache folder: ");
		
		File cacheDir  = new File(in.nextLine());
		
		System.out.print("Site: ");
		
		File domainDir = new File(cacheDir,in.nextLine());
		
		System.out.print("Page: ");
		
		File page = new File(domainDir,in.nextLine());
		File pageChecksum = new File(page.getParentFile(),page.getName()+".sum");
		
		byte[] pageB = Files.readAllBytes(page.toPath());
		
		List<String> lines = new ArrayList<String>();
		
		String s3 = BASE64.encode(pageB);
		String[] s1 = s3.split("\n");
		for (String s2 : s1) {
			lines.add(s2);
		}
		
		String pageS = "";
		
		for(String s : lines) {
			pageS += "\n"+s;
		}
		
		pageS = pageS.replaceFirst("\n", "");
		
		String checksum = HashingUtils.hash(pageS, HashingUtils.SHA512);
		
		pageChecksum.delete();
		pageChecksum.createNewFile();
		
		PrintWriter out = new PrintWriter(new FileWriter(pageChecksum,true));
		
		out.print(checksum);
		
		out.close();
		
		in.close();
		
	}
	
}
