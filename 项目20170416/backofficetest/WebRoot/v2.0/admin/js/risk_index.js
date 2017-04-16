var cu = null;
$(function() {
	cu = new countUser();
});

var countUser = function() {
	this.pageNo=1;
	this.refresh(this.pageNo);
};

countUser.prototype = {
	refresh : function(_pageNo) {
		$("#thetable").treetable("destroy");
		var pageSize=$("#pageSize").val();
		var urlFormat="javascript:cu.refresh(??)";
		var keyword=$("#keyword").val();
		$.jsonRPC.request("admin2RiskService.getAllUserCount", { 
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
					
					var depositCounts_html = "";//入金次数
					var withdrawalsCounts_html = "";//出金次数
					
					if(item.depositCounts!=null){
						depositCounts_html = '['+item.depositCounts+'次]';
					}
					if(item.withdrawalsCounts != null){
						withdrawalsCounts_html = '[<a style="text-decoration:underline;" title="点击查看详细出金信息" href="javascript:parent.openNewWindow(\'./../admin/finance_check_record.html?scheme=REMITTED&userid='+item.userId+'\');">'+item.withdrawalsCounts+'次</a>]';
					}
						html+= '<tr data-tt-parent-id="0" data-tt-id="12580'+item.userId+'">' 
							+ '<td class="td-icon"><i class="icon-sort-down"></i>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;' 
							+ '<a onclick="goSeeProfile('+item['userId']+')">'+(item.name==null?"--":item.name)+(item.userId==null?"":"</a>")+ ' </td>'
							+ '<td >'+(item.userEmail==null?"--":item.userEmail)+(item.userId==null?"":"</a>")+ ' </td>' 
							+ '<td class="amount bold">' 
							+ (item.depositsAmount==null?"--":'<span class="bold font-green">'+item.depositsAmount.toFixed(2)+'</span>'+depositCounts_html)+'</td>' 
							+ '<td class="amount bold">' 
							+ (item.withdrawalsAmount==null?"--":'<span class="bold font-red">'+item.withdrawalsAmount.toFixed(2)+'</span>'+withdrawalsCounts_html)+'</td>' 
							+ '<td class="amount bold">' + (item.amountAvailable==null?"--":item.amountAvailable.toFixed(2))+ ' </td>' 
							+ '<td style="text-align: center;">' + (item.login==null?"--":item.login)+ ' </td>' 
							+ '<td class="amount bold">' + (item.mt4BalanceSum==null?"--":item.mt4BalanceSum.toFixed(2)) + ' </td>' 
							+ '</tr>';
							for (var j = 0; j < mlist.length; j++) { 
								  var mitem=mlist[j];
								  if(mitem.user.id==item.userId){
								       html += '<tr  data-tt-parent-id="12580'+item.userId+'" data-tt-id="'+mitem.login+'">' 
											+ '<td class="amount bold">&nbsp; </td>' 
											+ '<td class="amount bold">&nbsp; </td>' 
											+ '<td class="amount bold">&nbsp; </td>' 
											+ '<td class="amount bold">&nbsp; </td>' 
											+ '<td class="amount bold">&nbsp; </td>' 
											+ '<td style="text-align: center;">' + (mitem.login==null?"--":mitem.login)+ '</td>' 
											+ '<td class="amount bold">' + (mitem.balance==null?"--":mitem.balance)+ ' </td>' 
											+ '</tr>';
								 }
							}
				}
				if(result.map.page.buttons!=null&&result.map.page.buttons!=""){
			    	html += '<tr><td colspan="10"><div class="pagelist">'+result.map.page.buttons+'</td></tr>';
			    }
				if (html=="") {
				     html += '<tr><td colspan="10" class="bold">对不起，暂未检索到相关记录</td></tr>';
				}
				$("#result_list").html(html);
				$("#thetable").treetable({expandable: true});
			}
		});
	}
};
