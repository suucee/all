var _pageNo = 1;
const PAGESIZE = 20;

$(function(){
	refreshList(_pageNo);
});



//获取用户列表
function refreshList(page) {
	_pageNo = page;
	
	$('#result_list').html("");
	
	$.jsonRPC.request("userRankService.getAllUser", {
		params : [_pageNo, PAGESIZE, "javascript:refreshList(??);"],
		success : function(result) {		
			list = result['list']['list'];
			html = '';
			for ( i = 0; i < list.length; i++) {
				var obj = list[i];
				var item = obj;
				var registrationTime="--";
				var parentRegistrationTime="--";
				var pid="--";
				var dohtml='';
				//var dohtml='<a href="javascript:openPopup('+"'tree'"+','+obj['id']+')">关系图</a>';
				var showinfo='<a href="javascript:openPopup('+"'info'"+','+obj['id']+')"><span class="btn btn-primary">查看</span></a>';
				obj.registrationTime== null ? registrationTime='--' : registrationTime=toDate(obj.registrationTime.time);
				obj.parent== null ? parentRegistrationTime='--' : parentRegistrationTime=toDate(obj.parent.registrationTime.time);
				obj.parent== null ? pid='--' : pid=obj.parent.id;
				var relation = ((parseFloat(obj.level)-parseFloat(loginUser.level))== 1 ? '<span class="green">直接客户</span>' : (parseFloat(obj.level)-parseFloat(loginUser.level)) + '级客户');
				
				switch (obj.state) {
				case 'UNVERIFIED':
					label_class = 'label-important';
					label_name = '资料不全';
					break;
				case 'VERIFIED':
					label_class = 'label-success';
					label_name = '已通过';
					break;
				case 'REJECTED':
					label_class = 'label-cancel';
					label_name = '被驳回';
					break;
				case 'AUDITING':
					label_class = 'label-pending';
					label_name = '审核中';
					break;
				default:
					label_class = 'label-pending';
					label_name = '';
				}
				
				html += '<tr>' 
					+ '	<td>' + (obj._name != "" ? obj._name : '<span class="gray">(暂无姓名)</span>') + '</td>' 
					+ '	<td>' + item['_vipGradeName'] + '</td>' 
					+ '	<td>' + relation + '</td>' 
					+ '	<td><span class="label '+label_class +'" >' + label_name+ '</span></td>' 
					+ '	<td>' + obj['mobile'] + '</td>' 
					+ '	<td>' + obj['email'] + '</td>'
					+ '	<td>' + registrationTime + '</td>' 
					+ '	<td>' + obj['referralCode']+'</td>' 
					+ '	<td>' + showinfo + '&nbsp;&nbsp;' + dohtml + '</td><tr>';
			}
			html += '<tr><td colspan="10"><div class="pagelist">' + result.buttons + '</td></tr>';
			$('#result_list').append(html);
		}
	});
}


function openPopup(type, id) {
	switch (type) {
	case "tree":
		getRankTree(id);
		break;
	case "info":
		$.get("_popup_show_userinfo.html", function(data) {
			$("body").append(data);
			$.jsonRPC.request("userRankService.getOneUserInfo", {
				params : [ id ],
				success : function(result) {
					if (result) {
						user = result['user'];
						user_msg = result;
						$(".user_id").text(user['id']);
						switch (user['state']) {
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
							alert("系统错误，请稍后再试");
							break;
						}
							
						$(".email").text(user['email']);
						$(".tel").text(user['mobile']);
						if(user_msg!=null){
							$(".name").text(user_msg['userName']);
							$(".ename").text(user_msg['userEName']);
							//下拉选项cardType
							$("#cardType").text(user_msg['cardType']);
							$("#cardID").text(user_msg['userIdCard']);
							//下拉居住地区countryCode
							$(".countryCode").text(user_msg['userNationality']);
							$(".address").text(user_msg['userEsidentialAddress']);
							$(".cname").text(user_msg['company']);//公司名称
							$(".hangye").text(user_msg['userIndustry']);//行业
							$(".userPosition").text(user_msg['position']);//职位
							$(".yearsr").text(user_msg['userYearsIncom']);//年收入
						}
						
					}
				}
			});
			$(".popup_dialog").fadeIn(200);
		});
		break;
	}
}
function closePopup() {
	$(".popup_dialog").remove();
}

