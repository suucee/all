var complianceCheckDeposit = null;
var _deposit_id = 0;

$(function() {
	finance_init(
			"../admin/_check_deposit.html","代为入金",
			function(){
				complianceCheckDeposit = new ComplianceCheckDeposit();
			},
			function(){
				finance_deposit_check('DEPOSITED');
			},
			function(){
				finance_deposit_check('REJECTED');
			}
		);
});
var ComplianceCheckDeposit = function() {
	_deposit_id = getParam("id", 0);
	this.deposit_id = _deposit_id; 
	this.user_id=0;
	this.init();
};
ComplianceCheckDeposit.prototype = {
	init : function() {
         this.getOneDeposit();
	},
	getOneDeposit : function() {
		var _this=this;
		$.jsonRPC.request("admin2DepositService.getOne", {
			params : [_this.deposit_id],
			success : function(result) {
			_this.user_id=result._userId;

			init_userinfo(_this.user_id);
			
			var labelClassName = getDepositLabelClassName(result['state']);
			var label_class = labelClassName[0];
			var label_name = labelClassName[1];
			$(".deposit_state").html('<span class="label '+label_class+'">'+label_name+'</span>');//////////状态///////////
			$(" .USD_ICO").html(USD_ICO);//////////USD图标///////////
			
			$(" .creatTime").text(toDate(result['creatTime'].time));
			$(" .accountName").text(result['accountName']);
			$(" .accountNo").text(result['accountNumber']);
			
			$(" .userName").text(result['_userName']);
			$(" .userEmail").text(result['_userEmail']);
			
			$(" .amount").text(result['amount'].toFixed(2));
			$(" .amount_CNY").text((result['amount'] * result['exchangeRate']).toFixed(2));/////////等价CNY金额/////////////
			$(" .userMemo").text(result['userComment']);
			}
		});
	}
};


/**
 * 用于财务的代为入金审核
 * @param scheme
 * @param jsonRPCRequestName
 * @param doCheckId
 */
function finance_deposit_check  (scheme) {
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
	
	//这个是大额出金的审核，或者是代为入金的审核-由财务主管审核
	$.jsonRPC.request("adminDepositService.changeState", {
		params : [_deposit_id,  password, scheme, admin_comment],
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


function refreshWithdrawalInfo(){
	if(complianceCheckDeposit!=null){
		complianceCheckDeposit.getOneDeposit();
	}
}
