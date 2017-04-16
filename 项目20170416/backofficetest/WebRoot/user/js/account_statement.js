$(document).ready(function(){

	$("#startDate").datetimepicker({
        timeFormat: "HH:mm:ss",
        dateFormat: "yy-mm-dd"
    });
	$("#endDate").datetimepicker({
        timeFormat: "HH:mm:ss",
        dateFormat: "yy-mm-dd"
    });
	
	$("#startDate").change(function(){
		refreshList(1);
	});
	$("#endDate").change(function(){
		refreshList(1);
	});
	refreshList(1);
});

function refreshList(pageNo) {
	$.jsonRPC.request("userAccountService.getBalanceLogs",{
		params:[pageNo, 10, "javascript:refreshList(??);", 
				 $("#startDate").val(), $("#endDate").val()],
		success:function(result){
			
		var html = '';
		for (var i in result.list.list) {
			var item = result.list.list[i];
			var type = item.currencyType;
			var refType = "";
			var refId = 0;
			var css_bg = "";
			var amount_color="";
			
			if(item.amount<0){
				amount_color="color:#c00000";
			}
			 if (item.depositId > 0) {
				refId = item.depositId;
				css_bg = "background:#f0f8ff";
			} else if (item.withdrawalId > 0) {
				refType = 'withdrawal';
				refId = item.withdrawalId;
				css_bg = "background:#fff8f0";
			} else if (item.transfersId > 0) {
				refId = item.transfersId;
				css_bg = "background:#fff8f0";
			}
			 
			html += '<tr style="'+css_bg+'">'
				+ '	<td>'+( refType=='withdrawal' ? '<a href="withdrawal_detail.html#'+refId+'">#'+refId+'</a>' : '#'+refId )+'</a></td>'
				+ '	<td>'+toDate(item.creatTime.time)+'</td>'
				+ '	<td class="amount bold " style="'+amount_color+'">'+item.amount.toFixed(2)+' '+type+'</td>'
				+ '	<td class="amount bold">'+item.amountAvailable.toFixed(2)+' '+type+'</td>'
				+ '	<td>'+item.description+'</td>'
				+ '</tr>';
	    }
		html += '<tr><td colspan="10"><div class="pagelist">'+result.buttons+'</td></tr>';
		
		$("tbody#result_list").html(html);
		
	} 
	});
}

