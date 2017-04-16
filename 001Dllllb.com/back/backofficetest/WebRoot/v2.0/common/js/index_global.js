var loginUser = null;
var filename = "";
var module = "";
var column = "";
var headerRootUrl = "";
var contentRootUrl = "";
var page = "";
var windowCallback = null;
var showContentFileName="";
var showDetailFileName="";
$(document).ready(
		function() {
			
			var hash = window.location.hash;
			page = hash.substring(1);
			$("#show_content").height($(".content").height());
			$("#show_detail").height($(".content").height());
			$(window).resize(function() {
				$("#show_content").height($(".content").height());
				$("#show_detail").height($(".content").height());
				
				
			//**************************侧边栏自适应********************************************
				if($(".search_contain")==undefined||$(".search_contain")==null){
					var bodyHeight = $(".container").height();
			
					switch (cc) {
					case "User":
						$(".type").hide();
						$(".my_customer").show();
						break;
					default:
						$(".type").show();
						$(".my_customer").hide();
						break;
					}
			
					var search_containOffsetTop = $(".search_contain").offset().top;
			
					var search_containHeight = bodyHeight - search_containOffsetTop;
					$(".search_contain").css("height", search_containHeight + "px");
				}
			//********************************************************************************************
			});

			if (filename == "login.html" || filename == "register.html"
					|| filename == "forgot_password.html") {

			} else {

				// 补登录
				if (loginUser != null) {
					initFramework();
				} else {
					$.jsonRPC.request('sessionService.checkLogined', {
						params : [],
						success : function(result) {
							loginUser = result;
							if (loginUser != null) {
								locStorage("loginUser", loginUser);
								initFramework();
							} else {
								relogin();
							}
						},
						error : function(e) {
							relogin();
						}
					});
				}
			}

			$.jsonRPC.request('user2Service.getUserLogin', {
				params : [],
				success : function(result) {
					$(".login-name").text("姓名：" + result.map.name);
					$(".login-id").text("ID：" + result.map.id);
				}
			});
			
			$(".admin-changepwd").click(function(){
				adminChangePassword();
			});
			
			
			$(".login-logout").click(function() {
				logout();
			});
			$("#show_content").load(function() {
				var win = $(this)[0].contentWindow;
				var strUrl = win.location.href;
				var arrUrl = strUrl.split("/");
				var filename = arrUrl[arrUrl.length - 1];
				var path = arrUrl[arrUrl.length - 1];
				if (filename.indexOf("?") >= 0) {
					filename = filename.substr(0, filename.indexOf("?"));
				}
				if (filename.indexOf("#") >= 0) {
					filename = filename.substr(0, filename.indexOf("#"));
				}
				showContentFileName=filename;
				var moduleAndColume = filename.replace(".html", "");
				moduleAndColume = moduleAndColume.split("_");
				if (typeof (moduleAndColume[1]) != "undefined") {
					column = moduleAndColume[1];
				}
				module = moduleAndColume[0];
				$(".frist-menu > .item").removeClass('active');
				$(".frist-menu > .item").each(function(index, element) {
					if ($(element).data("module") == module) {
						$(element).addClass('active');
					}
				});
				location.href = "#" + win.location.href;
			});
			$("#show_detail").load(function() {
				var win = $(this)[0].contentWindow;
				var strUrl = win.location.href;
				var arrUrl = strUrl.split("/");
				var filename = arrUrl[arrUrl.length - 1];
				var path = arrUrl[arrUrl.length - 1];
				if (filename.indexOf("?") >= 0) {
					filename = filename.substr(0, filename.indexOf("?"));
				}
				if (filename.indexOf("#") >= 0) {
					filename = filename.substr(0, filename.indexOf("#"));
				}
				showDetailFileName=filename;
			});
			
			var hold = getParam("hold",false);
			if(hold || hold == "true"){
//				layer.alert("温馨提示：<br/>1、在使用“代管模式”中，勿随意关闭窗口。<br/>2、一定要保证是<span style='color: red;font-weight: bold;'>正常登出（点击右上角的退出）</span>。");
				var tipHtml = '<div class="tip" style="text-align: center;height: 30px;background-color: #618590;color:#de942a;line-height: 30px;font-weight: bold;">';
				var spanHtml = '<span>温馨提示：在使用“代管模式”中，勿随意关闭窗口（一定不能直接关闭窗口）。 一定要保证正常登出（点击退出按钮）。</span>'
				tipHtml += spanHtml;
				tipHtml += '</div>';
				$(".header").parents("body").prepend(tipHtml);
				
				
				$(window).unload(function(){
					holdLogout();
					setTimeout(function(){
					},1000);
				});
			}
	});


function locStorage(key, value) {
	if (window.localStorage) {
		if (arguments.length == 1) {
			try {
				var value = JSON.parse(window.localStorage
						.getItem(arguments[0]));
				return value;
			} catch (e) {
				layer.alert(e);
				return null;
			}
		} else {
			if (arguments.length == 2) {
				window.localStorage.removeItem(arguments[0]);

				if (arguments[1] !== null) {
					return window.localStorage.setItem(arguments[0], JSON
							.stringify(arguments[1]));
				}
			}
		}
	} else {
		layer.alert('不支持localStorage');
	}
	return null;
}

function initShowInfo() {
	$(".user_tree").load("../common/showinfo.html", function() {
		
		//获取此用户的父级员工信息，设置在左侧导航栏（注意，父级有可能是代理，所以要筛选出员工信息来）
		$.jsonRPC.request('user2Service.getParentStuffInfo', {
			params : [],
			success : function(result) {
				if (result) {
					$(".parent_name").html(result.map.profiles.userName);
					$(".parent_name").attr("title",result.map.profiles.userName);
					
					$(".parent_mobile").html(result.map.users.mobile);
					$(".parent_mobile").attr("title",result.map.users.mobile);
					
					$(".parent_email").html(result.map.users.email);
					$(".parent_email").attr("title",result.map.users.email);
				}
			}
		});
	});
}

function initTree() {
	$(".user_tree").load("../common/tree.html", function() {
		var bodyHeight = $(".container").height();

		switch (loginUser.role) {
		case "User":
			$(".type").hide();
			$(".my_customer").show();
			break;
		default:
			$(".type").show();
			$(".my_customer").hide();
			break;
		}

		var search_containOffsetTop = $(".search_contain").offset().top;

		var search_containHeight = bodyHeight - search_containOffsetTop;
		$(".search_contain").css("height", search_containHeight + "px");

		getTreeData("MY");
		$("#text_search_customer").keypress(function(e) {
			if (e.which == 13) {
				search(null);
			}
		});
		
		$(".sideTool").click(function() {
			if (!canBroaden) {
				$(".sideTool i").attr("title", "拓宽客户关系树");
				$(".search_contain").animate({
					"width" : "100%"
				}, 300, "linear", function() {
					canBroaden = true;
				});
			} else {
				$(".sideTool i").attr("title", "收起客户关系树");
				$(".search_contain").animate({
					"width" : "180%"
				}, 300, "linear", function() {
					canBroaden = false;
				});
			}
		});
	});
}

function initFramework() {
	var content_url = "";
	switch (loginUser.role) {
	case "User":
		if (loginUser.level <= 3) {
			initTree();
			headerRootUrl = "../staff/";
		} else if (loginUser.vipGrade > 0) {
			initTree();
			headerRootUrl = "../agent/";
		} else {
			initShowInfo();
			headerRootUrl = "../user/";
		}
		contentRootUrl = "../user/";
		content_url = contentRootUrl + "start_index.html";
		break;
	case "Webmaster":
		location.herf="../../cms/index.html";
		break;
	default:
		$(".admin-changepwd").show();
		initTree();
		headerRootUrl = "../admin/";
		contentRootUrl = "../admin/";
		content_url = contentRootUrl + "manager_index.html";
		break;
	}
	if (page == "") {
		$("#show_content").attr("src", content_url);
	} else {
		$("#show_content").attr("src", page);
	}
	// 载header
	$(".menu").html("");
	var header_url = "";
	switch (loginUser.role) {
	case "User":
		if (loginUser.level <= 3) {
			header_url = headerRootUrl + "_menu_staff.html";
		} else if (loginUser.vipGrade > 0) {
			header_url = headerRootUrl + "_menu_agent.html";
		} else {
			header_url = headerRootUrl + "_menu_user.html";
		}
		break;
	default:
		header_url = headerRootUrl + "_menu"
				+ (loginUser.role.replace(/([A-Z]{1})/g, "_$1").toLowerCase())
				+ '.html';
		break;
	}
	$.get(header_url, null, function(data) {
		$(".menu").html(data);
		$(".frist-menu > .item").click(
				function() {
					showContent();
					var selectdModule = $(this).data("module");
					findModuleLocation(selectdModule);
					$("#show_content").attr("src",
							contentRootUrl + selectdModule + "_index.html");
				});
	});
}

function findModuleLocation(module) {
	switch (module) {
	case "customer":
		contentRootUrl = "../agent/";
		break;
	case "start":
	case "info":
	case "webaccount":
	case "mt4account":
	case "public":
		contentRootUrl = "../user/";
		break;
	case "manager":
	case "compliance":
	case "user":
	case "agent":
	case "finance":
	case "agent":
	case "mt4s":
	case "risk":
	case "rebate":
	case "report":
	case "website":
	case "setting":
		contentRootUrl = "../admin/";
		break;
	default:
		contentRootUrl = "../user/";
	}
}

// 用户树点击事件
callback = function(id,isAgent) {
	//如果是代理——显示这个代理的信息和他所有的下级客户
	//如果是客户，直接显示他的个人信息
	
	showContent();
	switch (loginUser.role) {
	case "User":
		if (loginUser.level <= 3) {
			$("#show_content").attr("src","./../agent/customer_user_detail.html?id=" + id);
		} else if (loginUser.vipGrade > 0) {
			$("#show_content").attr("src","./../agent/customer_user_detail.html?id=" + id);
		} else {
		}
		break;
	default:
		if(isAgent){
			$("#show_content").attr("src", "./../admin/see_agent.html?id=" + id);
		}else{
			$("#show_content").attr("src", "./../admin/see_profile.html?id=" + id);
		}
		break;
	}
};


function adminChangePassword(){
	parent.openNewWindow("../admin/admin_changepassword.html");
}



function logout() {
	$.jsonRPC.request('session2Service.logout', {
		params : [],
		success : function(result) {
			window.localStorage.clear();
			delCookie("TOKEN");
			if (result) {// 代管退出Success
				locStorage("loginUser", result);
				setCookie('TOKEN', result.token, 30 * 24);
				//有可能在代管下，回到管理员界面继续使用退出功能
				if(location.href.indexOf("see_profile") != -1){
					layer.alert("退出成功！<br/>由于您代管的用户没有正常退出，此次登出的是您刚刚代管的用户，目前您仍然处于登录状态。<br/>如要退出当前管理员，请继续点击退出按钮。");
				}else{
					window.close();
				}
			} else {
				window.open('../common/login.html?holder=true', '_self');
			}
		}
	});
}

function openNewWindow(url, windowcallbcak) {
	windowCallback = windowcallbcak;
	 $("#show_content").css("display", "none");
	 $("#show_detail").css("display", "block");
	$("#show_detail").attr("src", url);
}

function closeWindow() {
	if (windowCallback != null) {
		windowCallback.call();
	}
	if(showContentFileName!="see_profile.html"){
	    localById(0);
	}
	$("#show_detail").css("display", "none");
	$("#show_content").css("display", "block");
}
function closeWindowToPage(url) {
	localById(0);
	$("#show_detail").css("display", "none");
	$("#show_content").css("display", "block");
	if(url!=""){
		$("#show_content").attr("src", url);
	}
}
function showContent() {
	$("#show_detail").css("display", "none");
	$("#show_content").css("display", "none");
	$("#show_content").show();
}

function clearNoNum(obj) {
	obj.value = obj.value.replace(/[^\d.]/g, "");
	obj.value = obj.value.replace(/^\./g, "");
	obj.value = obj.value.replace(/\.{2,}/g, ".");
	obj.value = obj.value.replace(".", "$#$").replace(/\./g, "").replace("$#$",
			".");
}

function setCookie(name, value, hours) {
	var exp = new Date();
	// new Date("December 31, 9998");
	exp.setTime(exp.getTime() + hours * 60 * 60 * 1000);
	document.cookie = name + "=" + escape(value) + ";path=/;expires="
			+ exp.toGMTString();
}

function getCookie(name) {
	var arr = document.cookie
			.match(new RegExp("(^| )" + name + "=([^;]*)(;|$)"));
	if (arr != null)
		return unescape(arr[2]);
	return null;
}

function delCookie(name) {
	var exp = new Date();
	exp.setTime(exp.getTime() - 1);
	var cval = getCookie(name);
	if (cval != null)
		document.cookie = name + "=" + cval + ";path=/;expires="
				+ exp.toGMTString();
}

Date.prototype.format = function(formatStr) {
	var date = this;
	var zeroize = function(value, length) {
		if (!length) {
			length = 2;
		}
		value = new String(value);
		for (var i = 0, zeros = ''; i < (length - value.length); i++) {
			zeros += '0';
		}
		return zeros + value;
	};
	return formatStr
			.replace(
					/"[^"]*"|'[^']*'|\b(?:d{1,4}|M{1,4}|yy(?:yy)?|([hHmstT])\1?|[lLZ])\b/g,
					function($0) {
						switch ($0) {
						case 'd':
							return date.getDate();
						case 'dd':
							return zeroize(date.getDate());
						case 'ddd':
							return [ 'Sun', 'Mon', 'Tue', 'Wed', 'Thr', 'Fri',
									'Sat' ][date.getDay()];
						case 'dddd':
							return [ 'Sunday', 'Monday', 'Tuesday',
									'Wednesday', 'Thursday', 'Friday',
									'Saturday' ][date.getDay()];
						case 'M':
							return date.getMonth() + 1;
						case 'MM':
							return zeroize(date.getMonth() + 1);
						case 'MMM':
							return [ 'Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun',
									'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec' ][date
									.getMonth()];
						case 'MMMM':
							return [ 'January', 'February', 'March', 'April',
									'May', 'June', 'July', 'August',
									'September', 'October', 'November',
									'December' ][date.getMonth()];
						case 'yy':
							return new String(date.getFullYear()).substr(2);
						case 'yyyy':
							return date.getFullYear();
						case 'h':
							return date.getHours() % 12 || 12;
						case 'hh':
							return zeroize(date.getHours() % 12 || 12);
						case 'H':
							return date.getHours();
						case 'HH':
							return zeroize(date.getHours());
						case 'm':
							return date.getMinutes();
						case 'mm':
							return zeroize(date.getMinutes());
						case 's':
							return date.getSeconds();
						case 'ss':
							return zeroize(date.getSeconds());
						case 'l':
							return date.getMilliseconds();
						case 'll':
							return zeroize(date.getMilliseconds());
						case 'tt':
							return date.getHours() < 12 ? 'am' : 'pm';
						case 'TT':
							return date.getHours() < 12 ? 'AM' : 'PM';
						}
					});
}
function toDate(v) {
	var ret = new Date();
	ret.setTime(v);
	return ret.format("yyyy-MM-dd HH:mm:ss");
}

function toDate0(v) {
	var ret = new Date();
	ret.setTime(v - 8 * 60 * 60 * 1000);
	return ret.format("yyyy-MM-dd HH:mm:ss");
}

function showException(e) {
	if (e.message) {
		switch (e.message) {
		case 'LANG_USER_FROZEN':
			layer.alert(LANG_USER_FROZEN);
			break;
		case 'LANG_USER_LIMITED':
			layer.alert(LANG_USER_LIMITED);
			break;
		case 'LANG_USER_EMAIL_UNVERIFIED':
			layer.alert(LANG_USER_EMAIL_UNVERIFIED);
			break;
		default:
			layer.alert(e.message);
		}
	} else {
		layer.alert(e);
	}
}

function trim(str) {
	return str.replace(/(^s*)|(s*$)/g, "");
}

function getParam(name, default_value) {
	var result = location.search.match(new RegExp(
			"[\?\&]" + name + "=([^\&]+)", "i"));
	if (result == null || result.length < 1) {
		return default_value;
	}
	return result[1];
}

function relogin() {
	if (loginUser != null) {
		var l = eval(loginUser);

		$.jsonRPC.request('sessionService.login', {
			params : [ token ],
			success : function(result) {
				locStorage("loginUser", loginUser);
			}
		});
	} else {
		locStorage("loginUser", loginUser);
		window.open("../common/login.html", "_self");
	}
}


function localUserById(id){
	localById(id);
}
