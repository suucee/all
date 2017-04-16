var editUserDetail = null;
$(function() {
	editUserDetail = new EditUserDetail();
});
var EditUserDetail = function() {
	this.user_id = getParam("id", 0);
	if(this.user_id != 0){
		parent.localUserById(this.user_id);
	}
	this.profile_id=0;
	this.init();
};
EditUserDetail.prototype = {
	init : function() {
		var _this=this;
		this.getUserDetail();
		this.getDepositsList(1);
		this.getWithdrawal(1);
		this.checkUserState();
		$(".tab-button").click(function() {
			$(".tab-button").removeClass('btn-b');
			$(".tab-button").addClass('btn-f');
			$(this).removeClass('btn-f');
			$(this).addClass('btn-b');
			$index = $(".tab-button").index($(this));
			$(".tab-content").css("display", "none");
			$(".tab-content").eq($index).show();
		});
		$("#btn_new_mt4_user").click(function(){
			layer.confirm("确定要新开设一个MT4帐号给这个用户吗？",{btn:["确认新开","放弃"]},
				function(){
					$("#btn_new_mt4_user").attr("disabled", "disabled");
					$("#btn_new_mt4_user").removeClass("btn-primary");
					
					$.jsonRPC.request('adminUserService.addMT4User', {
						params : [_this.user_id],
						success : function(result) {
							$("#btn_new_mt4_user").removeAttr("disabled");
							$("#btn_new_mt4_user").addClass("btn-primary");
							layer.msg("添加成功！",{time:1000,icon:1});
							editUserDetail.getUserDetail();
						}
					});
				},
				function(){
					return;
				});
		});
		$("#btn_set_agent").click(function() {
			  _this.changeAgentLevel();
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
						     html += '<tr><td colspan="10" class="bold"></td></tr>';
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
							label_name = '待财务主管审核';
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
					//page
					if(result.buttons != ""){
						html += '<tr><td colspan="10"><div class="pagelist">'+result.buttons+'</td></tr>';
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
				// page
				if(result.buttons != ""){
					html += '<tr><td colspan="6"><div class="pagelist">' + result.buttons + '</td></tr>';
				}
	
				$("tbody#result_list_deposits").html(html);
			}
		}
	});
	},
	uploadFile : function() {
		var id = this.profile_id;
		$.ajaxFileUpload({
			url : '../../fileupload2/user-' + id + '.do', // 用于文件上传的服务器端请求地址
			secureuri : false, // 一般设置为false
			fileElementId : 'file_upload', // 文件上传空间的name属性 <input type="file"
			// id="file"
			dataType : 'json', // 返回值类型 一般设置为json
			success : function(data, status)// 服务器成功响应处理函数
			{
				if (data.status == "failed") {
					layer.msg(data.result,{time:2000,icon:2});
				} else {
					if ($("#attachment_id").val() == "") {
						$("#attachment_id").val(data['id']);
					} else {
						$("#attachment_id").val($("#attachment_id").val() + "," + data['id']);
					}
					layer.msg("上传成功！",{time:1000,icon:1});
					var html="";
					html += "<div style='width:250px;height:150px;text-align:center;margin:5px;float:left;overflow: hidden;'>";
					html += "<div style='background-image: url(/upload" + data.src + ");background-position: center;background-size:auto 100%;background-repeat: no-repeat;width: 250px;height: 150px;line-height: 150px;margin-bottom:5px;'>";
					html += "<div style='width: 250px;height: 150px;line-height: 150px;'>";
			    	html+="<a style='' href='javascript:editUserDetail.removeProof("+ data.id+");'  class='btn btn-e'><i class='fa fa-remove' aria-hidden='true'></i>&nbsp;&nbsp;移除</a>&nbsp;&nbsp;";
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
		$(".remove").attr("href","javascript:void(0);");
		$.jsonRPC.request("userService.removeImg",{
			params:[id],
			success:function(data){
				if(!data){
					layer.msg("删除失败！",{time:2000,icon:2});
				}
			getImg();
			}
	    });
   },
   checkUserState:function(){
   	var _this=this;
	$.jsonRPC.request('adminUserService.getOne', {
		params : [_this.user_id ],
		success : function(result) {
			if (result) {
				$(".id").html(result.id);
				$("[name='userId']").val(result.id);
				$("[name='email']").val(result.email);
				$("[name='mobile']").val(result.mobile);

				$(".vipGrade").html(result._vipGradeName);
				
				if (result.disable) {
					$(".id").html(result.id + ' <span class="red">已禁用</span>');
					$(".btn").hide();
					$("input, select").attr("disabled", "disabled");
					window.setTimeout('$(".btn").hide();', 2000);
				}
				
				switch (result.state) {
				case "UNVERIFIED":
					$(".profile_sbt").html('<a style="background:#a36000; color:white;margin:10px auto;" href="javascript:editUserDetail.updateProfile();" class="btn btn-primary" >更新用户资料</a>');
					break;
				case "AUDITING":
					$(".profile_sbt").html('<a style="color:white; margin:10px auto;" href="javascript:editUserDetail.updateProfile();" class="btn   btn-primary" >资料审核中，更新用户资料</a>');
					break;
				case "REJECTED":
					$(".profile_sbt").html('<a style="color:white; margin:10px auto;" href="javascript:editUserDetail.updateProfile();" class="btn   btn-primary" >资料被拒绝，更新用户资料</a>');
					break;
				case "VERIFIED":
					$(".profile_sbt").html('<a style="color:white; margin:10px auto;" href="javascript:editUserDetail.updateProfile();" class="btn   btn-primary" >帐户已激活，更新用户资料</a>');
					break;

				default:
					layer.msg("系统错误，请稍后再试",{time:2000,icon:2});
					break;
				}
				
				
			}
			
		}
	});
},
   updateProfile:function (){
   	var _this=this;
		var name = $(".name").val();
		var ename = $(".ename").val();
		//下拉选项cardType
		var cardType = $("#cardType").val();
		var cardID = $("#cardID").val();
		//下拉居住地区countryCode
		var countryCode = $("#countryCode").val();
		var address = $(".address").val();
		var cname = $(".cname").val();//公司名称
		var hangye = $(".hangye").val();//行业
		var userPosition = $(".userPosition").val();//职位
		var yearsr = $(".yearsr").val();//年收入
		var attachment_id = $("input[name='attachment_id']").val();
		
		if (name==""){
			layer.tips("姓名不能为空.",".name",{time:2000,tips:[2,'#56a787']});
			$(".name").focus();
		} else if (ename=="") {
			layer.tips("姓名拼音不能为空.",".ename",{time:2000,tips:[2,'#56a787']});
			$(".ename").focus();
		} else if (cardType=="") {
			layer.tips("请选择证件类型.","#cardType",{time:2000,tips:[2,'#56a787']});
			$("#cardType").focus();
		} else if (cardID=="") {
			layer.tips("请输入证件号码.","#cardID",{time:2000,tips:[2,'#56a787']});
			$("#cardID").focus();
		} else if (countryCode=="") {
			layer.tips("请选择居住国家.","#countryCode",{time:2000,tips:[2,'#56a787']});
			$("#countryCode").focus();
		} else{
			$.jsonRPC.request("admin2UserService.updateProfile",{
				params:[_this.user_id , name,ename,cardType,cardID,countryCode,address,cname,hangye
				        ,yearsr,userPosition,attachment_id],
				success : function(result){
					if (result) {
						layer.msg("资料更新成功！",{time:1000,icon:1});
					} else {
						layer.msg("资料更新失败！！",{time:2000,icon:2});
					}
					//getUserDetail();
				}
			});
		}
	},
};
