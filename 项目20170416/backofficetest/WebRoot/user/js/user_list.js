const PAGESIZE = 10;

var scheme ="";
$(document).ready(function() {
	scheme=getParam("scheme", "all");
	switch (scheme) {
	case "all":
		$(".title").text("所有用户");
		break;
	case "unverified":
		$(".title").text("资料不全用户");
		break;
	case "auditing":
		$(".title").text("待审核用户");
		break;
	case "verified":
		$(".title").text("已审核用户");
		break;
	case "rejected":
		$(".title").text("已拒绝用户");
		break;
	case "frozen":
		$(".title").text("已冻结用户");
		break;
	case "disabled":
		$(".title").text("已禁用用户列表");
		break;
	default:
		$(".title").text("用户列表");
	}
	$("#btn_search").click(function(){
		refreshList(1);
	});
	
	refreshList(1);
});

function refreshList(pageNo) {
	$.jsonRPC.request("adminUserService.getPage", {
		params : [pageNo, PAGESIZE, "javascript:refreshList(??);", scheme, $("#keyword").val()],
		success : function(result) { 
		if (result != null) {
			_pageNo = pageNo;
			var html = '';
			for ( var i in result.list.list) {
				var item = result.list.list[i].map;
				var label_class = "";
				var label_name = "";
				
				switch (item.state) {
				case 'UNVERIFIED':
					label_class = 'label-pending';
					label_name = '资料不全';
					break;
				case 'AUDITING':
					label_class = 'label-pending';
					label_name = '待审核';
					break;
				case 'VERIFIED':
					label_class = 'label-success';
					label_name = '已审核';
					break;
				case 'REJECTED':
					label_class = 'label-important';
					label_name = '已驳回';
					break;
				default:
					label_class = 'label-pending';
					label_name = '无状态';
				}
				
				var disable_html = '';
				var frozen_html = '';
				var edit_html = '';
				
				if (loginUser.role == "OperationsManager" || loginUser.role == "ComplianceOfficer") {
					if (item['disable']){
				     	disable_html = '<button class="btn btn-primary" onclick="disableuser('+item['uId']+','+"'disable'"+')">已禁用</button>';
					} else {
				     	disable_html = '<button class="btn" onclick="disableuser('+item['uId']+','+"'disable'"+')">禁用</button>';
					}
					if (item['is_frozen']){
						frozen_html = '<button class="btn btn-primary" style="margin-left:10px" onclick="disableuser('+item['uId']+','+"'freeze'"+')">已冻结</button>';
					} else {
						frozen_html = '<button class="btn" style="margin-left:10px" onclick="disableuser('+item['uId']+','+"'freeze'"+')">冻结</button>';
					}
				}
				if (loginUser.role == "OperationsManager" || loginUser.role == "CustomerServiceStaff") {
					edit_html = '<button class="btn btn-primary" style="margin-left:10px" onclick="javascript:editUser('+item['uId']+');">编辑</button>';
				}
				html += '<tr>'
						+ '<td><a href="user_detail.html?id='+item.uId+'" target="_blank"><i class="icon-user"></i> '+(item._userName != null ? item._userName : '(暂无姓名)')+(item.disable ? ' <span class="red">[禁]</span>' : '') + (item.is_frozen ? ' <span class="purple">[冻]</span>' : '')+'</a></td>'
						+ '<td>' + item._vipGradeName + '</td>'
						+ '<td><span class="label '	+ label_class + '">' + label_name + '</span></td>'
						+ '<td>' + item.email + '</td>'
						+ '<td>' + item.mobile + '</td>'
						+ '<td>' + item.referralCode + ' </td>'
						+ '<td>' + toDate(item.registrationTime.time) + '</td>'
						+ '<td><button class="btn btn-primary" style="color:#fff;" onclick="javascript:window.open(\'user_detail.html?id='+item.uId+'\', \'_blank\');">查看</button>'
						+ edit_html + '<br />'
						+ disable_html
						+ frozen_html
						+ '</td>'
					    + '</tr>';
			}
			// page
			html += '<tr><td colspan="9"><div class="pagelist">'
					+ result.buttons + '</td></tr>';

		
			$("tbody#result_list").html(html);
					
		}					
	}
	});
}

function disableuser(id, scheme){
	$.jsonRPC.request("adminCheckService.freezeOrDisableUser", {
		params : [id, scheme],
		success : function(result) {	
			refreshList(_pageNo);
		}
	});
}

function editUser(id) {
	window.open("user_edit_profile.html?userId="+id, "_blank");
}