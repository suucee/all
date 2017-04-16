$(document).ready(function() {
//	refreshList(1);
	getHistory(1);
	$("#go").click(function(){
		getHistory(1);
	});
});

var _pageNo = 1;
function refreshList(pageNo) {
	$.jsonRPC.request("userDepositService.getPage", {
		params : [ pageNo, 20, "javascript:refreshList(??);" ],
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
					case 'DEPOSITED':
					case 'ACCEPTED':
						label_class = 'label-success';
						label_name = '已到账';
						show_remove = true;
						break;
					case 'PENDING_PAY':
						label_class = 'label-pending';
						label_name = '待付款';
						show_remove = true;
						break;
					case 'PENDING_AUDIT':
					case 'PENDING_SUPERVISOR':
						label_class = 'label-pending';
						label_name = '审核中';
						show_remove = true;
						break;
					case 'REJECTED':
						label_class = 'label-important';
						label_name = '已驳回';
						show_remove = true;
						break;
					}
	
					html += '<tr><td>'
						+ item.id
						+ '</td>'
						+ '    <td><span class="label '
						+ label_class
						+ '">'
						+ label_name
						+ '</span></td>'
						+ '<td class="amount bold">'
						+ item.amount.toFixed(2)
						+ '</td>'
						+ '<td>'
						+ item.orderNum
						+ ' </td>'
						+ '    <td>'
						+ toDate(item.creatTime.time)
						+ '</td>'
						+ '    <td>'
						+ (item.paymentTime== null ? '--' : toDate(item.paymentTime.time))
						+ '</td>'
					    + '</tr>';
				}
				// page
				if(result.buttons != ""){
					html += '<tr><td colspan="6"><div class="pagelist">' + result.buttons + '</td></tr>';
				}
	
				$("tbody#result_list").html(html);
			}
		}
	});
}

/**
 * 获取历史记录
 * @param pageNo
 */
function getHistory(pageNo) {
	$.jsonRPC.request("user2DepositService.getPageByTimeAndState", {
		params : [ pageNo, 20, "javascript:getHistory(??);",
		           $("#startDate").val(), $("#endDate").val(), $("#state").val() ],
		success : function(result) {
			if (result != null) {
				if(result.list.list.length == 0){
					$("tbody#result_list").html("<tr><td class='bold' colspan=6>对不起，没有搜索到相关的记录。</td></tr>");
					return;
				}
				
				_pageNo = pageNo;
	
				var html = '';
				for ( var i in result.list.list) {
					var item = result.list.list[i];
					var label_class = "";
					var label_name = "";
					var show_remove = false;
					switch (item.state) {
					case 'DEPOSITED':
						label_class = 'label-success';
						label_name = '已到账';
						show_remove = true;
						break;
					case 'ACCEPTED':
						label_class = 'label-success';
						label_name = '已审核(到帐)';
						show_remove = true;
						break;
					case 'PENDING_PAY':
						label_class = 'label-pending';
						label_name = '待付款';
						show_remove = true;
						break;
					case 'PENDING_AUDIT':
					case 'PENDING_SUPERVISOR':
						label_class = 'label-pending';
						label_name = '审核中';
						show_remove = true;
						break;
					case 'REJECTED':
						label_class = 'label-important';
						label_name = '已驳回';
						show_remove = true;
						break;
					}
	
					html += '<tr><td>'
						+ item.id
						+ '</td>'
						+ '    <td><span class="label '
						+ label_class
						+ '">'
						+ label_name
						+ '</span></td>'
						+ '<td class="amount bold">'
						+ item.amount.toFixed(2)
						+ '</td>'
						+ '<td>'
						+ item.orderNum
						+ ' </td>'
						+ '    <td>'
						+ toDate(item.creatTime.time)
						+ '</td>'
						+ '    <td>'
						+ (item.paymentTime== null ? '--' : toDate(item.paymentTime.time))
						+ '</td>'
					    + '</tr>';
				}
				// page
				html += '<tr><td colspan="6"><div class="pagelist">' + result.buttons + '</td></tr>';
	
				$("tbody#result_list").html(html);
			}
			
		}
	});
}