const PAGESIZE = 20;
$(document).ready(function() {
	$("#startDate").datetimepicker({
        timeFormat: "HH:mm:ss",
        dateFormat: "yy-mm-dd"
    });
	$("#go").click(function(){
		refreshList(1);
	});
	$("#startDate").change(function(){
		refreshList(1);
	});
});

var _pageNo = 1;
function refreshList(pageNo) {
	$.jsonRPC
			.request(
					"adminReportService.getBalanceLogPage",
					{
						params : [ pageNo, PAGESIZE, "javascript:refreshList(??);",$("#startDate").val()],
						success : function(result) {
							if (result != null) {
								_pageNo = pageNo;
								var html = '';
								for ( var i in result.list.list) {
									var item = result.list.list[i].map;
									html += '<tr>'
											+ '<td>'+(parseFloat(i)+1+(pageNo-1)*PAGESIZE)+'</td>'
											+ '<td>'
											+ '<a href="user_detail.html?id='+item.uid+'" target="_blank">'+item.email+'</a>'
											+ '</td>'
											+ '<td class="amount bold">'
											+ (item.amountAvailable+item.amountFrozen).toFixed(2)
											+ '</td>'
											+ '<td class="amount bold">'
											+ item.amountAvailable.toFixed(2)
											+ '</td>'
											+ '<td class="amount bold">'
											+ item.amountFrozen.toFixed(2)
											+ '</td>'
											+ '<td>'
											+ toDate(item.creatTime.time)
											+ ' </td>'
										    + '</tr>';
								}
								// page
								html += '<tr><td colspan="6"><div class="pagelist">'
										+ result.buttons + '</td></tr>';

								$("tbody#result_list").html(html);

							}
						}
					});

}
function updatePayState(id,orderNum) {
	$.jsonRPC.request("userDepositService.updatePayState",{
	  params : [ id,orderNum ],
	  success : function(result) {
		  if(result>0){
				alert("入金成功");
				refreshList(1);
						}
		}
	});
	
} 