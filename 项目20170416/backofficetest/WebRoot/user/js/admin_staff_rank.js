var user_page=1;
$(function(){
	getUserlist(user_page);
});


//获取用户列表
function getUserlist() {
	$('#result_list').html("");
	$.jsonRPC.request("userRankService.getStaffRank", {
		params : [],
		success : function(result) {		
			list = result['map']['list']['list'];
			pid = result['map']['pid'];
			html=maketree(list, pid);
		//	$('#result_list').append(html);
			$("#thetable").treetable({expandable: true});
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
			user_name = obj['user_name'] == null || obj['user_name'] == "" ? '<span class="gray">(暂无姓名)</span>' : obj['user_name'];
			html = '<tr data-tt-parent-id="'+obj['up_id']+'" data-tt-id="'+obj['user_id']+'">'
				+ '<td>' + user_name + '</td>' 
				+ '<td>' + item['_vipGradeName'] + '</td>' 
				+ '<td><span  class="label '+label_class +'" >' + label_name+ '</span></td>' 
				+ '<td>' + obj['mobile'] + '</td>' 
				+ '<td>' + obj['email'] + '</td>' 
				+ '<td>' + registrationTime.substr(0, 10) + '</td>' 
				+ '<td>' + obj['user_code']+'</td>' 
				+ '<td><a class="btn btn-primary" style="color:#fff;" href="user_detail.html?id='+obj['user_id']+'" target="_blank">查看</a>'
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

function makeusertree(list,pid,user_id){
	htmler="";
	for (var ii = 0; ii < list.length; ii++) {
		user = list[ii]['map'];
		if(pid==user['up_id']){
			if(user_id==user['user_id']){
				htmler+="<li><a class='theuser'>"+user['email']+"</a>";
			}else{
				htmler+="<li><a onclick='treerefresh("+user['user_id']+")'>"+user['email']+"</a>";
			}
			havechild=0;
			for (var jj = 0; jj < list.length; jj++) {
				 ch_user = list[jj]['map'];
			     if(ch_user['up_id']==user['user_id']){
			    	 havechild=1;
			    	 break;
			     }
			}
			if(havechild==1){
				htmler+="<ul>";
				htmler+=makeusertree(list,user['user_id'],user_id);
				htmler+="</ul>";
			}
			htmler+="</li>";
		}
	}
	return htmler;
}

function treerefresh(id){
	$(".only-ul").children().remove();
	$.jsonRPC.request("userRankService.getTheUserRank", {
		params : [id],
		success : function(result) {
			list = result['map']['list']['list'];
			pid = result['map']['pid'];
			user_id= result['map']['user_id'];
			$(".tree ul").append(makeusertree(list,pid,user_id));
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
