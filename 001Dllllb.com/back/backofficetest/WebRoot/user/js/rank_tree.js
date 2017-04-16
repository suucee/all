var theuserhasshowintree=false;

function getRankTree(id){
	$.get("_popup_rank_tree.html", function(data) {
		$("body").append(data);
		$.jsonRPC.request("userRankService.getTheUserRank", {
			params : [ id ],
			success : function(result) {
				list = result['map']['list']['list'];
				pid = result['map']['pid'];
				user_id = result['map']['user_id'];
				usercount = 0;
				for (var ii = 0; ii < list.length; ii++) {
					child_count = 0;
					for (var jj = 0; jj < list.length; jj++) {
						if (list[ii]['user_id'] == list[jj]['up_id']) {
							child_count++;
						}
					}
					if (usercount < child_count) {
						usercount = child_count;
					}
				}
				treewidth=((usercount * 200)>980)?(usercount * 200):980;
				$(".tree").width(treewidth);
				html = makeusertree(list, pid, user_id);
				theuserhasshowintree=false;
				$(".tree ul").append(html);
				$(".popup_dialog").fadeIn(200);
			}
		});
	});
}


function makeusertree(list,pid,user_id){
	htmler="";
	for (var ii = 0; ii < list.length; ii++) {
		user = list[ii]['map'];
		if(pid==user['up_id']){
			if(user_id==user['user_id']){
				htmler+="<li><a class='theuser'>"+user['email']+"</a>";
				theuserhasshowintree=true;
			}else{
				if(theuserhasshowintree){
			     	htmler+="<li><a class='user_bottom' onclick='treerefresh("+user['user_id']+")' style='cursor:pointer'>"+user['email']+"</a>";
				}else{
					htmler+="<li><a  class='user_top' >"+user['email']+"</a>";
				}
			}
			havechild=0;
			for (var jj = 0; jj < list.length; jj++) {
				 ch_user = list[jj]['map'];
			     if(ch_user['up_id']==user['user_id']){
			    	 havechild=1;
			    	 break;
			     }
			}
			if(havechild==1){
				htmler+="<ul>";
				htmler+=makeusertree(list,user['user_id'],user_id);
				htmler+="</ul>";
			}
			htmler+="</li>";
		}
	}
	return htmler;
}

function treerefresh(id){
	$(".only-ul").children().remove();
	$.jsonRPC.request("userRankService.getTheUserRank", {
		params : [id],
		success : function(result) {
			list = result['map']['list']['list'];
			pid = result['map']['pid'];
			user_id= result['map']['user_id'];
			usercount = 0;
			for (var ii = 0; ii < list.length; ii++) {
				child_count = 0;
				for (var jj = 0; jj < list.length; jj++) {
					if (list[ii]['user_id'] == list[jj]['up_id']) {
						child_count++;
					}
				}
				if (usercount < child_count) {
					usercount = child_count;
				}
			}
			treewidth=((usercount * 200)>980)?(usercount * 200):980;
			$(".tree").width(treewidth);
			
			$(".tree ul").append(makeusertree(list,pid,user_id));
			theuserhasshowintree=false;
		}
	});
}


