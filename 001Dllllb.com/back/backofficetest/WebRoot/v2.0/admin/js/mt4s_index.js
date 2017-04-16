var _pageNo = 1;
const PAGE_SIZE = 10;

$(document).ready(function() {
	
	$("#state1").change(function(){
		refreshList(1);
	});
	
	//可以指定搜索的login
	var login = getParam("login",0);
	if(login != 0){
		getOneMT4(login);		
	}else{
		refreshList(1);
	}
	
	
	$("#startDate1").datetimepicker({
        timeFormat: "HH:mm:ss",
        dateFormat: "yy-mm-dd"
    });
	$("#endDate1").datetimepicker({
        timeFormat: "HH:mm:ss",
        dateFormat: "yy-mm-dd"
    });
	$("#btn1").click(function(){
		refreshList(1);
	});
	
});

var _bindingLogin = 0;
function bindUser(login) {
	_bindingLogin = login;
	openPopupChooseUser(0);
}



//解绑
function unbindUser(login) {
	layer.confirm('您确定要<span class="red">解除MT4账号: '+login+' </span>与其网站账户的绑定吗？', {
		  btn: ['即刻解除','放弃'] //按钮
		},function(){
			$.jsonRPC.request('adminUserService.unbindMT4User', {
				params : [login],
				success : function(result) {
					layer.msg("解绑成功！",{time:1000,icon:1});
					refreshList(_pageNo);
				}
			});
		},function(){
			return;
		});
}

function refreshList(pageNo) {
	_pageNo = pageNo;
	$.jsonRPC.request("admin2MT4Service.getPage", {
		params : [ pageNo, PAGE_SIZE, "javascript:refreshList(??);",
		           $("#startDate1").val(), $("#endDate1").val(),
		           $("#state1").val(), $("#keyword1").val()],
		success : function(result) {
			if (result != null) {
				_pageNo = pageNo;
				if(result.list.list.length==0){
					$("tbody#result_list1").html('<tr><td colspan="7">对不起，没有检索到相关内容。</td></tr>');
					return;
				}
				var html = '';
				for ( var i in result.list.list) {
					var item = result.list.list[i];
					var name = '';
					var action = '';
					var mt4sOrderHTML = '';
					var state = '<span class="font-green">(正常)</span>';
					if(item.enable==0){state = '<span class="font-red">(已禁用)</span>';}
					if(item.deleted){state = '<span class="font-red">(已删除)</span>';}
					if(item.enableReadOnly){state = '<span class="purple">(已冻结)</span>';}
					if (item.user == null) {
						if (loginUser.role == 'OperationsManager') {
//							action = ' <button class="btn btn-a" onclick="javascript:bindUser('+item.login+');">绑定</button>';	
							action = ' <a class="btn btn-a"  onclick="parent.openNewWindow('+"'./../admin/mt4s_bind.html?login="+item.login+"'"+',updateList)">绑定用户</a>';	
						}
						name += '（未绑定）';
						//，没绑定就不显示下划线
						mt4sOrderHTML = item.login + state;
					} else {
						if (loginUser.role == 'OperationsManager') {
							action = ' <button class="btn btn-b" onclick="javascript:unbindUser('+item.login+');">解除绑定</button>';	
						}
						name += '<a  onclick="goSeeProfile('+item.user["id"]+')"><i class="fa fa-user"></i>' + item.user._name + '</a>';
						
						//显示下划线，可以查询交易记录
						mt4sOrderHTML = '<a href="mt4s_order.html?login='+item.login+'" style="text-decoration:underline;">'+item.login +'</a>'+ state;
					}
					
					html += '<tr><td>'+mt4sOrderHTML + '</td>'
							+ '<td>' + name	+ ' </td>'
							+ '<td>' + item.name + ' </td>'
							+ '<td class="amount bold font-red">' + item.balance.toFixed(2) + '</td>'
							+ '<td>' + item.group + ' </td>'
							+ '<td>' + toSimpleDate(item.regdate * 1000) + ' </td>'
							+ '<td>' + (item.lastTradeSyncTime == null ? '--' : toDate(item.lastTradeSyncTime.time)) + '</td>'
							+ '<td>' + action + '</td>'
						    + '</tr>';
				}
				// page
				if(result.buttons != ""){
					html += '<tr><td colspan="9"><div class="pagelist">'+ result.buttons + '</td></tr>';
				}
	
				$("tbody#result_list1").html(html);
	
			}
			else{
				$("tbody#result_list1").html('<tr><td colspan="7">对不起，没有检索到相关内容。</td></tr>');
				return;
			}
		}
	});
} 
function updateList(){
	refreshList(_pageNo);
}

function getOneMT4(login){
	$.jsonRPC.request("admin2MT4Service.getOneMT4", {
		params : [login],
		success : function(result) {
			if (result != null) {
				var item = result;
				var html = '';
				var name = '';
				var action = '';
				var mt4sOrderHTML = '';
				var state = '<span class="font-green">(正常)</span>';
				if(item.enable==0){state = '<span class="font-red">(已禁用)</span>';}
				if(item.deleted){state = '<span class="font-red">(已删除)</span>';}
				if(item.enableReadOnly){state = '<span class="purple">(已冻结)</span>';}
				if (item.user == null) {
					if (loginUser.role == 'OperationsManager') {
						action = ' <button class="btn btn-a" onclick="javascript:bindUser('+item.login+');">绑定</button>';	
					}
					name += '（未绑定）';
					//，没绑定就不显示下划线
					mt4sOrderHTML = item.login + state;
				} else {
					if (loginUser.role == 'OperationsManager') {
						action = ' <button class="btn btn-b" onclick="javascript:unbindUser('+item.login+');">解绑</button>';	
					}
					name += '<a  onclick="goSeeProfile('+item.user["id"]+')><i class="fa fa-user"></i>' + item.user._name + '</a>';
					
					//显示下划线，可以查询交易记录
					mt4sOrderHTML = '<a href="mt4s_order.html?login='+item.login+'"  style="text-decoration:underline;">'+item.login +'</a>'+ state;
				}
				
				html += '<tr><td>'+mt4sOrderHTML + '</td>'
						+ '<td>' + name	+ ' </td>'
						+ '<td>' + item.name + ' </td>'
						+ '<td class="amount bold font-red">' + item.balance.toFixed(2) + '</td>'
						+ '<td>' + item.group + ' </td>'
						+ '<td>' + toDate(item.regdate * 1000) + ' </td>'
						+ '<td>' + (item.lastTradeSyncTime == null ? '--' : toDate(item.lastTradeSyncTime.time)) + '</td>'
						+ '<td>' + action + '</td>'
					    + '</tr>';
				$("tbody#result_list1").html(html);
			}
			else{
				$("tbody#result_list1").html('<tr><td colspan="7">对不起，没有检索到相关内容。</td></tr>');
				return;
			}
	}
	});
}


