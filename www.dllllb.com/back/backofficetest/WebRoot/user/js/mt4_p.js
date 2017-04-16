$(document).ready(function() {
	refreshList(1);
});

var _pageNo = 1;
function refreshList(pageNo) {
	$.jsonRPC
			.request(
					"adminMT4Service.getSymbolsPage",
					{
						params : [ pageNo, 20, "javascript:refreshList(??);"],
						success : function(result) {
							if (result != null) {
								_pageNo = pageNo;

								var html = '';
								for ( var i in result.list.list) {
									var item = result.list.list[i];

									html += '<tr><td>'
											+item.symbol
											+ '</td>'
											+ '<td>'
											+ item.digits
											+ ' </td>'
											
										    + '</tr>';
								}
								// page
								html += '<tr><td colspan="2"><div class="pagelist">'
										+ result.buttons + '</td></tr>';

								$("tbody#result_list").html(html);

							}
						}
					});

} 