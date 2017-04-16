$(document).ready(function() {
	getYearList();
	$("#startMonth").change(function () {
		if($("#startYear").val()==0){
			$("#startYear").val((new Date()).getFullYear());
		}
	})
	$("#pageSize").change(function () {
		refreshList(1);
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
					"adminReportService.getRebatePage",
					{
						params : [ pageNo, $("#pageSize").val(), "javascript:refreshList(??);",$("#startYear").val(),$("#startMonth").val()],
						success : function(result) {
							
							if (result != null) {
								_pageNo = pageNo;
								var html = '';
								for ( var i in result.list.list) {
									var item = result.list.list[i].map;
									html += '<tr>'
											+ '<td>'
											+ '<a href="user_detail.html?id='+item.uid+'" target="_blank">'+item.email+'</a>'
											+ '</td>'
											+ '<td>'
											+ item.mobile
											+ '</td>'
											+ '<td class="amount bold">'
											+  parseFloat(item.amount).toFixed(2)
											+ '</td>'
											+ '<td>'
											+ toDate(item.creatTime.time)
											+ '</td>'
											+ '<td>'
											+ item.comment
											+ '</td>'
										    + '</tr>';
								}
								// page
								html += '<tr><td colspan="5"><div class="pagelist">'
										+ result.buttons + '</td></tr>';

								$("tbody#result_list").html(html);

							};
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