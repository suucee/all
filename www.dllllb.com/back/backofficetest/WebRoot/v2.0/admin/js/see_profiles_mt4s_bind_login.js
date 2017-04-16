var _pageNo = 1;
const PAGE_SIZE = 10;
var _bindingId = 0;
$(document).ready(function() {
	
	//可以指定搜索的id
	var id = getParam("id", "0");
	if(id != "0"){
		_bindingId = parseInt(id);
		getOneUser(_bindingId);		
	}else{
		refreshList(1);
	}
	
	$("#btn1").click(function(){
		refreshList(1);
	});
	
	refreshList(1);
});






//绑定
function bindByLogin(login) {
	if (login > 0 && _bindingId >0) {
		$.jsonRPC.request('adminUserService.bindMT4User', {
			params : [login, _bindingId],
			success : function(result) {
				_bindingLogin = 0;
				layer.msg("绑定成功！",{time:1000,icon:1},function(){
					parent.closeWindow();
				});
				
			}
		});
	}
}


function refreshList(pageNo) {
	_pageNo = pageNo;
	//pageNo, int pageSize, String urlFormat, String scheme, String keyword
	$.jsonRPC.request("admin2MT4Service.serachMT4ByKeywords", {
		params : [ pageNo, PAGE_SIZE, "javascript:refreshList(??);","all", $("#keyword1").val()],
		success : function(result) {
			if (result != null) {
				_pageNo = pageNo;
				if(result.list.list.length==0){
					$("tbody#result_list1").html('<tr><td colspan="5">对不起，没有检索到相关MT4账号。</td></tr>');
					return;
				}
				var html = '';
				for ( var i in result.list.list) {
					var item = result.list.list[i];
					
					html += '<tr>'
						+'<td><a  onclick="goSeeProfile('+item['userId']+')">'+item.login+'</td>'
						+'<td>'+(item.user == null ? '(未绑定)': item.user._name)+'</td>'
						+'<td>'+item.name+'</td>'
						+'<td>'+item.balance+'</td>'
						+'<td>'+item.group+'</td>'
						+'<td>'+toDate(item.creatTime.time)+'</td>'
						+'<td>'+(item.user == null ?'<button class="btn btn-a" onclick="javascript:bindByLogin('+item.login+')">绑定为此MT4账号</button></td>':BAN_ICO)
						+'</tr>';
				}
				// page
				if(result.buttons != ""){
					html += '<tr><td colspan="5"><div class="pagelist">'+ result.buttons + '</td></tr>';
				}
				$("tbody#result_list1").html(html);
	
			}//end-if
			else{
				$("tbody#result_list1").html('<tr><td colspan="5">对不起，没有检索到相关MT4账号。</td></tr>');
				return;
			}
		}
	});
} 


function getOneUser(userId){
	$.jsonRPC.request("user2Service.getUserDetail", {
		params : [userId],
		success : function(result) {
			if (result != null) {
				
				var profiles = result.map.profiles;
				var users = result.map.users;
				
				$(".name").html(profiles.userName);
				$(".ename").html(profiles.userEName);
				$(".email").html(users.email);
				$(".mobile").html(users.mobile);
				$(".cardType").html(profiles.cardType);
				$(".cardId").html(profiles.userIdCard);
				
				switch (users.state) {
				case "UNVERIFIED":
					$(".state").html('<span class="label-pending label" >资料不全</span>（资料不全！等待客户补全资料）');
					break;
				case "AUDITING":
					$(".state").html('<span class="label-pending label" >审核中</span>（客户可修改或追加资料）');
					break;
				case "REJECTED":
					var comment = users._userProfilesComment;
					var causeHtml = ''; 
					if(comment != undefined && comment != ""){
						causeHtml = '<span class="label-important label" >被驳回</span> 驳回理由：'+comment+' （客户可修改资料重新提交）';
					}else{
						causeHtml = '<span class="label-important label" >被驳回</span>（客户可修改资料重新提交）';
					}
					$(".state").html(causeHtml);
					break;
				case "VERIFIED":
					$(".state").html('<span class="label-success label" >账户已激活</span>（不可更改资料）');
					break;
				default:
					$(".state").html("--");
					break;
				}
				if(users.disable){
					$(".state").html($(".state").html()+"<span class='red'>[禁]</span>");
				}
				if(users.frozen){
					$(".state").html($(".state").html()+"<span class='purple'>[冻]</span>");
				}
			}
	}
	});
}


