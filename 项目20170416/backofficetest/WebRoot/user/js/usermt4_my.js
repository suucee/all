$(document).ready(function(){
	$("#popup_dialog").load("_popup_create_mt4_user.html");
	
	refreshList();

	$("#btn_continue").click(function(){
		addMt4();
	});
});

function addMt4() {
	if (window.confirm("确定要新开一个MT4帐户吗？")) {
		$.jsonRPC.request("userMT4Service.add",{
			params:[],
			success:function(result){
				if (result) {
					window.alert("开通MT4账户成功");
				} else {
					window.alert("开通MT4账户失败");
				}
				refreshList();
			}
		});
	}
}


function addMt4User(scheme) {
	if (window.confirm("确定要新开一个MT4帐户吗？")) {
		$.jsonRPC.request("userMT4Service.add2",{
			params:[scheme],
			success:function(result){
				if (result) {
					window.alert("开通MT4账户成功");
				} else {
					window.alert("开通MT4账户失败");
				}
				refreshList();
			}
		});
	}
}

function refreshList() {
	$.jsonRPC.request("userMT4Service.getList",{
		params:[],
		success:function(result){
		var html = '';
		if(result!=null){
			for (var i in result.list) {
				var item = result.list[i];
				html += '<tr>'
					+ '	<td>'+item.login+(item.enable ? '' : '<span class="red">(禁)</span>')+(item.enableReadOnly ? '<span class="purple">(冻)</span>' : '')+'</td>'
					+ ' <td>'+item.balance.toFixed(2) + (item.credit == 0 ? '' : '<span class="blue">+信用'+item.credit.toFixed(2)+'</span>')+'</td>'
					+ '	<td>'+item.password+'</td>'
					+ '	<td>'+item.passwordInvestor+'</td>'
					+ '	<td>'+toDate(item.creatTime.time)+'</td>'
					+ ' <td>'+item.group+'</td>'
					+ ' <td><button class="btn btn-primary" onclick="javascript:resetMT4Password('+item.login+');">重置密码</button></td>'
					+ '</tr>';
		    }
			$("tbody#result_list").html(html);
		}
	} 
	});
}
function resetMT4Password(login) {
	if (window.confirm("确定重置MT4账户"+login+"的密码？")) {
		$.jsonRPC.request('userMT4Service.resetMT4UserPassword', {
			params : [login],
			success : function(result) {
				window.alert("重置密码成功！");
				refreshList();
			}
		});
	}
}
