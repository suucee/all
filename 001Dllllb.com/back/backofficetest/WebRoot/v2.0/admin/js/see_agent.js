var  user_id=0;
var _backurl ='';
$(function(){
	user_id = getParam("id",0);
	_backurl = getParam("backurl",null);
	init();
	getUserlist();
});


function back(){
	if(_backurl != null){
		window.location.href = _backurl;
		parent.localUserById(0);//折叠所有节点
	}
}


function init(){
	if(_backurl != null){//是从别的地方过来的
		$(".container > div:first").show();
		parent.localUserById(user_id);
	}
}

//获取用户列表
function getUserlist() {
	$('#result_list').html("");
	$.jsonRPC.request("admin2UserService.getAgentCoustomers", {
		params : [user_id],
		success : function(result) {	
			if (result != null) {
				var html = '';
				var deposit = '';
				var agentNameHtml = '';
				var stuffNameHtml = '';
				var hasTrade = '<span class="label label-success">有交易</span>';
				var noTrade =  '<span class="label label-pending">无交易</span>';
				var item = result.map.agent.map;
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
				var see_profile_html = '<a class="btn btn-a" onclick="goSeeProfile('+item["uId"]+',true)">管理</a>';
				var name = "";
				if(item._userName == null || item._userName == ""){
					name = '<a onclick="goSeeProfile('+item["uId"]+',true)">(暂无姓名)</a>';
				}else{
					name = '<a onclick="goSeeProfile('+item["uId"]+',true)">'+item._userName+'</a>';
				}
				if(item.disable ){
					name += '<span class="red">[禁]</span>';
				}
				if(item.is_frozen){
					name += '<span class="purple">[冻]</span>';
				}
				$(".agentName").html(item._userName);
				
				html += '<tr>'
						+ '<td style="text-align: center;" title="'+ item._userName +'">'+ name +'</td>'
						+ '<td style="text-align: center;">' + item._vipGradeName + '</td>'
						+ '<td style="text-align: center;"><span class="label '	+ label_class + '">' + label_name + '</span></td>'
						+ '<td style="text-align: center;">'+deposit+'</td>'
						+ '<td style="text-align: center;">'+ stuffNameHtml +' </td>'
						+ '<td style="text-align: center;">'+ agentNameHtml +' </td>'
						+ '<td " title="' + (item.email==null||item.email=="" ?  "(暂无邮箱)" :item.email ) + '">' + (item.email==null||item.email=="" ?  "(暂无邮箱)" :item.email ) + '</td>'
						+ '<td style="text-align: center;">' + item.mobile + '</td>'
						+ '<td style="text-align: center;">' + toSimpleDate(item.registrationTime.time) + '</td>'
						+ '<td style="text-align: center;">'+see_profile_html+'</td>'
					    + '</tr>';
			}			
			if(result.buttons!=null&&result.buttons!=""){
		    	html += '<tr><td colspan="11"><div class="pagelist">'+result.buttons+'</td></tr>';
		    }
			if (html=="") {
			     html += '<tr><td colspan="11" >对不起，暂未检索到相关记录</td></tr>';
			}
			$("tbody#result_agent").html(html);


			$("#thetable").treetable("destroy");
			maketree(result.map.coustomers.list,result.map.agent.map.uId );	

			$("#thetable").treetable({expandable: true});
			
			
			if($("#result_list").html() == ""){
				$("#result_list").html('<tr><td colspan="11" >该用户暂时没有客户</td></tr>');
			}
		}
	});
}



function maketree(list,pid){
		var deposit = '';
		var agentNameHtml = '';
		var stuffNameHtml = '';
		var hasTrade = '<span class="label label-success">有交易</span>';
		var noTrade =  '<span class="label label-pending">无交易</span>';
		for ( var i in list) {
			var item=list[i].map;
//			console.log(item.up_id+"----------------"+pid)
			if(item.up_id==pid){
				var item =list[i].map;
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
				
				var html = '<tr data-tt-parent-id="'+item['up_id']+'" data-tt-id="'+item['uId']+'">'
						+ '<td style="text-align: center;" title="'+ item._userName +'">'+ name +'</td>'
						+ '<td style="text-align: center;">' + item._vipGradeName + '</td>'
						+ '<td style="text-align: center;"><span class="label '	+ label_class + '">' + label_name + '</span></td>'
						+ '<td style="text-align: center;">'+deposit+'</td>'
						+ '<td style="text-align: center;">'+ stuffNameHtml +' </td>'
						+ '<td style="text-align: center;">'+ agentNameHtml +' </td>'
						+ '<td " title="' + (item.email==null||item.email=="" ?  "(暂无邮箱)" :item.email ) + '">' + (item.email==null||item.email=="" ?  "(暂无邮箱)" :item.email ) + '</td>'
						+ '<td style="text-align: center;">' + item.mobile + '</td>'
						+ '<td style="text-align: center;">' + toSimpleDate(item.registrationTime.time) + '</td>'
						+ '<td style="text-align: center;">'+see_profile_html+'</td>'
					    + '</tr>';
				$("tbody#result_list").append(html);
				maketree(list, item.uId);
			}
		}			
}
