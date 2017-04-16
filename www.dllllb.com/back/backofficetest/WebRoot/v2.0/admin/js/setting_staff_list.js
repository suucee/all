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
				var disable_html = '';
				var frozen_html = '';
				var edit_html = '';
				
				var dohtml = '<a class="btn btn-primary" onclick="goSeeProfile('+obj['u_id']+')">查看员工</a>';
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
			if(result.buttons!=null&&result.buttons!=""){
		    	html += '<tr><td colspan="10"><div class="pagelist">'+result.buttons+'</td></tr>';
		    }
			if (html=="") {
			     html += '<tr><td colspan="10" class="bold">对不起，暂未检索到相关记录</td></tr>';
			}
			$('#result_list').append(html);
		}
	});
}
