$(document).ready(function() {
   bal=new bankAccountList(1);
});
var bankAccountList=function(_page){
	this.pageNo=_page;
	this.isAllowOpenPopup=false;
	this.popupWindow="";
	this.isAllow=true;
	this.userBankAccountId=0;
	this.urlroot="/upload";
	this.init();
};
bankAccountList.prototype={	
	   init:function(){
		    var _this=this;
			$(".scheme").change(function(){
				_this.getData(_this.pageNo);
			});
			$("#go").click(function(){
				_this.getData(_this.pageNo);
			});
			_this.getData(_this.pageNo);
			$.get("_popup_check_user_bank_account.html",function(data){
				_this.popupWindow=data;
			});
	   },	
	   getData:function (_pageNo) {
		    var _this=this;
			var pageSize=10;
			var urlFormat="javascript:bal.getData(??)";
			var scheme=$(".scheme").val();
			var keyword=$(".keyword").val();				
			$.jsonRPC.request("adminCheckBankAccountService.getPage",{
								params : [_pageNo,pageSize,urlFormat,scheme, keyword],
								success : function(result) {
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
				var dohtml="<button onclick='popupProject.open("+obj.userBankAccountId+")' class='btn btn-medium btn-primary' type='button'>审核</button>";
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
				+ '	<td><a href="user_detail.html?id='+obj.userId+'" target="_blank">' + (obj.name==null?"--":obj.name) + '</a></td>' 
				+ '	<td><span  class="label '+label_class +'" >' + label_name+ '</span></td>' 
				+ '	<td>' + (obj.bankName==null?"--":obj.bankName) + '</td>'
				+ '	<td>' + (obj.accountName==null?"--":obj.accountName)+ '</td>'
				+ '	<td>' + (obj.accountNo==null?"--":obj.accountNo) + '</td>'
				+ '	<td>' + (obj.countryCode==null?"--":obj.countryCode) + '</td>'
				+ '	<td>' + (obj.updateTime==null?"--":toDate(obj.updateTime.time)) + '</td>'
				+ '	<td>'+dohtml+'</td><tr>';
			}
			html += '<tr><td colspan="10"><div class="pagelist">' + result.buttons + '</td></tr>';
			$('#result_list').html(html);
        },
        uploadCheckFile:function() {
        	var _this=this; 
        	if(_this.isAllow){
        		_this.isAllow=false;
        		var id=0;
        		if($("input[name='id']").val()!=""){
        		   id=$("input[name='id']").val();	
        		}
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
        					_this.isAllow = false;
        				} else {
        					if($("#attachment_id").val()==""){
        						$("#attachment_id").val(data['id']);	
        					}else{
        						$("#attachment_id").val($("#attachment_id").val()+","+data['id']);	
        					}
        					alert("上传成功！");
        				}
        				_this.isAllow = true;
        			},
        			error : function(data, status, e)// 服务器响应失败处理函数
        			{
        				alert("文件上传失败");
        				_this.isAllow = true;
        			}
        		});
        	}else{
        		alert("文件正在上传中！");
        	}
        }
};


var popupProject = {
		callbackFunc : null,
		userBankAccountId : 0,
		init : function() {
			var $popup=$(bal.popupWindow).clone();
			$("#popup_container").html($popup);
		},
		open : function(_id) {
			this.userBankAccountId=_id;
			this.init();
			userBankAccountId=_id;
			$.jsonRPC.request("adminCheckBankAccountService.getOne",{
				params : [userBankAccountId],
				success : function(result) {
					var userProfile=result.map.userProfiles;
					var userBankAccount=result.map.userBankAccount;
					var userImg=result.map.userImg;
					var bankImg=result.map.bankImg;
					switch (result.map.user_state) {
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
					var notInput="未填写";
					$(".bank_check .email").text(result.map.email);
					$(".bank_check .tel").text(result.map.mobile);
					if(userProfile!=null){
						$(".bank_check .name").text(nullcheck(userProfile['userName']));
						$(".bank_check .ename").text(nullcheck(userProfile['userEName']));
						//下拉选项cardType
						$(".bank_check #cardType").text(nullcheck(userProfile['cardType']));
						$(".bank_check #cardID").text(nullcheck(userProfile['userIdCard']));
						//下拉居住地区countryCode
						$(".bank_check .countryCode").text(nullcheck(userProfile['userNationality']));
						$(".bank_check .address").text(nullcheck(userProfile['userEsidentialAddress']));
						$(".bank_check .cname").text(nullcheck(userProfile['company']));//公司名称
						$(".bank_check .hangye").text(nullcheck(userProfile['userIndustry']));//行业
						$(".bank_check .userPosition").text(nullcheck(userProfile['position']));//职位
						$(".bank_check .yearsr").text(nullcheck(userProfile['userYearsIncom']));//年收入
						
						$(".bank_check .user_comment").text(nullcheck(userProfile['comment']));
					}else{
						$(".bank_check .name").text(notInput);
						$(".bank_check .ename").text(notInput);
						//下拉选项cardType
						$(".bank_check #cardType").text(notInput);
						$(".bank_check #cardID").text(notInput);
						//下拉居住地区countryCode
						$(".bank_check .countryCode").text(notInput);
						$(".bank_check .address").text(notInput);
						$(".bank_check .cname").text(notInput);//公司名称
						$(".bank_check .hangye").text(notInput);//行业
						$(".bank_check .userPosition").text(notInput);//职位
						$(".bank_check .yearsr").text(notInput);//年收入
						
						$(".bank_check .user_comment").text(notInput);
					}
					$(".bank_check .bankName").text(nullcheck(userBankAccount['bankName']));
					$(".bank_check .bankNo").text(nullcheck(userBankAccount['accountNo']));
					$(".bank_check .cardholder_Name").text(nullcheck(userBankAccount['accountName']));
					$(".bank_check .countryCode").text(nullcheck(userBankAccount['countryCode']));
					$(".bank_check .bankBranch").text(nullcheck(userBankAccount['bankBranch']));
					$(".bank_check .bankAddress").text(nullcheck(userBankAccount['bankAddress']));
					$(".bank_check .swiftCode").text(nullcheck(userBankAccount['swiftCode']));
					$(".bank_check .ibanCode").text(nullcheck(userBankAccount['ibanCode']));
					if(userBankAccount['state']==""||userBankAccount['state']==null){
					    $(".bank_check .changeState").val("");
					}else{
						$(".bank_check .changeState").val(userBankAccount['state']);
					}
                    if(userImg!=null&&userImg.list.length>0){
						var html="";
						for(var i=0;i<userImg.list.length;i++){
							var item=userImg.list[i];
							html+="<a href='"+bal.urlroot+item.path+"' target='_blank'><div style='background-image: url("+bal.urlroot+item.path+");background-position: center;background-size:contain;background-repeat: no-repeat;width: 100px;height: 100px;margin-bottom:5px;display:inline-block;'>";
							html+="</div></a>";
						}
						$("#proof_list").html(html);
					}else{
						$("#proof_list").html("暂无图片");
					}
					if(bankImg!=null&&bankImg.list.length>0){
						var html="";
						for(var i=0;i<bankImg.list.length;i++){
							var item=bankImg.list[i];
							html+="<a href='"+bal.urlroot+item.path+"' target='_blank'><div style='background-image: url("+bal.urlroot+item.path+");background-position: center;background-size:contain;background-repeat: no-repeat;width: 100px;height: 100px;margin-bottom:5px;display:inline-block;'>";
							html+="</div></a>";
						}
						$("#bank_list").html(html);
					}else{
						$("#bank_list").html("暂无图片");
					}
					
					
					switch (userBankAccount['state']) {
					case "WAITING":
						$(".bankOldState").html('<span class="label-pending label" >待审核</span>');
						break;
					case "AUDITED":
						$(".bankOldState").html('<span class="label-pending label" >通过审核</span>');
						$(".bank_check .audited").show();
						break;
					case "REJECTED":
						$(".bankOldState").html('<span class="label-important label" >驳回审核</span>');
						$(".bank_check .rejected").show();
						break;
					default:
						$(".bankOldState").text("未知");
						break;
					}

				    if(userBankAccount['countryCode']=="CN"||userBankAccount['countryCode']==""){
				    	$(".home").show();
				    	$(".abroad").hide();
				    }else {
				    	$(".home").hide();
				    	$(".abroad").show();
				    }

				}
			});
			$('.popup_dialog').fadeIn(200);

		},
		close : function() {
			$('.popup_dialog').fadeOut(200);
		},
		submit : function() {
			var _this=this;
			var comment = $(".comment").val();
			var user_comment = $(".user_comment").val();
			var remindertime=$(".remindertime").val();
			var tag=$(".tag").val();
			var dopassword=$(".dopassword").val();
			var a_id = $("#attachment_id").val();
			var state=$(".changeState").val();
			$.jsonRPC.request("adminCheckBankAccountService.doCheck", {
				params : [_this.userBankAccountId, state, comment,user_comment,remindertime,tag,dopassword,a_id],
				success : function(result) {
					bal.getData(bal.pageNo);
					alert("操作成功！");
					popupProject.close();
				}
			});
		}
	};



function nullcheck(obj){
	if(obj==null||obj==""){
		return "未填写";
	}else{
		return obj;
	}
}
