package com.maxivetech.backoffice.annotation;

public enum Role {
	ComplianceOfficer("合规"),
	FinancialStaff("财务"),
	FinancialSuperior("财务主管"),
	CustomerServiceStaff("客服"),
	Webmaster("网站管理员"),
	OperationsManager("运维经理"),
	RiskManagementCommissioner("风控专员"),
	User("用户");
	
	private String name;
	
	Role(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
