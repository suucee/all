const LANG_SUBMIT = '继续&gt;&gt;';
const LANG_SUBMITTING = '提交中...';

var maxDepositMoney = -1;
var minDepositMoney = -1;

$(document).ready(function() {
	checkState();
	getMaxDepositMoney();
	
	$(".loginUserName").html(loginUser.tel+"+您的姓名。");
	$(".a_showDepositTipDiv").click(function(){
			$(".depositTipDiv").show(500);
	});
	$(".btn-hide").click(function(){
		$(".depositTipDiv").hide(500);
	});
	
	$("#startDate").datetimepicker({
		timeFormat : "HH:mm:ss",
		dateFormat : "yy-mm-dd"
	});
	$("#endDate").datetimepicker({
		timeFormat : "HH:mm:ss",
		dateFormat : "yy-mm-dd"
	});
	
	$("#startDate").change(function(){	getHistory(1);	});
	$("#endDate").change(function(){	getHistory(1);	});
	$("#state").change(function(){	getHistory(1);	});
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
	
	getRate();	
	
	refreshAccountList();
	
	
	$("#btn_continue").html(LANG_SUBMIT);
	$("#btn_continue").removeAttr("disabled");
	$("#btn_continue").click(function() {
		//get
		var toLogin = $(".transfer_mt4List").val();//mt4账号，如果==0，则为网页账户
		var amountStr = $("input[name='amount']").val();
		if (amountStr=="" || isNaN(amountStr))
		{
			layer.tips("请您输入正确的入金金额哟.","input[name='amount']",{time:2000,tips:[2,'#56a787']});
			return;
		}
		var amount = parseFloat($("input[name='amount']").val());
		$("input[name='amount']").val(amount.toFixed(2));
		amount = amount.toFixed(2);
		var realAmount = parseFloat($("input[name='amount']").val());
		
		if(maxDepositMoney!=-1 && minDepositMoney!=-1){
			if(realAmount >= maxDepositMoney){
				layer.tips("对不起，最多允许入金"+maxDepositMoney.toMoney()+"。","input[name='amount']",{time:2000,tips:[2,'#56a787']});
				return;
			}else if(realAmount < minDepositMoney){
				layer.tips("对不起，最少允许入金"+minDepositMoney.toMoney()+"。","input[name='amount']",{time:2000,tips:[2,'#56a787']});
				return;
			}
		}
		
		//根据汇率计算最小输入金额
		amountRate = parseFloat($(".rate").text());
		if (!isNaN(amount))
		{
			$("#btn_continue").html(LANG_SUBMITTING);
			$("#btn_continue").attr("disabled", "disabled");
			
			var time= new Date();
			var order_time=toDates(time);
			
			var paymentPassword = "";
			//询问框
			var tip = "";
			var inputHtml = '<br/><br/><i class="fa fa-unlock-alt margin-right10 font-red"></i>输入支付密码：<input type="password" placeholder="输入您的支付密码" class="popup_pwd" maxlength="30">';
			if(toLogin == 0){
				tip = "您确定要入金<span class='red'> "+amount+"(USD) </span>到<span class='red'>网页账户</span>吗？";
			}else{
				tip = "您确定要入金<span class='red'> "+amount+"(USD) </span>到<span class='red'>"+$(".transfer_mt4List :selected").html()+"</span>吗？<br/>注意，入金到MT4账户需要输入支付密码。";
				tip += inputHtml;
			}
			
			layer.open({
		        type: 1,
		        title:"账户入金",
		        skin: 'layui-layer-demo',
		        closeBtn: 0,
		        area: '400px, 400px',
		        shift: 2,
		        shadeClose: true,
		        content: '<div style="padding:20px;color:blank">'+tip+'</div>',
		        btn:['前往支付','放弃入金'],
		        yes: function(index, layero){
		        	paymentPassword = $(".popup_pwd").val();
		        	if(paymentPassword == undefined){paymentPassword = "";}
		        	layer.close(index);
		        	$.jsonRPC.request('user2DepositService.addWithMT4', {
					    params : [toLogin,amount,paymentPassword],
					    success : function(result) {
							if (result) {	//true
								window.location.href="../../zhifu2/B2CDinpay.jsp?order_amount="+(amount*amountRate).toFixed(2)+"&order_time="+order_time+"&order_no="+result;
							} else {	//false
								layer.msg("入金信息提交失败",{time:2000,icon:2});
							}
							$("#btn_continue").html(LANG_SUBMIT);
							$("#btn_continue").removeAttr("disabled");
						}, error : function(e) {
							layer.alert(e.error.msg,{icon:2},function(){
								layer.closeAll();
					        	$("#btn_continue").html(LANG_SUBMIT);
								$("#btn_continue").removeAttr("disabled");
							});
						}
					});
		        },
		        btn2:function(index, layero){
		        	layer.close(index);
		        	$("#btn_continue").html(LANG_SUBMIT);
					$("#btn_continue").removeAttr("disabled");
		        }
			});
		}
	});
	
	
	getHistory(1);
	$("#go").click(function(){
		getHistory(1);
	});
	
	
});	





function refreshAccountList() {
	$.jsonRPC.request("userMT4Service.getList", {
		params:[],
		success:function(result) {
			var html = '';
			if(result!=null) {
				//生成MT4账号数组
				var mt4Login="<option value='0'>网站账户余额(默认)</option>";
				
				arry=[];
				for (var i in result.list) {
					var item = result.list[i];
					arry.push(item.login);
					mt4Login+="<option value='"+arry[i]+"'>MT4账号："+arry[i]+"</option>";
				}
				
//				var mt4Login2="<option value='0'>网站账户余额(默认)</option>";
				$(".transfer_mt4List").html(mt4Login);
				$(".transfer_mt4List").val(0);
				$(".transfer_mt4List").change();
			}
		} 
	});
}







function checkState(){
	//检查用户状态
	$.jsonRPC.request("userService.state",{
		params:[],
		success:function(state){
			if(state != "VERIFIED"){
				$("#btn_continue").removeAttr("btn-b").addClass("btn-e");
				$("#btn_continue").attr("disabled","disabled");
				$("#btn_continue").css("cursor","default");
				
				
				layer.confirm("您的资料还未通过审核,暂不能提交入金申请哦.",{btn:["前往完善资料？","我知道了"]},
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






var rate = null;
//获取汇率
function getRate(){
	$.jsonRPC.request('userDepositService.getRate', {
	    params : [],
	    success : function(result) {
	    	rate = result;
	    	$(".rate").text(result);	
	    }
	});
}

function toDates(v) {
	var ret = new Date();
	ret.setTime(v);
	return ret.format("yyyy-MM-dd HH:mm:ss");
}

function amountchange(){
	$("#calc").html("");
	var moneyUSD = 0.0;
	var moneyUSDStr = $("input[name='amount']").val().trim();
	if(!isNaN(moneyUSDStr))
	{
		moneyUSD = parseFloat(moneyUSDStr).toFixed(2);
		$("input[name='amount']").val(moneyUSD);
		var moneyCNY = rate * moneyUSD;
		var html = moneyUSD+ " USD = "+moneyCNY.toFixed(2)+" CNY";
		$("#calc").html(html);
		$("#calc").show();
	}
	else{
		$("#calc").html("请您输入正确的入金金额。");
		$("#calc").show();
	}
}


/**
 * 获取历史记录
 * @param pageNo
 */
function getHistory(pageNo) {
	$.jsonRPC.request("user2DepositService.getPageByTimeAndState", {
		params : [ pageNo, 20, "javascript:getHistory(??);",
		           $("#startDate").val(), $("#endDate").val(), $("#state").val() ],
		success : function(result) {
			if (result != null) {
				if(result.list.list.length == 0){
					$("tbody#result_list").html("<tr><td class='bold' colspan=6>对不起，没有搜索到相关的记录。</td></tr>");
					return;
				}
				
				_pageNo = pageNo;
	
				var html = '';
				for ( var i in result.list.list) {
					var item = result.list.list[i];
					var label_class = "";
					var label_name = "";
					var show_remove = false;
					switch (item.state) {
					case 'ACCEPTED'://通过审核
					case 'DEPOSITED':
						label_class = 'label-success';
						label_name = '入金完成';
						show_remove = true;
						break;
					case 'PENDING_PAY':
						label_class = 'label-pending';
						label_name = '待付款';
						show_remove = true;
						break;
					case 'PENDING_AUDIT':
						label_class = 'label-pending';
						label_name = '审核中';
						show_remove = true;
						break;
					case 'PENDING_SUPERVISOR':
						label_class = 'label-pending';
						if(item.orderNum.indexOf("[D]") != -1){//订单号为D开头的都是代为入金
							label_name = '审核中(代为入金)';
						}else{
							label_name = '审核中(大额入金)';
						}
						show_remove = true;
						break;
					case 'REJECTED':
						label_class = 'label-important';
						label_name = '已驳回';
						show_remove = true;
						break;
					}
					
					
					html += '<tr><td>'
						+ item.id
						+ '</td>'
						+ '    <td><span class="label '
						+ label_class
						+ '">'
						+ label_name
						+ '</span></td>'
						+ '<td class="amount bold">'
						+ item.amount.toFixed(2)
						+ '</td>'
						+ '<td>'
						+ item.orderNum
						+ ' </td>'
						+ '    <td>'
						+ toDate(item.creatTime.time)
						+ '</td>'
						+ '    <td>'
						+ (item.paymentTime== null ? '--' : toDate(item.paymentTime.time))
						+ '</td>'
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
			
		}
	});
}

/**
 * 获取用户最大入金金额
 */
function getMaxDepositMoney(){
	$.jsonRPC.request("adminSettingService.getList",{
		params:[],
		success:function(result){
			maxDepositMoney = result.list[6].doubleValue;
			if(maxDepositMoney != -1){
				$(".deposit_max_tip").html(maxDepositMoney.toMoney());
				$(".deposit_tip").show();
			}else{
				$(".deposit_tip").hide();
			}
			
			minDepositMoney = result.list[7].doubleValue;
			if(minDepositMoney != -1){
				$(".deposit_min_tip").html(minDepositMoney.toMoney());
				$(".deposit_tip").show();
			}else{
				$(".deposit_tip").hide();
			}
		}
	});
}
