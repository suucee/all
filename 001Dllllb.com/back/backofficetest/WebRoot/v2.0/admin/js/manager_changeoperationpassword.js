$(document).ready(function(){
	var state =0;
	$.jsonRPC.request('userService.operationPwd', {
		params : [],
		success : function(result) {
			if (result) {	//true
			} else {	//false
				$(".oldpassword").hide();  
				$(".updatepwd").html("添加操作密码");
				$(".xinxi").html("添加操作密码");
				state=1;
			}
		}
	});
	
	
	$("#btn_edit_password").click(function(){
		var oldPassword = $("input[name='old_password']").val();
		var newPassword = $("input[name='new_password']").val();
		var confirmPassword = $("input[name='confirm_password']").val();
		if(state<1){
			if (oldPassword == '') {
				layer.tips("对不起，旧密码不能为空。","input[name='old_password']",{time:2000,tips: [2,'#56a787']});
				$("input[name='old_password']").focus();
			}
		} 
		if (newPassword.length < 6) {
			layer.tips("对不起，密码不能少于6位。","input[name='new_password']",{time:2000,tips: [2,'#56a787']});
			$("input[name='new_password']").focus();
		} else if (newPassword != confirmPassword) {
			layer.tips("对不起，确认密码和新密码不一致。","input[name='confirm_password']",{time:2000,tips: [2,'#56a787']});
			$("input[name='confirm_password']").focus();
		} else {
			$.jsonRPC.request('userService.modifyOperationPassword', {
				params : [oldPassword, newPassword],
				success : function(result) {
					if (result) {	//true
						if (state<1) {
							layer.msg("恭喜您，修改登录密码成功!",{time:2000,icon:4});
							$("input[name='old_password']").val("");
							$("input[name='new_password']").val("");
							$("input[name='confirm_password']").val("");
						} else {
							layer.msg("恭喜您，密码设置成功!",{time:2000,icon:4});
							$("input[name='new_password']").val("");
							$("input[name='confirm_password']").val("");
						}
					} else {	//false
						if (state<1) {
							layer.msg("对不起，您输入的旧密码错误。",{time:2000,icon:2});
						} else{
							layer.msg("对不起，密码设置失败。",{time:2000,icon:2});
						}
					}
				}
			});
			
		}
	});
	
});

