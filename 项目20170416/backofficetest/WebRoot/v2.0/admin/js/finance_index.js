var PAGESIZE = 5;
var _pageNo1 = 1;
var _pageNo2 = 1;
$(document).ready(function() {
	
	hideFinanceDeposit();
	
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
		refreshList1(1);
	});
	
	$("#btn2").click(function(){
		refreshList2(1);
	});
	
	
	$("#startDate1").change(function(){refreshList1(1);});
	$("#endDate1").change(function(){refreshList1(1);});
	$("#state1").change(function(){refreshList1(1);});
	
	$("#startDate2").change(function(){refreshList2(1);});
	$("#endDate2").change(function(){refreshList2(1);});
	$("#state2").change(function(){refreshList2(1);});
	
	refreshList1(1);
	refreshList2(1);
	
	
//	getSummarizes();
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



function refreshList1(pageNo) {
	$("tbody#result_list1").html('<tr><td colspan="7">正在搜索...</td></tr>');
	var startDate = $("#startDate1").val();
	var endDate = $("#endDate1").val();
	var keyword = $("#keyword1").val();
	var scheme = $("#state1").val();
	getPage1(0, pageNo, PAGESIZE, "javascript:refreshList1(??);", scheme,startDate,endDate,keyword,"finance_check_withdrawal.html","审核");
}


function refreshList2(pageNo) {
	$("tbody#result_list2").html('<tr><td colspan="7">正在搜索...</td></tr>');
	var startDate = $("#startDate2").val();
	var endDate = $("#endDate2").val();
	var keyword = $("#keyword2").val();
	var scheme = $("#state2").val();
	getPage2(0, pageNo, PAGESIZE, "javascript:refreshList2(??);", scheme,startDate,endDate,keyword);
}

//获取待汇款记录
function getPage1(userId, pageNo, PAGESIZE, urlFormat, scheme,startDate,endDate,keyword){
	$.jsonRPC.request("admin2WithdrawalService.getPage", {
		params : [0, pageNo, PAGESIZE, urlFormat, scheme, startDate, endDate, keyword],
		success : function(result) { 
		if (result != null) {
			if(result.list.list.length == 0){
				$("tbody#result_list1").html('<tr><td colspan="7">对不起，没有搜索到相应结果。</td></tr>');
				return;
			}
			_pageNo1 = pageNo;
			var html = '';
			for ( var i in result.list.list) {
				
				var item = result.list.list[i];
				var label_class = "";
				var label_name = "";
				
				switch (item.state) {
				case 'WAITING':
					label_class = 'label-pending';
					label_name = '待审核';
					show_cancel = true;
					break;
				case 'PENDING_SUPERVISOR':
					label_class = 'label-pending';
					label_name = '待财务主管审核';
					break;
				case 'AUDITED':
					label_class = 'label-pending';
					label_name = '待汇款';
					break;
				case 'REMITTED':
					label_class = 'label-success';
					label_name = '已汇款';
					break;
				case 'REJECTED':
					label_class = 'label-cancel';
					label_name = '已驳回';
					show_cancel = true;
					break;
				case 'BACK':
					label_class = 'label-cancel';
					label_name = '银行退回';
					show_cancel = true;
					break;
				case 'CANCELED':
					label_class = 'label-cancel';
					label_name = '客户取消';
					break;
				}
				
				var disable_html = '';
				var frozen_html = '';
				var edit_html = '';
				
				var pass = '<a  class="btn btn-medium btn-primary"  onclick="parent.openNewWindow('+"'./../admin/finance_check_withdrawal.html?id="+item.id+"'"+',updateList1)">审核</a>';
				
				html += '<tr>'
						+ '<td>#'+item.id+'</td>'
						+ '<td><span class="label '	+ label_class + '">' + label_name + '</span></td>'
						+ '<td>' +(item._userName != "" ? item._userName : '(暂无姓名)')+(item.disable ? ' <span class="red">[禁]</span>' : '') + (item.is_frozen ? ' <span class="purple">[冻]</span>' : '')+ '</td>'
						+ '<td>' + item.accountName + '</td>'
						+ '<td class="font-red">' + item.amount.toFixed(2) + '</td>'
						+ '<td>' + toDate(item.creatTime.time) + '</td>'
						+ '<td>'+checkRole(new Array("FinancialStaff","FinancialSuperior","OperationsManager"), pass, null)+'</td>'
					    + '</tr>';
			}
			// page
			if(result.buttons!=""){
				html += '<tr><td colspan="7"><div class="pagelist">'+ result.buttons + '</td></tr>';
			}
			$("tbody#result_list1").html(html);
		}					
	}
	});
}


//获取待汇款记录
function getPage2(userId, pageNo, PAGESIZE, urlFormat, scheme,startDate,endDate,keyword){
	$.jsonRPC.request("admin2WithdrawalService.getPage", {
		params : [0, pageNo, PAGESIZE, urlFormat, scheme, startDate, endDate, keyword],
		success : function(result) { 
		if (result != null) {
			if(result.list.list.length == 0){
				$("tbody#result_list2").html('<tr><td colspan="7">对不起，没有搜索到相应结果。</td></tr>');
				return;
			}
			_pageNo2 = pageNo;
			var html = '';
			for ( var i in result.list.list) {
				
				var item = result.list.list[i];
				var label_class = "";
				var label_name = "";
				var operation = '<a class="btn btn-a" href="javascript:parent.openNewWindow('+"'./../admin/finance_withdrawal_detail.html?id="+item.id+"'"+',donothing)">查看详情</a>';
				switch (item.state) {
				case 'WAITING':
					label_class = 'label-pending';
					label_name = '待审核';
					show_cancel = true;
					break;
				case 'PENDING_SUPERVISOR':
					label_class = 'label-pending';
					label_name = '待财务主管审核';
					break;
				case 'AUDITED':
					label_class = 'label-pending';
					label_name = '待汇款';
					operation = '<a class="btn btn-a"  onclick="parent.openNewWindow('+"'./../admin/finance_check_remittance.html?id="+item.id+"'"+',updateList2)">汇款确认</a>';
					break;
				case 'REMITTED':
					label_class = 'label-success';
					label_name = '已汇款';
					break;
				case 'REJECTED':
					label_class = 'label-cancel';
					label_name = '已驳回';
					show_cancel = true;
					break;
				case 'BACK':
					label_class = 'label-cancel';
					label_name = '银行退回';
					show_cancel = true;
					break;
				case 'CANCELED':
					label_class = 'label-cancel';
					label_name = '客户取消';
					break;
				}
				
				var disable_html = '';
				var frozen_html = '';
				var edit_html = '';
				
				html += '<tr>'
						+ '<td>#'+item.id+'</td>'
						+ '<td><span class="label '	+ label_class + '">' + label_name + '</span></td>'
						+ '<td>' +(item._userName != "" ? item._userName : '(暂无姓名)')+(item.disable ? ' <span class="red">[禁]</span>' : '') + (item.is_frozen ? ' <span class="purple">[冻]</span>' : '')+ '</td>'
						+ '<td>' + item.accountName + '</td>'
						+ '<td class="font-red">' + item.amount.toFixed(2) + '</td>'
						+ '<td>' + toDate(item.creatTime.time) + '</td>'
						+ '<td>'+checkRole(new Array("FinancialStaff","FinancialSuperior","OperationsManager"), operation, null)+'</td>'
					    + '</tr>';
			}
			// page
			if(result.buttons!=""){
				html += '<tr><td colspan="7"><div class="pagelist">' + result.buttons + '</td></tr>';
			}
			$("tbody#result_list2").html(html);
		}					
	}
	});
}

function updateList1(){
	refreshList1(_pageNo1);
	refreshList2(_pageNo2);
}
function updateList2(){
	refreshList2(_pageNo2);
}
function donothing(){}


