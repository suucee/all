var _recordCount = 0;
$(document).ready(function() {
	getYearList();
	$("#startMonth").change(function () {
		if($("#startYear").val()==0){
			$("#startYear").val((new Date()).getFullYear());
		}
		refreshList(1);
	});

	$("#startYear").change(function(){
		refreshList(1);
	});
	
	
	generateTable();
	
	$("#go").click(function(){
		refreshList(1);
	});
});


var rebateCount = 0;
function generateTable() {
	$.jsonRPC.request("adminRebateSchemeService.getList", {
		params : [],
		success : function(result) {
			if (result != null) {
				rebateCount = result.list.length;
				
				for (var i in result.list) {
					var item = result.list[i];
					
					var html = '<th class="blue" style="text-align:right;">'+item.name+'/标准手</th>';
					$(html).insertBefore($("#columnBefore"));
				}
				
				refreshList(1);
			}
		}
	});
}

var _pageNo = 1;
function refreshList(pageNo) {
	$.jsonRPC.request("adminRebateService.getStaffVolumeList", {
		params : [ $("#startYear").val(), $("#startMonth").val()],
		success : function(result) {
			if (result != null) {
				_recordCount = result.list.length;
				_pageNo = pageNo;
				var html = '';
				for ( var i in result.list) {
					var item = result.list[i];
					html += '<tr>'
						+ '<td>'+item.name+'</td>'
						+ '<td>'+item.vipGradeName+'</td>'
						+ '<td>'+item.email+'</td>'
						+ '<td>'+item.mobile+'</td>';
					for (var x=0;x<rebateCount;x++) {
						html += '<td style="text-align:right;">'+(item.volumes[x] == 0 ? '0.00' : (item.volumes[x] / 100.0).toFixed(2))+'</td>'
					}
					
					html += '<td><a class="btn btn-a" onclick="goSeeProfile('+item.userId+')">查看员工</a></td></tr>';
				}
				if (html=="") {
				     html += '<tr><td colspan="10" class="bold">对不起，暂未检索到相关记录</td></tr>';
				}
				
				if (result.list.length == 0) {
					html += '<tr><td colspan="10" style="text-align:center;">没有记录..</td></tr>';
				}
				//html += '<tr><td colspan="10" style="text-align:right;"><a href="javascript:sendMoney();"><span class="btn btn-primary">返佣</span></a></td></tr>';
				$("tbody#result_list").html(html);
			};
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