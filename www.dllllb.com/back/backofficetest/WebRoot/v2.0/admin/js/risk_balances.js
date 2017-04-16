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
	
	refreshList(1);
});

var _pageNo = 1;
function refreshList(pageNo) {
	$.jsonRPC
			.request(
					"admin2ReportService.getBalancePage",
					{
						params : [ pageNo, PAGESIZE, "javascript:refreshList(??);"],
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
											+item.email
											+ '</td>'
											+ '<td  style="text-align: center;" class="amount bold">'
											+ (item.amountAvailable+item.amountFrozen).toFixed(2)
											+ '</td>'
											+ '<td style="text-align: center;"  class="amount bold">'
											+ item.amountAvailable.toFixed(2)
											+ '</td>'
											+ '<td style="text-align: center;" class="amount bold">'
											+ item.amountFrozen.toFixed(2)
											+ '</td>'
											+ '<td style="text-align: center;">'
											+ toDate(item.updatedTime.time)
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