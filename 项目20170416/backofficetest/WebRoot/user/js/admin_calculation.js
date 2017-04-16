var _recordCount = 0;
$(document).ready(function() {
	getYearList();
	$("#startMonth").change(function () {
		if($("#startYear").val()==0){
			$("#startYear").val((new Date()).getFullYear());
		}
	});

	$("#go").click(function(){
		refreshList(1);
	});
	$("#all").click(function () {
		
		if($(this).is(":checked")){
			$(".checkSelect").attr("checked","checked");
		}else {
			$(".checkSelect").removeAttr("checked");
		}
	});
	
});

var _pageNo = 1;
function refreshList(pageNo) {
	$.jsonRPC.request("adminRebateService.getRebatePage", {
		params : [ $("#startYear").val(),$("#startMonth").val()],
		success : function(result) {
			if (result != null) {
				_recordCount = result.list.length;
				_pageNo = pageNo;
				var html = '';
				for ( var i in result.list) {
					var item = result.list[i];
					html += '<tr>'
						+ '<td>'
						+ '<input type="hidden" value="'+item.userId+'" name="rebateUser"><a href="user_detail.html?id='+item.userId+'" target="_blank">'+item.email+'</a>'
						+ '</td>'
						+ '<td>'
						+ item.mobile
						+ '</td>'
						+ '<td class="amount bold '+item.userId+'rebate_amount">'
						+ item.rebate.toFixed(2)
						+ '</td>'
						+ '<td class="amount">'
						+ '<input type="text" style="width:100px;text-align:right;" name="adjustRebate" placeholder="0.00"  onchange="javascript:updateRebate('+item.userId+',this.value);"/>'
						+ '</td>'
						+ '<td class="amount bold red '+item.userId+'real_amount">'
						+ item.rebate.toFixed(2)
						+ '</td>'
						+ '<td></td>'
					    + '</tr>';
				}
				
				if (result.list.length == 0) {
					html += '<tr><td colspan="10" style="text-align:center;">没有记录..</td></tr>';
				}
				html += '<tr><td colspan="10" style="text-align:right;"><a href="javascript:sendMoney();"><span class="btn btn-primary">返佣</span></a></td></tr>';
				$("tbody#result_list").html(html);
				
			};
		}
	});
}
function getYearList(){
	var html = '<option value="0">[全部]</option>';
	var date = new Date();
	for ( var i = 2015; i <= date.getFullYear(); i++) {
		html += '<option value="'+i+'">'+i+'</option>';
	};
	$("#startYear").html(html);
	$("#startYear").val(date.getFullYear());
	$("#startMonth").val(date.getMonth() + 1);
	refreshList(1);
};
function updateRebate(userId,amount){
	if(!isNaN(parseFloat(amount))){
		var realAmount = parseFloat($("."+userId+"rebate_amount").text())+parseFloat(amount);
		$("."+userId+"real_amount").parent().find("input[name='adjustRebate']").val(parseFloat(amount));
		$("."+userId+"real_amount").text(realAmount.toFixed(2));
	}else {
		$("."+userId+"real_amount").parent().find("input[name='adjustRebate']").val("");
	}
}
function sendMoney(){
	if(_recordCount==0){
		alert("暂无数据，不可返佣");
	}else{
		
		var flag = true;
		var userArray = [];
		var reabteArray = [];
	    $("input[name='rebateUser']").each(function(){
	    	var _userId = $(this).val();
	    	var _adjustRebate = $(this).parent().parent().find("input[name='adjustRebate']").val();
	        if(_userId > 0){
	        	if(isNaN(parseFloat(_adjustRebate)) && _adjustRebate!=""){
	        		alert("金额必须为数字");
	        		flag = false;
	        		return false;
	        	}
	        	if(_adjustRebate==""||isNaN(parseFloat(_adjustRebate))){
	        		_adjustRebate = 0;
	        	}
	        	//user添加到数组
	        	userArray.push(_userId);
	        	//返佣修正金额添加到数组
	        	reabteArray.push(_adjustRebate);
	        }
	    });
			 if(flag && confirm("确定要返佣吗？")){
				 $.jsonRPC.request("adminRebateService.rebateByMonth", {
						params : [ $("#startYear").val(),$("#startMonth").val(),userArray, reabteArray],
						success : function(result) {
								if(result.list.length > 0){
									alert('发送成功');
									refreshList(1);
								}	
						}
				 });
			 }
	}
}