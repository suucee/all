var userDetail = null;
$(function() {
	userDetail = new UserDetail();
});
var UserDetail = function() {
	this.user_id = getParam("id", 0);
	
	if(this.user_id != 0){
		parent.localUserById(this.user_id);
	}
	$(".toeditprofile").html('<a  class="btn btn-a margin15"   href="./../admin/user_edit_detail.html?id='+this.user_id+'">编辑客户的资料</a>');
	this.init();
};
UserDetail.prototype = {
	init : function() {
		this.getUserDetail();
		this.getDepositsList(1);
		this.getWithdrawal(1);
		$(".tab-button").click(function() {
			$(".tab-button").removeClass('btn-b');
			$(".tab-button").addClass('btn-f');
			$(this).removeClass('btn-f');
			$(this).addClass('btn-b');
			$index = $(".tab-button").index($(this));
			$(".tab-content").css("display", "none");
			$(".tab-content").eq($index).show();
		});

	},
	getUserDetail : function() {
		var _this = this;
		$.jsonRPC.request("user2Service.getUserDetail", {
			params : [_this.user_id],
			success : function(result) { {
					if (result.map.users != null) {
						var item = result.map.users;
						$(".id").text(item.id);
						$(".email").text(item.email);
						$(".tel").text(item.mobile);
						switch (item.state) {
						case "UNVERIFIED":
							$(".state").html('<span class="label-pending label" >资料不全</span>（客户资料不全！）');
							break;
						case "AUDITING":
							$(".state").html('<span class="label-pending label" >审核中</span>（客户客户可修改或追加资料）');
							break;
						case "REJECTED":
							var comment = item._userProfilesComment;
							var causeHtml = ''; 
							if(comment != undefined && comment != ""){
								causeHtml = '<span class="label-important label" >被驳回</span> 驳回理由：'+comment+' （客户可修改资料重新提交）';
							}else{
								causeHtml = '<span class="label-important label" >被驳回</span>（客户可修改资料重新提交）';
							}
							$(".state").html(causeHtml);
							break;
						case "VERIFIED":
							$(".state").html('<span class="label-success label" >账户已激活</span>（不可更改资料）');
							break;

						default:
							layer.msg("系统错误，请稍后再试",{time:2000,icon:2});
							break;
						}
						if(item.disable){
							$(".state").html($(".state").html()+"<span class='red'>[禁]</span>");
						}
						if(item.frozen){
							$(".state").html($(".state").html()+"<span class='purple'>[冻]</span>");
						}
						var html = '';
						
						//manager和合规可以审核资料或者重新审核资料
						if(loginUser.role == "OperationsManager" || loginUser.role == "ComplianceOfficer"){
							//可以审核
							if(item.state != "VERIFIED"){
								html += '&nbsp;&nbsp;&nbsp;&nbsp;<a  class="btn btn-a"  href="./../admin/compliance_check_user.html?id='+item.id+'">审核资料</a>';
							}else{
								html += '&nbsp;&nbsp;&nbsp;&nbsp;<a  class="btn btn-a"  href="./../admin/compliance_check_user.html?id='+item.id+'">重新审核资料</a>';
							}
						}
						
						
                        if(loginUser.role == "OperationsManager"){
                        	html+='&nbsp;&nbsp;&nbsp;&nbsp;<a  class="btn btn-a"   href="./../admin/user_setting.html?id='+item.id+'">管理用户</a>';
                        } 
						if (loginUser.role == "OperationsManager" || loginUser.role == "CustomerServiceStaff") {
							html+= '&nbsp;&nbsp;&nbsp;&nbsp;<a  class="btn btn-a"  href="./../admin/user_edit_detail.html?id='+item.id+'">编辑资料</a>';
						}
						if (loginUser.role == "OperationsManager" || loginUser.role == "ComplianceOfficer") {
							if (item['disable']){
								html+= '&nbsp;&nbsp;&nbsp;&nbsp;<button class="btn btn-b" onclick="disableuser('+item.id+','+"'disable'"+')">解除禁用</button>';
							} else {
								html+= '&nbsp;&nbsp;&nbsp;&nbsp;<button class="btn btn-a" onclick="disableuser('+item.id+','+"'disable'"+')">禁用账号</button>';
							}
							if (item['frozen']){
								html+= '&nbsp;&nbsp;&nbsp;&nbsp;<button class="btn btn-b" onclick="disableuser('+item.id+','+"'freeze'"+')">解除冻结</button>';
							} else {
								html+= '&nbsp;&nbsp;&nbsp;&nbsp;<button class="btn btn-a"  onclick="disableuser('+item.id+','+"'freeze'"+')">冻结账号</button>';
							}
							
						}
						$(".toeditprofile").html(html);
					}
				} {
					if (result.map.profiles != null) {
						var item = result.map.profiles;
						$(".name").val(item.userName);
						$(".ename").val(item.userEName);
						//下拉选项cardType
						$("#cardType").val(item.cardType);
						$("#cardID").val(item.userIdCard);
						//下拉居住地区countryCode
						$("#countryCode").val(item.userNationality);
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
				} {
					if (result.map.attach != null) {
						var html = "";
						for (var i in result.map.attach.list) {
							var imgList = result.map.attach.list[i];
							html += "<div style='width:250px;height:150px;text-align:center;margin:5px;float:left;overflow: hidden;'>";
							html += "<div style='background-image: url(/upload" + imgList.path + ");background-position: center;background-size:auto 100%;background-repeat: no-repeat;width: 250px;height: 150px;line-height: 150px;margin-bottom:5px;'>";
							html += "<div style='width: 250px;height: 150px;line-height: 150px;'>";
							html += "&nbsp;";
							html += "<a style='' href='/upload" + imgList.path + "' target='_blank'  class='btn btn-g'><i class='fa fa-eye' aria-hidden='true'></i>&nbsp;&nbsp;查看</a>";
							html += "</div>";
							html += "</div>";
							html += "</div>";
						}
						if (html=="") {
						     html += '<tr><td colspan="10" class="bold">对不起，暂未检索到相关记录</td></tr>';
						}
						$(".imageShow").html(html);
					}
				} {
					if (result.map.userBankAccounts != null) {
						var html = '';
						for (var i in result.map.userBankAccounts.list) {
							var item = result.map.userBankAccounts.list[i];

							var label_class = "";
							var label_name = "";
							switch (item.state) {
							case 'WAITING':
								label_class = 'label-pending';
								label_name = '待审核';
								break;
							case 'AUDITED':
								label_class = 'label-success';
								label_name = '通过审核';
								break;
							case 'REJECTED':
								label_class = 'label-important';
								label_name = '已驳回， 驳回理由：'+item.bankComment;
								break;
							default:
								label_class = 'label-pending';
								label_name = '无状态';
								break;
							}
							 html += '<tr>' + '	<td><span class="label ' + label_class + '">' + label_name + '</span></td>' ;
							 html +=  '	<td>'+item.bankName + '</td>';
							 html +=  '	<td>' + item.accountName + '</td>' ;
							 html +=  '	<td>' + item.accountNo + '</td>' ;
							 html += '	<td>' + item.countryCode + '</td>' ;
							 html += '	<td class="actions_icons">&nbsp;</td>' ;
							 html +=  '</tr>';
							 html+='<tr>';
							 html+='<td colspan="6"  style="padding-left:0px;padding-right:0px;">';
							 html+='		<div class="layout imageShow" style="text-align: left;background-color:#e0e2e3;overflow-x:auto;"   class="imageShow">';
								for (var i in result.map.bankImg.list) {
									var imgList = result.map.bankImg.list[i];
									html += "<div style='width:250px;height:150px;text-align:center;margin:5px;float:left;overflow: hidden;'>";
									html += "<div style='background-image: url(/upload" + imgList.path + ");background-position: center;background-size:auto 100%;background-repeat: no-repeat;width: 250px;height: 150px;line-height: 150px;margin-bottom:5px;'>";
									html += "<div style='width: 250px;height: 150px;line-height: 150px;'>";
									html += "&nbsp;";
									html += "<a style='' href='/upload" + imgList.path + "' target='_blank'  class='btn btn-g'><i class='fa fa-eye' aria-hidden='true'></i>&nbsp;&nbsp;查看</a>";
									html += "</div>";
									html += "</div>";
									html += "</div>";
								} 
							 html+='        </div></td>';
							 html+='</tr>"';
						}
						if (html=="") {
						     html += '<tr><td colspan="10" class="bold">对不起，暂未检索到相关记录</td></tr>';
						}
						$("#bankAccountList").html(html);
					}
				}
				 {
					var item = result.map.userBalances;
					var sum=result.map.withdrawalsSum;
					var html = '<tr>';
					 html += '	<td class="sample-currency">USD</td>';
					 html += '	<td>' + toDate(item.updatedTime.time) + '</td>';
					 html +=  '	<td class="bold align-right">' + item.amountAvailable.toFixed(2)  + '&nbsp;&nbsp;USD</td>' ;
					 html +=  '	<td class="bold align-right">' + sum.toFixed(2) + '&nbsp;&nbsp;USD</td>' ;
					 html +=  '	<td>&nbsp;</td>' ;
					 html +=  '</tr>';

					$("tbody#result_list_ub").html(html);
				};
				{
					var html = '';
					for (var i in result.map.mt4UsersList.list) {
						var item =result.map.mt4UsersList.list[i];
						html += '<tr>';
						html += '	<td>'+item.login+'</td>';
						html += '	<td class="amount bold">'+item.balance.toFixed(2)+'</td>';
						html += '	<td>'+toDate(item.creatTime.time)+'</td>';
						html += ' <td>'+item.group+'</td>';
						html += ' <td>';
						html += '</td>';
						html +=  '</tr>';
				    }
					if (html=="") {
					     html += '<tr><td colspan="10" class="bold">对不起，暂未检索到相关记录</td></tr>';
					}
					$("tbody#mt4result_list").html(html);
				}
				{
				
						var list = result['map']['monthRebate']['list'];
						var rebateCount = result['map']['rebateCount'];
						var html="";
						if(list.length==0){
						for (var ii = 0; ii < list.length; ii++) {
							var obj = list[ii];
							html+= '<tr>';
							html+= '<td>' + obj['monthstr'] + '</td>';
							html+= '<td>' + obj['rebateCount'].toFixed(2)  + '</td>' ;
			                html+= '</tr>';
						}
						html+='<tr>';
						html+='<td>我的返佣收入累计：<span class="bold"> ' + rebateCount.toFixed(2) + '（USD）</span></td>';
						html+='<td>&nbsp;</td>';
						html+='</tr>';
						}
						if (html=="") {
						     html += '<tr><td colspan="10" class="bold">对不起，暂未检索到相关记录</td></tr>';
						}
			            $('#monthrebate_list').html(html);	
				}
			}
		});
	},
	getWithdrawal:function(pageNo){
		var _this = this;
		$.jsonRPC.request("user2Service.getPageWithdrawal",{
			params:[_this.user_id,pageNo, 10,"javascript:complianceCheckUser.getWithdrawal(??);"],
			success:function(result){
				if (result != null) {
					_pageNo = pageNo;
					
					var html = '';
					for (var i in result.list.list) {
						var item = result.list.list[i];
						var label_class = "";
						var label_name = "";
						var show_cancel = false;
						switch (item.state) {
						case 'WAITING':
							label_class = 'label-pending';
							label_name = '待审核';
							show_cancel = true;
							break;
						case 'PENDING_SUPERVISOR':
							label_class = 'label-pending';
							label_name = '待审核（大额出金）';
							show_cancel = true;
							break;
						case 'AUDITED':
							label_class = 'label-pending';
							label_name = '待汇款';
							break;
						case 'REMITTED':
							label_class = 'label-success';
							label_name = '已汇出';
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
							label_class = 'label-pending';
							label_name = '用户取消';
							break;
						}

						html += '<tr>'
							+ '<td><a style="text-decoration: underline;" target="_blank" href="webaccount_withdrawal_detail.html#'+item.id+'">#'+item.id+'</a></td>' 
							+ '<td class="amount bold">'+item.amount.toFixed(2)+' '+item.currency+'</td>'
							+ '<td><span class="label '+label_class+'">'+label_name+'</span></td>'
							+ '<td>'+toDate(item.creatTime.time)+'</td>'
							+ '<td>'+(item.auditedTime == null ? ' - ' : toDate(item.auditedTime.time))+'</td>'
							+ '<td>&nbsp;</td>'
							+ '</tr>';
				    }
					if(result.buttons!=null&&result.buttons!=""){
				    	html += '<tr><td colspan="10"><div class="pagelist">'+result.buttons+'</td></tr>';
				    }
					if (html=="") {
					     html += '<tr><td colspan="10" class="bold">对不起，暂未检索到相关记录</td></tr>';
					}
					
					$("tbody#result_list_withdrawal").html(html);
				}	
			}
		});
	},
	getDepositsList:function(pageNo){
		var _this = this;
		$.jsonRPC.request("user2Service.getPageDeposits", {
		params : [ _this.user_id,pageNo, 20, "javascript:complianceCheckUser.getDepositsList(??);" ],
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
					case 'ACCEPTED':
						label_class = 'label-success';
						label_name = '已到账';
						show_remove = true;
						break;
					case 'PENDING_PAY':
						label_class = 'label-pending';
						label_name = '待付款';
						show_remove = true;
						break;
					case 'PENDING_AUDIT':
					case 'PENDING_SUPERVISOR':
						label_class = 'label-pending';
						label_name = '审核中';
						show_remove = true;
						break;
					case 'REJECTED':
						label_class = 'label-important';
						label_name = '已驳回';
						show_remove = true;
						break;
					}
	
					html += '<tr><td>'
						+ item.id
						+ '</td>'
						+ '    <td><span class="label '
						+ label_class
						+ '">'
						+ label_name
						+ '</span></td>'
						+ '<td class="amount bold">'
						+ item.amount.toFixed(2)
						+ '</td>'
						+ '<td>'
						+ item.orderNum
						+ ' </td>'
						+ '    <td>'
						+ toDate(item.creatTime.time)
						+ '</td>'
						+ '    <td>'
						+ (item.paymentTime== null ? '--' : toDate(item.paymentTime.time))
						+ '</td>'
					    + '</tr>';
				}
				if(result.buttons!=null&&result.buttons!=""){
			    	html += '<tr><td colspan="10"><div class="pagelist">'+result.buttons+'</td></tr>';
			    }
				if (html=="") {
				     html += '<tr><td colspan="10" class="bold">对不起，暂未检索到相关记录</td></tr>';
				}
	
				$("tbody#result_list_deposits").html(html);
			}
		}
	});
	}
};

function disableuser(id, scheme){
	$.jsonRPC.request("adminCheckService.freezeOrDisableUser", {
		params : [id, scheme],
		success : function(result) {	
			userDetail.getUserDetail();
		}
	});
}
