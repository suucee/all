var _pageNo = 1;
var PAGESIZE = 6;
$(document).ready(function(){
	refreshList(1);
});
function refreshList(pageNo) {
	$.jsonRPC.request("userReportService.getDepositPage",{
						params : [ pageNo, PAGESIZE, "javascript:refreshList(??);"],
						success : function(result) {
							var html = '';
							if (result != null) {
								_pageNo = pageNo;
								
								for ( var i in result.list.list) {
									var item = result.list.list[i].map;
									html += '<tr>'
											+ '<td>' +item.period+'</td>'
											+ '<td class="amount bold">' + item.amount.toFixed(2)+ '</td>'
											+ '<td class="amount">'
											+ ''+item.count+''
											+ '</td>'
											+ '<td>'
											+ '</td>'
										    + '</tr>';
								}
								$.jsonRPC.request("userReportService.getDepositSum",{
									params : [],
									success : function(data) {
						html += '<tr><td colspan="4"></td></tr><tr>'
							+ '<td class="bold" colspan="4" style="border-top:0;">'
							+	'累计入金总额（CNY）:&nbsp;&nbsp;<span class="red">'+data.toFixed(2)+"</span>"
							+ '</td>'
						    + '</tr>';
						
									// page
									html += '<tr><td colspan="4"><div class="pagelist">'
											+ result.buttons + '</td></tr>';
									$("tbody#result_list").html(html);
									}
								});
								

							}
						}
					});

}