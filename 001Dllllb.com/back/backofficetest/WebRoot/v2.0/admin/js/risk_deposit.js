const PAGESIZE = 20;
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
					"admin2ReportService.getDepositPage",
					{
						params : [ pageNo, PAGESIZE, "javascript:refreshList(??);", 
									 $("#startDate").val(), $("#endDate").val()],
						success : function(result) {
							if (result != null) {
								_pageNo = pageNo;
								var html = '';
								for ( var i in result.list.list) {
									var item = result.list.list[i].map;
									html += '<tr>'
											+ '<td style="text-align: center;">'
											+ '<a  onclick="goSeeProfile('+item['uid']+')">'+(item.name==null?"--":item.name)+'</a>'
											+ '</td>'
											+ '<td style="text-align: center;">'
											+ item.email 
											+ '</td>'
											+ '<td  style="text-align: center;" class="amount bold">'
											+ (item.amountSum.toFixed(2))
											+ '</td>'
											+ '<td style="text-align: center;">'
											+ item.depositcounts
											+ '</td>'
											+ '<td style="text-align: center;">'
											+ (item.lastTime!=null?toDate(item.lastTime.time):"--")
											+ ' </td>'
										    + '</tr>';
								}
								if(result.buttons!=null&&result.buttons!=""){
							    	html += '<tr><td colspan="10"><div class="pagelist">'+result.buttons+'</td></tr>';
							    }
								if (html=="") {
								     html += '<tr><td colspan="10" class="bold">对不起，暂未检索到相关记录</td></tr>';
								}

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
			  layer.msg("入金成功！",{time:1000,icon:1});
				refreshList(1);
						}
		}
	});
	
} 