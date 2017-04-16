var _page=1;
$(function(){
	$("#go").click(function() {
	  getlist(1);
	});
	$("#scheme").change(function() {
	  getlist(1);
	});
	$(".allowgrouptransfer").click(function(){
		 $.jsonRPC.request("adminRiskService.setGroupAllowTransfer", {
				params : [$("#scheme").val(),$(this).data("type")],
				success : function(result) {	
					getlist(_page);
				}
				});
	});
	getlist(_page);
});

function getlist(pageNo){
	$.jsonRPC.request("adminRiskService.getAllMT4UserList", {
		params : [pageNo,20,"javascript:getlist(??)", $("#keyword").val(),$("#scheme").val()],
		success : function(result) {	
			_page=result.map.page.currentPage;
	         var html="";
		    for(var i in  result.map.page.list.list){
		    	var item= result.map.page.list.list[i].map;
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
				html+= '<td>' + item.Login + '&nbsp;&nbsp;'+(item.allowTransfer==0?'<span style="color:red;">[禁止转账]</span>':'')+'</td>' ;
				html+= '<td>' + item.Group +'</td>' ;
				html+= '<td  class="amount bold">' + item.Balance.toFixed(2) +'</td>' ;
				html+= '<td>' + item.Name +'</td>' ;
				html+= '<td>' + item['_VIPGradeName'] + '  </td>' ;
				html+= '<td><span  class="label '+label_class +'" >' + label_name+ '</span></td>' ;
				html+= '<td>' + item.Mobile + '</td>' ;
				html+= '<td>' + item.Email + '</td>' ;
				html+= '<td>';
				html+= '<a class="btn btn-primary" style="color:#fff;" href="user_detail.html?id='+item['UId']+'" target="_blank">查看</a>';
				html+= '&nbsp;&nbsp;&nbsp;&nbsp;<input type="checkbox" data-login="'+item['Login']+'" '+(item.allowTransfer==0?'checked="true"':'') +' class="allowuser" data-type="allowtransfer">禁止转账</input>';
				html+= '</td>';
				html+= '</tr>';
		    } 	
		    html+="<tr><td colspan='10'><div class='pagelist'>"+result.map.page.buttons+"</div></td></tr>";	
			$('#result_list').html(html);
			$(".allowuser").change(function() {
			 	   $.jsonRPC.request("adminRiskService.setUserAllowTransfer", {
					params : [$(this).data("login")],
					success : function(result) {	
						getlist(_page);
					}
					});
			});
			if($("#scheme").val()=="all"){
			var htmlg="";
			htmlg+="<option value='all'>[全部]</option>";
			htmlg+="<option value='allowtransfer'>已禁止转账</option>";
			for(var n in result.map.group.list){
			  var ite=result.map.group.list[n];
			  htmlg+="<option value='"+ite+"'>"+ite+"</option>";
			}
			$("#scheme").html(htmlg);
			}
		}
	});
}


