var _pageNo = 0;
var _id = 0;
var createNew = false;
$(document).ready(function() {
	
	_id = getParam("id","0");
	if(_id=="0"){
		createNew = true;
	}else{
		createNew = false;
		getDetail(_id);
	}
	
	$("#isTop").change(function(){
		if($("#isTop").is(":checked")){
			$("#sortNum").val("0");
			$("#sortNum").prop("disabled","true");
		}else{
			$("#sortNum").removeAttr("disabled");
			$("#sortNum").val("10");
		}
	});
	
	$("#sortNum").change(function(){
		var value = $("#sortNum").val();
		if(value!="0"){
			$("#isTop").removeAttr("checked");//取消选中
		}
		if(isNaN(value)  || value=="0"){
			$("#sortNum").focus();
			$("#sortNum").val("");
			layer.tips("请输入大于等于1的整数.","#sortNum",{time:2000,tips:[2,'#56a787']});
			$("#sortNum").attr("placeholder","请输入大于等于1的整数");
			return;
		}else{
			$("#sortNum").val(parseInt(value));
		}
	});
	
	
	
	$("#creatTime").datetimepicker({
         timeFormat: "HH:mm:ss",
         dateFormat: "yy-mm-dd"
     });
	$("#publish_time").datetimepicker({
		timeFormat: "HH:mm:ss",
		dateFormat: "yy-mm-dd"
	});

	$("#publish_now").click(function(){
		addAnnouncement(true,_id);
	});
	$("#refresh").click(function(){
		addAnnouncement(true,_id);
	});
	$("#publish_timing").click(function(){
		addAnnouncement(false,_id);
	});
	
	
	if(createNew){//如果是新创建的话
		$("#publishTable").show(300);
		$("#refreshDiv").hide();
	}else{
		$("#refreshDiv").show(300);
		$("#publishTable").hide();
	}
	
});

/**
 * 
 * @param isPublishNow 立即发布？
 * @param id 更新的时候需要指定id,否则为0
 */
function addAnnouncement(isPublishNow, id){
	var successTip = '';
	var title = $("#name").val();
	if(title==""){
		layer.tips("标题不能为空哦","#name",{time:2000,tips:[2,'#56a787']});
		$("#name").css('background', '#ffff80').focus();
		return;
	}
	var content = editor.getContent();
	var publishTime = $("#publish_time").val();
	if(!isPublishNow){//不是立即发布，需要设定定时发布的时间
		if(publishTime=="" || publishTime == null){
			layer.tips("需要选择定时时间哦","#publish_time",{time:2000,tips:[1,'#56a787']});
			$("#publish_time").css('background', '#ffff80').focus();
			return;
		}
		successTip = "发布成功，新增加的公告会在您规定的时候上线显示。";
	}else{
		successTip = "发布成功，新增加的公告将即刻上线显示。";
	}
	var sortNum = $("#sortNum").val();
	if(sortNum==""){
		layer.tips("请输入大于等于1的整数.","#sortNum",{time:2000,tips:[2,'#56a787']});
		$("#sortNum").css('background', '#ffff80').focus();
		return;
	}
	var top = $("#isTop").is(":checked");
	var display = $("#display").is(":checked");
	$.jsonRPC.request('adminAnnouncementService.addOrUpdate', {
	    params : [id,title,content,sortNum,publishTime,top,display],
	    success : function(result) {
			if (result) {
				layer.msg(successTip,{time:1000,icon:1},
					function(){
					parent.closeWindow();
				});
			}else{
				layer.msg("当前网络状况不太好，稍后再试吧",{time:2000,icon:2});
			}
	    }
	});
}

function getDetail(id){
	$.jsonRPC.request('adminAnnouncementService.getOne', {
	    params : [parseInt(id)],
	    success : function(result) {
			if (result) {
				$("#name").val(result.title);
				$("#sortNum").val(result.sort);
				if(result.top){
					$("#isTop").prop("checked","checked");
				}else{
					$("#isTop").removeAttr("checked");
				}
				if(result.display){
					$("#display").prop("checked","checked");
				}else{
					$("#display").removeAttr("checked");
				}
				editor.addListener("ready", function() {
					editor.setContent(result.content);
				});
			}else{
				layer.msg("当前网络状况不太好，稍后再试吧",{time:2000,icon:2});
			}
	    }
	});
}
