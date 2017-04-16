$(document).ready(function() {
	generateTable();
	refreshList();
});

var rebateCount = 0;
function generateTable() {
	$.jsonRPC.request("adminRebateSchemeService.getList", {
		params : [],
		success : function(result) {
			if (result != null) {
				rebateCount = result.list.length;
				
				for (var i in result.list) {
					var item = result.list[i];
					
					var html = '<th class="blue">'+item.name+'</th>';
					$(html).insertBefore($("#columnBefore"));
				}
			}
		}
	});
}

function refreshList() {
	$.jsonRPC.request("adminRebateSchemeService.getVipGradeMt4GroupList", {
		params : [],
		success : function(result) {
			if (result != null) {
				var html = '';
				var old_group = '';
				var max_time = 0;
				var x = 0;
				for (var i in result.list) {
					var item = result.list[i];
					var group = item._vipGradeName;
					if (old_group != group) {
						html += '<tr><td colspan="5" class="bold">'+group+'</span></td></tr>';
						old_group = group;
					}

					if (x % rebateCount == 0) {
						max_time = item.updatedTime.time;
						html += '<tr>' 
							+ '	<td style="text-indent:2em;">'+item.mt4Group+'</td>';
					} else {
						if (max_time < item.updatedTime.time) {
							max_time = item.updatedTime.time;
						}
					}
					html += '	<td>'+item.money.toFixed(2)+'</td>';
					if ((x + 1) % rebateCount == 0) {
						html += '	<td>'+toDate(max_time)+'</td></tr>';
					}
					
					x++;
				}
				$("#result_list").html(html);
			}
		}
	});
}
