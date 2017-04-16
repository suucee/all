const PAGESIZE = 10;
$(document).ready(function() {
	$("#startDate").datetimepicker({
        timeFormat: "HH:mm:ss",
        dateFormat: "yy-mm-dd"
    });
	$("#endDate").datetimepicker({
        timeFormat: "HH:mm:ss",
        dateFormat: "yy-mm-dd"
    });
	$("#go").click(function(){
		refreshList(1);
	});
	$("#startDate").change(function(){
		refreshList(1);
	});
	$("#endDate").change(function(){
		refreshList(1);
	});
	
	refreshList(1);
});

var _pageNo = 1;
function refreshList(pageNo) {
	$.jsonRPC
			.request(
					"userReportService.getWithdrawalPage",
					{
						params : [ pageNo, PAGESIZE, "javascript:refreshList(??);"],
						success : function(result) {
							
							if (result != null) {
								_pageNo = pageNo;
								var html = '';
								for ( var i in result.list.list) {
									var item = result.list.list[i].map;
									html += '<tr>'
										+ '<td>' +item.period+'</td>'
										+ '<td  class="amount bold">' + item.amount.toFixed(2)+ '</td>'
										+ '<td  class="amount">'
										+ ''+item.count+''
										+ '</td>'
										+ '<td>'
										+ '</td>'
									    + '</tr>';
								}
								$.jsonRPC.request("userReportService.getWithdrawalSum",{
									params : [],
									success : function(data) {
						html += '<tr><td colspan="4"></td></tr><tr>'
							+ '<td class="bold" colspan="4" style="border-top:0;">'
							+	'累计出金总额（CNY）:&nbsp;&nbsp;<span class="red">'+data.toFixed(2)+"</span>"
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
