var user_page = 1;
$(function() {
	getUserlist(user_page);
});

//获取用户列表
function getUserlist() {
	$('#result_list').html("");
	$.jsonRPC.request("user2RankService.getMonthRebate", {
		params : [],
		success : function(result) {
			var list = result['map']['monthRebate']['list'];
			var rebateCount = result['map']['rebateCount'];
			var html="";
			for (var ii = 0; ii < list.length; ii++) {
				var obj = list[ii];
				html+= '<tr>';
				html+= '<td>' + obj['monthstr'] + '</td>';
				html+= '<td>' + obj['rebateCount'].toFixed(2)  + '</td>' ;
                html+= '</tr>';
			}
			html+='<tr><td class="align-right">返佣合计：</td><td> ' + rebateCount + '</td></tr>'
            $('#result_list').append(html);
		}
	});
}