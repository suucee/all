var cu = null;
$(function() {
	//alert($("dfsafas").data("id"));
	cu = new countUser();
});

var countUser = function() {
	this.pageNo=1;
	this.refresh(this.pageNo);
};

countUser.prototype = {
	refresh : function(_pageNo) {
		var pageSize=$("#pageSize").val();
		var urlFormat="javascript:cu.refresh(??)";
		var keyword=$("#keyword").val();
		$.jsonRPC.request("adminRiskService.getAllUserCount", {
			params : [_pageNo,pageSize,urlFormat,keyword],
			success : function(result) {
				 $("#result_list").html("");
				var list=result.map.page.list.list;
				var mlist=result.map.lmt.list;
				var userIds=new Array();
				for (var i = 0; i < list.length; i++) {
				     var item=list[i];
				     var hasUser=false;
				     for(var j=0;j<userIds.length;j++){
				     	if(userIds[j]==item.userId){
				     		hasUser=true;
				     	}
				     }
				     if(!hasUser){
				     	userIds.push(item.userId);
				     }
				}
				var html ="";
				for (var i = 0; i < list.length; i++) {
					var item=list[i];
						html+= '<tr class="user_'+item.userId+' showmt4" data-userid="'+item.userId+'">' 
							+ '<td class="td-icon"><i class="icon-sort-down"></i>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;' + (item.userId==null?"":"<a href='user_detail.html?id="+item.userId+"'>")+(item.name==null?"--":item.name)+(item.userId==null?"":"</a>")+ ' </td>'
							+ '<td >' + (item.userId==null?"":"<a href='user_detail.html?id="+item.userId+"'>")+(item.userEmail==null?"--":item.userEmail)+(item.userId==null?"":"</a>")+ ' </td>' 
							+ '<td class="amount bold" >' + (item.depositsAmount==null?"--":item.depositsAmount.toFixed(2)) + ' </td>' 
							+ '<td class="amount bold" >' + (item.withdrawalsAmount==null?"--":item.withdrawalsAmount.toFixed(2))+ ' </td>' 
							+ '<td class="amount bold" >' + (item.amountAvailable==null?"--":item.amountAvailable.toFixed(2))+ ' </td>' 
							+ '<td class="amount bold">' + (item.login==null?"--":item.login)+ ' </td>' 
							+ '<td class="amount bold">' + (item.mt4BalanceSum==null?"--":item.mt4BalanceSum.toFixed(2)) + ' </td>' 
							+ '<td class="amount bold">&nbsp; </td>' 
							+ '</tr>';
							for (var j = 0; j < mlist.length; j++) { 
								  var mitem=mlist[j];
								  if(mitem.user.id==item.userId){
								       html += '<tr class="mt4_'+item.userId+'" data-userid="'+item.userId+'" style="display:none;">' 
											+ '<td class="amount bold">&nbsp; </td>' 
											+ '<td class="amount bold">&nbsp; </td>' 
											+ '<td class="amount bold">&nbsp; </td>' 
											+ '<td class="amount bold">&nbsp; </td>' 
											+ '<td class="amount bold">&nbsp; </td>' 
											+ '<td class="amount bold">' + (mitem.login==null?"--":mitem.login)+ '</td>' 
											+ '<td class="amount bold">' + (mitem.balance==null?"--":mitem.balance)+ ' </td>' 
											+ '<td class="amount bold">&nbsp; </td>' 
											+ '</tr>';
								 }
							}
				}
				html+= '<tr><td colspan="10"><div class="pagelist">' + result.buttons + '</td></tr>';
				$("#result_list").html(html);
				$(".showmt4").click(function() {
				   var uid=$(this).data("userid");	
				      $(".mt4_"+uid).slideToggle(0,"linear");
				});
			}
		});
	}
};
