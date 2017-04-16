var rank_user_id=getParam("user_id", 0);
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
		refreshList(1,rank_user_id);
	});
	$("#startDate").change(function(){
		refreshList(1,rank_user_id);
	});
	$("#endDate").change(function(){
		refreshList(1,rank_user_id);
	});
	refreshList(1,rank_user_id);
});

var _pageNo = 1;
function refreshList(pageNo,rank_user_id) {
	if(rank_user_id!=0){
	$.jsonRPC.request(
					"userRankService.getRankUserOrder",
					{
						params : [ pageNo, 10,rank_user_id,"javascript:refreshList(??,"+rank_user_id+");",$("#startDate").val(),$("#endDate").val()],
						success : function(result) {
							if (result != null) {
								_pageNo = pageNo;
								var html = '';
								list=result['map']['page']['list']['list'];
								email=result['map']['u_email'];
								$("#title").text(email+"的历史交易记录");
								for ( var i in list) {
									var item = list[i];
									var lable_css="";
									var cmd_name = "";
									var reason_name="";
									switch (item.cmd) {
									case 0:
										cmd_name = 'BUY';
										break;
									case 1:
										cmd_name = 'SELL';
										break;
									case 2:
										cmd_name = 'BUY_LIMIT';
										break;
									case 3:
										cmd_name = 'SELL_LIMIT';
										break;
									case 4:
										cmd_name = 'BUY_STOP';
										break;
									case 5:
										cmd_name = 'SELL_STOP';
										break;
									case 6:
										cmd_name = 'BALANCE';
										break;
									case 7:
										cmd_name = 'CREDIT';
										break;
									default:
										cmd_name = '';
										break;
									}
									switch (item.reason) {
									case 0:
										reason_name = 'client';
										break;
									case 1:
										reason_name = 'expert';
										break;
									case 2:
										reason_name = 'dealer';
										break;
									case 3:
										reason_name = 'signal';
										break;
									case 4:
										reason_name = 'gateway';
										break;
									case 5:
										reason_name = 'mobile';
										break;
									case 6:
										reason_name = 'Web';
										break;
									case 7:
										reason_name = 'API';
										break;
									default:
										reason_name = '';
									break;
									}
									var lable_css_commission = '';
									var lable_css_storage = '';
									
									if (item.profit<0){
										lable_css="color:#b94a48;";
									}
									if (item.commission<0){
										lable_css_commission="color:#b94a48;";
									}
									if (item.storage<0){
										lable_css_storage="color:#b94a48;";
									}
									html += '<tr><td>'+ item.order	+ '</td>'
										+ '<td>' + item.login + '</td>'
										+ '<td>' + toDate(item.openTime*1000) + ' </td>'
										+ '<td>' + cmd_name + ' </td>'
										+ '<td>' + (item.symbol != ""? item.symbol:'<font style="color:#b94a48;">'+item.comment+'</font>') + ' </td>'
										+ '<td class="amount bold">' + (item.cmd < 2 && item.volume > 0 ? (item.volume/100).toFixed(2) : '') + ' </td>'
										+ '<td class="amount bold">' + (item.openPrice > 0 ? item.openPrice.toFixed(item.digits) : '') + '</td>'
										+ '<td class="amount bold" style="'+lable_css_commission+'">' + (item.commission != 0 ? item.commission.toFixed(2) : '') + ' </td>'
										+ '<td class="amount bold" style="'+lable_css_storage+'">' + (item.storage != 0 ? item.storage.toFixed(2) : '') + ' </td>'
										+ '<td class="amount bold" style="'+lable_css+'">' + (item.profit != 0 ? item.profit.toFixed(2) : '' )+ '</td>'
									    + '</tr>';
								}
								// page
								html += '<tr><td colspan="10"><div class="pagelist">'
										+ result['map']['page']['buttons'] + '</td></tr>';

								$("tbody#result_list").html(html);

							}
						}
					});

     } 
}