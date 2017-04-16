const LANG_SUBMIT = '提交>>';
const LANG_SUBMITTING = '提交中...';
const USER_ICO = '<i class="fa fa-user" aria-hidden="true"></i>';
var submitConfirm = true;
var PAGESIZE = 10;
var _pageNo = 1;

var maxDepositMoney = -1;

$(document).ready(function() {
	getMaxDepositMoney();
	
	
	
	$("#payDate").datetimepicker({
        timeFormat: "HH:mm:ss",
        dateFormat: "yy-mm-dd"
    });
	$("#startDate1").datetimepicker({
		timeFormat: "HH:mm:ss",
		dateFormat: "yy-mm-dd"
	});
	$("#endDate1").datetimepicker({
		timeFormat: "HH:mm:ss",
		dateFormat: "yy-mm-dd"
	});
	
	$("#btn_search").click(function () {
		search();
	});
	
	$("#btn_submit").click(function () {
		add();
	});
	
	$("#go").click(function () {
		refreshList1(1);
	});
	
	$("#startDate1").change(function(){refreshList1(1);});
	$("#endDate1").change(function(){refreshList1(1);});
	$("#state1").change(function(){refreshList1(1);});
	
	
	$("#search_table").hide();
	
	
	refreshList1(1);
	
});

function autoFill(){
	$("#payId").val(toDateNumber(new Date()));
}


function search(){
	var keyword=$("#keyword").val();
	if(keyword.length==0){
		layer.tips("搜索关键字不能为空哦.","#keyword",{time:2000,tips:[2,'#56a787']});
		$("#keyword").focus();
		return;
	}
	keyword = keyword.trim();
	$.jsonRPC.request("admin2DepositService.findUser", {
		params : [keyword],
		success : function(result) {
			$("#search_table").show();
			if(result.list.length>0){
				$(".search_list").html("");
				var titleTag= '';
				var html = '';
				var item;
				for(var i in result.list){
					item = result.list[i];
					titleTag = "姓名："+item.username+" 手机："+item.mobile+" 邮箱："+item.email+" 身份证："+item.idcard;
					html += '<li title="'+titleTag+'"><input style="width:15px" type="radio" name="radio" value="'
					+item.userId+'"/>'+USER_ICO+'<span id="username">'+item.username+'</span>(手机：'+item.mobile+')</li>';
				}
				$(".search_list").html(html);
			}
			else{
				$(".search_list").html("对不起，没有搜索到相关的用户。");
			}
			//只有一个时，默认选中
			if(result.list.length == 1){
				$("input[name='radio']").attr("checked","checked");
			}
		}
	});
}

function amountchange(){
	var moneyUSD = 0.0;
	var moneyUSDStr = $("#amount").val().trim();
	if(moneyUSDStr!="" && !isNaN(moneyUSDStr))
	{
		moneyUSD = parseFloat(moneyUSDStr).toFixed(2);
		$("#amount").val(moneyUSD);
	}
	else{
		layer.tips("您输入的金额不正确.","#amount",{time:2000,tips:[2,'#56a787']});
		$("#amount").focus();
	}
}


//提交代为入金资料
function add() {
		var userId = $("input[name='radio']:checked").val();
		if(userId == null){
			layer.msg("请在上方搜索结果中选择要代为入金的用户。",{time:2000});
			return;
		}
		var username = $(":radio:checked").parents("li").find("span").html();
		var payId=$("#payId").val();
		var amount=$("#amount").val();
		var payDate=$("#payDate").val();
		var userComment=$("#userComment").val();
		var operationPassword=$("#operationPassword").val();
		
		//清除样式
		
		if (keyword==""){
			layer.tips("搜索关键字不能为空哦.","#keyword",{time:2000,tips:[2,'#56a787']});
			$("#keyword").focus();
			return ;
		} else if (!(amount > 0) || isNaN(amount)) {
			layer.tips("金额不正确.","#amount",{time:2000,tips:[2,'#56a787']});
			$("#amount").focus();
			return ;
		}else if (maxDepositMoney!=-1 &&  amount > maxDepositMoney) {
			layer.msg("代为入金金额超过最大入金金额："+maxDepositMoney,{time:2000,icon:2});//失败
			return;
		}
		else if (payId=="") {
			layer.tips("流水号不能为空哦.","#payId",{time:2000,tips:[2,'#56a787']});
			$("#payId").focus();
			return ;
		} else if (payDate=="") {
			layer.tips("请填写支付时间.","#payDate",{time:2000,tips:[2,'#56a787']});
			$("#payDate").focus();
			return ;
		} else if (operationPassword=="") {
			layer.tips("操作密码不能为空哦.","#operationPassword",{time:2000,tips:[2,'#56a787']});
			$("#operationPassword").focus();
			return ;
		} else {
			//询问框
			layer.confirm("您确定代用户<span class='font-red'>"+username+"</span></br>入金<span class='font-red'>"+amount+"(USD)</span>吗？", {
			  btn: ['确认为其入金','放弃入金'] //按钮
			}, function(){
				if(submitConfirm){
					submitConfirm = false;
					$("#btn_submit").attr("disabled","disabled");
					$("#btn_submit").html(LANG_SUBMITTING);
					
					$.jsonRPC.request("admin2DepositService.add",{
						params:[userId,payId,amount,payDate,userComment,operationPassword],
						success:function(result){
							if (result) {
								if(result=="提交成功"){
									layer.msg(result,{time:1000,icon:1});
								}else{
									layer.msg(result,{time:2000,icon:2});//失败
								}
							} else {
								layer.msg(result,{time:2000,icon:2});//失败
							}
							submitConfirm = true;
							$("#btn_submit").html(LANG_SUBMIT);
							$("#btn_submit").removeAttr("disabled");
							$("#operationPassword").val("");
							refreshList1(1);
						},
						error : function(e) {
							layer.msg(e.error.msg,{time:2000,icon:2});////失败
						    submitConfirm = true;
						    $("#btn_submit").html(LANG_SUBMIT);
						    $("#btn_submit").removeAttr("disabled","disabled");
						    $("#operationPassword").val("");
						}
					});
				}
			}, function(){
			$("#operationPassword").val("");
			  return;
			});
		}
	}
		


function refreshList1(pageNo) {
	$("tbody#result_list1").html('<tr><td colspan="7">正在搜索...</td></tr>');
	var startDate = $("#startDate1").val();
	var endDate = $("#endDate1").val();
	var keyword = $("#keyword1").val();
	var scheme = $("#state1").val();
	getPage(0, pageNo, PAGESIZE, "javascript:refreshList1(??);", scheme,startDate,endDate,keyword,"result_list1");
}

//获取代为入金记录
function getPage(userId, pageNo, PAGESIZE, urlFormat, scheme, startDate,endDate,keyword,resultListTagId){
	$.jsonRPC.request("admin2DepositService.getPage", {
		params : [0, pageNo, PAGESIZE, urlFormat, scheme, startDate, endDate, keyword,true],
		success : function(result) { 
		if (result != null) {
			if(result.list.list.length == 0){
				$("tbody#"+resultListTagId).html('<tr><td colspan="7">对不起，没有搜索到相应结果。</td></tr>');
				return;
			}
			_pageNo = pageNo;
			var html = '';
			
			for ( var i in result.list.list) {
				
				var item = result.list.list[i];
				var label_class = "";
				var label_name = "";
				
				var operationHTML = null;
				
				switch (item.state) {
				case 'PENDING_SUPERVISOR':
					label_class = 'label-pending';
					label_name = '待财务主管审核';
					operationHTML = '<button class="btn btn-primary" onclick="parent.openNewWindow('+"'./../admin/finance_check_deposit.html?id="+item.id+"'"+',updateList1)">审核</button>';
					break;
				case 'DEPOSITED':
					label_class = 'label-success';
					label_name = '已到帐';
					break;
				case 'REJECTED':
					label_class = 'label-cancel';
					label_name = '已驳回';
					show_cancel = true;
					break;
				}
				
				var paymentTime=null;
				var auditedTime=null;
				if(item.paymentTime==null){
					paymentTime = '-[还未处理]-' ;
				}else{
					paymentTime = toDate(item.paymentTime.time);
				}
				if(item.auditedTime==null){
					auditedTime = '-[还未处理]-' ;
				}else{
					auditedTime = toDate(item.auditedTime.time);
				}
				
				var goSeeProfile= "<a onclick='goSeeProfile("+item._userId+")'>"+item._userName+"</a>";
				
				html += '<tr>'
						+ '<td>'+item.id+'</td>'
						+ '<td><span class="label '	+ label_class + '">' + label_name + '</span></td>'
						+ '<td>' +(item._userName != "" ? goSeeProfile : '(暂无姓名)')+(item.disable ? ' <span class="red">[禁]</span>' : '') + (item.is_frozen ? ' <span class="purple">[冻]</span>' : '')+ '</td>'
						+ '<td>' + paymentTime + '</td>'
						+ '<td class="font-green">' + item.amount.toFixed(2) + '</td>'
						+ '<td>' + item.orderNum + '</td>'
						+ '<td>' + auditedTime + '</td>'
						+ '<td>' +checkRole(new Array("FinancialSuperior","OperationsManager"), operationHTML, null)+ '</td>'
						+ '</td>'
					    + '</tr>';
			}
			// page
			if(result.buttons != ""){
				html += '<tr><td colspan="8"><div class="pagelist">'+ result.buttons + '</td></tr>';
			}
			$("tbody#"+resultListTagId).html(html);
		}					
	}
	});
}

/**
 * 获取用户最大入金金额
 */
function getMaxDepositMoney(){
	$.jsonRPC.request("adminSettingService.getList",{
		params:[],
		success:function(result){
			maxDepositMoney = result.list[6].doubleValue;
		}
	});
}


function updateList1(){
	refreshList1(1);
}