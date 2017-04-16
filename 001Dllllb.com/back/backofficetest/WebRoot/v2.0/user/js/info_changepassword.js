$(document).ready(function(){
	
	var style = getParam("style",null);
	if(style != null){//跳转设置支付密码
		$("#activateAccount").hide(200);
	}else{
		$("#activateAccount").show(200);
	}
	
	
	$("#btn_edit_password").click(function(){
		var oldPassword = $("input[name='old_password']").val();
		var newPassword = $("input[name='new_password']").val();
		var confirmPassword = $("input[name='confirm_password']").val();
		
		if (oldPassword == '') {
			layer.tips("对不起，旧密码不能为空。","input[name='old_password']",{time:2000,tips: [2,'#56a787']});
			$("input[name='old_password']").focus();
		} else if (newPassword.length < 6) {
			layer.tips("对不起，密码不能少于6位。","input[name='new_password']",{time:2000,tips: [2,'#56a787']});
			$("input[name='new_password']").focus();
		} else if (newPassword != confirmPassword) {
			layer.tips("对不起，确认密码和新密码不一致。","input[name='confirm_password']",{time:2000,tips: [2,'#56a787']});
			$("input[name='confirm_password']").focus();
		} else if(newPassword == oldPassword){
			layer.tips("对不起，旧密码不能和新密码不一致。","input[name='new_password']",{time:2000,tips: [2,'#56a787']});
			$("input[name='new_password']").focus();
		}
		else {
			$.jsonRPC.request('userService.modifyPassword', {
				params : [oldPassword, newPassword],
				success : function(result) {
					if (result) {	//true
						layer.msg("恭喜您，修改登录密码成功!",{time:2000,icon:4});
						$("input[name='old_password']").val("");
						$("input[name='new_password']").val("");
						$("input[name='confirm_password']").val("");
					} else {	//false
						layer.msg("对不起，更改密码失败。请检查旧密码是否正确。",{time:2000,icon:2});
					}
				}
			});
			
		}
	});
	
});

