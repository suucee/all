var checkUser=null;
$(function(){
	checkUser=new CheckUser();
});
var CheckUser=function(){
    this.pageNo=1;
	this.init();
};
CheckUser.prototype={
	init:function(){
		this.getUserlist(this.pageNo);
		this.getAllCount();
	},
	getUserlist:function(page) {
		var _this=this;
		$.jsonRPC.request("adminCheckService.getAllUser", {
			params : [page, 20,"javascript:getUserlist(??)"],
			success : function(result) {	
				_this.pageNo=result.currentPage;
				var list = result['list']['list'];
				var html = '';
				for ( i = 0; i < list.length; i++) {
					obj = list[i];
					var registrationTime="--";
					var auditedTime="--";
					var dohtml='';
					var dohtml='<a  class="btn btn-medium btn-primary"  onclick="parent.openNewWindow('+"'./../admin/compliance_check_user.html?id="+obj['id']+"'"+',updateList)">审核</a>';
					var disablehtml='';
					var frozenhtml='';
					
					if(obj['disable']){
						obj['_name'] = obj['_name']+"<span class='red'>[禁]</span>";
				     	disablehtml='<a  class="btn btn-b"  style="color:#fff;" href="javascript:checkUser.disableuser('+obj['id']+',\'DISABLED\','+"'disable'"+')">解禁</a>';
					}else{
				     	disablehtml='<a  class="btn btn-medium"  href="javascript:checkUser.disableuser('+obj['id']+',\'UNDISABLED\','+"'disable'"+')">禁用</a>';
					}
					if(obj['frozen']){
						obj['_name'] = obj['_name']+"<span class='purple'>[冻]</span>";
						frozenhtml='<a  class="btn btn-b"  style="color:#fff;"href="javascript:checkUser.disableuser('+obj['id']+',\'FROZEN\','+"'freeze'"+')">解冻</a>';
					}else{
						frozenhtml='<a  class="btn btn-medium"  href="javascript:checkUser.disableuser('+obj['id']+',\'UNFROZEN\','+"'freeze'"+')">冻结</a>';
					}
					obj.registrationTime== null ? registrationTime='--' : registrationTime=toDate(obj.registrationTime.time);
					obj.auditedTime== null ? auditedTime='--' : auditedTime=toDate(obj.auditedTime.time);
					switch (obj.state) {
					case 'VERIFIED':
						label_class = 'label-success';
						label_name = '已通过';
						break;
					case 'REJECTED':
						label_class = 'label-important';
						label_name = '已驳回';
						break;
					case 'AUDITING':
						label_class = 'label-pending';
						label_name = '待审核';
						break;
					default:
						break;
					}
					var level="";
					switch (obj['level']) {
					case 1:
						level="公司";
						break;
					case 2:
						level="经理/经理";
						break;
					case 3:
						level="员工/员工";
						break;
					default:
						level="客户";
						break;
					}
					html += '<tr><td><a  onclick="goSeeProfile('+obj['id']+')" ><i class="icon-user"></i> ' + obj['_name'] + '</a></td>' 
					+ '	<td><span  class="label '+label_class +'" >' + label_name+ '</span></td>' 
					+ '	<td>' + level + '</td>' 
					+ '	<td>' + (obj['email'] == "" ? '(暂未绑定)' : obj['email']) + '</td>'
					+ '	<td>' + obj['mobile'] + '</td>'
					+ '	<td>' + registrationTime + '</td>' 
					+ '	<td>' + frozenhtml + '&nbsp;&nbsp;' + disablehtml + '&nbsp;&nbsp;' + dohtml + '</td></tr>';
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
	disableuser:function(id,nowscheme,scheme){
		var schemeTip = "";
		if(nowscheme == "DISABLED"){
			schemeTip = "解除禁用";
		}else if(nowscheme == "UNDISABLED"){
			schemeTip = "立即禁用";
		}else if(nowscheme == "FROZEN"){
			schemeTip = "解除冻结";
		}else if(nowscheme == "UNFROZEN"){
			schemeTip = "立即冻结";
		}
		layer.confirm('您确定要<span class="font-red">'+schemeTip+'</span>该用户吗？', {
			  btn: [schemeTip,'放弃'] //按钮
			},function(){
				$.jsonRPC.request("adminCheckService.freezeOrDisableUser", {
					params : [id, scheme],
					success : function(result) {
						if(result==0){
							layer.msg(schemeTip+"该用户成功。", {time:1000,icon:1}, function(){
								checkUser.getUserlist(checkUser.pageNo);
							});
						}else{
							layer.msg(schemeTip+"该用户失败。", {time:2000,icon:2}, function(){
								checkUser.getUserlist(checkUser.pageNo);
							});
						}
						
					}
				});
			},function(){
				return;
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
	checkUser.getAllCount();
	checkUser.getUserlist(checkUser.pageNo);
}






