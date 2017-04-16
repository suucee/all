const PAGE_SIZE = 10;

$(document).ready(function() {
//	drawNoticeNumGlobaly(0);	
	
	$("#search").click(function(){
		getAnnouncementList(1);
	});
	
	getAnnouncementList(1);
	getWebItem();
});
function getWebItem(){
	$.jsonRPC.request('admin2IndexService.getWebItem',{
		params : [],
		success : function(result) {
			if(result!=null){
				setMessage_info(result.list);
			}
		}
	});
}
function setMessage_info (list) {
	var html ="";
	for(var i in list){
		var item=list[i];
		html += '<tr><td>';
		html += '<i class="fa fa-circle"></i>';
		html += '<span id="date">'+toDate(item.countTime.time)+'</span>';
		html += '<span id="message_info">'+item.content+'</span>';
		html += '</td><td>';
		if (item.link!="") {
		    html += '<a class="btn btn-a msg_btn_detail" href="'+item.link+'">查看详情</a >';
		}else{
			html+='&nbsp;';
		}
		html += '</td></tr>';
	}
	if (html=="") {
		html += '<tr><td colspan="10" class="bold">对不起，暂未检索到相关记录</td></tr>';
	};
	$("#message_table tbody").html(html);
} 

function getAnnouncementList(pageNo){
	var keyword = $("#keyword").val();
	$.jsonRPC.request('userAnnouncementService.getPage',
			{
				params : [ pageNo, PAGE_SIZE,"javascript:getAnnouncementList(??);","publish",keyword],
				success : function(result) {
					var html = '';
					if (result != null) {
						if(result.list.list.length==0){
							$("#notice_table").html('<tr><td colspan="2">对不起，没有找到相关内容。</td></tr>');
							return;
						}
						for ( var i in result.list.list) {
							var item = result.list.list[i];
							html += '<tr><td><i class="fa fa-circle" ></i>'
									+'<a onclick="parent.openNewWindow('+"'./../admin/announcement_content.html?id="+item.id+"'"+',null)">'
									+(item.top?'<font color="red">[顶]</font>'+item.title:item.title)
									+'</a></td>'
									+'<td><span id="date">'+toDate(item.publishTime.time)+'</span></td></tr>'
						}
						
						if(result.buttons!=null&&result.buttons!=""){
					    	html += '<tr><td colspan="10"><div class="pagelist">'+result.buttons+'</td></tr>';
					    }
						if (html=="") {
						     html += '<tr><td colspan="10" class="bold">对不起，暂未检索到相关记录</td></tr>';
						}
						$("#notice_table").html(html);
					}else{
						$("#notice_table").html('<tr><td colspan="2">网络状况不好，请稍候再试吧。</td></tr>');
					}
				}
			}
	);
}

