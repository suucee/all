const PAGE_SIZE = 5;
var userDetail =  null;
//var checkBank =  null;
//var checkUser =  null;
//var checkWithdrawal = null;
//var checkDeposit =  null;
//var check_type = "";//bank/withdrawal/deposit/user
var maxMT4Count = 5;
var _user_id = null;
$(function() {
	
	//离开或者刷新该页面
	$(window).unload( function () {
		parent.localUserById(0);//折叠所有节点
	} );
	
	
	$(".table-info input").css("height","15px");
	$(".agent").width();
	
	$(".table-info input").change(function(event){
		changed(event);
	});
	$(".table-info select").change(function(event){
		changed(event);
	});
	
	$(".select_state").change(function(){
		if($(".select_state").val() == "VERIFIED"){
			layer.tips("选择此项表示合规通过用户资料<br/>（当前未保存更改）",".select_state",{time:2500,tips:[3,'#56a787']});
		}else if($(".select_state").val() == "REJECTED"){
			layer.tips("选择此项表示合规拒绝用户资料<br/>（当前未保存更改）",".select_state",{time:2500,tips:[3,'#56a787']});
		}
	});
	$(".disable_frozen").change(function(){
		if($(".disable_frozen").val() == "disable"){
			layer.tips("选择此项表示禁用此用户<br/>（当前未保存更改）",".disable_frozen",{time:2500,tips:[3,'#56a787']});
		}else if($(".disable_frozen").val() == "frozen"){
			layer.tips("选择此项表示冻结此用户<br/>（当前未保存更改）",".disable_frozen",{time:2500,tips:[3,'#56a787']});
		}else if($(".disable_frozen").val() == ""){
			layer.tips("选择此项表示解除禁用或者解除冻结此用户<br/>（当前未保存更改）",".disable_frozen",{time:2500,tips:[3,'#56a787']});
		}
	});
	
	
	$(".agent_level").change(function(){
		var defaultV = $(".agent_level").find("option:selected").attr("default");
		if(defaultV == "true"){//没变
			agent_level_noChanged();
		}else{
			agent_level_hasChanged();
		}
	});
	
	$(".agent").change(function(){
		var defaultV = $(".agent").find("option:selected").attr("default");
		if(defaultV == "true"){//没变
			agent_noChanged();
		}else{
			agent_hasChanged();
		}
	});
	
	
	$(".select_userNationality").load("../admin/_options_country.html");
	$(".select_mobileCode").load("../admin/_options_countryCode.html");
	
	
	userDetail = new UserDetail();
	
	$("#btn_restpassword").click(function(){
		userDetail.resetPwd();
	});
	
	$("#btn_updateProfile").click(function(){
		changeGuideDiv(0,false);
		userDetail.updateProfile();
	});
	
	$("#btn_holderUser").click(function(){
		userDetail.holderUser();
	});
	
	$("#btn_addMT4ForUser").click(function(){
		userDetail.addMT4ForUser();
	});
	
	$("#btn_bindMT4ForUser").click(function(){
		parent.openNewWindow('./../admin/see_profiles_mt4s_bind_login.html?id='+userDetail.user_id, updateList);
	});
	
	$("#btn_checkProfile").click(function(){
		parent.openNewWindow('./../admin/compliance_check_user.html?id='+userDetail.user_id, updateList);
	});
	
	$("#btn_setAgent").click(function(){
		userDetail.setAgent();
	});
	
	
	
	
	getMaxMT4Count();
	
	checkLoginRole();
	$(".rightTR a").css("width","80px");
	
	
	$(".tab-button").click(function() {
		$(".tab-button").removeClass('btn-b');
		$(".tab-button").addClass('btn-f');
		$(this).removeClass('btn-f');
		$(this).addClass('btn-b');
		$index = $(".tab-button").index($(this));
		switch($index){
		case 1:
			userDetail.getBankAccount(1);
		case 3:
			userDetail.getPageCapital(1);
		default:
			userDetail.getUserDetail();
			break;
		}
		$(".tab-content").hide();
		$(".tab-content").eq($index).show(200);
		userDetail.getUserDetail();
		
		backToDefault();

	});
	
	
//	changeGuideDiv(3);
	
	
});


function agent_level_hasChanged(){
	layer.tips("注意不能同时更改推荐人<br/>（当前未保存更改）",".agent_level",{time:3000,tips:[3,'#56a787']});
	$(".agent").attr("title","更改了用户级别后不能同时更改推荐人");
	$(".agent").css("color","#d2d2d2;");
	$(".agent").attr("disabled","disabled");
}
function agent_level_noChanged(){
	$(".agent").removeAttr("disabled","disabled");
	$(".agent").attr("title","");
	$(".agent_level").css("background","");
}

function agent_hasChanged () {
	layer.tips("注意不能同时设置用户级别<br/>（当前未保存更改）",".agent",{time:3000,tips:[3,'#56a787']});
	$(".agent_level").attr("title","更改了推荐人后不能同时更改用户级别");
	$(".agent_level").css("color","#d2d2d2;");
	$(".agent_level").attr("disabled","disabled");
}

function agent_noChanged () {
	$(".agent_level").attr("title","");
	$(".agent_level").removeAttr("disabled","disabled");
	$(".agent").css("background","");
}





/**
 * 标签切换
 * @param tab_button_index 0开始
 */
function changeGuideDiv(tab_button_index, refresh){
	$(".tab-button").removeClass('btn-b');
	$(".tab-button").addClass('btn-f');
	var $this =  $(".tab-button").eq(tab_button_index);
	
	$this.removeClass('btn-f');
	$this.addClass('btn-b');
	if(refresh){
		switch(tab_button_index){
			case 1:
				userDetail.getBankAccount(1);
			case 3:
				userDetail.getPageCapital(1);
			default:
				userDetail.getUserDetail();
		}
	}
	$(".tab-content").css("display", "none");
	$(".tab-content").eq(tab_button_index).show();
	backToDefault();
}



function changed(event){
	var $this = event.target;
	$($this).css("background","rgba(160,197,232,0.6)");
	$($this).attr("title","您已经更改了此项信息，点击右侧“更新资料”以保存更改");
	$(".saveTip").html("<span class='font-red'>提示：信息有变更，未保存更改<span>");
	$(".saveTip").show(200);
	userDetail.hasChangedInfo = true;
}


function alignRight(){
		var teltdWidth = $(".tel").parents("td").width();
		
		$(".tel").css("width",teltdWidth-60-20);
		
		var righttdWidth = $(".field-right").parents("td").width();
		$(".field-right").css("width",righttdWidth-20+3);
		
		var agenttdWidth = $(".agent").width();
		$(".address").css("width",agenttdWidth-100-8);
		
		var nametdWidth = $(".name").width();
		$("#cardID").css("width",nametdWidth-100-4);
		
}


//合规结束后应该收起详情和操作台
function backToDefault(){
	$(".check_detail_info").hide(200);
	
	//操作台
	$(".check_operation").hide(function(){
		$(".default_check_operation").show(300);
		$(".check_operation").html("");//将操作台清除
	});
	
	//银行卡
	$(".one_bank_table").hide(200,function(){
		$(".all_bank_table").show();
		$("#one_bankAccountList").html("");//将单个信息清除
	});
	
	//出金或者入金
	$(".one_capital_table").hide(200,function(){
		$(".all_capital_table").show();
		$("#one_result_list_capital").html("");//将单个信息清除
	});
	
}


var UserDetail = function() {
	_user_id = getParam("id", 0);
	this.backurl = getParam("backurl",null);
	this.user_id = _user_id;
	this.state = null;
	this.vip_grade = null;
	this.level = null;
	this.hasChangedInfo = false;
	this.profile_id=0;
	this.init(_user_id);
	alignRight();
	parent.localById(_user_id, true);
};

UserDetail.prototype = {
	
	back : function(){
		if(this.backurl != null){
			window.location.href = this.backurl;
			parent.localUserById(0);//折叠所有节点
		}
	},	

	/**
	 * 
	 */
	init:function(user_id) {
		if(!(user_id > 0)){
			layer.alert("对不起，无法获取指定用户的基本信息。");
			return false;
		}
		if(this.backurl != null){//是从别的地方过来的
			$(".container > div:first").show();
//			parent.localUserById(user_id);
		}
		this.getUserDetail();
		this.getBankAccount(1);
		this.getPageCapital(1);
	},
	
	getUserDetail : function() {
		var _this = this;
		$.jsonRPC.request("user2Service.getUserDetail", {
			params : [_this.user_id],
			success : function(result) { 
					if (result.map.users != null) {
//个人信息Users------------------------------------------------------------------	----------------------			
						var item = result.map.users;
						
						createParentsTree(item._up_id, item.path)
						
//级别--------------------------------------------------------------------------------------------
						_this.level = item.level;
						_this.vip_grade = item.vipGrade;
						
						
//						console.log($(".agent_level option [value='vip_grade_0']"));选择不到用循环判断
						
						if(item.level >=1 && item.level <=3 ){//员工、经理、公司
							$(".agent_level").val("level_"+item.level);
							$(".agent_level option").each(function (i, ele) {
								if($(ele).val() == "level_"+item.level){
									$(ele).attr("default","true");
									$(ele).css("background","rgb(160,197,232)");
								}
							})
							
						}else{//代理
							$(".agent_level").val("vip_grade_"+item.vipGrade);
							
							$(".agent_level option").each(function (i, ele) {
								if($(ele).val() == "vip_grade_"+item.vipGrade){
									$(ele).attr("default","true");
									$(ele).css("background","rgb(160,197,232)");
								}
							})
						}

						if(item.vipGrade > 0){//代理
							$(".agent_guide").show();
							$(".agent_level2").val(item.vipGrade);
						}
						
						$(".email").val(item.email);
						$(".referralCode").html(item.referralCode);
						
						if(item.state == "AUDITING"){//待审核
							$(".userWaitingCount").html("(1)");
							$("#btn_checkProfile").html("审核资料");
							$("#btn_checkProfile").removeClass("btn-b").addClass("btn-a");
						}else if(item.state == "UNVERIFIED"){//资料不全，无法更改
							$(".select_state").attr("disabled","disabled");
							$(".select_state").attr("title","用户资料不全，无法更改状态。");
						}else{
							//审核资料为重新审核
							$("#btn_checkProfile").html("重审资料");
							$("#btn_checkProfile").removeClass("btn-a").addClass("btn-b");
						}
						$(".select_state").val(item.state);
						_this.state = item.state;
						
						
						var holeMobile = item.mobile;
						$(".select_mobileCode").val(holeMobile.substring(1,holeMobile.indexOf(".")));
						$(".tel").val(holeMobile.substring(holeMobile.indexOf(".")+1,holeMobile.length));
						
						if(item.frozen){
							$(".disable_frozen").val("frozen");
						}
						if(item.disable){
							$(".disable_frozen").val("disable");
						}
						if(( !item.frozen)  && (!item.disable)){//正常
							$(".disable_frozen").val("");
						}
						
						if(item.email==null || item.email == ""){
							$("#btn_restpassword").removeClass("btn-a");
							$("#btn_restpassword").addClass("btn-e");
							$("#btn_restpassword").css({"cursor":"default"});
							$("#btn_restpassword").attr("title","用户还未绑定邮箱，邮件将无法发送，因此无法重置密码。");
							$("#btn_restpassword").unbind("click"); //移除click
						}
						
					}
					
					
					//负责人——上级员工
					{
						if (result.map.usersParentStuffInfoMap.map != null) {
							var userName = result.map.usersParentStuffInfoMap.map.profiles.userName;
							$(".stuffName").html(userName);
						}
					}
					
//个人资料Profile------------------------------------------------------------------	----------------------			
				{
					if (result.map.profiles != null) {
						var item = result.map.profiles;
						$(".name").val(item.userName);
						$(".legend_name").html(item.userName);
						$(".ename").val(item.userEName);
						//下拉选项cardType
						$("#cardType").val(item.cardType);
						$("#cardID").val(item.userIdCard);
						//下拉居住地区countryCode
						$(".userNationality").val(item.userNationality);
						$(".address").val(item.userEsidentialAddress);
						$(".cname").val(item.company);
						//公司名称
						$(".hangye").val(item.userIndustry);
						//行业
						$(".userPosition").val(item.position);
						//职位
						$(".yearsr").val(item.userYearsIncom);
						//年收入
					}
				}
				
				
//身份证------------------------------------------------------------------	----------------------					
				{
					if (result.map.attach != null) {
						var html = "";
						for (var i in result.map.attach.list) {
							var imgList = result.map.attach.list[i];
							html += "<div style='width:250px;height:150px;text-align:center;margin:5px;float:left;overflow: hidden;'>";
							html += "<div style='background-image: url(/upload" + imgList.path + ");background-position: center;background-size:auto 100%;background-repeat: no-repeat;width: 250px;height: 150px;line-height: 150px;margin-bottom:5px;'>";
							html += "<div style='width: 250px;height: 150px;line-height: 150px;'>";
							html += "&nbsp;";
							html += "<a style='' href='javascript:userDetail.removeProof("+ imgList.id+");'  class='btn btn-e'><i class='fa fa-remove' aria-hidden='true'></i>&nbsp;&nbsp;移除</a>&nbsp;&nbsp;";
							html += "<a style='' href='/upload" + imgList.path + "' target='_blank'  class='btn btn-g'><i class='fa fa-eye' aria-hidden='true'></i>&nbsp;&nbsp;查看</a>";
							html += "</div>";
							html += "</div>";
							html += "</div>";
						}
						if (html=="") {
						     html += '<tr><td colspan="10" class="bold" style="text-align:center;">暂无图片.</td></tr>';
						}
						$(".imageShow").html(html);
					}
				}
				
//网站账户---------------------------------------------------------------------------------------				
				 {
					 //网站账户
					var item = result.map.userBalances;
					var sum=result.map.withdrawalsSum;
					var html = '<tr>';
					 html += '	<td class="sample-currency">USD</td>';
					 html += '	<td>' + toDate(item.updatedTime.time) + '</td>';
					 html +=  '	<td class="align-center">' + item.amountAvailable.toFixed(2)  + '&nbsp;&nbsp;USD</td>' ;
					 html +=  '	<td class="align-center">' + sum.toFixed(2) + '&nbsp;&nbsp;USD</td>' ;
					 html +=  '</tr>';
					$("tbody#result_list_ub").html(html);
				}
				 
//MT4账户---------------------------------------------------------------------------------------					 
				 {
					var html = '';
					if(result.map.mt4UsersList.list == 0){//没有MT4账户
						html = "<tr><td colspan='2'>该客户还没有任何MT4账号。</td>";
						var operationHtml = "<td colspan=2>";
						operationHtml += "</td></tr>";
						
						html += operationHtml;
						$("tbody#mt4result_list").html(html);
					}
					else{
						for (var i in result.map.mt4UsersList.list) {
							var item =result.map.mt4UsersList.list[i];
							var pass = '<a class="btn btn-a margin-right15" href="javascript:unbindUser('+item.login+');">解绑</a>';
							var forbid = '<a class="btn btn-a" href="javascript:parent.openNewWindow(\'./../admin/see_profile_mt4s_order.html?login='+item.login+'\',donothing)">历史交易</a>';
							pass += forbid;
							
							html += '<tr>';
							html += '	<td>'+item.login+'</td>';
							html += '	<td>'+toDate(item.creatTime.time)+'</td>';
							html += '	<td style="text-align:center">'+item.balance.toFixed(2)+'</td>';
							html += ' <td style="text-align:center">'+item.group+'</td>';
							html += ' <td style="text-align:center">'+checkRole(new Array("OperationsManager"),pass,forbid)+'</td>';
							html +=  '</tr>';
					    }
					}
					$("tbody#mt4result_list").html(html);
				}
				
//返佣---------------------------------------------------------------------------------------		
				{
						var list = result['map']['monthRebate']['list'];
						var rebateCount = result['map']['rebateCount'];
						var html="";
						if(list.length!=0){
						for (var ii = 0; ii < list.length; ii++) {
							var obj = list[ii];
							html+= '<tr>';
							html+= '<td>' + obj['monthstr'] + '</td>';
							html+= '<td>' + obj['rebateCount'].toFixed(2)  + '</td>' ;
			                html+= '</tr>';
						}
						html+='<tr>';
						html+='<td>客户的返佣收入累计：<span class="bold"> ' + rebateCount.toFixed(2) + '（USD）</span></td>';
						html+='<td>&nbsp;</td>';
						html+='</tr>';
						}else{html += '<tr><td colspan="10" class="bold">对不起，暂无该用户的返佣统计。</td></tr>';}
						html += '<tr><td colspan="2"><span>返佣说明：此处的返佣是该客户自己的返佣收入。</span></td></tr>';
			            $('#monthrebate_list').html(html);
				}
			}
		});
	},
	
	
//银行卡------------------------------------------------------------------	----------------------					
	getBankAccount : function(pageNo){
		var _this = this;
		$.jsonRPC.request("user2Service.getBankAccount", {
			params:[_this.user_id,pageNo, PAGE_SIZE,"javascript:userDetail.getBankAccount(??);"],
			success : function(result) { 
				if (result!= null) {
						var html = '';
						var bankWaitingCount = 0;
						for (var i in result.map.page.list.list) {
							var item = result.map.page.list.list[i];
							var label_class = "";
							var label_name = "";
							var dohtml="<a onclick='parent.openNewWindow("+'"./../admin/compliance_check_backaccount.html?id='+item.id+'"'+",updateBank)' class='btn btn-b'>立即审核</a>";
							switch (item.state) {
							case 'WAITING':
								label_class = 'label-pending';
								label_name = '待审核';
								bankWaitingCount ++;
								break;
							case 'AUDITED':
								label_class = 'label-success';
								label_name = '已审核';
								var dohtml="<a onclick='parent.openNewWindow("+'"./../admin/compliance_check_backaccount.html?id='+item.id+'"'+",updateBank)' class='btn btn-a'>重新审核</a>";
								break;
							case 'REJECTED':
								label_class = 'label-important';
								label_name = '已驳回';
								var dohtml="<a onclick='parent.openNewWindow("+'"./../admin/compliance_check_backaccount.html?id='+item.id+'"'+",updateBank)' class='btn btn-a'>重新审核</a>";
								break;
							default:
								label_class = 'label-pending';
								label_name = '无状态';
								break;
							}
							var operationHtml = checkRole(new Array("ComplianceOfficer","OperationsManager"),dohtml,null);
							 html += '<tr>';
							 html += '<td style="text-align: center;"><span class="label ' + label_class + '">' + label_name + '</span></td>'; 
							 html += '<td style="text-align: center;">'+item.bankName + '</td>';
							 html += '<td style="text-align: center;">' + item.accountName + '</td>' ;
							 html += '<td style="text-align: center;">' + item.accountNo + '</td>' ;
							 html += '<td style="text-align: center;">' + item.countryCode + '</td>' ;
							 html += '<td style="text-align: center;">'+operationHtml+'</td>' ;
							 html += '</tr>';
						}
						if(bankWaitingCount > 0){
							$(".bankWaitingCount").html("("+bankWaitingCount+")");
						}
						if(result.map.page.buttons!=null&&result.map.page.buttons!=""){
					    	html += '<tr><td colspan="6"><div class="pagelist">'+result.map.page.buttons+'</td></tr>';
					    }
						if (html=="") {
						     html += '<tr><td colspan="6">用户还未添加银行卡</td></tr>';
						}
						$("#bankAccountList").html(html);
					}
			}
		});
	},
	
	
	/**
	 * 获取所有的出金入金以及转账状况
	 * @param pageNo 页数
	 */
	getPageCapital:function(pageNo){
		var _this = this;
		$.jsonRPC.request("user2Service.getUserPageCapital",{
//		$.jsonRPC.request("user2Service.getPageCapital",{
			params:[_this.user_id,pageNo, PAGE_SIZE,"javascript:userDetail.getPageCapital(??);"],
			success:function(result){
				if (result != null) {
					_pageNo = pageNo;
					var html = '';
					//待处理事项数目
					var capitalWaitingCount = 0;var WAITING_Count  = 0;var AUDITED_Count = 0;var PENDING_SUPERVISOR_Count = 0;
					for (var i in result.list.list) {
						var item = result.list.list[i].map;
						var label_class = "";
						var label_name = "";
						var show_cancel = false;
						var operationHtml = null;
						
						var class_name = getLabelClassName(item.state,item.type);
						label_class = class_name[0];
						label_name = class_name[1];
						
						var typeHtml = '';
						var moneyHtml = '';
						item.amount = Math.abs(item.amount);
						if(item.type == "withdrawals"){
							moneyHtml = '<span class="font-red">'+item.amount.toMoney()+'</span>';
							typeHtml = '<span class="label label-red">出金</span>';
						}else if(item.type == "deposits"){
							moneyHtml = '<span class="font-green">'+item.amount.toMoney()+'</span>';
							typeHtml = '<span class="label label-success">入金</span>';
						}else{
							moneyHtml = '<span class="font-green">'+item.amount.toMoney()+'</span>';
							typeHtml = '<span class="label badge-particular-filled">转账</span>';
							
						}
						
						if(item.state == "WAITING"){//合规人员----审核
							WAITING_Count++;
							var pass = '<a class="btn btn-b" href="javascript:parent.openNewWindow(\'./../admin/compliance_check_withdrawal.html?id='+item.id+'\',refreshDepositsList)">立即审核</a>';
							operationHtml = checkRole(new Array("ComplianceOfficer","OperationsManager"),pass,operationHtml);
						}
						else if(item.state == "AUDITED"){//财务-----汇款确认
							AUDITED_Count++;
							var pass = '<a class="btn btn-b" href="javascript:parent.openNewWindow(\'./../admin/finance_check_remittance.html?id='+item.id+'\',refreshDepositsList)">汇款确认</a>';
							operationHtml = checkRole(new Array("FinancialStaff","FinancialSuperior","OperationsManager"),pass,operationHtml);
						}
						else if(item.state == "PENDING_SUPERVISOR"){//【财务主管-大额出金审核/代为入金审核】
							PENDING_SUPERVISOR_Count++;
							var pass = '<a class="btn btn-b" href="javascript:parent.openNewWindow(\'./../admin/finance_check_withdrawal.html?id='+item.id+'\',refreshDepositsList)">立即审核</a>';
							if(item.type == "deposits"){//代为入金审核
								pass = '<a class="btn btn-b" href="javascript:parent.openNewWindow(\'./../admin/finance_check_deposit.html?id='+item.id+'\',refreshDepositsList)">立即审核</a>';
							}
							operationHtml = checkRole(new Array("FinancialSuperior","OperationsManager"),pass,operationHtml);
						}
						else if(item.type == "withdrawals"){//出金详情
							operationHtml = "<a class='btn btn-a' href='javascript:parent.openNewWindow(\"./../admin/finance_withdrawal_detail.html?id="+item.id+"\",donothing)'>查看详情</a>";
						}else {
							operationHtml = '<i class="fa fa-ban" aria-hidden="true"></i>';
						}
						
						
						html += '<tr>'
							+ '<td>'+typeHtml+'</td>' 
							+ '<td style="text-align: right;">'+moneyHtml+'</td>'
							+ '<td><span class="label '+label_class+'">'+label_name+'</span></td>'
							+ '<td title="'+item.description+'">'+item.description+'</td>' 
							+ '<td>'+toSimpleDate2M(item.create_time.time)+'</td>'
							+ '<td>'+(item.deal_time == null ? ' -暂未处理- ' : toSimpleDate2M(item.deal_time.time))+'</td>'
							+ '<td style="text-align: center;">'+operationHtml+'</td>'
							+ '</tr>';
				    }
					
					//显示待处理数量
					if(loginUser.role == "ComplianceOfficer"){//合规人员----审核
						if(WAITING_Count > 0){
							$(".capitalWaitingCount").html("("+WAITING_Count+")");
						}
					}else if(loginUser.role == "FinancialStaff"){
						if(AUDITED_Count > 0){
							$(".capitalWaitingCount").html("("+AUDITED_Count+")");
						}
					}
					else if(loginUser.role == "FinancialSuperior"){
						capitalWaitingCount = AUDITED_Count + PENDING_SUPERVISOR_Count;
						if(capitalWaitingCount > 0){
							$(".capitalWaitingCount").html("("+capitalWaitingCount+")");
						}
					}
					else if(loginUser.role == "OperationsManager"){
						capitalWaitingCount = AUDITED_Count + PENDING_SUPERVISOR_Count;
						capitalWaitingCount += WAITING_Count;
						if(capitalWaitingCount > 0){
							$(".capitalWaitingCount").html("("+capitalWaitingCount+")");
						}
					}
					
					if(result.buttons!=null&&result.buttons!=""){
				    	html += '<tr><td colspan="7"><div class="pagelist">'+result.buttons+'</td></tr>';
				    }
					if (html=="") {
					     html += '<tr><td colspan="7" class="bold">暂未检索到相关的出金记录</td></tr>';
					}
					$("tbody#result_list_capital").html(html);
				}	
			}
		});
	},
	
	
	resetPwd : function(){
		layer.confirm("您确定要重置这个用户的网站登录密码和支付密码为随机密码吗？<br/>重置密码完成后将发送邮件通知用户。",
				{btn:["确认重置","放弃"],icon:3},
				function(){
					$.jsonRPC.request("adminUserService.resetUserPassword", {
						params : [_user_id],
						success : function(result) {
							layer.msg("密码重置成功！新登录密码和支付密码已经邮件发给客户！",{time:1500,icon:1},function(){
								$("#btn_restpassword").removeClass("btn-primary");
								$("#btn_restpassword").addClass("btn-e");
								$("#btn_restpassword").css("cursor","default");
								$("#btn_restpassword").attr("disabled","disabled");
							});
						}
					});
				},
				function(){
					return;
				}
			);
	},
	
	addMT4ForUser : function(){
		var _this = this;
		//Role.OperationsManager, Role.CustomerServiceStaff
		layer.confirm("您确定要新开设一个MT4帐号给这个用户吗？<br/>如果新开MT4成功将发送邮件通知用户。",
				{btn:["确认开设","放弃"],icon:3},
				function(){
					$.jsonRPC.request('admin2UserService.addMT4User', {
						params : [_this.user_id],
						success : function(result) {
							if(result){
								layer.msg("为此用户新增MT4成功！",{time:1500,icon:1});
								_this.getUserDetail();
							}else{
								layer.msg("对不起，为此用户新增MT4失败！",{time:2000,icon:2});
							}
						}
					});
				},
				function(){
					return;
				}
		);
	},
	
	/**
	 * 更新用户资料
	 */
	updateProfile:function (){
		//由于设计，Nationality 才应该是用户国家，countyCode在这里用作手机区号
		var _this=this;
		
		var staffScheme = '';
		
		var select_state = $(".select_state").val();
		if(select_state == null){//是不可选的项
			select_state = _this.state;
		}
		
		var agent_level = $(".agent_level").val();
		if(agent_level.indexOf("vip_grade_") != -1){
			_this.vip_grade = agent_level.replace("vip_grade_","");
		}else if(agent_level.indexOf("level_") != -1){
			_this.level = agent_level.replace("level_","");
			switch(_this.level){
			case "1":
				staffScheme = "Company";
				break;
			case "2":
				staffScheme = "Manager";
				break;
			case "3":
				staffScheme = "Staff";
				break;
			}
		}
		
		var upId = $("select[name='upId']").val();
		var mobileCode = $(".select_mobileCode").val();
		var mobile = $(".tel").val();
		var mobileWhole = '';//如：+86.123123123
		var email = $(".email").val();
		var name = $(".name").val();
		var ename = $(".ename").val();
		//下拉选项cardType
		var cardType = $("#cardType").val();
		var cardID = $("#cardID").val();
		//下拉居住地区userNationality
		var userNationality = $(".select_userNationality").val();
		var address = $(".address").val();
		var cname = $(".cname").val();//公司名称
		var hangye = $(".hangye").val();//行业
		var userPosition = $(".userPosition").val();//职位
		var yearsr = $(".yearsr").val();//年收入
		var profile_attachment_id = $("#profile_attachment_id").val()
		
		//管理员设置个人资料时，权限很高。
		if (!/^[0-9]{8,11}$/.test(mobile)) {
			layer.tips("手机号码不正确.",".tel",{time:2000,tips:[2,'#56a787']});
			$(".tel").focus();
		}else if(mobileCode == ""){
			layer.tips("请选择手机地区代码.",".select_mobileCode",{time:2000,tips:[2,'#56a787']});
			$(".select_mobileCode").focus();
		}
		else if(!/^([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\-|\_|\.]?)*[a-zA-Z0-9]+\.[a-zA-Z]{2,10}$/.test(email)){
			layer.tips("请输入正确的电子邮箱地址.",".email",{time:2000,tips:[3,'#56a787']});
			$(".email").focus();
		}
		else if (name==""){
			layer.tips("姓名不能为空.",".name",{time:2000,tips:[2,'#56a787']});
			$(".name").focus();
		} else if (ename==""|| !(/^[a-zA-Z]+$/g).test(ename) ) {
			layer.tips("姓名拼音格式不正确.",".ename",{time:2000,tips:[2,'#56a787']});
			$(".ename").focus();
		} else if (cardType=="") {
			layer.tips("请选择证件类型.","#cardType",{time:2000,tips:[2,'#56a787']});
			$("#cardType").focus();
		} else if (cardID=="" || !(/^[0-9a-zA-Z]+$/g).test(cardID)) {
			layer.tips("请输入正确的证件号码.","#cardID",{time:2000,tips:[2,'#56a787']});
			$("#cardID").focus();
		} else if (userNationality=="") {
			layer.tips("请选择居住国家.",".userNationality",{time:2000,tips:[2,'#56a787']});
			$(".userNationality").focus();
		}else if (address=="") {
			layer.tips("请填写居住地址.",".address",{time:2000,tips:[2,'#56a787']});
			$(".address").focus();return;
		} 
//		parent.refreshTreeData();//刷新结构树
//*
		else
		{
			var confirmTip = '';
			if(loginUser.role == "OperationsManager"){//运维
				confirmTip = '<span class="font-red">温馨提示：</span><br/>您的更改将直接影响用户的资料状态（不需要合规审核）。<br/><span class="font-red">您确认修改吗？</span>';
			}else{//客服
				confirmTip = '<span class="font-red">温馨提示：</span><br/>如果您修改了用户的重要资料（打了*的必填资料）后，用户资料将回到“待审核”状态，此时需要合规人员重新审核用户的资料。<br/><span class="font-red">您确认修改吗？</span>';
			}
			
			layer.prompt({title: '<span class="font-red">温馨提示：此项操作需要您输入操作密码</span>',	formType: 1},
				function(dopassword){
					mobileWhole = "+"+mobileCode+"."+mobile;
					$.jsonRPC.request("admin2UserService.adminUpdateProfile",{
						params:[dopassword,_this.user_id ,email,mobileWhole, name,ename,cardType,cardID,userNationality,address,cname,hangye,yearsr,userPosition,profile_attachment_id,
						        select_state,_this.vip_grade,_this.level,staffScheme,upId],
						success : function(result){
							if (result) {
								layer.msg("资料更新成功！",{time:1000,icon:1},function(){
									location.reload();//刷新内容页
									
//									parent.refreshTreeData();//刷新结构树
								});
							} else {
								layer.msg("资料更新失败！",{time:2000,icon:2});
							}
						},
						error : function(result){
							layer.alert(result.error.msg,{title:"出错了",icon:2});
						}
					});
					
		  		}
			);
		}
		
		//*/
	},
	
	

	uploadFile : function() {
		var id = this.profile_id;
		$.ajaxFileUpload({
			url : '../../fileupload2/user-' + id + '.do', // 用于文件上传的服务器端请求地址
			secureuri : false, // 一般设置为false
			fileElementId : 'profile_file_upload', // 文件上传空间的name属性 <input type="file"
			// id="file"
			dataType : 'json', // 返回值类型 一般设置为json
			success : function(data, status)// 服务器成功响应处理函数
			{
				if (data.status == "failed") {
					layer.msg(data.result,{time:2000,icon:2});
				} else {
					if ($("#profile_attachment_id").val() == "") {
						$("#profile_attachment_id").val(data['id']);
					} else {
						$("#profile_attachment_id").val($("#profile_attachment_id").val() + "," + data['id']);
					}
					layer.msg("上传成功！",{time:1000,icon:1});
					var html="";
					html += "<div style='width:250px;height:150px;text-align:center;margin:5px;float:left;overflow: hidden;'>";
					html += "<div style='background-image: url(/upload" + data.src + ");background-position: center;background-size:auto 100%;background-repeat: no-repeat;width: 250px;height: 150px;line-height: 150px;margin-bottom:5px;'>";
					html += "<div style='width: 250px;height: 150px;line-height: 150px;'>";
			    	html +="<a style='' href='javascript:userDetail.removeProof("+ data.id+");'  class='btn btn-e'><i class='fa fa-remove' aria-hidden='true'></i>&nbsp;&nbsp;移除</a>&nbsp;&nbsp;";
					html += "<a style='' href='/upload" + data.src + "' target='_blank'  class='btn btn-g'><i class='fa fa-eye' aria-hidden='true'></i>&nbsp;&nbsp;查看</a>";
					html += "</div>";
					html += "</div>";
					html += "</div>";
					$(".imageShow").append(html);
				}
			},
			error : function(data, status, e)// 服务器响应失败处理函数
			{
				layer.msg("文件上传失败",{time:2000,icon:2});
			}
		});
	},
	
	
	removeProof:function (id) {
		layer.confirm('您确定要永久性移除该图片？（注意：后台会删除该图片）', {
			  btn: ['即刻移除','放弃'], //按钮
			  icon:3
			},function(){
				$(".remove").attr("href","javascript:void(0);");
				$.jsonRPC.request("admin2UserService.removeImg",{
					params:[_user_id,id],
					success:function(data){
						if(!data){
							layer.msg("删除失败！",{time:2000,icon:2});
							return;
						}else{
							layer.msg("删除图片成功！",{time:1000,icon:1},function(){
								getImg(_user_id);
							} );
						}
					}
			    });
			},function(){
				return;
			});
   },

	holderUser : function(){
		_this = this;
		layer.confirm("温馨提示：<br/>1、代管是十分危险的功能，如若操作不当，将可能导致后续操作会出现“权限不够”的问题，那么需要重新登录。<br/>"+
				"2、在使用代管功能时请勿直接关闭浏览器窗口，一定保证正常登出。",{btn:["确认使用","放弃"],icon:3},function(){
					$.jsonRPC.request('admin2UserService.holdUser', {
						params : [_this.user_id,window.location.href],
						success : function(result) {
							if (result != null) {
								//Success
								locStorage("loginUser", result);
								setCookie('TOKEN', result.token, 30 * 24);
								layer.msg("代管登录成功，页面即将跳转到用户界面...",{time:1500,icon:1},function(){
									window.open('./../common/index.html?hold=true', '_black');
								});
							} else {
								locStorage("loginUser", null);
								setCookie('TOKEN', null, -1);
								layer.msg("代管登录失败，请检查账号和密码！",{time:2000,icon:2});
							} 
						}
					});
				},function(){
					return;
				});
	},
	
	setAgent : function (){
		var _this = this;
		var rebates = new Array();
		var vipGrade = $(".agent_level2").val();
		var operationPassword = $(".password").val();
		var rebate1 = parseFloat($(".agent_forex").val());
		var rebate2 = parseFloat($(".agent_metal").val());
		var rebate3 = parseFloat($(".agent_oil").val());
		
		if (isNaN(rebate1)) {
			layer.tips("请选择正确的外汇内返.",".agent_forex",{time:2000,tips:[2,'#56a787']});
		} else if (isNaN(rebate2)) {
			layer.tips("请选择正确的金属内返.",".agent_metal",{time:2000,tips:[2,'#56a787']});
		} else if (isNaN(rebate3)) {
			layer.tips("请选择正确的原油内返.",".agent_oil",{time:2000,tips:[2,'#56a787']});
		} else if(operationPassword == ""){
			layer.tips("操作密码不能为空哦.",".password",{time:2000,tips:[2,'#56a787']});
		}else{
			rebates.push(rebate1);
			rebates.push(rebate2);
			rebates.push(rebate3);
			$.jsonRPC.request('adminAgentService.setAgent', {
				params : [_this.user_id, vipGrade, operationPassword, rebates],
				success : function(result) {
					layer.msg("设置成功！",{time:1500,icon:1});
				}
			});
		}
	}
	
};


//外部页面跳转到个人信息页面
//function checkBank(bankaccount_id){
//	checkBank.getOneBankAccount(bankaccount_id);
//}



//解绑
function unbindUser(login) {
	layer.confirm('您确定要解除<span class="red">MT4账号: '+login+' </span>与其网站账户的绑定吗？', {
		  btn: ['即刻解除','放弃'], //按钮
		  icon:3
		},function(){
			$.jsonRPC.request('adminUserService.unbindMT4User', {
				params : [login],
				success : function(result) {
					layer.msg("解绑成功！",{time:1000,icon:1});
					updateList();
				}
			});
		},function(){
			return;
		});
}





function donothing(){
	
}
function refreshDepositsList(){
	changeGuideDiv(3,true);
	if(userDetail != null){
		userDetail.getPageCapital(1);
	}
}


function updateList(){
	if(userDetail != null){
		userDetail.getUserDetail(1);
	}
}


function updateBank(){
	if(userDetail != null){
		userDetail.getBankAccount(1);
	}
}



/**
 * 禁用冻结或者解除禁用东街
 * @param id
 * @param nowscheme 当前的状态：DISABLED、UNDISABLED、FROZEN、UNFROZEN
 * @param scheme
 */
function disableuser(id,nowscheme,scheme){
	var schemeTip = "";
	if(nowscheme == "DISABLED"){
		schemeTip = "解除禁用";
	}else if(nowscheme == "UNDISABLED"){
		schemeTip = "立即禁用";
	}else if(nowscheme == "FROZEN"){
		schemeTip = "解除冻结";
	}else if(nowscheme == "UNFROZEN"){
		schemeTip = "立即冻结";
	}
	layer.confirm('您确定要<span class="font-red">'+schemeTip+'</span>该用户吗？', {
		  btn: [schemeTip,'放弃'], //按钮
		  icon:3
		},function(){
			$.jsonRPC.request("adminCheckService.freezeOrDisableUser", {
				params : [id, scheme],
				success : function(result) {
					if(result==0){
						layer.msg(schemeTip+"该用户成功。", {time:1000,icon:1}, function(){
							userDetail.getUserDetail();
						});
					}else{
						layer.msg(schemeTip+"该用户失败。", {time:2000,icon:2}, function(){
							userDetail.getUserDetail();
						});
					}
					
				}
			});
		},function(){
			return;
		});
}


/**
 * 获取用户最大可开mt4账号数目
 */
function getMaxMT4Count(){
	$.jsonRPC.request("adminSettingService.getList",{
		params:[],
		success:function(result){
			maxMT4Count = result.list[0].intValue;
		}
	});
}

function getImg(user_id) {
	// 获取图片
	$.jsonRPC.request("adminUserService.getproFileImage", {
		params : [user_id],
		success : function(result) {
			var html = "";
			// 获取图片
			if (result != null) {
				for ( var i in result.list) {
					imgList = result.list[i];
					html += '<div style="width:100px;height:150px;text-align:center;margin:5px;float:left;overflow: hidden;">'
							+ '<img border="0" style="margin:0px 0px 5px 0px;" src="/upload'
							+ imgList.path
							+ '" height="100px" />'
							+ '<a class="remove" href="javascript:removeProof('
							+ imgList.id
							+ ');"><span class="btn btn-primary"><span>移除</span></span></a>'
							+ '</div>';
				}
			}
			userDetail.getUserDetail();
			$(".imageShow").html(html);
		}
	});
}

function createMt4Login(){
	layer.confirm("确定要新开设一个MT4帐号给这个用户吗？",
			{btn:["确认","放弃"],icon:3},
			function(){
				$(".btn_new_mt4_user").attr("disabled", "disabled");
				$(".btn_new_mt4_user").removeClass("btn-primary");
				
				$.jsonRPC.request('adminUserService.addMT4User', {
					params : [_user_id],
					success : function(result) {
						$(".btn_new_mt4_user").removeAttr("disabled");
						$(".btn_new_mt4_user").addClass("btn-primary");
						window.alert("添加成功！");
						getMT4UserList();
					}
				});
			},function(){
				return;
			});
}

//代理人树
function createParentsTree(upId, path){
	$.jsonRPC.request('admin2UserService.getList', {params : [],
		success : function(result2) {
			if (result2) {
				var html = '';
				for (var i in result2.list) {
					var ele = result2.list[i].map;
					//滤掉自己和自己的客户
					if (ele.path.indexOf(path) == 0) {continue;}
					else {
						var spaces = '';
						for (var k=0;k<ele.level - 1;k++) { spaces += '　'; }
						html += '<option value="'+ele.id+'">'+spaces+'ID:'+ele.id+' - '+ele.name+' - ' +ele.mobile+ ' - ' + ele.email +'</option>';
					}
				}
				$("[name='upId']").html(html);
				if(upId != null){
					$("[name='upId']").val(upId);
					var selectedOption = $(".agent option[value='"+upId+"']");
					$(selectedOption).css("background","rgb(160,197,232)");
					$(selectedOption).attr("default","true");
					$(selectedOption).html($(selectedOption).html() + "—————当前推荐人");
				}
			}
		}
	});
}


/**
 * 根据登录的角色做角色分配
 */
function checkLoginRole(){
	var role = loginUser.role;
//隐藏按钮----------------------
	if(role != "OperationsManager"){//不是运维经理
		setBtnDisabled("#btn_setAgent");
		setBtnDisabled("#btn_bindMT4ForUser");
//		setBtnDisabled("#btn_holderUser");
	}
	
	
	if(role != "OperationsManager" && role != "CustomerServiceStaff"){//不是运维经理和客服
		setCannotEdit();
		setBtnDisabled("#btn_setAgent");
		setBtnDisabled("#btn_addMT4ForUser");
		setBtnDisabled("#btn_restpassword");
		setBtnDisabled("#btn_holderUser");
	}
	
	if(role == "CustomerServiceStaff"){//客服
		$(".select_state").attr("disabled","disabled");
		$(".disable_frozen").attr("disabled","disabled");
		$(".agent_level").attr("disabled","disabled");
		$(".agent").attr("disabled","disabled");
		setBtnDisabled("#btn_checkProfile");
	}
	
	if(role == "FinancialStaff" || role =="FinancialSuperior"){//财务
		
	}
	
	//将固定高度的tr移除掉
	$(".rightTR tr").each(function(i, tr){
		if($(tr).html().replace(/\s+/g,"") == ""){
			$(tr).remove();
		}
	});
}

function setCannotEdit(){
	$(".saveTipTr").remove();
	$("input").attr("disabled","disabled");
	$("select").attr("disabled","disabled");
	setBtnDisabled("#btn_updateProfile");
}

function setBtnDisabled(btn_selector){
	$(btn_selector).removeClass("btn-a");
	$(btn_selector).addClass("btn-e");
	$(btn_selector).css({"cursor":"default"});
	$(btn_selector).unbind("click"); //移除click
}
