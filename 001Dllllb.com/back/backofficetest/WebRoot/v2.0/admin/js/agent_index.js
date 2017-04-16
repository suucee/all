const PAGESIZE = 10;
var _pageNo = 1;


$(document).ready(function(){
	
	
	refreshList(1);
});

function refreshList(pageNo) {
	_pageNo = pageNo;
	
	$.jsonRPC.request('admin2AgentService.getPageAgent', {
		params : [pageNo, PAGESIZE, "javascript:refreshList(??);", '', $("#keyword").val()],
		success : function(result) {
			$("tbody#result_list").html("");
			var page=result.map.page;
			var rebateAgent=result.map.rebateAgent;
			var html = '';
			for (var i in page.list.list) {
				var item = page.list.list[i];
				var status = item.state;
				var label_class = "";
				var label_name = "";
				
				switch (item.vipGrade) {
				case 1:
					label_class = 'label-pending';
					break;
				case 2:
					label_class = 'label-particular-filled';
					break;
				}
			   html += '<tr>'
					+ '    <td><a  onclick="goSeeProfile('+item['id']+')\">'+item._name+(item.disable ? ' <span class="red">[禁]</span>' : '') + (item.is_frozen ? ' <span class="purple">[冻]</span>' : '')+'</a></td>' 
					+ '    <td><span class="label '+label_class+'">'+item._vipGradeName+'</span></td>'
					+ '    <td>'+item.mobile+'</td>'
					+ '    <td>'+item.email+'</td>'
					+ '    <td>'+toSimpleDate(item.registrationTime.time)+'</td>'
					+ '    <td><a href="register.html?code='+item.referralCode+'" target="_blank">'+item.referralCode+'</a></td>'
					+ '    <td>'
					+ ' <button onclick="javascript:doEdit('+item.id+');" class="btn  btn-a">编辑</button>'
					+ ' <button onclick="javascript:doSet('+item.id+');"  class="btn  btn-a">设置</button>'
					+ '</td>'
					+ '</tr>'
					+ '<tr class="setting_panel user_panel'+item['id']+'" style="display:none;">'
				//	+ '<td>返佣设置:</td>'
					+ '<td>级别:'
					+ '<select class="vipGrade" name="user'+item.id+'vipGrade" style="width:120px;">'
					+ '</select>'
					+ '</td>'
					+ '<td >外汇内返:<br/><select name="user'+item.id+'rebate1"  style="width:120px;">'
					+ '<option value="0">0</option>'
					+ '<option value="1">1</option>'
					+ '<option value="2">2</option>'
					+ '<option value="3">3</option>'
					+ '<option value="4">4</option>'
					+ '<option value="5">5</option>'
					+ '<option value="6">6</option>'
					+ '<option value="7">7</option>'
					+ '<option value="8">8</option>'
					+ '<option value="9">9</option>'
					+ '<option value="10">10</option>'
					+ '<option value="11">11</option>'
					+ '<option value="12">12</option>'
					+ '<option value="13">13</option>'
					+ '<option value="14">14</option>'
					+ '<option value="15">15</option>'
					+ '</select></td>'
					+ '<td >金属内返:<br/><select name="user'+item.id+'rebate2"  style="width:120px;">'
					+ '	<option value="0">0</option>'
					+ '	<option value="1">1</option>'
					+ '	<option value="2">2</option>'
					+ '	<option value="3">3</option>'
					+ '	<option value="4">4</option>'
					+ '	<option value="5">5</option>'
					+ '	<option value="6">6</option>'
					+ '	<option value="7">7</option>'
					+ '	<option value="8">8</option>'
					+ '	<option value="9">9</option>'
					+ '	<option value="10">10</option>'
					+ '	<option value="11">11</option>'
					+ '	<option value="12">12</option>'
					+ '	<option value="13">13</option>'
					+ '	<option value="14">14</option>'
					+ '	<option value="15">15</option>'
					+ '	<option value="16">16</option>'
					+ '	<option value="17">17</option>'
					+ '	<option value="18">18</option>'
					+ '	<option value="19">19</option>'
					+ '	<option value="20">20</option>'
					+ '	<option value="21">21</option>'
					+ '	<option value="22">22</option>'
					+ '	<option value="23">23</option>'
					+ '	<option value="24">24</option>'
					+ ' <option value="25">25</option>'
					+ '</select></td>'
					+ '<td >原油内返:<br/><select name="user'+item.id+'rebate3"  style="width:120px;">'
					+ '	<option value="0">0</option>'
					+ '	<option value="1">1</option>'
					+ '	<option value="2">2</option>'
					+ '	<option value="3">3</option>'
					+ '	<option value="4">4</option>'
					+ '	<option value="5">5</option>'
					+ '	<option value="6">6</option>'
					+ '	<option value="7">7</option>'
					+ '	<option value="8">8</option>'
					+ '	<option value="9">9</option>'
					+ '	<option value="10">10</option>'
					+ '	<option value="11">11</option>'
					+ '	<option value="12">12</option>'
					+ '	<option value="13">13</option>'
					+ '	<option value="14">14</option>'
					+ '	<option value="15">15</option>'
					+ '	<option value="16">16</option>'
					+ '	<option value="17">17</option>'
					+ '	<option value="18">18</option>'
					+ '	<option value="19">19</option>'
					+ '	<option value="20">20</option>'
					+ '	<option value="21">21</option>'
					+ '	<option value="22">22</option>'
					+ '	<option value="23">23</option>'
					+ '	<option value="24">24</option>'
					+ '	<option value="25">25</option>'
					+ '</select></td>'
					+ '<td colspan="2">操作密码:<br/><input type="password" name="user'+item.id+'operationPassword"  style="width:100px;" /></td>'
					+ '<td> <button onclick="javascript:submitData(this);"  class="btn  btn-a submitData"  data-userid="'+item.id+'">确认</button></td>'
					+'</tr>';
		    }
			$("tbody#result_list").append(html);
			$.get("_block_agents.html", function(data){
				$(".setting_panel .vipGrade").html(data);

				for (var i in page.list.list) {
					 var item=page.list.list[i];
				     $("select[name='user"+item.id+"vipGrade']").val(item.vipGrade);
				}
		    });
			if(page.buttons != ""){
				$("tbody#result_list").append('<tr><td colspan="10"><div class="pagelist">'+page.buttons+'</td></tr>');
			}
			var lista=rebateAgent.list;
			for(var p in lista){
				var item=lista[p];
				$("select[name='user"+item.user.id+"rebate"+item.rebate.id+"']").val(item.money);
			}
		}
	});
}
function doSet(userId) {
	$(".user_panel"+userId).toggle();
}
function doEdit(userId) {
	goSeeProfile(userId);
}
function submitData(obj){
	var str=$(obj).data("userid");
	var vipGrade = $("[name='user"+str+"vipGrade']").val();
	var operationPassword = $("[name='user"+str+"operationPassword']").val();
	var rebate1 = parseFloat($("[name='user"+str+"rebate1']").val());
	var rebate2 = parseFloat($("[name='user"+str+"rebate2']").val());
	var rebate3 = parseFloat($("[name='user"+str+"rebate3']").val());
	var rebates = new Array();
	
	if (isNaN(rebate1)) {
		layer.tips("请选择正确的外汇内返.","[name='user"+str+"rebate1']",{time:2000,tips:[2,'#56a787']});
	} else if (isNaN(rebate2)) {
		layer.tips("请选择正确的金属内返.","[name='user"+str+"rebate2']",{time:2000,tips:[2,'#56a787']});
	} else if (isNaN(rebate3)) {
		layer.tips("请选择正确的原油内返.","[name='user"+str+"rebate3']",{time:2000,tips:[2,'#56a787']});
	} else if(operationPassword == ""){
		layer.tips("操作密码不能为空哦.","[name='user"+str+"operationPassword']",{time:2000,tips:[2,'#56a787']});
	}else {
		rebates.push(rebate1);
		rebates.push(rebate2);
		rebates.push(rebate3);
		$.jsonRPC.request('adminAgentService.setAgent', {
			params : [str, vipGrade, operationPassword, rebates],
			success : function(result) {
				layer.msg("设置成功！",{time:1500,icon:1});
			}
		});
	}
	
	
}

