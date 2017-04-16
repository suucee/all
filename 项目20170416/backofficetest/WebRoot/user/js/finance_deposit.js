const LANG_SUBMIT = '提交';
const LANG_SUBMITTING = '提交中...';
$(document).ready(function() {
	$("#payDate").datetimepicker({
        timeFormat: "HH:mm:ss",
        dateFormat: "yy-mm-dd"
    });
	$(".sbt").click(function () {
		add();
	});
	
});

function doLookup() {
	var mobile = $("#mobile").val();
	$.jsonRPC.request('adminUserService.findByMobile', {
		params : [mobile],
		success : function(result) {
			if (result) {
				var html = "";
				
				html += '<span class="green">姓名：'+result.map._name+'</span><br />';
				html += '<span class="green">Email：'+result.map.email+'</span><br />';
				html += '<span class="green">可用余额：'+result.map._amountAvailable.toFixed(2)+' USD</span>';
				
				$('.lookup_result').html(html);
			} else {
				$('.lookup_result').html('<span class="red">查询失败</span>');
			}
		},
		error : function(e) {
			window.alert(e.error.msg);
			$('.lookup_result').html('<span class="red">查询失败</span>');
		}
	})
}

//提交代为入金资料
function add() {
		var mobile=$("#mobile").val();
		var orderNum=$("#orderNum").val();
		var amount=$("#amount").val();
		var payDate=$("#payDate").val();
		var userComment=$("#userComment").val();
		var operationPassword=$("#operationPassword").val();
		
		//清除样式
		$("#email").css("background", "white");
		$("#amount").css("background", "white");
		$("#orderNum").css("background", "white");
		$("#payDate").css("background", "white");
		$("#operationPassword").css("background", "white");
		
		if (mobile==""){
			$("#mobile").css("background", "#ffff80");
			$("#mobile").focus();
		} else if (!(amount > 0)) {
			$("#amount").css("background", "#ffff80");
			$("#amount").focus();
		} else if (orderNum=="") {
			$("#orderNum").css("background", "#ffff80");
			$("#orderNum").focus();
		} else if (payDate=="") {
			$("#payDate").css("background", "#ffff80");
			$("#payDate").focus();
		} else if (operationPassword=="") {
			$("#operationPassword").css("background", "#ffff80");
			$("#operationPassword").focus();
		} else {
			$(".sbt").text(LANG_SUBMITTING);
			$(".sbt").attr("disabled", "disabled");
			$.jsonRPC.request("adminDepositService.add", {
				params : [mobile,orderNum,amount,payDate,userComment,operationPassword],
				success : function(result) { 
					alert(result);
					$(".sbt").text(LANG_SUBMIT);
					$(".sbt").removeAttr("disabled");
				}
			});
		}
	}
		
