var _pageNo = 1;
var _login= getParam("login", 0);
var _userid=getParam("userid", 0);
$(document).ready(function() {
	_login = getParam("login", 0);
	$("#startDate").datetimepicker({
        timeFormat: "H:m",
        dateFormat: "y-m-d",
        defaultTime:"00:00:00",

    });
	$("#endDate").datetimepicker({
        timeFormat: "H:m",
        dateFormat: "y-m-d",
        defaultTime:"23:59:59",        	
    });
	$("#go").click(function(){
		_month="";
		refreshList(1);
	});
	refreshRequirement();
	refreshList(1);
});

function refreshRequirement(){
	$.jsonRPC.request("admin2MT4Service.getOrderPageRequirement", {
		params : [],
		success : function(result) {
			if(result.map.userlist.list.length>0){
				var list=result.map.userlist.list;
				var html='<option value="0">[全部]</option>';
				for(var i in list){
					html+="<option value='"+list[i]["userId"]+"'>"+list[i]["name"]+"</option>";
				}
				$("#userid").html(html);
			}
			if(result.map.loginlist.list.length>0){
				var list=result.map.loginlist.list;
				var html='<option value="0">[全部]</option>';
				for(var i in list){
					html+="<option value='"+list[i]["login"]+"'>"+list[i]["name"]+"--"+list[i]["login"]+"</option>";
				}
				$("#login").html(html);
			}
			$("#login").change(function(){
				if(result.map.loginlist.list.length>0){
					var list=result.map.loginlist.list;
					for(var i in list){
						if(list[i]['login']==$("#login").val()){
							$("#userid").val(list[i]['userId']);
							$("#userid").change();
						}
				    }
				}
				refreshList(1);
			});
			$("#userid").change(function(){
				if(result.map.loginlist.list.length>0){
					var list=result.map.loginlist.list;
					var html='<option value="0">[全部]</option>';
					for(var i in list){
						if($("#userid").val()!=0){
							if(list[i]['userId']==$("#userid").val()){
								html+="<option value='"+list[i]["login"]+"'>"+list[i]["name"]+"--"+list[i]["login"]+"</option>";
					        }
						}else{
							html+="<option value='"+list[i]["login"]+"'>"+list[i]["name"]+"--"+list[i]["login"]+"</option>";
						}
					}
					$("#login").html(html);
					refreshList(1);
				}
			});
		}
	});
}

function refreshList(pageNo) {
	$("tbody#result_list").html("");
	$.jsonRPC.request("admin2MT4Service.getOrderPage", {
		//params : [ pageNo, 20, "javascript:refreshList(??);", $("#startDate").val(), $("#endDate").val(), _login, _userid,"rebated"],
		params : [ pageNo, 20, "javascript:refreshList(??);",$("#startDate").val(),$("#endDate").val(),$("#login").val(),$("#userid").val(),"rebated",$("#keyWord").val()],
		success : function(result) {
			$("#thetable").treetable("destroy");
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
					html += '<tr  data-tt-parent-id="0" data-tt-id="'+item.order+'"><td>'+ item.order	+ '</td>'
						+ '<td><a href="javascript:changeLogin('+item.login+');">' + item.login + '</a></td>'
						+ '<td><a href="javascript:changeLogin('+item.login+');">' + item._bindUsername + '</a></td>'
						//+ '<td>' + toDate(item.openTime*1000) + ' </td>'
						+ '<td class="amount bold">' + (item.openPrice > 0 ? item.openPrice.toFixed(item.digits) : '') + '</td>'
						//+ '<td>' + toDate(item.closeTime*1000) + ' </td>'
						+ '<td class="amount bold">' + (item.closePrice > 0 ? item.closePrice.toFixed(item.digits) : '') + '</td>'
						+ '<td>' + cmd_name + ' </td>'
						+ '<td>' + (item.symbol != ""? item.symbol:'<font style="color:#b94a48;">'+item.comment+'</font>') + ' </td>'
						+ '<td class="bold">' + (item.cmd < 2 && item.volume > 0 ? (item.volume/100).toFixed(2) : '') + ' </td>'
					//	+ '<td class="amount bold" style="'+lable_css_commission+'">' + (item.commission != 0 ? item.commission.toFixed(2) : '') + ' </td>'
						+ '<td class=" bold" style="'+lable_css_storage+'">' + (item.storage != 0 ? item.storage.toFixed(2) : '') + ' </td>'
						+ '<td class="amount bold" style="'+lable_css+'">' + (item.profit != 0 ? item.profit.toFixed(2) : '' )+ '</td>'
					    + '</tr>';
					//返佣
					var str = '';
					var rebateName = '';
					for (var k in item._rebateRecords.list) {
						var record = item._rebateRecords.list[k];
						rebateName = record.rebateName;
						var color = k == item._rebateRecords.list.length - 1 ? 'blue' : 'red';
						html += '<tr  data-tt-parent-id="'+item.order+'" data-tt-id="'+record.id+'">';
						html += '<td colspan=2"><span class="blue">'+rebateName+'</span>返佣</td>';
						html += '<td colspan=2">'+record.comment +'的返佣收入</td>';
						html += '<td colspan=7">'+ '<span class="'+color+' bold">' +record.amount + "</span>"+'</td>';
						html += '</tr>';
					}
				}
				if(result.buttons!=null&&result.buttons!=""){
			    	html += '<tr><td colspan="10"><div class="pagelist">'+result.buttons+'</td></tr>';
			    }
				if (html=="") {
				     html += '<tr><td colspan="10" class="bold">对不起，暂未检索到相关记录</td></tr>';
				}

				$("tbody#result_list").html(html);
				$("#thetable").treetable({expandable: true});
			}
		}
	});

} 

function changeLogin(login) {
	$("[name='login']").val(login);
	$("[name='login']").change();
}