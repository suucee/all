package com.maxivetech.backoffice.pojo;

public class PojoSum {

	private String scheme;
	private double sum;

	public PojoSum() {
	}
	public PojoSum(String scheme, double sum) {
		this.scheme = scheme;
		this.sum = sum;
	}

	public String getScheme() {
		return scheme;
	}

	public void setScheme(String scheme) {
		this.scheme = scheme;
	}
	public double getSum() {
		return sum;
	}
	public void setSum(double sum) {
		this.sum = sum;
	}
	
}