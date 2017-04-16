var _pageNo = 1;
const PAGE_SIZE = 10;
var _bindingLogin = 0;
$(document).ready(function() {
	
	//可以指定搜索的login
	var login = getParam("login", "0");
	if(login != "0"){
		_bindingLogin = parseInt(login);
		getOneMT4(login);		
	}else{
		refreshList(1);
	}
	
	$("#btn1").click(function(){
		refreshList(1);
	});
	
	refreshList(1);
});






//绑定
function bind(userId,obj) {
	
	$(obj).find("button");
	
	layer.confirm("您确认要将此用户和MT4账号进行绑定？",{btn:["确认绑定","放弃"]},function(){
		if (_bindingLogin > 0) {
			$.jsonRPC.request('adminUserService.bindMT4User', {
				params : [_bindingLogin, userId],
				success : function(result) {
					_bindingLogin = 0;
					layer.msg("绑定成功！",{time:1000,icon:1},function(){
						parent.closeWindow();
					});
				}
			});
		}
	},
	function(){
		return;
	});
}


function refreshList(pageNo) {
	_pageNo = pageNo;
	$.jsonRPC.request("admin2UserService.findUsersByKeyword", {
		params : [ _pageNo, PAGE_SIZE, "javascript:refreshList(??);", $("#keyword1").val()],
		success : function(result) {
			if (result != null) {
				_pageNo = pageNo;
				if(result.list.list.length==0){
					$("tbody#result_list1").html('<tr><td colspan="5">对不起，没有检索到相关内容。</td></tr>');
					return;
				}
				var html = '';
				for ( var i in result.list.list) {
					var item = result.list.list[i];
					
					html += '<tr>'
						+'<td><a  onclick="goSeeProfile('+item["userId"]+')" >'+item.username+'</td>'
						+'<td>'+item.mobile+'</td>'
						+'<td>'+item.email+'</td>'
						+'<td>'+item.idcard+'</td>'
						+'<td><button class="btn btn-a" onclick="bind('+item.userId+',this)">绑定为此用户</button></td>'
						+'</tr>';
				}
				// page
				if(result.buttons != ""){
					html += '<tr><td colspan="5"><div class="pagelist">'+ result.buttons + '</td></tr>';
				}
				$("tbody#result_list1").html(html);
	
			}//end-if
			else{
				$("tbody#result_list1").html('<tr><td colspan="5">对不起，没有检索到相关内容。</td></tr>');
				return;
			}
		}
	});
} 


function getOneMT4(login){
	$.jsonRPC.request("admin2MT4Service.getOneMT4", {
		params : [parseInt(login)],
		success : function(result) {
			if (result != null) {
				var item = result;
				var html = '';
				var name = '';
				var action = '';
				var mt4sOrderHTML = '';
				
				var state = '<span class="font-green">正常</span>';
				if(item.enable==0){state = '<span class="font-red">已禁用</span>';}
				if(item.deleted){state = '<span class="font-red">已删除</span>';}
				if(item.enableReadOnly){state = '<span class="purple">已冻结</span>';}
				
				$(".login").html(item.login);
				$(".name").html(item.name);
				$(".bind_state").html("未绑定");//未绑定
				$(".use_state").html(state);
				$(".balance").html('<span class="amount bold font-red">' + item.balance.toFixed(2) + '</span>');
				$(".group").html('<a style="text-decoration:underline" href="mt4s_group.html?group='+item.group+'">'+item.group+'</a>');
				$(".create_time").html(toDate(item.regdate * 1000));
			}
	}
	});
}


