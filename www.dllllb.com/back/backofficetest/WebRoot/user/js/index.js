/**
 * 
 */
$(document).ready(function() {
	getCodeAndLink();
	name();
});

function getCodeAndLink() {
	$.jsonRPC.request("userRankService.getReferralCode", {
		params : [],
		success : function(result) {
			if (result != null && result != '') {
				url = location.href;
				index1 = url.indexOf("start_user.html");
				referralLink = url.substring(0, index1) + "register.html?code=" + result;

				$(".referralCode").html(result);
				$(".referralLink").html('<a href="'+referralLink+'" target="_blank">' + referralLink + '</a>');
				$(".mycodeandlink").show();
			}
		}
	});
}

function name() {
	$(".body_div").html("");
	var stateHTML = "";
	var emailHTML = "";
	var moblieHTML = "";
	var PWDHTML = "";
	
	$.jsonRPC.request("userService.state", {
		params : [],
		success : function(result) {
			switch (result) {
			case 'UNVERIFIED':
				stateHTML += '<h2 style="color:#c00">请完善您的资料以激活账户.. <button class="btn btn-primary" onclick="javascript:window.open(\'start_profile.html\', \'_self\');">完善..</button></h2>';
				break;
			case 'AUDITING':
				stateHTML += '<h2 class="gray">您的资料正在审核中...</h2>';
				break;
			case 'VERIFIED':
				stateHTML += '<h2 class="green">认证已通过</h2>';
				break;
			case 'REJECTED':
				stateHTML += '<h2 style="color:#c00">您的资料被驳回..  <button class="btn btn-primary" onclick="javascript:window.open(\'start_profile.html\', \'_self\');">处理..</button></h2>';
				break;
			}
			
			$("#first_state").html(stateHTML);
		}
	});
	
	// 验证邮件是否验证
	$.jsonRPC.request("userService.checkEmail", {
		params : [],
		success : function(data) {
			if (data) {
				emailHTML += '<div class="warning"><h4 class="h4" >您的Email地址未验证!<a href="javascript:offEmail()" class="btn btns right btn-primary">点击验证&gt;&gt;</a></h4></div><div class="clear"></div>';
				//$(".body_div").append(emailHTML);
			}
		}
	});
	
	$.jsonRPC.request("userService.checkOperationPwd", {
		params : [],
		success : function(data) {
			if (data) {
				
			} else {
				PWDHTML += '<div class="warning"><h4 class="h4">请设置您的支付密码! <a href="start_payment_password.html" style="margin-left:20px;" class="btn btns right  btn-primary">设置密码&gt;&gt;</a></h4></div><div class="clear"></div>';
					$(".body_div").append(PWDHTML);
			}
		}
	});
}
// 发送邮件
function offEmail() {
	$.jsonRPC.request("userService.offEmail", {
		params : [],
		success : function(result) {
			if (result) {
				window.alert("邮件已成功发送!");
				name();
			} else {
				window.alert("邮件发送失败，请检查你的邮箱是否正确!");
			}
		},
		error : function(e) {
			window.alert("邮件发送失败，请检查你的邮箱是否正确!");
		}
	});
}
function okPhone() {
	var num = $("#inputm").val();
	window.alert(num);
	if (num == null || num == "") {
		return false;
	}
	$.jsonRPC.request("userService.okPhone", {
		params : [ num ],
		success : function(data) {
			if (data) {
				window.alert("手机验证成功!")
				name();
			} else {
				window.alert("手机验证失败!")
			}
		}
	});
}
// 发送短信
function offPhone() {
	$(".inputcode").fadeIn(300);
	$.jsonRPC.request("userService.offPhone", {
		params : [],
		success : function(data) {
			if (data == 3) {
				window.alert("短信已发送，10分钟之内有效!");
			} else {
				if (data == 0) {
					window.alert("短信已成功发送!");
				} else if (data < 0) {
					window.alert("短息发送失败，请检查你的手机号码是否正确!")
				}
			}
		}
	});
}
