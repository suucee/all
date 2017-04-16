var user_page = 1;
$(function() {
	getUserlist(user_page);
});

//获取用户列表
function getUserlist() {
	$('#result_list').html("");
	$.jsonRPC.request("user2RankService.getRebateUserAndMT4", {
		params : [],
		success : function(result) {
			var mrl = result['map']['mt4Rebate']['list'];
			var url = result['map']['userRebate']['list'];
			var html = "";
			for (var ii = 0; ii < url.length; ii++) {
				var obj = url[ii];
				var user_name = obj['name'] == null || obj['name'] == "" ? '<span class="gray">(暂无姓名)</span>' : obj['name'];
				html += '<tr data-tt-parent-id="0" data-tt-id="' + obj['orderUserId'] + '">';
				html += '<td class="bold">' + user_name + '</td>';
				html += '<td class="bold">' + (obj['rebate'] == null ? '0.00' : obj['rebate'].toFixed(2)) + '</td>';
				html += '<td>--</td>';
				html += '<td class="bold">' + (obj['volume'] == null ? '0.00' : obj['volume'].toFixed(2)) + '</td>';
				html += '<td>--</td>';
				html += '</tr>';
				for (var jj = 0; jj < mrl.length; jj++) {
					var item = mrl[jj];
					if (obj['orderUserId'] == item['orderUserId']) {
						html += '<tr data-tt-parent-id="' + item['orderUserId'] + '" data-tt-id="' + obj['login'] + '">';
						html += '<td>' + (item['login'] == null ? '--' : item['login']) + '</td>';
						html += '<td>&nbsp;</td>';
						html += '<td>' + (item['rebate'] == null ? '0.00' : item['rebate'].toFixed(2)) + '</td>';
						html += '<td>&nbsp;</td>';
						html += '<td>' + (item['volume'] == null ? '0.00' : item['volume'].toFixed(2)) + '</td>';
						html += '</tr>';
					}
				}
			}
			$('#result_list').append(html);
			$("#thetable").treetable({
				expandable : true
			});
		}
	});
}

