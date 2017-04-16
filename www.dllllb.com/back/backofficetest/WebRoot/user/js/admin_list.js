const PAGESIZE = 10;
var _pageNo = 1;

var SCHEMES = {
	'all' : '运维团队',
	'operations_manager' : '运维经理',
	'compliance_officer' : '合规',
	'financial_staff' : '财务',
	'financial_superior' : '财务主管',
	'customer_service_staff' : '客服',
	'risk_management_commissioner' : '风控',
	'disabled' : '已禁用',
};
$(document).ready(function(){
	$("#popup_container").load("_popup_admin.html");
	$("#popup_container_add").load("_popup_addadmin.html");
	
	$("#btn_add").click(function(){
		$(".add_admin").fadeIn(200);
	});
	
	refreshList(1);
});

function refreshList(pageNo) {
	_pageNo = pageNo;
	var scheme = getParam("scheme", "all");
	$(".title").text(SCHEMES[scheme]);
	
	$.jsonRPC.request('adminAdminService.getPage', {
		params : [pageNo, PAGESIZE, "javascript:refreshList(??);", scheme, $("#keyword").val()],
		success : function(result) {
			var html = '';
			for (var i in result.list.list) {
				var item = result.list.list[i];
				var status = item.state;
				var label_class = "";
				var label_name = "";
				
				switch (item.role) {
				case 'OperationsManager':
					label_class = 'label-pending';
					label_name = '运维经理';
					break;
				case 'ComplianceOfficer':
					label_class = 'label-particular-filled';
					label_name = '合规';
					break;
				case 'FinancialStaff':
					label_class = 'label-warning';
					label_name = '财务';
					break;
				case 'FinancialSuperior':
					label_class = 'label-cancel';
					label_name = '财务主管';
					break;
				case 'CustomerServiceStaff':
					label_class = 'label-important';
					label_name = '客服';
					break;
				case 'Webmaster':
					label_class = 'label-important';
					label_name = '网站管理员';
					break;
				case 'RiskManagementCommissoner':
					label_class = 'label-important';
					label_name = '风控';
					break;
				}
				html += '<tr>'
					+ '    <td style="text-align:center;">'+item.id+'</a></td>' 
					+ '    <td>'+(item.disabled ? '<span class="red">禁用</span>' : '<span class="green">启用</span>')+'</td>'
					+ '    <td><span class="label '+label_class+'">'+label_name+'</span></td>'
					+ '    <td>'+item.showName+'</td>'
					+ '    <td>'+item.account+'</td>'
					+ '    <td>'+toDate(item.creatTime.time).substr(0, 16)+'</td>'
					+ '    <td class="amount bold">'+'</td>'
					+ '    <td style="text-align:center;">'
					+ (item.role == 'OperationsManager' ? '--' : ' <button style="padding:2px 4px;margin-left:4px;" onclick="javascript:openPopup('+item.id+');" class="btn btn-medium btn-primary">操作</button>')
					+ '</td></tr>';
		    }
			html += '<tr><td colspan="10"><div class="pagelist">'+result.buttons+'</td></tr>';
			
			$("tbody#result_list").html(html);
		}
	});
}
function openPopup(id) {
	if (id > 0) {
		$.jsonRPC.request('adminAdminService.getOne', {
			params : [id],
			success : function(result) {
				if (result) {
					var html = '<option value="">-- 请选择 --</option>';
					var label_class = "";
					var label_name = "";
					switch (result.role) {
					case 'OperationsManager':
						label_class = 'label-pending';
						label_name = '运维经理';
						break;
					case 'ComplianceOfficer':
						label_class = 'label-particular-filled';
						label_name = '合规';
						break;
					case 'FinancialStaff':
						label_class = 'label-warning';
						label_name = '财务';
						break;
					case 'FinancialSupervisor':
						label_class = 'label-cancel';
						label_name = '财务主管';
						break;
					case 'CustomerServiceStaff':
						label_class = 'label-important';
						label_name = '客服';
						break;
					case 'Webmaster':
						label_class = 'label-important';
						label_name = '网站管理员';
						break;
					}
					if (result.disabled) {
						html += '<option value="ENABLE">ENABLE - 启用账号</option>';
					} else {
						html += '<option value="DISABLE">DISABLE - 禁用账号</option>';
					}
					
					$(".popup_dialog .role").html('<span style="font-size:16px;padding:4px;" class="label '+label_class+'">'+label_name+'</span>');
					$(".popup_dialog .oldState").html(result.disabled ? '<span class="red">已禁用</span>' : '<span class="green">启用中</span>');
					$('.popup_dialog [name="action"]').html(html);
					
					$('.popup_dialog [name="operationPassword"]').val("");
					$(".popup_dialog .id").html(result.id);
				}
			}
		});
		
		$(".admin_list").fadeIn(200);
	} else {
		
	}
}

function closePopup() {
	$(".popup_dialog").fadeOut(200);
}

function doPopup() {
	var operationPassword = $(".popup_dialog input[name='operationPassword']").val();
	var action = $(".popup_dialog select[name='action']").val();
	var memo = $(".popup_dialog textarea[name='memo']").val();
	var id = parseInt($(".popup_dialog .id").html());
	
	if (operationPassword.length < 6) {
		window.alert("操作面不得低于6位!");
	} else if (!(id > 0)) {
		window.alert("ID不能为0!");
	} else {
		$.jsonRPC.request('adminAdminService.edit', {
			params : [id, action, memo, operationPassword],
			success : function(result) {
				if (result == "操作成功") {
					window.alert(result);
					closePopup();
				} else {
					window.alert(result);
				}
				
				//Refresh
				refreshList(_pageNo);
			}
		});
	}
}
	function doAddPopup() {
		var role = $(".popup_dialog .a_role").val();
		var email = $(".popup_dialog .a_email").val();
		var password = $(".popup_dialog .a_password").val();
		var password2 = $(".popup_dialog .a_password1").val();
		var show_name = $(".popup_dialog .a_showName").val();
		var operationPassword = $(".popup_dialog .a_operationPassword").val();
		
		if (operationPassword.length < 6) {
			window.alert("操作密码不得低于6位!");
		} else if (role == "") {
			window.alert("请选择管理员角色!");
		} else if (email == "") {
			window.alert("请填写登录账号!");
		} else if (password.length < 6 || password.length > 20) {
			window.alert("请输入6-20位登录密码!");
		} else if (password != password2) {
			window.alert("两次密码不一致!");
		} else if (show_name == "") {
			window.alert("请填写显示名称!");
		} else {
			$.jsonRPC.request('adminAdminService.add', {
				params : [role, email, password, show_name,operationPassword],
				success : function(result) {
					if (result > 0) {
						window.alert("添加管理员成功");
						closePopup();
						getCounts();
					} else {
						window.alert("添加管理员失败");
					}
					
					//Refresh
					refreshList(_pageNo);
				}
			});
		}
}
