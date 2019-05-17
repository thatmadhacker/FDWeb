package org.thatmadhacker.fdweb;

import java.net.InetAddress;
import java.util.List;

public class Network {
	
	private List<InetAddress> peers;

	public Network(List<InetAddress> peers) {
		super();
		this.peers = peers;
	}

	public List<InetAddress> getPeers() {
		return peers;
	}

	public void setPeers(List<InetAddress> peers) {
		this.peers = peers;
	}
	
}
