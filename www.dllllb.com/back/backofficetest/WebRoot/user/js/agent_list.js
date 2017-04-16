const PAGESIZE = 10;
var _pageNo = 1;


$(document).ready(function(){
	$("#popup_container").load("_popup_agent.html");
	
	
	refreshList(1);
});

function refreshList(pageNo) {
	_pageNo = pageNo;
	
	$.jsonRPC.request('adminAgentService.getPage', {
		params : [pageNo, PAGESIZE, "javascript:refreshList(??);", '', $("#keyword").val()],
		success : function(result) {
			var html = '';
			for (var i in result.list.list) {
				var item = result.list.list[i];
				var status = item.state;
				var label_class = "";
				var label_name = "";
				
				switch (item.vipGrade) {
				case 1:
					label_class = 'label-pending';
					break;
				case 2:
					label_class = 'label-particular-filled';
					break;
				}
				html += '<tr>'
					+ '    <td><a href="user_detail.html?id='+item.id+'" target="_blank"><i class="icon-user"></i> '+item._name+(item.disable ? ' <span class="red">[禁]</span>' : '') + (item.is_frozen ? ' <span class="purple">[冻]</span>' : '')+'</a></td>' 
					+ '    <td><span class="label '+label_class+'">'+item._vipGradeName+'</span></td>'
					+ '    <td>'+item.mobile+'</td>'
					+ '    <td>'+item.email+'</td>'
					+ '    <td>'+toDate(item.registrationTime.time)+'</td>'
					+ '    <td><a href="register.html?code='+item.referralCode+'" target="_blank">'+item.referralCode+'</a></td>'
					+ '    <td>'
					+ ' <button onclick="javascript:doEdit('+item.id+');" style="padding:2px 4px;margin-left:4px;" class="btn btn-medium btn-primary">编辑</button>'
					+ ' <button onclick="javascript:doSet('+item.id+');" style="padding:2px 4px;margin-left:4px;" class="btn btn-medium btn-primary">设置代理</button>'
					+ '</td></tr>';
		    }
			html += '<tr><td colspan="10"><div class="pagelist">'+result.buttons+'</td></tr>';

			$("tbody#result_list").html(html);
		}
	});
}

function doSet(userId) {
	popupAgent.open(userId, function() {
		refreshList(_pageNo);
	});
}
function doEdit(userId) {
	window.open('user_edit_profile.html?userId='+userId, '_blank');
}