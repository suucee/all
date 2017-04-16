$(document).ready(function() {
	var userMobile = true;
	
	$(".tab_mobile").click(function () {
		$(".tab_mobile").css({"borderBottom":"5px solid rgb(222, 148, 42)","cursor":"default","fontWeight":"bold"});
		$(".tab_emial").css({"borderBottom":"none","cursor":"pointer","fontWeight":"normal"});
		$("#account").animate({"width":"170px"},200,"linear",
			function () {
				userMobile = true;
				$("#account").attr("placeholder","手机号码");
				$(".select").show(300);
			}
		);
	});
	
	$(".tab_emial").click(function () {
		$(".tab_emial").css({"borderBottom":"5px solid rgb(222, 148, 42)","cursor":"default","fontWeight":"bold"});
		$(".tab_mobile").css({"borderBottom":"none","cursor":"pointer","fontWeight":"normal"});
		$(".select").hide(300,function () {
			userMobile = false;
			$("#account").attr("placeholder","电子邮箱");
			$("#account").animate({"width":"238px"},200,"linear");
		});
	});
	
	
	//
	$(".btn_login").click(function() {
		var countryCode = $("[name='countryCode']").val();
		var account = $("#account").val();
		var password = $("#password").val();
		var remembered = $("input[name=remember_password]:checked").length;

		if (account.length == 0) {
			layer.tips("账号不能为空哦。","#account",{time:2000,tips:[2,'#56a787']});
			$("#account").focus();
			return ;
		} else if (password.length == 0) {
			layer.tips("密码不能为空哦。","#password",{time:2000,tips:[2,'#56a787']});
			$("#password").focus();
			$("#password").focus();
		} 
		else {
			if(userMobile){//用手机号登录
				if (!/^[0-9]{8,11}$/.test(account)) {
					layer.tips("请输入8-11位的手机号！","#account",{time:2000,tips:[2,'#56a787']});
					return ;
				}
			}
//			else{//用邮箱登录
//				if (!/^([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\-|\_|\.]?)*[a-zA-Z0-9]+\.[a-zA-Z]{2,10}$/.test(account)){
//					layer.tips("请输入正确的电子邮箱地址.","#account",{time:2000,tips:[2,'#56a787']});
//					return;
//				}
//			}
			
			
			$.jsonRPC.request('session2Service.loginPwdError', {
				params : [account, password, countryCode],
				success : function(result) {
					if (result != null) {
						//Success
						locStorage("loginUser", result);

						setCookie('TOKEN', result.token, 30 * 24);
						window.open('index.html', '_self');
					} else {
						//Failed
						locStorage("loginUser", null);
						setCookie('TOKEN', null, -1);
						layer.msg("登录失败，请检查账号和密码!",{time:2000,icon:2});
					}
				},
				error:function(result){
					layer.alert(result.error.msg,{icon:2});
				}
				
			});
		}

		return false;
	});
});