var checkWithdrawal=null;
$(function(){
	checkWithdrawal=new CheckWithDrawal();
});
var CheckWithDrawal=function(){
	this.pageNo=1;
	this.init();
};
CheckWithDrawal.prototype={
	init:function(){
		this.getWithdrawalslist(this.pageNo);
		this.getAllCount();
	},
	getWithdrawalslist:function(page){
		var _this =this;
			$.jsonRPC.request("adminCheckService.getAllWithdrawals", {
				params : [page, 20,"javascript:getWithdrawalslist(??)"],
				success : function(result) {
					_this.pageNo=result.currentPage;	
					list = result['list']['list'];
					html = '';
					for ( i = 0; i < list.length; i++) {
						obj = list[i];
						var label_class = "";
						var label_name = "";
						var creatTime="--";
						var auditedTime="--";
						var dohtml="<a   onclick='parent.openNewWindow("+'"./../admin/compliance_check_withdrawal.html?id='+obj.id+'"'+",updateList)' class='btn btn-a'>审核</a>";
						obj.creatTime== null ? creatTime='--' : creatTime=toDate(obj.creatTime.time);
						obj.auditedTime== null ? auditedTime='--' : auditedTime=toDate(obj.auditedTime.time);
						switch (obj.state) {
						case 'REMITTED':
							label_class = 'label-success';
							label_name = '已汇出';
							show_remove = true;
							break;
						case 'RETURNED':
							label_class = 'label-pending';
							label_name = '银行退回';
							show_remove = true;
							break;
						case 'CANCELED':
							label_class = 'label-pending';
							label_name = '客户取消';
							show_remove = true;
							break;
						case 'WAITING':
							label_class = 'label-pending';
							label_name = '待审核';
							show_remove = true;
							break;
						case 'AUDITED':
							label_class = 'label-success';
							label_name = '已通过';
							show_remove = true;
							break;
						case 'REJECTED':
							label_class = 'label-important';
							label_name = '已驳回';
							show_remove = true;
							break;
						}
						
						var level="";
						switch (obj['user']['level']) {
						case 1:
							level="公司";
							break;
						case 2:
							level="经理";
							break;
						case 3:
							level="员工";
							break;
						default:
							level="客户";
							break;
						}
						var detail="<a style='text-decoration:underline;' onclick='parent.openNewWindow("+'"./../admin/finance_withdrawal_detail.html?id='+obj.id+'"'+",null)' >" + obj['id'] + "</a>";
						
						html += '<tr><td>'+detail+'</td>' 
						+ '	<td><span  class="label '+label_class +'" >' + label_name+ '</span></td>' 
						+ '	<td>' +level + '</td>' 
						+ '	<td>' + obj['accountName'] + '</td>' 
						+ '	<td><a  onclick="goSeeProfile('+obj.user.id+')"><i class="icon-user"></i> ' + obj['user']['_name'] + '</a></td>' 
						+ '	<td>' + obj['amount'] + '</td>'
						+ '	<td>' + creatTime + '</td>' 
						+ '	<td>'+dohtml+'</td></tr>';
					}
					if(result.buttons!=null&&result.buttons!=""){
		    	         html += '<tr><td colspan="10"><div class="pagelist">'+result.buttons+'</td></tr>';
				    }
					if (html=="") {
					     html += '<tr><td colspan="10" class="bold">对不起，暂未检索到相关记录</td></tr>';
					}
					$('#result_list').html(html);
				}
			});
        },
       getAllCount : function() {
		var _this=this;
		$.jsonRPC.request("admin2CheckService.getAllCount", {
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
	checkWithdrawal.getWithdrawalslist(checkWithdrawal.pageNo);
	checkWithdrawal.getAllCount();
}


