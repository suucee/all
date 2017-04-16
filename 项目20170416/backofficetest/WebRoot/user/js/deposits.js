const LANG_SUBMIT = '继续&gt;&gt;';
const LANG_SUBMITTING = '提交中...';

$(document).ready(function() {
	getRate();	
	
	$("#btn_continue").html(LANG_SUBMIT);
	$("#btn_continue").removeAttr("disabled");
	$("#btn_continue").click(function() {
		//get
		var amount = parseFloat($("input[name='amount']").val());
		$("input[name='amount']").val(amount.toFixed(2));
		
		var realAmount = parseFloat($("input[name='amount']").val());
		//根据汇率计算最小输入金额
		amountRate = parseFloat($(".rate").text());
		if (!isNaN(amount))
		{
			$("#btn_continue").html(LANG_SUBMITTING);
			$("#btn_continue").attr("disabled", "disabled");
			
			var time= new Date();
			var order_time=toDates(time);
			
			$.jsonRPC.request('userDepositService.add', {
			    params : [amount],
			    success : function(result) {
					if (result) {	//true
						window.location.href="../zhifu/B2CDinpay.jsp?order_amount="+(amount*amountRate).toFixed(2)+"&order_time="+order_time+"&order_no="+result;
					} else {	//false
						window.alert("入金信息提交失败");
					}
					
					$("#btn_continue").html(LANG_SUBMIT);
					$("#btn_continue").removeAttr("disabled");
				}, error : function(e) {
					window.alert(e.error.msg);

					$("#btn_continue").removeAttr("disabled");
				}
			});
		}
	});
});	
//获取汇率
function getRate(){
	$.jsonRPC.request('userDepositService.getRate', {
	    params : [],
	    success : function(result) {
	    
	    	$(".rate").text(result);	
	    }
	});
}

function toDates(v) {
	var ret = new Date();
	ret.setTime(v);
	return ret.format("yyyy-MM-dd HH:mm:ss");
}