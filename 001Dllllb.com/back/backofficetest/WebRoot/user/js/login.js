$(document).ready(function(){
	//
	$("#login_form").submit(function(){
		var countryCode = $("[name='countryCode']").val();
		var account = $("#account").val();
		var password = $("#password").val();
		var remembered = $("input[name=remember_password]:checked").length;
		
		if (account.length == 0) {
			$("#account").focus();
		} else if (password.length == 0) {
			$("#password").focus();
		} else {
			$.jsonRPC.request('sessionService.login', {
				params : [account, password, countryCode],
				success : function(result) {
					if (result != null) {
						//Success
						locStorage("loginUser", result);

						setCookie('TOKEN', result.token, 30 * 24);
						window.open('./start'+(result.role == 'User' ? '_user' : '_admin')+'.html', '_self');
					} else {
						//Failed
						locStorage("loginUser", null);
						
						setCookie('TOKEN', null, -1);
						window.alert("登录失败，请检查账号和密码!");
					} 
				}
			});
		}
		
		return false;
	});
});
