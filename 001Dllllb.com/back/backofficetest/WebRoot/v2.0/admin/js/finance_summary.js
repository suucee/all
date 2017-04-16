var PAGESIZE = 5;
var _pageNo = 1;

//纵向图表的label
var labels_vertical = ["1月", "2月", "3月", "4月", "5月", "6月","7月", "8月", "9月", "10月", "11月", "12月"];

//横向图表的label-入金的
var labels_Deposit_summary = ["总计","今天", "昨天", "近7天", "近30天", "本月"];
//横向图表的label-出金的
var labels_Withdrawal_summary = ["总计","待汇出","今天", "昨天", "近7天", "近30天", "本月"];


var chartData_Withdrawal = [];//图表的数据，1-12月的出金
var chartData_Withdrawal_summary = [];//图表的数据，出金概要


var chartData_Deposit = [];//图表的数据，1-12月的入金
var chartData_Deposit_summary = [];//图表的数据，入金概要


$(document).ready(function() {
	hideFinanceDeposit();
	
	
	$("#search1").hide();
	$("#search2").hide();
	
	
	//时间选择控件
	$("#startDate1").datetimepicker({
		timeFormat : "HH:mm:ss",
		dateFormat : "yy-mm-dd"
	});
	$("#endDate1").datetimepicker({
		timeFormat : "HH:mm:ss",
		dateFormat : "yy-mm-dd"
	});
	$("#startDate2").datetimepicker({
		timeFormat : "HH:mm:ss",
		dateFormat : "yy-mm-dd"
	});
	$("#endDate2").datetimepicker({
		timeFormat : "HH:mm:ss",
		dateFormat : "yy-mm-dd"
	});
	
	$("#btn1").click(function(){
		getSummarizes("admin2WithdrawalService.getSummarizes",$("#startDate1").val(),$("#endDate1").val(), "result_list1");
		$("#table1").show();
	});
	
	$("#btn2").click(function(){
		getSummarizes("admin2DepositService.getSummarizes",$("#startDate2").val(),$("#endDate2").val(), "result_list2");
		$("#table2").show();
	});
	
	getSummarizes("admin2WithdrawalService.getSummarizes",$("#startDate1").val(),$("#endDate1").val(), "result_list1");
	getSummarizes("admin2DepositService.getSummarizes",$("#startDate2").val(),$("#endDate2").val(), "result_list2");

});



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




function getSummarizes( RPCMethodName,startDate,endDate, resultListTagId){
	$.jsonRPC.request(RPCMethodName, {
		params : [startDate,endDate],
		success : function(result) {
		if (result != null) {
			if(result.list.length == 0){
				$("tbody#"+resultListTagId).html('<tr><td colspan="2">对不起，没有搜索到相应结果。</td></tr>');
				return;
			}
			var html = '';
			var schemeTag = '';
			for ( var i in result.list) {
				var item = result.list[i];
				var scheme = item.scheme;
				switch(scheme){
					case "td": schemeTag="今天";break;
					case "yd": schemeTag="昨天";break;
					case "7d": schemeTag="最近7天";break;
					case "30d": schemeTag="最近30天";break;
					case "tm": schemeTag="本月";break;
					case "tbr": schemeTag="待汇出";break;
					case "total": schemeTag="总计";break;
					default:schemeTag = item.scheme;break;
				}
				if(scheme.indexOf("EM_")>=0){//每个月
					schemeTag = scheme.substr(3,4)+"年"+(parseInt(scheme.substring(8,scheme.length))+1)+"月";
					if(RPCMethodName.indexOf("admin2WithdrawalService")>=0){
						chartData_Withdrawal.push(item.sum.toFixed(2));
					}
					else{
						chartData_Deposit.push(item.sum.toFixed(2));
					}
				}else if(scheme.indexOf("IN_")>=0){//时间间隔
					var start = scheme.substring(3,scheme.indexOf("_",3));
					var end   = scheme.substring(scheme.indexOf("_",3)+1,scheme.length);
					schemeTag = "从 "+start+" 到 "+end;
				}
				else{
					if(RPCMethodName.indexOf("admin2WithdrawalService")>=0){
						chartData_Withdrawal_summary.push(item.sum.toFixed(2));
					}
					else{
						chartData_Deposit_summary.push(item.sum.toFixed(2));
					}
				html += '<tr><td>'
						+schemeTag+'</td>'
						+ '<td class="font-red">'+item.sum.toFixed(2)+ '</td>'
					    + '</tr>';
				}
			}
			drawWithdrawalChart("myChart1_1",labels_vertical,"出金统计(USD)", chartData_Withdrawal);
			drawWithdrawalChart("myChart1_2",labels_Withdrawal_summary,"近期出金概要(USD)", chartData_Withdrawal_summary);
			
			drawDepositChart("myChart2_1",labels_vertical,"入金统计(USD)", chartData_Deposit);
			drawDepositChart("myChart2_2",labels_Deposit_summary,"近期入金概要(USD)", chartData_Deposit_summary);
		}					
	}
	});
}


function drawWithdrawalChart(chartTagId, chartLabels,topLabel,chartData){
//	var ctx = $("#"+chartTagId);
	var ctx = document.getElementById(chartTagId).getContext('2d');
	var myChart = new Chart(ctx, {
	    type: 'bar',//horizontalBar
	    data: {
	        labels: chartLabels,
	        datasets: [
	        {
	            label: topLabel,//'出金统计(USD)',
				data:chartData,
	            backgroundColor: ['red','red','red','red','red','red','red','red','red','red','red','red'],
	            borderColor: ['red'],
	            borderWidth: 1
	        }
	        ]
	    },
	    options: {
	        scales: {
	            yAxes: [{
	                ticks: {
	                    beginAtZero:true
	                }
	            }]
	        }
	    }
	});
}

function drawDepositChart(chartTagId, chartLabels,topLabel, chartData){
	var ctx = $("#"+chartTagId);
	var myChart = new Chart(ctx, {
	    type: 'bar',//horizontalBar
	    data: {
	        labels: chartLabels,
	        datasets: [
	        {
	            label: topLabel,//'出金统计(USD)',
				data:chartData,
	            backgroundColor: ['green','green','green','green','green','green','green','green','green','green','green','green'],
	            borderColor: ['green'],
	            borderWidth: 1
	        }
	        ]
	    },
	    options: {
	        scales: {
	            yAxes: [{
	                ticks: {
	                    beginAtZero:true
	                }
	            }]
	        }
	    }
	});
}
