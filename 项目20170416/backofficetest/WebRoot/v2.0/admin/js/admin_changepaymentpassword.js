$(document).ready(function(){
	
	$("#btn_edit_operationpassword").click(function(){
		var oldPassword = $("input[name='old_paymentpassword']").val();
		var newPassword = $("input[name='new_operationpassword']").val();
		var confirmPassword = $("input[name='confirm_operationpassword']").val();
		if (newPassword.length < 6) {
			layer.tips("对不起，密码不能少于6位。","input[name='new_operationpassword']",{time:2000,tips: [2,'#56a787']});
			$("input[name='new_operationpassword']").focus();
		} else if (newPassword != confirmPassword) {
			layer.tips("对不起，确认密码不一致。","input[name='confirm_operationpassword']",{time:2000,tips: [2,'#56a787']});
			$("input[name='confirm_operationpassword']").focus();
		} else {
			$.jsonRPC.request('userService.modifyOperationPassword', {
				params : [oldPassword, newPassword],
				success : function(result) {
					if (result) {	//true
							layer.msg("恭喜您，修改操作密码成功!",{time:2000,icon:4});
							$("input[name='old_paymentpassword']").val("");
							$("input[name='new_operationpassword']").val("");
							$("input[name='confirm_operationpassword']").val("");
					} else {	//false
							layer.msg("对不起，您输入的旧操作密码错误。",{time:2000,icon:2});
					}
				},
				error:function(result){
					layer.msg(result.error.msg,{time:2000,icon:2});
				}
			});
			
		}
	});
	
});



function showForgetPaymentpwdTip(){
	layer.tips("如果您忘记了操作密码,可以通过登录页面的“忘记密码”来重新设置",".showForgetPaymentpwdTip",{time:5000,tips: [2,'#56a787']});
}