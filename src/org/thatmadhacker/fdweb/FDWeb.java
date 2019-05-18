package org.thatmadhacker.fdweb;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FDWeb {

	public static Page requestPage(String url, Network network, File cacheFolder, boolean cache, long expiryMS, boolean useCached)
			throws Exception {

		if (network.getPeers().size() == 0) {
			throw new Exception("No peers!");
		}

		String domain = url.split("/")[0];
		String page = url.split("/", 2)[1];

		File domainCacheFolder = new File(cacheFolder, domain);

		if (domainCacheFolder.exists() && useCached) {
			// Using cached checksum (and possibly cached page)

			File checksumFile = new File(domainCacheFolder, page + ".sum");

			if (checksumFile.exists()) {

				Scanner in = new Scanner(checksumFile);
				String checksum = "";
				while (in.hasNextLine()) {
					checksum += '\n' + in.nextLine();
				}
				in.close();
				checksum = checksum.replaceFirst("\n", "");

				File pageFile = new File(domainCacheFolder, page);

				if (pageFile.exists()) {
					// Page is cached

					if (pageFile.lastModified()+expiryMS > System.currentTimeMillis() && expiryMS != -1) {
						in.close();
						pageFile.delete();
					} else {

						List<String> pageLines = new ArrayList<String>();
						while (in.hasNextLine()) {
							pageLines.add(in.nextLine());
						}

						in.close();
						
						String[] pageArray = new String[pageLines.size()];
						
						for(int i = 0; i < pageLines.size(); i++) {
							pageArray[i] = pageLines.get(i);
						}
						
						String actualChecksum = getChecksum(pageArray);
						
						if(!actualChecksum.equals(checksum)) {
							throw new Exception("Cached page and cached checksum do not match!!");
						}

						Page retVal = new Page(pageLines, Page.PageStatus.SUCCESS_CACHED,Page.Encoding.ASCII);

						return retVal;
					}

				} else {

					return reqPage(domain, page, network, cacheFolder, cache, expiryMS, checksum);

				}

			}
			// Checksum file does not exist for some weird reason or cached page is expired
			// so continue as if the domain cache folder doesn't exist

		}
		// Not using cached checksum (and no cached pages)

		Socket s = new Socket(domain, 11475);

		PrintWriter out = new PrintWriter(s.getOutputStream(), true);
		Scanner in = new Scanner(s.getInputStream());

		out.println("Length:5");
		out.println("Version:1.0");
		out.println("ReqType:CHECKSUM");
		out.println("Encoding:base64");
		out.println("Site:" + domain);
		out.println("Page:" + page);

		String response = "";

		int length = Integer.valueOf(in.nextLine().split(":")[1]);

		for (int i = 0; i < length; i++) {

			response += "\n" + in.nextLine();

		}

		response = response.replaceFirst("\n", "");

		String[] responseData = response.split("\n");

		Page.PageStatus status = Page.PageStatus.valueOf(responseData[4].split(":")[1]);
		if (status.equals(Page.PageStatus.SUCCESS)) {
			length = Integer.valueOf(responseData[6].split(":")[1]);
			String checksum = "";
			for (int i = 6; i < length + 6; i++) {
				checksum += "\n" + in.nextLine();
			}
			checksum = checksum.replaceFirst("\n", "");
			in.close();
			s.close();
			return reqPage(domain, page, network, cacheFolder, cache, expiryMS, checksum);
		} else {
			in.close();
			s.close();
			throw new Exception("Failed to get page checksum due to " + status.toString());
		}

	}

	private static Page reqPage(String domain, String page, Network network, File cacheFolder, boolean cache,
			long expiryMS, String checksum) throws Exception {

		for (InetAddress addr : network.getPeers()) {

			try {

				Socket s = new Socket(addr, 11475);

				PrintWriter out = new PrintWriter(s.getOutputStream(), true);
				Scanner in = new Scanner(s.getInputStream());

				out.println("Length:6");
				out.println("Version:1.0");
				out.println("ReqType:GET");
				out.println("Encoding:base64");
				out.println("Site:" + domain);
				out.println("Page:" + page);
				out.println("ContentLen:0");

				int length = Integer.valueOf(in.nextLine().split(":")[1]);
				
				String[] response = new String[length];
				
				for(int i = 0; i < length; i++) {
					response[i] = in.nextLine();
				}
				
				Page.PageStatus status = Page.PageStatus.valueOf(response[4].split(":")[1]);
				if(status.equals(Page.PageStatus.SUCCESS)) {
					length = Integer.valueOf(response[6].split(":")[1]);
					
					Page.PageType type = Page.PageType.valueOf(response[5].split(":")[1]);
					
					String[] pageData = new String[length];
					
					for(int i = 0; i < length; i++) {
						
						pageData[i] = in.nextLine();
						
					}
					
					if(type.equals(Page.PageType.TEXT)) {
						for(int i = 0; i < pageData.length; i++) {
							pageData[i] = new String(BASE64.decode(pageData[i]));
						}
					}
					Page.Encoding encoding;
					
					if(type.equals(Page.PageType.TEXT)) {
						encoding = Page.Encoding.ASCII;
					}else {
						encoding = Page.Encoding.BASE64;
					}
					
					String actualChecksum = getChecksum(pageData);
					
					if(!actualChecksum.equals(checksum)) {
						System.err.println("Checksum invalid for page sent from "+addr.toString());
						continue;
					}
					
					List<String> pageL = new ArrayList<String>();
					
					for(String s1 : pageData) {
						pageL.add(s1);
					}
					
					in.close();
					s.close();
					out.close();
					
					if(cache) {
						
						File domainCacheFolder = new File(cacheFolder,domain);
						
						domainCacheFolder.mkdirs();
						
						File pageCacheFile = new File(domainCacheFolder,page);
						
						pageCacheFile.getParentFile().mkdirs();
						
						pageCacheFile.delete();
						pageCacheFile.createNewFile();
						
						out = new PrintWriter(new FileWriter(pageCacheFile,true));
						
						out.println(""+(System.currentTimeMillis()+expiryMS));
						
						for(String s1 : pageL) {
							out.println(s1);
						}
						
						out.close();
						
					}
					
					return new Page(pageL,Page.PageStatus.SUCCESS,encoding);
					
				}else {
					continue;
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		return new Page(null, Page.PageStatus.FAILED_UNKNOWN,null);

	}
	
	private static String getChecksum(String[] page) throws Exception{
		
		String pageS = "";
		
		for(String s : page) {
			pageS += "\n"+s;
		}
		
		pageS = pageS.replaceFirst("\n", "");
		
		String checksum = HashingUtils.hash(pageS, HashingUtils.SHA512);
		
		return checksum;
		
	}

}
