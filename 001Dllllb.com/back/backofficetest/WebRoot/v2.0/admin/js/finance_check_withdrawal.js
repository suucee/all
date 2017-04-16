/**
 * 待财务主管审核--大额出金
 */
var financeCheckWithdrawal = null;
var _withdrawal_id = 0;

$(function() {
	finance_init(
			"../admin/_check_withdrawal.html","大额出金",
			function(){
				financeCheckWithdrawal = new FinanceCheckWithdrawal();
			},
			function(){
				financeCheckWithdrawal.check('AUDITED',"adminDepositService.changeState",_withdrawal_id)
			},
			function(){
				financeCheckWithdrawal.check('REJECTED',"adminDepositService.changeState",_withdrawal_id)
			}
		);
});
var FinanceCheckWithdrawal = function() {
	_withdrawal_id= getParam("id", 0);
	this.withdrawal_id = _withdrawal_id;
	this.user_id=0;
	this.init();
};
FinanceCheckWithdrawal.prototype = {
	init : function() {
         this.getOneWithdrawal();
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
	},
	check : function (scheme){
		var admin_comment = $(".admin_comment").val();
		if(scheme == "REJECTED"){
			if(admin_comment.trim() == ""){
				layer.tips("请填写驳回的理由，以便客户进行修改和再次提交。",".admin_comment",{time:2000,tips:[3,'#56a787']});
				$(".admin_comment").focus();
				return;
			}
		}
		var password = $(".password").val();
		if (password == "") {
			layer.tips("操作密码不能为空哦.",".password",{time:2000,tips:[2,'#56a787']});
			$(".password").focus();
			return;
		}
		
		$.jsonRPC.request("adminWithdrawalService.changeState", {
			params : [_withdrawal_id, "PENDING_SUPERVISOR", password, scheme,admin_comment],
			success : function(result) {
				if (result) {
					if(result=="操作成功"){
						layer.msg(result,{time:1500,icon:1},function(){
							parent.closeWindow();//关闭窗口
						});
						
					}else{
						layer.msg(result,{time:2000,icon:2});
					}
					$(".password").val("");
					refreshWithdrawalInfo();
				}
			}
		});
	}
	

};




function refreshWithdrawalInfo(){
	if(financeCheckWithdrawal!=null){
		financeCheckWithdrawal.getOneWithdrawal();
	}
}
