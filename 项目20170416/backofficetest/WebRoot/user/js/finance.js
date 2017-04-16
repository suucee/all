
$(document).ready(function() {
	getSummarizes();
});

function getSummarizes() {
	$.jsonRPC.request("adminDepositService.getSummarizes",
			{params : [],
				success : function(result) { 
	if (result) {
		for (var i in result.list) {
			var item = result.list[i];
			
		    $(".d_"+item.scheme).text(item.sum.toFixed(2));
		}
	}
				}
	});
	
	$.jsonRPC.request("adminWithdrawalService.getSummarizes",
			{params : [],
				success : function(result) { 
	if (result) {
		for (var i in result.list) {
			var item = result.list[i];
			$(".w_"+item.scheme).text(item.sum.toFixed(2));
		}
	}
				}
	});
	
}

function cc(s){
    if(/[^0-9\.]/.test(s)) return "invalid value";
    s=s.replace(/^(\d*)$/,"$1.");
    s=(s+"00").replace(/(\d*\.\d\d)\d*/,"$1");
    s=s.replace(".",",");
    var re=/(\d)(\d{3},)/;
    while(re.test(s))
            s=s.replace(re,"$1,$2");
    s=s.replace(/,(\d\d)$/,".$1");
    var a="$" + s.replace(/^\./,"0.");
    }