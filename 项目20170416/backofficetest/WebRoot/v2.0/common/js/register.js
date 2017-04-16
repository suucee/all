var cd = 0;

$(document).ready(function(){
	var code = getParam('code', '');
	if (code == '') {
		$("#referralCodeRow").show();
	} else {
		$("[name='referralCode']").val(code);
	}
	
	$("#btn_send_sms").click(function(){
		if (cd > 0) {return;}	//等CD

		var countryCode = $("[name='countryCode']").val();
		var mobile = $("[name='mobile']").val();

		$("[name='email']").css("background", "white");
		$("[name='mobile']").css("background", "white");
		
		if (!/^[0-9]{8,11}$/.test(mobile)) {
			layer.tips("请输入8-11位的手机号！","[name='mobile']",{time:2000,tips:[2,'#56a787']});
		} else {
			$(this).css('background', 'gray');
			$.jsonRPC.request('session2Service.sendSms4Register', {
				params : [mobile, countryCode],
				success : function(result) {
					if (result) {
						cd = 120;
						window.setTimeout("countDown();", 1000);
						layer.msg("短信验证码已发送，5分钟内有效！",{time:2000,icon:1});
					}
				},
				error : function(e) {
					cd = 1;
					window.setTimeout("countDown();", 1000);
				  // layer.alert(e.error.msg);
					layer.msg(e.error.msg,{time:2000,icon:2});
				}
			});
		}
	});
	
	
	$("#btn_register").click(function(){
		var mobile = $("[name='mobile']").val();
		var smsCode = $("[name='smsCode']").val();
		var password = $("[name='password']").val();
		var confirmPassword = $("[name='confirmPassword']").val();
		var countryCode = $("[name='countryCode']").val();
		var referralCode = $("[name='referralCode']").val();
		if (!/^[0-9]{8,11}$/.test(mobile)) {
			layer.tips("请输入8-11位的手机号！","[name='mobile']",{time:2000,tips:[2,'#56a787']});
			return;
		}  if (!/^[0-9]{6}$/.test(smsCode)) {
			layer.tips("请输入6位的手机验证码！","[name='smsCode']",{time:2000,tips:[2,'#56a787']});
			return;
		}  if (password.length < 6) {$("[name='confirmPassword']").focus();
			layer.tips("登录密码不能少于6位！","[name='password']",{time:2000,tips:[2,'#56a787']});
			return;
		}  if (confirmPassword != password) {
			layer.alert('确认登录密码不一致！');
			return;
		} 
		{
			$.jsonRPC.request('session2Service.register', {
				params : [ mobile, smsCode, password,  countryCode, referralCode],
				success : function(result) {
					if (result) {
						//注册成功，自动登录
						$.jsonRPC.request('sessionService.login', {
							params : [mobile, password, countryCode],
							success : function(result) {
								if (result) {
									window.open('index.html', '_self');
								}
							}
						});
					}
				},
				error : function(e) {
					layer.msg(e.error.msg,{time:2000,icon:2});
				}
			});
		}
	});
});

function countDown() {
	cd --;
	if (cd <= 0) {
		$("#btn_send_sms").css('background', '#56a787');
		$("#btn_send_sms").html('发送验证码');
	} else {
		window.setTimeout("countDown();", 950);
		
		$("#btn_send_sms").html(cd + '秒后重发');
	}
}