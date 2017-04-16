var vipGrade = -1;
var _userid=getParam("id", "0");
var _up_id = null;
/**
 * 默认推荐人的所有上级节点数组
 */
var defaultAgentParentsIdList = new Array();;
var defaultAgentId = -1;

$(document).ready(function() {
	if(_userid != 0){
		parent.localUserById(_userid);
	}
	
	//刷新结构树
	parent.refreshTreeData();
	//定位结构树
	window.setTimeout(function(){
		parent.localById (_userid);
	}, 1500);

	checkUserState();
	
	$("#btn1").click(function(){
		getUserlist();
	});
	if (loginUser.role == 'OperationsManager') {
//		$("[name='email']").removeAttr("disabled");
//		$("[name='mobile']").removeAttr("disabled");
//		$("[name='upId']").removeAttr("disabled");
	
		$("#btn_modify_account").show();
		$("#btn_set_agent").show();
		$("#btn_hold_user").show();
		$(".changeLevel").show();
	}
//	$("#btn_modify_account").click(function(){
//		var email = $("[name='email']").val();
//		var mobile = $("[name='mobile']").val();
//		var upId = $("[name='upId']").val();
//		if (upId == null) {upId = 0;}
//		
//		$.jsonRPC.request("admin2UserService.modifyAccount", {
//			params : [_userid, email, mobile],
//			success : function(result) {
//				checkUserState();
//				layer.msg("更新账号成功！",{time:1000,icon:1});
//			}
//		});
//	});
	$("#btn_restpassword").click(function(){
		layer.confirm("您确定要重置这个用户的网站登录密码和支付密码为随机密码吗？<br/>重置密码完成后将发送邮件通知用户。",
				{btn:["确认重置","放弃"]},
				function(){
					$.jsonRPC.request("adminUserService.resetUserPassword", {
						params : [_userid],
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
	});
	$("#btn_hold_user").click(function(){
		layer.confirm("温馨提示：<br/>1、代管是十分危险的功能，如若操作不当，将可能导致后续操作会出现“权限不够”的问题，那么需要重新登录。<br/>"+
				"2、在使用代管功能时请勿直接关闭浏览器窗口，一定保证正常登出。",{btn:["确认使用","放弃"]},function(){
					$.jsonRPC.request('admin2UserService.holdUser', {
						params : [_userid,window.location.href],
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
	});
	$("#btn_set_agent").click(function() {
		 changeAgentLevel();
	});

	getUserlist()
});


function openHolderIframe(url){
	//iframe窗	  
   var index = layer.open({
		type: 2,
		title: '您正在使用代管用户功能。注意：在该模式下请勿刷新浏览器',
		shadeClose: true,
		shade: false,
		maxmin: true, //开启最大化最小化按钮
		area: ['980px', '600px'],
		content: url,
		end:function(){
			holderLogout();
		}
    });
   
//   layer.full(index);
}


function holderLogout() {
	$.jsonRPC.request('session2Service.logout', {
		params : [],
		success : function(result) {
			window.localStorage.clear();
			delCookie("TOKEN");
			if (result) {// 代管退出Success
				locStorage("loginUser", result);
				setCookie('TOKEN', result.token, 30 * 24);
//				window.close();
//				window.open(result.redirectUrl, '_self');
			} else {
				window.open('../common/login.html', '_self');
			}
		}
	});
}







function changeAgentLevel(){
	if (_userid> 0) {
		var vipGrade = $("[name='vipGrade']").val();
		var operationPassword = $("[name='operationPassword']").val();
		
		$.jsonRPC.request('adminAgentService.setAgent', {
			params : [_userid, vipGrade, operationPassword],
			success : function(result) {
				layer.msg("设置级别成功！刷新中...",{time:2000,icon:1},function(){
					location.reload();
				});
			}
		});
	}else{
		layer.msg("用户不存在！",{time:2000,icon:2});
	}
}
//获取用户列表
function getUserlist() {
	$('tbody#result_list1').html("");
	$.jsonRPC.request("user2RankService.getUserRank", {
		params : [$("#keyword").val()],
		success : function(result) {	
			$("#thetable").treetable("destroy");	
			list = result['map']['list']['list'];
			pid = result['map']['pid'];
			if(list!=null&&list.length>0){
				if($("#keyword").val()==""){
					maketree(list, pid);
					$("#thetable").treetable({expandable: true});
				}else{
					var html="";
					for (var ii = 0; ii < list.length; ii++) {
						var obj = list[ii]['map'];
						var item = obj;
							var registrationTime="--";
							var parentRegistrationTime="--";
							var disable_html = '';
							var frozen_html = '';
							var edit_html = '';
							if (loginUser.role == "OperationsManager" || loginUser.role == "CustomerServiceStaff") {
								edit_html = '&nbsp;<a  class="btn btn-a"   onclick="parent.openNewWindow('+"'./../admin/user_setting.html?id="+obj.user_id+"'"+',updateList)">设置</a>';
							}
							
							obj.registrationTime== null ? registrationTime='--' : registrationTime=toDate(obj.registrationTime.time);
							obj.parentRegistrationTime== null ? parentRegistrationTime='--' : parentRegistrationTime=toDate(obj.parentRegistrationTime.time);
							switch (obj.state) {
							case 'UNVERIFIED':
								label_class = 'label-important';
								label_name = '资料不全';
								break;
							case 'VERIFIED':
								label_class = 'label-success';
								label_name = '已通过';
								break;
							case 'REJECTED':
								label_class = 'label-cancel';
								label_name = '被驳回';
								break;
							case 'AUDITING':
								label_class = 'label-pending';
								label_name = '审核中';
								break;
							default:
								label_class = 'label-pending';
								label_name = '';
							}
							user_name = obj['user_name'] == null || obj['user_name'] == "" ? '<span class="gray">(暂无姓名)</span>' : obj['user_name'];
							var canSetParent = '<button class="btn btn-a" onclick="javascript:setParent('+item.user_id+')">设为推荐人</button>';
							var cannotSetParent = '<button class="btn btn-e" style="cursor:default;">已是推荐人</button>';
							var self = '<button class="btn btn-e" style="cursor:default;">不能是自己</button>';
							
							
							html+= '<tr>'
								+ '<td>' + user_name + '</td>' 
								+ '<td>' + item['_vipGradeName'] + '</td>' 
								+ '<td><span  class="label '+label_class +'" >' + label_name+ '</span></td>' 
								+ '<td>' + obj['mobile'] + '</td>' 
								+ '<td>' + obj['email'] + '</td>' 
								+ '<td>' + registrationTime.substr(0, 10) + '</td>' 
								+ '<td>' + obj['user_code']+'</td>' 
								+'<td>'+(_up_id == item.user_id?cannotSetParent :(_userid == item.user_id?self:canSetParent))+'</td>'
								+ '</tr>';
					}
					
					$('tbody#result_list1').html(html);
				}
			}else{
				$("tbody#result_list1").html('<tr><td colspan="5">对不起，没有检索到相关内容。</td></tr>');
				return;
			}
		}
	});
}



function maketree(list,pid){
	for (var ii = 0; ii < list.length; ii++) {
		var obj = list[ii]['map'];
		var item = obj;
		if (pid==obj['up_id']){
			var registrationTime="--";
			var parentRegistrationTime="--";

			var disable_html = '';
			var frozen_html = '';
			
			obj.registrationTime== null ? registrationTime='--' : registrationTime=toDate(obj.registrationTime.time);
			obj.parentRegistrationTime== null ? parentRegistrationTime='--' : parentRegistrationTime=toDate(obj.parentRegistrationTime.time);
			switch (obj.state) {
			case 'UNVERIFIED':
				label_class = 'label-important';
				label_name = '资料不全';
				break;
			case 'VERIFIED':
				label_class = 'label-success';
				label_name = '已通过';
				break;
			case 'REJECTED':
				label_class = 'label-cancel';
				label_name = '被驳回';
				break;
			case 'AUDITING':
				label_class = 'label-pending';
				label_name = '审核中';
				break;
			default:
				label_class = 'label-pending';
				label_name = '';
			}
			user_name = obj['user_name'] == null || obj['user_name'] == "" ? '<span class="gray">(暂无姓名)</span>' : obj['user_name'];
			
			var canSetParent = '<button class="btn btn-set btn-a" onclick="javascript:setParent('+item.user_id+')">设为推荐人</button>';
			var cannotSetParent = '<button class="btn btn-set btn-e" style="cursor:default;">已是推荐人</button>';
			var self = '<button class="btn btn-set btn-e" style="cursor:default;">不能是自己</button>';
			
			
			html = '<tr data-tt-parent-id="'+obj['up_id']+'" data-tt-id="'+obj['user_id']+'">'
				+ '<td>'+ user_name + '</td>' 
				+ '<td>' + item['_vipGradeName'] + '</td>' 
				+ '<td><span  class="label '+label_class +'" >' + label_name+ '</span></td>' 
				+ '<td>' + obj['mobile'] + '</td>' 
				+ '<td>' + obj['email'] + '</td>' 
				+ '<td>' + registrationTime.substr(0, 10) + '</td>' 
				+ '<td>' + obj['user_code']+'</td>' 
				+'<td>'+(_up_id == item.user_id?cannotSetParent :(_userid == item.user_id?self:canSetParent))+'</td>'
				+ '</tr>';
			
			$('tbody#result_list1').append(html);
			maketree(list, obj['user_id'])
		}
	}
}





function checkUserState(){
	$.jsonRPC.request('adminUserService.getOne', {
		params : [_userid ],
		success : function(result) {
			if (result) {
				//上级的ID
				_up_id = result._up_id;
				console.log(_up_id);
				defaultAgentId = _up_id;
				//自己的ID
				vipGrade = result.vipGrade;
				$("select[name='vipGrade']").val(vipGrade);
				
				$(".email").html(result.email);
				$(".mobile").html(result.mobile);
				$(".name").html(result._name);
				$(".vipGrade").html(result._vipGradeName);
				
				if (result.disable) {
					$(".id").html(result.id + ' <span class="red">已禁用</span>');
					$(".btn").hide();
					$(".btn-mini").show();
					$("input, select").attr("disabled", "disabled");
					window.setTimeout('$(".btn").hide();$(".btn-mini").show();', 2000);
				}
				switch (result.state) {
				case "UNVERIFIED":
					$(".state").html('<span class="label-pending label" >资料不全</span>（资料不全！等待客户补全资料）');
					break;
				case "AUDITING":
					$(".state").html('<span class="label-pending label" >审核中</span>（客户可修改或追加资料）');
					break;
				case "REJECTED":
					var comment = result._userProfilesComment;
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
				if(result.disable){
					$(".state").html($(".state").html()+"<span class='red'>[禁]</span>");
				}
				if(result.frozen){
					$(".state").html($(".state").html()+"<span class='purple'>[冻]</span>");
				}
				
			}
			
		}
	});
}


function setParent(id){

	$.jsonRPC.request('admin2UserService.setParent', {
		params : [_userid,id ],
		success : function(result) {
			layer.msg("设置推荐人成功！刷新中...",{time:2000,icon:1},function(){
				location.reload();
			});
		}
	});
}


