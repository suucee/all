const PAGESIZE = 10;
var _pageNo=1;
var scheme ="";
$(document).ready(function() {
	scheme=getParam("scheme", "all");
	switch (scheme) {
	case "all_stuff":
		$("legend").html('<i class="fa fa-users margin-right15" aria-hidden="true"></i>所有员工');
		break;
	case "all_agent":
		$("legend").html('<i class="fa fa-users margin-right15" aria-hidden="true"></i>所有代理');
		break;
	case "all":
		$("legend").html('<i class="fa fa-users margin-right15" aria-hidden="true"></i>所有用户');
		break;
	case "unverified":
		$("legend").html('<i class="fa fa-users margin-right15" aria-hidden="true"></i>资料不全用户');
		break;
	case "auditing":
		$("legend").html('<i class="fa fa-users margin-right15" aria-hidden="true"></i>待审核用户');
		break;
	case "verified":
		$("legend").html('<i class="fa fa-users margin-right15" aria-hidden="true"></i>已审核用户');
		break;
	case "rejected":
		$("legend").html('<i class="fa fa-users margin-right15" aria-hidden="true"></i>已驳回用户');
		break;
	case "frozen":
		$("legend").html('<i class="fa fa-users margin-right15" aria-hidden="true"></i>已冻结用户');
		break;
	case "disabled":
		$("legend").html('<i class="fa fa-users margin-right15" aria-hidden="true"></i>已禁用用户列表');
		break;
	default:
		$("legend").html('<i class="fa fa-users margin-right15" aria-hidden="true"></i>用户列表');
	}
	$("#btn_search").click(function(){
		refreshList(1);
	});
	$(".link").removeClass('btn-d');
	$(".link").addClass('btn-e');
	$("."+scheme).removeClass('btn-e');
	$("."+scheme).addClass('btn-d');
	refreshList(1);
});

function refreshList(pageNo) {
	$.jsonRPC.request("admin2UserService.getPage", {
		params : [pageNo, PAGESIZE, "javascript:refreshList(??);", scheme, $("#keyword").val()],
		success : function(result) { 
		if (result != null) {
			_pageNo = pageNo;
			var html = '';
			
			var deposit = '';
			var agentNameHtml = '';
			var stuffNameHtml = '';
			
			var hasTrade = '<span class="label label-success">有交易</span>';
			var noTrade =  '<span class="label label-pending">无交易</span>';
			
			for ( var i in result.list.list) {
				var item = result.list.list[i].map;
				
				if(item.deposits>0){
					deposit = '<span class="font-green align-right">'+item.deposits.toMoney()+'</span>';
				}else{
					deposit = '<span class="font-green align-right">0.00</span>'
				}
				
				if(item.agentName == null){
					agentNameHtml = '<span class="">无代理</span>';
				}else{
					agentNameHtml = '<span class="">'+item.agentName+'</span>';
				}
				if(item.staff_name == null){
					stuffNameHtml = '<span class="">暂无</span>';
				}else{
					stuffNameHtml = '<span class="">'+item.staff_name+'</span>';
				}
				
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
				if(label_name.length == 3){
					label_name = "&nbsp;&nbsp;" + label_name + "&nbsp;&nbsp;";
				}
//				var disable_html = '';
//				var frozen_html = '';
//				var edit_html = '';
				
				//查看信息
//				var see_profile_html = '<a onclick="localUserGlobaly('+item["uId"]+')" class="btn btn-a" href="./../admin/see_profile.html?id='+item["uId"]+'&backurl='+window.location.href+'">更多...</a>';
				var see_profile_html = '<a class="btn btn-a" onclick="goSeeProfile('+item["uId"]+')">管理</a>';
				
				var name = "";
				if(item._userName == null || item._userName == ""){
					name = '<a onclick="goSeeProfile('+item["uId"]+')">(暂无姓名)</a>';
				}else{
					name = '<a onclick="goSeeProfile('+item["uId"]+')">'+item._userName+'</a>';
				}
				
				if(item.disable ){
					name += '<span class="red">[禁]</span>';
				}
				if(item.is_frozen){
					name += '<span class="purple">[冻]</span>';
				}
				
				html += '<tr>'
						+ '<td style="text-align: center;" title="'+ item._userName +'">'+ name +'</td>'
						+ '<td style="text-align: center;">' + item._vipGradeName + '</td>'
						+ '<td style="text-align: center;"><span class="label '	+ label_class + '">' + label_name + '</span></td>'
						
						+ '<td style="text-align: center;">'+deposit+'</td>'
//						+ '<td style="text-align: center;">'+noTrade+'</td>'
						
						+ '<td style="text-align: center;">'+ stuffNameHtml +' </td>'
						+ '<td style="text-align: center;">'+ agentNameHtml +' </td>'
						
						+ '<td " title="' + (item.email==null||item.email=="" ?  "(暂无邮箱)" :item.email ) + '">' + (item.email==null||item.email=="" ?  "(暂无邮箱)" :item.email ) + '</td>'
						+ '<td style="text-align: center;">' + item.mobile + '</td>'
//						+ '<td style="text-align: center;">' + item.referralCode + ' </td>'
						+ '<td style="text-align: center;">' + toSimpleDate(item.registrationTime.time) + '</td>'
//						+ edit_html 
//						+ '&nbsp;'+ disable_html
//						+ '&nbsp;'+ frozen_html
//						+ '&nbsp;'+ see_profile_html
						+ '<td style="text-align: center;">'+see_profile_html+'</td>'
					    + '</tr>';
			}			
			if(result.buttons!=null&&result.buttons!=""){
		    	html += '<tr><td colspan="11"><div class="pagelist">'+result.buttons+'</td></tr>';
		    }
			if (html=="") {
			     html += '<tr><td colspan="11" class="bold">对不起，暂未检索到相关记录</td></tr>';
			}
		
			$("tbody#result_list").html(html);
					
		}					
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
function updateList(){
	refreshList(_pageNo);
}
