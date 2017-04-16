var _pageNo = 1;
$(document).ready(function(){
	refreshList(1);
});

function refreshList(pageNo) {
	_pageNo = pageNo;
	$.jsonRPC.request("userCustomerService.getMT4UserPage",{
		params:[_pageNo, 20, "javascript:refreshList(??);", 0],
		success:function(result){
		var html = '';
		if(result!=null){
			for (var i in result.list.list) {
				var item = result.list.list[i];
				html += '<tr>'
					+ '	<td>'+item.user._name+'</td>'
					+ '	<td>'+item.login+(item.enable ? '' : '<span class="red">(禁)</span>')+(item.enableReadOnly ? '<span class="purple">(冻)</span>' : '')+'</td>'
					+ ' <td style="text-align:right;color:red">'+item.balance.toFixed(2) + (item.credit == 0 ? '' : '<span class="blue">+信用'+item.credit.toFixed(2)+'</span>')+'</td>'
					+ '	<td>'+item.user.mobile+'</td>'
					+ '	<td>'+toDate(item.creatTime.time)+'</td>'
					+ ' <td>'+item.group+'</td>'
					+ ' <td></td>'
					+ '</tr>';
		    }
			// page
			html += '<tr><td colspan="10"><div class="pagelist">' + result.buttons + '</td></tr>';

			$("tbody#result_list").html(html);
		}
	} 
	});
}