const PAGESIZE = 10;
var _pageNo = 1;
var scheme = getParam("scheme", "all");
var SCHEMES = {
	'all' : '运维团队',
	'operations_manager' : '运维经理',
	'compliance_officer' : '合规',
	'financial_staff' : '财务',
	'financial_superior' : '财务主管',
	'customer_service_staff' : '客服',
	'risk_management_commissioner' : '风控',
	'disabled' : '已禁用',
};
$(document).ready(function(){
	$(".link").each(function(index,element) {
	  if ($(element).attr("href")==("setting_admin_list.html?scheme="+scheme)) {
	  	$(element).removeClass('btn-f');
	  	$(element).addClass('btn-b');
	  };
	});
	refreshList(1);
});

function refreshList(pageNo) {
	_pageNo = pageNo;
	$(".title").text(SCHEMES[scheme]);
	
	$.jsonRPC.request('adminAdminService.getPage', {
		params : [pageNo, PAGESIZE, "javascript:refreshList(??);", scheme, $("#keyword").val()],
		success : function(result) {
			var html = '';
			for (var i in result.list.list) {
				var item = result.list.list[i];
				var status = item.state;
				var label_class = "";
				var label_name = "";
				
				switch (item.role) {
				case 'OperationsManager':
					label_class = 'label-pending';
					label_name = '运维经理';
					break;
				case 'ComplianceOfficer':
					label_class = 'label-particular-filled';
					label_name = '合规';
					break;
				case 'FinancialStaff':
					label_class = 'label-warning';
					label_name = '财务';
					break;
				case 'FinancialSuperior':
					label_class = 'label-cancel';
					label_name = '财务主管';
					break;
				case 'CustomerServiceStaff':
					label_class = 'label-important';
					label_name = '客服';
					break;
				case 'Webmaster':
					label_class = 'label-important';
					label_name = '网站管理员';
					break;
				case 'RiskManagementCommissioner':
					label_class = 'label-important';
					label_name = '风控';
					break;
				}
				if(item.lastLogonTime == null){
					item.lastLogonTime = item.creatTime;
				}
				html += '<tr>'
					+ '    <td style="text-align:center;">'+item.id+'</a></td>' 
					+ '    <td>'+(item.disabled ? '<span class="red">禁用</span>' : '<span class="green">启用</span>')+'</td>'
					+ '    <td><span class="label '+label_class+'">'+label_name+'</span></td>'
					+ '    <td>'+item.showName+'</td>'
					+ '    <td>'+item.account+'</td>'
					+ '    <td>'+toDate(item.creatTime.time).substr(0, 16)+'</td>'
					+ '    <td>'+toDate(item.lastLogonTime.time).substr(0, 16)+'</td>'
					+ '    <td style="text-align:center;">'
					+ (item.role == 'OperationsManager' ? '--' : 
					(item.disabled?
					 ' <button  onclick="javascript:disabledAdmin('+item.id+','+"'ENABLE'"+')" class="btn btn-primary">启用</button>'
					:' <button  onclick="javascript:disabledAdmin('+item.id+','+"'DISABLE'"+')" class="btn btn-b">禁用</button>'))
					+ '</td></tr>';
		    }
			if(result.buttons!=null&&result.buttons!=""){
		    	html += '<tr><td colspan="10"><div class="pagelist">'+result.buttons+'</td></tr>';
		    }
			if (html=="") {
			     html += '<tr><td colspan="10" class="bold">对不起，暂未检索到相关记录</td></tr>';
			}
			$("tbody#result_list").html(html);
		}
	});
}

function disabledAdmin(id,action) {
		$.jsonRPC.request('admin2AdminService.edit', {
			params : [id, action, ""],
			success : function(result) {
				if (result == "操作成功") {
					layer.msg(result,{time:1000,icon:1});
				} else {
					layer.msg(result,{time:2000,icon:2});
				}
				
				//Refresh
				refreshList(_pageNo);
			}
		});
	}


