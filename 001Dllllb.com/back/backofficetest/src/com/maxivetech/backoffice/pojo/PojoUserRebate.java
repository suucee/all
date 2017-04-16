package com.maxivetech.backoffice.pojo;

public class PojoUserRebate {

    private String name;
    private Double rebate;
    private Double volume;
    private Integer OrderUserId;


	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Double getRebate() {
		return rebate;
	}
	public void setRebate(Double rebate) {
		this.rebate = rebate;
	}
	public Double getVolume() {
		return volume;
	}
	public void setVolume(Double volume) {
		this.volume = volume;
	}
	public Integer getOrderUserId() {
		return OrderUserId;
	}
	public void setOrderUserId(Integer orderUserId) {
		OrderUserId = orderUserId;
	}

    
}
