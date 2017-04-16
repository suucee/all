const PAGE_SIZE = 10;

/*这部分是批处理的操作代码，与后台对应*/
const DELETE = 'delete';
const HIDE = 'hide';
const TOP = 'top';
const SHOW = 'show';
const NOTOP = 'noTop';
var pageNo=1;
var close = true;//是否关闭了批处理工作域
$(document).ready(function() {
	$("#selectAll").change(function () {
		if($("#selectAll").is(':checked')){//选中了
			$("#result_list tr td [type='checkbox']").prop("checked","checked");//全选//用prop比attr好，attr有bug
		}else{
			$("#result_list tr td [type='checkbox']").removeAttr("checked");//取消全选
		}
	});
	
	$("#oderBy").change(function () {
		refreshList(1);
	});
	refreshList(pageNo);
	
});


function batchDeal(){//批处理
	if(close){
	//准备显示
		$("#batchFont").text("关闭批量管理");
		$("#batchDealDiv").show(200);	
		$(".my-sticky-element").prepend('<th width="5%" class="checkTH">选择</th>');	//增加选择栏目
		$("#result_list tr").prepend('<td><input type="checkbox" name="checkbox"/></td>');//增加复选框td
		
		var pageListTr = $(".pagelist").parents("tr");
		$(pageListTr).find("input").remove();//删除最后一个有翻页所在行的复选框
		close = false;
	}
	else{
		$("#batchFont").text("批量管理");
		$("#batchDealDiv").hide(200);
		$("#selectAll").removeAttr("checked");//取消全选
		$(".checkTH").remove();	//删除选择栏目
		$("#result_list tr td [type='checkbox']").parent("td").remove();//删除复选框td
		
		close = true;
	}
}



function refreshList(page) {
	var columnId = $("#parent").val();
	var orderBy = $("#oderBy").val();
	$.jsonRPC.request('adminAnnouncementService.getPage',
			{
				params : [ page, PAGE_SIZE,"javascript:refreshList(??);",orderBy],
				success : function(result) {
					pageNo=result.currentPage;
					var html = '';
					if (result != null) {
						if(result.list.list.length == 0){
							$("tbody#result_list").html('<tr><td colspan="10">目前暂无公告</td></tr>');
							return;
						}
						close = false;//是否关闭了批处理工作域
						batchDeal();
						
						for ( var i in result.list.list) {
							var item = result.list.list[i];
							var publishState = '已发布';
							if(compDate(parseDate(item.publishTime.time), null) == 1){
								publishState = '未到发布时间';
							}
							
							html+=(item.display?'<tr id="TR_'+item.id+'" style="text-align:center;" >':'<tr id="TR_'+item.id+'" style="text-align:center;color:gray;">')
									+'<td style="text-align:left;">'
									+(item.top?'<font color="red">[顶]</font>'+item.title:item.title)
									+'</td>'
								+'<td><input type="text" id="'+item.id+'" onchange="updateSortNum('+item.id+','+item.sort+')" value="'+item.sort+'" style="width: 25px;"></td>'
								+'<td class="changeD'+item.id+'"><a title="点击以更改状态" href="javascript:updateDisplay('
								+(item.display?'true,'+item.id+');"><i class="fa fa-eye"></i>显示</a></td>':'false,'+item.id+');"><i class="fa fa-eye-slash"></i>不显示</a></td>')
								+'<td class="changeT'+item.id+'"><a title="点击以更改状态" href="javascript:updateTop('
								+(item.top? 'true,'+item.id+');"><i class="fa fa-thumb-tack"></i>置顶' : 'false,'+item.id+');">正常')
								+'</a></td>'
								+'<td>'+toDate(item.publishTime.time)+'</td>'
								
								+'<td>'+publishState+'</td>'
								
								+'<td>'+item.adminsShowName+'</td>'
								+'<td><a class="margin-right10"  onclick="parent.openNewWindow('+"'./../admin/announcement_detail.html?id="+item.id+"'"+',updateList)"><i class="fa fa-pencil-square-o"></i>编辑</a>'
								+'<a class="font-red margin-right10"  href="javascript:deleteOne('+item.id+');"><i class="fa fa-trash"></i>删除</a>'
								+'<a class="" onclick="parent.openNewWindow('+"'./../admin/announcement_content.html?id="+item.id+"'"+',donothing)"><i class="fa fa-eye"></i>预览</a>'
								+'</td>'
								+'</tr>';
						}
						if(result.buttons != ""){
							html += '<tr><td colspan="10"><div class="pagelist">'+ result.buttons + '</td></tr>';
						}
						_pageNo = pageNo;
						$("tbody#result_list").html(html);
					}
				}
			});//fa-eye-slash

}
function updateList(){
	refreshList(pageNo);
}
function updateDisplay(display, id){
	var td = $(".changeD"+id);
	var content = '';
	if(display){
		content = "您确认将显示状态改变为：<span class='red'>隐藏</span>？（网站将不会显示此条公告）"
	}else{
		content = "您确认将显示状态改变为：<span class='red'>显示</span>？（网站将立即显示此条公告）"
	}
	layer.confirm(content, 
			{btn:["确认修改","放弃修改"]}, 
			function(){
				$.jsonRPC.request('adminAnnouncementService.updateDisplay',
					{
						params : [id,display],
						success : function(result) {
							if(!result){
								layer.msg("当前网络状况不太好，稍后再试吧.",{time:2000});
							}else{
								layer.msg("修改成功",{time:1000});
								if(display){//直接在前台修改，省得发请求
									$(td).html('<a title="点击以更改状态" href="javascript:updateDisplay(false,'+id+');"><i class="fa fa-eye-slash"></i>不显示</a>');
								}else{
									$(td).html('<a title="点击以更改状态" href="javascript:updateDisplay(true,'+id+');"><i class="fa fa-eye"></i>显示</a>');
								}
							}
							return;
						}
					});
			}, 
			function(){return;}
	);
}

function updateTop(top, id){
	var td = $(".changeT"+id);
	var content = '';
	if(top){
		content = "您确认<span class='red'>取消置顶</span>此条公告？"
	}else{
		content = "您确认<span class='red'>置顶</span>此条公告？"
	}
	layer.confirm(content, 
			{btn:["确认","放弃"]}, 
			function(){
				$.jsonRPC.request('adminAnnouncementService.updateTop',
					{
						params : [id,top],
						success : function(result) {
							if(!result){
								layer.msg("当前网络状况不太好，稍后再试吧.",{time:2000,icon:2});
							}else{
								layer.msg("修改成功",{time:1000,icon:1});
								refreshList(1);
							}
							return;
						}
					});
			}, 
			function(){return;});
}


function deleteOne(id){
	layer.confirm("您确认<span class='red'>删除</span>此条公告？", 
			{btn:["删除","放弃"]}, 
			function(){
				$.jsonRPC.request('adminAnnouncementService.delete',
					{
						params : [id],
						success : function(result) 
						{
							if(result){
								layer.msg("公告删除成功。",{time:1000,icon:1});
								$("#TR_"+id).animate({"opacity":"0"},500,"linear",function () {
									$("#TR_"+id).remove();
								});
								return;
							}
						}
					});
			}, 
			function(){return;});
}


function updateSortNum(id,originSort){
	
	var sort = $(":input[id='"+id+"']").val();
	if(isNaN(sort) || parseInt(sort) <= 0 ){
		$(":input[id='"+id+"']").val("").focus();
		layer.tips("请输入大于等于1的整数.",":input[id='"+id+"']",{time:2000,tips:[2,'#56a787']});
	}
	else{
		if(originSort == 0){
			layer.confirm("当前是<span class='red'>置顶</span>状态，您的更改会导致该项不再置顶，是否确认修改？", 
					{btn:["确认修改","放弃"]}, 
					function(){
						$.jsonRPC.request('adminAnnouncementService.updateSortNum',
								{
									params : [id,parseInt(sort)],
									success : function(result) {
										if(result){
											layer.msg("修改成功",{time:1000,icon:1});
											refreshList(1);
											return;
										}else{
											layer.msg("当前网络状况不太好，稍后再试吧.",{time:2000,icon:2});
										}
									}
								});
						
					},
					function(){
						return;
					}
				);
		}
		
	}
}


function batchDeal_operation(operationName){
	var selectStr = '';
	  $('input:checkbox[name="checkbox"]:checked').each(function(i){
		  var announcementId = $(this).parents("tr").attr("id").replace("TR_","");//获取公告id
		  selectStr += announcementId+",";
	  });
	  if(selectStr == ''){
		  layer.msg("您还没有勾选任何一个公告呢....",{time:1500,icon:2});
		  return;
	  }
	  
	  $.jsonRPC.request('adminAnnouncementService.batchDeal',
				{
					params : [operationName, selectStr],
					success :function(result) {
						if(result == null || !result){
							layer.msg("当前网络状况不太好，稍后再试吧.",{time:2000,icon:2});
						}else{
							layer.msg("修改成功",{time:1000,icon:1});
							refreshList(1);
						}
					}
				}
	  );
}

function hideSelect(){
	batchDeal_operation(HIDE);
}
function showSelect(){
	batchDeal_operation(SHOW);
}
//function setTopSelect(){
//	batchDeal_operation(TOP);
//}
function noTopSelect(){
	batchDeal_operation(NOTOP);
}
function deleteSelect(){
	layer.confirm("您确定<span class='red'>删除</span>所选公告，操作将不可恢复。",
			{btn:["确定删除","放弃"]},
			function(){
				batchDeal_operation(DELETE);
			},
			function(){
				return;
			});
}


function compDate(Date1, Date2){
	if(Date2 == null){
		Date2 = new Date();
	}
	if(Date1 == Date2){
		return 0;
	}else if(Date1 > Date2){
		return 1;
	}else if(Date1 < Date2){
		return -1;
	}
}


function parseDate(date) {
	var ret = new Date();
	ret.setTime(date);
	return ret;
}

function donothing(){
	
}

function callback(){
	refreshList(1);
}