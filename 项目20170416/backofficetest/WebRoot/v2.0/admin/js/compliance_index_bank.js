var bal=null;
$(document).ready(function() {
   bal=new bankAccountList();
});
var bankAccountList=function(){
	this.pageNo=1;
	this.userBankAccountId=0;
	this.urlroot="/upload";
	this.init();
};
bankAccountList.prototype={	
	   init:function(){
			this.getData(this.pageNo);
			this.getAllCount();
	   },	
	   getData:function (_pageNo) {
		    var _this=this;
			var pageSize=10;
			var urlFormat="javascript:bal.getData(??)";
			var scheme="WAITING";
			var keyword="";				
			$.jsonRPC.request("adminCheckBankAccountService.getPage",{
								params : [_pageNo,pageSize,urlFormat,scheme, keyword],
								success : function(result) {
					                _this.pageNo=result.currentPage;	
									_this.initView(result);
								}
							});
	   },
       initView:function(result){
    	    var list=result.list.list;
			var html="";
			for ( var i = 0; i < list.length; i++) {
				var obj = list[i];
				var label_class="";
				var label_name="";
			    var dohtml="<a   onclick='parent.openNewWindow("+'"./../admin/compliance_check_backaccount.html?id='+obj.userBankAccountId+'"'+",updateList)' class='btn btn-a'>审核</a>";
				switch (obj.state) {
				case 'WAITING':
					label_class = 'label-pending';
					label_name = '待审核';
					break;
				case 'AUDITED':
					label_class = 'label-success';
					label_name = '通过审核';
					break;
				case 'REJECTED':
					label_class = 'label-important';
					label_name = '已驳回';
					break;
				default:	
					label_class = 'label-pending';
					label_name = '无状态';
					break;
				}
				html += '<tr><td >' + obj.userBankAccountId+ '</td>' 
				+ '	<td><a  onclick="goSeeProfile('+obj['userId']+')">' + (obj.name==null?"--":obj.name) + '</a></td>' 
				+ '	<td><span  class="label '+label_class +'" >' + label_name+ '</span></td>' 
				+ '	<td>' + (obj.bankName==null||obj.bankName=="null"?"--":obj.bankName) + '</td>'
				+ '	<td>' + (obj.accountName==null?"--":obj.accountName)+ '</td>'
				+ '	<td>' + (obj.accountNo==null?"--":obj.accountNo) + '</td>'
				+ '	<td>' + (obj.countryCode==null?"--":obj.countryCode) + '</td>'
				+ '	<td>' + (obj.updateTime==null?"--":toDate(obj.updateTime.time)) + '</td>'
				+ '	<td>'+dohtml+'</td></tr>';
			}
			if(result.buttons!=null&&result.buttons!=""){
		    	html += '<tr><td colspan="10"><div class="pagelist">'+result.buttons+'</td></tr>';
		    }
			if (html=="") {
			     html += '<tr><td colspan="10" class="bold">对不起，暂未检索到相关记录</td></tr>';
			}
			$('#result_list').html(html);
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
	bal.getData(bal.pageNo);
	bal.getAllCount();
}

