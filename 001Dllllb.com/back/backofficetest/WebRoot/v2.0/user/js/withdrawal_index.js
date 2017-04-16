const LANG_SUBMIT = '提交>>';
const LANG_SUBMITTING = '提交中...';
var submitConfirm = true;
var minWithdrawlMoney = -1;


$(document).ready(function() {
	checkState();
	
	getMaxWithdrawlMoney();
	checkCanWithdrawal();
	
	
	//如果没有支付密码，先前往设置支付密码
	$.jsonRPC.request('userService.operationPwd', {
		params : [],
		success : function(result) {
			if (result) {	//true
				$("#setpaypwdtip").html("");
				$("#setpaypwdtip").hide();
				$("#amount-input").removeAttr("disabled");
				$("#calc").show();
			} else {	//false
				$("#amount-input").attr("disabled","disabled");
				$("#setpaypwdtip").html("温馨提示：对不起，您还没有设置支付密码，不能出金哟。<a href='profile_changepassword.html' style='text-decoration: underline;'>前往设置支付密码</a>");
				$("#calc").hide();
				$("#setpaypwdtip").show();
			}
		}
	});
	
	$(".transfer_mt4List").change(function(){
		var login = $(this).val();
		if(login == ""){
			$(".balance_amount").html("");
		}else {
			$.jsonRPC.request("userAccountService.getMt4Balance", {
				params:[login],
				success:function(result){
					$(".balance_amount").html("可用余额：<font color='red' class='available'>"+result.toMoney(2)+"</font>&nbsp;&nbsp;USD");
				}
			});
		}
	});
	refreshAccountList();
	
	
	$("#startDate").datetimepicker({
		timeFormat : "HH:mm:ss",
		dateFormat : "yy-mm-dd"
	});
	$("#endDate").datetimepicker({
		timeFormat : "HH:mm:ss",
		dateFormat : "yy-mm-dd"
	});
	
	$("#go").click(function() {
		getHistory(1);
	});
	$("#startDate").change(function() {
		getHistory(1);
	});
	$("#endDate").change(function() {
		getHistory(1);
	});
	$("#state").change(function() {
		getHistory(1);
	});
	getHistory(1);
	
	
	
	getRate();
	$("#btn_submit").text(LANG_SUBMIT);
	$("#btn_submit").removeAttr("disabled");
	$("#btn_submit").click(function() {
		submitForm();
	});
	loadBankAccounts();
	refreshList();
});



function checkState(){
	//检查用户状态
	$.jsonRPC.request("userService.state",{
		params:[],
		success:function(state){
			if(state != "VERIFIED"){
				$("#btn_submit").removeAttr("btn-b").addClass("btn-e");
				$("#btn_submit").attr("disabled","disabled");
				$("#btn_submit").css("cursor","default");
				layer.confirm("您的资料还未通过审核,暂不能提交出金申请哦.",{btn:["前往完善资料？","我知道了"]},
					function(){
						location.href = './../user/profile_index.html';
						return;
					},
					function(){
						return;
					}
				);
				return;
			}
		}
	});
}



function refreshAccountList() {
	$.jsonRPC.request("userMT4Service.getList", {
		params:[],
		success:function(result) {
			var html = '';
			if(result!=null) {
				//生成MT4账号数组
				var mt4Login="<option value='0'>网站账户余额</option>";
				arry=[];
				for (var i in result.list) {
					var item = result.list[i];
					arry.push(item.login);
					mt4Login+="<option value='"+arry[i]+"'>MT4账号："+arry[i]+"</option>";
				}
				optionStr = mt4Login;
				$(".transfer_mt4List").html(mt4Login);
				$(".transfer_mt4List").val(0);
				$(".transfer_mt4List").change();
			}
		} 
	});
}





function refreshList() {
	$.jsonRPC.request("userAccountService.getBalanceList",{
		params:[],
		success:function(result){
			for (var i in result.list) {
				var item = result.list[i];
				$(".amountAvailable").html(item.amountAvailable.toMoney(2));
				$(".amountFrozen").html(item.amountFrozen.toMoney(2));
			}
			
		}
	});
}

var rate = null;
//获取汇率
function getRate(){
	$.jsonRPC.request('userWithdrawalService.getRate', {
	    params : [],
	    success : function(result) {
	    	rate = result;
	    	$(".rate").text(result);
	    }
	});
}

function submitForm() {
	
	var fromLogin = $(".transfer_mt4List").val();
	
	
	//get
	var amount = parseFloat($("input[name='amount']").val()).toFixed(2);
	var bankAccount = $("select[name='bankAccount']").val();
	var payPassword = $("#payPassword").val();
	var userMemo = $(".userMemo").val();
	
	//clean
	//check
	if (amount <= 0||$("input[name='amount']").val()=="" || isNaN(amount)) {
		layer.tips("金额不正确 .","input[name='amount']",{time:2000,tips:[2,'#56a787']});
		$("input[name='amount']").focus();
	} else if (amount > rmoney($('.available').text())) {
		layer.tips("对不起，您的金额不足 .","input[name='amount']",{time:2000,tips:[2,'#56a787']});
		$("input[name='amount']").focus();
	} else if (bankAccount == "") {
		layer.tips("请选择一个银行账户","select[name='bankAccount']",{time:2000,tips:[2,'#56a787']});
		$("select[name='bankAccount']").focus();
	}else if(payPassword == "" || payPassword ==null){
		layer.tips("支付密码不能为空","input[name='payPassword']",{time:2000,tips:[2,'#56a787']});
		$("input[name='payPassword']").focus();
	}else if(minWithdrawlMoney != -1 && amount<minWithdrawlMoney){
		layer.tips("对不起，最少出金金额为："+minWithdrawlMoney.toMoney()+"。","input[name='amount']",{time:2000,tips:[2,'#56a787']});
		$("input[name='amount']").focus();
	}
	else {
		var tip = "";
		if(fromLogin == 0){
			tip = "您确定要从<span class='red'>网页账户</span>出金<span class='red'> "+amount+"(USD) </span>到</br><span class='red'> "+bankAccount.split(":")[1]+" </span>吗？";
		}else{
			tip = "您确定要从<span class='red'>"+$(".transfer_mt4List :selected").html()+"</span>出金<span class='red'> "+amount+"(USD) </span>到</br><span class='red'> "+bankAccount.split(":")[1]+" </span>吗？";
		}
		
		//询问框
		layer.confirm(tip, {
		  btn: ['确认出金','放弃出金'] //按钮
		}, function(){
			if(submitConfirm){
				submitConfirm = false;
				$("#btn_submit").attr("disabled","disabled");
				$("#btn_submit").html(LANG_SUBMITTING);
				bankAccount = bankAccount.split(":")[0];
				
				$.jsonRPC.request("user2WithdrawalService.addWithMt4",{
					params:['USD',amount, userMemo, bankAccount,payPassword,rate, fromLogin],
					success:function(result){
						if (result) {
							layer.msg("出金申请提交成功.",{time:1000,icon:1});
						} else {
							layer.msg("出金申请提交失败.",{time:2000,icon:2});
						}
						getHistory(1);
						submitConfirm = true;
						$("#btn_submit").html(LANG_SUBMIT);
						$("#btn_submit").removeAttr("disabled");
						$("#payPassword").val("");
					},
					error : function(e) {
						layer.msg(e.error.msg,{time:2000,icon:2});
					    submitConfirm = true;
					    $("#btn_submit").html(LANG_SUBMIT);
					    $("#btn_submit").removeAttr("disabled","disabled");
					    $("#payPassword").val("");
					}
				});
			}
		}, function(){
		$("#payPassword").val("");
		  return;
		});
	}
	
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
				    	  html += '<optgroup style="color:#d2d2d2;" label="('+item.currencyType+'): '+ item.accountNo + ' ' + item.accountName +'   账户未通过审核，不能出金"></optgroup>';
				    }
				}
				
				$("select[name='bankAccount']").html(html);
			}
		}
	});
	
}


function amountchange(){
	$("#calc").html("");
	var moneyUSD = 0.0;
	var moneyUSDStr = $("input[name='amount']").val().trim();
	if(moneyUSDStr!="" && !isNaN(moneyUSDStr))
	{
		moneyUSD = parseFloat(moneyUSDStr).toFixed(2);
		$("input[name='amount']").val(moneyUSD);
		var moneyCNY = rate * moneyUSD;
		var html = moneyUSD+ " USD = "+moneyCNY.toFixed(2)+" CNY";
		$("#calc").html(html);
		$("#calc").show();
	}
	else{
		$("#calc").html("请您输入正确的出金金额。");
		$("#calc").show();
	}
}



var _pageNo = 1;
function getHistory(pageNo) {
	$.jsonRPC.request("user2WithdrawalService.getPageByTimeAndState", {
		params : [ pageNo, 10, "javascript:getHistory(??);",
				$("#startDate").val(), $("#endDate").val(), $("#state").val()],
		success : function(result) {
			if (result.list.list != null && result.list.list.length !=0 ) {
				_pageNo = pageNo;
				
				var html = '';
				for (var i in result.list.list) {
					var item = result.list.list[i];
					var label_class = "";
					var label_name = "";
					var show_cancel = false;
					switch (item.state) {
					case 'WAITING':
						label_class = 'label-pending';
						label_name = '待审核';
						show_cancel = true;
						break;
					case 'PENDING_SUPERVISOR':
						label_class = 'label-pending';
						label_name = '审核中';
						break;
					case 'AUDITED':
						label_class = 'label-pending';
						label_name = '待汇款';
						break;
					case 'REMITTED':
						label_class = 'label-success';
						label_name = '已汇出';
						break;
					case 'REJECTED':
						label_class = 'label-cancel';
						label_name = '已驳回';
						show_cancel = true;
						break;
					case 'BACK':
						label_class = 'label-cancel';
						label_name = '银行退回';
						show_cancel = true;
						break;
					case 'CANCELED':
						label_class = 'label-pending';
						label_name = '用户取消';
						break;
					}

					html += '<tr>'
						+ '<td><a style="text-decoration: underline;" href="javascript:parent.openNewWindow('+"'./../user/withdrawal_detail.html?id="+item.id+"'"+',updateList)">#'+item.id+'</a></td>' 
						+ '<td class="amount bold">'+item.amount.toFixed(2)+' '+item.currency+'</td>'
						+ '<td><span class="label '+label_class+'">'+label_name+'</span></td>'
						+ '<td>'+toDate(item.creatTime.time)+'</td>'
						+ '<td>'+(item.auditedTime == null ? ' - ' : toDate(item.auditedTime.time))+'</td>'
						+ '<td>'
						+ (show_cancel ? '<a href="javascript:cancelRecord(\''+item.id+'\');"><span class="btn btn-b" alt="取消出金" title="取消出金">取消出金</span></a>&nbsp;&nbsp;'+
										 '<a onclick="parent.openNewWindow('+"'./../user/withdrawal_detail.html?id="+item.id+"'"+',updateList)" class="btn btn-primary" alt="查看详情" title="查看详情">查看详情</a>' :
										 '<a onclick="parent.openNewWindow('+"'./../user/withdrawal_detail.html?id="+item.id+"'"+',updateList)" class="btn btn-primary" alt="查看详情" title="查看详情">查看详情</a>');
						+'</td>'
						+ '</tr>';
			    }
			    
				if(result.buttons!=null&&result.buttons!=""){
			    	html += '<tr><td colspan="10"><div class="pagelist">'+result.buttons+'</td></tr>';
			    }
				if (html=="") {
				     html += '<tr><td colspan="10" class="bold">对不起，暂未检索到相关记录</td></tr>';
				}
				$("tbody#result_list").html(html);
			}
			else{
				$("tbody#result_list").html("<tr><td class='bold' colspan=6>对不起，没有搜索到相关的记录。</td></tr>");
			}
		}
	});
}


var flag = true;
function cancelRecord(id) {
	
	//询问框
	layer.confirm('您确定要 <span class="red">取消单号为 #' + id + '</span>的这项出金吗？', {
	  btn: ['确认取消','退出'] //按钮
	}, 
	function(){//确认取消
		if (flag) {
			flag = false;
			$.jsonRPC.request("userWithdrawalService.cancel", {
				params : [ id ],
				success : function(result) {
					if (result) {
						layer.msg("取消成功。", {time:1000});
						getHistory(_pageNo);
					} else {
						layer.msg("抱歉，取消出金单为 #" + id + "出错了，建议您稍候再试。", {time:2000});
						getHistory(_pageNo);
					}
//					layer.closeAll('dialog');
					flag = true;
				}
			});
		}
	},
	function(){//退出
	});
}

function updateList(){
}


/**
 * 检查是否可以出金
 */
function checkCanWithdrawal(){
	$.jsonRPC.request("userService.state",{
		params:[],
		success:function(date){
			switch (date) {
			case "UNVERIFIED":
			case "AUDITING":
			case "REJECTED":
				$("#amount-input").attr("disabled","disabled");
				$("#calc").html("温馨提示：对不起，您的资料还未通过审核，暂时不能出金哟。<a href='profile_index.html' style='text-decoration: underline;'>前往完善资料？</a>");
				$("#calc").show();
				break;
			case "VERIFIED":
				$("#calc").html("");
				$("#calc").hide();
				$("#amount-input").removeAttr("disabled");
				break;
			default:
				$("#amount-input").attr("disabled","disabled");
				$("#calc").html("温馨提示：对不起，您的资料还未通过审核，暂时不能出金哟。<a href='profile_index.html' style='text-decoration: underline;'>前往完善资料？</a>");
				$("#calc").show();
				break;
			}
		}
	});
}



/**
 * 获取用户最小出金金额
 */
function getMaxWithdrawlMoney(){
	$.jsonRPC.request("adminSettingService.getList",{
		params:[],
		success:function(result){
			minWithdrawlMoney = result.list[8].doubleValue;
			if(minWithdrawlMoney != -1){
				$(".withdrawl_min_tip").html(minWithdrawlMoney.toMoney());
				$(".withdrawl_tip").show();
			}else{
				$(".withdrawl_tip").hide();
			}
		}
	});
}
