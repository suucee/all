var email = null;
var countryCode = null;
var mobile = null;
var timerid = -1;
var type="mobile";//发送验证码的方式：手机和邮箱


$(function(){
	
	//验证方式
	$(".btn_mobile").click(function () {
		type="mobile";
		to1(type);
	});
	$(".btn_email").click(function () {
		type="email";
		to1(type);
	});
	
	//发送验证码
	$(".btn_send").click(function () {
		reSend();
	});
	
	//发送短信
	$(".btn_send_sms").click(function () {
		reSendSms();
	});
	
	//发送邮件
	$(".btn_send_email").click(function () {
		reSendEmail();
	});
	
	//校验验证码
	$(".btn_check").click(function () {
		checkCode();
	});
	
	//设置密码
	$(".btn_pass").click(function () {
		resetPassword();
	});
	
});

function resetPassword(){
	var password = $("#password").val();
	if(password.length < 6){
		layer.tips("密码格式不正确（至少6位）","#password",{time:2000,tips:[2,'#56a787']});
		return;
	}
	var paymentpassword = $("#paymentpassword").val();
	if(paymentpassword.length < 6){
		layer.tips("支付密码格式不正确（至少6位）","#paymentpassword",{time:2000,tips:[2,'#56a787']});
		return;
	}
	
	var confirm_password = $("#confirm_password").val();
	if(password != confirm_password){
		layer.tips("确认密码与密码不一致","#confirm_password",{time:2000,tips:[2,'#56a787']});
		return;
	}
	var confirm_paymentpassword = $("#confirm_paymentpassword").val();
	if(paymentpassword != confirm_paymentpassword){
		layer.tips("确认支付密码与支付密码不一致","#confirm_paymentpassword",{time:2000,tips:[2,'#56a787']});
		return;
	}
	if(paymentpassword == password){
		layer.tips("支付密码不能与登录密码一致哟","#paymentpassword",{time:2000,tips:[2,'#56a787']});
		return;
	}
	
	if(email != null){
		account = email;
	}else if(mobile){
		account = mobile;
	}
	$.jsonRPC.request("session2Service.resetPassword",{
		params:[account,countryCode,password,paymentpassword],
		success:function(result){
			if(result){
				layer.msg("恭喜您，解除账户锁定成功。",{time:1000,icon:1},function(){
					to3();
				});
			}
		},
		error:function(result){
			layer.msg(result.error.msg,{time:2000,icon:2});
		}
	});
}




function checkCode(){
	var code = $("#code").val();
	if(code==""){
		layer.tips("请输入验证码","#code",{time:2000,tips:[3,'#56a787']});
		return;
	}
	$.jsonRPC.request("session2Service.checkCode4Unlock",{
		params:[email,mobile,countryCode,code],
		success:function(result){
			if(result){
				layer.msg("验证成功。",{time:1000,icon:1},function(){
					$(".operation_1").hide();
					to3();
				});
			}else{
				layer.msg("验证码不正确",{time:2000,icon:2});
				return;
			}
		},
		error:function(result){
			layer.msg(result.error.msg,{time:2000,icon:2});
		}
	});
}




function reSend() {
	
	
	if(type=="mobile"){
		reSendSms();
	}else{
		reSendEmail();
	}
}

function reSendEmail() {
	email = $("#email").val();
	if (!/^([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\-|\_|\.]?)*[a-zA-Z0-9]+\.[a-zA-Z]{2,10}$/.test(email)){
		layer.tips("请输入正确的电子邮箱地址.","#email",{time:2000,tips:[3,'#56a787']});
		return;
	}
	$.jsonRPC.request("session2Service.sendEmail4ForgotPassword",{
		params:[email],
		success:function(result){
			if(result){
				layer.msg("验证码发送成功",{time:1000,icon:1},function(){
					cannotClick();
					window.setTimeout(canClick, 60*2*1000);//120秒后可以重新发送
				});
			}
		},
		error:function(result){
			layer.msg(result.error.msg,{time:2000,icon:2});
		}
	});
}

function reSendSms() {
	countryCode = $("#countryCode").val();
	mobile = $("#mobile").val();
	if (!/^[0-9]{8,11}$/.test(mobile)){
		layer.tips("请输入8-11位的手机号.","#mobile",{time:2000,tips:[3,'#56a787']});
		return;
	}
	$.jsonRPC.request("session2Service.sendSms4ForgotPassword",{
		params:[mobile,countryCode],
		success:function(result){
			if(result){
				layer.msg("验证码发送成功",{time:1000,icon:1},function(){
					cannotClick();
					window.setTimeout(canClick, 60*2*1000);//120秒后可以重新发送
				});
			}
		},
		error:function(result){
			layer.msg(result.error.msg,{time:2000,icon:2});
		}
	});
}
function to0(){
	$(".processor_0").css({"background":"rgba(23,154,23,1)"});
	$(".processor_1").css({"background":"gainsboro"});
	$(".operation_1").hide();
	$(".operation_0").show(300);
}

function to1(type){
	email = null;
	countryCode = null;
	mobile = null;
	$(".processor_0").css({"background":"rgba(23,154,23,0.5)"});
	$(".processor_1").css({"background":"rgba(23,154,23,1)"});
	$(".operation_0").hide();
	$(".operation_1").show(300);
	if(type=="mobile"){
		$(".mobile_div").show();
		$(".email_div").hide();
	}else{
		$(".mobile_div").hide();
		$(".email_div").show(function(){
			//邮箱自动补充功能
			$(".auto-complete-email").completer({
				separator: "@",
				source: ["fxideal.com","qq.com","163.com","sina.com","126.com","sogou.com",
				         "sohu.com","56.com","msn.com","gmail.com"]
			});
		});
	}
}
function to2(pwd_type){
	if(pwd_type == "ADMIN"){
		$(".pwd_type").html("操作");
	}else if(pwd_type == "USER"){
		$(".pwd_type").html("支付");
	}
	$(".processor_1").css({"background":"rgba(23,154,23,0.5)"});
	$(".processor_2").css({"background":"rgba(23,154,23,1)"});
	$(".operation_1").hide();
	$(".operation_2").show(300);
}
function to3(){
	$(".processor_2").css({"background":"rgba(23,154,23,0.5)"});
	$(".processor_3").css({"background":"rgba(23,154,23,1)"});
	$(".operation_2").hide();
	$(".operation_3").show(300);
}



function canClick(){
	stopCalc();
	$(".times_tip").hide(200,function(){
		$(".resend").show(500);
	});
	$(".btn_send").css('background', '');
	$(".btn_send").removeAttr("disabled");
	
}
function cannotClick(){
	$(".times_tip").show(200,function(){
		startCalc();
	});
	$(".btn_send").css('background', 'gray');
	$(".btn_send").attr("disabled","disabled");
}


function startCalc(){
	timerid = window.setInterval(function(){
		$("#times").html(parseInt($("#times").html()) - 1);
	}, 1000);
}

function stopCalc(){
	if(timerid != -1){
		window.clearInterval(timerid);
		$("#times").html(120);
	}
}
