var order = null;
$(function() {
	order = new MT4Order();
});

var MT4Order = function() {
	this.init();
};

MT4Order.prototype = {
	init : function() {
		$.jsonRPC.request("adminRiskService.getMt4OrdersCountMt4Login", {
			params : [],
			success : function(result) {
				var html = '';
				$(".update_time").text(toDate(result.map.update_time.time));
				var list=result.map.list.list;
				for (var i = 0; i < list.length; i++) {
					var the = list[i];
					var item = the.mt4Trades;
					var lable_css = "";
					var cmd_name = "";
					var reason_name = "";
					switch (item.cmd) {
					case 0:
						cmd_name = 'BUY';
						break;
					case 1:
						cmd_name = 'SELL';
						break;
					case 2:
						cmd_name = 'BUY_LIMIT';
						break;
					case 3:
						cmd_name = 'SELL_LIMIT';
						break;
					case 4:
						cmd_name = 'BUY_STOP';
						break;
					case 5:
						cmd_name = 'SELL_STOP';
						break;
					case 6:
						cmd_name = 'BALANCE';
						break;
					case 7:
						cmd_name = 'CREDIT';
						break;
					default:
						cmd_name = '';
						break;
					}
					switch (item.reason) {
					case 0:
						reason_name = 'client';
						break;
					case 1:
						reason_name = 'expert';
						break;
					case 2:
						reason_name = 'dealer';
						break;
					case 3:
						reason_name = 'signal';
						break;
					case 4:
						reason_name = 'gateway';
						break;
					case 5:
						reason_name = 'mobile';
						break;
					case 6:
						reason_name = 'Web';
						break;
					case 7:
						reason_name = 'API';
						break;
					default:
						reason_name = '';
						break;
					}
					var lable_css_commission = '';
					var lable_css_storage = '';

					if (item.profit < 0) {
						lable_css = "color:#b94a48;";
					}
					if (item.commission < 0) {
						lable_css_commission = "color:#b94a48;";
					}
					if (item.storage < 0) {
						lable_css_storage = "color:#b94a48;";
					}
					html += '<tr>' 
					+ '<td>' + (the.login==null?"--":the.login)+ ' </td>' 
					+ '<td class="amount bold" style="' + lable_css + '">' + (item.profit != 0 ? item.profit.toFixed(2) : '' ) + '</td>' 
					+ '<td class="amount bold">' + the.orderCount + ' </td>' 
					+ '<td class="amount bold">' + (item.cmd < 2 && item.volume > 0 ? (item.volume / 100).toFixed(2) : '') + ' </td>' 
					+ '<td class="amount bold" style="' + lable_css_commission + '">' + (item.commission != 0 ? item.commission.toFixed(2) : '') + ' </td>' 
					+ '<td class="amount bold" style="' + lable_css_storage + '">' + (item.storage != 0 ? item.storage.toFixed(2) : '') + ' </td>' 
					+ '</tr>';
				}
				$("#result_list").html(html);
			}
		});
	}
};
