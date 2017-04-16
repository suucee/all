$(document).ready(function() {
	refreshDetail(parseInt(getParam("id", 0)));
	$("#print").click(function () {
		$("#printTable").jqprint();
	})
});

function refreshDetail(id) {
	$.jsonRPC.request("admin2WithdrawalService.getOne",{
		params:[id],
		success:function(result){
		if (result != null) {
			var label_class = '';
			var label_name = '';

			switch (result.state) {
			case 'WAITING':
				label_class = 'label-pending';
				label_name = '待审核';
				show_cancel = true;
				break;
			case 'AUDITED':
				label_class = 'label-pending';
				label_name = '待汇款';
				show_cancel = true;
				break;
			case 'PENDING_SUPERVISOR':
				label_class = 'label-pending';
				label_name = '待审核(大额出金)';
				show_cancel = true;
				break;
			case 'REMITTED':
				label_class = 'label-success';
				label_name = '已汇出';
				break;
			case 'REJECTED':
				label_class = 'label-cancel';
				label_name = '已驳回';
				show_cancel = true;
				break;
			case 'RETURNED'://兼容旧版本
			case 'BACK':
				label_class = 'label-cancel';
				label_name = '银行退回';
				show_cancel = true;
				break;
			case 'CANCELED':
				label_class = 'label-pending';
				label_name = '已取消';
				break;
			}
			
			$(".id").html("#"+result.id);
			$(".state").html('<span class="label '+label_class+'">'+label_name+'</span>');
			$(".amount").html(USD_ICO+result.amount.toFixed(2));
			$(" .amount_CNY").html(CNY_ICO+ (result['amount'] * result['exchangeRate']).toFixed(2));/////////等价CNY金额/////////////
			$(" .rate").text(result['exchangeRate']);//////////出金汇率///////////
			$(".creatTime").html(toDate(result.creatTime.time));
			$(".auditedTime").html(result.auditedTime == null ? '-' : toDate(result.auditedTime.time));
			$(".auditedMemo").html(result.auditedMemo);
			$(".userMemo").html(result.userMemo);
			
			//bank
			$(".countryCode").html(result.country);
			$(".bankName").html(result.bankName);
			$(".accountName").html(result.accountName);
			$(".accountNo").html(result.accountNumber == '' ? result.iban : result.accountNumber);
			$(".bicSwiftCode").html(result.swiftCode);
			if (result.country == 'HK') {
				$(".show_hk").show();
			}
			//inter
			$(".intermediaryBankName").text(result.intermediaryBankName);
			$(".intermediaryBankBranch").text(result.intermediaryBankBranch);
			//$(".intermediaryBankAddress").text(result.intermediaryBankAddress);
			$(".intermediaryBicSwiftCode").text(result.intermediaryBicSwiftCode);
			
			
		} else {
			
		}
	}
	});
}
