var _pageNo = 1;
const PAGESIZE = 20;

$(function(){
	getYearList();
	$("#startMonth").change(function () {
		if($("#startYear").val()==0){
			$("#startYear").val((new Date()).getFullYear());
		}
	})
	$("#go").click(function(){
		if($("#startYear").val()==0&&$("#startMonth").val()!=0){
			$("#startYear").val((new Date()).getFullYear());
		}
		refreshList(_pageNo);
	});
	refreshList(_pageNo);
});



//获取用户列表
function refreshList(page) {
	_pageNo = page;
	
	$('#result_list').html("");
	
	$.jsonRPC.request("userRankService.getUserRebatePage", {
		params : [_pageNo, PAGESIZE, "javascript:refreshList(??);",$("#startYear").val(),$("#startMonth").val()],
		success : function(result) {
			if(result.list.list.length > 0){
			html = '';
			for ( var i in result.list.list) {
				var item = result.list.list[i].map;
				var relation = ((parseFloat(item.level)-parseFloat(loginUser.level))== 1 ? '<span class="green">直接客户</span>' : (parseFloat(item.level)-parseFloat(loginUser.level)) + '级客户');
				switch (item.state) {
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
				
				html += '<tr><td >' + item.uid + '</td>' 
					+ '	<td>' + relation + '</td>' 
					+ '	<td><span class="label '+label_class +'" >' + label_name+ '</span></td>' 
					+ '	<td>' + item.mobile + '</td>' 
					+ '	<td>' + item.email+ '</td>'
					+ '	<td class="amount bold">'+ item.amountSum.toFixed(2)+'</td>' ;
			}
			html += '<tr><td colspan="10"><div class="pagelist">' + result.buttons + '</td></tr>';
			$('#result_list').html(html);
			}
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
						$(".userinfo .email").text(user['email']);
						$(".userinfo .mobile").text(user['mobile']);
						
						if (user_msg != null) {
							$(".userinfo .ename").text(user_msg['userEName']);
							$(".userinfo .nationality").text(user_msg['userNationality']);
							$(".userinfo .esidentialaddress").text(user_msg['userEsidentialAddress']);
							$(".userinfo .comname").text(user_msg['userComName']);
							$(".userinfo .industry").text(user_msg['userIndustry']);
							$(".userinfo .years_income").text(user_msg['userYearsIncom']);
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
function getYearList(){
	var html='<option value="0">[全部]</option>';
	var year = 2000;
	var date = new Date();
	for ( var i = 0; i <= (date.getFullYear()-2000); i++) {
		html+='<option value="'+year+'">'+year+'</option>';
		year++;
	};
	$("#startYear").html(html);
	$("#startYear").val(date.getFullYear());
	$("#startMonth").val(date.getMonth() + 1);
	refreshList(1);
};
