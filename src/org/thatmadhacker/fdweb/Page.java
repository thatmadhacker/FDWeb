package org.thatmadhacker.fdweb;

import java.util.List;

public class Page {
	
	private List<String> page;
	private PageStatus status;
	
	enum PageStatus{
		NOT_FOUND,
		REDIRECT,
		FAILED_UNKNOWN,
		PEER_ERROR,
		CLIENT_ERROR,
		SUCCESS,
		SUCCESS_CACHED;
	}

	public Page(List<String> page, PageStatus status) {
		super();
		this.page = page;
		this.status = status;
	}

	public List<String> getPage() {
		return page;
	}

	public PageStatus getStatus() {
		return status;
	}
	
}
