const PAGESIZE = 10;
var flag=true;
$(document).ready(function(){
	refreshList(1);
});

var _pageNo = 1;
function refreshList(pageNo) {
		$.jsonRPC.request("userWithdrawalService.getPage",{
			params:[pageNo, PAGESIZE,"javascript:refreshList(??);"],
			success:function(result){
				if (result != null) {
					_pageNo = pageNo;
					
					var html = '';
					for (var i in result.list.list) {
						var item = result.list.list[i];
						var label_class = "";
						var label_name = "";
						var show_cancel = false;
						switch (item.state) {
						case 'WAITING':
							label_class = 'label-pending';
							label_name = '待审核';
							show_cancel = true;
							break;
						case 'PENDING_SUPERVISOR':
						case 'AUDITED':
							label_class = 'label-pending';
							label_name = '审核中';
							break;
						/*case 'AUDITED':
							label_class = 'label-important';
							label_name = 'Transferring Accepted';
							break;
						*/case 'REMITTED':
							label_class = 'label-success';
							label_name = '已汇出';
							break;
						case 'REJECTED':
							label_class = 'label-cancel';
							label_name = '已驳回';
							show_cancel = true;
							break;
						case 'BACK':
							label_class = 'label-cancel';
							label_name = '银行退回';
							show_cancel = true;
							break;
						case 'CANCELED':
							label_class = 'label-pending';
							label_name = '用户取消';
							break;
						}

						html += '<tr>'
							+ '<td><a target="_blank" href="withdrawal_detail.html#'+item.id+'">#'+item.id+'</a></td>' 
							+ '<td class="amount bold">'+item.amount.toFixed(2)+' '+item.currency+'</td>'
							+ '<td><span class="label '+label_class+'">'+label_name+'</span></td>'
							+ '<td>'+toDate(item.creatTime.time)+'</td>'
							+ '<td>'+(item.auditedTime == null ? ' - ' : toDate(item.auditedTime.time))+'</td>'
							+ '<td>'
							+ (show_cancel ? ' <a href="javascript:cancelRecord(\''+item.id+'\');" class="btn btn-small" alt="Remove Record" title="Remove Record"><i class="icon-remove"></i></a>' : ' - ')
							+'</td>'
							+ '</tr>';
				    }
					//page
					html += '<tr><td colspan="10"><div class="pagelist">'+result.buttons+'</td></tr>';
					
					$("tbody#result_list").html(html);
				}	
			}
		});
	
}


function cancelRecord(id) {
	if(flag){
		flag=false;
		$.jsonRPC.request("userWithdrawalService.cancel", {
			params : [ id ],
			success : function(result) {

				if (result) {
					// success
					refreshList(_pageNo);
				} else {
					// failed
					window.alert("取消出金单为 #" + id + "出错!");
					refreshList(_pageNo);
				}
		flag=true;
	}
	});
}
}