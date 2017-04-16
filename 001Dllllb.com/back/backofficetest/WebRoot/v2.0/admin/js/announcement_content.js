var _pageNo = 0;
var _id = 0;
var createNew = false;
$(document).ready(function() {
	_id = getParam("id","0");
	if(_id!="0"){
		getDetail(_id);
	}
});

function getDetail(id){
	$.jsonRPC.request('userAnnouncementService.getOne', {
	    params : [parseInt(id)],
	    success : function(result) {
			if (result) {
				$("#title").html(result.title);				
				$("#adminName").html(result.adminsShowName);				
				$("#publish_time").html(toDate(result.publishTime.time));				
				$(".announcement_content").html(result.content);
				$("#owner").html("backoffice 2.0");
				$("#time").html(getDate(result.publishTime.time));				
				
			}else{
				layer.msg("当前网络状况不太好稍后再试吧。",{time:2000},
						function(){
						parent.closeWindow();
					});
			}
	    }
	});
}

function getDate(date){
	var ret = new Date();
	ret.setTime(date);
	return ret.format("yyyy年MM月dd日");
}
