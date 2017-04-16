$(document).ready(function() {
	refreshList();	
	
});

function refreshList() {
	$.jsonRPC.request("adminSettingService.getList", {
		params : [],
		success : function(result) {
			if (result) {
				var defaultMt4GroupValue = '';
				var html = '';
				var current_group = '';
				
				for (var i in result.list) {
					var item = result.list[i];
					var value = '';
					var css = '';
					switch (item.type) {
					case "int":
						value = item.intValue;
						css = 'text-align:right;';
						break;
					case "double":
						value = item.doubleValue;
						css = 'text-align:right;';
						break;
					case "String":
						value = item.stringValue;
					}

					if (item.groups != current_group) {
						//新组
						current_group = item.groups;
						html += '<tr style="background:#aaaaaa;"><td colspan="5" class="bold">'+current_group+'</td></tr>';
					}
					var defaultBtn = '<button class="btn btn-1">更新</button>';
					var detaultInp = '<input class="edit" name="value" type="text" value="'+value+'" style="'+css+'" />';
					
					if(item.key == "DefaultAgent"){//默认推荐码
						css += 'width: 50px;';//
						detaultInp = '<input class="edit" name="value" type="text" maxlength="5" value="'+value+'" style="'+css+'"/>';
						detaultInp += '<a class="margin-left10" title="如果记不住推荐码，点击此处选择推荐人，系统会自动设置为该人的推荐码" '+
							'style="text-decoration:underline;" onclick="parent.openNewWindow(\'./../admin/setting_default_agent.html?code='+value+'\', refreshList)">记不住推荐码？点此</a>';//防止出现btn点击，用a代替
					}else if(item.key == "DefaultMt4Group"){
						defaultMt4GroupValue = value;
					}
					html += '<tr>'
						+ '<td style="text-align:right;"><input type="hidden" name="key" value="'+item.key+'" />'+item.name+'：</td>'
						+ '<td>'+detaultInp+'</td>'
						+ '<td>'+defaultBtn+'</td>'
						+ '<td>'+(item.updatedTime == null ? '-' : toDate(item.updatedTime.time))+'</td>'
						+ '<td>'+item.description+'</td>'
						+ '</tr>';
				}
				if (html=="") {
				     html += '<tr><td colspan="10" class="bold">对不起，暂未检索到相关记录</td></tr>';
				}
				$("#result_list").html(html);
				
				getDefaultMt4Group(defaultMt4GroupValue);
				
				$("#result_list").find("[name='value']").change(function(){
					$(this).parent().parent().find("button").addClass("btn-primary");
				});
				
				
				$("#result_list").find("button").click(function(){
					var _button = $(this);
					var key = $(this).parent().parent().find("[name='key']").val();
					var value = $(this).parent().parent().find("[name='value']").val();
					$.jsonRPC.request('adminSettingService.edit', {
						params : [key, value],
						success : function(result) {
							$(_button).removeClass('btn-primary');
							layer.msg("已更新",{time:1000,icon:1});
						}
					});
				});
			}
		}
	});
}

/**
 * 如果数据库有记录，将原本的文本框替换为下拉列表，否则还是保持文本框
 * @param xdefaultMt4GroupValue 默认的组名
 */
function getDefaultMt4Group(xdefaultMt4GroupValue){
	$.jsonRPC.request("adminMT4Service.getGroupPage", {
		params : [],
		success : function(result) {
			if(result!=null && result.list.length > 0){
				//将文本框改为下拉列表
				$input = $("[value='"+xdefaultMt4GroupValue+"']");
				$inputParent = $($input).parent("td");
				$($input).remove();
				
				detaultInp = '<select name="value" >';
				for(var j in result.list){
					if(result.list[j].group == xdefaultMt4GroupValue){
						detaultInp += '<option value='+result.list[j].group+' selected="selected">'+result.list[j].group+'</option>';
					}else{
						detaultInp += '<option value='+result.list[j].group+'>'+result.list[j].group+'</option>';
					}
				}
				detaultInp += '</select>';
				
				$($inputParent).append(detaultInp);
			}
		}
	});
}