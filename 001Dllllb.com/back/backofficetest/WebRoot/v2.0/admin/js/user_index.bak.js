var user_page=1;
$(function(){
	getUserlist(user_page);
	$("#btn1").click(function(){
		getUserlist(1);
	});
});


//获取用户列表
function getUserlist() {
	$('#result_list').html("");
	$.jsonRPC.request("user2RankService.getUserRank", {
		params : [$("#keyword").val()],
		success : function(result) {	
			$("#thetable").treetable("destroy");	
			list = result['map']['list']['list'];
			pid = result['map']['pid'];
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
						/*if (loginUser.role == "ComplianceOfficer" || loginUser.role == "OperationsManager" ) {
							if (item['disable']){
						     	disable_html = '<button class="btn btn-primary" style="margin-left:10px" onclick="disableuser('+item['user_id']+','+"'disable'"+')">已禁用</button>';
							} else {
						     	disable_html = '<button class="btn" style="margin-left:10px" onclick="disableuser('+item['user_id']+','+"'disable'"+')">禁用</button>';
							}
							if (item['is_frozen']){
								frozen_html = '<button class="btn btn-primary" style="margin-left:10px" onclick="disableuser('+item['user_id']+','+"'freeze'"+')">已冻结</button>';
							} else {
								frozen_html = '<button class="btn" style="margin-left:10px" onclick="disableuser('+item['user_id']+','+"'freeze'"+')">冻结</button>';
							}
						}*/
						if (loginUser.role == "OperationsManager" || loginUser.role == "CustomerServiceStaff") {
							edit_html = '&nbsp;<a  class="btn btn-a"   onclick="parent.openNewWindow('+"'./../admin/user_edit_detail.html?id="+obj.user_id+"'"+',updateList)">编辑</a>'
							+'&nbsp;<a  class="btn btn-a"   onclick="parent.openNewWindow('+"'./../admin/user_setting.html?id="+obj.user_id+"'"+',updateList)">设置</a>';
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
						html+= '<tr>'
							+ '<td> ' + user_name + '</td>' 
							+ '<td>' + item['_vipGradeName'] + '</td>' 
							+ '<td><span  class="label '+label_class +'" >' + label_name+ '</span></td>' 
							+ '<td>' + obj['mobile'] + '</td>' 
							+ '<td>' + obj['email'] + '</td>' 
							+ '<td>' + registrationTime.substr(0, 10) + '</td>' 
							+ '<td>' + obj['user_code']+'</td>' 
							+ '<td>'
							+ disable_html
							+ frozen_html
							+ edit_html
							+ '</td>'
							+ '</tr>';
				}
				
				$('#result_list').html(html);
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
			var edit_html = '';
			/*if (loginUser.role == "ComplianceOfficer" || loginUser.role == "OperationsManager" ) {
				if (item['disable']){
			     	disable_html = '<button class="btn btn-primary" style="margin-left:10px" onclick="disableuser('+item['user_id']+','+"'disable'"+')">已禁用</button>';
				} else {
			     	disable_html = '<button class="btn" style="margin-left:10px" onclick="disableuser('+item['user_id']+','+"'disable'"+')">禁用</button>';
				}
				if (item['is_frozen']){
					frozen_html = '<button class="btn btn-primary" style="margin-left:10px" onclick="disableuser('+item['user_id']+','+"'freeze'"+')">已冻结</button>';
				} else {
					frozen_html = '<button class="btn" style="margin-left:10px" onclick="disableuser('+item['user_id']+','+"'freeze'"+')">冻结</button>';
				}
			}*/
			if (loginUser.role == "OperationsManager" || loginUser.role == "CustomerServiceStaff") {
				edit_html = '&nbsp;<a  class="btn btn-a"   onclick="parent.openNewWindow('+"'./../admin/user_edit_detail.html?id="+obj.user_id+"'"+',updateList)">编辑</a>'
				+'&nbsp;<a  class="btn btn-a"   onclick="parent.openNewWindow('+"'./../admin/user_setting.html?id="+obj.user_id+"'"+',updateList)">设置</a>';
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
			html = '<tr data-tt-parent-id="'+obj['up_id']+'" data-tt-id="'+obj['user_id']+'">'
				+ '<td> <a onclick="goSeeProfile('+obj.user_id+')" >' + user_name + '</td>' 
				+ '<td>' + item['_vipGradeName'] + '</td>' 
				+ '<td><span  class="label '+label_class +'" >' + label_name+ '</span></td>' 
				+ '<td>' + obj['mobile'] + '</td>' 
				+ '<td>' + obj['email'] + '</td>' 
				+ '<td>' + registrationTime.substr(0, 10) + '</td>' 
				+ '<td>' + obj['user_code']+'</td>' 
				+ '<td>'
				+ disable_html
				+ frozen_html
				+ edit_html
				+ '</td>'
				+ '</tr>';
			
			$('#result_list').append(html);
			maketree(list, obj['user_id'])
		}
	}
}



function editUser(id) {
	window.open("user_edit_profile.html?userId="+id, "_blank");
}



function disableuser(id, scheme){
	$.jsonRPC.request("adminCheckService.freezeOrDisableUser", {
		params : [id, scheme],
		success : function(result) {	
			refreshList(_pageNo);
		}
	});
}

function updateList(){
	getUserlist();
}