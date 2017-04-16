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
				window.alert("舊密碼不能爲空!");
				$("input[name='old_password']").focus();
			}
		} 
		if (newPassword.length < 6) {
			window.alert("密碼不能少于6位!");
			$("input[name='new_password']").focus();
		} else if (newPassword != confirmPassword) {
			window.alert("確認密碼不一致!");
			$("input[name='confirm_password']").focus();
		} else {
			$.jsonRPC.request('userService.modifyOperationPassword', {
				params : [oldPassword, newPassword],
				success : function(result) {
					if (result) {	//true
						if (state<1) {
							window.alert("密码修改成功!");
							$("input[name='old_password']").val("");
							$("input[name='new_password']").val("");
							$("input[name='confirm_password']").val("");
						} else {
							window.alert("密码设置成功!");
							$("input[name='new_password']").val("");
							$("input[name='confirm_password']").val("");
						}
					} else {	//false
						if (state<1) {
							window.alert("旧密码错误!");
						} else{
							window.alert("密码设置失败!");
						}
					}
				}
			});
			
		}
	});
	
});

