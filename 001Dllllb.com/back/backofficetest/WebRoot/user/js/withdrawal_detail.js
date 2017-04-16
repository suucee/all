$(document).ready(function() {
	refreshDetail(parseInt(window.location.hash.substr(1)));
});

function refreshDetail(id) {
	$.jsonRPC.request("userWithdrawalService.getById",{
		params:[id],
		success:function(result){
		if (result != null) {
			var label_class = '';
			var label_name = '';

			switch (result.state) {
			case 'WAITING':
			case 'AUDITED':
				label_class = 'label-pending';
				label_name = 'Pending';
				show_cancel = true;
				break;
			/*case 'AUDITED':
				label_class = 'label-important';
				label_name = 'Transferring Accepted';
				break;
			*/case 'REMITTED':
				label_class = 'label-success';
				label_name = 'Remitted';
				break;
			case 'REJECTED':
				label_class = 'label-cancel';
				label_name = 'Rejected';
				show_cancel = true;
				break;
			case 'RETURNED':
				label_class = 'label-cancel';
				label_name = 'Bank Returned';
				show_cancel = true;
				break;
			case 'CANCELED':
				label_class = 'label-pending';
				label_name = 'User Canceled';
				break;
			}
			
			$(".id").html(result.id);
			$(".state").html('<span class="label '+label_class+'">'+label_name+'</span>');
			$(".amount").html(result.amount.toFixed(2) + " " + result.currency);
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
