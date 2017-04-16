var _page=1;
$(function(){
	$("#go").click(function() {
	  getlist(1);
	});
	$("#scheme").change(function() {
		  getlist(1);
		});
	getlist(_page);
});

function getlist(pageNo){
	$.jsonRPC.request("adminRiskService.getAllUserList", {
		params : [pageNo,20,"javascript:getlist(??)", $("#keyword").val(),$("#scheme").val()],
		success : function(result) {	
			_page=result.currentPage;
	         var html="";
		    for(var i in  result.list.list){
		    	var item=result.list.list[i].map;
		    	var label_class="";
		    	var label_name="";
		    	switch (item.State) {
				case 'UNVERIFIED':
					label_class = 'label-important';
					label_name = '资料不全';
					break;
				case 'VERIFIED':
					label_class = 'label-success';
					label_name = '已通过';
					break;
				case 'REJECTED':
					label_class = 'label-cancel';
					label_name = '被驳回';
					break;
				case 'AUDITING':
					label_class = 'label-pending';
					label_name = '审核中';
					break;
				default:
					label_class = 'label-pending';
					label_name = '--';
				}
				html+= '<tr>';
				html+= '<td>' + item.Name + '&nbsp;'+(item.AllowDeposit==0?'<span style="color:red;">[禁止入金]</span>':'') +'&nbsp;'+(item.AllowWithdrawal==0?'<span style="color:red;">[禁止出金]</span>':'') +'</td>' ;
				html+= '<td>' + item['_VIPGradeName'] + '  </td>' ;
				html+= '<td><span  class="label '+label_class +'" >' + label_name+ '</span></td>' ;
				html+= '<td>' + item.Mobile + '</td>' ;
				html+= '<td>' + item.Email + '</td>' ;
				html+= '<td>';
				html+= '<a class="btn btn-primary" style="color:#fff;" href="user_detail.html?id='+item['UId']+'" target="_blank">查看</a>';
				html+= '&nbsp;<input type="checkbox" data-uid="'+item['UId']+'" '+(item.AllowDeposit==0?'checked="true"':'') +' class="allowuser" data-type="allowdeposit">禁止入金</input>';
				html+= '&nbsp;<input type="checkbox" data-uid="'+item['UId']+'" '+(item.AllowWithdrawal==0?'checked="true"':'') +' class="allowuser" data-type="allowwithdrawal">禁止出金</input>';
				html+= '</td>';
				html+= '</tr>';
		    } 	
		    html+="<tr><td colspan='10'><div class='pagelist'>"+result.buttons+"</div></td></tr>"	
			$('#result_list').html(html);
			$(".allowuser").change(function() {
			 	   $.jsonRPC.request("adminRiskService.setUserAllow", {
					params : [$(this).data("uid"),$(this).data("type")],
					success : function(result) {	
						getlist(_page);
					}
					});
			});
			
		}
	});
}

