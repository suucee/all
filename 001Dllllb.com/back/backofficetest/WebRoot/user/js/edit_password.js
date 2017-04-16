$(document).ready(function(){
	$("#btn_edit_password").click(function(){
		var oldPassword = $("input[name='old_password']").val();
		var newPassword = $("input[name='new_password']").val();
		var confirmPassword = $("input[name='confirm_password']").val();
		
		if (oldPassword == '') {
			window.alert("舊密碼不能爲空!");
			$("input[name='old_password']").focus();
		} else if (newPassword.length < 6) {
			window.alert("密碼不能少于6位!");
			$("input[name='new_password']").focus();
		} else if (newPassword != confirmPassword) {
			window.alert("確認密碼不一致!");
			$("input[name='confirm_password']").focus();
		} else {
			$.jsonRPC.request('userService.modifyPassword', {
				params : [oldPassword, newPassword],
				success : function(result) {
					if (result) {	//true
						window.alert("修改登录密码成功!");
						$("input[name='old_password']").val("");
						$("input[name='new_password']").val("");
						$("input[name='confirm_password']").val("");
					} else {	//false
						window.alert("旧密码错误!");
					}
				}
			});
			
		}
	});
	
});

