var user_page=1;
var datalist;
$(function(){
	$("#result_head").html('' + '	<th width="30%"  style="text-align:center;">邮箱</th>' 
			+ '	<th width="10%" style="text-align:center;">狀態</th>' 
			+ '	<th width="5%" style="text-align:center;">用戶</th>' 
			+ '	<th width="20%" style="text-align:center;">注册時間</th>' 
			+ '	<th width="10%" style="text-align:center;">推荐码</th>' 
			+ '	<th width="15%" style="text-align:center;">查看资料</th>' 
		   + '	<th width="10%" style="text-align:center;">操作</th>');
	$("#go").click(function(){
     	$val=$(".usercode").val();
     	if($val.length!=""){
			  url=location.href;
     		  index1=url.indexOf("start.html");
			  link=url.substring(0,index1)+"user_referral.html?keyword="+$val;
			  location.href=link;
     	}
	});
	keyword=getParam("keyword", "");
	if(keyword!=""){
	   $(".usercode").val(keyword);
	   getUserlist(keyword);
	}
});


//获取用户列表
function getUserlist(code) {
	$('#result_list').html("");
	$.jsonRPC.request("userRankService.findUserByReferralCode", {
		params : [code],
		success : function(result) {
				if(result!=null){
				for(var idc=0;idc<result['list'].length;idc++){
					datalist = result['list'][idc]['map']['list']['list'];
					pid = result['list'][idc]['map']['pid'];
					group_step=10000*idc;
					html=maketree(pid,group_step);
					$('#result_list').append(html);
				}
				$("#thetable").treetable({expandable: true});
			}else{
				alert("查询失败!");
			}
		}
	});
}



function maketree(pid,group_step){
	html="";
	for (var ii = 0; ii < datalist.length; ii++) {
		obj = datalist[ii]['map'];
		if(pid==obj['up_id']){
			var registrationTime="--";
			var parentRegistrationTime="--";
			var dohtml='';
			//var dohtml='<a href="javascript:openPopup('+"'tree'"+','+obj['user_id']+')">关系图</a>';
			var showinfo='<a href="javascript:openPopup('+"'info'"+','+obj['user_id']+')">查看资料</a>';
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
			the_up_id=obj['up_id'];
			the_id=obj['user_id'];
			user_name=obj['user_name']==null?"": obj['user_name'];
			html+= '<tr  data-tt-parent-id="'+the_up_id+'" data-tt-id="'+the_id+'"><td width="30%">' + obj['email'] + '</td>' 
			+ '	<td width="10%" style="text-align:center;"><span  class="label '+label_class +'" >' + label_name+ '</span></td>' 
			+ '	<td width="5%" style="text-align:center;">' + user_name+ '</td>' 
			+ '	<td width="20%" style="text-align:center;">' + registrationTime + '</td>' 
			+ '	<td width="10%" style="text-align:center;">'+obj['user_code']+'</td>' 
			+ '	<td width="15%" style="text-align:center;">'+showinfo+'</td>' 
			+ '	<td width="10%" style="text-align:center;">'+dohtml+'</td><tr>';
			var s=[];
			for(var jj=0;jj<datalist;jj++){
			   if(jj!=ii){
				   s.push(datalist[jj]);
			   }	
			}
			datalist=s;
			ii--;
			html+=maketree(obj['user_id'],group_step);
		}
	}
	return html;
}




function openPopup(type, id) {
	switch (type) {
	case "tree":
		getRankTree(id);
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
