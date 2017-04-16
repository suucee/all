var user_page=1;
$(function(){
	getUserlist(user_page);
});


//获取用户列表
function getUserlist() {
	$('#result_list').html("");
	$.jsonRPC.request("userRankService.getTheRankNextUser", {
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
		if(pid==obj['up_id']){
			var registrationTime="--";
			var parentRegistrationTime="--";
			var dohtml='';
			//var dohtml='<a href="javascript:openPopup('+"'tree'"+','+obj['user_id']+')">关系图</a>';
			var showinfo='<a href="javascript:openPopup('+"'info'"+','+obj['user_id']+')"><span class="btn btn-primary">查看</span></a>';
			
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
				+ '<td>' + showinfo + '&nbsp;&nbsp;' +dohtml+'</td><tr>';
			
			$('#result_list').append(html);
			maketree(list, obj['user_id'])
		}
	}
}




function openPopup(type, id) {
	switch (type) {
	case "tree":
		getRankTree(id);
		break;
	case "info":
		$.get("_popup_show_userinfo.html", function(data) {
			$("body").append(data);
			$.jsonRPC.request("userRankService.getOneUserInfo", {
				params : [ id ],
				success : function(result) {
					user = result['user'];
					user_msg = result;
					
					$(".userinfo .email").text(user['email']);
					$(".userinfo .mobile").text(user['mobile']);
					if (user_msg != null) {
						$(".userinfo .ename").text(user_msg['userEName']);
						$(".userinfo .nationality").text(user_msg['userNationality']);
						$(".userinfo .esidentialaddress").text(user_msg['userEsidentialAddress']);
						$(".userinfo .comname").text(user_msg['userComName']);
						$(".userinfo .industry").text(user_msg['userIndustry']);
						$(".userinfo .years_income").text(user_msg['userYearsIncom']);
					}
				}
			});
			$(".popup_dialog").fadeIn(200);
		});
		break;
	}
}
function closePopup() {
	$(".popup_dialog").remove();
}