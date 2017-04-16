var emailpattern = /^([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\-|\_|\.]?)*[a-zA-Z0-9]+\.[a-zA-Z]{2,10}$/;
var numpattern=/^[0-9]+$/;
var telpattern=/^((\+)|[0-9])+$/;

$(function(){
	$(".add_staff").click(function(){
		addStaff();
	});
});

function addStaff(){
	var u_name=$(".u_name").val();
	var countryCode = $("#countryCode").val();
	var u_mobile=$(".u_mobile").val();
	var u_password=$(".u_password").val();
	var u_password1=$(".u_password1").val();
	
	if(u_name==""){
		layer.tips("对不起，名称不能为空。",".u_name",{time:2000,tips: [2,'#56a787']});
		return;
	}
	if(u_mobile==""){
		layer.tips("对不起，电话号码不能为空。",".u_mobile",{time:2000,tips: [2,'#56a787']});
		return;
	}
	if (!/^[0-9]{8,11}$/.test(u_mobile)){
		layer.tips("对不起，电话号码格式不正确。",".u_mobile",{time:2000,tips: [2,'#56a787']});
		return;
	}
	if(u_password.length<6){
		layer.tips("对不起，登录密码至少6位。",".u_password",{time:2000,tips: [2,'#56a787']});
		return;
	}
	if(u_password!=u_password1){
		layer.tips("对不起，密码与确认密码不一致。",".u_password1",{time:2000,tips: [2,'#56a787']});
		return;
	}
	
	$.jsonRPC.request("admin2BranchCompanyService.addStaff", {
		params : [u_name,countryCode,u_mobile,u_password],
		success : function(result) {
			if (result) {
				layer.msg("添加员工成功。",{time:1000,icon:1},function(){
					$(".u_name").val("");
					$(".u_mobile").val("");
				});
			}else{
				layer.msg("添加员工失败。",{time:2000,icon:2});
			}
		}, 
		error : function(e) {
			layer.msg(e.error.msg,{time:2000,icon:2});
		}
	});
}