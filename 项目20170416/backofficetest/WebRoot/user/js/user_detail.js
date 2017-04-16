var _pageNo = 1;
var _id = 0;
var isallowdocheck=true;
var isAllow = true;
$(document).ready(function() {
	$("#tab_list li").click(function(){
		var index = parseInt($(this).attr("id").substr("tag_".length));

		$("#tab_list li").removeClass("current");
		$(".tab_content:visible").fadeOut(200);
		
		$("#tab_"+index).addClass("current");
		$("#content_"+index).fadeIn(200);
	});
	
	//show
	if (loginUser.role == 'ComplianceOfficer') {
		$(".compliance_show").fadeIn(200);
	}
	
	_id = getParam("id", "0");
	$(".id").text(_id);
	/*refreshDetail(_id);*/
	refreshStatementList(_pageNo);
	refreshUserBankAccountList(_pageNo);
	getUserInfo();
	getMT4UserList();
});

function refreshUserBankAccountList(_pageNo) {
	$.jsonRPC.request("adminUserService.getUserBankAccountList",{
		params:[_id,_pageNo, 20, "javascript:refreshUserBankAccountList(??);"],
		success:function(result){
			
		var html = '';
		if(result.list.list==0){
			html+='<tr><td colspan="10" style="text-align:center;">暂无用户银行卡信息..</td></tr>';
		}
		for (var i in result.list.list) {
			var item = result.list.list[i];
			var type = item.currencyType;
			
			html += '<tr>'
				+ '	<td>'+(item.state ? '<font color="#b94a48">[绑定]</font>':'')+item.bankName+'</a></td>'
				+ '	<td>'+item.accountName+'</td>'
				+ '	<td>'+item.accountNo+'</td>'
				+ '	<td>'+type+'</td>'
				+ '	<td>'+item.bankAddress+'</td>'
				+ '</tr>';
	    }
		html += '<tr><td colspan="10"><div class="pagelist">'+result.buttons+'</td></tr>';
		
		$("tbody#user_bankaccount_list").html(html);
		
	} 
	});
}
function refreshStatementList(_pageNo) {
	$.jsonRPC.request("adminUserService.getUserBalanceLogs",{
		params:[_id,_pageNo, 10, "javascript:refreshStatementList(??);"],
		success:function(result){
			
			var html = '';
			if(result.list.list==0){
				html+='<tr><td colspan="10" style="text-align:center;">暂无用户资金变更记录..</td></tr>';
			}
			for (var i in result.list.list) {
				var item = result.list.list[i];
				var type = item.currencyType;
				var refType = "";
				var refId = 0;
				var css_bg = "";
				var amount_color="";
				
				if(item.amount<0){
					amount_color="color:#c00000";
				}
				if (item.depositId > 0) {
					refId = item.depositId;
					css_bg = "background:#f0f8ff";
				} else if (item.withdrawalId > 0) {
					refType = 'withdrawal';
					refId = item.withdrawalId;
					css_bg = "background:#fff8f0";
				} else if (item.transfersId > 0) {
					refId = item.transfersId;
					css_bg = "background:#fff8f0";
				} 
				html += '<tr style="'+css_bg+'">'
					+ '	<td>'+'#'+refId+'</a></td>'
					+ '	<td>'+toDate(item.creatTime.time)+'</td>'
					+ '	<td class="amount bold " style="'+amount_color+'">'+item.amount.toFixed(2)+' '+type+'</td>'
					+ '	<td class="amount bold">'+item.amountAvailable.toFixed(2)+' '+type+'</td>'
					+ '	<td>'+item.description+'</td>'
					+ '</tr>';
			}
			html += '<tr><td colspan="10"><div class="pagelist">'+result.buttons+'</td></tr>';
			
			$("tbody#user_balancelogs_list").html(html);
			
		} 
	});
}


function getUserInfo(){
	$.jsonRPC.request("adminUserService.getUserProfile",{
		params:[_id],
		success:function(result){
		user=result['map']['user'];	
		user=result['map']['user'];	
		userProfile=result['map']['userProfile'];	
		
		$(".user_id").text(user['id']);
		switch (user['state']) {
		case "UNVERIFIED":
			$(".oldState").html('<span class="label-pending label" >资料不全</span>');
			$(".newState").html('<option value="">[当前不能操作]</option>');
			break;
		case "AUDITING":
			$(".oldState").html('<span class="label-pending label" >待审核</span>');
			$(".newState").html('<option value="VERIFIED">通过</option><option value="REJECTED">拒绝</option>');
			break;
		case "REJECTED":
			$(".oldState").html('<span class="label-important label" >已拒绝</span>');
			$(".newState").html('<option value="VERIFIED">通过</option>');
			break;
		case "VERIFIED":
			$(".oldState").html('<span class="label-success label" >已审核</span>');
			$(".newState").html('<option value="REJECTED">拒绝</option>');
			break;
	
		default:
			alert("系统错误，请稍后再试");
			break;
		}
			
		$(".email").text(user['email']);
		$(".tel").text(user['mobile']);
		if(userProfile!=null){
			$(".name").text(userProfile['userName']);
			$(".ename").text(userProfile['userEName']);
			//下拉选项cardType
			$("#cardType").text(userProfile['cardType']);
			$("#cardID").text(userProfile['userIdCard']);
			//下拉居住地区countryCode
			$(".countryCode").text(userProfile['userNationality']);
			$(".address").text(userProfile['userEsidentialAddress']);
			$(".cname").text(userProfile['company']);//公司名称
			$(".hangye").text(userProfile['userIndustry']);//行业
			$(".userPosition").text(userProfile['position']);//职位
			$(".yearsr").text(userProfile['userYearsIncom']);//年收入
		}
			
		}
	});
	//获取图片
	$.jsonRPC.request("adminUserService.getUserImage",{
		params : [_id],
	   success : function(result) {
		var html = "";
		// 获取图片
		if (result != null) {
			for ( var i in result.list) {
				imgList = result.list[i];
				html += '<div style="width:100px;height:130px;text-align:center;margin:5px;float:left;overflow: hidden;">'
						+ '<a href="/upload'+imgList.path+'" target="_blank"><img border="0" style="margin:0px 0px 5px 0px;" src="/upload'
						+ imgList.path
						+ '" height="100px" />'
						+ '</a></div>';
			                            }
		                    }
		else {
			html +='无';
		}
		$("#proof_list").html(html);
	}
});	
}

function checkUsers() {
	dotype=$(".newState").val();
	comment = $(".comment").val();
	user_comment = $(".user_comment").val();
	remindertime=$(".remindertime").val();
	tag=$(".tag").val();
	dopassword=$(".dopassword").val();
	if(!isallowdocheck){
		alert("操作提交中!");
		return;
	}
	if(dotype==''){
		alert("当前你不可做任何操作");
		return;
	}
	if(dotype=="REJECTED"){
	    if(comment==""){
	         alert("请填写合规备注");	
	         return;
	     }	
	    if(user_comment==""){
	         alert("请填写合规备注");
	         return;	
	     }	
	}
	if($(".dopassword").val()==""){
		alert("清填写操作密码!");
	   return;
	}
	if($("#checkfile").val() !=""){
		uploadCddCheckFile();
	}
	if(isAllow){
			$.jsonRPC.request("adminCheckService.doCheck", {
				params : [_id, "users", dotype, comment,user_comment,remindertime,tag,dopassword,0/*data['id']*/],
				success : function(result) {
					if (result==1) {
						alert("操作成功");
						location.reload();
					}else if(result==2){
						alert("操作密码不正确，操作失败!");
					}else if(result==4){
						alert("，用户资料为空，操作失败");
					}else if(result==3){
						alert("操作失败");
					}
					isallowdocheck=true;
				}
			});
	}
}

function uploadCddCheckFile() {
isallowdocheck = false;
$.ajaxFileUpload({
	url : '../fileupload/check.do', // 用于文件上传的服务器端请求地址
	secureuri : false, // 一般设置为false
	fileElementId : 'checkfile', // 文件上传空间的name属性 <input type="file"
	// id="file"
	dataType : 'json', // 返回值类型 一般设置为json
	success : function(data, status) // 服务器成功响应处理函数
	{
		if (data == "") {
			alert("图片上传失败！");
			isAllow = false;
		} else {
			$("#attachment_id").val(data['id']);
		}
		isallowdocheck=true;
	},
	error : function(data, status, e)// 服务器响应失败处理函数
	{
		alert("文件上传失败");
		isAllow = false;
		isallowdocheck=true;
	}
});
}

function getMT4UserList() {
	$.jsonRPC.request("adminUserService.getMT4UserList", {
		params : [_id],
		success : function(result) {
			if (result) {
				var html = '';
				
				for (var i in result.list) {
					var item = result.list[i];
					html += '<tr>'
						+ '<td>'+item.login+(item.enable ? '' : '<span class="red">(禁)</span>')+(item.enableReadOnly ? '<span class="purple">(冻)</span>' : '')+'</td>'
						+ '<td style="text-align:right;">'+item.balance.toFixed(2)+'</td>'
						+ '<td>'+item.name+'</td>'
						+ '<td>'+item.city+'</td>'
						+ '<td>'+toDate(item.regdate * 1000)+'</td>'
						+ '<td>'+(item.lastTradeSyncTime != null ? toDate(item.lastTradeSyncTime.time) : '-')+'</td>'
						+ '<td></td>'
						+ '</tr>';
				}
				$("#mt4_user_result_list").html(html);
			}
		}
	});
}
