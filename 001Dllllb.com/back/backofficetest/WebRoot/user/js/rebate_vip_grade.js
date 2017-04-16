$(document).ready(function() {
	refreshList();
});

function refreshList() {
	$.jsonRPC.request("adminRebateSchemeService.getVipGradeList", {
		params : [],
		success : function(result) {
			if (result != null) {
				var html = '';
				var old_group = '';
				for (var i in result.list) {
					var item = result.list[i];
					var group = item._vipGradeName;
					if (old_group != group) {
						old_group = group;
						html += '<tr><td colspan="4" class="bold">'+group+'</span></td></tr>';
					}
					
					html += '<tr>' 
						+ '	<td style="text-indent:2em;">'+item.rebate.name+'</td>'
						+ '	<td>'+item.money.toFixed(2)+'</td>'
						+ '	<td>'+toDate(item.updatedTime.time)+'</td>'
						+ '</tr>';
				}
				$("#result_list").html(html);
			}
		}
	});
}
