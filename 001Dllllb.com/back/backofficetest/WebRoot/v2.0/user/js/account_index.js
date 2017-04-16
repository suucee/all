var arry=[];
var flag=true;
$(document).ready(function(){
		
		$("#transfer_in_from").change(function(){
			var login = $(this).val();
			
			if(login == ""){
				$(".balance_amount").html("");
			}else {
				$.jsonRPC.request("userAccountService.getMt4Balance", {
					params:[login],
					success:function(result){
						$(".balance_amount").html("可用余额：<font color='red'>"+result.toMoney(2)+"</font>&nbsp;&nbsp;USD");
					}
				});
			}
		});
	
	paymentPwd();
	getUserEmailAndName();
	getBalanceList();
	refreshMT4List();
	$("#btn_refresh_mt4users").click(function(){
		$.jsonRPC.request("userAccountService.refreshMT4Users", {
			params : [],
			success : function(result) {
				refreshMT4Table();
				layer.msg("更新余额成功。",{time:1000,icon:1});
			}
		});
	});
	
	
	$("#addMt4").click(function(){
		addMt4();
	});
	
	$.jsonRPC.request("userAccountService.refreshMT4Users", {
		params : [],
		success : function(result) {
			refreshMT4Table();
		}
	});
	
	if (loginUser != null) {
			if (loginUser.level <= 3) {
	               getMonthRebateList();
			} else if (loginUser.vipGrade > 0) {
	               getMonthRebateList();
			} else {
			}
		}
});

var username = '';
var userEmail = '';

function getUserEmailAndName(){
	$.jsonRPC.request("user2Service.getUserEmailAndName",{
		params:[],
		success:function(result){
			username = result.map.name
			userEmail = result.map.email
		}
	});
}


function getWithdrawalsSum(item) {
	var type = item.currencyType;
	
	$.jsonRPC.request("userAccountService.getWithdrawalsSum",{
		params:[type],
		success:function(sum){
			html = '<tr>'
			     + '	<td>'+type+'</td>'
			     + '	<td class="bold align-right">'+item.amountAvailable.toMoney(2)+' '+type+'</td>'
			     + '	<td class="bold align-right">'+item.amountFrozen.toMoney(2)+'  '+type+'</td>'
			     + '	<td class="bold align-right">'+sum.toMoney(2)+'  '+type+'</td>'
			     + '	<td  style="text-align: center;">' + toDate(new Date()) + '</td>'
			     + '	<td  style="text-align: center;"><a class="btn btn-b" onclick="parent.openNewWindow('+"'./../user/account_statement.html'"+',updateList)">流水</a></td>'
			     + '</tr>';
		if (html=="") {
		     html += '<tr><td colspan="10" class="bold">对不起，暂未检索到相关记录</td></tr>';
		}
			$("tbody#result_list").append(html);
		}
	});
}

function getBalanceList(){
	$.jsonRPC.request("userAccountService.getBalanceList", {
		params:[],
		success:function(result){
			$("tbody#result_list").html("");
			for (var i in result.list) {
				var item = result.list[i];
				getWithdrawalsSum(item);
			}
		}
	});
}

function refreshMT4Table() {
	$.jsonRPC.request("userMT4Service.getList", {
		params:[],
		success:function(result) {
			var html = '';
			if(result!=null) {
				for (var i in result.list) {
					var item = result.list[i];
					if(item.password!=null){
						item.password = item.password.substring(0,1)+"******";
					}
					if(item.passwordInvestor!=null){
						item.passwordInvestor = item.passwordInvestor.substring(0,1)+"******";
					}
					
					html += '<tr>'
						+ '	<td style="text-align:center">'+item.login+'</td>'
						+ '	<td style="text-align:center" class="amount bold">'+item.balance.toFixed(2)+'</td>'
						+ '	<td style="text-align:center">'+item.password+'</td>'
						+ '	<td style="text-align:center">'+item.passwordInvestor+'</td>'
						+ '	<td style="text-align:center">'+toDate(item.creatTime.time)+'</td>'
						+ ' <td style="text-align:center">'
						+ '<button class="btn  btn-b" onclick="javascript:resetMT4Password('+item.login+');">忘记密码</button>&nbsp;&nbsp;&nbsp;&nbsp;'
						+ '<a class="btn btn-b" onclick="parent.openNewWindow('+"'./../user/mt4history.html?login="+item.login+"'"+',updateList)">历史交易</a>'
						+ '</td>'
						+ '</tr>';
			    } 	    
		if (html=="") {
		     html += '<tr><td colspan="10" class="bold">对不起，暂未检索到相关记录</td></tr>';
		}
				$("tbody#mt4result_list").html(html);
				
			}
		} 
	});
}

function resetMT4Password(login) {
	layer.confirm("确定重置MT4账户"+login+"的密码？",{btn:["重置","取消",]},
		function(){
			$.jsonRPC.request('userMT4Service.resetMT4UserPassword', {
				params : [login],
				success : function(result) {
					layer.msg("重置MT4账户密码成功！",{time:1000,icon:1},function(){
						refreshMT4Table();
					});
				}
			});
	},function(){});
}

function getMonthRebateList(){
	$.jsonRPC.request("user2RankService.getMonthRebate", {
		params : [],
		success : function(result) {
			var list = result['map']['monthRebate']['list'];
			var rebateCount = result['map']['rebateCount'];
			var html="";
			for (var ii = 0; ii < list.length; ii++) {
				var obj = list[ii];
				html+= '<tr>';
				html+= '<td>' + obj['monthstr'] + '</td>';
				html+= '<td>' + obj['rebateCount'].toFixed(2)  + '</td>' ;
				html+='<td><a  class="btn  btn-b" onclick="parent.openNewWindow('+"'./../agent/customer_rebate_order.html?month="+obj['monthstr']+"'"+',updateList)">查看返佣明细</a></td>';
                html+= '</tr>';
			}
			html+='<tr>';
			html+='<td colspan="2">我的返佣收入累计：<span class="bold"> ' + rebateCount.toFixed(2) + '（USD）</span></td>';
			html+='<td><a  class="btn  btn-b" onclick="parent.openNewWindow('+"'./../agent/customer_rebate_order.html'"+',updateList)">查看返佣明细</a></td>';
			html+='</tr>';
		if (html=="") {
		     html += '<tr><td colspan="10" class="bold">对不起，暂未检索到相关记录</td></tr>';
		}
            $('#monthrebate_list').html(html);
            $('.monthrebate').show();
		}
	});
}


var arry=[];
var flag=true;
var optionStr = null;
$(document).ready(function(){
	
	
	$("#transfer_in_from").change(function(){
		var login = $(this).val();
		$(".transfer_mt4List_to").html(optionStr);
		if(login == ""){
			$(".balance_amount").html("");
		}else {
			$("#transfer_in_to option[value='"+login+"']").remove(); 
			$.jsonRPC.request("userAccountService.getMt4Balance", {
				params:[login],
				success:function(result){
					$(".balance_amount").html("可用余额：<font color='red'>"+result.toFixed(2)+"</font>&nbsp;&nbsp;USD");
				}
			});
		}
	});
	
	
	$("#transfer_in_to").change(function(){
		var login = $(this).val();
		if(login == ""){
			$(".balance_amount_to").html("");
		}else {
			$.jsonRPC.request("userAccountService.getMt4Balance", {
				params:[login],
				success:function(result){
					$(".balance_amount_to").html("可用余额：<font color='red'>"+result.toFixed(2)+"</font>&nbsp;&nbsp;USD");
				}
			});
		}
	});
	
	refreshMT4List();
	
});


function refreshMT4List() {
	$.jsonRPC.request("userMT4Service.getList", {
		params:[],
		success:function(result) {
			var html = '';
			if(result!=null) {
				
				//生成MT4账号数组
				var mt4Login="<option value=''>- 请选择 -</option><option value='0'>网站账户余额</option>";
				arry=[];
				for (var i in result.list) {
					var item = result.list[i];
					arry.push(item.login);
					mt4Login+="<option value='"+arry[i]+"'>MT4账号："+arry[i]+"</option>";
				}
				optionStr = mt4Login;
				$(".transfer_mt4List").html(mt4Login);
				$(".transfer_mt4List_to").html(mt4Login);
				
				//获取参数，可以由外部指定参数来设置转账
				var fromLogin = getParam("from", null);
				var toLogin = getParam("to", null);
				if(fromLogin != null){
					 setTransferFrom(fromLogin);
				}
				if(toLogin != null){
					setTransferTo(toLogin);
				}
			}
		} 
	});
}
function transferMT4List(login){
	var mt4Login="<option value=''>- 请选择 -</option><option value='0'>网站账户余额</option>";
	for ( var i = 0; i < arry.length; i++) {
		if(arry[i]!=login){
			mt4Login+="<option value='"+arry[i]+"'>MT4账号："+arry[i]+"</option>";
		}
	}
	$(".transfer_mt4List").html(mt4Login);
}


function transfer(){
	
	var fromLogin = $("#transfer_in_from").val();
	var money = $(".transfer_in_amount").val();
	var toLogin = $("#transfer_in_to").val();
	var operationInPassword=$("#operationInPassword").val();
	
	money = parseFloat(money).toFixed(2);
	
	if(fromLogin == ""){
		layer.tips("请选择一个转出账号.","#transfer_in_from",{time:2000,tips:[2,'#56a787']});
		$("#transfer_in_from").focus();
	}else if(toLogin == ""){
		layer.tips("请选择一个转入账号.","#transfer_in_to",{time:2000,tips:[2,'#56a787']});
		$("#transfer_in_to").focus();
	}else if(money == "" ||  (!(money > 0)) ){
		layer.tips("金额不正确.",".transfer_in_amount",{time:2000,tips:[2,'#56a787']});
		$(".transfer_in_amount").focus();
	}else if(operationInPassword==""){
		layer.tips("操作密码不能为空.","#operationInPassword",{time:2000,tips:[2,'#56a787']});
		$("#operationInPassword").focus();
	} else {
		var fromStr = null;
		var toStr = null;
		if(fromLogin == 0){
			fromStr = "网站账户余额";
		}else{
			fromStr = "MT4账号："+fromLogin;
		}
		if(toLogin == 0){
			toStr = "网站账户";
		}
		else{
			toStr = "MT4账号："+toLogin;
		}
		//询问框
		layer.confirm("您确定要从 <span class='font-red'>"+fromStr+"</span> 里面转账 </br><span class='font-red'>"+money+"(USD)</span>到 <span class='font-red'>"+toStr+"</span>吗？", {
		  btn: ['确认转账','放弃转账'] //按钮
		}, function(){
			if (flag) {
				flag=false;
				$.jsonRPC.request("userTransfersService.add",{
					params:[fromLogin,toLogin,money, operationInPassword],
					success:function(result){
						if(result=="交易成功"){
							layer.msg(result,{time:1000,icon:1},function(){
								location.reload();
							});
						}else{
							layer.msg(result,{time:2000,icon:2});
						}
						flag=true;
//						getBalanceList();
//						refreshMT4List();
					},
					error:function(result){
						flag=true;
						//Could not initialize class com.shoory.mt4.jni.ShooryMT4 ....
						if(result.error.msg.indexOf("Could")!=-1 || result.error.msg.indexOf("library")!=-1){
							layer.msg("后台程序好像开小差了，稍后再试吧.</br>",{time:2000,icon:2});
						}else{
							layer.msg(result.error.msg,{time:2000,icon:2});
						}
					}
				});
			}
		}, function(){});
	}
}

function addMt4() {
	var title = "请提供您的姓名和电子邮箱新开MT4";
	
	if(username != "" && userEmail != ""){
		title = '请确认您的姓名和邮箱是否正确';
	}
	
	//当用户名和邮箱为空时，用下面的方法
	var popup_name = '';
	var popup_email = '';
	var html = "您的姓名：（用于开户凭据）<br/>" +
				"<input type='text' class='popup_name' placeholder='输入您的姓名，以新开MT4账号'><br/>" +
				"您的邮箱：（用于接收密码）<br/>"+
				"<input type='text' class='popup_email' placeholder='输入您的邮箱，以接收MT4密码'><br/>";
	layer.open({
        type: 1,
        title:title,
        skin: 'layui-layer-demo',
        closeBtn: 2,
        area: '350px',
        shift: 2,
        shadeClose: true,
        content: '<div style="padding:20px;background:#56a787;color:white">'+html+'</div>',
        btn:['立即开户','放弃'],
        
        success:function(){
        	$(".popup_name").val(username);
        	$(".popup_email").val(userEmail);
        },
        
        yes: function(index, layero){
        	popup_name = $(".popup_name").val();
        	if(popup_name == ""){
        		layer.tips("请输入您的姓名.",".popup_name",{time:2000,tips:[2]});
        		return;
        	}
        	
        	popup_email = $(".popup_email").val();
        	if (!/^([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\-|\_|\.]?)*[a-zA-Z0-9]+\.[a-zA-Z]{2,10}$/.test(popup_email)){
        		layer.tips("请输入正确的电子邮箱地址.",".popup_email",{time:2000,tips:[2]});
        		return;
        	}
        	//通过邮箱和姓名新开MT4
        	$.jsonRPC.request("user2MT4Service.addByNameEmail",{
    			params:[popup_name,popup_email],
    			success:function(result){
    				layer.close(index);
    				layer.alert("<span class='font-red'>新开MT4账户成功.</span><br/>MT4账号和登录密码已经下发至您的邮箱，请注意查收（查收时需注意邮件是否被拦截为垃圾邮件）。",{icon:1},function(){
    					layer.closeAll();
    				});
    				refreshMT4Table();
    			},
    			error:function(result){
    				layer.close(index);
    				if(result.error.msg.indexOf("Could")!=-1){//Could not initialize class com.shoory.mt4.jni.ShooryMT4  错误原因："+result.error.msg
    					layer.msg("后台程序好像开小差了，稍后再试吧.</br>",{time:2000,icon:2},function(){
        					layer.closeAll();
        				});
    				}else{
    					layer.msg(result.error.msg,{time:3000,icon:2},function(){
        					layer.closeAll();
        				});
    				}
    			}
    		});
        	
        }
      });
}


function setTransferFrom(fromLogin){
	$("#transfer_in_from").val(fromLogin);
}

function setTransferTo(toLogin){
	$("#transfer_in_to").val(toLogin);
}

function updateList(){
	
}


function amountchange(){
	var moneyUSD = 0.0;
	var moneyUSDStr = $(".transfer_in_amount").val().trim();
	if(!isNaN(moneyUSDStr))
	{
		moneyUSD = parseFloat(moneyUSDStr).toFixed(2);
		$(".transfer_in_amount").val(moneyUSD);
	}
	else{
		layer.tips("请输入正确的转账金额.",".transfer_in_amount",{time:2000,tips:[2,'#56a787']});
		$(".transfer_in_amount").focus();
	}
}


/**
 * 检查支付密码是否设置
 */
function paymentPwd(){
	$.jsonRPC.request('userService.operationPwd', {
		params : [],
		success : function(result) {
			if (!result) {	//没设置支付密码
				$(".setPayA").attr("href","profile_changepassword.html?style=pay");
				$(".payTip").show(200);
				$(".transferbtn").removeAttr("onclick");
				$(".transferbtn").removeClass("btn-b");
				$(".transferbtn").addClass("btn-e");
				$(".transferbtn").css("cursor","default");
				
			}else{
				$(".payTip").hide(200);
			}
		}
	});
}
