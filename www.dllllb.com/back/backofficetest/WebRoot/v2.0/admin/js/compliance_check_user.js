var complianceCheckUser = null;
var _user_id = 0;
$(function() {
	check_init(
			null,"",
			function(){
				$(".check_user_hide").hide();
				$(".check_user_show").removeClass("margin-left15").removeClass("btn-f").addClass("btn-b");
				$(".check_user_show").html("待审核的用户资料");
				$(".div_userinfo").show();
				$(".check_userinfo_content_name").html("用户资料");
				complianceCheckUser = new ComplianceCheckUser();
			},
			function(){
				doCheck('ACCEPTED',"admin2CheckUserService.doCheck",_user_id)
			},
			function(){
				doCheck('REJECTED',"admin2CheckUserService.doCheck",_user_id)
			}
		);
	
});

var ComplianceCheckUser = function() {
	_user_id = getParam("id", 0);
	this.user_id = _user_id;
	this.c_id = getParam("cid", 0);
	this.init();
	if(this.user_id != 0){
		parent.localUserById(this.user_id);
	}
};
ComplianceCheckUser.prototype = {
	init : function() {
		init_userinfo(this.user_id);
        getCddCheckComment(this.c_id);
	},
};
