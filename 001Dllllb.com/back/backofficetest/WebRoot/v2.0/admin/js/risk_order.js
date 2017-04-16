var order = null;
$(function() {
	order = new MT4Order();
	$("select[name=login]").change(function() {
		$(".table-serach").hide();
		$(".table-serach").each(function(index, element) {
			if ($("select[name=login]").val() != 0&&$("select[name=symbol]").val() != 0) {
				if ($(element).data("login") == $("select[name=login]").val()&&$(element).data("symbol") == $("select[name=symbol]").val()) {
					$(element).show();
				}
			} else if ($("select[name=login]").val() == 0&&$("select[name=symbol]").val() != 0) {
				if ($(element).data("symbol") == $("select[name=symbol]").val()) {
					$(element).show();
				}
			}else if ($("select[name=login]").val() != 0&&$("select[name=symbol]").val() == 0) {
				if ($(element).data("login") == $("select[name=login]").val()) {
					$(element).show();
				}
			}else{
			    $(".table-serach").show();
			}
		});
	});
	$("select[name=symbol]").change(function() {
		$(".table-serach").hide();
		$(".table-serach").each(function(index, element) {
			if ($("select[name=login]").val() != 0&&$("select[name=symbol]").val() != 0) {
				if ($(element).data("login") == $("select[name=login]").val()&&$(element).data("symbol") == $("select[name=symbol]").val()) {
					$(element).show();
				}
			} else if ($("select[name=login]").val() == 0&&$("select[name=symbol]").val() != 0) {
				if ($(element).data("symbol") == $("select[name=symbol]").val()) {
					$(element).show();
				}
			}else if ($("select[name=login]").val() != 0&&$("select[name=symbol]").val() == 0) {
				if ($(element).data("login") == $("select[name=login]").val()) {
					$(element).show();
				}
			}else{
			    $(".table-serach").show();
			}
		});
	});
});

var MT4Order = function() {
	this.init();
};

MT4Order.prototype = {
	init : function() {
		$.jsonRPC.request("adminRiskService.getAllMt4Orders", {
			params : [],
			success : function(result) {
				var html = '';
				var loginarr = new Array();
				var symbolarr = new Array();
				$(".update_time").text(toDate(result.map.update_time.time));
				var list=result.map.list.list;
				for (var i = 0; i < list.length; i++) {
					var the = list[i];
					var haslogin = false;
					var hassymbol = false;
					for (var l = 0; l < loginarr.length; l++) {
						var ll = loginarr[l];
						if (ll == the.login) {
							haslogin = true;
						}
					}
					if (!haslogin) {
						loginarr.push(the.login);
					}
					for (var s = 0; s < symbolarr.length; s++) {
						var ss = symbolarr[s];
						if (ss == the.symbol) {
							hassymbol = true;
						}
					}
					if (!hassymbol) {
						symbolarr.push(the.symbol);
					}
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
					html += '<tr class="table-serach" data-login="' + the.login + '"  data-symbol="' + the.symbol + '"><td>' + item.order + '</td>' 
					+ '<td>' + (the.userName == null ? "--" : the.userName) + ' </td>' 
					+ '<td><a href="javascript:changeLogin(' + item.login + ');">' + item.login + '</a></td>' 
					+ '<td>' + toDate(item.openTime * 1000) + ' </td>' + '<td>' + cmd_name + ' </td>' 
					+ '<td>' + (item.symbol != "" ? item.symbol : '<font style="color:#b94a48;">' + item.comment + '</font>') + ' </td>' 
					+ '<td class="amount bold">' + (item.cmd < 2 && item.volume > 0 ? (item.volume / 100).toFixed(2) : '') + ' </td>' 
					+ '<td class="amount bold">' + (item.openPrice > 0 ? item.openPrice.toFixed(item.digits) : '') + '</td>' 
					+ '<td class="amount bold" style="' + lable_css_commission + '">' + (item.commission != 0 ? item.commission.toFixed(2) : '') + ' </td>' 
					+ '<td class="amount bold" style="' + lable_css_storage + '">' + (item.storage != 0 ? item.storage.toFixed(2) : '') + ' </td>' 
					+ '<td class="amount bold" style="' + lable_css + '">' + (item.profit != 0 ? item.profit.toFixed(2) : '' ) + '</td>' 
					+ '</tr>';
				}
				if (html=="") {
				     html += '<tr><td colspan="10" class="bold">对不起，暂未检索到相关记录</td></tr>';
				}
				$("#result_list").html(html);

				for (var l = 0; l < loginarr.length; l++) {
					$("select[name=login]").append('<option value="' + loginarr[l] + '">' + loginarr[l] + '</option>');
				}
				for (var s = 0; s < symbolarr.length; s++) {
					$("select[name=symbol]").append('<option value="' + symbolarr[s] + '">' + symbolarr[s] + '</option>');
				}

			},
		error:function(error){
			   var  html = '<tr><td colspan="10" class="bold">对不起，暂未检索到相关记录</td></tr>';
				$("#result_list").html(html);
			}
		});
	}
};

function changeLogin(login){
	$("select[name=login]").val(login);
	$("select[name=login]").change();
}
