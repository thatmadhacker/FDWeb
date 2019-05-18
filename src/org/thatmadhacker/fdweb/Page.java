package org.thatmadhacker.fdweb;

import java.util.List;

public class Page {
	
	private List<String> page;
	private PageStatus status;
	private Encoding encoding;
	
	enum PageStatus{
		NOT_FOUND,
		REDIRECT,
		FAILED_UNKNOWN,
		PEER_ERROR,
		CLIENT_ERROR,
		BAD_REQUEST,
		SUCCESS,
		SUCCESS_CACHED;
	}
	enum PageType{
		TEXT,
		BINARY,
		NULL;
	}
	enum Encoding{
		ASCII,
		BASE64,
		NONE;
	}

	public Page(List<String> page, PageStatus status, Encoding encoding) {
		super();
		this.page = page;
		this.status = status;
		this.encoding = encoding;
	}

	public List<String> getPage() {
		return page;
	}

	public PageStatus getStatus() {
		return status;
	}

	public Encoding getEncoding() {
		return encoding;
	}
}
