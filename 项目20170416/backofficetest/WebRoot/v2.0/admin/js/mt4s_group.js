
var group = '';
$(document).ready(function() {
	group = getParam("group","0");
	refreshList();
});


function refreshList() {
	$.jsonRPC
			.request(
					"adminMT4Service.getGroupPage",
					{
						params : [],
						success : function(result) {
							if (result != null) {
								if(result.list.length == 0){
									$("tbody#result_list").html("<td colspan=10> 暂无MT4组信息。 </td>");
									return;
								}
								var html = '';
								for ( var i in result.list) {
									var item = result.list[i];
									if(item.group == group){
										html += '<tr class="bold"><td>'
											+item.group
											+ '</td>'
											+ '<td>'
											+ item.company
											+ ' </td>'
											
											+ '</tr>';
									}else{
										
									html += '<tr><td>'
											+item.group
											+ '</td>'
											+ '<td>'
											+ item.company
											+ ' </td>'
										    + '</tr>';
									}
								}
								$("tbody#result_list").html(html);

							}
						}
					});

} 