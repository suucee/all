const PAGE_SIZE = 10;
var notifyPageNo=1;


$(document).ready(function() {
	if(loginUser.vipGrade > 0 ){//代理
		$(".extensionDiv").show(200);
	}
	
	
	$("#search").click(function(){
		getAnnouncementList(1);
	});
	getBalance();
	paymentPwd();
	getAnnouncementList(1);
	getNotifyList(notifyPageNo);
	getCodeAndLink();
	getUserEmailAndName();
});

function getUserEmailAndName(){
	//获取详细信息
	$.jsonRPC.request("user2Service.getUserEmailAndName",{
	params:[],
	success:function(result){
		if (result.map.name == "") {
			$(".bindNameA").attr("href","profile_index.html");
			$(".nameTip").show(200);
		}
		else{
			$(".nameTip").hide(200);
		}
		if (result.map.email == "" && result.map.mobile != "") {
			
			$(".bindEmailA").attr("href",
					"javascript:parent.openNewWindow('./../user/bind_email.html',null)");
			$(".emailTip").show(200);
		}else{
			$(".emailTip").hide(200);
		}
	}
	});
}



function getBalance(){
	$.jsonRPC.request("userAccountService.getBalanceList",{
		params:[],
		success:function(result){
			for (var i in result.list) {
				var item = result.list[i];
				$(".amountAvailable").html(item.amountAvailable.toMoney(2));
				$(".amountFrozen").html(item.amountFrozen.toMoney(2));
			}
			
		}
	});
}



function getCodeAndLink() {
	$.jsonRPC.request("userRankService.getReferralCode", {
		params : [],
		success : function(result) {
			if (result != null && result != '') {
				url = location.href;
				index1 = url.indexOf("user");
				referralLink = url.substring(0, index1) + "common/register.html?code=" + result;

				$("#extensionCode").html(result);
				$("#extensionURL").html(referralLink);
			}
		}
	});
}
function getNotifyList(page){
	
	
	//将个人信息状态提取出来
	$.jsonRPC.request("userService.state",{
		params:[],
		success:function(date){
			var tooltip = '';
			var state_operation = '';
			var html = '';
			switch (date) {
			case "UNVERIFIED":
				tooltip = '<span class="label-pending label" >资料不全</span>（资料不全！补全资料以激活账户）';
				state_operation = '<a href="profile_index.html" class="btn btn-b" >完善资料</a>';
				break;
			case "AUDITING":
				tooltip = '<span class="label-pending label" >审核中</span>（可修改或追加资料）';
				state_operation = '<a href="profile_index.html" class="btn btn-primary">查看资料</a>';
				break;
			case "REJECTED":
				tooltip = '<span class="label-important label" >被驳回</span>（可修改资料重新提交）';
				state_operation = '<a href="profile_index.html" class="btn btn-primary">修改资料</a>';
				break;
			case "VERIFIED":
				tooltip = '<span class="label-success label" >账户已激活</span>（不可更改资料）';
				state_operation = '<a href="profile_index.html" class="btn btn-primary">查看资料</a>';
				break;
			default:
				layer.msg("获取个人资料状态失败，请稍后再试",{time:2000,icon:2});
				return;
			}
			$("#state").html(tooltip);
			$("#state_operation").html(state_operation);
		}
	});
	
	//将通知状态提取出来
	$.jsonRPC.request('user2NotifyService.getPageAll',{
		params : [ page, 5,"javascript:getNotifyList(??);"],
		success : function(result) {
			if(result!=null){
				  notifyPageNo=result.currentPage;
//				  setMessage_count(result.totalRows);这里面包含了已读和未读，不能这样设置
		          setMessage_info (result.list.list,result.buttons);
	         }
			
		}
	});	
}
	


function setMessage_count (count_int) {
	$("#message_tip").html('您有<span id="message_count">'+count_int+'</span>条新的通知:');
}

function setMessage_info (list,buttons) {
	var html ="";
	var greyStyle = " style='color: grey;'";
	var count = 0;

	for(var i in list){
		var item=list[i];
		var seehtml="";
		if(!item.read)
			count++;
		switch(item.notifyType){
			case "user":
//			   seehtml='<a class="btn btn-a margin-left15" href="profile_index.html?id='+item.id+'">查看详情</a>';
			break;
			case "bankAccount":
//			   seehtml='<a class="btn btn-a margin-left15" href="profile_bank_accounts.html?id='+item.id+'">查看详情</a>';
			break;
			case "withdrawal":
			   seehtml='<a class="btn btn-a margin-left15" onclick="parent.openNewWindow('+"'./../user/withdrawal_detail.html?id="+item.notifyId+"'"+',null)">查看详情</a>';
			break;
		}
		html +='<tr' +(item.read ? greyStyle : '')+' ><td>';
		html +=	'<i ' +(item.read ? greyStyle : '')+' class="fa fa-circle"></i>';
		html +=	'<span id="date">'+toDate(item.createTime.time)+'</span>';
		html +=	'<span id="message_info">'+item.content+'</span>';
		html +=	'</td><td>';
		if(!item.read){
		    html +=	'<a class="btn btn-b" onclick="i_read('+item.id+',this)">我知道了</a>';
	 	}
		html +=	seehtml;
		html +=	'</td></tr>';
	}
	setMessage_count(count);	//只设置未读消息		
	if(buttons!=null&&buttons!=""){
    	html += '<tr><td colspan="10"><div class="pagelist">'+buttons+'</td></tr>';
    }
	if (html=="") {
	     html += '<tr><td colspan="10" class="bold">暂时还没有新的通知。</td></tr>';
	}
	$("#message_table #notify_list").html(html);
} 
function i_read(id,obj){
	$.jsonRPC.request('user2NotifyService.readNotify',{
		params : [id],
		success : function(result) {
			if (result){
				var tr = $(obj).parents("tr");
				$(tr).animate({"opacity":"0"},500,"linear",function () {
					var message_count = $("#message_count").html();
					$("#message_count").html( parseInt(message_count) - 1);
                   getNotifyList(notifyPageNo);
				});	
           }
		}
	});	
}



function getAnnouncementList(pageNo){
	
	var keyword = $("#keyword").val();
	$.jsonRPC.request('userAnnouncementService.getPage',
			{
				params : [ pageNo, PAGE_SIZE,"javascript:getAnnouncementList(??);","publish",keyword],
				success : function(result) {
					var html = '';
					if (result != null) {
						if(result.list.list.length==0){
							$("#notice_table").html('<tr><td colspan="2">对不起，没有找到相关内容。</td></tr>');
							return;
						}
						for ( var i in result.list.list) {
							var item = result.list.list[i];
							html += '<tr><td><i class="fa fa-circle" ></i>'
									+'<a onclick="parent.openNewWindow('+"'./../user/announcement_detail.html?id="+item.id+"'"+',null)">'
									+(item.top?'<font color="red">[顶]</font>'+item.title:item.title)
									+'</a></td>'
									+'<td><span id="date">'+toDate(item.publishTime.time)+'</span></td></tr>';
						}
						
						if(result.buttons!=null&&result.buttons!=""){
					    	html += '<tr><td colspan="10"><div class="pagelist">'+result.buttons+'</td></tr>';
					    }
						if (html=="") {
						     html += '<tr><td colspan="10" class="bold">对不起，暂未检索到相关记录</td></tr>';
						}
						$("#notice_table").html(html);
					}else{
						$("#notice_table").html('<tr><td colspan="2">网络状况不好，请稍候再试吧。</td></tr>');
					}
				}
			}
	);
}


/**
 * 检查登录密码是否设置
 */
function paymentPwd(){
	$.jsonRPC.request('userService.operationPwd', {
		params : [],
		success : function(result) {
			if (!result) {	//没设置支付密码
				$(".setPayA").attr("href","profile_changepassword.html?style=pay");
				$(".payTip").show(200);
			}else{
				$(".payTip").hide(200);
			}
		}
	});
}