var length =0;
$(document).ready(function(){
	$(".clickAddUserPro").click(function add(){
		var a  = adds();
	});
	$(".uploadImg").click(function click() {
		if (length<7) {
			startUploads();
		}else {
			alert("最多可以上传7张图片!")
		}
	});

	$.jsonRPC.request("userService.getUser",{
		params:[],
		success:function(data){
			if (data!=null) {
				$(".email").val(data.email);
				$(".phone").val(data.mobile);
			}
		}
    });
	$.jsonRPC.request("userService.getproFileImage",{
		params:[],
		success:function(data){
			if (data!=null) {
				var img="";
				for (var i in data.list) {
					length++;//保证图片只能上传7张
					var item = data.list[i];
					img+='<a href="'+item.path+'"><img style="width:100px;height: 100px;"  alt="" src="'+item.path+'"> </a>'
					+'<a id="fenye_a" href="javascript:delImg('+item.id+');">删除</a>';
				}
				$(".imageShow").html(img);
			}	
		}
    });
	/*$.jsonRPC.request("userService.getBank",{
		params:[],
		success:function(data){
			if (data!=null) {
				$(".backtype").html(data.backCardType);//
				$(".backNo").html(data.accountNo);//
			}	
		}
    });*/
	//删除图片
	function delImg(id) {
		alert(id);
		$.jsonRPC.request("userService.delImg",{
			params:[id],
			success:function(data){
				if (data) {
					$.jsonRPC.request("userService.getproFileImage",{
						params:[],
						success:function(data){
							if (data!=null) {
								var img="";
								for (var i in data.list) {
									var item = data.list[i];
									img+='<a href="'+item.path+'"><img style="width:100px;height: 100px;"  alt="" src="'+item.path+'"> </a>'
									+'<a id="fenye_a" href="javascript:delImg('+item.id+');">删除</a>';
								}
								$(".imageShow").html(img);
							}	
						}
				    });
				}
			}
	    });
	}
});
function adds(){
	var name = $(".name").val();
	var ename = $(".ename").val();
	//下拉选项cardType
	var cardType = $("#cardType").val();
	var cardID = $(".cardID").val();
	//下拉居住地区countryCode
	var countryCode = $("#countryCode").val();
	var address = $(".address").val();
	var cname = $(".cname").val();//公司名称
	var hangye = $(".hangye").val();//行业
	var userPosition = $(".userPosition").val();//职位
	var yearsr = $(".yearsr").val();//年收入
	var backName = $(".backName").val();
	var backNo = $(".backNo").val();
	var cardholder_Name = $(".cardholderName").val();//持卡人姓名
	//国家地址：countryCode countryAdress
	var countryAdress = $("#countryAdress").val();
	$.jsonRPC.request("userService.saveUserProfiles",{
		params:[name,ename,cardType,cardID,countryCode,address,cname,hangye
		        ,yearsr,userPosition,backName,backNo,cardholder_Name
		        ,countryAdress],
		success:function(data){
			if (data!=null) {
				alet("添加成功!");
			}	
		}
    });
	return false;
}



















