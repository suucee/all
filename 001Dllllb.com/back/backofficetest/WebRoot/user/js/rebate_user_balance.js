$(document).ready(function() {
	$("#btn_send").click(function(){
		var userIds = new Array();
		$("[name='userId']:checked").each(function(){
			var item={
				"userId":$(this).val(),
				"months":[$(this).data("month")]
			};
			var hasUser=false;
			var index=-1;
			for(var i in userIds){
				if(userIds[i]['userId']==item['userId']){
					hasUser=true;
					index=i;
					break;
				}
			}
			if(!hasUser){
		    	userIds.push(item);
			}else{
				if(item.months[0]=="all"){
					userIds[index].months=["all"];
				}
				if(userIds[index].months[0]!="all"){
			       	userIds[index].months.push(item.months[0]);
				}
			}
		});
		if (userIds.length == 0) {
			window.alert("一个用户也没勾选..");
			return;
		}
		
		if (window.confirm("确认要发放已勾选的用户的返佣吗？")) {
			$.jsonRPC.request("adminRebateService.sendRebate1", {
				params : [userIds],
				success : function(result) {
					window.alert("返佣金额已经成功转入用户网站可用金额！");
					refreshList();
				}
			});
		}
	});
	
	$("#check_all").change(function(){
		if ($("#check_all:checked").length == 1) {
			$("[name='userId']").prop("checked",true);
		} else {
			$("[name='userId']").prop("checked",false);
		}
	});
	
	refreshList();
});


function refreshList() {
	$('tbody#result_list').html("");
	$.jsonRPC.request("adminRebateService.getRebateBalanceList1", {
		params : [],
		success : function(result) {
			$("#thetable").treetable("destroy");
			if (result != null) {		
				var html = '';
				var c=0;
				var userid=0;
				for ( var i in result.map.listAll.list) {
					var item = result.map.listAll.list[i];
					if(c==0){
						userid=item['userId'];
					}
					html += '<tr  data-tt-parent-id="0" data-tt-id="'+item['userId']+'">'
						+ '<td><input class="parent-row p'+item['userId']+'" type="checkbox" name="userId" value="'+item.userId+'"  data-month="all"/></td>'
						+ '<td>'+item.userName+'</td>'
						+ '<td>'+item.vipGradeShowName+'</td>'
						+ '<td>'+item.userEmail+'</td>'
						+ '<td>'+item.userMobile+'</td>'
						+ '<td class="amount bold red">'+item.amount.toFixed(2)+'</td>'
						+ '<td class="amount bold">'+item.amountAvailable.toFixed(2)+'</td>'
						+ '<td class="amount bold">'+item.amountFrozen.toFixed(2)+'</td>'
						+ '<td class="amount">'+(item.amount+item.amountAvailable).toFixed(2)+'</td>'
						+ '</tr>';
					for ( var j in result.map.listMonth.list) {
						   var obj = result.map.listMonth.list[j];
						   if(obj.userId==item['userId']){
						   html += '<tr  data-tt-parent-id="'+item['userId']+'" data-tt-id="c'+obj['userId']+'">'
								+ '<td><input  class="child-row a'+item['userId']+'"  type="checkbox" name="userId" value="'+item.userId+'" data-month="'+obj.month+'" /></td>'
								+ '<td colspan="4">'+obj.month+'返佣</td>'
								+ '<td class="amount bold red">'+obj.amount.toFixed(2)+'</td>'
								+ '<td class="amount bold"></td>'
								+ '<td class="amount bold"></td>'
								+ '<td class="amount">'+(obj.amount+item.amountAvailable).toFixed(2)+'</td>'
								+ '</tr>';
						}
					}
				}
				$("tbody#result_list").html(html);
				$(".parent-row").change(function(){
					if ($(this).is(":checked")) {
						$(".a"+$(this).val()).prop("checked",true);
					} else {
						$(".a"+$(this).val()).prop("checked",false);
					}
					var isAllChecked=true;
					$(".parent-row").each(function(index,element){
						   if(!$(element).is(":checked")){
							   isAllChecked=false;
						   }
					});
					if(isAllChecked){
						$("#check_all").prop("checked",true);
					}else{
						$("#check_all").prop("checked",false);
					}
				});
				$(".child-row").change(function(){
					if ($(this).is(":checked")) {
						var hasNoChecked=false;
						$(".a"+$(this).val()).each(function(index,element){
							if (!$(element).is(":checked")) {
								hasNoChecked=true;
							}
						});
						if(!hasNoChecked){
							$(".p"+$(this).val()).prop("checked",true);
						}
					}else{
						$(".p"+$(this).val()).prop("checked",false);
					}
				});
				var mhtml="";
				for(var m in result.map.monthlist.list){
					var t=result.map.monthlist.list[m];
					mhtml+="<input type='checkbox' class='allmonth' value='"+t+"'>"+t+"</input>&nbsp;&nbsp;";
				}
				$(".monthlist").html(mhtml);
				$(".allmonth").change(function(){
					var v=$(this).val();
					var isc=false;
					if ($(this).is(":checked")) {
						isc=true;
					} else {
						isc=false;
					}
					$(".child-row").each(function(index,elem){
						if($(elem).data("month")==v){
							$(elem).prop("checked",isc);
						}
					});
				});
				$("#thetable").treetable({expandable: true}); 
			}
		}
	});

} 