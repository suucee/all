const PAGESIZE =20;
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
		refreshList(1);
	});
});

var _pageNo = 1;
function refreshList(pageNo) {
	$.jsonRPC
			.request(
					"userReportService.getRebatePage",
					{
						params : [ pageNo, PAGESIZE, "javascript:refreshList(??);",$("#startYear").val(),$("#startMonth").val()],
						success : function(result) {
							var html = '';
							if (result != null) {
								_pageNo = pageNo;
								
								for ( var i in result.list.list) {
									var item = result.list.list[i].map;
									html += '<tr>'
										+ '<td>'
										+ item.comment
										+ '</td>'
										+ '<td class="amount bold">'
										+  parseFloat(item.amount).toFixed(2)
										+ '</td>'
										+ '<td class="amount">'
										+ toDate(item.creatTime.time)
										+ '</td>'
										+ '</tr>';
								}
							}
								$.jsonRPC.request("userReportService.getRebateSum",{
											params : [$("#startYear").val(),$("#startMonth").val()],
											success : function(data) {
								html += '<tr><td colspan="4"></td></tr><tr>'
									+ '<td class="bold" colspan="4" style="border-top:0;">'
									+	'返佣总额（USD）:&nbsp;&nbsp;<span class="red">'+data.toFixed(2)+"</span>"
									+ '</td>'
								    + '</tr>';
								// page
								html += '<tr><td colspan="4"><div class="pagelist">'
										+ result.buttons + '</td></tr>';

								$("tbody#result_list").html(html);
											}
										});
								
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