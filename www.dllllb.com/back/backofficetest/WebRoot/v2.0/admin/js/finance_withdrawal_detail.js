$(document).ready(function() {
	refreshDetail(parseInt(getParam("id")));
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
			
			$(".id").html(result.id);
			$(".state").html('<span class="label '+label_class+'">'+label_name+'</span>');
			$(".amount").html(result.amount.toFixed(2) + " " + result.currency);
			$(".creatTime").html(toDate(result.creatTime.time));
			$(".auditedTime").html(result.auditedTime == null ? '-' : toDate(result.auditedTime.time));
			$(".auditedMemo").html((result.auditedMemo == "")?"<span style='color:gray;'>-未填写-</span>":result.auditedMemo);
			$(".userMemo").html((result.userMemo == "")?"<span style='color:gray;'>-未填写-</span>":result.userMemo);
			
			//bank
			$(".countryCode").html(result.country);
			$(".bankName").html(result.bankName);
			$(".accountName").html(result.accountName);
			$(".accountNo").html(result.accountNumber == '' ? result.iban : result.accountNumber);
			$(".bankBranch").html(result.bankBranch);
			$(".bankAddress").html(result.bankAddress);
			$(".bicSwiftCode").html(result.swiftCode);
			$(".bicIBANCode").html(result.ibanCode);
			if (result.country == 'CN') {
				$(".bank_branch").show();
				$(".bank_address").show();
				$(".Swift_Code").hide();
				$(".IBAN_Code").hide();
			}else{
				$(".Swift_Code").show();
				$(".IBAN_Code").show();
				$(".bank_branch").hide();
				$(".bank_address").hide();
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
