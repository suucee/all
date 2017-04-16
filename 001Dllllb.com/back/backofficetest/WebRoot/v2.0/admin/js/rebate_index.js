$(document).ready(function() {
	refreshList();
});

function refreshList() {
	$.jsonRPC.request("adminRebateSchemeService.getList", {
		params : [],
		success : function(result) {
			if (result != null) {
				var html = '';
				for (var i in result.list) {
					var item = result.list[i];
					
					html += '<tr>' 
						+ '	<td>'+item.name+'</td>'
						+ '	<td>'+item.description+'</td>'
						+ '	<td>'+toDate(item.updatedTime.time)+'</td>'
						+ '</tr>';
				}
				if (html=="") {
				     html += '<tr><td colspan="10" class="bold">对不起，暂未检索到相关记录</td></tr>';
				}
				$("#result_list").html(html);
			}
		}
	});
}
