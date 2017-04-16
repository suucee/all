var loginUser = null;
var filename = "";

$(document).ready(function(){
	//check logined
		var strUrl = window.location.href;
		var arrUrl = strUrl.split("/");
		var filename = arrUrl[arrUrl.length-1];
		var path = arrUrl[arrUrl.length-1];
		if (filename.indexOf("?") >= 0) {
			filename = filename.substr(0, filename.indexOf("?"));
		}
		if (filename.indexOf("#") >= 0) {
			filename = filename.substr(0, filename.indexOf("#"));
		}
		
		var pieces = filename.split("_");
		pieces[0] = pieces[0].replace(".html", "");
		var module = "";
		switch (pieces[0]) {
		case "website":
		case "link":
		case "article":
		case "question":
		case "column":
			module = "website";	//网站
			break;
			
		case "customer":
			module = "customer";	//客户
			break;
			
		case "user":
			module = "user";	//用户
			break;

		case "mt4":
			module = "mt4";	//MT4
			break;
		case "risk":
			module = "risk";	//Risk
			break;

		case "compliance":
			module = "compliance";	//合规
			break;

		case "finance":
			module = "finance";	//财务
			break;

		case "usermt4":
			module = "usermt4";	//
			break;
			
		case "report":
			module = "report";	//报表
			break;
			
		case "userreport":
			module = "userreport";	//报表
			break;
		
		case "setting":
		case "admin":
			module = "setting";	//设置
			break;
		case "agent":
			module = "agent";	//
			break;
		case "rebate":
			module = "rebate";	//
			break;
		
		default:
			module = "start";	//开始
		}

		if (filename == "login.html" ||
			filename == "register.html" ||
			filename == "forgot_password.html"
			)
		{
			
		} else {
			//检查登录
			loginUser = locStorage("loginUser");
			
			//补登录
			if (loginUser != null) {
				initFramework(module, path);
			} else {
				$.jsonRPC.request('sessionService.checkLogined', {
					params : [],
					success : function(result) {
						loginUser = result;
						
						locStorage("loginUser", loginUser);
							
						if (loginUser != null) {
							//Success
							initFramework(module, path);
						} else {
							if (window.localStorage) {
								window.localStorage.setItem("loginUser", JSON.stringify(loginUser));
							}
							window.open("login.html", "_self");
						}
					},
					error : function(e) {
						if (window.localStorage) {
							window.localStorage.setItem("loginUser", JSON.stringify(loginUser));
						}
						window.open("login.html", "_self");
					}
				});
			}
		}
});

function locStorage(key, value) {
    if (window.localStorage) {
        if (arguments.length == 1) {
        	try {
        		var value = JSON.parse(window.localStorage.getItem(arguments[0]));
        		return value;
        	} catch (e) {
        		window.alert(e);
        		return null;
        	}
        } else {
            if (arguments.length == 2) {
            	window.localStorage.removeItem(arguments[0]);
                
                if (arguments[1] !== null) {
                    return window.localStorage.setItem(arguments[0], JSON.stringify(arguments[1]));
                }
            }
        }
    } else {
        window.alert('不支持localStorage');
    }
    return null;
}


function initFramework(module, path) {
	//载header
	if ($(".header").html() == '') {
		$.get("_header" + (loginUser.role.replace(/([A-Z]{1})/g, "_$1").toLowerCase()) + '.html', null, function(data){
			$(".header").html(data);

			//当前模块高亮
			$(".header .nav_"+module).parent().addClass("current");
		});
	}
	//载sidenav
	$.get("_sidenav_"+module+(module == "start" ? (loginUser.role == "User" ? "_user" : "_admin") : "")+".html", null, function(data){
		$(".sidenav").html(data);
		$(".sidenav a[href='"+path+"']").parent().addClass("active");
		if (loginUser != null) {
			//显示名称
			$(".customer .customer_id").html(loginUser.name);
			//显示角色
			var role = '';
			switch (loginUser.role) {
			case 'User':
				switch (loginUser.level) {
				case 1:
					role = '公司';
					break;
				case 2:
					role = '经理';
					break;
				case 3:
					role = '员工';
					break;
				default:
					role = '客户';
					break;
				}
				break;
			case 'ComplianceOfficer':	//合规
				role = '合规';
				break;
			case 'FinancialStaff':	//财务
				role = '财务';
				break;
			case 'FinancialSuperior':	
				role = '财务主管';
				break;
			case 'CustomerServiceStaff':	
				role = '客服';
				break;
			case 'Webmaster':	
				role = '网站管理员';
				break;
			case 'OperationsManager':	
				role = '运维经理';
				break;
			case 'RiskManagementCommissioner':	
				role = '风控';
				break;
			}
			$(".customer .role").html(role + ' (ID:' + loginUser.id + ')');
		}
	});
	//载footer
	$(".footer").load("_footer.html");
}

function logout() {
	$.jsonRPC.request('sessionService.logout', {
		params : [],
		success : function(result) {
			window.localStorage.clear();
			delCookie("TOKEN");
			
			if (result) {	//Success
				locStorage("loginUser", result);

				setCookie('TOKEN', result.token, 30 * 24);
				window.open(result.redirectUrl, '_self');
			} else {
				window.open('login.html', '_self');
			}
		}
	});
}

function clearNoNum(obj)
{
    obj.value = obj.value.replace(/[^\d.]/g,"");
    obj.value = obj.value.replace(/^\./g,"");
    obj.value = obj.value.replace(/\.{2,}/g,".");
    obj.value = obj.value.replace(".","$#$").replace(/\./g,"").replace("$#$",".");
}

function setCookie(name, value, hours)
{
    var exp  = new Date();    //new Date("December 31, 9998");
    exp.setTime(exp.getTime() + hours*60*60*1000);
    document.cookie = name + "="+ escape (value) + ";path=/;expires=" + exp.toGMTString();
}
function getCookie(name)
{
    var arr = document.cookie.match(new RegExp("(^| )"+name+"=([^;]*)(;|$)"));
    if(arr != null) return unescape(arr[2]); return null;
}
function delCookie(name)
{
    var exp = new Date();
    exp.setTime(exp.getTime() - 1);
    var cval=getCookie(name);
    if(cval!=null) document.cookie= name + "="+cval+";path=/;expires="+exp.toGMTString();
}

Date.prototype.format = function (formatStr) {    
    var date = this;
    var zeroize = function (value, length) {    
        if (!length) {    
            length = 2;    
        }    
        value = new String(value);    
        for (var i = 0, zeros = ''; i < (length - value.length); i++) {    
            zeros += '0';    
        }    
            return zeros + value;    
    };    
    return formatStr.replace(/"[^"]*"|'[^']*'|\b(?:d{1,4}|M{1,4}|yy(?:yy)?|([hHmstT])\1?|[lLZ])\b/g, function($0) {    
        switch ($0) {    
            case 'd': return date.getDate();    
            case 'dd': return zeroize(date.getDate());    
            case 'ddd': return ['Sun', 'Mon', 'Tue', 'Wed', 'Thr', 'Fri', 'Sat'][date.getDay()];    
            case 'dddd': return ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'][date.getDay()];    
            case 'M': return date.getMonth() + 1;    
            case 'MM': return zeroize(date.getMonth() + 1);    
            case 'MMM': return ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'][date.getMonth()];    
            case 'MMMM': return ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December'][date.getMonth()];    
            case 'yy': return new String(date.getFullYear()).substr(2);    
            case 'yyyy': return date.getFullYear();    
            case 'h': return date.getHours() % 12 || 12;    
            case 'hh': return zeroize(date.getHours() % 12 || 12);    
            case 'H': return date.getHours();    
            case 'HH': return zeroize(date.getHours());    
            case 'm': return date.getMinutes();    
            case 'mm': return zeroize(date.getMinutes());    
            case 's': return date.getSeconds();    
            case 'ss': return zeroize(date.getSeconds());    
            case 'l': return date.getMilliseconds();    
            case 'll': return zeroize(date.getMilliseconds());    
            case 'tt': return date.getHours() < 12 ? 'am' : 'pm';    
            case 'TT': return date.getHours() < 12 ? 'AM' : 'PM';    
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
			window.alert(LANG_USER_FROZEN);
			break;
		case 'LANG_USER_LIMITED':
			window.alert(LANG_USER_LIMITED);
			break;
		case 'LANG_USER_EMAIL_UNVERIFIED':
			window.alert(LANG_USER_EMAIL_UNVERIFIED);
			break;
		default:
			window.alert(e.message);
		}
	} else {
		window.alert(e);
	}
}
function trim(str){
    return str.replace(/(^s*)|(s*$)/g, "");
}

function getParam(name, default_value) {
     var result = location.search.match(new RegExp("[\?\&]" + name+ "=([^\&]+)","i"));
     if (result == null || result.length < 1){
         return default_value;
     }
     return result[1];
}
