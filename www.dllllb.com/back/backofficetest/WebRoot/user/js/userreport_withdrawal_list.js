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
	var userid = getParam("userid", "0");
	var scheme = getParam("scheme", "all");
	$.jsonRPC
			.request(
					"userWithdrawalService.getPageByUser",
					{
						params : [userid, pageNo, PAGESIZE,scheme, "javascript:refreshList(??);"],
						success : function(result) {
							
							if (result != null) {
								_pageNo = pageNo;
								var html = '';
								for ( var i in result.list.list) {
									var item = result.list.list[i];
									var label_class = "";
									var label_name = "";
									var show_remove = false;
									
									switch (item.state) {
									case 'WAITING':
										label_class = 'label-pending';
										label_name = '待审核';
										break;
									case 'AUDITED':
										label_class = 'label-important';
										label_name = '已通过';
										show_remove = true;
										break;
									case 'REJECTED':
										label_class = 'label-important';
										label_name = '已拒绝';
										break;
									case 'REMITTED':
										label_class = 'label-success';
										label_name = '已汇出';
										show_remove = true;
										break;
									case 'BACK':
										label_class = 'label-important';
										label_name = '银行退回';
										show_remove = true;
										break;
									case 'CANCELED':
										label_class = 'label-cancel';
										label_name = '用户撤销';
										break;
									}
									html += '<tr>'
										+ '    <td>#'+item.id+'</td>' 
										+ '    <td><span class="label '+label_class+'">'+label_name+'</span></td>'
										+ '    <td><a href="user_detail.html?id='+item._userId+'" target="_blank">'+item._userName+'</a></td>'
										+ '    <td class="amount bold">'+item.amount.toFixed(2)+' '+item.currency+'</td>'
										+ '    <td>'+toDate(item.creatTime.time)+'</td>'
										+ '    <td>'+(item.auditedTime == null ? ' - ' : toDate(item.auditedTime.time))+'</td>'
										+ '</tr>';
								}
								
								// page
								html += '<tr><td colspan="7"><div class="pagelist">'
										+ result.buttons + '</td></tr>';

							
							$("tbody#result_list").html(html);
										
							}	
						}
					});

}
