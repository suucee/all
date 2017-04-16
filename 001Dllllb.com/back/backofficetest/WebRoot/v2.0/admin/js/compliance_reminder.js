var complianceCddcheck = null;

$(function() {
	complianceCddcheckReminder = new ComplianceCddcheckReminder();
});
var ComplianceCddcheckReminder = function() {
    this.pageNo=1;
    this.allpageNo=1;
	this.init(); 
};
ComplianceCddcheckReminder.prototype = {
	init : function() {
		var _this=this;
		$("#btn1").click(function(){
			_this.getMyCddCheck(_this.pageNo);
		});
		$("#btn2").click(function(){
			_this.getAllCddCheck(_this.pageNo);
		});
		$("#scheme1").change(function(){
			_this.getMyCddCheck(_this.pageNo);
		});
		$("#scheme2").change(function(){
			_this.getAllCddCheck(_this.pageNo);
		});
		this.getMyCddCheck(this.pageNo);
		this.getAllCddCheck(this.allpageNo);
	},
	getMyCddCheck : function(page) {
		var _this = this;
		$.jsonRPC.request("admin2CheckService.getCddchekReminderList", {
			params : [page, 10, "javascript:complianceCddcheckReminder.getMyCddCheck(??)", $("#scheme1").val(), $("#keyword1").val(), "", ""],
			success : function(result) {
				_this.pageNo=result.currentPage;
				var list = result.list.list;
				var html = "";
				for (var i in list) {
					var item = list[i];
					var label_class = "";
					var label_name = "";
					switch (item.state) {
					case 'ACCEPTED':
						label_class = 'label-success';
						label_name = '已通过';
						break;
					case 'AUDITED':
						label_class = 'label-success';
						label_name = '已通过';
						break;
					case 'VERIFIED':
						label_class = 'label-success';
						label_name = '已通过';
						break;
					case 'REJECTED':
						label_class = 'label-important';
						label_name = '已驳回';
						break;

					}
					var dohtml=""
					switch(item.checkType){
						case "用户":
						    dohtml="<a   onclick='parent.openNewWindow("+'"./../admin/compliance_check_user.html?id='+item.id+'"'+",updateList)' class='btn btn-a'>重新审核</a>";
						break;
						case "出金":
						    dohtml="<a   onclick='parent.openNewWindow("+'"./../admin/compliance_check_withdrawal.html?id='+item.id+'"'+",updateList)' class='btn btn-a'>重新审核</a>";
						break;
						case "银行卡":
						    dohtml="<a   onclick='parent.openNewWindow("+'"./../admin/compliance_check_backaccount.html?id='+item.id+'"'+",updateList)' class='btn btn-a'>重新审核</a>";
						break;
					}
					
					var attach="";
					if(item.attachment!=null){
						attach="<a href='/upload"+item.attachment+"' target='_blank'>附件下载</a>";
					}
					
					html += "<tr>";
					html += "<td style='text-align: center;'>" + item.cid + "</td>";
					html += "<td style='text-align: center;'><span  class='label " + label_class + "' >" + label_name + "</span>&nbsp;&nbsp;"+attach+"</td>";
					html += "<td style='text-align: center;'>" + item.checkType + "</td>";
					html += "<td style='text-align: center;'>" + (item.userName==null?'--':'<a onclick="goSeeProfile('+item.id+')">'+item.userName+'</a>')+ "</td>";
					html += "<td style='text-align: center;'>" + (toDate(item.reminderTime.time)) + "</td>";
					html += "<td style='text-align: center;' title='"+item.comment+"' >" + (item.comment==null?'--':item.comment) + "</td>";
					html += "<td style='text-align: center;'>" + item.adminName + "</td>";
					html += "<td>";
					html +=dohtml;
					html +="<a onclick='complianceCddcheckReminder.cancelReminder("+item.cid+")'  class='btn btn-b margin-left15'>取消提醒</a></td>";
					html += "</tr>";
				}
				if(result.buttons!=null&&result.buttons!=""){
			    	html += '<tr><td colspan="10"><div class="pagelist">'+result.buttons+'</td></tr>';
			    }
				if (html=="") {
				     html += '<tr><td colspan="10" class="bold">对不起，暂未检索到相关记录</td></tr>';
				}
				$("#result_list_my").html(html);
			}
		});
	},
	getAllCddCheck : function(page) {
		var _this=this;
		$.jsonRPC.request("admin2CheckService.getCddchekReminderList", {
			params : [page, 10, "javascript:complianceCddcheckReminder.getAllCddCheck(??)",   $("#scheme2").val(), $("#keyword2").val(),  "", ""],
			success : function(result) {
				_this.allpageNo=result.currentPage;
				var list = result.list.list;
				var html = "";
				for (var i in list) {
					var item = list[i];
					var label_class = "";
					var label_name = "";
					switch (item.state) {
					case 'WAITING':
						label_class = 'label-success';
						label_name = '待审核';
						break;
					case 'ACCEPTED':
						label_class = 'label-success';
						label_name = '已通过';
						break;
					case 'AUDITED':
						label_class = 'label-success';
						label_name = '已通过';
						break;
					case 'VERIFIED':
						label_class = 'label-success';
						label_name = '已通过';
						break;
					case 'REJECTED':
						label_class = 'label-important';
						label_name = '已驳回';
						break;

					}
					var dohtml=""
					switch(item.checkType){
						case "用户":
						    dohtml="<a   onclick='parent.openNewWindow("+'"./../admin/compliance_check_user.html?id='+item.id+'"'+",updateList)' class='btn btn-a'>重新审核</a>";
						break;
						case "出金":
						    dohtml="<a   onclick='parent.openNewWindow("+'"./../admin/compliance_check_withdrawal.html?id='+item.id+'"'+",updateList)' class='btn btn-a'>重新审核</a>";
						break;
						case "银行卡":
						    dohtml="<a   onclick='parent.openNewWindow("+'"./../admin/compliance_check_backaccount.html?id='+item.id+'"'+",updateList)' class='btn btn-a'>重新审核</a>";
						break;
					}
					var attach="";
					if(item.attachment!=null){
						attach="<a href='/upload"+item.attachment+"' target='_blank'>附件下载</a>";
					}
					
					html += "<tr>"
					html += "<td style='text-align: center;'>" + item.cid + "</td>";
					html += "<td style='text-align: center;'><span  class='label " + label_class + "' >" + label_name + "</span>&nbsp;&nbsp;"+attach+"</td>";
					html += "<td style='text-align: center;'>" + item.checkType + "</td>";
					html += "<td style='text-align: center;'>" + (item.userName==null?'--':'<a onclick="goSeeProfile('+item.id+')">'+item.userName+'</a>')+ "</td>";
					html += "<td style='text-align: center;'>" + (toDate(item.reminderTime.time)) + "</td>";
					html += "<td style='text-align: center;' title='"+item.comment+"'>" + (item.comment==null?'--':item.comment) + "</td>";
					html += "<td style='text-align: center;'>" + item.adminName + "</td>";
					html += "<td style='text-align: center;'>";
					html +=dohtml;
					html +="<a onclick='complianceCddcheckReminder.cancelReminder("+item.cid+")' class='btn btn-b margin-left15'>取消提醒</a></td>";
					html += "</tr>";
				}
				if(result.buttons!=null&&result.buttons!=""){
			    	html += '<tr><td colspan="10"><div class="pagelist">'+result.buttons+'</div></td></tr>';
			    }
				if (html=="") {
				     html += '<tr><td colspan="10" class="bold">对不起，暂未检索到相关记录</td></tr>';
				}
				$("#result_list_all").html(html);
			}
		});
	},
	cancelReminder:function(id){
		var _this=this;
		$.jsonRPC.request("admin2CheckService.cancelReminder", {
			params : [id],
			success : function(result) {
			   if(result){
				layer.msg("取消提醒成功！",{time:1000,icon:1});
			   	_this.getAllCddCheck(complianceCddcheckReminder.allpageNo);
			   	_this.getMyCddCheck(complianceCddcheckReminder.pageNo);
			   }
			}
		});
	}
};


function updateList(){
	complianceCddcheckReminder.getMyCddCheck(complianceCddcheckReminder.pageNo);
}
function updateAllList(){
	complianceCddcheckReminder.getAllCddCheck(complianceCddcheckReminder.allpageNo);
}