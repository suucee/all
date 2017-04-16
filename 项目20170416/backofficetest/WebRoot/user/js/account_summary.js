var arry=[];
var flag=true;
$(document).ready(function(){
	$.get("_popup_in.html", function(data) {
		$("#popupIn_container").html(data);
		
		$("#transfer_in_from").change(function(){
			var login = $(this).val();
			
			if(login == ""){
				$(".balance_amount").html("");
			}else {
				$.jsonRPC.request("userAccountService.getMt4Balance", {
					params:[login],
					success:function(result){
						$(".balance_amount").html("可用余额：<font color='red'>"+result.toFixed(2)+"</font>&nbsp;&nbsp;USD");
					}
				});
			}
		});
	});
	
	$("#popupOut_container").load("_popup_out.html");
	
	getBalanceList();
	refreshMT4List();
	
	$("#btn_refresh_mt4users").click(function(){
		$.jsonRPC.request("userAccountService.refreshMT4Users", {
			params : [],
			success : function(result) {
				refreshMT4List();
				window.alert("更新成功！");
			}
		});
	});
	
	$.jsonRPC.request("userAccountService.refreshMT4Users", {
		params : [],
		success : function(result) {
			refreshMT4List();
		}
	});
});

function getWithdrawalsSum(item) {
	var type = item.currencyType;
	
	$.jsonRPC.request("userAccountService.getWithdrawalsSum",{
		params:[type],
		success:function(sum){
			html = '<tr>'
			     + '	<td class="sample-currency"><a href="account_statement.html">'+type+'</a></td>'
			     + '	<td>' + toDate(item.updatedTime.time) + '</td>'
			     + '	<td class="amount bold">'+item.amountAvailable.toFixed(2)+' '+type+'</td>'
			     + '	<td class="amount bold blue">'+item.amountFrozen.toFixed(2)+' '+type+'</td>'
			     + '	<td class="amount">'+sum.toFixed(2)+'  '+type+'</td>'
			     + '</tr>';
		    
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

function refreshMT4List() {
	$.jsonRPC.request("userMT4Service.getList", {
		params:[],
		success:function(result) {
			var html = '';
			if(result!=null) {
				for (var i in result.list) {
					var item = result.list[i];
					html += '<tr>'
						+ '	<td>'+item.login+'</td>'
						+ '	<td class="amount bold">'+item.balance.toFixed(2)+'</td>'
						+ '	<td style="padding-left: 20px;"><a href="javascript:openPopupIn('+item.login+');"><span class="btn btn-primary">转入</span></a>&nbsp;&nbsp;'
						+ ' <a href="javascript:openPopupOut('+item.login+');"><span class="btn btn-primary">转出</span></a></td>'
						+ '</tr>';
			    }
				$("tbody#mt4result_list").html(html);
				
				//生成MT4账号数组
				arry=[];
				for (var i in result.list) {
					var item = result.list[i];
					arry.push(item.login);
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

function openPopupIn(login){
	$(".do_in .transfer_in_to").html("<font color='#2d4f68'>[MT4账号]</font>  "+login);
	$("#to_login").val(login);
	transferMT4List(login);
	$(".do_in").fadeIn(200);
}

function openPopupOut(login){
	$(".do_out .transfer_out_from").html("<font color='#2d4f68'>[MT4账号]</font>  "+login);
	$("#from_login").val(login);
	$.jsonRPC.request("userAccountService.getMt4Balance", {
		params:[login],
		success:function(result){
			$(".out_balance_amount").text(result.toFixed(2));
		}
	});
	
	transferMT4List(login);
	$(".do_out").fadeIn(200);
}

function doPopupIn(){
	if ($(".transfer_in_to").text()!=""){
		var to_login=$("#to_login").val();
		var from_login=$("#transfer_in_from").val();
		var amount=$(".transfer_in_amount").val();
		var operationInPassword=$("#operationInPassword").val();
		
		if (to_login==""){
			window.alert("系统错误，请稍后再试");
		} else if(from_login==""){
			$("#transfer_in_from").css("background", "#ffff80");
			$("#transfer_in_from").focus();
		} else if(!(amount > 0)){
			$("#transfer_in_from").css("background", "#fff");
			$(".transfer_in_amount").css("background", "#ffff80");
			$(".transfer_in_amount").focus();
		} else if(operationInPassword==""){
			$(".transfer_in_amount").css("background", "#fff");
			$("#operationInPassword").css("background", "#ffff80");
			$("#operationInPassword").focus();
		} else {
			if (flag) {
				flag=false;
				$.jsonRPC.request("userTransfersService.add",{
					params:[from_login,to_login,amount, operationInPassword],
					success:function(result){
						alert(result);
						getBalanceList();
						refreshMT4List();
						closePopup();
						flag=true;
					}
				});
			}
		}
	}
}
function doPopupOut(){
	if($(".transfer_out_from").text()!=""){
		
		var from_login=$("#from_login").val();
		var to_login=$("#transfer_out_to").val();
		var amount=$(".transfer_out_amount").val();
		var operationOutPassword=$("#operationOutPassword").val();
		if(from_login==""){
			alert("系统错误，请稍后再试");
		}else if(to_login==""){
			$("#transfer_out_to").css("background", "#ffff80");
			$("#transfer_out_to").focus();
		}else if(!(amount > 0)){
			$("#transfer_out_to").css("background", "#fff");
			$(".transfer_out_amount").css("background", "#ffff80");
			$(".transfer_out_amount").focus();
		}else if(operationOutPassword==""){
			$(".transfer_out_amount").css("background", "#fff");
			$("#operationOutPassword").css("background", "#ffff80");
			$("#operationOutPassword").focus();
		} else {
			if(flag){
				flag=false;
				
				$.jsonRPC.request("userTransfersService.add",{
					params:[from_login,to_login,amount, operationOutPassword],
					success:function(result){
						alert(result);
						getBalanceList();
						refreshMT4List();
						closePopup();
						flag=true;
					}
				});
			}
		}
	}
}

function closePopup() {
	$(".do_in").fadeOut(200);
	$(".do_out").fadeOut(200);
}