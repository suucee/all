var complianceCheckBankAccount = null;

var _bankaccount_id = 0;

$(function() {
	check_init(
			"../admin/_check_bankaccount.html","银行卡",
			function(){
				complianceCheckBankAccount = new ComplianceCheckBankAccount();
			},
			function(){
				doCheck('ACCEPTED',"admin2CheckBankAccountService.doCheck",_bankaccount_id)
			},
			function(){
				doCheck('REJECTED',"admin2CheckBankAccountService.doCheck",_bankaccount_id)
			}
		);
});
var ComplianceCheckBankAccount= function() {
	_bankaccount_id = getParam("id", 0);
	this.bankaccount_id = _bankaccount_id;
	this.c_id = getParam("cid", 0);
	this.user_id=0;
	this.init();
};
ComplianceCheckBankAccount.prototype = {
	init : function() {
         this.getOneBankAccount();
         getCddCheckComment(this.c_id);
	},
	
	getOneBankAccount : function() {
		var _this=this;
		$.jsonRPC.request("admin2CheckBankAccountService.getOneUserBankAccounts", {
			params : [_this.bankaccount_id],
			success : function(result) {
			var userBankAccount=result.map.userBankAccount;
			_this.user_id=userBankAccount.user.id;
			
			init_userinfo(_this.user_id);
			
            $(" .bankName").text(userBankAccount['bankName']);
			$(" .bankNo").text(userBankAccount['accountNo']);
			$(" .cardholder_Name").text(userBankAccount['accountName']);
			$(" .countryCode").text(userBankAccount['countryCode']);
			$(" .bankBranch").text(userBankAccount['bankBranch']);
			$(" .bankAddress").text(userBankAccount['bankAddress']);
			if(userBankAccount['countryCode'].trim() == "CN"){
				$(".abroad").hide();
				$(".home").show();
			}else{
				$(".home").hide();
				$(".abroad").show();
			}
			$(" .swiftCode").text(userBankAccount['swiftCode']);
			$(" .ibanCode").text(userBankAccount['ibanCode']);
			switch (userBankAccount['state']) {
			case "WAITING":
				$(".bankOldState").html('<span class="label-pending label" >待审核</span>');
				break;
			case "AUDITED":
				$(".bankOldState").html('<span class="label-pending label" >通过审核</span>');
				$(".bank_check .audited").show();
				break;
			case "REJECTED":
				$(".bankOldState").html('<span class="label-important label" >驳回审核</span>');
				$(".bank_check .rejected").show();
				break;
			default:
				$(".bankOldState").text("未知");
				break;
			}
			var html="";
			if(result.map.attach!=null){
	            for (var i in result.map.attach.list) {
					var imgList = result.map.attach.list[i];
					html += "<div style='width:250px;height:150px;text-align:center;margin:5px;float:left;overflow: hidden;'>";
					html += "<div style='background-image: url(/upload" + imgList.path + ");background-position: center;background-size:auto 100%;background-repeat: no-repeat;width: 250px;height: 150px;line-height: 150px;margin-bottom:5px;'>";
					html += "<div style='width: 250px;height: 150px;line-height: 150px;'>";
					html += "&nbsp;";
					html += "<a style='' href='/upload" + imgList.path + "' target='_blank'  class='btn btn-g'><i class='fa fa-eye' aria-hidden='true'></i>&nbsp;&nbsp;查看</a>";
					html += "</div>";
					html += "</div>";
					html += "</div>";
				}
			}
			$(".theimageShow").html(html);
			}
		});
	},
	
};
