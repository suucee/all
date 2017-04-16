var emailpattern = /^([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\-|\_|\.]?)*[a-zA-Z0-9]+\.[a-zA-Z]{2,10}$/;
var numpattern=/^[0-9]+$/;
var telpattern=/^((\+)|[0-9])+$/;
var users_page=1;
$(function(){
	getbranchcompanylist(users_page);
});

function getbranchcompanylist(page) {
	$("#result_head").html('' + '<th width="5%" >编号</th>' 
	+ '	<th width="6%">状态</th>' 
	+ '	<th width="15%">名称</th>' 
	+ '	<th width="12%">邮箱</th>' 
	+ '	<th width="10%">电话号码</th>'
	+ '	<th width="10%">银行名称</th>'  
	+ '	<th width="10%">账户名</th>'
    + '	<th width="15%">账号</th>'
	+ '	<th width="20%">操作</th>' );
	
	$('#result_list').html("");
	
	$.jsonRPC.request("adminBranchCompanyService.getAllBranchCompany", {
		params : [page, 1000, "javascript:getbranchcompanylist(??)"],
		success : function(result) {		
			list = result['list']['list'];
			html = '';
			for ( i = 0; i < list.length; i++) {
				var obj = list[i]['map'];
				var level = "";
				switch (obj.u_level) {
				case 1:
					level = '<span class="red">[公司]</span> ';
					break;
				case 2:
					level = '<span class="blue">[经理]</span> ';
					break;
				case 3:
					level = '<span>[员工]</span> ';
					break;
				}
				var label_class = "";
				var label_name = "错误";
				//dohtml = '<button class="btn btn-primary" onclick="javascript:openPopup('+obj['u_id']+');">调整</button>';
				var disable_html = '';
				var frozen_html = '';
				var edit_html = '';
				
				var dohtml = '<button class="btn btn-primary" onclick="javascript:editUserProfile('+obj['u_id']+');">编辑</button>';
				switch (obj.u_state) {
				case 'VERIFIED':
					label_class = 'label-success';
					label_name = '已通过';
					break;
				case 'REJECTED':
					label_class = 'label-cancel';
					label_name = '已驳回';
					break;
				case 'AUDITING':
					label_class = 'label-important';
					label_name = '审核中';
					break;
				default:
					label_class = 'label-pending';
					label_name = '待审核';
				}
				html += '<tr><td >' + obj['u_id'] + '</td>' 
					+ '	<td ><span class="label '+label_class +'" >' + label_name+ '</span></td>' 
					+ '	<td >' + level + (obj['u_name'] != null ? obj['u_name'] : '') + '</td>' 
					+ '	<td >' + obj['u_email'] + '</td>'
					+ '	<td >' + obj['u_mobile'] + '</td>' 
					+ '	<td >' +obj['u_bank_name']+'</td>'
					+ '	<td >' +obj['u_bank_id_name']+'</td>' 
					+ '	<td >' +obj['u_bank_id']+'</td>'
					+ '	<td >' + dohtml
					+ disable_html
					+ frozen_html
					+ edit_html
					+ '</td></tr>';
			}
			html += '<tr><td colspan="10"><div class="pagelist">' + result.buttons + '</td></tr>';
			 
			$('#result_list').append(html);
		}
	});
}

function editUserProfile(id) {
	window.open('user_edit_profile.html?userId='+id, '_blank');
}

function openPopup(id){
	$.get("_popup_branch_company.html", function(data) {
		$("body").append(data);
		if(id!="0"){
			$.jsonRPC.request("adminBranchCompanyService.getOneBranchCompany", {
				params : [id],
				success : function(result) {
					if (result!=null){
						var map = result['map'];
						
						$(".u_id").val(map['u_id']);
						$(".u_name").val(map['u_name']);
						$(".u_mobile").val(map['u_mobile']);
						$(".u_email").val(map['u_email']);
						$(".u_password").val(map['']);
						$(".u_password1").val(map['']);
						$(".u_bank_name").val(map['u_bank_name']);
						$(".u_bank_id_name").val(map['u_bank_id_name']);
						$(".u_bank_id").val(map['u_bank_id']);
						
						$(".addorupdate").text("更新信息");
					}
					
				}
			});
		}
		$(".popup_dialog").fadeIn(200);	
	});
		
}
function closePopup(){
	$(".popup_dialog").remove();
}
function doPopup(){
	var id=$(".u_id").val();
	var u_name=$(".u_name").val();
	var u_mobile=$(".u_mobile").val();
	var u_email=$(".u_email").val();
	var u_password=$(".u_password").val();
	var u_password1=$(".u_password1").val();
	var u_back_name=$(".u_bank_name").val();
	var u_back_id_name=$(".u_bank_id_name").val();
	var u_back_id=$(".u_bank_id").val();
	if(u_name==""){
		alert("名称不能为空！");
		return;
	}
	if(u_mobile==""){
		alert("电话号码不能为空！");
		return;
	}else if(!telpattern.test(u_mobile)){
		alert("电话号码格式不对！");
		return;
	}
	if(u_email==""){
		alert("邮箱地址不能为空！");
		return;
	}else if(!emailpattern.test(u_email)){
		alert("电子邮箱格式不对！");
		return;
	}
	if(u_back_name==""){
		alert("开户银行不能为空！");
		return;
	}
	if(u_back_id_name==""){
		alert("账户名不能为空！");
		return;
	}
	if(u_back_id==""){
		alert("账号不能为空！");
		return;
	}else if(!numpattern.test(u_back_id)){
		alert("账号格式不对！");
		return;
	}
	if(id==0){
		if(u_password.length<6){
			alert("登录密码必须大于6位");
			return;
		}
		if(u_password!=u_password1){
			alert("密码与确认密码不一致");
			return;
		}
	}
	
	$.jsonRPC.request("adminBranchCompanyService.saveOrUpdate", {
		params : [id,u_name,u_mobile,u_email,u_password,u_back_name,u_back_id_name,u_back_id],
		success : function(result) {
			if (result) {
				getbranchcompanylist(users_page);
			}
			closePopup();
		}, 
		error : function(e) {
			getbranchcompanylist(users_page);
			window.alert(e.error.msg);
		}
	});
}







