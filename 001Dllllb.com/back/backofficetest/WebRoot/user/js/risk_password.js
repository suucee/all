var resetPass = null;
$(function() {
	resetPass = new resetPassword();
	$("#scheme").change(function() {
	     resetPass.initView();	
	});
	$("#go").click(function() {
	     resetPass.initView();	
	});
	$("#checkedAll").click(function() {
	     resetPass.checkedAll();	
	});
	$("#resetpassword").click(function() {
	      popupProject.open();	
	});
});

var resetPassword = function() {
	this.popupWindow;
	this.init();
};
resetPassword.prototype = {
	init : function() {
		var _this=this;
		this.initGroup();
		$.get("_popup_risk_reset_password.html",function(data){
				_this.popupWindow=data;
			});
	},
	initGroup : function() {
		var _this = this;
		$.jsonRPC.request("adminRiskService.getGroupList", {
			params : [],
			success : function(result) {
				var list = result.list;
				var html = "";
				for (var i = 0; i < list.length; i++) {
					var item = list[i];
					html += "<option value='" + item + "'>" + item + "</option>";
				}
				$("#scheme").html(html);
				_this.initView();
			}
		});
	},
	initView : function() {
		$.jsonRPC.request("adminRiskService.getAll", {
			params : [$("#scheme").val(), $("#keyword").val()],
			success : function(result) {
				var list = result.list;
				var html = "";
				for (var i = 0; i < list.length; i++) {
					var item = list[i];
					html+="<tr>";
					html+="<td><input class='checkthis' type='checkBox' data-login='"+item.login+"'/></td>";
					html+="<td>"+item.login+"</td>";
					html+="<td>"+item.groupName+"</td>";
					html+="<td><a href='user_detail.html?id="+item.userId+"'>"+(item.name==null?"--":item.name)+"</a></td>";
					html+="<td class='amount'>"+item.balance+"</td>";
					html+="<td>"+item.credit+"</td>";
					html+="<td>"+(item.createTime==null?"--":toDate(item.createTime.time))+"</td>";
					
					html+="</tr>";
				}
				$("#result_list").html(html);
			}
		});
	},
	checkedAll:function(){
		if($("#checkedAll").is(':checked')){
			$(".checkthis").prop("checked", true);
		}else{
			$(".checkthis").prop("checked", false);
		}
	}
};

var popupProject = {
		callbackFunc : null,
		idArray:null,
		init : function() {
			popupProject.idArray=new Array();
			var $popup=$(resetPass.popupWindow).clone();
			$("#popup_container").html($popup);
			$(".checkthis").each(function(index,element) {
			   if($(element).is(':checked')){
			   	  popupProject.idArray.push($(element).data("login"));
			   }
			});
		},
		open : function() {
			this.init();
			$('.popup_dialog').fadeIn(200);

		},
		close : function() {
			$('.popup_dialog').fadeOut(200);
		},
		submit : function() {
			var _this=this;
			var password=$(".password").val();
			var passwordInvestor=$(".passwordInvestor").val();
			var dopassword=$(".dopassword").val();
			if(popupProject.idArray==null||popupProject.idArray.length==0){
				alert("你没有选择任何用户");
				return;
			}
			if(password.length==0){
				alert("密码不能为空");
				return;
			}
			if(password.length<6){
				alert("密码不能小于6位");
				return;
			}
			if(passwordInvestor.length==0){
				alert("投资人密码不能为空");
				return;
			}
			if(passwordInvestor.length<6){
				alert("投资人密码不能小于6位");
				return;
			}
			$.jsonRPC.request("adminRiskService.resetMT4Password", {
				params : [popupProject.idArray,password,passwordInvestor,dopassword],
				success : function(result) {
					alert("操作成功！");
					popupProject.close();
				}
			});
		}
	};
