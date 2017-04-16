/**
 * ——Lvbo
 *此为合规通用代码，在合规时应当引入这个文件
 */

const PAGE_SIZE = 5;
var _check_user_id = 0;
var userDetail =  null;

$(function() {
	
});



/**
 * 根据userid初始化用户信息（所有合规页面通用）
 * @param user_id
 */
function init_userinfo(user_id){
	_check_user_id = user_id;
	
//	console.log("获取到userid="+_check_user_id);
	
	userDetail = new UserDetail(_check_user_id);
	
		$(".tab-button").click(function() {
			$(".tab-button").removeClass('btn-b');
			$(".tab-button").addClass('btn-f');
			$(this).removeClass('btn-f');
			$(this).addClass('btn-b');
			$index = $(".tab-button").index($(this));
			switch($index){
			case 1:
				userDetail.getBankAccount(1);
			case 3:
				userDetail.getPageCapital(1);
			default:
				userDetail.getUserDetail();
				break;
			}
			$(".tab-content").css("display", "none");
			$(".tab-content").eq($index).show(function(){
				if($index == 1){
				}
			});
			userDetail.getUserDetail();
			
		});
}

/**
 * 用于合规审核
 * @param scheme ACCEPTED/REJECTED
 * @param jsonRPCRequestName请求方法名：xxxService.xxxx
 * @param paramsArray [_this.bankaccount_id, scheme, admin_co..]
 */
function doCheck(scheme, jsonRPCRequestName, doCheckId) {
	var admin_comment = $(".admin_comment").val();
	var user_comment = $(".user_comment").val();
	if(scheme == "REJECTED"){
		if(user_comment.trim() == ""){
			layer.tips("请填写驳回的理由，以便客户进行修改和再次提交。",".user_comment",{time:2000,tips:[3,'#56a787']});
			$(".user_comment").focus();
			return;
		}
	}
	var attachment_id = $("#attachment_id").val();
	var password = $(".password").val();
	var reminder=$("#reminder").val();
	
	if(admin_comment != "" && reminder == ""){//填写了备忘，但是没选日期
		layer.tips("您填写了备忘，但是没选提醒日期，这将导致备忘无效。","#reminder",{time:2000,tips:[3,'#56a787']});
		$("#reminder").focus();
		return;
	}
	
	if(admin_comment == "" && reminder != ""){//，选了日期，没填写备忘
		layer.tips("您选择了提醒日期，但是还未填写备忘哦。",".admin_comment",{time:2000,tips:[3,'#56a787']});
		$(".admin_comment").focus();
		return;
	}
	
	if (password == "") {
		layer.tips("操作密码不能为空!",".password",{time:2000,tips:[2,'#56a787']});
		return;
	}
	$.jsonRPC.request(jsonRPCRequestName, {
		params : [doCheckId, scheme, admin_comment, user_comment,reminder, password, attachment_id],
		success : function(result) {
			if (result) {
				layer.msg("审核成功！",{time:1000,icon:1},function(){
					parent.closeWindow();//关闭窗口
			    });
			}
		}
	});
}



function uploadFile(){
	if ($("#attachment_id").val() != ""){
		layer.tips("您已经上传文件了。（只能传1个）",".upload-tip",{time:2000,tips:[3,'#56a787']});
		return;
	}
	var id = 0;
	$.ajaxFileUpload({
		url : '../../fileupload2/cddcheck-' + id + '.do', // 用于文件上传的服务器端请求地址
		secureuri : false, // 一般设置为false
		fileElementId : 'file_upload', // 文件上传空间的name属性 <input type="file"
		// id="file"
		dataType : 'json', // 返回值类型 一般设置为json
		success : function(data, status)// 服务器成功响应处理函数
		{
			if (data.status == "failed") {
				layer.msg(data.result,{time:2000,icon:2});
			} else {
				if ($("#attachment_id").val() == "") {
					$("#attachment_id").val(data['id']);
				} else {
					$("#attachment_id").val($("#attachment_id").val() + "," + data['id']);
				}
				
				var titleFileName = data.src.substring(data.src.lastIndexOf("/")+1,data.src.length);
				var fileName = titleFileName;
				if(titleFileName.length > 25){
					var fileName = titleFileName.substring(titleFileName.length-25, titleFileName.length);
				}
				fileName = "..." + fileName;
				var trHtml = '<tr><td style="text-overflow: ellipsis; white-space: nowrap; overflow: hidden; ">'+
								'<i class="fa fa-file-text margin-right10"></i>'+
								'<span title="合规文件（存于服务器上的）：'+titleFileName+'">'+fileName+
								'</span></td></tr>';
				$("#file_upload").parents("tr").after(trHtml);
				
				layer.msg("上传成功！",{time:1000,icon:1});
			}
		},
		error : function(data, status, e)// 服务器响应失败处理函数
		{
			layer.msg("文件上传失败",{time:2000,icon:2});
		}
	});
}



/**
 * 用于合规审核
 * @param check_content_pageName 要显示审核内容的页面名。注意：合规用户时不需要载入页面，指定为null
 * @param check_userinfo_content_name 审核内容，如：银行卡，资料，出金，代为入金。
 * @param load_complate_function 加载页面完成的回调函数，多为获取数据等等或者初始化对象
 * @param accept_function 通过审核的click事件
 * @param reject_function 拒绝审核的click事件
 */
function check_init(
		check_content_pageName,
		check_userinfo_content_name,
		load_complate_function,
		accept_function,
		reject_function
		){
	$(".load_back").load("../admin/_check_back.html");//装载返回按钮
	$(".load_check_userinfo").load("../admin/_check_userinfo.html",function(){//装载用户信息页面
		if(check_content_pageName != null){//合规用户时不需要载入此页面
			$(".check_userinfo_content").load(check_content_pageName,function(){
				$(".check_userinfo_content_name").html(check_userinfo_content_name);
				load_complate_function.call();
			});
		}else{
			load_complate_function.call();
		}
		
		$(".load_check_operation").load("../admin/_check_operation.html",function(){
			//加载时间控件
			$("#reminder").datetimepicker({
				timeFormat : "HH:mm:ss",dateFormat : "yy-mm-dd"
			});
			$("#btn_doCheck_tr").delegate(".btn_accept","click",accept_function);
			$("#btn_doCheck_tr").delegate(".btn_reject","click",reject_function);
	//		$(".btn_accept").bind("click",accept_function);
	//		$(".btn_reject").bind("click",reject_function);
		});
	});
}






/**
 * 用于财务审核
 * @param check_content_pageName 要显示审核内容的页面名。注意：合规用户时不需要载入页面，指定为null
 * @param check_userinfo_content_name 审核内容，如：银行卡，资料，出金，代为入金。
 * @param load_complate_function 加载页面完成的回调函数，多为获取数据等等或者初始化对象
 * @param accept_function 通过审核的click事件
 * @param reject_function 拒绝审核的click事件
 */
function finance_init(
		check_content_pageName,
		check_userinfo_content_name,
		load_complate_function,
		accept_function,
		reject_function
		){
	$(".load_check_userinfo").load("../admin/_check_userinfo.html",function(){//装载用户信息页面
		if(check_content_pageName != null){//合规用户时不需要载入此页面
			$(".check_userinfo_content").load(check_content_pageName,function(){
				$(".check_userinfo_content_name").html(check_userinfo_content_name);
				load_complate_function.call();
			});
		}else{
			load_complate_function.call();
		}
		
		$(".load_back").load("../admin/_check_back.html");//装载返回按钮
		
		$(".load_check_operation").load("../admin/_finance_operation.html",function(){
			$("#btn_doCheck_tr").delegate(".btn_accept","click",accept_function);
			$("#btn_doCheck_tr").delegate(".btn_reject","click",reject_function);
		});
	});
}






	var UserDetail = function(_user_id) {
//		console.log("准备初始化user对象");
		this.user_id = _user_id;
		this.state = null;
		this.vip_grade = null;
		this.level = null;
		this.hasChangedInfo = false;
		this.profile_id=0;
		this.init(_user_id);
		setTimeout(function(){
			parent.localUserById(_user_id);
		}, 1000);
	};

	UserDetail.prototype = {
		init:function(user_id) {
			if(!(user_id > 0)){
				layer.alert("对不起，无法获取指定用户的基本信息。");
				return false;
			}
			
			this.getUserDetail();
			this.getBankAccount(1);
			this.getPageCapital(1);
		},
		
		getUserDetail : function() {
//			console.log("准备初始化user对象————获取用户详情");
			var _this = this;
			$.jsonRPC.request("user2Service.getUserDetail", {
				params : [_this.user_id],
				success : function(result) { 
						if (result.map.users != null) {
	//个人信息Users------------------------------------------------------------------	----------------------			
							var item = result.map.users;
							
	//级别--------------------------------------------------------------------------------------------
							$(".agent_level").html(item._vipGradeName);
							var agentName = null;
							if(item.parent != null && item.parent._name != null){
								agentName = item.parent._name +"&nbsp;&nbsp;&nbsp;&nbsp;" +
								item.parent.mobile +"&nbsp;&nbsp;&nbsp;&nbsp;" + item.parent.email;
							}else{
								agentName = "暂无";
							}
							$(".agent").html(agentName);
							if(item.vipGrade > 0){//代理
								$(".agent_guide").show();
							}
							$(".email").html(item.email);
							$(".referralCode").html(item.referralCode);
							
							$(".select_state").html(item.state);
							_this.state = item.state;
							
							var labelClassNameTip = getUserProfileLabelClassNameTip(item.state,false,false);
							$(".select_state").html('<span class="label '+labelClassNameTip[0]+'" >'+labelClassNameTip[1]+'</span>');
							
							$(".tel").html(item.mobile);
							
							if(item.disable){
								$(".disable_frozen").html('<span class="red">[禁]</span>');
							}
							if(item.frozen){
								$(".disable_frozen").html($(".disable_frozen").html()+'<span class="purple">[冻]</span>');
							}
							$(".toeditprofile").html(html);
						}
						
						
						//负责人——上级员工
						{
							if (result.map.usersParentStuffInfoMap.map != null) {
								var userName = result.map.usersParentStuffInfoMap.map.profiles.userName;
								$(".stuffName").html(userName);
							}
						}
						
	//个人资料Profile------------------------------------------------------------------	----------------------			
					{
						if (result.map.profiles != null) {
							var item = result.map.profiles;
							$(".name").html(item.userName);
							$(".legend_name").html(item.userName);
							$(".ename").html(item.userEName);
							//下拉选项cardType
							$("#cardType").html(item.cardType);
							$("#cardID").html(item.userIdCard);
							if(item.userNationality == "CN"){
								$(".userNationality").html("中国");
							}else{
								$(".userNationality").html(item.userNationality);
							}
							$(".address").html(item.userEsidentialAddress);
							$(".cname").html(item.company);
							//公司名称
							$(".hangye").html(item.userIndustry);
							//行业
							$(".userPosition").html(item.position);
							//职位
							$(".yearsr").html(item.userYearsIncom);
							//年收入
						}
					}
					
					
	//身份证------------------------------------------------------------------	----------------------					
					{
						if (result.map.attach != null) {
							var html = "";
							for (var i in result.map.attach.list) {
								var imgList = result.map.attach.list[i];
								html += "<div style='width:250px;height:150px;text-align:center;margin:5px;float:left;overflow: hidden;'>";
								html += "<div style='background-image: url(/upload" + imgList.path + ");background-position: center;background-size:auto 100%;background-repeat: no-repeat;width: 250px;height: 150px;line-height: 150px;margin-bottom:5px;'>";
								html += "<div style='width: 250px;height: 150px;line-height: 150px;'>";
								html += "&nbsp;";
								html += "<a style='' href='/upload" + imgList.path + "' target='_blank'  class='btn btn-g'><i class='fa fa-eye' aria-hidden='true'></i>&nbsp;&nbsp;查看</a>";
								html += "</div>";
								html += "</div>";
								html += "</div>";
							}
							if (html=="") {
							     html += '<tr><td colspan="10" class="bold" style="text-align:center;">暂无图片.</td></tr>';
							}
							$(".imageShow").html(html);
						}
					}
					
	//网站账户---------------------------------------------------------------------------------------				
					 {
						 //网站账户
						var item = result.map.userBalances;
						var sum=result.map.withdrawalsSum;
						var html = '<tr>';
						 html += '	<td class="sample-currency">USD</td>';
						 html += '	<td>' + toDate(item.updatedTime.time) + '</td>';
						 html +=  '	<td class="align-center">' + item.amountAvailable.toFixed(2)  + '&nbsp;&nbsp;USD</td>' ;
						 html +=  '	<td class="align-center">' + sum.toFixed(2) + '&nbsp;&nbsp;USD</td>' ;
						 html +=  '</tr>';
						$("tbody#result_list_ub").html(html);
					}
					 
	//MT4账户---------------------------------------------------------------------------------------					 
					 {
						var html = '';
						if(result.map.mt4UsersList.list == 0){//没有MT4账户
							html = "<tr><td colspan='2'>该客户还没有任何MT4账号。</td>";
							$("tbody#mt4result_list").html(html);
						}
						else{
							for (var i in result.map.mt4UsersList.list) {
								var item =result.map.mt4UsersList.list[i];
								html += '<tr>';
								html += '	<td>'+item.login+'</td>';
								html += '	<td>'+toDate(item.creatTime.time)+'</td>';
								html += '	<td style="text-align:center">'+item.balance.toFixed(2)+'</td>';
								html += ' <td style="text-align:center">'+item.group+'</td>';
//								html += ' <td style="text-align:center"></td>';
								html +=  '</tr>';
						    }
						}
						$("tbody#mt4result_list").html(html);
					}
					
	//返佣---------------------------------------------------------------------------------------		
					{
							var list = result['map']['monthRebate']['list'];
							var rebateCount = result['map']['rebateCount'];
							var html="";
							if(list.length!=0){
							for (var ii = 0; ii < list.length; ii++) {
								var obj = list[ii];
								html+= '<tr>';
								html+= '<td>' + obj['monthstr'] + '</td>';
								html+= '<td>' + obj['rebateCount'].toFixed(2)  + '</td>' ;
				                html+= '</tr>';
							}
							html+='<tr>';
							html+='<td>客户的返佣收入累计：<span class="bold"> ' + rebateCount.toFixed(2) + '（USD）</span></td>';
							html+='<td>&nbsp;</td>';
							html+='</tr>';
							}else{html += '<tr><td colspan="10" class="bold">对不起，暂无该用户的返佣统计。</td></tr>';}
							html += '<tr><td colspan="2"><span>返佣说明：此处的返佣是该客户自己的返佣收入。</span></td></tr>';
				            $('#monthrebate_list').html(html);	
					}
				}
			});
		},
		
//银行卡------------------------------------------------------------------	----------------------					
		getBankAccount : function(pageNo){
//			console.log("准备初始化user对象————获取用户银行卡");
			var _this = this;
			$.jsonRPC.request("user2Service.getBankAccount", {
				params:[_this.user_id,pageNo, PAGE_SIZE,"javascript:userDetail.getBankAccount(??);"],
				success : function(result) { 
					if (result!= null) {
							var html = '';
							for (var i in result.map.page.list.list) {
								var item = result.map.page.list.list[i];
								
								var labelClassName = getBankLabelClassName(item.state);
								var label_class = labelClassName[0];
								var label_name  = labelClassName[1];
								
								 html += '<tr>';
								 html += '<td style="text-align: center;"><span class="label ' + label_class + '">' + label_name + '</span></td>'; 
								 html += '<td style="text-align: center;">'+item.bankName + '</td>';
								 html += '<td style="text-align: center;">' + item.accountName + '</td>' ;
								 html += '<td style="text-align: center;">' + item.accountNo + '</td>' ;
								 html += '<td style="text-align: center;">' + item.countryCode + '</td>' ;
//								 html += '<td style="text-align: center;">'+''+'</td>' ;
								 html += '</tr>';
							}
							if(result.map.page.buttons!=null&&result.map.page.buttons!=""){
						    	html += '<tr><td colspan="6"><div class="pagelist">'+result.map.page.buttons+'</td></tr>';
						    }
							if (html=="") {
							     html += '<tr><td colspan="6">用户还未添加银行卡</td></tr>';
							}
							$("#bankAccountList").html(html);
						}
				}
			});
		},
		
		
		/**
		 * 获取所有的出金入金状况
		 * @param pageNo 页数
		 */
		getPageCapital:function(pageNo){
//			console.log("准备初始化user对象————获取用户资金");
			var _this = this;
			$.jsonRPC.request("user2Service.getPageCapital",{
				params:[_this.user_id,pageNo, PAGE_SIZE,"javascript:userDetail.getPageCapital(??);"],
				success:function(result){
					if (result != null) {
						_pageNo = pageNo;
						var html = '';
						//待处理事项数目
						var capitalWaitingCount = 0;var WAITING_Count  = 0;var AUDITED_Count = 0;var PENDING_SUPERVISOR_Count = 0;
						for (var i in result.list.list) {
							var item = result.list.list[i].map;
							var label_class = "";
							var label_name = "";
							
							var class_name = getLabelClassName(item.state,item.type);
							label_class = class_name[0];
							label_name = class_name[1];
							
							var typeHtml = '';
							var moneyHtml = '';
							if(item.type == "withdrawals"){
								moneyHtml = '<span class="font-red">'+item.amount.toMoney()+'</span>';
								typeHtml = '<span class="label label-red">出金</span>';
							}else if(item.type == "deposits"){
								moneyHtml = '<span class="font-green">'+item.amount.toMoney()+'</span>';
								typeHtml = '<span class="label label-success">入金</span>';
							}
							
							html += '<tr>'
								+ '<td>'+typeHtml+'</td>' 
								+ '<td>'+moneyHtml+'</td>'
								+ '<td><span class="label '+label_class+'">'+label_name+'</span></td>'
								+ '<td>'+toDate(item.create_time.time)+'</td>'
								+ '<td>'+(item.deal_time == null ? ' -暂未处理- ' : toDate(item.deal_time.time))+'</td>'
//								+ '<td></td>'
								+ '</tr>';
					    }
						
						if(result.buttons!=null&&result.buttons!=""){
					    	html += '<tr><td colspan="6"><div class="pagelist">'+result.buttons+'</td></tr>';
					    }
						if (html=="") {
						     html += '<tr><td colspan="6" class="bold">暂未检索到相关的出金记录</td></tr>';
						}
						$("tbody#result_list_capital").html(html);
					}	
				}
			});
		}
	};


function donothing(){
	
}
function refreshDepositsList(){
	if(userDetail != null){
		userDetail.getPageCapital(1);
	}
}


function updateList(){
	if(userDetail != null){
		userDetail.getUserDetail(1);
	}
}



function getImg(user_id) {
	// 获取图片
	$.jsonRPC.request("adminUserService.getproFileImage", {
		params : [user_id],
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
			userDetail.getUserDetail();
			$(".imageShow").html(html);
		}
	});
}


/**
 * 从审核记录过来才需要传递cddcheckid
 * @param cddCheckId 合规记录的id
 */
function getCddCheckComment(cddCheckId){
	if(getCddCheckComment === undefined || getCddCheckComment == null ){
		return;
	}
	if(cddCheckId!=0){
		$.jsonRPC.request("admin2CheckService.getOneCddCheck", {
			params : [cddCheckId],
			success : function(result) {
				$(".admin_comment").val(result.comment);
			}
		});
	}
}
