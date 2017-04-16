$(document).ready(function() {
	refreshList();
});

function refreshList() {
	$.jsonRPC.request("adminRebateSchemeService.getReferralList", {
		params : [],
		success : function(result) {
			if (result != null) {
				var html = '';
				for (var i in result.list) {
					var item = result.list[i];
					
					html += '<tr>' 
						+ '	<td>'+item.rebate.name+'</td>'
						+ '	<td>'+item.money1.toFixed(2)+'</td>'
						+ '	<td>'+item.money2.toFixed(2)+'</td>'
						+ '	<td>'+toDate(item.updatedTime.time)+'</td>'
						+ '</tr>';
				}
				$("#result_list").html(html);
			}
		}
	});
}
