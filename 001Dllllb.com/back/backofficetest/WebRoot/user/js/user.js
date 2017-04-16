var user_page=1;
$(function(){
	getUserlist(user_page);
});



//获取用户列表
function getUserlist(page) {
	$("#result_head").html('' + '	<th width="10%"  style="text-align:center;">單號</th>' 
	+ '	<th width="10%" style="text-align:center;">狀態</th>' 
	+ '	<th width="10%" style="text-align:center;">用戶</th>' 
	+ '	<th width="10%" style="text-align:center;">邮箱</th>' 
	+ '	<th width="20%" style="text-align:center;">注册時間</th>' 
	+ '	<th width="10%" style="text-align:center;">上线</th>'
	+ '	<th width="20%" style="text-align:center;">上线注册时间</th>' 
   + '	<th width="10%" style="text-align:center;">操作</th>');
	$('#result_list').html("");
	$.jsonRPC.request("userRankService.getAllUser", {
		params : [page, 6,"javascript:getUserlist(??)"],
		success : function(result) {		
			list = result['list']['list'];
			html = '';
			for ( i = 0; i < list.length; i++) {
				obj = list[i];
				var registrationTime="--";
				var parentRegistrationTime="--";
				var pid="--";
				var dohtml='';
				//var dohtml='<button class="btn btn-medium btn-primary" onclick="openPopup('+obj['id']+')">关系图</button>';
				obj.registrationTime== null ? registrationTime='--' : registrationTime=toDate(obj.registrationTime.time);
				obj.parent== null ? parentRegistrationTime='--' : parentRegistrationTime=toDate(obj.parent.registrationTime.time);
				obj.parent== null ? pid='--' : pid=obj.parent.id;
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
				html += '<tr><td width="10%"  style="text-align:center;">' + obj['id'] + '</td>' 
				+ '	<td width="10%" style="text-align:center;"><span  class="label '+label_class +'" >' + label_name+ '</span></td>' 
				+ '	<td width="10%" style="text-align:center;">' + obj['id'] + '</td>' 
				+ '	<td width="10%" style="text-align:center;">' + obj['email'] + '</td>'
				+ '	<td width="20%" style="text-align:center;">' + registrationTime + '</td>' 
				+ '	<td width="10%" style="text-align:center;">'+pid+'</td>' 
				+ '	<td width="20%" style="text-align:center;">'+parentRegistrationTime+'</td>'
				+ '	<td width="10%" style="text-align:center;">'+dohtml+'</td><tr>';
			}
			html += '<tr><td colspan="10"><div class="pagelist">' + result.buttons + '</td></tr>';
			$('#result_list').append(html);
		}
	});
}

function openPopup(id){
	   $.get("_popup_rank_tree.html", function(data) {
		$("body").append(data);
		$.jsonRPC.request("userRankService.getTheUserRank", {
			params : [id],
			success : function(result) {
			list = result['map']['list']['list'];
			pid = result['map']['pid'];
			user_id= result['map']['user_id'];
			usercount=0;
			for (var ii = 0; ii < list.length; ii++) {
				child_count=0;
				for (var jj = 0; jj < list.length; jj++) {
					if(list[ii]['user_id']==list[jj]['up_id']){
						child_count++;
					}
				}
				if(usercount<child_count){
					usercount=child_count;
				}
			}
			 $(".tree").width(usercount*120);
			html=makeusertree(list,pid,user_id);
		   $(".tree ul").append(html);
			$(".popup_dialog").fadeIn(200);
			}
		});
	   });
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


