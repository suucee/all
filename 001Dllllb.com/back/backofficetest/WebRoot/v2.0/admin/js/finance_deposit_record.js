
const closeDIV = '<div class="mini-layout-opacilty w980">'
				+'<button class="btn btn-f float-left" onclick="parent.closeWindow()"><i class="fa fa-chevron-left margin-right10"></i>返回</button>'
				+'<button class="btn btn-f float-right margin-right25" onclick="parent.closeWindow()"><i class="fa fa-remove margin-right10"></i>关闭</button>'
				+'<div class="clear"></div>'
				+'</div>';

var PAGESIZE = 10;
var _pageNo = 1;
var scheme;
var userid = 0;
$(document).ready(function() {
	hideFinanceDeposit();
	scheme = getParam("scheme", undefined);
	userid = getParam("userid", 0);
	if(scheme !== undefined && userid !== 0){
		//移除tab标签
		$(".container div:first").remove();
		//加上关闭和返回
		$(".container").prepend(closeDIV);
		refreshListByParam(1);
	}else{
		refreshList1(1);
	}
	//时间选择控件
	$("#startDate1").datetimepicker({
		timeFormat : "HH:mm:ss",
		dateFormat : "yy-mm-dd"
	});
	$("#endDate1").datetimepicker({
		timeFormat : "HH:mm:ss",
		dateFormat : "yy-mm-dd"
	});
	
	$("#btn1").click(function(){
		refreshList1(1);
	});
	
	$("#startDate1").change(function(){refreshList1(1);});
	$("#endDate1").change(function(){refreshList1(1);});
	$("#state1").change(function(){refreshList1(1);});
	
	
	
	
//	getSummarizes();
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



function refreshList1(pageNo) {
	$("tbody#result_list1").html('<tr><td colspan="8">正在搜索...</td></tr>');
	var startDate = $("#startDate1").val();
	var endDate = $("#endDate1").val();
	var keyword = $("#keyword1").val();
	var scheme = $("#state1").val();
	getPage(userid, pageNo, PAGESIZE, "javascript:refreshList1(??);", scheme,startDate,endDate,keyword,"result_list1");
}

function refreshListByParam(pageNo) {
	$("tbody#result_list1").html('<tr><td colspan="8">正在搜索...</td></tr>');
	var startDate = $("#startDate1").val();
	var endDate = $("#endDate1").val();
	getPage(userid, pageNo, PAGESIZE, "javascript:refreshListByParam(??);", scheme,startDate,endDate,"","result_list1");
}



//getPage(int userId,int pageNo, int pageSize, String urlFormat,
//String state, String start, String end, String keyword, boolean replacement,
function getPage(userId, pageNo, PAGESIZE, urlFormat, scheme,startDate,endDate,keyword,resultListTagId){
	$.jsonRPC.request("admin2DepositService.getPage", {
		params : [userId, pageNo, PAGESIZE, urlFormat, scheme, startDate, endDate, keyword,false],
		success : function(result) { 
		if (result != null) {
			if(result.list.list.length == 0){
				$("tbody#"+resultListTagId).html('<tr><td colspan="8">对不起，没有搜索到相应结果。</td></tr>');
				return;
			}
			_pageNo = pageNo;
			var html = '';
			var show_operation = false;
			
			for ( var i in result.list.list) {
				
				var item = result.list.list[i];
				
				var labelClassName = getDepositLabelClassName(item.state);
				var label_class = labelClassName[0];
				var label_name = labelClassName[1];
				
				
				//根据状态决定是否显示“审核按钮”
				var paymentTime=null;
				if(item.paymentTime==null){
					paymentTime = '-待支付-' ;
				}else{
					paymentTime = toDate(item.paymentTime.time);
				}
				
				var auditedTime=null;
				if(item.auditedTime==null){
					 if(item.orderNum.indexOf("D")!=-1){//代为入金
						 auditedTime = '-还未处理-' ;
					}else{
						auditedTime = paymentTime;
					}
				}
				else{
					auditedTime = toDate(item.auditedTime.time);
				}
				
				var pass = null ;
				if(item.state=="PENDING_SUPERVISOR"){//代为入金待财务主管审核
					pass = '<a class="btn btn-primary" onclick="parent.openNewWindow('+"'./../admin/finance_check_deposit.html?id="+item.id+"'"+',donothing)">立即审核</a>' ;
				}
				
				html += '<tr  style="text-align: center;">'
						+ '<td>#'+item.id+'</td>'
						+ '<td><span class="label '	+ label_class + '">' + label_name + '</span></td>'
						+ '<td>' +(item._userName != "" ? '<a onclick="goSeeProfile('+item._userId+')" >'+item._userName+'</a>' : '(暂无姓名)')+(item.disable ? ' <span class="red">[禁]</span>' : '') + (item.is_frozen ? ' <span class="purple">[冻]</span>' : '')+ '</td>'
						+ '<td style="text-align:left;">' + item.orderNum + '</td>'
						+ '<td  style="text-align:left;"><span class="font-green" >' + item.amount.toFixed(2) + '</span></td>'
						+ '<td>' + paymentTime + '</td>'
						+ '<td>' + auditedTime + '</td>'
						+ '<td>' +checkRole(new Array("FinancialStaff","FinancialSuperior","OperationsManager"), pass, null)+ '</td>'
					    + '</tr>';
			}
			// page
			if(result.buttons!=""){
				html += '<tr><td colspan="8"><div class="pagelist">'+ result.buttons + '</td></tr>';
			}
			$("tbody#"+resultListTagId).html(html);
		}					
	}
	});
}
function updateList1(){
	refreshList1(_pageNo);
}
function donothing(){}

