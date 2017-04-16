$(function(){
	getUserlist();
	$("#go").click(function(){
		 getUserlist();
	});
});


//获取用户列表
function getUserlist() {
	$('#result_list').html("");
	$.jsonRPC.request("user2RankService.getTheRankNextUser", {
		params : [$("#keyWord").val()],
		success : function(result) {	
			$("#thetable").treetable("destroy");
			list = result['map']['list']['list'];
			pid = result['map']['pid'];
			if($("#keyWord").val()==""){
				maketree(list, pid);
				$("#thetable").treetable({expandable: true});
				if($('#result_list').html() == ""){
					$('#result_list').html("<td colspan=10> 暂无客户。 </td>");
				}
			}else{
				var html="";
				for (var ii = 0; ii < list.length; ii++) {
					var obj = list[ii];
					var item = obj;
						var registrationTime="--";
						var parentRegistrationTime="--";

						var disable_html = '';
						var frozen_html = '';
						var edit_html = '';
						if (loginUser.role == "OperationsManager" || loginUser.role == "CustomerServiceStaff") {
							edit_html = '<button class="btn btn-primary" style="margin-left:10px" onclick="javascript:editUser('+item['user_id']+');">编辑</button>';
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
						user_name = obj['userName'] == null || obj['userName'] == "" ? '<span class="gray">(暂无姓名)</span>' : obj['userName'];
						html += '<tr data-tt-parent-id="'+obj['upId']+'" data-tt-id="'+obj['userId']+'">'
							+ '<td>' + user_name + '</td>' 
							+ '<td>' + item['vipGradeName'] + '</td>' 
							+ '<td><span  class="label '+label_class +'" >' + label_name+ '</span></td>' 
							+ '<td>' + (obj['depositAmount']==null?'0.00':obj['depositAmount'].toFixed(2)) + '</td>' 
							+ '<td>' + (obj['withdrawalAmount']==null?'0.00':obj['withdrawalAmount'].toFixed(2))+ '</td>' 
							+ '<td>' + (obj['rebateAmount']==null?'0.00':obj['rebateAmount'].toFixed(2))+ '</td>' 
							+ '<td>' + (obj['volume']==null?'0.00':obj['volume'].toFixed(2))+ '</td>' 
							+ '<td>' + registrationTime.substr(0, 10) + '</td>' 
							+ '<td><a class="btn btn-a" onclick="parent.openNewWindow('+"'./../agent/customer_open_user_detail.html?id="+obj['userId']+"'"+',null)">查看<a></td>' 
							+ '</tr>';
					}
				if(html == ""){
					$('#result_list').html("<td colspan=10> 暂无客户。 </td>");
					return;
				}
				$('#result_list').html(html);
			}
			
		}
	});
}



function maketree(list,pid){
	for (var ii = 0; ii < list.length; ii++) {
		var obj = list[ii];
		var item = obj;
		if (pid==obj['upId']){
			var registrationTime="--";
			var parentRegistrationTime="--";

			var disable_html = '';
			var frozen_html = '';
			var edit_html = '';
			if (loginUser.role == "OperationsManager" || loginUser.role == "CustomerServiceStaff") {
				edit_html = '<button class="btn btn-primary" style="margin-left:10px" onclick="javascript:editUser('+item['user_id']+');">编辑</button>';
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
			user_name = obj['userName'] == null || obj['userName'] == "" ? '<span class="gray">(暂无姓名)</span>' : obj['userName'];
			html = '<tr data-tt-parent-id="'+obj['upId']+'" data-tt-id="'+obj['userId']+'">'
				+ '<td>' + user_name + '</td>' 
				+ '<td>' + item['vipGradeName'] + '</td>' 
				+ '<td><span  class="label '+label_class +'" >' + label_name+ '</span></td>' 
				+ '<td>' + (obj['depositAmount']==null?'0.00':obj['depositAmount'].toFixed(2)) + '</td>' 
				+ '<td>' + (obj['withdrawalAmount']==null?'0.00':obj['withdrawalAmount'].toFixed(2))+ '</td>' 
				+ '<td>' + (obj['rebateAmount']==null?'0.00':obj['rebateAmount'].toFixed(2))+ '</td>' 
				+ '<td>' + (obj['volume']==null?'0.00':obj['volume'].toFixed(2))+ '</td>' 
				+ '<td>' + registrationTime.substr(0, 10) + '</td>'  
				+ '<td><a class="btn btn-a"  onclick="parent.openNewWindow('+"'./../agent/customer_open_user_detail.html?id="+obj['userId']+"'"+',null)">查看<a></td>' 
				+ '</tr>';
			
			$('#result_list').append(html);
			maketree(list, obj['userId'])
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
