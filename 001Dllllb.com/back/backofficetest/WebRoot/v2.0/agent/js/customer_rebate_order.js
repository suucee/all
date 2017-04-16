var _pageNo = 1;
var _month = getParam("month", "");
$(document).ready(function() {
	$("#startDate").datetimepicker({
        timeFormat: "H:m",
        dateFormat: "y-m-d",
        defaultTime:"00:00:00",

    });
	$("#endDate").datetimepicker({
        timeFormat: "H:m",
        dateFormat: "y-m-d",
        defaultTime:"23:59:59",        	
    });
	$("#go").click(function(){
		_month="";
		refreshList(1);
	});
	refreshRequirement();
	refreshList(1);
});
function refreshRequirement(){
	$.jsonRPC.request("user2RebateService.getOrderPageRequirement", {
		params : [0],
		success : function(result) {
			if(result.map.userlist.list.length>0){
				var list=result.map.userlist.list;
				var html='<option value="0">[全部]</option>';
				for(var i in list){
					html+="<option value='"+list[i]["userId"]+"'>"+list[i]["name"]+"</option>";
				}
				$("#userid").html(html);
			}
			if(result.map.loginlist.list.length>0){
				var list=result.map.loginlist.list;
				var html='<option value="0">[全部]</option>';
				for(var i in list){
					html+="<option value='"+list[i]["login"]+"'>"+list[i]["name"]+"--"+list[i]["login"]+"</option>";
				}
				$("#login").html(html);
			}
			$("#login").change(function(){
				if(result.map.loginlist.list.length>0){
					var list=result.map.loginlist.list;
					for(var i in list){
						if(list[i]['login']==$("#login").val()){
							$("#userid").val(list[i]['userId']);
						}
				    }
				}
				_month="";
				refreshList(1);
			});
			$("#userid").change(function(){
				if(result.map.loginlist.list.length>0){
					var list=result.map.loginlist.list;
					var html='<option value="0">[全部]</option>';
					for(var i in list){
						if($("#userid").val()!=0){
							if(list[i]['userId']==$("#userid").val()){
								html+="<option value='"+list[i]["login"]+"'>"+list[i]["name"]+"--"+list[i]["login"]+"</option>";
					        }
						}else{
							html+="<option value='"+list[i]["login"]+"'>"+list[i]["name"]+"--"+list[i]["login"]+"</option>";
						}
					}
					$("#login").html(html);
					_month="";
					refreshList(1);
				}
			});
		}
	});
}

function refreshList(pageNo) {
	$.jsonRPC.request("user2RebateService.getOrderPage", {
		params : [ pageNo, 20, "javascript:refreshList(??);",0,$("#userid").val(),$("#login").val(),$("#startDate").val(),$("#endDate").val(),_month,$("#keyWord").val()],
		success : function(result) {
            if($("#keyWord").val()!=""){
				$("legend").html('<i class="fa fa-list margin-right15" aria-hidden="true"></i>&nbsp;&nbsp;关键词《'+$("#keyWord").val()+'》的搜索结果');
			}else if(_month==""){
			    var title="我";
				if($("#userid").val()!=0){
					title+="的客户："+$("#userid").find("option:selected").text();
				}
				if($("#login").val()!=0){
					title+="，该客户的MT4账号："+$("#login").val();
				}
				if($("#startDate").val()!=""&&$("#endDate").val()!=""){
					title+="在"+$("#startDate").val()+"到"+$("#endDate").val()+"之间";
				}
				if($("#startDate").val()!=""&&$("#endDate").val()==""){
					title+="在"+$("#startDate").val()+"之后";
				}
				if($("#startDate").val()==""&&$("#endDate").val()!=""){
					title+="在"+$("#endDate").val()+"之前";
				}
				if(title!="我"){
			    	title+="，给我带来的返佣收入";
				}else{
					title+="的返佣收入";
				}
				$("legend").html('<i class="fa fa-list margin-right15" aria-hidden="true"></i>&nbsp;&nbsp;'+title);
			}else {
				$("legend").html('<i class="fa fa-list margin-right15" aria-hidden="true"></i>&nbsp;&nbsp;'+_month+'&nbsp;&nbsp;返佣明细');
			}
			if (result != null) {
				_pageNo = pageNo;					
				var html = '';
				for ( var i in result.list.list) {
					var item = result.list.list[i];
					html += '<tr><td>'+ item.orderId	+ '</td>'
						+ '<td>'+ item.login + '</td>'
						+ '<td>'+ item.name + '</td>'
						+ '<td>'+ item.rebateType + '</td>'
						+ '<td>'+ item.symbol + '</td>'
						+ '<td  class="amount bold">' + (item.volume/100).toFixed(2) + ' </td>'
						//+ '<td  class="amount bold">' +  (item.openPrice > 0 ? item.openPrice.toFixed(2) : '')+ ' </td>'
						//+ '<td  class="amount bold">' +  (item.closePrice > 0 ? item.closePrice.toFixed(2) : '')+ ' </td>'
						+ '<td  class="amount bold">' + (item.profit != 0 ? item.profit.toFixed(2) : '' )+ '</td>'
						+ '<td>' + toDate(item.createTime.time) + ' </td>'
						+ '<td class="amount bold">' + (item.amount != 0 ? item.amount.toFixed(2) : '') + ' </td>'
					    + '</tr>';
				}
				if (html=="") {
					html += '<tr><td colspan="10" class="bold">暂无返佣记录</td></tr>';
				}else{
				// page
					if(result.buttons != ""){
						html += '<tr><td colspan="12"><div class="pagelist">'+ result.buttons + '</td></tr>';
					}
                 }
				$("tbody#result_list").html(html);
			}
		}
	});

} 
