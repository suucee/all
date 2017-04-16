var _pageNo = 1;
var _login = 0;

$(document).ready(function() {
	_login = getParam("login", 0);
	//$("[name='login']").val(_login);
	
	$("#startDate").datetimepicker({
        timeFormat: "HH:mm:ss",
        dateFormat: "yy-mm-dd"
    });
	$("#endDate").datetimepicker({
        timeFormat: "HH:mm:ss",
        dateFormat: "yy-mm-dd"
    });
	// $("[name='login']").change(function(){
		// _login = $(this).val();
		// refreshList(1);
	// });
	$("#go").click(function(){
		refreshList(1);
	});
	initLoginList();
	refreshList(1);
});

function initLoginList() {
	$.jsonRPC.request("userCustomerService.getMT4UserPage",{
		params:[_pageNo, 1000, "javascript:refreshList(??);", 0],
		success : function(result) {
			if (result) {
				var html = '';
				html += '<option value="0">[不限]</option>';
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
	$.jsonRPC.request("userRebateService.getOrderPage", {
		params : [ pageNo, 20, "javascript:refreshList(??);", $("#startDate").val(), $("#endDate").val(), _login, "rebated"],
		success : function(result) {
			if (result != null) {
				_pageNo = pageNo;
								
				var html = '';
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
					html += '<tr><td>'+ item.order	+ '</td>'
						+ '<td><a href="javascript:changeLogin('+item.login+');">' + item.login + '</a></td>'
						+ '<td>' + toDate(item.closeTime*1000) + ' </td>'
						+ '<td>' + cmd_name + ' </td>'
						+ '<td>' + (item.symbol != ""? item.symbol:'<font style="color:#b94a48;">'+item.comment+'</font>') + ' </td>'
						+ '<td class="amount bold">' + (item.cmd < 2 && item.volume > 0 ? (item.volume/100).toFixed(2) : '') + ' </td>'
						+ '<td class="amount bold">' + (item.openPrice > 0 ? item.openPrice.toFixed(item.digits) : '') + '</td>'
						+ '<td class="amount bold" style="'+lable_css_commission+'">' + (item.commission != 0 ? item.commission.toFixed(2) : '') + ' </td>'
						+ '<td class="amount bold" style="'+lable_css_storage+'">' + (item.storage != 0 ? item.storage.toFixed(2) : '') + ' </td>'
						+ '<td class="amount bold padding-right20" style="'+lable_css+'">' + (item.profit != 0 ? item.profit.toFixed(2) : '' )+ '</td>'
					    + '</tr>';
					//返佣
					var str = '';
					var rebateName = "";
					for (var k in item._rebateRecords.list) {
						var record = item._rebateRecords.list[k];
						rebateName = record.rebateName;
						var color = k == item._rebateRecords.list.length - 1 ? 'blue' : 'red';
						str += record.comment + '=<span class="'+color+' bold">' + record.amount + "</span>；";
					}
					html += '<tr>'
						+ '<td><span class="blue">'+rebateName+'</span>返佣：</td>'
						+ '<td colspan=9">'+str+'</td>'
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

function changeLogin(login) {
	$("[name='login']").val(login);
	$("[name='login']").change();
}