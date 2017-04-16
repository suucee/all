var email = '';
var timerid = -1;
$(function(){
	$(".btn_send_code").click(function () {
		email = $("#email").val();
		if (!/^([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\-|\_|\.]?)*[a-zA-Z0-9]+\.[a-zA-Z]{2,10}$/.test(email)) {
			layer.tips("请输入正确的电子邮箱地址！","#email",{time:2000,tips:[2,'#56a787']});
			return;
		}else{
			//发送验证码到邮箱
			reSend();
		}
	});
	
	$(".btn_check").click(function () {
		var code = $("#code").val();
		if(code == ""){
			layer.tips("请输入正确的邮箱验证码！","#code",{time:2000,tips:[2,'#56a787']});
			return;
		}
		bindEmail(code);
	});
	
	$(".auto-complete-email").focus(function(){
		//邮箱自动补充功能
		$(".auto-complete-email").completer({
			separator: "@",
			source: ["fxideal.com","qq.com","163.com","sina.com","126.com","sogou.com",
			         "sohu.com","56.com","msn.com","gmail.com"]
		});
	});
	
});

function toFillCode () {
	$(".processor_1").css({"background":"rgba(23,154,23,0.5)"});
	$(".processor_2").css({"background":"rgba(23,154,23,1)"});
	$(".operation_1").hide(300,function () {
		$("#email_2").html(email);
		$(".operation_2").show(300);
	});
}


function toSuccess () {
	$(".processor_2").css({"background":"rgba(23,154,23,0.5)"});
	$(".processor_3").css({"background":"rgba(23,154,23,1)"});
	$(".operation_2").hide(300,function () {
		$("#email_3").html(email);
		$(".operation_3").show(300);
	});
}

function reSend() {
	$.jsonRPC.request("session2Service.sendEmailRegister",{
		params:[email],
		success:function(result){
			if(result){
				layer.msg("验证码发送成功",{time:1000,icon:1},function(){
					cannotClick();
					window.setTimeout(canClick, 60*2*1000);//120秒后可以重新发送
					toFillCode ();
				});
			}
		},
		error:function(result){
			layer.msg(result.error.msg,{time:2000,icon:2});
		}
	});
}

function canClick(){
	stopCalc();
	$(".times_tip").hide(200,function(){
		$(".resend").show(500);
	});
	
}
function cannotClick(){
	$(".resend").hide(300,function(){
		$(".times_tip").show(200,function(){
			startCalc();
		});
	});
}


function bindEmail(code){
	$.jsonRPC.request("session2Service.bindEmail",{
		params:[0, email, code],
		success:function(result){
			if(result){
				layer.msg("邮箱绑定成功",{time:1000,icon:1},function(){
					toSuccess ();
				});
			}
		},
		error:function(result){
			layer.msg(result.error.msg,{time:2000,icon:2});
		}
	});
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
