
function startUpload() {
	if ($("#img_upload").val() != "" && _id > 0) {
		ajaxFileUpload();
	}

}

function ajaxFileUpload() {
	$.ajaxFileUpload({
		url : '../fileupload/cmsImg.do', // 用于文件上传的服务器端请求地址
		secureuri : false, // 一般设置为false
		fileElementId : 'img_upload', // 文件上传空间的id属性 <input type="file"
		// id="file"
		dataType : 'HTML', // 返回值类型 一般设置为json
		success : function(data, status) // 服务器成功响应处理函数
		{
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
					$.jsonRPC.request("adminImagesService.addImage",{
							params:[_id,data, img_name, img_type],
							success:function(result){
					if (result) {
						var imgHtml = $(".imgView").html();
						imgHtml += "<a href='/upload/" + data
								+ "' target='_blank'><img src='/upload/" + data
								+ "'height='100px'></a>";
						$(".imgView").html(imgHtml);
						alert("图片上传成功!");
						location.reload();//刷新一下页面，用来显示remove按钮和隐藏上传按钮。
					}
				}
			});
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
function ajaxUserProfileUpload() {
	
	var imgUpload=$("#img_upload").val();
	if(imgUpload==""){
		alert("请选择图片");
		return;
	}
	$(".imgStartUpload").text("上传中..");
	$(".imgStartUpload").attr("href","javascript:void(0);");
	$.jsonRPC.request("userService.getproFileImage",{
		params:[],
		success:function(result){
			if(result!=null&&result.list.length>=7){
				alert("上传图片已达上限");
				$(".imgStartUpload").text("上传");
				$(".imgStartUpload").attr("href","javascript:ajaxUserProfileUpload();");
			}else {
				
				$.ajaxFileUpload({
					url : '../fileupload/userProfile.do', // 用于文件上传的服务器端请求地址
					secureuri : false, // 一般设置为false
					fileElementId : 'img_upload', // 文件上传空间的id属性 <input type="file"
					// id="file"
					dataType : 'HTML', // 返回值类型 一般设置为json
					success : function(data, status) // 服务器成功响应处理函数
					{
						$(".imgStartUpload").text("上传");
						$(".imgStartUpload").attr("href","javascript:ajaxUserProfileUpload();");
						if (data) {
							alert("图片上传成功");
							getImg();
						} else {
							alert("图片上传失败");
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
		}
		});
	
}
