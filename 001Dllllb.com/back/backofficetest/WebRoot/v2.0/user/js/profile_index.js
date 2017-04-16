var length =0;
var comment = '';
var userProfileMinImageNum = -1;
$(document).ready(function(){
	$(".load_profile").load("./../admin/_profile.html",function () {
//		$("#select_mobileCode").load("./../admin/_options_countryCode.html");
		$(".userNationality").load("./../admin/_options_country.html");
		
		$(".agent_tr").hide();
		if(loginUser.vipGrade > 0 ){//代理
			$(".agent_td").show();
		}else{
			$(".agent_td").hide();
		}
		//下面两句使居住地右对齐
		var rightWrapWidth = $(".enameValueTd").width() - $(".ename").width();//获取右边文本框右间隙
		$(".address").css("width",$(".addressTd").width() - rightWrapWidth - 104);//td - 右边文本框右间隙 - 100（select） - 4  
		
		getCodeAndLink();
		getParentStuff();
		getUserProfileMinImageNum();
		getUserEmailAndName();
		getUserInfo();
		getUserDetail();
		$("#countryAdress").change(function(){
		    if($("#countryAdress").val()=="CN"){
		    	$(".home").show();
		    	$(".abroad").hide();
		    }else{
		    	$(".home").hide();
		    	$(".abroad").show();
		    }
		});
		
	});
	
});

/**
 * 获取用户最少文件上传目
 */
 function getUserProfileMinImageNum(){
	$.jsonRPC.request("adminSettingService.getList",{
		params:[],
		success:function(result){
			if(result!= null){
				for(var i in result.list ){
					if(result.list[i].key == "UserProfileMinImageNum"){
						userProfileMinImageNum = result.list[i].intValue;
						return;
					}
				}
			}
		}
	});
}



function getUserEmailAndName(){
	//获取详细信息
	$.jsonRPC.request("user2Service.getUserEmailAndName",{
		params:[],
		success:function(result){
			if (result.map.name != "") {
				$(".name").html(result.map.name);
			}
			if(result.map.mobile != ""){
//				var holeMobile = result.map.mobile;
//				$(".select_mobileCode").val(holeMobile.substring(1,holeMobile.indexOf(".")));
//				$(".tel").val(holeMobile.substring(holeMobile.indexOf(".")+1,holeMobile.length));
				$(".tel").text(result.map.mobile);
			}
			if (result.map.email != "") {
				$(".email").html(result.map.email);
//				$(".email").val(result.map.email);
			}else{
				$(".email_opear").html("您还未绑定邮箱，立即绑定邮箱?");
				$(".email_opear").attr("href","javascript:parent.openNewWindow('./../user/bind_email.html',null)");
			}
		}
	});
}


function getCodeAndLink() {
	$.jsonRPC.request("userRankService.getReferralCode", {
		params : [],
		success : function(result) {
			if (result != null && result != '') {
				$(".referralCode").html(result);
			}
		}
	});
}


function getParentStuff(){
	//获取此用户的父级员工信息，设置在左侧导航栏（注意，父级有可能是代理，所以要筛选出员工信息来）
	$.jsonRPC.request('user2Service.getParentStuffInfo', {
		params : [],
		success : function(result) {
			if (result) {
				
				var stuffInfo = result.map.profiles.userName + "  " + result.map.users.mobile + "  "
				+result.map.users.email;
				
				$(".stuffName").html(stuffInfo);
				$(".stuffName").attr("title",stuffInfo);
			}
		}
	});
}

function getUserInfo(){
	$.jsonRPC.request("user2Service.getUserInfo",{
		params:[],
		success:function(result){
			if (result!=null) {
				$(".agent_level").html(result._vipGradeName);
				if(result.disable){
					$(".disable_frozen").html('<span class="red">[禁]</span>');
				}
				if(result.frozen){
					$(".disable_frozen").html($(".disable_frozen").html()+'<span class="purple">[冻]</span>');
				}
				
			}
		}
	});
}



function getUserDetail(){
	//获取详细信息
	$.jsonRPC.request("userService.getProfiles",{
	params:[],
	success:function(result){
		if (result!=null) {
			$(".name").val(result.userName);
			$(".ename").val(result.userEName);
			//下拉选项cardType
			$("#cardType").val(result.cardType);
			$("#cardID").val(result.userIdCard);
			//下拉居住地区countryCode
			$(".userNationality").val(result.userNationality);
//			$("#countryCode").val(result.userNationality);
			$(".address").val(result.userEsidentialAddress);
			$(".cname").val(result.company);//公司名称
			$(".hangye").val(result.userIndustry);//行业
			$(".userPosition").val(result.position);//职位
			$(".yearsr").val(result.userYearsIncom);//年收入
			comment = result.userComment;
		} 
	}
});
	getImg();
}
function checkUserState(){
	//检查用户状态
	$.jsonRPC.request('sessionService.checkLogined', {
		params : [],
		success : function(result) {
			$(".id").text(result.id);
			$.jsonRPC.request("userService.state",{
				params:[],
				success:function(state){
					switch (state) {
					case "UNVERIFIED":
						$(".state").html('<span class="label-pending label" >资料不全</span>（资料不全！补全资料以激活账户）');
						$(".profile_sbt").html('<a style="background:#a36000; color:white;margin:10px auto;" href="javascript:updateProfile();" class="btn btn-primary" >提交资料激活我的账户</a>');
						break;
					case "AUDITING":
						$(".state").html('<span class="label-pending label" >审核中</span>（可修改或追加资料）');
						$(".profile_sbt").html('<a style="color:white; margin:10px auto;" href="javascript:updateProfile();" class="btn   btn-primary" >资料审核中，追加我的资料</a>');
						break;
					case "REJECTED":
						var causeHtml = ''; 
						if(comment != undefined && comment != ""){
							causeHtml = '<span class="label-important label" >被驳回</span> 驳回理由：'+comment+' （可修改资料重新提交）';
						}else{
							causeHtml = '<span class="label-important label" >被驳回</span>（可修改资料重新提交）';
						}
						$(".state").html(causeHtml);
						$(".profile_sbt").html('<a style="color:white; margin:10px auto;" href="javascript:updateProfile();" class="btn   btn-primary" >资料被驳回，重新提交资料</a>');
						break;
					case "VERIFIED":
						readOnly();//不可更改
						$(".userImg").hide();
						$(".remove").attr("href","javascript:void(0);");
						$(".remove").hide();
						$(".state").html('<span class="label-success label" >账户已激活</span>（资料已经通过审核，如要更改，请联系客服处理。）');
						$(".profile_sbt").html('<a style="background:gray; color:white;margin:10px auto;cursor:text;" class="btn btn-primary" >账户已激活</a>');
						break;
				
					default:
						layer.msg("系统错误，请稍后再试",{time:2000,icon:2});
						break;
					}
				}
			});
		}
	});
}
function getImg() {
	// 获取图片
	$.jsonRPC.request("userService.getproFileImage",{
				params : [],
			   success : function(result) {
				var html = "";
				// 获取图片
				if (result != null) {
					for ( var i in result.list) {
						imgList = result.list[i];
		   		        html+="<div style='width:250px;height:150px;text-align:center;margin:5px;float:left;overflow: hidden;'>";
						html+="<div style='background-image: url(/upload"+imgList.path+");background-position: center;background-size:auto 100%;background-repeat: no-repeat;width: 250px;height: 150px;line-height: 150px;margin-bottom:5px;'>";
						html+="<div style='width: 250px;height: 150px;line-height: 150px;'>";
						html+="<a style='' href='javascript:removeProof("+ imgList.id+");'  class='btn btn-g btn-remove'><i class='fa fa-remove' aria-hidden='true'></i>&nbsp;&nbsp;移除</a>&nbsp;&nbsp;";
						html+="<a style='' href='/upload"+imgList.path+"' target='_blank'  class='btn btn-g'><i class='fa fa-eye' aria-hidden='true'></i>&nbsp;&nbsp;查看</a>";
						html+="</div>";
						html+="</div>";
						html+="</div>";		
					                            }
				                    }
				checkUserState();
				$(".imageShow").html(html);
			}
		});
	
}


function bindEmailBackFun(){
	layer.closeAll();
	
	getUserDetail();
}


function updateProfile(){
	
//	var email = $(".email").val();
	
	var email = $(".email").html();
	if(email == ""){
		layer.confirm("您还未绑定邮箱,暂不能提交资料哦.",{btn:["前往绑定邮箱","我知道了"]},
			function(){
				parent.openNewWindow('./../user/bind_email.html',bindEmailBackFun);
				return;
			},
			function(){
				return;
			}
		);
		return;
	}
	
	var name = $(".name").val();
	var ename = $(".ename").val();
	//下拉选项cardType
	var cardType = $("#cardType").val();
	var cardID = $("#cardID").val();
	//下拉居住地区countryCode
	var userNationality = $(".userNationality").val();
	var address = $(".address").val();
	var cname = $(".cname").val();//公司名称
	var hangye = $(".hangye").val();//行业
	var userPosition = $(".userPosition").val();//职位
	var yearsr = $(".yearsr").val();//年收入
	
	if(name==""){
		layer.tips("您的姓名不能为空.",".name",{time:2000,tips:[2,'#56a787']});
		$(".name").focus();return;
	}else if (ename=="" || !(/^[a-zA-Z]+$/g).test(ename) ) {//拼音只能是字母开头和结束
		layer.tips("您的姓名拼音格式不正确.",".ename",{time:2000,tips:[2,'#56a787']});
		$(".ename").focus();return;
	}else if (cardType=="") {
		layer.tips("请您选择证件类型.","#cardType",{time:2000,tips:[2,'#56a787']});
		$("#cardType").focus();return;
	}else if (cardID=="" || !(/^[0-9a-zA-Z]+$/g).test(cardID)) {//国外身份证和中国不一样，不能一概而论
		layer.tips("请您输入正确的证件号码.","#cardID",{time:2000,tips:[2,'#56a787']});
		$("#cardID").focus();return;
	}
	//身份证明图片数目验证
	else if(userProfileMinImageNum != -1 && $(".imageShow > div").length < userProfileMinImageNum){
		layer.tips("请上传最少"+userProfileMinImageNum+"张身份证明图片，以便后台人员审核您的资料",".btn-file",{time:2000,tips:[2,'#56a787']});
		$(".btn-file").focus();return;
	}
	else if (userNationality=="") {
		layer.tips("请您选择居住国家.",".userNationality",{time:2000,tips:[2,'#56a787']});
		$(".userNationality").focus();return;
	}
	else if (address=="") {
		layer.tips("请您填写您的居住地址.",".address",{time:2000,tips:[2,'#56a787']});
		$(".address").focus();return;
	}
	else{
		$.jsonRPC.request("user2Service.updateProfile",{
			params:[name,ename,cardType,cardID,userNationality,address,cname,hangye
			        ,yearsr,userPosition],
			        success:function(result){
			        	if(result == "提交成功"){
			        		layer.msg("资料提交成功，后台将会尽快审核您的资料。",{time:2000,icon:1});
			        		moveToTop();
			        		getUserDetail();
			        	}else{
			        		layer.msg(result,{time:2000,icon:2});
			        	}
			        }
		});
	}
}

//删除图片
function removeProof(id) {
	$(".remove").attr("href","javascript:void(0);");
	$.jsonRPC.request("userService.removeImg",{
		params:[id],
		success:function(data){
			if(!data){
				layer.msg('删除失败',{time:2000,icon:2});
			}
		getImg();
		}
    });
}

function readOnly(){
	$(":input").attr("disabled","disabled");
	$("select").attr("disabled","disabled");
	$(":input").attr("title","资料已经通过审核，如要更改，请联系客服处理。");
	$(".btn-remove").hide();
	$(".btn-file").hide();
}

function canEdit(){
	
}













