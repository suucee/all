var _pageNo = 1;
const PAGESIZE = 10;
$(document).ready(function() {
	getYearList();
	$("#startMonth").change(function () {
		if($("#startYear").val()==0){
			$("#startYear").val((new Date()).getFullYear());
		}
	})
	$("#go").click(function(){
		if($("#startYear").val()==0&&$("#startMonth").val()!=0){
			$("#startYear").val((new Date()).getFullYear());
		}
		refreshList(_pageNo);
	});
	refreshList(_pageNo);
});

var _pageNo = 1;
function refreshList(pageNo) {
	$.jsonRPC
			.request(
					"userReportService.getCustomerWithdrawalPage",
					{
						params : [ pageNo, PAGESIZE, "javascript:refreshList(??);", 
									 $("#startYear").val(),$("#startMonth").val()],
						success : function(result) {
							
							if (result != null) {
								_pageNo = pageNo;
								var html = '';
								for ( var i in result.list.list) {
									var item = result.list.list[i].map;
									html += '<tr>'
											+ '<td>'+(parseFloat(i)+1+(pageNo-1)*PAGESIZE)+'</td>'
											+ '<td>'
											+ ''+item.email+''
											+ '</td>'
											+ '<td class="amount bold">'
											+ ''+(item.amountSum).toFixed(2)+''
											+ '</td>'
											+ '<td>'
											+ ''+item.withdrawalcounts+''
											+ '</td>'
											+ '<td>'
											+ toDate(item.lastTime.time)
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
function getYearList(){
	var html='<option value="0">[全部]</option>';
	var year = 2000;
	var date = new Date();
	for ( var i = 0; i <= (date.getFullYear()-2000); i++) {
		html+='<option value="'+year+'">'+year+'</option>';
		year++;
	};
	$("#startYear").html(html);
	$("#startYear").val(date.getFullYear());
	$("#startMonth").val(date.getMonth() + 1);
	refreshList(1);
};
