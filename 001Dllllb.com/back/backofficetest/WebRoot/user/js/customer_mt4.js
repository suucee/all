$(document).ready(
		function() {
			$("#go").click(
					function() {
						keyword = $(".keyword").val();
						if (keyword != "") {
							url = location.href;
							index1 = url.indexOf("user_mt4.html");
							link = url.substring(0, index1)
									+ "user_mt4.html?keyword=" + keyword;
							location.href = link;
						}
					});
			keyword1 = getParam("keyword", "");
			$(".keyword").val(keyword1);
			refreshList(keyword1);
		});

function refreshList(keyword) {
	$.jsonRPC.request("userRankService.getRankMt4UserList", {
		params : [ keyword ],
		success : function(result) {
			if (result != null) {
				pid = result['map']['pid'];
				list = result['map']['list']['list'];
				html = "";
				for (var ii = 0; ii < list.length; ii++) {
					item = list[ii]['map'];
					dohtml = '<a href="javascript:seetradelist('
							+ item['mu_u_id'] + ')">交易列表</a>';

					html += '<tr><td style="text-align:right;">' + item['mu_id'] + '</td>' + '<td>'
							+ item['u_email'] + ' </td>' + '<td>'
							+ item['user_name'] + ' </td>' + '<td>'
							+ item['mu_group'] + ' </td>' + '<td>'
							+ item['mu_city'] + ' </td>' + '<td>'
							+ item['email'] + ' </td>'
							+ '<td class="amount bold" style="text-align:right;">'
							+ item['balance'].toFixed(2) + '</td>' + '<td>'
							+ dohtml + '</td>' + '</tr>';
				}
				$("tbody#result_list").html(html);
			}
		}
	});

}

function seetradelist(id) {
	url = location.href;
	index1 = url.indexOf("customer_mt4.html");
	link = url.substring(0, index1) + "customer_mt4_order.html?user_id=" + id;
	location.href = link;
}