var _pageNo = 1;
var _scheme = "";

$(document).ready(function() {
	_scheme = getParam("scheme", "");
	switch (_scheme) {
	case "all":
		$("h1").html("所有MT4账号");
		break;
	case "binded":
		$("h1").html("已绑定的MT4账号");
		break;
	case "unbinded":
		$("h1").html("未绑定的MT4账号");
		break;
	case "frozen":
		$("h1").html("冻结的MT4账号");
		break;
	case "disable":
		$("h1").html("禁用的MT4账号");
		break;
	case "deleted":
		$("h1").html("删除的MT4账号");
		break;
	}
	
	$("#popup_container").load("_popup_choose_user.html");
	refreshList(1);
	
	$("#startDate").datetimepicker({
        timeFormat: "HH:mm:ss",
        dateFormat: "yy-mm-dd"
    });
	$("#endDate").datetimepicker({
        timeFormat: "HH:mm:ss",
        dateFormat: "yy-mm-dd"
    });
	$("#go").click(function(){
		refreshList(1);
	});
	$("#startDate").change(function(){
		refreshList(1);
	});
	$("#endDate").change(function(){
		refreshList(1);
	});
});

var _bindingLogin = 0;
function bindUser(login) {
	_bindingLogin = login;
	openPopupChooseUser(0);
}
function unbindUser(login) {
	if (window.confirm('确定要解除MT4账号'+login+'的网站账户绑定吗？')) {
		$.jsonRPC.request('adminUserService.unbindMT4User', {
			params : [login],
			success : function(result) {
				refreshList(_pageNo);
				window.alert("解绑成功！");
			}
		});
	}
}

function callbackPopupChooseUser(userId) {
	if (_bindingLogin > 0) {
		$.jsonRPC.request('adminUserService.bindMT4User', {
			params : [_bindingLogin, userId],
			success : function(result) {
				
				_bindingLogin = 0;
				refreshList(_pageNo);
				closePopupChooseUser();
				window.alert("绑定成功！");
			}
		});
	}
}

function refreshList(pageNo) {
	_pageNo = pageNo;
	$.jsonRPC.request("adminMT4Service.getPage", {
		params : [ pageNo, 10, "javascript:refreshList(??);",
		           $("#startDate").val(), $("#endDate").val(),
		           _scheme, ""],
		success : function(result) {
			if (result != null) {
				_pageNo = pageNo;
	
				var html = '';
				for ( var i in result.list.list) {
					var item = result.list.list[i];
					var name = '';
					var action = '';
					if (item.user == null) {
						if (loginUser.role == 'OperationsManager') {
							action = ' <button class="btn btn-primary" onclick="javascript:bindUser('+item.login+');">绑定</button>';	
						}
						name += '（未绑定）';
					} else {
						if (loginUser.role == 'OperationsManager') {
							action = ' <button class="btn" onclick="javascript:unbindUser('+item.login+');">解绑</button>';	
						}
						name += '<a href="user_detail.html?id='+item.user.id+'" target="_blank"><i class="icon-user"></i> ' + item.user._name + '</a>';
					}
					
					html += '<tr><td><a href="mt4_order.html?login='+item.login+'" target="_blank">' + item.login + (item.enable ? '' : '<span class="red">(禁)</span>')+(item.enableReadOnly ? '<span class="purple">(冻)</span>' : '') + '</a></td>'
							+ '<td>' + name	+ ' </td>'
							+ '<td>' + item.name + ' </td>'
							+ '<td class="amount bold">' + item.balance.toFixed(2) + '</td>'
							+ '<td>' + item.group + ' </td>'
							+ '<td>' + toDate(item.regdate * 1000) + ' </td>'
							+ '<td>' + (item.lastTradeSyncTime == null ? '--' : toDate(item.lastTradeSyncTime.time)) + '</td>'
							+ '<td>' + action + '</td>'
						    + '</tr>';
				}
				// page
				html += '<tr><td colspan="9"><div class="pagelist">'
						+ result.buttons + '</td></tr>';
	
				$("tbody#result_list").html(html);
	
			}
		}
	});
} 



