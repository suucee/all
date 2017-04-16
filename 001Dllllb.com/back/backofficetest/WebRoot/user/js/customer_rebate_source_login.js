$(document).ready(function() {
	getYearList();
	$("#startMonth").change(function () {
		if($("#startYear").val()==0){
			$("#startYear").val((new Date()).getFullYear());
		}
	});

	$("#go").click(function(){
		refreshList();
	});
	refreshList();
});

function refreshList() {
	$.jsonRPC.request("userRebateService.reportSourceLogins", {
		params : [$("#startYear").val(), $("#startMonth").val(), "", ""],
		success : function(result) {
			if (result != null) {
								
				var html = '';
				for ( var i in result.list) {
					var item = result.list[i];
					
					html += '<tr>'
						+ '<td>'+item.userName+'</td>'
						+ '<td>'+item.login+'</td>'
						+ '<td>'+item.vipGradeShowName+'</td>'
						+ '<td>'+item.userEmail+'</td>'
						+ '<td>'+item.userMobile+'</td>'
						+ '<td class="amount bold">'+item.rebate.toFixed(2)+'</td>'
						+ '<td class="amount">'+''+'</td>'
						+ '<td></td>'
						+ '</tr>';
				}
				// page
				html += '<tr><td colspan="10"><div class="pagelist">'
					+ result.buttons + '</td></tr>';

				$("tbody#result_list").html(html);
			}
		}
	});

} 

function getYearList(){
	var html = '<option value="0">[全部]</option>';
	var date = new Date();
	for ( var i = 2015; i <= date.getFullYear(); i++) {
		html += '<option value="'+i+'">'+i+'</option>';
	};
	$("#startYear").html(html);
	$("#startYear").val(date.getFullYear());
	$("#startMonth").val(date.getMonth() + 1);
};