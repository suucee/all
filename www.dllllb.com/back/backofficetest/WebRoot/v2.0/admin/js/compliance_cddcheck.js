var complianceCddcheck = null;
$(function() {
	complianceCddcheck = new ComplianceCddcheck();
});
var ComplianceCddcheck = function() {
	this.pageNo = 1;
	this.allpageNo=1;
	this.myScheme="my_check_user";
	this.allScheme="all_check_user";
	this.init();
};
ComplianceCddcheck.prototype = {
	init : function() {
		var _this=this;
		this.getMyCddCheck(this.pageNo);
		this.getAllCddCheck(this.allpageNo);
		this.getCddCheckCounts();
		$(".mySchemeChange").click(function() {
			$(".mySchemeChange").removeClass('btn-b');
			$(".mySchemeChange").addClass('btn-f');
			$(this).removeClass('btn-f');
			$(this).addClass('btn-b');
		      _this.myScheme=$(this).data("scheme");
		      _this.getMyCddCheck(_this.pageNo);
		});
		$(".allSchemeChange").click(function() {
			$(".allSchemeChange").removeClass('btn-b');
			$(".allSchemeChange").addClass('btn-f');
			$(this).removeClass('btn-f');
			$(this).addClass('btn-b');
		      _this.allScheme=$(this).data("scheme");
		      _this.getAllCddCheck(_this.pageNo);
		});
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
	},
	getMyCddCheck : function(_pageNo) {
		var _this = this;
		$.jsonRPC.request("admin2CheckService.getCddchekList", {
			params : [_pageNo, 10, "javascript:complianceCddcheck.getMyCddCheck(??)", _this.myScheme,$("#keyword1").val(), $("#scheme1").val() ],
			success : function(result) {
				_this.pageNo=result.currentPage;
				var list = result.list.list;
				var html = "";
				for (var i in list) {
					var item = list[i];
					var label_class = "";
					var label_name = "";
					var dohtml=""
					switch(item.checkType){
						case "用户":
						    dohtml="<a   onclick='parent.openNewWindow(\"./../admin/compliance_check_user.html?id="+item.id+"&cid="+item.cid+"\",updateList)' class='btn btn-a'>重新审核</a>";
						break;
						case "出金":
						    dohtml="<a   onclick='parent.openNewWindow(\"./../admin/compliance_check_withdrawal.html?id="+item.id+"&cid="+item.cid+"\",updateList)' class='btn btn-a'>重新审核</a>";
						break;
						case "银行卡":
						    dohtml="<a   onclick='parent.openNewWindow(\"./../admin/compliance_check_backaccount.html?id="+item.id+"&cid="+item.cid+"\",updateList)' class='btn btn-a'>重新审核</a>";
						break;
					}
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
					var attach="";
					if(item.attachment!=null){
						attach="<a href='/upload"+item.attachment+"' target='_blank'>附件下载</a>";
					}
					html += "<tr>";
					
//					var pass = '<a style="text-decoration:underline;" onclick="parent.openNewWindow('+"'./../admin/finance_withdrawal_detail.html?id="+item.id+"'"+',donothing)">'+ item.id +'</a>' ;
					
					html += "<td>" + item.cid + "</td>";
					html += "<td style='text-align: left;'><span  class='label " + label_class + "' >" + label_name + "</span>&nbsp;&nbsp;"+attach+"</td>";
					html += "<td>" + item.checkType + "</td>";
					html += "<td>" + (item.userName==null?'--':item.userName)+ "</td>";
					html += "<td>" + (toDate(item.checkTime.time)) + "</td>";
					html += "<td>" + item.adminName + "</td>";
					html += "<td>"+dohtml+"</td>";
					html += "</tr>";
				}
				if(result.buttons!=null&&result.buttons!=""){
			    	html += '<tr><td colspan="10"><div class="pagelist">'+result.buttons+'</td></tr>';
			    }
				if (html=="") {
				     html += '<tr><td colspan="10" class="bold">对不起，暂无相关记录</td></tr>';
				}
				$("#result_list_my").html(html);
			}
		});
	},
	getAllCddCheck : function(_pageNo) {
		var _this=this;
		$.jsonRPC.request("admin2CheckService.getCddchekList", {
			params : [_pageNo, 10, "javascript:complianceCddcheck.getAllCddCheck(??)",  _this.allScheme,$("#keyword2").val() ,  $("#scheme2").val()],
			success : function(result) {
				_this.allpageNo=result.currentPage;
				var list = result.list.list;
				var html = "";
				for (var i in list) {
					var item = list[i];
					var label_class = "";
					var label_name = "";
					var dohtml=""
					switch(item.checkType){
						case "用户":
						    dohtml="<a   onclick='parent.openNewWindow(\"./../admin/compliance_check_user.html?id="+item.id+"&cid="+item.cid+"\",updateList)' class='btn btn-a'>重新审核</a>";
						break;
						case "出金":
						    dohtml="<a   onclick='parent.openNewWindow(\"./../admin/compliance_check_withdrawal.html?id="+item.id+"&cid="+item.cid+"\",updateList)' class='btn btn-a'>重新审核</a>";
						break;
						case "银行卡":
						    dohtml="<a   onclick='parent.openNewWindow(\"./../admin/compliance_check_backaccount.html?id="+item.id+"&cid="+item.cid+"\",updateList)' class='btn btn-a'>重新审核</a>";
						break;
					}
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
					var attach="";
					if(item.attachment!=null){
						attach="<a href='/upload"+item.attachment+"' target='_blank'>附件下载</a>";
					}
					html += "<tr>";
					html += "<td>" + item.cid + "</td>";
					html += "<td style='text-align: left;'><span  class='label " + label_class + "' >" + label_name + "</span>&nbsp;&nbsp;"+attach+"</td>";
					html += "<td>" + item.checkType + "</td>";
					html += "<td>" + (item.userName==null?'--':item.userName+'</a>')+ "</td>";
					html += "<td>" + (toDate(item.checkTime.time)) + "</td>";
					html += "<td>" + item.adminName + "</td>";
					html += "<td>"+dohtml+"</td>";
					html += "</tr>";
				}
				if(result.buttons!=null&&result.buttons!=""){
			    	html += '<tr><td colspan="10"><div class="pagelist">'+result.buttons+'</td></tr>';
			    }
				if (html=="") {
				     html += '<tr><td colspan="10" class="bold">对不起，暂无相关记录</td></tr>';
				}
				$("#result_list_all").html(html);
			}
		});
	},
	getCddCheckCounts : function() {
		var _this=this;
		$.jsonRPC.request("admin2CheckService.getCddCheckCounts", {
			params : [],
			success : function(result) {
				var list = result.list;
				for (var i in list) {
					var item=list[i];
					$(".count_"+item['scheme']).text(item['count']);
				}
			}
		});
	}
};
function updateList(){
	complianceCddcheck.getMyCddCheck(complianceCddcheck.pageNo);
}
function  updateAllList(){
	complianceCddcheck.getAllCddCheck(complianceCddcheck.allpageNo);
	
} 