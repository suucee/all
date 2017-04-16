const PAGESIZE = 20;

var scheme="";
$(document).ready(function() {
	scheme=getParam("scheme", "all");
	 switch (scheme) {
	case "all":
		$(".title").text("所有入金记录列表");
		break;
	case "pending_pay":
		$(".title").text("待付款入金列表");
		break;
	case "deposited":
		$(".title").text("已到账入金列表");
		break;
	case "pending_supervisor":
		$(".title").text("待审核代为入金列表");
		break;
	default:
		$(".title").text("入金列表");
		break;
	}
	$("#popup_container").load("_popup_deposit_detail.html");
	$("#startDate").datetimepicker({
        timeFormat: "HH:mm:ss",
        dateFormat: "yy-mm-dd"
    });
	$("#endDate").datetimepicker({
        timeFormat: "HH:mm:ss",
        dateFormat: "yy-mm-dd"
    });
	$("#go").click(function(){
		refreshList(1);
	});
	$("#startDate").change(function(){
		refreshList(1);
	});
	$("#endDate").change(function(){
		refreshList(1);
	});
	$("."+scheme+"link").addClass('btn-selected');
	refreshList(1);
	
});

function refreshList(pageNo) {
		var userid = getParam("userid", "0");
		var scheme = getParam("scheme", "all");
		$.jsonRPC.request("adminDepositService.getPage", {
			params : [userid,pageNo, PAGESIZE, "javascript:refreshList(??);", 
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
						case 'DEPOSITED':
							label_class = 'label-success';
							label_name = '已到账';
							break;
						case 'PENDING_PAY':
							label_class = 'label-pending';
							label_name = '待付款';
							break;
						case 'PENDING_AUDIT':
							label_name = '待审核';
							break;
						case 'ACCEPTED':
							label_name = '已审核';
							break;
						case 'REJECTED':
							label_class = 'label-important';
							label_name = '已驳回';
							break;
						case 'PENDING_SUPERVISOR':
							label_name = '待财务主管审核';
							show_remove = true;
							break;
						}
						html += '<tr><td>' + item.id + '</td>'
								+ '<td><span class="label '	+ label_class + '">' + label_name + '</span></td>'
								+ '<td>' +item._userName + '</td>'
								+ '<td class="amount bold">' + item.amount.toFixed(2) + '</td>'
								+ '<td>' + item.orderNum + ' </td>'
								+ '<td>' + (item.paymentTime== null ? '--' : toDate(item.paymentTime.time)) + '</td>'
								+ '<td>' + (item.state =="PENDING_SUPERVISOR" ?  '<a href="javascript:openPopup('+item.id+');"><span class="btn btn-primary">操作</span></a>': '--') + '</td>'
							    + '</tr>';
					}
					// page
					if(result.buttons != ""){
						html += '<tr><td colspan="7"><div class="pagelist">'+ result.buttons + '</td></tr>';
					}
		
				
				$("tbody#result_list").html(html);
							
				}					
			}
		});
}
function openPopup(id) {
	$.jsonRPC.request("adminDepositService.getOne",{
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
				newState += '<option value="DEPOSITED">通过</option>';
				newState += '<option value="REJECTED">驳回</option>';
				break;
			case 'DEPOSITED':
				label_class = 'label-success';
				label_name = '已到账';
				break;
			case 'PENDING_AUDIT':
				label_class = 'label-pending';
				label_name = '待审核';
				break;
			case 'REJECTED':
				label_class = 'label-important';
				label_name = '已驳回';
				break;
			}
			
			$(".id").html(result.id);
			$(".state").html('<span class="label '+label_class+'">'+label_name+'</span>');
			$(".popamount").html(result.amount.toFixed(2) + " " + result.currency);
			$(".orderNum").html(result.orderNum);
			$("#newState").html(newState);
			$(".creatTime").html(toDate(result.creatTime.time));
			$(".payTime").html(toDate(result.paymentTime.time));
			$(".userComment").html(result.userComment);
			$(".auditedTime").html(toDate(result.auditedTime.time));
			$(".auditedMemo").html(result.auditedMemo);
			
	}
		}
	});
	$(".popup_dialog").fadeIn(200);
}
//function closePopup() {
//	$(".popup_dialog").fadeOut(200);
//}
//function doPopup(){
//	var id=$(".id").text();
//	var newState=$("#newState").val();
//	var auditedMemo=$(".auditedMemo").html();
//	var operationPassword=$("#operationPassword").val();
//	$("#newState").css("background", "white");
//	$("#operationPassword").css("background", "white");
//	if(newState==""){
//		$("#newState").css("background", "#ffff80");
//		$("#newState").focus();
//	}else if (operationPassword=="") {
//		$("#operationPassword").css("background", "#ffff80");
//		$("#operationPassword").focus();
//	}else {
//		$.jsonRPC.request("adminDepositService.changeState",{
//			params:[id,operationPassword,newState,auditedMemo],
//			success:function(result){
//				layer.msg(result,{time:1000});
//				closePopup();	
//				refreshList(_pageNo);
//				getCounts();
//			}
//		});
//	}
//}