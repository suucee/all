var state =0;
$(document).ready(function(){
	fresh();
	
	$("#btn_edit_paymentpassword").click(function(){
		var oldPassword = $("input[name='old_paymentpassword']").val();
		var newPassword = $("input[name='new_paymentpassword']").val();
		var confirmPassword = $("input[name='confirm_paymentpassword']").val();
		if(state<1){
			if (oldPassword == '') {
				layer.tips("对不起，旧支付密码不能为空。","input[name='old_paymentpassword']",{time:2000,tips: [2,'#56a787']});
				$("input[name='old_paymentpassword']").focus();
				return;
			}
		} 
		if (newPassword.length < 6) {
			layer.tips("对不起，密码不能少于6位。","input[name='new_paymentpassword']",{time:2000,tips: [2,'#56a787']});
			$("input[name='new_paymentpassword']").focus();
		} else if (newPassword != confirmPassword) {
			layer.tips("对不起，确认密码不一致。","input[name='confirm_paymentpassword']",{time:2000,tips: [2,'#56a787']});
			$("input[name='confirm_paymentpassword']").focus();
		} else {
			$.jsonRPC.request('userService.modifyOperationPassword', {
				params : [oldPassword, newPassword],
				success : function(result) {
					if (result) {	//true
						if(state<1){
							layer.msg("恭喜您，修改支付密码成功!",{time:2000,icon:4});
						$("input[name='old_paymentpassword']").val("");
						$("input[name='new_paymentpassword']").val("");
						$("input[name='confirm_paymentpassword']").val("");
						}else{
							layer.msg("恭喜您，支付密码设置成功!",{time:2000,icon:4});
							$("input[name='new_paymentpassword']").val("");
							$("input[name='confirm_paymentpassword']").val("");
							fresh();
						}
					} else {	//false
						if(state<1){
							layer.msg("对不起，您输入的旧密码错误。",{time:2000,icon:2});
						}else{
							layer.msg("对不起，密码设置失败。",{time:2000,icon:2});
						}
					}
				},
				error:function(result){
					layer.msg(result.error.msg,{time:2000,icon:2});
				}
			});
			
		}
	});
	
});

function fresh(){
	$.jsonRPC.request('userService.operationPwd', {
		params : [],
		success : function(result) {
			if (result) {	//true
				$(".oldpaymentpassword").show();  
				$("#btn_edit_paymentpassword").html("修改支付密码");
				state=0;
				
			} else {	//false
				$(".oldpaymentpassword").hide();  
				$("#btn_edit_paymentpassword").html("设置支付密码");
				state=1;
			}
		}
	});
}



function showForgetPaymentpwdTip(){
	layer.tips("如果您忘记了支付密码,可以通过登录页面的“忘记密码”来重新设置",".showForgetPaymentpwdTip",{time:5000,tips: [2,'#56a787']});
}