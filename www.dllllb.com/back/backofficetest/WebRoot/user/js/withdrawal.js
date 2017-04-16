const LANG_SUBMIT = '提交';
const LANG_SUBMITTING = '提交中...';
var submitConfirm = true;
$(document).ready(function() {
	getRate();
	$("#btn_submit").text(LANG_SUBMIT);
	$("#btn_submit").removeAttr("disabled");
	$("#btn_submit").click(function() {
		submitForm();
	});
	loadBankAccounts();
	refreshList();
});

function refreshList() {
	$.jsonRPC.request("userAccountService.getBalanceList",{
		params:[],
		success:function(result){
			$("tbody#balance_list").html('');
			
			for (var i in result.list) {
				var item = result.list[i];
				var type = item.currencyType;
					
				$("tbody#balance_list").append('<tr>'
			        + '	<td class="sample-currency">'+type+'</td>'
			        + '	<td>' + toDate(item.updatedTime.time) + '</td>'
			        + '	<td class="amount bold available">'+item.amountAvailable.toFixed(2)+' '+type+'</td>'
			        + '	<td class="amount">'+item.amountFrozen.toFixed(2)+' '+type+'</td>'
			        + '	<td class="amount"><span class="pending"></span>  '+type+'</span></td>'
			        + '</tr>');
					
				$.jsonRPC.request("userAccountService.getWithdrawalsSum",{
					params:[type],
					success:function(sum){
						$('tr .pending').html(sum.toFixed(2));
					}
				});
			}
			
		}
	});
}
//获取汇率
function getRate(){
	$.jsonRPC.request('userWithdrawalService.getRate', {
	    params : [],
	    success : function(result) {
	    	$(".rate").text(result);	
	    }
	});
}
function submitForm() {
	
	//get
	var amount = parseFloat($("input[name='amount']").val());
	var bankAccount = $("select[name='bankAccount']").val();
	
	//clean
	$("input[name='amount']").css("background", "white");
	$("select[name='bankAccount']").css("background", "white");
	//check
	if (amount <= 0||$("input[name='amount']").val()=="") {
		$("input[name='amount']").css("background", "#ffff80");
		$("input[name='amount']").focus();
	} else if (amount > parseFloat($('.available').text())) {
		$("input[name='amount']").css("background", "#ffff80");
		$("input[name='amount']").focus();
	} else if (bankAccount == "") {
		$("select[name='bankAccount']").css("background", "#ffff80");
		$("select[name='bankAccount']").focus();
	} else {
		//start
		$.get("_popup_withdrawal_confirm.html", function(data) {
			$("#popup_container").html(data);
				$(".amountUSD").text(amount.toFixed(2));
				$(".rateConfirm").text("1 USD = "+$(".rate").text()+" CNY");
				$(".amountCNY").text((amount*parseFloat($(".rate").text())).toFixed(2));
				$(".bankAccountConfirm").text(bankAccount.substr(bankAccount.indexOf(":") + 1));
				$(".popup_dialog").fadeIn(200);
		});
		
		
		}
	
}
function doPopup(){
		var userMemo = $("textarea[name='userMemo']").val();
		var amount = parseFloat($(".amountUSD").text());
		var bankAccount = $("select[name='bankAccount']").val().split(":")[0];
		var payPassword = $(".payPassword").val();
		if(payPassword == "" || payPassword ==null){
			alert("请输入支付密码！");
		}else {
		if(submitConfirm){
			
			submitConfirm = false;
			$(".submitConfirm").attr("href","javascript:void(0);");
			$.jsonRPC.request("userWithdrawalService.add",{
				params:['USD',amount, userMemo, bankAccount,payPassword],
				success:function(result){
				if (result) {
					window.alert("提交成功");
					window.open("withdrawal_list.html", "_self");
				} else {
					window.alert("提交失败!");
				}
				//end
				submitConfirm = true;
				$(".submitConfirm").attr("href","javascript:doPopup();");
				
			}, error : function(e) {
			    window.alert(e.error.msg);
			    submitConfirm = true;
				$(".submitConfirm").attr("href","javascript:doPopup();");
			}
			});
		}
		}
}
function closePopup() {
		$(".popup_dialog").fadeOut(200);
	}
function selectCurrencyType(currencyType) {
	var _available = parseFloat($("."+currencyType+'_available').text());
	
	$("input[name='amount']").val(_available);
}

function loadBankAccounts() {
	$.jsonRPC.request("userAccountService.getBankAccountList",{
		params:[],
		success:function(result){
			if (result != null) {
				var html = '<option value="">- 我的银行账户 -</option>';
				for (var i in result.list) {
					var item = result.list[i];
					if(item.state=="AUDITED"){					
					      html += '<option value="'+ item.id +':' + item.bankName + ' - ' + item.accountNo + ' - ' + item.accountName +'">('+item.currencyType+') ' + ': '+ item.accountNo + ' ' + item.accountName +'</option>';
				    }else{
				    	  html += '<optgroup label="('+item.currencyType+'): '+ item.accountNo + ' ' + item.accountName +'   账户未通过审核，不能出金"></optgroup>';
				    }
				}
				
				$("select[name='bankAccount']").html(html);
			}
		}
	});
	
}

