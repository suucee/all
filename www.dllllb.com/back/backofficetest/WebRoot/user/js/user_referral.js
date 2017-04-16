var user_page=1;
$(function(){
	$("#result_head").html('' + '	<th width="30%"  style="text-align:center;">邮箱</th>' 
			+ '	<th width="10%" style="text-align:center;">狀態</th>' 
			+ '	<th width="5%" style="text-align:center;">用戶</th>' 
			+ '	<th width="20%" style="text-align:center;">注册時間</th>' 
			+ '	<th width="25%" style="text-align:center;">查看资料</th>' 
		   + '	<th width="10%" style="text-align:center;">操作</th>');
	$("#go").click(function(){
     	$val=$(".usercode").val();
     	if($val.length!=5){
     		alert("推荐码错误，请重新输入推荐码！");
     	}else{
     		getUserlist($val);
     	}
	});
	getCodeAndLink();
});
function getCodeAndLink(){
	$.jsonRPC.request("userRankService.getReferralCode", {
		params : [],
		success : function(result) {
		  if(result!=null){
			  url=location.href;
			  index=url.indexOf("user_referral.html");
			  signinlink=url.substring(0,index)+"register.html?code="+result;
			  $(".referralCode").text(result);
			  $(".referralLink").text(signinlink);
			 $(".mycodeandlink").show(); 
		  }		
		}
	});	
}

//获取用户列表
function getUserlist(code) {
	$('#result_list').html("");
	$.jsonRPC.request("userRankService.findUserByReferralCode", {
		params : [code],
		success : function(result) {
				if(result!=null){
				list = result['map']['list']['list'];
				pid = result['map']['pid'];
				html=maketree(list, pid);
			//	$('#result_list').append(html);
				$("#thetable").treetable({expandable: true});
			}else{
				alert("推荐码不存储在!");
			}
		}
	});
}



function maketree(list,pid){
	for (var ii = 0; ii < list.length; ii++) {
		obj = list[ii]['map'];
		if(pid==obj['up_id']){
			var registrationTime="--";
			var parentRegistrationTime="--";
			var dohtml='';
			//var dohtml='<button class="btn btn-medium btn-primary" onclick="openPopup('+"'tree'"+','+obj['user_id']+')">关系图</button>';
			var showinfo='<button class="btn btn-medium btn-primary" onclick="openPopup('+"'info'"+','+obj['user_id']+')">查看资料</button>';
			obj.registrationTime== null ? registrationTime='--' : registrationTime=toDate(obj.registrationTime.time);
			obj.parentRegistrationTime== null ? parentRegistrationTime='--' : parentRegistrationTime=toDate(obj.parentRegistrationTime.time);
			switch (obj.state) {
			case 'VERIFIED':
				label_class = 'label-success';
				label_name = '已通过审核';
				break;
			case 'REJECTED':
				label_class = 'label-pending';
				label_name = '未通过审核';
				break;
			case 'AUDITING':
				label_class = 'label-pending';
				label_name = '正在审核中';
				break;
				default:
					break;
			}
			html= '<tr  data-tt-parent-id="'+obj['up_id']+'" data-tt-id="'+obj['user_id']+'"><td width="30%">' + obj['email'] + '</td>' 
			+ '	<td width="10%" style="text-align:center;"><span  class="label '+label_class +'" >' + label_name+ '</span></td>' 
			+ '	<td width="5%" style="text-align:center;">' + obj['u_name'] + '</td>' 
			+ '	<td width="20%" style="text-align:center;">' + registrationTime + '</td>' 
			+ '	<td width="25%" style="text-align:center;">'+showinfo+'</td>' 
			+ '	<td width="10%" style="text-align:center;">'+dohtml+'</td><tr>';
			
			$('#result_list').append(html);
			maketree(list, obj['user_id'])
		}
	}
}




function openPopup(type, id) {
	switch (type) {
	case "tree":
		$.get("_popup_rank_tree.html", function(data) {
			$("body").append(data);
			$.jsonRPC.request("userRankService.getTheUserRank", {
				params : [ id ],
				success : function(result) {
					list = result['map']['list']['list'];
					pid = result['map']['pid'];
					user_id = result['map']['user_id'];
					usercount = 0;
					for (var ii = 0; ii < list.length; ii++) {
						child_count = 0;
						for (var jj = 0; jj < list.length; jj++) {
							if (list[ii]['user_id'] == list[jj]['up_id']) {
								child_count++;
							}
						}
						if (usercount < child_count) {
							usercount = child_count;
						}
					}
					$(".tree").width(usercount * 120);
					html = makeusertree(list, pid, user_id);
					$(".tree ul").append(html);
					$(".popup_dialog").fadeIn(200);
				}
			});
		});
		break;
	case "info":
		$.get("_popup_show_userinfo.html", function(data) {
			$("body").append(data);
			$.jsonRPC.request("userRankService.getOneUserInfo",
					{
						params : [ id ],
						success : function(result) {
							user = result['user'];
							user_msg = result;
							$(".userinfo .email").text(user['email']);
							$(".userinfo .mobile").text(user['mobile']);
							if (user_msg != null) {
								$(".userinfo .ename").text(
										user_msg['userEName']);
								$(".userinfo .nationality").text(
										user_msg['userNationality']);
								$(".userinfo .esidentialaddress").text(
										user_msg['userEsidentialAddress']);
								$(".userinfo .comname").text(
										user_msg['userComName']);
								$(".userinfo .industry").text(
										user_msg['userIndustry']);
								$(".userinfo .years_income").text(
										user_msg['userYearsIncom']);
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