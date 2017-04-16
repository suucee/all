var authorizationCode = "";
const LANG_ADD_BANK_ACCOUNT = '添加银行卡';
const LANG_EDIT_BANK_ACCOUNT = '编辑银行卡';
var isAllow=true;
var show = false;
var urlroot="/upload"
$(document).ready(function(){
	
	$("#detail_div").hide();
	
	
	//先检查用户的资料状态，待用户资料完成后才可以审核银行卡
	getUserEmailAndName();
	
	refreshList();
	
	$("select[name='countryCode']").change(function(){
		var code = $(this).val();
		if (code == "HK") {
			$(".hk_show").slideDown(200);
		} else {
			$(".hk_show").slideUp(200);
		}
	    if($("select[name='countryCode']").val()=="CN"){
	    	$(".home").show();
	    	$(".abroad").hide();
	    }else{
	    	$(".home").hide();
	    	$(".abroad").show();
	    }
	});
});

function refreshList() {
		$.jsonRPC.request("userAccountService.getBankAccountList",{
			params:[],
			success:function(result){
		if (result != null) {
			var html = '';
			
			for (var i in result.list) {
				var item = result.list[i];

				var label_class="";
				var label_name="";
				switch (item.state) {
				case 'WAITING':
					label_class = 'label-pending';
					label_name = '待审核';
					break;
				case 'AUDITED':
					label_class = 'label-success';
					label_name = '已通过';
					break;
				case 'REJECTED':
					label_class = 'label-important';
					label_name = '被驳回';
					break;
				default:	
					label_class = 'label-pending';
					label_name = '无状态';
					break;
				}
				
				html += '<tr>' 
					+ '	<td><span class="label '+label_class+'">'+label_name+'</span></td>'
					+ '	<td>'+/*(item.default ? '<font color="#b94a48">[绑定]</font>':'')+*/item.bankName+'</td>'
					+ '	<td>'+item.accountName+'</td>'
					+ '	<td>'+item.accountNo+'</td>'
					+ '	<td>'+item.countryCode+'</td>'
					+ '	<td class="actions_icons" style="text-align: center;">'
				//	+ (item.default ? '-':
					+(item.state=="AUDITED"?'<a class="btn btn-a  margin-right15"  title="查看银行卡信息" href="javascript:editAccount('+item.id+',true);"><i class="fa fa-eye" aria-hidden="true"></i></a>':
						   					'<a class="btn btn-a  margin-right15"  title="查看/编辑银行卡信息" href="javascript:editAccount('+item.id+',false);"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>')
					+ '		<a class="btn btn-b" href="javascript:deleteAccount('+item.id+');" title="删除该银行卡"><i class="fa fa-trash" aria-hidden="true"></i></a>'
					+'</td>'
					+ '</tr>';
			}
			
				if (html=="") {
					html += '<tr><td colspan="10" class="bold">对不起，您还未添加银行卡。</td></tr>';
				};
			$("#bankAccountList").html(html);
		}
	 }
	});
}

function deleteAccount(id) {
	if (authorizationCode == "" && loginUser.securityPolicy > 0) {
		openSecurityConfirm();
		return;
	}
	
	layer.confirm("您确定要删除该银行账户吗?",function(){
		$.jsonRPC.request("userAccountService.deleteBankAccount",{
			params:[id],
			success:function(result){
				if (result) {
					layer.msg("删除银行账户成功",{time:1000,icon:1});
					refreshList();
					hide();
				} else {
					layer.msg("删除银行账户失败",{time:2000,icon:2});
				}
			}
		});
	});
		
}


function addAccount() {
	show = false;
	$("#detail_div").hide(150);
	readAndEditFun();
	$("#detail_div").show(300,function(){
		$(":input[name='bankName']").focus();
	});
	
	$(".bankstate").text("添加银行卡中...");
	$(".bankstate").attr("class","bankstate label label-success");
	$("input[name='id']").val("");
	$("input[name='bankName']").val("");
	$("input[name='accountName']").val("");
	$("input[name='accountNo']").val("");
	$("select[name='countryCode']").val("CN");
	$("select[name='countryCode']").change();
    if($("select[name='countryCode']").val()=="CN"){
    	$(".home").show();
    	$(".abroad").hide();
    }else{
    	$(".home").hide();
    	$(".abroad").show();
    }
	$("input[name='swiftCode']").val("");
	$("input[name='ibanCode']").val("");
	$("input[name='bankBranch']").val("");
	$("input[name='bankAddress']").val("");	
	$("#attachment_id").val("");
	$("#bankfile").val("");	
	$("#imgView").html("");
}

/**
 * 
 * @param id
 * @param readOnly是否是只读的（不可更改）
 */
function editAccount(id,readOnly) {
	$("#detail_div").hide(150);
	
	$("#detail_div").show(150);
	$("#btn_back").html("返回");
	
	
	$("#detail_div input").change(function(){
		$("#btn_submit").show(200);
	});
	
	
	if (authorizationCode == "" && loginUser.securityPolicy > 0) {
		openSecurityConfirm();
		return;
	}
	$("#imgView").html("");
	$("#account_form_container").fadeIn(200);
	$(".form_title").text(LANG_EDIT_BANK_ACCOUNT);
		$.jsonRPC.request("userAccountService.getBankAccount",{
			params:[id],
			success:function(result){
				if (result) {
					
					//show
					var label_class="";
					var label_name="";
					switch (result.state) {
					case 'WAITING':
						label_class = 'label-pending';
						label_name = '待审核';
						break;
					case 'AUDITED':
						label_class = 'label-success';
						label_name = '通过审核，（银行卡已经通过审核，更改信息请联系客服）。';
						break;
					case 'REJECTED':
						label_class = 'label-important';
						label_name = '已驳回， 驳回理由：'+result.bankComment;
						break;
					default:	
						label_class = 'label-pending';
						label_name = '无状态';
						break;
					}
					$(".bankstate").attr("class","bankstate label "+label_class);
					
					$(".bankstate").text(label_name);
					
					
					
					$("input[name='id']").val(result.id);
					$("input[name='bankName']").val(result.bankName);
					$("input[name='accountName']").val(result.accountName);
					$("input[name='accountNo']").val(result.accountNo);
					$("select[name='countryCode']").val(result.countryCode);
					$("select[name='countryCode']").change();
				    if($("select[name='countryCode']").val()=="CN"){
				    	$(".home").show();
				    	$(".abroad").hide();
				    }else{
				    	$(".home").hide();
				    	$(".abroad").show();
				    }
					$("input[name='swiftCode']").val(result.swiftCode);
					$("input[name='ibanCode']").val(result.ibanCode);
					$("input[name='bankBranch']").val(result.bankBranch);
					$("input[name='bankAddress']").val(result.bankAddress);	
					$("#attachment_id").val("");
					$("#bankfile").val("");		
					if(readOnly){
						readOnlyFun();
					}
				} else {
					layer.msg("获取银行卡信息失败",{time:2000,icon:2});
				}	
			}
	});
	$.jsonRPC.request("userAccountService.getBankImageList",{
		params:[id],
		success:function(result){
			var list=result.list;
			var html="";
			for(var i=0;i<list.length;i++){
				var item=list[i];
		        html+="<div class='thisimage' data-attachment="+item.id+" style='width:250px;height:150px;text-align:center;margin:5px;float:left;overflow: hidden;'>";
				html+="<div style='background-image: url(/upload"+item.path+");background-position: center;background-size:auto 100%;background-repeat: no-repeat;width: 250px;height: 150px;line-height: 150px;margin-bottom:5px;'>";
				html+="<div class='width: 250px;height: 150px;line-height: 150px;'>";
				html+="<a style=''  onclick='deleteBankFile(this)'  class='btn btn-e btn-remove'><i class='fa fa-remove' aria-hidden='true'></i>&nbsp;&nbsp;移除</a>&nbsp;&nbsp;";
				html+="<a style='' href='/upload"+item.path+"' target='_blank'  class='btn btn-a'><i class='fa fa-eye' aria-hidden='true'></i>&nbsp;&nbsp;查看</a>";
				html+="</div>";
				html+="</div>";
				html+="</div>";	
			}
			$("#imgView").html(html);
			if(readOnly){
				readOnlyFun();
			}
		}
	});
		
}


function submitForm() {
		var id = $("input[name='id']").val();
		var bankName = $("input[name='bankName']").val();
		var accountName = $("input[name='accountName']").val();
		var accountNo = $("input[name='accountNo']").val();
		var countryCode = $("select[name='countryCode']").val();

		 var swiftCode = $("input[name='swiftCode']").val();
		 var ibanCode = $("input[name='ibanCode']").val();
		 var bankBranch = $("input[name='bankBranch']").val();
	     var bankAddress = $("input[name='bankAddress']").val();
	     var attachment_id = $("input[name='attachment_id']").val();
		
		if (bankName == '') {
			layer.tips("银行名称不能为空.","input[name='bankName']",{time:2000,tips:[2,'#56a787']});
			$("input[name='bankName']").focus();
		} else if (accountName == '') {
			layer.tips("账户名称不能为空.","input[name='accountName']",{time:2000,tips:[2,'#56a787']});
			$("input[name='accountName']").focus();
		} else if (accountNo == '') {
			layer.tips("账户号码不能为空.","input[name='accountNo']",{time:2000,tips:[2,'#56a787']});
			$("input[name='accountNo']").focus();
		} else if (countryCode == '') {
			layer.tips("地区不能为空 .","select[name='countryCode']",{time:2000,tips:[2,'#56a787']});
			$("select[name='countryCode']").focus();
		} else if(bankBranch == ''){
			layer.tips("分行名称不能为空 .","input[name='bankBranch']",{time:2000,tips:[2,'#56a787']});
			$("input[name='bankBranch']").focus();
		}
//		else if(bankAddress == ''){
//			layer.tips("分行地址不能为空 .","input[name='bankAddress']",{time:2000,tips:[2,'#56a787']});
//			$("input[name='bankAddress']").focus();
//		}
		
		else if($("#imgView > div").length < 1){
			layer.tips("请上传最少1张银行卡证明图片，以便后台人员审核您的银行卡",".btn-file",{time:2000,tips:[2,'#56a787']});
			$(".btn-file").focus();return;
		}else {
			
			if (id > 0) {
				$.jsonRPC.request("user2AccountService.editBankAccount",{
					params:[id, bankName,accountName, accountNo, countryCode,swiftCode,ibanCode,bankBranch,bankAddress],
					success:function(result){
				if (result) {
					layer.msg("银行卡账户信息更改成功",{time:1000,icon:1});
					hide();
					refreshList();
				} else {
					layer.msg("银行卡账户信息更改失败",{time:2000,icon:2});
				}
					}
				});
			} else {
				$.jsonRPC.request("userAccountService.addBankAccount",{
					params:[ bankName,  accountName,accountNo, countryCode,swiftCode,ibanCode,bankBranch,bankAddress,attachment_id],
					success:function(result){
				
				if (result) {
					layer.msg("银行卡账户添加成功",{time:1000,icon:1});
					hide();
					refreshList();
				} else {
					layer.msg("银行卡账户添加失败",{time:2000,icon:2});
				}
					
				}
				});
		
           }
		}
}

function uploadBankFile() {
	if(isAllow){
		isAllow=false;
		var id=0;
		if($("input[name='id']").val()!=""){
		   id=$("input[name='id']").val();	
		}
		$.ajaxFileUpload({
			url : '../../fileupload/bankFile-'+id+'.do', // 用于文件上传的服务器端请求地址
			secureuri : false, // 一般设置为false
			fileElementId : 'bankfile', // 文件上传空间的name属性 <input type="file"
			// id="file"
			dataType : 'json', // 返回值类型 一般设置为json
			success : function(data, status) // 服务器成功响应处理函数
			{
				if (data.status == "failed") {
					layer.msg(data.result,{time:2000});
				} else {
					if($("#attachment_id").val()==""){
						$("#attachment_id").val(data['id']);	
					}else{
						$("#attachment_id").val($("#attachment_id").val()+","+data['id']);	
					}
					layer.msg("上传成功！",{time:1000,icon:1});

		        var html="<div class='thisimage' data-attachment="+data.id+" style='width:250px;height:150px;text-align:center;margin:5px;float:left;overflow: hidden;'>";
				html+="<div style='background-image: url(/upload"+data.src+");background-position: center;background-size:auto 100%;background-repeat: no-repeat;width: 250px;height: 150px;line-height: 150px;margin-bottom:5px;'>";
				html+="<div class='width: 250px;height: 150px;line-height: 150px;'>";
				html+="<a style='' onclick='deleteBankFile(this)'  class='btn btn-e btn-remove'><i class='fa fa-remove' aria-hidden='true'></i>&nbsp;&nbsp;移除</a>&nbsp;&nbsp;";
				html+="<a style='' href='/upload"+data.src+"' target='_blank'  class='btn btn-a'><i class='fa fa-eye' aria-hidden='true'></i>&nbsp;&nbsp;查看</a>";
				html+="</div>";
				html+="</div>";
				html+="</div>";	
				$("#imgView").append(html);
				}
				isAllow = true;
			},
			error : function(data, status, e)// 服务器响应失败处理函数
			{
				layer.msg("文件上传失败",{time:2000,icon:2});
				isAllow = true;
			}
		});
	}else{
		 layer.msg("文件正在上传中.",{time:2000,icon:2});
	}
}

function deleteBankFile(obj){
		  var $thisimg=$(obj).parents(".thisimage");
		  var id=$thisimg.data("attachment");
		 $.jsonRPC.request("userAccountService.deleteBankImage",{
			 params:[id],
			 success:function(result){
				 if(result){
					$thisimg.remove();
				 }else{
					 layer.msg("删除失败",{time:2000,icon:2});
				 }
			 }
		 });
	
}

function hide(){
	$("#detail_div").hide(300,function(){
		$(".container").focus();
		document.body.scrollTop = document.documentElement.scrollTop = 0;//回到顶部
	});
}

function readOnlyFun(){
	$("#detail_div input").attr("disabled","disabled");
	$("select").attr("disabled","disabled");
	$("#detail_div input").attr("title","银行卡已经通过审核，不可更改信息了。");
	$(".btn-remove").hide(300);
	$("#fileUpTr").hide(300);
	$("#buts").hide(300);
}

function readAndEditFun(){
	$("#detail_div input").removeAttr("disabled");
	$("select").removeAttr("disabled");
	$("#detail_div input").removeAttr("title");
	$("#fileUpTr").show(300);
	$("#buts").show(300);
}


function getUserEmailAndName(){
	//获取详细信息
	$.jsonRPC.request("user2Service.getUserEmailAndName",{
		params:[],
		success:function(result){
			if (result.map.name == null || result.map.name == "" || result.map.email == null || result.map.name == "") {
				layer.alert("对不起，由于您的基本资料还未完善，无法提交银行卡的审核，请先完善您的基础资料。",function(){
					window.location.href="profile_index.html";
				});
			}
		}
	});
}
