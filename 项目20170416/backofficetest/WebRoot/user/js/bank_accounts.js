var authorizationCode = "";
const LANG_ADD_BANK_ACCOUNT = '添加银行卡';
const LANG_EDIT_BANK_ACCOUNT = '编辑银行卡';
var isAllow=true;
var urlroot="/upload"
$(document).ready(function(){
	refreshList();
	
	$("select[name='countryCode']").change(function(){
		var code = $(this).val();
		if (code == "HK") {
			$(".hk_show").slideDown(200);
		} else {
			$(".hk_show").slideUp(200);
		}
	    if($("select[name='countryCode']").val()=="CN"){
	    	$(".home").show();
	    	$(".abroad").hide();
	    }else{
	    	$(".home").hide();
	    	$(".abroad").show();
	    }
	});
//	$("#bankfile").change(function(){
//		if(isAllow){
//			uploadBankFile();
//		}else{
//			alert("文件上传中！");
//		}
//	});
	
});

function refreshList() {
		$.jsonRPC.request("userAccountService.getBankAccountList",{
			params:[],
			success:function(result){
		if (result != null) {
			var html = '';
			
			for (var i in result.list) {
				var item = result.list[i];

				var label_class="";
				var label_name="";
				switch (item.state) {
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
				
				html += '<tr>' 
					+ '	<td><span class="label '+label_class+'">'+label_name+'</span></td>'
					+ '	<td>'+(item.default ? '<font color="#b94a48">[绑定]</font>':'')+item.bankName+'</td>'
					+ '	<td>'+item.accountName+'</td>'
					+ '	<td>'+item.accountNo+'</td>'
					+ '	<td>'+item.countryCode+'</td>'
					+ '	<td class="actions_icons">'
					+ (item.default ? '-':
					(item.state=="AUDITED"?'':'<a class="btn btn-small btn-edit" href="javascript:void(0);" onclick="javascript:editAccount('+item.id+');" title="Edit"><i class="icon-edit"></i></a>')
					+ '		<a class="btn btn-small" href="javascript:void(0);" onclick="javascript:deleteAccount('+item.id+');" title="Delete"><i class="icon-remove"></i></a>')
					+'</td>'
					+ '</tr>';
			}
			
			$("#bankAccountList").html(html);
		}
	 }
	});
}

function deleteAccount(id) {
	if (authorizationCode == "" && loginUser.securityPolicy > 0) {
		openSecurityConfirm();
		return;
	}
	
	if (!window.confirm("您确定要删除该银行账户吗?")) {
		return;
	}
	
		$.jsonRPC.request("userAccountService.deleteBankAccount",{
			params:[id],
			success:function(result){
		
		if (result) {
			refreshList();
		} else {
			window.alert("删除银行账户失败");
		}
			}
	});
}


function addAccount() {
	/*if (authorizationCode == "" && loginUser.securityPolicy > 0) {
		openSecurityConfirm();
		return;
	}
	*/
	$(".bankstate").text("添加中");
	$("#account_form_container").fadeIn(200);
	$("input[name='id']").val(0);
	$(".form_title").text(LANG_ADD_BANK_ACCOUNT);
	$("#attachment_id").val("");
	$("#bankfile").val("");	
	$("#account_form_container input").val("");
	$("#account_form_container select").val("");
	$("#attachment_id").val("");
	$("#bankfile").val("");	
	$("#imgView").html("");
}

function editAccount(id) {
	if (authorizationCode == "" && loginUser.securityPolicy > 0) {
		openSecurityConfirm();
		return;
	}
	$("#imgView").html("");
	$("#account_form_container").fadeIn(200);
	$(".form_title").text(LANG_EDIT_BANK_ACCOUNT);
		$.jsonRPC.request("userAccountService.getBankAccount",{
			params:[id],
			success:function(result){
				if (result) {
					
					//show
					var label_class="";
					var label_name="";
					switch (result.state) {
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
					$(".bankstate").attr("class","bankstate label "+label_class);
					$(".bankstate").text(label_name);
					
					
					
					$("input[name='id']").val(result.id);
					$("input[name='bankName']").val(result.bankName);
					$("input[name='accountName']").val(result.accountName);
					$("input[name='accountNo']").val(result.accountNo);
					$("select[name='countryCode']").val(result.countryCode);
					$("select[name='countryCode']").change();
				    if($("select[name='countryCode']").val()=="CN"){
				    	$(".home").show();
				    	$(".abroad").hide();
				    }else{
				    	$(".home").hide();
				    	$(".abroad").show();
				    }
					$("input[name='swiftCode']").val(result.swiftCode);
					$("input[name='ibanCode']").val(result.ibanCode);
					$("input[name='bankBranch']").val(result.bankBranch);
					$("input[name='bankAddress']").val(result.bankAddress);	
					$("#attachment_id").val("");
					$("#bankfile").val("");				
					
					
					
					
					
					
				} else {
					window.alert("获取银行卡信息失败！");
					closeAddForm();
				}	
			}
	});
	$.jsonRPC.request("userAccountService.getBankImageList",{
		params:[id],
		success:function(result){
			var list=result.list;
			var html="";
			for(var i=0;i<list.length;i++){
				var item=list[i];
				html+="<div class='thisimage' data-attachment="+item.id+" style='width:100px;herght:150px;text-align: center;display:inline-block;margin:5px;'>";
				html+="<div style='background-image: url("+urlroot+item.path+");background-position: center;background-size:contain;background-repeat: no-repeat;width: 100px;height: 100px;margin-bottom:5px;'>";
				html+="</div>";
				html+="<a style='' onclick='deleteBankFile(this)'><span class='btn btn-primary'><span>移除</span></span></a>"
				html+="</div>";
			}
			$("#imgView").html(html);
		}
	});
		
}

function closeAddForm() {
	$("#account_form_container").fadeOut(200);
}


function submitForm() {
		var id = $("input[name='id']").val();
		var bankName = $("input[name='bankName']").val();
		var accountName = $("input[name='accountName']").val();
		var accountNo = $("input[name='accountNo']").val();
		var countryCode = $("select[name='countryCode']").val();

		 var swiftCode = $("input[name='swiftCode']").val();
		 var ibanCode = $("input[name='ibanCode']").val();
		 var bankBranch = $("input[name='bankBranch']").val();
	     var bankAddress = $("input[name='bankAddress']").val();
	     var attachment_id = $("input[name='attachment_id']").val();
		

		$("input[name='bankName']").css("background", "white");
		$("input[name='accountName']").css("background", "white");
		$("input[name='accountNo']").css("background", "white");
		$("select[name='countryCode']").css("background", "white");
		
		if (bankName == '') {
			$("input[name='bankName']").css("background", "#ffff80");
			$("input[name='bankName']").focus();
		} else if (accountName == '') {
			$("input[name='accountName']").css("background", "#ffff80");
			$("input[name='accountName']").focus();
		} else if (accountNo == '') {
			$("input[name='accountNo']").css("background", "#ffff80");
			$("input[name='accountNo']").focus();
		} else if (countryCode == '') {
			$("select[name='countryCode']").css("background", "#ffff80");
			$("select[name='countryCode']").focus();
		}  else if (bankBranch == ''&&countryCode=="CN") {
			$("input[name='bankBranch']").css("background", "#ffff80");
			$("input[name='bankBranch']").focus();
		}  else if (bankAddress == ''&&countryCode=="CN") {
			$("input[name='bankAddress']").css("background", "#ffff80");
			$("input[name='bankAddress']").focus();
		}else {
			
			if (id > 0) {
				$.jsonRPC.request("userAccountService.editBankAccount",{
					params:[id, bankName,accountName, accountNo, countryCode,swiftCode,ibanCode,bankBranch,bankAddress],
					success:function(result){
				if (result) {
					closeAddForm();
					refreshList();
					window.alert("银行卡账户信息更改成功");
				} else {
					window.alert("银行卡账户信息更改失败");
				}
					}
				});
			} else {
				$.jsonRPC.request("userAccountService.addBankAccount",{
					params:[ bankName,  accountName,accountNo, countryCode,swiftCode,ibanCode,bankBranch,bankAddress,attachment_id],
					success:function(result){
				
				if (result) {
					closeAddForm();
					refreshList();
					window.alert("银行卡账户添加成功");
				} else {
					window.alert("银行卡账户添加失败");
				}
					
				}
				});
		
           }
		}
}

function uploadBankFile() {
	if(isAllow){
		isAllow=false;
		var id=0;
		if($("input[name='id']").val()!=""){
		   id=$("input[name='id']").val();	
		}
		$.ajaxFileUpload({
			url : '../fileupload/bankFile-'+id+'.do', // 用于文件上传的服务器端请求地址
			secureuri : false, // 一般设置为false
			fileElementId : 'bankfile', // 文件上传空间的name属性 <input type="file"
			// id="file"
			dataType : 'json', // 返回值类型 一般设置为json
			success : function(data, status) // 服务器成功响应处理函数
			{
				if (data.status == "failed") {
					alert(data.result);
				} else {
					if($("#attachment_id").val()==""){
						$("#attachment_id").val(data['id']);	
					}else{
						$("#attachment_id").val($("#attachment_id").val()+","+data['id']);	
					}
					alert("上传成功！");
					html="<div class='thisimage' data-attachment="+data.id+" style='width:100px;herght:150px;text-align: center;display:inline-block;margin:5px;'>";
					html+="<div style='background-image: url("+urlroot+data.src+");background-position: center;background-size:contain;background-repeat: no-repeat;width: 100px;height: 100px;margin-bottom:5px;'>";
					html+="</div>";
					html+="<a style='' onclick='deleteBankFile(this)'><span class='btn btn-primary'><span>移除</span></span></a>"
					html+="</div>";
					$("#imgView").append(html);
				}
				isAllow = true;
			},
			error : function(data, status, e)// 服务器响应失败处理函数
			{
				alert("文件上传失败");
				isAllow = true;
			}
		});
	}else{
		alert("文件正在上传中！");
	}
}

function deleteBankFile(obj){
	if (confirm("你确认要删除这张图片吗？"))  {  
		  var $thisimg=$(obj).parents(".thisimage");
		  var id=$thisimg.data("attachment");
			$.jsonRPC.request("userAccountService.deleteBankImage",{
				params:[id],
				success:function(result){
					if(result){
						$thisimg.remove();
					}else{
						alert("删除失败！");
					}
				}
			});
	}
	
}
function openSecurityConfirm() {
	$("#security_confirm").fadeIn(200);
	$(".orderAmount").html(toMyFixed(parseFloat($("#haveAmount").val()), 2) + " " + $(".fromCurrencyType").val());
	$(".rate").html("1 "+$(".fromCurrencyType").val() + " = " + parseFloat($("#fromRate").val()).toPrecision(5) + " " + $(".toCurrencyType").val());
	var now = new Date();
	$(".orderTimestamp").html(toDate(now.getTime()));
	
	var policy = loginUser.securityPolicy;

	switch (policy) {
	case 1:
		break;
	case 2:
		$("#security_confirm .mobile_no").html("("+loginUser.mobileDistrictNumber+")"+loginUser.mobileNo+"&nbsp;");
		
		$("#security_confirm .mobile_show").show();
		$("#security_confirm .google_secret_show").hide();
		$("#security_confirm .app_show").hide();
		break;
	case 3:
		$("#security_confirm .mobile_show").hide();
		$("#security_confirm .google_secret_show").show();
		$("#security_confirm .app_show").hide();
		break;
	case 4:
		$("#security_confirm .mobile_show").hide();
		$("#security_confirm .google_secret_show").hide();
		$("#security_confirm .app_show").show();
		break;
	} 
}

function closeSecurityConfirm() {
	$("#security_confirm").fadeOut(200);
}

function doSecurityConfirm() {
	var paymentPassword = $("#security_confirm input[name='paymentPassword']").val();
	var smsCode = $("#security_confirm input[name='smsCode']").val();
	var googleCode = $("#security_confirm input[name='googleCode']").val();
	
	if (paymentPassword.length < 6) {
		window.alert("Payment password must be at least 6 characters!");
	} else {
		var policy = loginUser.orderPolicy == -1 ? 
				loginUser.securityPolicy :
				loginUser.orderPolicy;

		switch (policy) {
		case 1:
			try {
				var result = jsonRpcClient.userUserService.getAuthorizationCodeForOrder(paymentPassword, "");
				if (result != "") {
					authorizationCode = result;
					closeSecurityConfirm();
				} else {
					window.alert("Failed to verify!");
				}
			} catch (e) {
				showException(e);
			}
			break;
		case 2:
			if (smsCode.length == 6) {
				try {
					var result = jsonRpcClient.userUserService.getAuthorizationCodeForOrder(paymentPassword, smsCode);
					if (result != "") {
						authorizationCode = result;
						closeSecurityConfirm();
					} else {
						window.alert("Failed to verify!");
					}
				} catch (e) {
					showException(e);
				}
			} else {
				window.alert("SMS verification code must be 6 characters!");
			}
			break;
		case 3:
			if (googleCode.length == 6) {m
				try {
					var result = jsonRpcClient.userUserService.getAuthorizationCodeForOrder(paymentPassword, googleCode);
					if (result != "") {
						authorizationCode = result;
						closeSecurityConfirm();
					} else {
						window.alert("Failed to verify!");
					}
				} catch (e) {
					showException(e);
				}
			} else {
				window.alert("SMS verification code must be 6 characters!");
			}
			break;
		case 4:
			break;
		}

	}
}
function doSendSMS() {
	try {
		window.alert("Failed to send SMS!");
	} catch (e) {
		showException(e);
	}
}