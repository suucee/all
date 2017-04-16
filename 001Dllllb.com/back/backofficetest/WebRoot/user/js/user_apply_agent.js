const PAGESIZE = 10;
$(document).ready(function() {
	$("#btn_search").click(function(){
		refreshList(1);
	});
	
	refreshList(1);
});

function refreshList(pageNo) {
	$.jsonRPC.request("adminUserService.getApplyAgent", {
		params : [pageNo, PAGESIZE, "javascript:refreshList(??);", $("#keyword").val()],
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
				var do_html = '';
				if (loginUser.role == "OperationsManager" || loginUser.role == "ComplianceOfficer") {
					if(item.path!=null){
						do_html += '<a class="btn btn-primary" style="color:#ffffff;" target="_blank" href="/upload'+item.path+'"   title="下载代理协议"><i class="icon-cloud-download"></i></a> &nbsp;&nbsp;';
					}
					do_html += '<button class="btn btn-primary"  style="position: relative; ';
					do_html += 'cursor: pointer;overflow: hidden; display: inline-block;*display: inline; *zoom: 1"  title="上传代理协议">';
					do_html += '<input type="file" name="agreementfile'+item.uId+'" id="agreementfile'+item.uId+'" onchange="uploadFile('+item.uId+')" style="position: absolute;font-size: 100px;right: 0;top: 0;';
					do_html += 'opacity: 0;filter: alpha(opacity=0);cursor: pointer"/><i class="icon-cloud-upload"></i></button>';
					do_html += '&nbsp;&nbsp;<button class="btn btn-primary" onclick="sendEmail('+item['attach_id']+')" title="发送协议邮件"><i class="icon-envelope"></i></button>';
					do_html += '&nbsp;&nbsp;<button class="btn btn-primary"  onclick="javascript:editUser('+item['uId']+');" title="设置成代理"><i class="icon-cogs"></i></button> ';
					
				}
				if (loginUser.role == "OperationsManager" || loginUser.role == "CustomerServiceStaff") {
					edit_html = '<button class="btn btn-primary" style="margin-left:10px" onclick="javascript:editUser('+item['uId']+');"><i class="icon-cogs"></i></button>';
				}
				html += '<tr>'
						+ '<td><a href="user_detail.html?id='+item.uId+'" target="_blank"><i class="icon-user"></i> '+(item._userName != null ? item._userName : '(暂无姓名)')+(item.disable ? ' <span class="red">[禁]</span>' : '') + (item.is_frozen ? ' <span class="purple">[冻]</span>' : '')+'</a></td>'
						+ '<td>' + item._vipGradeName + '</td>'
						+ '<td><span class="label '	+ label_class + '">' + label_name + '</span></td>'
						+ '<td>' + item.email + '</td>'
						+ '<td>' + item.mobile + '</td>'
						+ '<td>' + item.referralCode + ' </td>'
						+ '<td>' + toDate(item.registrationTime.time) + '</td>'
						+ '<td>'
						+ do_html 
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


function uploadFile(id) {
		$.ajaxFileUpload({
			url : './../fileupload2/agent_agreement-'+id+'.do', // 用于文件上传的服务器端请求地址
			secureuri : false, // 一般设置为false
			fileElementId : 'agreementfile'+id, // 文件上传空间的name属性 <input type="file"
			// id="file"
			dataType : 'json', // 返回值类型 一般设置为json
			success : function(data, status) // 服务器成功响应处理函数
			{
				if (data.status == "failed") {
					layer.msg(data.result,{time:2000,icon:2});
				} else {
					layer.msg("上传成功！",{time:1000,icon:1});
					refreshList(_pageNo);
				}
			},
			error : function(data, status, e)// 服务器响应失败处理函数
			{
				layer.msg("文件上传失败",{time:2000,icon:2});
			}
		});
}


function editUser(id) {
	window.open("user_edit_profile.html?userId="+id, "_blank");
}
function sendEmail(id){
	if(id==null){
		alert("请先上传协议 ，在发送邮件！");
		return;
	}
	$.jsonRPC.request("adminUserService.sendAagreementEmail", {
		params : [id],
		success : function(result) { 
			if(result){
				alert("代理协议发送成功！");
			}
		   
		}
	});
}