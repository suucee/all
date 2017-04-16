$(function(){
	$(".auto-complete-email").focus(function(){
		//邮箱自动补充功能
		$(".auto-complete-email").completer({
			separator: "@",
			source: ["fxideal.com","qq.com","163.com","sina.com","126.com","sogou.com",
			         "sohu.com","56.com","msn.com","gmail.com"]
		});
	});
});

function doAddPopup() {
		var role = $(".a_role").val();
		var email = $(".a_email").val();
		var password = $(".a_password").val();
		var password1 = $(".a_password1").val();
		var opassword = $(".o_password").val();
		var opassword1 = $(".o_password1").val();
		var show_name = $(".a_showName").val();
		var operationPassword = $(".a_operationPassword").val();
		
		if (role == "") {
			layer.tips("请选择管理员角色！",".a_role",{time:2000,tips:[2,'#56a787']});
			return;
		}  if (!/^([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\-|\_|\.]?)*[a-zA-Z0-9]+\.[a-zA-Z]{2,10}$/.test(email)){
			layer.tips("请填写正确的电子邮箱地址！",".a_email",{time:2000,tips:[2,'#56a787']});
			return;
		}  if (password.length < 6 || password.length > 20) {
			layer.tips("请输入6-20位登录密码！",".a_password",{time:2000,tips:[2,'#56a787']});
			return;
		}  if (password != password1) {
			layer.tips("确认密码与密码不一致！",".a_password1",{time:2000,tips:[2,'#56a787']});
			return;
		}  if (opassword.length < 6 || opassword.length > 20) {
			layer.tips("请输入6-20位操作密码！",".o_password",{time:2000,tips:[2,'#56a787']});
			return;
		}  if (password == opassword) {
			layer.tips("操作密码不能和登录密码一致！",".o_password",{time:2000,tips:[2,'#56a787']});
			return;
		}   if (opassword != opassword1) {
			layer.tips("确认操作密码和操作密码不一致！",".o_password1",{time:2000,tips:[2,'#56a787']});
			return;
		} if (show_name == "") {
			layer.tips("请填写显示名称！",".a_showName",{time:2000,tips:[2,'#56a787']});
			return;
		}  if (operationPassword.length < 6) {
			layer.tips("操作密码不得低于6位！",".a_operationPassword",{time:2000,tips:[2,'#56a787']});
			return;
		}
		else {
			$.jsonRPC.request('admin2AdminService.add', {
				params : [role, email, password,opassword,show_name,operationPassword],
				success : function(result) {
					if (result > 0) {
						layer.msg("添加管理员成功！",{time:1000,icon:1},function(){
							window.location.href="setting_admin_list.html";
						});
					} else {
						layer.msg("添加管理员失败！",{time:2000,icon:2});
					}
				}
			});
		}
}
