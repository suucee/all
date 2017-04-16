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
	
	if(fromLogin == ""){
		layer.tips("请选择一个转出账号.","#transfer_in_from",{time:2000,tips:[2,'#56a787']});
		$("#transfer_in_from").focus();
	}else if(toLogin == ""){
		layer.tips("请选择一个转入账号.","#transfer_in_to",{time:2000,tips:[2,'#56a787']});
		$("#transfer_in_to").focus();
	}else if(money == "" ||  (!(money > 0)) ){
		layer.tips("金额不正确.","#transfer_in_amount",{time:2000,tips:[2,'#56a787']});
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
		layer.confirm("您确定要从 <span class='red'> "+fromStr+" </span>里面转账</br> <span class='red'> "+money+"(USD)</span>到 <span class='red'> "+toStr+"</span>吗？", {
		  btn: ['确认转账','放弃转账'] //按钮
		}, function(){
			if (flag) {
				flag=false;
				$.jsonRPC.request("userTransfersService.add",{
					params:[fromLogin,toLogin,money, operationInPassword],
					success:function(result){
						flag=true;
						layer.msg(result,{time:2000});
					}
				});
			}
		}, function(){
		  return;
		});
	}
	
}

function setTransferFrom(fromLogin){
	$("#transfer_in_from").val(fromLogin);
}

function setTransferTo(toLogin){
	$("#transfer_in_to").val(toLogin);
}


