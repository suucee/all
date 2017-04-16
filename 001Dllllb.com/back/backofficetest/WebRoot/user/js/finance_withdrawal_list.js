const PAGESIZE = 20;

var _pageNo=1;
var scheme="";
$(document).ready(function() {
	scheme=getParam("scheme", "all");
	 switch (scheme) {
	case "all":
		$(".title").text("所有出金记录列表");
		break;
	case "pending":
		$(".title").text("待审核出金列表");
		break;
	case "audited":
		$(".title").text("待汇款出金列表");
		break;
	case "remitted":
		$(".title").text("已汇款出金列表");
		break;
	case "rejected":
		$(".title").text("已驳回出金列表");
		break;
	case "returned":
		$(".title").text("银行退回出金列表");
		break;
	case "canceled":
		$(".title").text("用户取消出金列表");
		break;
	case "pending_supervisor":
		$(".title").text("待审核代为入金列表");
		break;
	
	default:
		$(".title").text("入金列表");
		break;
	}
	$("#popup_container_detail").load("_popup_withdrawal_detail.html");
	$("#popup_container").load("_popup_withdrawal.html");
	$("#startDate").datetimepicker({
        timeFormat: "HH:mm:ss",
        dateFormat: "yy-mm-dd"
    });
	$("#endDate").datetimepicker({
        timeFormat: "HH:mm:ss",
        dateFormat: "yy-mm-dd"
    });
	
	$("#startDate").change(function(){
		refreshList(1);
	});
	$("#endDate").change(function(){
		refreshList(1);
	});
	
	refreshList(1);
	
});

function refreshList(pageNo) {
		var userid = getParam("userid", "0");
		var scheme = getParam("scheme", "all");
		$.jsonRPC.request("adminWithdrawalService.getPage",
				{params : [userid,pageNo, PAGESIZE, "javascript:refreshList(??);", 
							scheme, $("#startDate").val(), $("#endDate").val()],
					success : function(result) { 
		if (result != null) {
			_pageNo = pageNo;
			var html = '';
			for ( var i in result.list.list) {
				var item = result.list.list[i];
				var label_class = "";
				var label_name = "";
				var show_remove = false;
				
				switch (item.state) {
				case 'WAITING':
					label_class = 'label-pending';
					label_name = '待审核';
					break;
				case 'PENDING_SUPERVISOR':
					label_class = 'label-pending';
					label_name = '待财务主管审核';
					show_remove = true;
					break;
				case 'AUDITED':
					label_class = 'label-important';
					label_name = '待汇款';
					show_remove = true;
					break;
				case 'REJECTED':
					label_class = 'label-important';
					label_name = '已拒绝';
					break;
				case 'REMITTED':
					label_class = 'label-success';
					label_name = '已汇出';
					show_remove = true;
					break;
				case 'BACK':
					label_class = 'label-important';
					label_name = '银行退回';
					show_remove = true;
					break;
				case 'CANCELED':
					label_class = 'label-cancel';
					label_name = '用户撤销';
					break;
				}
				html += '<tr>'
					+ '    <td><a href="javascript:doDetailPopup('+item.id+');">#'+item.id+'</a></td>' 
					+ '    <td><span class="label '+label_class+'">'+label_name+'</span></td>'
					+ '    <td><a href="user_detail.html?id='+item._userId+'" target="_blank">'+item._userName+'</a></td>'
					+ '    <td>'+item.accountName+'</td>'
					+ '    <td class="amount bold">'+item.amount.toFixed(2)+' '+item.currency+'</td>'
					+ '    <td>'+toDate(item.creatTime.time)+'</td>'
					+ '    <td>'+(item.auditedTime == null ? ' - ' : toDate(item.auditedTime.time))+'</td>'
					+ '    <td><a href="javascript:doDetailPopup('+item.id+');"><span class="btn btn-primary">详细</span></a>&nbsp;&nbsp;&nbsp;&nbsp;'+(show_remove ?  '  <a href="javascript:openPopup('+item.id+');"><span class="btn btn-primary">操作</span></a>'  :  '' )+'</td>'
					+ '</tr>';
			}
			
			// page
			html += '<tr><td colspan="8"><div class="pagelist">'
					+ result.buttons + '</td></tr>';

		
		$("tbody#result_list").html(html);
					
		}					
	}
		});
}
function  doDetailPopup(id) {
	$.jsonRPC.request("adminWithdrawalService.getOne",{
		params:[id],
		success:function(result){
		if (result != null) {
			var label_class = '';
			var label_name = '';

			switch (result.state) {
			case 'WAITING':
				label_class = 'label-pending';
				label_name = '待审核';
				break;
			case 'PENDING_SUPERVISOR':
				label_class = 'label-pending';
				label_name = '待财务主管审核';
				break;
			case 'AUDITED':
				label_class = 'label-important';
				label_name = '待汇款';
				break;
			case 'REJECTED':
				label_class = 'label-important';
				label_name = '已拒绝';
				break;
			case 'REMITTED':
				label_class = 'label-success';
				label_name = '已汇出';
				break;
			case 'BACK':
				label_class = 'label-important';
				label_name = '银行退回';
				break;
			case 'CANCELED':
				label_class = 'label-cancel';
				label_name = '用户撤销';
				break;
			}
			
			$(".id").html(result.id);
			$(".state").html('<span class="label '+label_class+'">'+label_name+'</span>');
			$(".detailAmount").html(result.amount.toFixed(2) + " " + result.currency);
			$(".detailAmountCNY").html((result.amount*result.exchangeRate).toFixed(2) + "  CNY");
			$(".exchangeRate").html(result.exchangeRate);
			$(".creatTime").html(toDate(result.creatTime.time));
			$(".auditedTime").html(result.auditedTime == null ? '-' : toDate(result.auditedTime.time));
			$(".auditedMemo").html(result.auditedMemo);
			$(".userMemo").html(result.userMemo);
			
			//bank
			$(".countryCode").html(result.country);
			$(".bankName").html(result.bankName);
			$(".accountName").html(result.accountName);
			$(".accountNo").html(result.accountNumber == '' ? result.iban : result.accountNumber);
			$(".bankBranch").text(result.bankBranch);
			$(".bankAddress").text(result.bankAddress);
			$(".swiftCode").text(result.swiftCode);
			$(".ibanCode").text(result.ibanCode);
			if(result.country=="CN"){
		    	$(".home").show();
		    	$(".abroad").hide();
		    }else if(result.country!=null&&result.country!=""){
		    	$(".home").hide();
		    	$(".abroad").show();
		    }
		} 
	}
	});
	$(".withdrawal_detail").fadeIn(200);
}
function closeDetailPopup() {
	$(".withdrawal_detail").fadeOut(200);
}
function closePopup() {
	$(".do_withdrawal").fadeOut(200);
}
function openPopup(id) {
	$.jsonRPC.request("adminWithdrawalService.getOne",{
		params:[id],
		success:function(result){
		if (result != null) {
			var label_class = '';
			var label_name = '';
			var newState = '<option value="">[暂时无法操作]</option>';

			switch (result.state) {
			case 'PENDING_SUPERVISOR':
				label_class = 'label-pending';
				label_name = "待财务主管审核";
				newState += '<option value="AUDITED">通过</option>';
				newState += '<option value="REJECTED">驳回</option>';
				break;
			case 'AUDITED':
				label_class = 'label-important';
				label_name = "待汇款";
				newState += '<option value="REJECTED">驳回</option>';
				newState += '<option value="REMITTED">已汇出</option>';
				break;
			case 'REMITTED':
				label_class = 'label-success';
				label_name = "已汇出";
				newState += '<option value="BACK">银行退回</option>';
				break;
			case 'BACK':
				label_class = 'label-important';
				label_name = "银行退回";
				newState += '<option value="REMITTED">已汇出</option>';
				break;
			}
			
			$(".withdrawal_id").html(result.id);
			$("#newState").html(newState);
			$("#nowState").val(result.state);
			$(".oldState").html('<span class="label '+label_class+'">'+label_name+'</span>');
			$(".popamount").html(result.amount.toFixed(2) + " " + result.currency);
			$("#auditedMemo").html(result.auditedMemo);
	}
		}
	});
	$(".do_withdrawal").fadeIn(200);
}
function doPopup(){
	var id=$(".withdrawal_id").html();
	var nowState=$("#nowState").val();
	var newState=$("#newState").val();
	var auditedMemo=$("#auditedMemo").html();
	var operationPassword=$("#operationPassword").val();
	$("#newState").css("background", "white");
	$("#operationPassword").css("background", "white");
	if(newState==""){
		$("#newState").css("background", "#ffff80");
		$("#newState").focus();
	}else if (operationPassword=="") {
		$("#operationPassword").css("background", "#ffff80");
		$("#operationPassword").focus();
	}else {
		$.jsonRPC.request("adminWithdrawalService.changeState",{
			params:[id,nowState,operationPassword,newState,auditedMemo],
			success:function(result){
				
				alert(result);
				closePopup();	
				refreshList(_pageNo);
				getCounts();
			}
		});
	}
}