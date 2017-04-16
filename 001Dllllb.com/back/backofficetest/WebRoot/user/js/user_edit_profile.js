var _userId = 0;
$(document).ready(function(){
	_userId = getParam("userId", 0);
	

	if (loginUser.role == 'OperationsManager') {
		$("[name='email']").removeAttr("disabled");
		$("[name='mobile']").removeAttr("disabled");
		$("[name='upId']").removeAttr("disabled");

		$("#btn_modify_account").show();
		$("#btn_set_agent").show();
		$("#btn_set_staff").show();
		$("#btn_hold_user").show();
	}
	
	$("#btn_modify_account").click(function(){
		var email = $("[name='email']").val();
		var mobile = $("[name='mobile']").val();
		var upId = $("[name='upId']").val();
		if (upId == null) {upId = 0;}
		
		$.jsonRPC.request("adminUserService.modifyAccount", {
			params : [_userId, email, mobile, upId],
			success : function(result) {
				getUserDetail();
				window.alert("更新账号成功！");
			}
		});
	});
	$("#btn_restpassword").click(function(){
		if (window.confirm("确定重置这个用户的网站登录密码和支付密码吗？")) {
			$.jsonRPC.request("adminUserService.resetUserPassword", {
				params : [_userId],
				success : function(result) {
					window.alert("密码重置成功！新登录密码和支付密码已经邮件发给客户");
				}
			});
		}
	});
	$("#btn_hold_user").click(function(){
		$.jsonRPC.request('adminUserService.holdUser', {
			params : [_userId],
			success : function(result) {
				if (result != null) {
					//Success
					locStorage("loginUser", result);

					setCookie('TOKEN', result.token, 30 * 24);
					window.open('./start_user.html', '_self');
				} else {
					//Failed
					locStorage("loginUser", null);
					
					setCookie('TOKEN', null, -1);
					window.alert("代管登录失败，请检查账号和密码!");
				} 
			}
		});
	});
	
	
	$("#btn_set_agent").click(function(){

		$.get("_popup_agent.html", function(data){
			$("#popup_container").html(data);
			popupAgent.open(_userId, function(){getUserDetail();});
		});
	});
	$("#btn_set_staff").click(function(){
		$.get("_popup_staff.html", function(data){
			$("#popup_container").html(data);
			popupStaff.open(_userId, function(){getUserDetail();});
		});
	});
	
	$("#countryAdress").change(function(){
	    if($("#countryAdress").val()=="CN"){
	    	$(".home").show();
	    	$(".abroad").hide();
	    }else{
	    	$(".home").hide();
	    	$(".abroad").show();
	    }
	});
	
	getUserDetail();
	getMT4UserList();
});

function getMT4UserList() {
	$.jsonRPC.request("adminUserService.getMT4UserList", {
		params : [_userId],
		success : function(result) {
			if (result) {
				var html = '';
				
				for (var i in result.list) {
					var item = result.list[i];
					html += '<tr>'
						+ '<td>'+item.login+(item.enable ? '' : '<span class="red">(禁)</span>')+(item.enableReadOnly ? '<span class="purple">(冻)</span>' : '')+'</td>'
						+ '<td style="text-align:right;">'+item.balance.toFixed(2)+(item.credit == 0 ? '' : '<span class="blue">+信用'+item.credit.toFixed(2)+'</span>')+'</td>'
						+ '<td>'+item.password+'</td>'
						+ '<td>'+item.passwordInvestor+'</td>'
						+ '<td>'+toDate(item.regdate * 1000)+'</td>'
						+ '<td>'+(item.lastTradeSyncTime != null ? toDate(item.lastTradeSyncTime.time) : '-')+'</td>'
						+ '<td><button class="btn btn-primary" onclick="javascript:resetMT4UserPassword('+item.login+');">重置密码</button></td>'
						+ '</tr>';
				}
				
				html += '<tr><td colspan="7" style="text-align:center;"><button class="btn btn-primary" id="btn_update_mt4_users">立即更新</button>';
				html += '&nbsp;&nbsp;&nbsp;&nbsp;<button class="btn btn-primary" id="btn_new_mt4_user">新增设一个MT4账号</button></td></tr>';
				
				$("#mt4_user_result_list").html(html);
				$("#btn_update_mt4_users").click(function(){
					$("#btn_update_mt4_users").attr("disabled", "disabled");
					$("#btn_update_mt4_users").removeClass("btn-primary");
					
					$.jsonRPC.request('adminUserService.updateMT4Users', {
						params : [_userId],
						success : function(result) {
							$("#btn_update_mt4_users").removeAttr("disabled");
							$("#btn_update_mt4_users").addClass("btn-primary");
							window.alert("更新成功！");
							getMT4UserList();
						}
					});
				});
				$("#btn_new_mt4_user").click(function(){
					if (window.confirm("确定要新开设一个MT4帐号给这个用户吗？")) {
						$("#btn_new_mt4_user").attr("disabled", "disabled");
						$("#btn_new_mt4_user").removeClass("btn-primary");
						
						$.jsonRPC.request('adminUserService.addMT4User', {
							params : [_userId],
							success : function(result) {
								$("#btn_new_mt4_user").removeAttr("disabled");
								$("#btn_new_mt4_user").addClass("btn-primary");
								window.alert("添加成功！");
								getMT4UserList();
							}
						});
					}
				});
			}
		}
	});
}

function resetMT4UserPassword(login) {
	if (window.confirm("确认要重置"+login+"的密码吗？客户用原密码将无法登录MT4！")) {
		$.jsonRPC.request('adminUserService.resetMT4UserPassword', {
			params : [login],
			success : function(result) {
				getMT4UserList();
				window.alert("重置成功，邮件已发送给客户！");
			}
		});
	}
}
var isAllow=true;
function uploadBankFile() {
	if(isAllow){
		isAllow=false;
		var id=0;
		if($("input[name='bank_id']").val()!=""){
		   id=$("input[name='bank_id']").val();	
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
					if($("#bank_attachment_id").val()==""){
						$("#bank_attachment_id").val(data['id']);	
					}else{
						$("#bank_attachment_id").val($("#bank_attachment_id").val()+","+data['id']);	
					}
					alert("上传成功！");
					
					html= '<div style="width:100px;height:150px;text-align:center;margin:5px;float:left;overflow: hidden;">'
								+ '<img border="0" style="margin:0px 0px 5px 0px;" src="/upload/'
								+ data.src
								+ '" height="100px" />'
								+ '<a class="remove" href="javascript:removeProof('
								+ data.id
								+ ');"><span class="btn btn-primary"><span>移除</span></span></a>'
								+ '</div>';
					$(".bankImageShow").append(html);
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

function getUserDetail(){
	//获取详细信息
	$.jsonRPC.request("adminUserService.getProfile",{
		params:[_userId],
		success:function(result){
			if (result!=null) {
				$(".name").val(result.userName);
				$(".ename").val(result.userEName);
				//下拉选项cardType
				$("#cardType").val(result.cardType);
				$("#cardID").val(result.userIdCard);
				//下拉居住地区countryCode
				$("#countryCode").val(result.userNationality);
				$(".address").val(result.userEsidentialAddress);
				$(".cname").val(result.company);//公司名称
				$(".hangye").val(result.userIndustry);//行业
				$(".userPosition").val(result.position);//职位
				$(".yearsr").val(result.userYearsIncom);//年收入
			} 
		}
	});
	
	//获取绑定银行卡信息
	$.jsonRPC.request("adminUserService.getBank",{
		params:[_userId],
		success:function(result){
			if (result!=null) {
				$(".bankName").val(result.bankName);
				$(".bankNo").val(result.accountNo);
				$(".cardholder_Name").val(result.accountName);//持卡人姓名
				//国家地址：countryCode countryAdress
				$("#countryAdress").val(result.countryCode);
				
				$("input[name='swiftCode']").val(result.swiftCode);
				$("input[name='ibanCode']").val(result.ibanCode);
				$("input[name='bankBranch']").val(result.bankBranch);
				$("input[name='bankAddress']").val(result.bankAddress);	
				
				
				$("input[name='bank_id']").val(result.id);
				$("input[name='swiftCode']").val(result.swiftCode);
				$("input[name='ibanCode']").val(result.ibanCode);
				$("input[name='bankBranch']").val(result.bankBranch);
				$("input[name='bankAddress']").val(result.bankAddress);	
			}
		}
	});
	
	
	getImg();
    getBankImg();
}
function checkUserState(){
	$.jsonRPC.request('adminUserService.getOne', {
		params : [_userId],
		success : function(result) {
			if (result) {
				$(".id").html(result.id);
				$("[name='userId']").val(result.id);
				$("[name='email']").val(result.email);
				$("[name='mobile']").val(result.mobile);

				$(".vipGrade").html(result._vipGradeName);
				switch (result.level) {
				case 1:
					$(".staff").html('公司');
					break;
				case 2:
					$(".staff").html('经理');
					break;
				case 3:
					$(".staff").html('员工');
					break;
				default:
					$(".staff").html('无');
				}
				
				if (result.disable) {
					$(".id").html(result.id + ' <span class="red">已禁用</span>');
					$(".btn").hide();
					$("input, select").attr("disabled", "disabled");
					window.setTimeout('$(".btn").hide();', 2000);
				}
				
				switch (result.state) {
				case "UNVERIFIED":
					$(".state").html('<span class="label-pending label" >资料不全</span>（资料不全！补全资料以激活账户）');
					$(".profile_sbt").html('<a style="background:#a36000; color:white;margin:10px auto;" href="javascript:updateProfile();" class="btn btn-primary" >更新用户资料</a>');
					break;
				case "AUDITING":
					$(".state").html('<span class="label-pending label" >审核中</span>（可修改或追加资料）');
					$(".profile_sbt").html('<a style="color:white; margin:10px auto;" href="javascript:updateProfile();" class="btn   btn-primary" >激活资料审核中，更新用户资料</a>');
					break;
				case "REJECTED":
					$(".state").html('<span class="label-important label" >被拒绝</span>（可修改资料重新提交）');
					$(".profile_sbt").html('<a style="color:white; margin:10px auto;" href="javascript:updateProfile();" class="btn   btn-primary" >激活资料审核中，更新用户资料</a>');
					break;
				case "VERIFIED":
					$(".state").html('<span class="label-success label" >账户已激活</span>');
					$(".profile_sbt").html('<a style="color:white; margin:10px auto;" href="javascript:updateProfile();" class="btn   btn-primary" >帐户已激活，更新用户资料</a>');
					break;

				default:
					alert("系统错误，请稍后再试");
					break;
				}
				
				$.jsonRPC.request('adminUserService.getList', {
					params : [],
					success : function(result2) {
						if (result2) {
							var html = '';
							for (var i in result2.list) {
								var item = result2.list[i].map;
								//滤掉自己和自己的客户
								if (item.path.indexOf(result.path) == 0) {
									continue;
								}
								else {
									var spaces = '';
									for (var k=0;k<item.level - 1;k++) {
										spaces += '　';
									}
									html += '<option value="'+item.id+'">'+spaces+'ID:'+item.id+' - '+item.name+' - ' +item.mobile+ ' - ' + item.email +'</option>';
								}
							}
							
							$("[name='upId']").html(html);
							$("[name='upId']").val(result._up_id);
						}
					}
				});
			}
			
		}
	});
}

function getImg() {
	// 获取图片
	$.jsonRPC.request("adminUserService.getproFileImage", {
		params : [_userId],
		success : function(result) {
			var html = "";
			// 获取图片
			if (result != null) {
				for ( var i in result.list) {
					imgList = result.list[i];
					html += '<div style="width:100px;height:150px;text-align:center;margin:5px;float:left;overflow: hidden;">'
							+ '<img border="0" style="margin:0px 0px 5px 0px;" src="/upload'
							+ imgList.path
							+ '" height="100px" />'
							+ '<a class="remove" href="javascript:removeProof('
							+ imgList.id
							+ ');"><span class="btn btn-primary"><span>移除</span></span></a>'
							+ '</div>';
				}
			}
			
			checkUserState();
			$(".imageShow").html(html);
		}
	});
}
function getBankImg() {
		$.jsonRPC.request("adminUserService.getBankImageList",{
		params:[_userId],
		success:function(result){
				if(result==null){
					return;
				}
			var list=result.list;
			var html="";
			for(var i=0;i<list.length;i++){
				var item=list[i];
			html += '<div style="width:100px;height:150px;text-align:center;margin:5px;float:left;overflow: hidden;">'
				+ '<img border="0" style="margin:0px 0px 5px 0px;" src="/upload/'
				+ item.path
				+ '" height="100px" />'
				+ '<a class="remove" href="javascript:removeProof('
				+ item.id
				+ ');"><span class="btn btn-primary"><span>移除</span></span></a>'
				+ '</div>';
			}
			$(".bankImageShow").html(html);
		}
	});
}

function updateProfile(){
	var name = $(".name").val();
	var ename = $(".ename").val();
	//下拉选项cardType
	var cardType = $("#cardType").val();
	var cardID = $("#cardID").val();
	//下拉居住地区countryCode
	var countryCode = $("#countryCode").val();
	var address = $(".address").val();
	var cname = $(".cname").val();//公司名称
	var hangye = $(".hangye").val();//行业
	var userPosition = $(".userPosition").val();//职位
	var yearsr = $(".yearsr").val();//年收入
	var bankName = $(".bankName").val();
	var bankNo = $(".bankNo").val();
	var cardholder_Name = $(".cardholder_Name").val();//持卡人姓名
	//国家地址：countryCode countryAdress
	var countryAdress = $("#countryAdress").val();
	
	var swiftCode = $("input[name='swiftCode']").val();
	var ibanCode = $("input[name='ibanCode']").val();
	var bankBranch = $("input[name='bankBranch']").val();
	var bankAddress = $("input[name='bankAddress']").val();
	var attachment_id = $("input[name='bank_attachment_id']").val();
	
	if (name==""){
		alert("姓名不能为空");
		$(".name").focus();
	} else if (ename=="") {
		alert("姓名拼音不能为空");
		$(".ename").focus();
	} else if (cardType=="") {
		alert("请选择证件类型");
		$("#cardType").focus();
	} else if (cardID=="") {
		alert("请输入证件号码");
		$("#cardID").focus();
	} else if (countryCode=="") {
		alert("请选择居住国家");
		$("#countryCode").focus();
	} else if (bankName=="") {
		alert("请输入银行名称");
		$("#bankName").focus();
	} else if (bankNo=="") {
		alert("请输入银行卡号");
		$("#bankNo").focus();
	} else if (cardholder_Name!=name) {
		alert("持卡人姓名和资料中不一致");
		$(".cardholder_Name").focus();
	} else{
		$.jsonRPC.request("adminUserService.updateProfile",{
			params:[_userId, name,ename,cardType,cardID,countryCode,address,cname,hangye
			        ,yearsr,userPosition,bankName,bankNo,cardholder_Name
			        ,countryAdress,swiftCode,ibanCode,bankBranch,bankAddress,attachment_id],
			success : function(result){
				if (result) {
					window.alert("资料更新成功！");
				} else {
					window.alert("资料更新失败！");
				}
				//getUserDetail();
			}
		});
	}
}

//删除图片
function removeProof(id) {
	$(".remove").attr("href","javascript:void(0);");
	$.jsonRPC.request("adminUserService.removeImg",{
		params:[id],
		success:function(data){
			if(!data){
				alert('删除失败');
			}
			getImg();
			getBankImg();
		}
    });
}

function ajaxUserProfileUpload() {
	var imgUpload=$("#img_upload").val();
	if (imgUpload==""){
		alert("请选择图片");
		return;
	}
	$(".imgStartUpload").text("上传中..");
	$(".imgStartUpload").attr("href","javascript:void(0);");
	
	$.jsonRPC.request("adminUserService.getproFileImage",{
		params:[_userId],
		success:function(result){
			if(result!=null&&result.list.length>=7){
				alert("上传图片已达上限");
				$(".imgStartUpload").text("上传");
				$(".imgStartUpload").attr("href","javascript:ajaxUserProfileUpload();");
			} else {
				
				$.ajaxFileUpload({
					url : '../fileupload/userProfile-'+_userId+'.do', // 用于文件上传的服务器端请求地址
					secureuri : false, // 一般设置为false
					fileElementId : 'img_upload', // 文件上传空间的id属性 <input type="file"
					// id="file"
					dataType : 'HTML', // 返回值类型 一般设置为json
					success : function(data, status) // 服务器成功响应处理函数
					{
						$(".imgStartUpload").text("上传");
						$(".imgStartUpload").attr("href","javascript:ajaxUserProfileUpload();");
						if (data) {
							alert("图片上传成功");
							getImg();
						} else {
							alert("图片上传失败");
						}
						if (typeof (data.error) != 'undefined') {
							if (data.error != '') {
								alert(data.error);
							} else {
								alert(data.msg);
							}
						}
					},
					error : function(data, status, e)// 服务器响应失败处理函数
					{
						alert(e);
					}
				});
				
				return false;
			}
		}
	});
}
