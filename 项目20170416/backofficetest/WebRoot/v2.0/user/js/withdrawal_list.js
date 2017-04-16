const PAGESIZE = 10;
var flag=true;
$(document).ready(function(){
	refreshDrawList(1);
});

var _pageNo = 1;
function refreshDrawList(pageNo) {
//	getPageByTime(int pageNo, int pageSize, String urlFormat, String startDate, String endTime,
		$.jsonRPC.request("userWithdrawalService.getPage",{
			params:[pageNo, PAGESIZE,"javascript:refreshDrawList(??);"],
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
							show_cancel = true;
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
							+ '<td><a style="text-decoration: underline;" target="_blank" href="withdrawal_detail.html?id='+item.id+'">#'+item.id+'</a></td>' 
							+ '<td class="amount bold">'+item.amount.toFixed(2)+' '+item.currency+'</td>'
							+ '<td><span class="label '+label_class+'">'+label_name+'</span></td>'
							+ '<td>'+toDate(item.creatTime.time)+'</td>'
							+ '<td>'+(item.dateTime == null ? ' - ' : toDate(item.dateTime.time))+'</td>'
							+ '<td>'
							+ (show_cancel ? '<a href="javascript:cancelRecord(\''+item.id+'\');"><span class="btn btn-b" alt="取消出金" title="取消出金">取消出金</span></a>&nbsp;&nbsp;'+
											 '<a  onclick="parent.openNewWindow('+"'./../user/withdrawal_detail.html?id="+item.id+"'"+',updateList)" class="btn btn-primary" >查看详情</a>' :
											 '<a  onclick="parent.openNewWindow('+"'./../user/withdrawal_detail.html?id="+item.id+"'"+',updateList)" class="btn btn-primary" >查看详情</a>');
							+'</td>'
							+ '</tr>';
				    }
					//page
					if(result.buttons != ""){
						html += '<tr><td colspan="10"><div class="pagelist">'+result.buttons+'</td></tr>';
					}
					
					$("tbody#result_list").html(html);
				}	
			}
		});
	
}


function cancelRecord(id) {
	//询问框
	layer.confirm("您确定要取消 <span class='red'>单号为 #" + id + "</span>的这项出金吗？", {
	  btn: ['确认取消','退出'] //按钮
	}, function(){//确认取消
		if (flag) {
			flag = false;
			$.jsonRPC.request("userWithdrawalService.cancel", {
				params : [ id ],
				success : function(result) {
					if (result) {
						refreshDrawList(_pageNo);
					} else {
						layer.msg("抱歉，取消出金单为 #" + id + "出错了，建议您稍候再试。",{time:2000,icon:2});
						refreshDrawList(_pageNo);
					}
					flag = true;
				}
			});
		}
	},function(){//退出
		return;
	});
}

function updateList(){
	
}
