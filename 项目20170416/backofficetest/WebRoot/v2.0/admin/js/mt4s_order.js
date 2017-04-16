var _pageNo = 1;
var _login = 0;
var show_operation = true;		
$(document).ready(function() {
	_login = getParam("login", 0);
	if(_login!=0){
		$("#keyword1").val(_login);
		refreshList(1);
	}
	
	$("#startDate").datetimepicker({
        timeFormat: "HH:mm:ss",
        dateFormat: "yy-mm-dd"
    });
	$("#startDate").change(function(){
		refreshList(1);
	});
	
	
	$("#endDate").datetimepicker({
        timeFormat: "HH:mm:ss",
        dateFormat: "yy-mm-dd"
    });
	$("#endDate").change(function(){
		refreshList(1);
	});
	
//	$("[name='login']").change(function(){
//		_login = $(this).val();
//		if(_login != 0){
//			show_operation = false;
//		}else{
//			show_operation = true;
//		}
//		refreshList(1);
//	});
	$("#go").click(function(){
		show_operation = true;
		refreshList(1);
	});

//	initLoginList();
	refreshList(1);
});

function initLoginList() {
	$.jsonRPC.request('adminMT4Service.getPage', {
		params : [1, 1000, "", "", "", "binded", ""],
		success : function(result) {
			if (result) {
				var html = '';
				html += '<option value="0">-所有MT4账号-</option>';
				for (var i in result.list.list) {
					var item = result.list.list[i];
					html += '<option value="'+item.login+'">'+item.login+' - '+item.user._name+' - '+item.name+'</option>';
				}
				$("[name='login']").html(html);
				
				_login = getParam("login", 0);
				$("[name='login']").val(_login);
				$("[name='login']").change();
			}
		}
	});
}

		
function refreshList(pageNo) {
	$.jsonRPC.request("admin2MT4Service.getOrderPageByKeywords", {
		params : [ pageNo, 20, "javascript:refreshList(??);", $("#startDate").val(), $("#endDate").val(), _login,0, "",$("#keyword1").val()],
		success : function(result) {
			if (result == null){
				$("tbody#result_list").html('<tr><td colspan="10">没有相关记录。</td></tr>');
				return;
			}
			else if (result != null) {
				_pageNo = pageNo;
				var html = '';
				if(result.list.list == 0){
					$("tbody#result_list").html('<tr><td colspan="10">没有相关记录。</td></tr>');
					return;
				}
				for ( var i in result.list.list) {
					var item = result.list.list[i];
					var lable_css="";
					var cmd_name = "";
					var reason_name="";
					switch (item.cmd) {
					case 0:
						cmd_name = 'BUY';
						break;
					case 1:
						cmd_name = 'SELL';
						break;
					case 2:
						cmd_name = 'BUY_LIMIT';
						break;
					case 3:
						cmd_name = 'SELL_LIMIT';
						break;
					case 4:
						cmd_name = 'BUY_STOP';
						break;
					case 5:
						cmd_name = 'SELL_STOP';
						break;
					case 6:
						cmd_name = 'BALANCE';
						break;
					case 7:
						cmd_name = 'CREDIT';
						break;
					default:
						cmd_name = '';
						break;
					}
					switch (item.reason) {
					case 0:
						reason_name = 'client';
						break;
					case 1:
						reason_name = 'expert';
						break;
					case 2:
						reason_name = 'dealer';
						break;
					case 3:
						reason_name = 'signal';
						break;
					case 4:
						reason_name = 'gateway';
						break;
					case 5:
						reason_name = 'mobile';
						break;
					case 6:
						reason_name = 'Web';
						break;
					case 7:
						reason_name = 'API';
						break;
					default:
						reason_name = '';
					break;
					}
					var lable_css_commission = '';
					var lable_css_storage = '';
					
					if (item.profit<0){
						lable_css="color:#b94a48;";
					}
					if (item.commission<0){
						lable_css_commission="color:#b94a48;";
					}
					if (item.storage<0){
						lable_css_storage="color:#b94a48;";
					}
					var name = '<a onclick="goSeeProfile('+item._bindUserId+')" >' + item._bindUsername + '</a>';
					if(item._bindUserId == 0 || item._bindUsername == null){
						name = "（未绑定）";
					}
					
					html += 
//						'<tr><td>'+ item.order	+ '</td>'
						'<tr><td><a href="mt4s_index.html?login='+item.login+'">' + item.login + '</a></td>'
						+ '<td>'+ name+ '</td>'
						+ '<td>' + toDate(item.openTime*1000) + ' </td>'
						+ '<td>' + cmd_name + ' </td>'
						+ '<td>' + (item.symbol != ""? item.symbol:'<font class="font-green">'+item.comment+'</font>') + ' </td>'
						+ '<td class="amount bold">' + (item.cmd < 2 && item.volume > 0 ? (item.volume/100).toFixed(2) : '0.00') + ' </td>'
						+ '<td class="amount bold">' + (item.openPrice > 0 ? item.openPrice.toFixed(item.digits) : '0.00') + '</td>'
						+ '<td class="amount bold" style="'+lable_css_commission+'">' + (item.commission != 0 ? item.commission.toFixed(2) : '0.00') + ' </td>'
						+ '<td class="amount bold" style="'+lable_css_storage+'">' + (item.storage != 0 ? item.storage.toFixed(2) : '0.00') + ' </td>'
						+ '<td class="amount bold" style="'+lable_css+'">' + (item.profit != 0 ? item.profit.toFixed(2) : '0.00' )+ '</td>'
						+ '<td>'
						+(show_operation ? '<a class="btn btn-a" onclick="javascript:changeLogin('+item.login+')">该号所有交易</a>' :'-')
						+'</td>'
						+ '</tr>';
				}
				// page
				if(result.buttons != ""){
					html += '<tr><td colspan="11"><div class="pagelist">'+ result.buttons + '</td></tr>';
				}

				$("tbody#result_list").html(html);
			}
		}
	});

} 

function changeLogin(login) {
//	$("[name='login']").val(login);
//	$("[name='login']").change();
	$("#keyword1").val(login);
	show_operation  = false;
	refreshList(1);
}