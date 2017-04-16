package com.maxivetech.backoffice.pojo;

public class PojoCounts {

	private String scheme;
	private long count;

	public PojoCounts() {
	}
	public PojoCounts(String scheme, long count) {
		this.scheme = scheme;
		this.count = count;
	}

	public String getScheme() {
		return scheme;
	}

	public void setScheme(String scheme) {
		this.scheme = scheme;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	
}