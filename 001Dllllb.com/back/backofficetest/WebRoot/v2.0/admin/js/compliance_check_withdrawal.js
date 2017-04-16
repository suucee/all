var complianceCheckWithdrawal = null;
var _withdrawal_id = 0;
$(function() {
	check_init(
			"../admin/_check_withdrawal.html","出金",
			function(){
				complianceCheckWithdrawal = new ComplianceCheckWithdrawal();
			},
			function(){
				doCheck('ACCEPTED',"admin2CheckWithdrawalService.doCheck",_withdrawal_id)
			},
			function(){
				doCheck('REJECTED',"admin2CheckWithdrawalService.doCheck",_withdrawal_id)
			}
		);
	
});
var ComplianceCheckWithdrawal = function() {
	_withdrawal_id= getParam("id", 0);
	this.withdrawal_id = _withdrawal_id;
	this.c_id = getParam("cid", 0);
	this.user_id=0;
	this.init();
};
ComplianceCheckWithdrawal.prototype = {
	init : function() {
         this.getOneWithdrawal();
         getCddCheckComment(this.c_id);
	},
	getOneWithdrawal : function() {
		var _this=this;
		$.jsonRPC.request("admin2CheckWithdrawalService.getOneWithdrawals", {
			params : [_this.withdrawal_id],
			success : function(result) {
			_this.user_id=result._userId;

			init_userinfo(_this.user_id);
			
			var labelClassName = getWithdrawalLabelClassName(result['state']);
   			var label_class = labelClassName[0];
			var label_name = labelClassName[1];
			
			$(".withdrawal_state").html('<span class="label '+label_class+'">'+label_name+'</span>');//////////状态///////////
			
			$(" .USD_ICO").html(USD_ICO);//////////USD图标///////////
			$(" .CNY_ICO").html(CNY_ICO);//////////CNY图标///////////
			
            $(" .bankName").text(result['bankName']);
			$(" .rate").text(result['exchangeRate']);//////////出金汇率///////////
			$(" .countryCode").text(result['country']);
			$(" .creatTime").text(toDate(result['creatTime'].time));
			$(" .accountName").text(result['accountName']);
			$(" .accountNo").text(result['accountNumber']);
			$(" .amount").text(result['amount'].toFixed(2));
			$(" .amount_CNY").text((result['amount'] * result['exchangeRate']).toFixed(2));/////////等价CNY金额/////////////
			$(" .userMemo").text(result['userMemo']);
			$(" .user_comment").html(result.auditedMemo);
			$(" .bankBranch").text(result['bankBranch']);
			$(" .bankAddress").text(result['bankAddress']);
			$(" .swiftCode").text(result['swiftCode']);
			$(" .ibanCode").text(result['ibanCode']);
			if(result['country']=="CN"){
		    	$(" .home").show();
		    	$(" .abroad").hide();
		    }else if(result['country']!=null&&result['country']!=""){
		    	$(" .home").hide();
		    	$(" .abroad").show();
		    }
			}
		});
	}
};
