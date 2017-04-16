var depsoit_page=1;
var users_page=1;
var withdrawals_page=1;
var allreminber_page=1;
var allchecks_page=1;
var checkeddeposit_page=1;
var checkeduser_page=1;
var checkedwithdrawal_page=1;
var param=getParam("scheme","deposits");
var isallowdocheck=true;
var isAllow = true;
$(function() {
	switch(param){
		case  "deposits":
			$("#title").text("入金合规");
			  getDepositlist(depsoit_page);
			  break;
		case   "users":
			$("#title").text("用户合规");
			   getUserlist(users_page);
			   break;
		case "withdrawals":
			$("#title").text("出金合规");
			   getWithdrawalslist(withdrawals_page);
			break;
		case "allreminder":
			$("#title").text("所有提醒");
			getAllreminber(allreminber_page,"all");
			break;
		case "myreminder":
			$("#title").text("我的提醒");
			getAllreminber(allreminber_page,"my");
			break;
		case "allchecks":
			$("#title").text("所有审核记录");
			getAllChecks(allchecks_page,"all");
			break;
		case "mychecks":
			$("#title").text("我的审核记录");
			getAllChecks(allchecks_page,"my");
			break;
		case "reject_user":
			$("#title").text("驳回用户记录");
			getCheckedUserlist(checkeduser_page,"reject_user");
			break;
		case "accept_user":
			$("#title").text("通过用户记录");
			getCheckedUserlist(checkeduser_page,"accept_user");
			break;
		case "reject_deposit":
			$("#title").text("驳回入金记录");
			getCheckedDepositlist(checkeddeposit_page,"reject_deposit");
			break;
		case "accept_deposit":
			$("#title").text("通过入金记录");
			getCheckedDepositlist(checkeddeposit_page,"accept_deposit");
			break;
		case "reject_withdrawal":
			$("#title").text("驳回出金记录");
			getCheckedWithdrawalslist(checkedwithdrawal_page,"reject_withdrawal");
			break;
		case "accept_withdrawal":
			$("#title").text("通过出金记录");
			getCheckedWithdrawalslist(checkedwithdrawal_page,"accept_withdrawal");
			break;	
	 	default:
	 		break;        
	}
});
function tabchange(obj,id){
	$(".tabhost-item").hide(200);
	$(".tabhost li").css("background-color","#90EE90");
	$(obj).css("background-color","#809b30");
	$(".tabhost-item").each(function(index,elem){
		if($(elem).data("item-id")==id){
			$(elem).css("display","");
		}
	});
}
//Popup操作
function closePopup() {
	$(".popup_dialog").hide();
	$(".popup_dialog").remove();
}


function openPopup(type, id) {
	switch(type) {
	case "users":
          getUserWindows(id);
		break;
	case "deposits":
		getDepositWindows(id);
		break;
	case "withdrawals":
		getWithDrawalWindows(id);
		break;
	default:
		break;
	}
}


function getUserWindows(id){
	$.get("_popup_check_users.html", function(data) {
		$("body").append(data);
		$.jsonRPC.request("adminCheckService.getOneUserCheck", {
			params : [id],
			success : function(result) {
				user_msg=result['map']['user_msg'];
				bank_msg=result['map']['bank_msg'];
				user_attach=result['map']['attach'];
				cdd=result['map']['cdd'];
				user=result['map']['user'];
				uploaddir=result['map']['imgurl'];
				switch (user['state']) {
				case "UNVERIFIED":
					$(".oldState").html('<span class="label-pending label" >资料不全</span>');
					break;
				case "AUDITING":
					$(".oldState").html('<span class="label-pending label" >待审核</span>');
					break;
				case "REJECTED":
					$(".oldState").html('<span class="label-important label" >已拒绝</span>');
					break;
				case "VERIFIED":
					$(".oldState").html('<span class="label-success label" >已审核</span>');
					break;
			
				default:
					$(".oldState").text("未知");
					break;
				}
				$(".users_check .email").text(user['email']);
				$(".users_check .tel").text(user['mobile']);
				if(user_msg!=null){
					$(".users_check .name").text(nullcheck(user_msg['userName']));
					$(".users_check .ename").text(nullcheck(user_msg['userEName']));
					//下拉选项cardType
					$(".users_check #cardType").text(nullcheck(user_msg['cardType']));
					$(".users_check #cardID").text(nullcheck(user_msg['userIdCard']));
					//下拉居住地区countryCode
					$(".users_check .countryCode").text(nullcheck(user_msg['userNationality']));
					$(".users_check .address").text(nullcheck(user_msg['userEsidentialAddress']));
					$(".users_check .cname").text(nullcheck(user_msg['company']));//公司名称
					$(".users_check .hangye").text(nullcheck(user_msg['userIndustry']));//行业
					$(".users_check .userPosition").text(nullcheck(user_msg['position']));//职位
					$(".users_check .yearsr").text(nullcheck(user_msg['userYearsIncom']));//年收入
					
					$(".users_check .user_comment").text(nullcheck(user_msg['comment']));
				}
				//获取用户绑定银行信息
				if(bank_msg!=null){
					$(".users_check .bankName").text(nullcheck(bank_msg['bankName']));
					$(".users_check .bankNo").text(nullcheck(bank_msg['accountNo']));
					$(".users_check .cardholder_Name").text(nullcheck(bank_msg['accountName']));
					$(".users_check .countryAdress").text(nullcheck(bank_msg['countryCode']));
					}
				$(".users_check .audit_user_id").val(user['id']);
			var htmlpopimg = "";
			if (user_attach != null) {
				user_attach = result['map']['attach']['list'];
				for (ii = 0; ii < user_attach.length; ii++) {
					if (user_attach[ii]['ownerType'] == "user"
							&& user_attach[ii]['ownerId'] == user_msg['id']) {
						htmlpopimg += '<a href="'
								+ uploaddir
								+ user_attach[ii]['path']
								+ '" target="_blank">';
						htmlpopimg += '<div style="margin:5px;display: inline-block;height: 100px;width: 100px;background-image: url('
								+ uploaddir
								+ user_attach[ii]['path']
								+ ');background-repeat: no-repeat;background-position: center;background-size: auto 100%; "></div>';
						htmlpopimg += '</a>';
					}
				}
			}
				if(user_attach==null){
					htmlpopimg ='<p style="margin:20px;text-align:center;">暂无资料证明图片..</p>';
				}
				if(cdd!=null){
					$(".users_check .comment").val(cdd["comment"]);
					$(".users_check .tag").val(cdd["tag"]);
				}
				$("#proof_list").append(htmlpopimg);

				if(user['state']=="REJECTED"){
					$(".users_check .rejected").show();
				}else if(user['state']=="VERIFIED"){
					$(".users_check .verified").show();
				}
			}
		});
//		if(param!="users"){
//			$(".docheckitem").remove();
//		}
		$(".popup_dialog").fadeIn(200);
	});	
}

function getDepositWindows(id){
	
	$.get("_popup_check_deposits.html", function(data) {
		$("body").append(data);
		$.jsonRPC.request("adminCheckService.getOneDeposits", {
			params : [id],
			success : function(result) {
				deposits=result['map']['ds'];
				user_msg=result['map']['user_msg'];
				bank_msg=result['map']['bank_msg'];
				cdd=result['map']['cdd'];
				user_attach=result['map']['attach'];
				user=result['map']['user'];
				uploaddir=result['map']['imgurl'];
				switch (user['state']) {
				case "UNVERIFIED":
					$(".oldState").html('<span class="label-pending label" >资料不全</span>');
					break;
				case "AUDITING":
					$(".oldState").html('<span class="label-pending label" >待审核</span>');
					break;
				case "REJECTED":
					$(".oldState").html('<span class="label-important label" >已拒绝</span>');
					break;
				case "VERIFIED":
					$(".oldState").html('<span class="label-success label" >已审核</span>');
					break;
			
				default:
					$(".oldState").text("未知");
					break;
				}
				$(".deposits_check .email").text(user['email']);
				$(".deposits_check .tel").text(user['mobile']);
				if(user_msg!=null){
					$(".deposits_check .name").text(nullcheck(user_msg['userName']));
					$(".deposits_check .ename").text(nullcheck(user_msg['userEName']));
					//下拉选项cardType
					$(".deposits_check .cardType").text(nullcheck(user_msg['cardType']));
					$(".deposits_check .cardID").text(nullcheck(user_msg['userIdCard']));
					//下拉居住地区countryCode
					$(".deposits_check .countryCode").text(nullcheck(user_msg['userNationality']));
					$(".deposits_check .address").text(nullcheck(user_msg['userEsidentialAddress']));
					$(".deposits_check .cname").text(nullcheck(user_msg['company']));//公司名称
					$(".deposits_check .hangye").text(nullcheck(user_msg['userIndustry']));//行业
					$(".deposits_check .userPosition").text(nullcheck(user_msg['position']));//职位
					$(".deposits_check .yearsr").text(nullcheck(user_msg['userYearsIncom']));//年收入
				}
				//获取用户绑定银行信息
				if(bank_msg!=null){
					$(".deposits_check .bankName").text(nullcheck(bank_msg['bankName']));
					$(".deposits_check .bankNo").text(nullcheck(bank_msg['accountNo']));
					$(".deposits_check .cardholder_Name").text(nullcheck(bank_msg['accountName']));
					$(".deposits_check .countryAdress").text(nullcheck(bank_msg['countryCode']));
					
					}
				$(".deposits_check .audit_deposit_id").val(deposits['id']);
				$(".deposits_check .deposit_id").text(deposits['id']);
			
				deposits.creatTime== null ? creatTime='--' : creatTime=toDate(deposits.creatTime.time);
				deposits.paymentTime== null ? paymentTime='--' : paymentTime=toDate(deposits.paymentTime.time);
				$(".deposits_check .amount").text(deposits['amount'].toFixed(2));
				$(".deposits_check .creatTime").text(creatTime);
				$(".deposits_check .paymentTime").text(paymentTime);
				$(".deposits_check .audit_deposit_id").val(deposits['id']);
				$(".deposits_check .user_comment").val(deposits['userComment']);
				
				var htmlpopimg = "";
				if (user_attach != null) {
					user_attach = result['map']['attach']['list'];
					for (ii = 0; ii < user_attach.length; ii++) {
						if (user_attach[ii]['ownerType'] == "user"
								&& user_attach[ii]['ownerId'] == user_msg['id']) {
							htmlpopimg += '<a href="'
									+ uploaddir
									+ user_attach[ii]['path']
									+ '" target="_blank">';
							htmlpopimg += '<div style="margin:5px;display: inline-block;height: 100px;width: 100px;background-image: url('
									+ uploaddir
									+ user_attach[ii]['path']
									+ ');background-repeat: no-repeat;background-position: center;background-size: auto 100%;"></div>';
							htmlpopimg += '</a>';
						}	
					}
				}
				if(user_attach==null){
					htmlpopimg ='<p style="margin:20px;text-align:center;">暂无资料证明图片..</p>';
				}
				$(".proof_list").html(htmlpopimg);
				
				if(cdd!=null){
					$(".deposits_check .comment").val(cdd["comment"]);
					$(".deposits_check .tag").val(cdd["tag"]);
				}
				if(deposits['state']=="REJECTED"){
					$(".deposits_check .rejected").show();
				}else if(deposits['state']=="ACCEPTED"||deposits['state']=="PENDING_SUPERVISOR"){
					$(".deposits_check .accepted").show();
				}
				
				
				switch (deposits['state']) {
				case "REJECTED":
				case "ACCEPTED":
				case "PENDING_SUPERVISOR":
				case "PENDING_AUDIT":
					break;
				default:
					$(".docheckitem").remove();
					break;
				}
					
			}
		});
//		if(param!="deposits"){
//			$(".docheckitem").remove();
//		}

		$(".popup_dialog").fadeIn(200);
	});
	
	
	
}



function getWithDrawalWindows(id){
	$.get("_popup_check_withdrawals.html", function(data) {
		$("body").append(data);
		$.jsonRPC.request("adminCheckService.getOneWithdrawals", {
			params : [id],
			success : function(result) {
				user_msg=result['map']['user_msg'];
				wd=result['map']['wd'];
				bank_msg=result['map']['bank_msg'];
				cdd=result['map']['cdd'];
				user_attach=result['map']['attach'];
				user=result['map']['user'];
				uploaddir=result['map']['imgurl'];
				switch (user['state']) {
				case "UNVERIFIED":
					$(".oldState").html('<span class="label-pending label" >资料不全</span>');
					break;
				case "AUDITING":
					$(".oldState").html('<span class="label-pending label" >待审核</span>');
					break;
				case "REJECTED":
					$(".oldState").html('<span class="label-important label" >已拒绝</span>');
					break;
				case "VERIFIED":
					$(".oldState").html('<span class="label-success label" >已审核</span>');
					break;
			
				default:
					$(".oldState").text("未知");
					break;
				}
				if(wd!=null){
					$(".withdrawals_check .audit_withdrawals_id").val(wd['id']);
					
					$(".withdrawals_check .bankName").text(wd['bankName']);
					$(".withdrawals_check .countryCode").text(wd['country']);
					$(".withdrawals_check .creatTime").text(toDate(wd['creatTime'].time));
					$(".withdrawals_check .accountName").text(wd['accountName']);
					$(".withdrawals_check .accountNo").text(wd['accountNumber']);
					$(".withdrawals_check .exchangeRate").text(wd['exchangeRate'].toFixed(4));
					$(".withdrawals_check .amount").text(wd['amount'].toFixed(2)+"  "+wd['currency']);
					$(".withdrawals_check .amountCNY").text((wd['amount']*wd['exchangeRate']).toFixed(2)+'  CNY');
					$(".withdrawals_check .userMemo").text(wd['userMemo']);
					$(".withdrawals_check .bankBranch").text(wd['bankBranch']);
					$(".withdrawals_check .bankAddress").text(wd['bankAddress']);
					$(".withdrawals_check .swiftCode").text(wd['swiftCode']);
					$(".withdrawals_check .ibanCode").text(wd['ibanCode']);
					if(wd['country']=="CN"){
				    	$(".withdrawals_check .home").show();
				    	$(".withdrawals_check .abroad").hide();
				    }else if(wd['country']!=null&&wd['country']!=""){
				    	$(".withdrawals_check .home").hide();
				    	$(".withdrawals_check .abroad").show();
				    }
				}else{
				    	$(".withdrawals_check .home").show();
				    	$(".withdrawals_check .abroad").hide();
				}
				
				$(".withdrawals_check .email").text(user['email']);
				$(".withdrawals_check .tel").text(user['mobile']);
				if(user_msg!=null){
					$(".withdrawals_check .name").text(nullcheck(user_msg['userName']));
					$(".withdrawals_check .ename").text(nullcheck(user_msg['userEName']));
					//下拉选项cardType
					$(".withdrawals_check .cardType").text(nullcheck(user_msg['cardType']));
					$(".withdrawals_check .cardID").text(nullcheck(user_msg['userIdCard']));
					//下拉居住地区countryCode
					$(".withdrawals_check .countryCode").text(nullcheck(user_msg['userNationality']));
					$(".withdrawals_check .address").text(nullcheck(user_msg['userEsidentialAddress']));
					$(".withdrawals_check .cname").text(nullcheck(user_msg['company']));//公司名称
					$(".withdrawals_check .hangye").text(nullcheck(user_msg['userIndustry']));//行业
					$(".withdrawals_check .userPosition").text(nullcheck(user_msg['position']));//职位
					$(".withdrawals_check .yearsr").text(nullcheck(user_msg['userYearsIncom']));//年收入
				}
				
				//获取用户绑定银行信息
				if(bank_msg!=null){
					$(".withdrawals_check .bankName").text(nullcheck(bank_msg['bankName']));
					$(".withdrawals_check .bankNo").text(nullcheck(bank_msg['accountNo']));
					$(".withdrawals_check .cardholder_Name").text(nullcheck(bank_msg['accountName']));
					$(".withdrawals_check .countryAdress").text(nullcheck(bank_msg['countryCode']));
					}
				$(".withdrawals_check .audit_withdrawals_id").val(wd['id']);
				$(".withdrawals_check .withdrawals_id").text(wd['id']);
				$(".withdrawals_check .user_comment").val(wd['userComment']);
			
				var htmlpopimg = "";
				if (user_attach != null) {
					
					user_attach = result['map']['attach']['list'];
					for (ii = 0; ii < user_attach.length; ii++) {
						if (user_attach[ii]['ownerType'] == "user"
								&& user_attach[ii]['ownerId'] == user_msg['id']) {
							htmlpopimg += '<a href="'
									+ uploaddir
									+ user_attach[ii]['path']
									+ '" target="_blank">';
							htmlpopimg += '<div style="margin:5px;display: inline-block;height: 100px;width: 100px;background-image: url('
									+ uploaddir
									+ user_attach[ii]['path']
									+ ');background-repeat: no-repeat;background-position: center;background-size: auto 100%;"></div>';
							htmlpopimg += '</a>';
						}	
					}
				}
				if(user_attach==null){
					htmlpopimg ='<p style="margin:20px;text-align:center;">暂无资料证明图片..</p>';
				}
				$(".proof_list").html(htmlpopimg);
				
				if(cdd!=null){
					$(".withdrawals_check .comment").val(cdd["comment"]);
					$(".withdrawals_check .tag").val(cdd["tag"]);
				}
				if(wd['state']=="REJECTED"){
					$(".withdrawals_check .rejected").show();
				}else if(wd['state']=="AUDITED"||wd['state']=="PENDING_SUPERVISOR"){
					$(".withdrawals_check .audited").show();
				}
				
				switch (wd['state']) {
				case "REJECTED":
				case "AUDITED":
				case "PENDING_SUPERVISOR":
				case "WAITING":
					break;
				default:
					$(".docheckitem").remove();
					break;
				}
			}
		});
//		if(param!="withdrawals"){
//			$(".docheckitem").remove();
//		}
		

		
		$(".popup_dialog").fadeIn(200);
	});
	
	
	
	
	
}

function doCheck0(type, dotype) {
	var comment = $(".comment").val();
	var user_comment = $(".user_comment").val();
	var remindertime=$(".remindertime").val();
	var tag=$(".tag").val();
	var dopassword=$(".dopassword").val();
	var a_id = $("#attachment_id").val();

		if(isAllow){
			switch(type) {
			case  "deposits":
				var id = $(".deposits_check .audit_deposit_id").val();
				$.jsonRPC.request("adminCheckService.doCheck", {
					params : [id, type, dotype, comment,user_comment,remindertime,tag,dopassword,a_id],
					success : function(result) {
						if (result==1) {
							$(".deposits_check .rejected").hide();
							$(".deposits_check .accepted").hide();
							if(dotype=="REJECTED"){
								$(".deposits_check .rejected").show();
							}else if(dotype=="PENDING_SUPERVISOR"){
								$(".deposits_check .accepted").show();
							}
							alert("操作成功");
							closePopup();
							window.location.href="compliance.html?scheme=mychecks";
						}else if(result==2){
							alert("操作密码不正确，操作失败!");
						}else if(result==3){
							alert("操作失败");
						}
						isallowdocheck=true;
					}
				});
				break;
			case "users":
				id = $(".users_check .audit_user_id").val();
				$.jsonRPC.request("adminCheckService.doCheck", {
					params : [id, type, dotype, comment,user_comment,remindertime,tag,dopassword,a_id],
					success : function(result) {
						if (result==1) {
							$(".users_check .rejected").hide();
							$(".users_check .verified").hide();
							if(dotype=="REJECTED"){
								$(".users_check .rejected").show();
							}else if(dotype=="AUDITED"){
								$(".users_check .verified").show();
							}
							alert("操作成功");
							
							closePopup();
							window.location.href="compliance.html?scheme=mychecks";
						}else if(result==2){
							alert("操作密码不正确，操作失败!");
						}else if(result==4){
							alert("用户资料为空，操作失败");
						}else if(result==3){
							alert("操作失败");
						}
						isallowdocheck=true;
					}
				});
				break;
			case "withdrawals":
				id = $(".withdrawals_check .audit_withdrawals_id").val();
				$.jsonRPC.request("adminCheckService.doCheck", {
					params : [id, type, dotype, comment,user_comment,remindertime,tag,dopassword,a_id],
					success : function(result) {
						if (result==1) {
							$(".withdrawals_check .rejected").hide();
							$(".withdrawals_check .audited").hide();
							if(dotype=="REJECTED"){
								$(".withdrawals_check .rejected").show();
							}else if(dotype=="AUDITED"){
								$(".withdrawals_check .audited").show();
							}
							alert("操作成功");
								closePopup();
								window.location.href="compliance.html?scheme=mychecks";
						}else if(result==2){
							alert("操作密码不正确，操作失败!");
						}else if(result==3){
							alert("操作失败");
						}
						isallowdocheck=true;
					}
				});
				break;
			default:
				break;
			}
		}	
}



function doPopup(type, dotype) {
	comment = $(".comment").val();
	user_comment = $(".user_comment").val();
	remindertime=$(".remindertime").val();
	tag=$(".tag").val();
	dopassword=$(".dopassword").val();
	var a_id = $("#attachment_id").val();
	if(!isallowdocheck){
		alert("操作提交中!");
		return;
	}
	if(dotype=="REJECTED"){
	    if(comment==""){
	         alert("请填写合规备注");
	         $(".comment").focus();
	         return;	
	     }	
	    if(user_comment==""){
	         alert("请填写合规备注");	
	         $(".user_comment").focus();
	         return;
	     }	
	}
	if($(".dopassword").val()==""){
		alert("请填写操作密码!");
		$(".dopassword").focus();
	   return;
	}
	if($("#checkfile").val() !=""){
		uploadCddCheckFile(type, dotype);
	} else {
		doCheck0(type, dotype);
	}
}
function uploadCddCheckFile(type, dotype) {
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
				
				doCheck0(type, dotype);
			}
			isallowdocheck=true;
		},
		error : function(data, status, e)// 服务器响应失败处理函数
		{
			alert("文件上传失败");
			isallowdocheck=true;
			isAllow = false;
		}
	});
}

//获取入金列表
function getDepositlist(page) {
	$("#result_head").html('' + '	<th width="10%">ID</th>' 
		+ '	<th width="12%" >状态</th>' 
		+ '	<th width="7%" >级别</th>' 
		+ '	<th width="18%" >用户</th>' 
		+ '	<th width="10%" >金額</th>' 
		+ '	<th width="20%" >提交時間</th>' 
	    + '	<th width="20%" >操作</th>');
	
	$('#result_list').html("");
	$.jsonRPC.request("adminCheckService.getAllDeposits", {
		params : [page, 20, "javascript:getDepositlist(??)"],
		success : function(result) {		
			list = result['list']['list'];
			html = '';
			for ( i = 0; i < list.length; i++) {
				obj = list[i];
				var label_class = "";
				var label_name = "";
				var creatTime="--";
				var auditedTime="--";
				var dohtml='';
				var dohtml='<a class="btn btn-medium btn-primary"  style="color:#fff;"href="javascript:openPopup('+"'deposits'"+','+obj['id']+')">审核</a>';
				obj.creatTime== null ? creatTime='--' : creatTime=toDate(obj.creatTime.time);
				obj.auditedTime== null ? auditedTime='--' : auditedTime=toDate(obj.auditedTime.time);
				switch (obj.state) {
				case 'DEPOSITED':
					label_class = 'label-success';
					label_name = '已到账';
					show_remove = true;
					break;
				case 'PENDING_PAY':
					label_class = 'label-pending';
					label_name = '待付款';
					show_remove = true;
					break;
				case 'PENDING_AUDIT':
					label_class = 'label-pending';
					label_name = '待审核';
					show_remove = true;
					dohtml='<a  class="btn btn-medium btn-primary"  style="color:#fff;" href="javascript:openPopup('+"'deposits'"+','+obj['id']+')">审核</a>';
					break;
				case 'ACCEPTED':
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
				
				html += '<tr><td   >' + obj['id'] + '</td>' 
				+ '	<td><span  class="label '+label_class +'" >' + label_name+ '</span></td>' 
				+ '	<td>' + level + '</td>'
				+ '	<td><a href="user_detail.html?id='+obj['user']["id"]+'" target="_blank">' + obj['user']['_name'] + '</a></td>' 
				+ '	<td>' + obj['amount'] + '</td>'
				+ '	<td>' + creatTime + '</td>' 
				+ '	<td>'+dohtml+'</td><tr>';
			}
			html += '<tr><td colspan="10"><div class="pagelist">' + result.buttons + '</td></tr>';
			$('#result_list').append(html);
		}
	});
}
//获取用户列表
function getUserlist(page) {
	$("#result_head").html('' + '	<th width="15%"  >用户</th>' 
	+ '	<th width="12%" >状态</th>' 
	+ '	<th width="8%" >级别</th>' 
	+ '	<th width="10%" >账号</th>' 
	+ '	<th width="10%" >手机</th>' 
	+ '	<th width="15%" >注册时间</th>' 
   + '	<th width="30%" >操作</th>');
	$('#result_list').html("");
	$.jsonRPC.request("adminCheckService.getAllUser", {
		params : [page, 20,"javascript:getUserlist(??)"],
		success : function(result) {		
			list = result['list']['list'];
			html = '';
			for ( i = 0; i < list.length; i++) {
				obj = list[i];
				var registrationTime="--";
				var auditedTime="--";
				var dohtml='';
				var dohtml='<a  class="btn btn-medium btn-primary"  style="color:#fff;" href="javascript:openPopup('+"'users'"+','+obj['id']+')">审核</a>';
				var disablehtml='';
				var frozenhtml='';
				if(obj['disable']){
			     	disablehtml='<a  class="btn btn-medium btn-primary"  style="color:#fff;" href="javascript:disableuser('+obj['id']+','+"'disable'"+')">已禁用</a>';
				}else{
			     	disablehtml='<a  class="btn btn-medium"  href="javascript:disableuser('+obj['id']+','+"'disable'"+')">禁用</a>';
				}
				if(obj['frozen']){
					frozenhtml='<a  class="btn btn-medium btn-primary"  style="color:#fff;"href="javascript:disableuser('+obj['id']+','+"'freeze'"+')">已冻结</a>';
				}else{
					frozenhtml='<a  class="btn btn-medium"  href="javascript:disableuser('+obj['id']+','+"'freeze'"+')">冻结</a>';
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
				html += '<tr><td><a href="user_detail.html?id='+obj['id']+'" target="_blank"><i class="icon-user"></i> ' + obj['_name'] + '</a></td>' 
				+ '	<td><span  class="label '+label_class +'" >' + label_name+ '</span></td>' 
				+ '	<td>' + level + '</td>' 
				+ '	<td><a href="user_detail.html?id='+obj['id']+'" target="_blank">' + obj['email'] + '</a></td>'
				+ '	<td>' + obj['mobile'] + '</td>'
				+ '	<td>' + registrationTime + '</td>' 
				+ '	<td>' + frozenhtml + '&nbsp;&nbsp;' + disablehtml + '&nbsp;&nbsp;' + dohtml + '</td></tr>';
			}
			html += '<tr><td colspan="10"><div class="pagelist">' + result.buttons + '</td></tr>';
			$('#result_list').append(html);
		}
	});
}
function disableuser(id,scheme){
	$.jsonRPC.request("adminCheckService.freezeOrDisableUser", {
		params : [id,scheme],
		success : function(result) {	
            getUserlist(users_page);
		}
	});
}
//获取出金列表
function getWithdrawalslist(page){
	$("#result_head").html('' + '	<th width="10%"  >ID</th>' 
			+ '	<th width="12%" >状态</th>' 
			+ '	<th width="7%" >级别</th>' 
			+ '	<th width="15%" >出金账户</th>' 
			+ '	<th width="15%" >用户</th>' 
			+ '	<th width="10%" >金額</th>' 
			+ '	<th width="20%" >提交時間</th>' 
		    + '	<th width="10%" >操作</th>');
	
			$('#result_list').html("");
			$.jsonRPC.request("adminCheckService.getAllWithdrawals", {
				params : [page, 20,"javascript:getWithdrawalslist(??)"],
				success : function(result) {		
					list = result['list']['list'];
					html = '';
					for ( i = 0; i < list.length; i++) {
						obj = list[i];
						var label_class = "";
						var label_name = "";
						var creatTime="--";
						var auditedTime="--";
						var dohtml='';
						var dohtml='<a   class="btn btn-medium btn-primary"  style="color:#fff;" href="javascript:openPopup('+"'withdrawals'"+','+obj['id']+')">审核</a>';
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
							dohtml='<a   class="btn btn-medium btn-primary"  style="color:#fff;" href="javascript:openPopup('+"'withdrawals'"+','+obj['id']+')">审核</a>';
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
						
						html += '<tr><td   >' + obj['id'] + '</td>' 
						+ '	<td><span  class="label '+label_class +'" >' + label_name+ '</span></td>' 
						+ '	<td>' +level + '</td>' 
						+ '	<td>' + obj['accountName'] + '</td>' 
						+ '	<td><a href="user_detail.html?id=' + obj["user"]["id"] + '"><i class="icon-user"></i> ' + obj['user']['_name'] + '</a></td>' 
						+ '	<td>' + obj['amount'] + '</td>'
						+ '	<td>' + creatTime + '</td>' 
						+ '	<td>'+dohtml+'</td><tr>';
					}
					html += '<tr><td colspan="10"><div class="pagelist">' + result.buttons + '</td></tr>';
					$('#result_list').append(html);
				}
			});
}

//获取提醒列表
function getAllreminber(page,scheme){
	$("#result_head").html('' + '	<th width="5%"  >ID</th>' 
			+ '	<th width="12%" >状态</th>' 
			+ '	<th width="8%" >类型</th>' 
			+ '	<th width="15%" >用户</th>' 
			+ '	<th width="15%" >提醒时间</th>' 
			+ '	<th width="15%" >提交時間</th>' 
			+ '	<th width="15%" >审核時間</th>' 
		   + '	<th width="20%" >操作</th>');
			$('#result_list').html("");
			$.jsonRPC.request("adminCheckService.getReminderCheck", {
				params : [page, 20,"javascript:getAllreminber(??,'"+scheme+"')",scheme],
				success : function(result) {
					allreminber_page=result['currentPage'];
					list = result['list']['list'];
					
					
					html = '';
					for ( i = 0; i < list.length; i++) {
						obj = list[i];
						var label_class = "";
						var label_name = "";
						var label_type = "";
						var creatTime="--";
						var auditedTime="--";
						var reminderTime="--";
						obj.reminderTimestamp== null ? reminderTime='--' : reminderTime=toDate(obj.reminderTimestamp.time);
						obj.timestamp== null ? auditedTime='--' : auditedTime=toDate(obj.timestamp.time);
						var user="";
						var userId = 0;
						if(obj.deposit!=null){
							label_type="入金";
							userId = obj.deposit.user.id;
							obj.deposit.creatTime== null ? creatTime='--' : creatTime=toDate(obj.deposit.creatTime.time);
							var dohtml='<a   class="btn btn-medium btn-primary"  style="color:#fff;" href="javascript:openPopup('+"'deposits'"+','+obj.deposit['id']+')">查看</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;';
                            user=obj.deposit['user'];  
						}
						if(obj.withdrawal!=null){
							label_type="出金";
							userId = obj.withdrawal.user.id;
							obj.withdrawal.creatTime== null ? creatTime='--' : creatTime=toDate(obj.withdrawal.creatTime.time);
							var dohtml='<a   class="btn btn-medium btn-primary"  style="color:#fff;" href="javascript:openPopup('+"'withdrawals'"+','+obj.withdrawal['id']+')">查看</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;';
							 user=obj.withdrawal['user'];  
						}
						if(obj.user!=null){
							label_type="用户";
							userId = obj.user.id;
							obj.user.registrationTime== null ? creatTime='--' : creatTime=toDate(obj.user.registrationTime.time);
							var dohtml='<a   class="btn btn-medium btn-primary"  style="color:#fff;" href="javascript:openPopup('+"'users'"+','+obj.user['id']+')">查看</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;';
							user=obj.user;
						}
						dohtml+='<a   class="btn btn-medium btn-primary"  style="color:#fff;" href="javascript:cancelReminber('+obj['id']+')">取消提醒</a>';
						switch (obj.result) {
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
						html += '<tr><td>' + obj['id'] + '</td>' 
						+ '	<td><span  class="label '+label_class +'" >' + label_name+ '</span></td>' 
						+ '	<td>' + label_type+ '</td>' 
						+ '	<td><a href="user_detail.html?id='+userId+'" target="_blank">' + user['email']+ '</a></td>' 
						+ '	<td>' + reminderTime + '</td>'
						+ '	<td>' + creatTime + '</td>' 
						+ '	<td>'+auditedTime+'</td>'
						+ '	<td>'+dohtml+'</td><tr>';
					}
					html += '<tr><td colspan="10"><div class="pagelist">' + result.buttons + '</td></tr>';
					$('#result_list').append(html);
				}
			});
}
//取消提醒
function   cancelReminber(id){
	$.jsonRPC.request("adminCheckService.cancelReminber", {
		params : [id],
		success : function(result) {
				if(result){
					switch(param){
						case "allreminder":
							getAllreminber(allreminber_page,"all");
							break;
						case "myreminder":
							getAllreminber(allreminber_page,"my");
							break;
						}
				}
		    }
		});
	
	
	
}
//获取审核
function getAllChecks(page,scheme){
	$("#result_head").html('' + '	<th width="6%"  >记录ID</th>' 
			+ '	<th width="12%" >状态</th>' 
			+ '	<th width="8%" >类型</th>' 
			+ '	<th width="8%" >级别</th>' 
			+ '	<th width="15%" >用户</th>' 
			+ '	<th width="10%" >属性</th>' 
			+ '	<th width="10%" >审核人</th>' 
			+ '	<th width="15%" >审核時間</th>' 
		   + '	<th width="15%" >操作</th>');
			$('#result_list').html("");
			
			$.jsonRPC.request("adminCheckService.getAllChecks", {
				params : [page, 20,"javascript:getAllChecks(??,'"+scheme+"')",scheme],
				success : function(result) {		
					list = result['map']['cddcheckarr']['list']['list'];
					html = '';
					for ( i = 0; i < list.length; i++) {
						obj = list[i]['map'];
						var simple = "";
						var label_class = "";
						var label_name = "";
						var label_type = "";
						var creatTime="--";
						var auditedTime="--";
						var reminderTime="--";
						var dohtml="";
						var user_email="";
						var level="";
						var userId = 0;
						obj.timestamp== null ? auditedTime='--' : auditedTime=toDate(obj.timestamp.time);
						
						if(obj.deposit_id!=null){
							label_type = "入金";
							userId = obj.deposit_uid;
							user_email=obj.deposit_email;
							obj.deposit_creatTime== null ? creatTime='--' : creatTime=toDate(obj.deposit_creatTime.time);
							switch (obj.deposit_state) {
							case "REJECTED":
							case "ACCEPTED":
							case "PENDING_SUPERVISOR":
							case "WAITING":
								dohtml='<a   class="btn btn-medium btn-primary"  style="color:#fff;" href="javascript:openPopup('+"'deposits'"+','+obj.deposit_id+')">重新审核</a>';
								break;
							default:
								 dohtml='<a   class="btn btn-medium btn-primary"  style="color:#fff;" href="javascript:openPopup('+"'deposits'"+','+obj.deposit_id+')">查看</a>';
								break;
							}
							
							switch (obj.deposit_level) {
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
							level = obj._vipGradeName;
						}
						
						if(obj.withdrawal_id!=null){
							label_type="出金";
							userId = obj.withdrawal_uid;
							user_email=obj.withdrawal_email;
							obj.withdrawal_creatTime== null ? creatTime='--' : creatTime=toDate(obj.withdrawal_creatTime.time);
							switch (obj.withdrawal_state) {
							case "REJECTED":
							case "AUDITED":
							case "PENDING_SUPERVISOR":
							case "PENDING_AUDIT":
								 dohtml='<a   class="btn btn-medium btn-primary"  style="color:#fff;" href="javascript:openPopup('+"'withdrawals'"+','+obj.withdrawal_id+')">重新审核</a>';
								break;
							default:
								 dohtml='<a    class="btn btn-medium btn-primary"  style="color:#fff;" href="javascript:openPopup('+"'withdrawals'"+','+obj.withdrawal_id+')">查看</a>';
								break;
							}
							switch (obj.withdrawal) {
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
						}
						if(obj.user_id!=null){
							label_type="用户";
							userId = obj.user_id;
							user_email=obj.user_email;
							user=obj.user;
							obj.user_registrationTime== null ? creatTime='--' : creatTime=toDate(obj.user_registrationTime.time);
							 dohtml='<a   class="btn btn-medium btn-primary"  style="color:#fff;" href="javascript:openPopup('+"'users'"+','+obj.user_id+')">重新审核</a>';
							 switch (obj.user_state) {
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
							 switch (obj.user_level) {
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
							 level = obj._vipGradeName;
						}
						switch (obj.result) {
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
						imgulhtml="";
						if(obj.attach_path!=null){
						   imgurl = result['map']['imgurl']+obj.attach_path;
						   imgulhtml='&nbsp;&nbsp;<a   href=' + imgurl + '><i class="icon-download-alt"  stytle="color:#000;"></i>&nbsp;附件</a>';
						}
                        
						
						html += '<tr><td   >' + obj['id'] + '</td>' 
							+ '	<td><span  class="label '+label_class +'" >' + label_name+ '</span>'+imgulhtml + '</td>' 
							+ '	<td>' + label_type+ '</td>'  
							+ '	<td>' + level+ '</td>'  
							+ '	<td> <a href="user_detail.html?id='+userId+'" target="_blank"><i class="icon-user"></i> ' + obj['_name'] + '</a></td>' 
							+ '	<td>' + simple + '</td>'
							+ '	<td>' + obj['admin_name'] + '</td>'
							+ '	<td>'+auditedTime+'</td>'
							+ '	<td>'+dohtml+'</td><tr>';
					}
					html += '<tr><td colspan="10"><div class="pagelist">' + result.map.cddcheckarr.buttons + '</td></tr>';
					$('#result_list').append(html);
				}
			});
}







//获取入审核完成金列表
function getCheckedDepositlist(page,scheme) {
	$("#result_head").html('' + '	<th width="10%"  >记录ID</th>' 
	+ '	<th width="12%" >状态</th>' 
	+ '	<th width="10%" >级别</th>' 
	+ '	<th width="10%" >用户</th>' 
	+ '	<th width="15%" >提交時間</th>' 
	+ '	<th width="15%" >审核時間</th>' 
	+ '	<th width="10%" >审核人</th>'
   + '	<th width="10%" >操作</th>');
	$('#result_list').html("");
	$.jsonRPC.request("adminCheckService.getAllChecks", {
		params : [page, 20,"javascript:getAllChecks(??,'"+scheme+"')",scheme],
		success : function(result) {		
			list = result['map']['cddcheckarr']['list']['list'];
			html = '';
			for ( i = 0; i < list.length; i++) {
				obj = list[i]['map'];
				var label_class = "";
				var label_name = "";
				var label_type = "";
				var creatTime="--";
				var auditedTime="--";
				var reminderTime="--";
				var dohtml="";
				var user_email="";
				var level="";
				obj.reminderTimestamp== null ? reminderTime='--' : reminderTime=toDate(obj.reminderTimestamp.time);
				obj.timestamp== null ? auditedTime='--' : auditedTime=toDate(obj.timestamp.time);
				if(obj.deposit_id!=null){
					label_type="入金";
					user_email=obj.deposit_email;
					obj.deposit_creatTime== null ? creatTime='--' : creatTime=toDate(obj.deposit_creatTime.time);
					switch (obj.deposit_state) {
					case "REJECTED":
					case "ACCEPTED":
					case "PENDING_SUPERVISOR":
					case "WAITING":
						 dohtml='<a   class="btn btn-medium btn-primary"  style="color:#fff;" href="javascript:openPopup('+"'deposits'"+','+obj.deposit_id+')">重新审核</a>';
						break;
					default:
						 dohtml='<a   class="btn btn-medium btn-primary"  style="color:#fff;" href="javascript:openPopup('+"'deposits'"+','+obj.deposit_id+')">查看</a>';
						break;
					}
					
					switch (obj.deposit_level) {
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
					level = obj._vipGradeName;
				}
				switch (obj.result) {
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
				imgulhtml="";
				if(obj.attach_path!=null){
				   imgurl=result['map']['imgurl']+obj.attach_path;
				   imgulhtml='&nbsp;&nbsp;<a   href=' + imgurl + '><i class="icon-download-alt"  stytle="color:#000;"></i>&nbsp;附件</a>';
				}
                
				
				html += '<tr><td>' + obj['id'] + '</td>' 
				+ '	<td><span  class="label '+label_class +'" >' + label_name+ '</span>'+imgulhtml + '</td>' 
				+ '	<td>' + level+ '</td>'  
				+ '	<td><a href="user_detail.html?id='+ obj.deposit_uid +'"><i class="icon-user"></i> ' + obj['_name']+ '</a></td>' 
				+ '	<td>' + creatTime + '</td>'
				+ '	<td>'+auditedTime+'</td>'
				+ '	<td>' + obj['admin_name']+ '</td>'
				+ '	<td>'+dohtml+'</td><tr>';
			}
			html += '<tr><td colspan="10"><div class="pagelist">' + result.map.cddcheckarr.buttons + '</td></tr>';
			$('#result_list').append(html);
		}
	});
}



//获取审核完成用户列表
function getCheckedUserlist(page,scheme) {
	$("#result_head").html('' + '	<th width="10%"  >记录ID</th>' 
	+ '	<th width="12%" >状态</th>' 
	+ '	<th width="7%" >级别</th>' 
	+ '	<th width="18%" >账号</th>' 
	+ '	<th width="15%" >注册時間</th>' 
	+ '	<th width="15%" >审核時間</th>' 
	+ '	<th width="10%" >审核人</th>'
   + '	<th width="10%" >操作</th>');
	$('#result_list').html("");
	$.jsonRPC.request("adminCheckService.getAllChecks", {
		params : [page, 20,"javascript:getAllChecks(??,'"+scheme+"')",scheme],
		success : function(result) {		
			list = result['map']['cddcheckarr']['list']['list'];
			html = '';
			for ( i = 0; i < list.length; i++) {
				obj = list[i]['map'];
				var label_class = "";
				var label_name = "";
				var label_type = "";
				var creatTime="--";
				var auditedTime="--";
				var reminderTime="--";
				var dohtml="";
				var user_email="";
				var level="";
				obj.reminderTimestamp== null ? reminderTime='--' : reminderTime=toDate(obj.reminderTimestamp.time);
				obj.timestamp== null ? auditedTime='--' : auditedTime=toDate(obj.timestamp.time);
				if(obj.user_id!=null){
					label_type="用户";
					user_email=obj.user_email;
					user=obj.user;
					obj.user_registrationTime== null ? creatTime='--' : creatTime=toDate(obj.user_registrationTime.time);
					 dohtml='<a   class="btn btn-medium btn-primary"  style="color:#fff;" href="javascript:openPopup('+"'users'"+','+obj.user_id+')">重新审核</a>';
					 switch (obj.user_state) {
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
					 switch (obj.user_level) {
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
					 level = obj._vipGradeName;
				}
				switch (obj.result) {
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
				imgulhtml="--";
				if(obj.attach_path!=null){
				   imgurl=result['map']['imgurl']+obj.attach_path;
				   imgulhtml='&nbsp;&nbsp;<a  href=' + imgurl + '><i class="icon-download-alt"  stytle="color:#000;"></i>&nbsp;附件</a>';
				}
                
				
				html += '<tr><td   >' + obj['id'] + '</td>' 
				+ '	<td><span  class="label '+label_class +'" >' + label_name+ '</span>'+imgulhtml + '</td>' 
				+ '	<td>' + level+ '</td>'  
				+ '	<td><a href="user_detail.html?id='+obj.user_id+'" target="_blank"><i class="icon-user"></i> ' + obj['_name']+ '</a></td>' 
				+ '	<td>' + creatTime + '</td>'
				+ '	<td>'+auditedTime+'</td>'
				+ '	<td>' + obj['admin_name']+ '</td>'
				+ '	<td>'+dohtml+'</td><tr>';
			}
			html += '<tr><td colspan="10"><div class="pagelist">' + result.map.cddcheckarr.buttons + '</td></tr>';
			$('#result_list').append(html);
		}
	});
}

//获取审核完成出金列表
function getCheckedWithdrawalslist(page,scheme){
	$("#result_head").html('' + '	<th width="10%"  >记录ID</th>' 
			+ '	<th width="12%" >状态</th>'  
			+ '	<th width="7%" >级别</th>'  
			+ '	<th width="9%" >出金账户</th>' 
			+ '	<th width="9%" >用户</th>' 
			+ '	<th width="15%" >提交時間</th>' 
			+ '	<th width="15%" >审核時間</th>' 
			+ '	<th width="10%" >审核人</th>'
		   + '	<th width="10%" >操作</th>');
	$.jsonRPC.request("adminCheckService.getAllChecks", {
		params : [page, 20,"javascript:getAllChecks(??,'"+scheme+"')",scheme],
		success : function(result) {		
			list = result['map']['cddcheckarr']['list']['list'];
			html = '';
			for ( i = 0; i < list.length; i++) {
				var obj = list[i]['map'];
				var label_class = "";
				var label_name = "";
				var label_type = "";
				var creatTime="--";
				var auditedTime="--";
				var reminderTime="--";
				var dohtml="";
				var user_email="";
				var level="";
				obj.reminderTimestamp== null ? reminderTime='--' : reminderTime=toDate(obj.reminderTimestamp.time);
				obj.timestamp == null ? auditedTime='--' : auditedTime=toDate(obj.timestamp.time);
				
				if(obj.withdrawal_id!=null){
					label_type="出金";
					user_email=obj.withdrawal_email;
					obj.withdrawal_creatTime== null ? creatTime='--' : creatTime=toDate(obj.withdrawal_creatTime.time);
					switch (obj.withdrawal_state) {
					case "REJECTED":
					case "AUDITED":
					case "PENDING_SUPERVISOR":
					case "PENDING_AUDIT":
						 dohtml='<a   class="btn btn-medium btn-primary"  style="color:#fff;" href="javascript:openPopup('+"'withdrawals'"+','+obj.withdrawal_id+')">重新审核</a>';
						break;
					default:
						 dohtml='<a   class="btn btn-medium btn-primary"  style="color:#fff;" href="javascript:openPopup('+"'withdrawals'"+','+obj.withdrawal_id+')">查看</a>';
						break;
					}
					switch (obj.withdrawal) {
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
				}
				
				switch (obj.result) {
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
				imgulhtml="--";
				if(obj.attach_path!=null){
				   imgurl=result['map']['imgurl']+obj.attach_path;
				   imgulhtml='&nbsp;&nbsp;<a    href=' + imgurl + '><i class="icon-download-alt"  stytle="color:#000;"></i>&nbsp;附件</a>';
				}
                
				
				html += '<tr><td   >' + obj['id'] + '</td>' 
					+ '	<td><span  class="label '+label_class +'" >' + label_name+ '</span>'+imgulhtml + '</td>' 
					+ '	<td>' + level+ '</td>'  
					+ '	<td>' + obj['withdrawal_account_name']+ '</td>' 
					+ '	<td><a href="user_detail.html?id='+obj.withdrawal_uid+'" target="_blank"><i class="icon-user"></i> ' + obj['_name']+ '</a></td>' 
					+ '	<td>' + creatTime + '</td>'
					+ '	<td>'+auditedTime+'</td>'
					+ '	<td>' + obj['admin_name']+ '</td>' 
					+ '	<td>'+dohtml+'</td><tr>';
			}
			html += '<tr><td colspan="10"><div class="pagelist">' + result.map.cddcheckarr.buttons + '</td></tr>';
			$('#result_list').append(html);
		}
	});
}

function nullcheck(obj){
	if(obj==null){
		return "";
	}else{
		return obj;
	}
}


