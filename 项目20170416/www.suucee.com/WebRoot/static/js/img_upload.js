var pathImg3="";
var pathImgId="";
var spathImg3="";
var spathImgId="";
var pathSrc="";
var spathSrc="";
function startUpload() {
	if ($("#img_upload").val() != ""&& pathImgId.length<9 ) {
		ajaxFileUpload();
	}else if(pathImgId.length>=9){
		alert("最多可以上传三张照片!")
		 $("#img_upload").val("");
	}
	if ($("#back_idCard_img").val() != ""&& spathImgId.length<3 ) {
			ajaxFileUploadback();
		}else if (spathImgId.length>=3) {
			alert("最多可以上传一张照片!")
			 $("#back_idCard_img").val("");
		}
}

function ajaxFileUpload() {
	$.ajaxFileUpload({
		url : 'upload/image/userProfile.do', // 用于文件上传的服务器端请求地址
		secureuri : false, // 一般设置为false
		fileElementId : 'img_upload', // 文件上传空间的name属性 <input type="file"
		// id="file"
		dataType : 'HTML', // 返回值类型 一般设置为json
		success : function(data, status) // 服务器成功响应处理函数
		{
			alert(data);
			if (data == "") {
				alert("图片上传失败！");
			} else {
					dt1 = data.split("/");
					img_name = dt1[dt1.length - 1];
					dt2 = data.split(".");
					img_type = dt2[dt2.length - 1];
					switch (img_type) {
					case "jpg":
					case "jpeg":
						img_type = "image/jpeg";
						break;
					case "png":
						img_type = "image/png";
						break;
					case "gif":
						img_type = "image/gif";
						break;

					default:
						img_type = "";
						break;
					}
					 var obj = new Function("return" + data)();//转换后的JSON对象
					 pathImg3+='<img style="width:100px;" src="'+obj.src+'">';
					 $("#img_isok1").html(pathImg3);
					 pathSrc+=obj.src+"0";
					 pathImgId+=obj.id+",";
					 alert(pathSrc);
					 $("#img_upload").val("");
						 /*$.jsonRPC.request("adminImagesService.addImage",{
							params:[_id,data, img_name, img_type],
							success:function(data2){
						var result=data2.result;
					if (result) {
						var imgHtml = $(".imgView").html();
						imgHtml += "<a href='" + data+ "' target='_blank'><img src='" + data+ "'height='100px'></a>";
						$(".imgView").html(imgHtml);
						alert("图片上传成功!");
					}
				}
			});*/
			}
			if (typeof (data.error) != 'undefined') {
				if (data.error != '') {
					alert(data.error);
				} else {
					alert(data.msg);
				}
			}
		},
		error : function(data, status, e)// 服务器响应失败处理函数
		{
			alert(e);
		}
	});

	return false;
}
//上传银行卡
function ajaxFileUploadback() {
	$.ajaxFileUpload({
		url : 'upload/image/userProfile.do', // 用于文件上传的服务器端请求地址
		secureuri : false, // 一般设置为false
		fileElementId : 'back_idCard_img', // 文件上传空间的name属性 <input type="file"
		// id="file"
		dataType : 'HTML', // 返回值类型 一般设置为json
		success : function(data, status) // 服务器成功响应处理函数
		{
			alert(data);
			if (data == "") {
				alert("图片上传失败！");
			} else {
					dt1 = data.split("/");
					img_name = dt1[dt1.length - 1];
					dt2 = data.split(".");
					img_type = dt2[dt2.length - 1];
					switch (img_type) {
					case "jpg":
					case "jpeg":
						img_type = "image/jpeg";
						break;
					case "png":
						img_type = "image/png";
						break;
					case "gif":
						img_type = "image/gif";
						break;

					default:
						img_type = "";
						break;
					}
					 var obj = new Function("return" + data)();//转换后的JSON对象
						 spathImg3+='<img style="width:100px;" src="'+obj.src+'">';
						 spathImgId+=obj.id;
						 spathSrc+=obj.src;
						 $("#img_back").html(spathImg3);
						alert(spathImgId);
						 $("#img_back").val("");
						 
			}
			if (typeof (data.error) != 'undefined') {
				if (data.error != '') {
					alert(data.error);
				} else {
					alert(data.msg);
				}
			}
		},
		error : function(data, status, e)// 服务器响应失败处理函数
		{
			alert(e);
		}
	});

	return false;
}