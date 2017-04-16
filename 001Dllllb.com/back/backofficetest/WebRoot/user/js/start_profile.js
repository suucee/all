var length =0;
$(document).ready(function(){
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
	$.jsonRPC.request("userService.getBank",{
		params:[],
		success:function(result){
			if (result!=null) {
				$("input[name=bank_id]").val(result.id);
				$(".bankName").val(result.bankName);
				$(".bankNo").val(result.accountNo);
				$(".cardholder_Name").val(result.accountName);//持卡人姓名
				//国家地址：countryCode countryAdress
				$("#countryAdress").val(result.countryCode);
				
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
	//检查用户状态
	$.jsonRPC.request('sessionService.checkLogined', {
		params : [],
		success : function(result) {
			$(".id").text(result.id);
			$(".email").text(result.name);
			$(".tel").text(result.tel);
			$.jsonRPC.request("userService.state",{
				params:[],
				success:function(date){
					switch (date) {
					case "UNVERIFIED":
						$(".state").html('<span class="label-pending label" >资料不全</span>（资料不全！补全资料以激活账户）');
						$(".profile_sbt").html('<a style="background:#a36000; color:white;margin:10px auto;" href="javascript:updateProfile();" class="btn btn-primary" >提交资料激活我的账户</a>');
						break;
					case "AUDITING":
						$(".state").html('<span class="label-pending label" >审核中</span>（可修改或追加资料）');
						$(".profile_sbt").html('<a style="color:white; margin:10px auto;" href="javascript:updateProfile();" class="btn   btn-primary" >激活资料审核中，更新我的资料</a>');
						break;
					case "REJECTED":
						$(".state").html('<span class="label-important label" >被拒绝</span>（可修改资料重新提交）');
						$(".profile_sbt").html('<a style="color:white; margin:10px auto;" href="javascript:updateProfile();" class="btn   btn-primary" >激活资料审核中，更新我的资料</a>');
						break;
					case "VERIFIED":
						$(".userImg").hide();
						$(".remove").attr("href","javascript:void(0);");
						$(".remove").hide();
						$(".state").html('<span class="label-success label" >账户已激活</span>（不可更改资料）');
						$(".profile_sbt").html('<a style="background:gray; color:white;margin:10px auto;cursor:text;" class="btn btn-primary" >账户已激活</a>');
						break;
				
					default:
						alert("系统错误，请稍后再试");
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
						html += '<div style="width:100px;height:150px;text-align:center;margin:5px;float:left;overflow: hidden;">'
								+ '<img border="0" style="margin:0px 0px 5px 0px;" src="/upload/'
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
		$.jsonRPC.request("userService.getBankImageList",{
		params:[],
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
	
	
	if(name==""){
		alert("姓名不能为空");
		$(".name").focus();
	}else if (ename=="") {
		alert("姓名拼音不能为空");
		$(".ename").focus();
	}else if (cardType=="") {
		alert("请选择证件类型");
		$("#cardType").focus();
	}else if (cardID=="") {
		alert("请输入证件号码");
		$("#cardID").focus();
	}else if (countryCode=="") {
		alert("请选择居住国家");
		$("#countryCode").focus();
	}else if (bankName=="") {
		alert("请输入银行名称");
		$("#bankName").focus();
	}else if (bankNo=="") {
		alert("请输入银行卡号");
		$("#bankNo").focus();
	}else if (cardholder_Name!=name) {
		alert("持卡人姓名和资料中不一致");
		$(".cardholder_Name").focus();
	}else{
		$.jsonRPC.request("userService.updateProfile",{
			params:[name,ename,cardType,cardID,countryCode,address,cname,hangye
			        ,yearsr,userPosition,bankName,bankNo,cardholder_Name
			        ,countryAdress,swiftCode,ibanCode,bankBranch,bankAddress,attachment_id],
			        success:function(result){
			        	alert(result);
			        	getUserDetail();
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
				alert('删除失败');
			}
		getImg();
	    getBankImg();
		}
    });
}

















