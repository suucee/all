$(document).ready(function() {
	refreshList();
});

function refreshList() {
	$.jsonRPC.request("adminRebateSchemeService.getLevelList", {
		params : [],
		success : function(result) {
			if (result != null) {
				var html = '';
				for (var i in result.list) {
					var item = result.list[i];
					var name = '';
					switch (item.vipGrade) {
					case 1:
						name = "经理";
						break;
					case 2:
						name = "员工";
						break;
					}
					
					html += '<tr>' 
						+ '	<td>'+name+'</td>'
						+ '	<td>'+item.rebate.name+'</td>'
						+ '	<td>'+item.money.toFixed(2)+'</td>'
						+ '	<td>'+toDate(item.updatedTime.time)+'</td>'
						+ '</tr>';
				}
				$("#result_list").html(html);
			}
		}
	});
}
