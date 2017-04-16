$(document).ready(function(){	
	refreshList();
});

function refreshList() {
	var html ="";
	$.jsonRPC.request('userService.questions', {
	    params : [],
	    success : function(result) {
	    	if(result!=null){
	    		for (var res in result.list) {
	    			var item = result.list[res];
	    		html += '<tr><td>'+item.userName+'</td><td>'+item.questionType+'</td><td>'+item.userEmail+'</td><td>'+item.userPhone+'</td><td>'+(item.state==1?"<a style='color:red;' href='javascript:changeState("+item.id+");'>未联系</a>":"<a href='javascript:changeState("+item.id+");'>已联系</a>")+'</td><td>'+toDate(item.creatTime.time)+'</td></tr>';
	    		}
	    		$("#result_list").html(html);
	    	} 
	    }
	});
}
function changeState(id){
	$.jsonRPC.request('userService.changeQuestion', {
	    params : [id],
	    success : function(result) {
	    	if(result){
	    		refreshList();	
	    	} else{
	    	}
	    }
	});
}






















