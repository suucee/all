var cd = 0;

$(document).ready(function(){
	$("#btn_send_sms").click(function(){
		if (cd > 0) {return;}	//等CD
		
		var countryCode = $("[name='countryCode']").val();
		var email = $("[name='email']").val();
		var mobile = $("[name='mobile']").val();

		$("[name='email']").css("background", "white");
		$("[name='mobile']").css("background", "white");
		
		if (!/^([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\-|\_|\.]?)*[a-zA-Z0-9]+\.[a-zA-Z]{2,10}$/.test(email)) {
			$("[name='email']").css("background", "#ffff80");
			$("[name='email']").focus();
			window.alert('请输入合法的Email地址！');
		} else if (!/^[0-9]{8,11}$/.test(mobile)) {
			$("[name='mobile']").css("background", "#ffff80");
			$("[name='mobile']").focus();
			window.alert('请输入8-11位的手机号！');
		} else {
			$(this).css('background', 'gray');
			
			$.jsonRPC.request('sessionService.sendSms4ForgotPassword', {
				params : [email, mobile, countryCode],
				success : function(result) {
					if (result) {
						cd = 300;
						window.setTimeout("countDown();", 1000);
						window.alert("短信验证码已发送，5分钟内有效");
					}
				},
				error : function(e) {
					cd = 1;
					window.setTimeout("countDown();", 1000);
					window.alert(e.error.msg);
				}
			});
		}
	});
	
	
	$("#btn_reset_password").click(function(){
		var countryCode = $("[name='countryCode']").val();
		var email = $("[name='email']").val();
		var mobile = $("[name='mobile']").val();
		var smsCode = $("[name='smsCode']").val();
		var password = $("[name='password']").val();
		var confirmPassword = $("[name='confirmPassword']").val();
		var paymentPassword = $("[name='paymentPassword']").val();
		var confirmPaymentPassword = $("[name='confirmPaymentPassword']").val();

		$("[name='email']").css("background", "white");
		$("[name='mobile']").css("background", "white");
		$("[name='smsCode']").css("background", "white");
		$("[name='password']").css("background", "white");
		$("[name='confirmPassword']").css("background", "white");
		$("[name='paymentPassword']").css("background", "white");
		$("[name='confirmPaymentPassword']").css("background", "white");
		
		if (!/^([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\-|\_|\.]?)*[a-zA-Z0-9]+\.[a-zA-Z]{2,10}$/.test(email)) {
			$("[name='email']").css("background", "#ffff80");
			$("[name='email']").focus();
			window.alert('请输入合法的Email地址！');
		} else if (!/^[0-9]{8,11}$/.test(mobile)) {
			$("[name='mobile']").css("background", "#ffff80");
			$("[name='mobile']").focus();
			window.alert('请输入8-11位的手机号！');
		} else if (!/^[0-9]{6}$/.test(smsCode)) {
			$("[name='smsCode']").css("background", "#ffff80");
			$("[name='smsCode']").focus();
			window.alert('请输入6位的手机验证码！');
		} else if (password.length < 6) {$("[name='confirmPassword']").focus();
			$("[name='password']").css("background", "#ffff80");
			$("[name='password']").focus();
			window.alert('登录密码不能少于6位！');
		} else if (confirmPassword != password) {
			$("[name='confirmPassword']").css("background", "#ffff80");
			$("[name='confirmPassword']").focus();
			window.alert('确认登录密码不一致！');
		} else if (paymentPassword.length < 6) {
			$("[name='paymentPassword']").css("background", "#ffff80");
			$("[name='paymentPassword']").focus();
			window.alert('支付密码不能少于6位！');
		} else if (confirmPaymentPassword != paymentPassword) {
			$("[name='confirmPaymentPassword']").css("background", "#ffff80");
			$("[name='confirmPaymentPassword']").focus();
			window.alert('确认支付密码不一致！');
		} else if (paymentPassword == password) {
			$("[name='paymentPassword']").css("background", "#ffff80");
			$("[name='password']").css("background", "#ffff80");
			$("[name='paymentPassword']").focus();
			window.alert('支付密码不能与登录密码相同！');
		} else {
			$.jsonRPC.request('sessionService.forgotPassword', {
				params : [email, mobile, smsCode, password, countryCode, paymentPassword],
				success : function(result) {
					if (result) {
						//重设成功，自动登录
						$.jsonRPC.request('sessionService.login', {
							params : [email, password, countryCode],
							success : function(result) {
								if (result) {
									window.open('start_user.html', '_self');
								}
							}
						});
					}
				}
			});
		}
	});
});

function countDown() {
	cd --;
	if (cd <= 0) {
		$("#btn_send_sms").css('background', '#90ab40');
		$("#btn_send_sms").html('发送验证码');
	} else {
		window.setTimeout("countDown();", 950);
		
		$("#btn_send_sms").html(cd + '秒内有效..');
	}
}