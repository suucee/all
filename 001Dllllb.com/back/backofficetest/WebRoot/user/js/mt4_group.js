$(document).ready(function() {
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

								var html = '';
								for ( var i in result.list) {
									var item = result.list[i];

									html += '<tr><td>'
											+item.group
											+ '</td>'
											+ '<td>'
											+ item.company
											+ ' </td>'
											
										    + '</tr>';
								}
								$("tbody#result_list").html(html);

							}
						}
					});

} 