$(document).ready(function() {
	refreshList();	
});

function refreshList() {
	$.jsonRPC.request("adminSettingService.getList", {
		params : [],
		success : function(result) {
			if (result) {
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
						html += '<tr style="background:#eef4ff;"><td colspan="5" class="bold">'+current_group+'</td></tr>';
					}
					html += '<tr>'
						+ '<td style="text-align:right;"><input type="hidden" name="key" value="'+item.key+'" />'+item.name+'：</td>'
						+ '<td><input class="edit" name="value" type="text" value="'+value+'" style="'+css+'" /></td>'
						+ '<td><button class="btn">更新</button></td>'
						+ '<td>'+(item.updatedTime == null ? '-' : toDate(item.updatedTime.time))+'</td>'
						+ '<td>'+item.description+'</td>'
						+ '</tr>';
				}
				
				$("#result_list").html(html);
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
							window.alert("已更新");
						}
					});
				});
			}
		}
	});
}
