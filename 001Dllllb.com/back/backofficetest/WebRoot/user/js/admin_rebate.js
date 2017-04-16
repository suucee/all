$(document).ready(function() {
	refreshList();
	$(".updatebt").click(function () {
		$(this).addClass("selectbt");
	});
});

function refreshList() {
	$.jsonRPC.request("adminMT4Service.getGroupPage",{
						params : [],
						success : function(result) {
							if (result != null) {

								var html = '';
								for ( var i in result.list) {
									var item = result.list[i];

									html = '<tr><td>'
											+item.group
											+ '</td>'
											+ '<td>'
											+ item.company
											+ ' </td>'
											+ '<td>'
											+ '<input type="text" value="0" class="'+item.group+'_f '+item.group+'_input rebate_input" disabled="disabled">'
											+ ' </td>'
											+ '<td>'
											+ '<input type="text" value="0"  class="'+item.group+'_o '+item.group+'_input rebate_input" disabled="disabled">'
											+ ' </td>'
											+ '<td>'
											+ '<input type="text" value="0" class="'+item.group+'_m '+item.group+'_input rebate_input" disabled="disabled">'
											+ ' </td>'
											+ '<td>'
											+ '<table style="width:100%;" class="rebate_tab">'
											+ '<tr>'
											+ '<td>'
											+ '5＜：</td>'
											+ '<td><span id="'+item.group+'1" class="'+item.group+'_span"></span>'
											+ '<select class="'+item.group+'1 '+item.group+'_select" style="display:none;">'
											+ '<option value="0.00">0%</option>'
											+ '<option value="0.05">5%</option>'
											+ '<option value="0.10">10%</option>'
											+ '<option value="0.15">15%</option>'
											+ '<option value="0.20">20%</option>'
											+ '<option value="0.25">25%</option>'
											+ '<option value="0.30">30%</option>'
											+ '<option value="0.35">35%</option>'
											+ '<option value="0.40">40%</option>'
											+ '<option value="0.45">45%</option>'
											+ '<option value="0.50">50%</option>'
											+ '<option value="0.55">55%</option>'
											+ '<option value="0.60">60%</option>'
											+ '<option value="0.65">65%</option>'
											+ '<option value="0.70">70%</option>'
											+ '<option value="0.75">75%</option>'
											+ '<option value="0.80">80%</option>'
											+ '<option value="0.85">85%</option>'
											+ '<option value="0.90">90%</option>'
											+ '<option value="0.95">95%</option>'
											+ '<option value="1.00">100%</option>'
											+ '</select>'
											+ '</td>'
											+ '<td><span id="'+item.group+'2" class="'+item.group+'_span"></span>'
											+ '<select class="'+item.group+'2  '+item.group+'_select" style="display:none;">'
											+ '<option value="0.00">0%</option>'
											+ '<option value="0.05">5%</option>'
											+ '<option value="0.10">10%</option>'
											+ '<option value="0.15">15%</option>'
											+ '<option value="0.20">20%</option>'
											+ '<option value="0.25">25%</option>'
											+ '<option value="0.30">30%</option>'
											+ '<option value="0.35">35%</option>'
											+ '<option value="0.40">40%</option>'
											+ '<option value="0.45">45%</option>'
											+ '<option value="0.50">50%</option>'
											+ '<option value="0.55">55%</option>'
											+ '<option value="0.60">60%</option>'
											+ '<option value="0.65">65%</option>'
											+ '<option value="0.70">70%</option>'
											+ '<option value="0.75">75%</option>'
											+ '<option value="0.80">80%</option>'
											+ '<option value="0.85">85%</option>'
											+ '<option value="0.90">90%</option>'
											+ '<option value="0.95">95%</option>'
											+ '<option value="1.00">100%</option>'
											+ '</select>'
											+ '</td>'
											+ '<td><span id="'+item.group+'3" class="'+item.group+'_span"></span>'
											+ '<select class="'+item.group+'3 '+item.group+'_select" style="display:none;">'
											+ '<option value="0.00">0%</option>'
											+ '<option value="0.05">5%</option>'
											+ '<option value="0.10">10%</option>'
											+ '<option value="0.15">15%</option>'
											+ '<option value="0.20">20%</option>'
											+ '<option value="0.25">25%</option>'
											+ '<option value="0.30">30%</option>'
											+ '<option value="0.35">35%</option>'
											+ '<option value="0.40">40%</option>'
											+ '<option value="0.45">45%</option>'
											+ '<option value="0.50">50%</option>'
											+ '<option value="0.55">55%</option>'
											+ '<option value="0.60">60%</option>'
											+ '<option value="0.65">65%</option>'
											+ '<option value="0.70">70%</option>'
											+ '<option value="0.75">75%</option>'
											+ '<option value="0.80">80%</option>'
											+ '<option value="0.85">85%</option>'
											+ '<option value="0.90">90%</option>'
											+ '<option value="0.95">95%</option>'
											+ '<option value="1.00">100%</option>'
											+ '</select></div>'
											+ '</td>'
											+ '</tr>'
											+ '<tr>'
											+ '<td>5＋：</td>'
											+ '<td><span id="'+item.group+'4" class="'+item.group+'_span"></span>'
											+ '<select class="'+item.group+'4 '+item.group+'_select" style="display:none;">'
											+ '<option value="0.00">0%</option>'
											+ '<option value="0.05">5%</option>'
											+ '<option value="0.10">10%</option>'
											+ '<option value="0.15">15%</option>'
											+ '<option value="0.20">20%</option>'
											+ '<option value="0.25">25%</option>'
											+ '<option value="0.30">30%</option>'
											+ '<option value="0.35">35%</option>'
											+ '<option value="0.40">40%</option>'
											+ '<option value="0.45">45%</option>'
											+ '<option value="0.50">50%</option>'
											+ '<option value="0.55">55%</option>'
											+ '<option value="0.60">60%</option>'
											+ '<option value="0.65">65%</option>'
											+ '<option value="0.70">70%</option>'
											+ '<option value="0.75">75%</option>'
											+ '<option value="0.80">80%</option>'
											+ '<option value="0.85">85%</option>'
											+ '<option value="0.90">90%</option>'
											+ '<option value="0.95">95%</option>'
											+ '<option value="1.00">100%</option>'
											+ '</select></td>'
											+ '<td><span id="'+item.group+'5" class="'+item.group+'_span"></span>'
											+ '<select class="'+item.group+'5 '+item.group+'_select" style="display:none;">'
											+ '<option value="0.00">0%</option>'
											+ '<option value="0.05">5%</option>'
											+ '<option value="0.10">10%</option>'
											+ '<option value="0.15">15%</option>'
											+ '<option value="0.20">20%</option>'
											+ '<option value="0.25">25%</option>'
											+ '<option value="0.30">30%</option>'
											+ '<option value="0.35">35%</option>'
											+ '<option value="0.40">40%</option>'
											+ '<option value="0.45">45%</option>'
											+ '<option value="0.50">50%</option>'
											+ '<option value="0.55">55%</option>'
											+ '<option value="0.60">60%</option>'
											+ '<option value="0.65">65%</option>'
											+ '<option value="0.70">70%</option>'
											+ '<option value="0.75">75%</option>'
											+ '<option value="0.80">80%</option>'
											+ '<option value="0.85">85%</option>'
											+ '<option value="0.90">90%</option>'
											+ '<option value="0.95">95%</option>'
											+ '<option value="1.00">100%</option>'
											+ '</select></td>'
											+ '<td><span id="'+item.group+'6" class="'+item.group+'_span"></span>'
											+ '<select class="'+item.group+'6 '+item.group+'_select" style="display:none;">'
											+ '<option value="0.00">0%</option>'
											+ '<option value="0.05">5%</option>'
											+ '<option value="0.10">10%</option>'
											+ '<option value="0.15">15%</option>'
											+ '<option value="0.20">20%</option>'
											+ '<option value="0.25">25%</option>'
											+ '<option value="0.30">30%</option>'
											+ '<option value="0.35">35%</option>'
											+ '<option value="0.40">40%</option>'
											+ '<option value="0.45">45%</option>'
											+ '<option value="0.50">50%</option>'
											+ '<option value="0.55">55%</option>'
											+ '<option value="0.60">60%</option>'
											+ '<option value="0.65">65%</option>'
											+ '<option value="0.70">70%</option>'
											+ '<option value="0.75">75%</option>'
											+ '<option value="0.80">80%</option>'
											+ '<option value="0.85">85%</option>'
											+ '<option value="0.90">90%</option>'
											+ '<option value="0.95">95%</option>'
											+ '<option value="1.00">100%</option>'
											+ '</select></td>'
											+ ' </tr>'
											+ ' </table>'
											+ ' </td>'
											+ '<td style="text-align:center;">'
											+ '<a href="javascript:open(\''+item.group+'\');" class="btn '+item.group+'_open">编辑</a>&nbsp;&nbsp;&nbsp;&nbsp;'
											+ '<a href="javascript:updateRebate(\''+item.group+'\');"><span class="btn  btn-primary '+item.group+'_save" style="display:none;">保存</span></a>&nbsp;&nbsp;'
											+ '<a href="javascript:closeRebate(\''+item.group+'\');"><span class="btn  btn-primary '+item.group+'_close" style="display:none;">取消</span></a>'
											+ ' </td>'
										    + '</tr>';
									
											 $("tbody#result_list").append(html);
											 
											 $("."+item.group+"_f").val(item.rebate1.toFixed(2));
											 $("."+item.group+"_o").val(item.rebate2.toFixed(2));
											 $("."+item.group+"_m").val(item.rebate3.toFixed(2));
											 $("#"+item.group+"1").text(item.rate1*100+"%");
											 $("#"+item.group+"2").text(item.rate2*100+"%");
											 $("#"+item.group+"3").text(item.rate3*100+"%");
											 $("#"+item.group+"4").text(item.rate4*100+"%");
											 $("#"+item.group+"5").text(item.rate5*100+"%");
											 $("#"+item.group+"6").text(item.rate6*100+"%");
											 $("."+item.group+"1").val(item.rate1.toFixed(2));
											 $("."+item.group+"2").val(item.rate2.toFixed(2));
											 $("."+item.group+"3").val(item.rate3.toFixed(2));
											 $("."+item.group+"4").val(item.rate4.toFixed(2));
											 $("."+item.group+"5").val(item.rate5.toFixed(2));
											 $("."+item.group+"6").val(item.rate6.toFixed(2));
								}
								

							}
						}
					});
} 

function open(group){
		$("."+group+"_span").hide();
		$("."+group+"_open").hide();
		$("."+group+"_save").show();
		$("."+group+"_close").show();
		$("."+group+"_select").show();
		$("."+group+"_input").attr("disabled",false);
		$("."+group+"_input").removeClass("rebate_input");
		$("."+group+"_input").addClass("rebate_input_active");
}
function updateRebate(group){
	 var rebate1=$("."+group+"_f").val();
	 var rebate2=$("."+group+"_o").val();
	 var rebate3=$("."+group+"_m").val();
	 var rate1=$("."+group+"1").val();
	 var rate2=$("."+group+"2").val();
	 var rate3= $("."+group+"3").val();
	 var rate4= $("."+group+"4").val();
	 var rate5=$("."+group+"5").val();
	 var rate6=$("."+group+"6").val();
	if(isNaN(rebate1)||isNaN(rebate2)||isNaN(rebate3)){
		alert("价格必须为数字");
		return;
	}
	$.jsonRPC.request("adminMT4Service.updateRebate",{
		params : [group,rebate1,rebate2,rebate3,rate1,rate2,rate3,rate4,rate5,rate6],
		success : function(result) {
			if(result){
				alert("分配方案成功");
			}else {
				alert("分配方案失败，请重新设置");
			}
			
			$("tbody#result_list").html("");
			refreshList();
			$("."+group+"_span").show();
			$("."+group+"_open").show();
			$("."+group+"_save").hide();
			$("."+group+"_select").hide();
			$("."+group+"_input").attr("disabled",true);
			$("."+group+"_input").removeClass("rebate_input_active");
			$("."+group+"_input").addClass("rebate_input");
		}
	});
	
}
function closeRebate(group){
	$("."+group+"_span").show();
	$("."+group+"_open").show();
	$("."+group+"_save").hide();
	$("."+group+"_close").hide();
	$("."+group+"_select").hide();
	$("."+group+"_input").attr("disabled",true);
	$("."+group+"_input").removeClass("rebate_input_active");
	$("."+group+"_input").addClass("rebate_input");
}