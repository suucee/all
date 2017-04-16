const CNY_ICO = '<i class="fa fa-jpy" aria-hidden="true"></i>';
const USD_ICO = '<i class="fa fa-usd" aria-hidden="true"></i>';
const BAN_ICO = '<i class="fa fa-ban" aria-hidden="true"></i>';

const ONKEYUP = "this.value=this.value.replace(/\D/g,'')";//只能输入数字,适用于银行卡


var loginUser=null;
$(function(){
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
		if (filename == "login.html" || filename == "register.html" || filename == "forgot_password.html") {

	    } else {
			//检查登录
			loginUser = locStorage("loginUser");
			//补登录
			if (loginUser != null) {
			} else {
				$.jsonRPC.request('sessionService.checkLogined', {
					params : [],
					success : function(result) {
						loginUser=result;
						if (loginUser != null) {
							locStorage("loginUser", loginUser);
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
});

function locStorage(key, value) {
    if (window.localStorage) {
        if (arguments.length == 1) {
        	try {
        		var value = JSON.parse(window.localStorage.getItem(arguments[0]));
        		return value;
        	} catch (e) {
        		layer.alert(e);
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
        layer.alert('不支持localStorage');
    }
    return null;
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
				window.parent.open('../common/login.html', '_self');
			}
		}
	});
}


function holdLogout(){
	logout();
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
function toSimpleDate(v) {
	var ret = new Date();
	ret.setTime(v);
	return ret.format("yy-MM-dd");
}
function toSimpleDate2M(v) {
	var ret = new Date();
	ret.setTime(v);
	return ret.format("yy-MM-dd HH:mm");
}



function toDateNumber(v){
	var ret = new Date();
	ret.setTime(v);
	return ret.format("yyyy-MM-dd-HH-mm-ss").replace(/-/g,"");
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
/**
 * @param passRoleList 拥有权限的数组，全部权限包括（
 * </br>ComplianceOfficer	合规人员        
 * </br>FinancialStaff		财务          
 * </br>FinancialSuperior	财务主管        
 * </br>CustomerServiceStaff	客服          
 * </br>Webmaster			网站管理员       
 * </br>OperationsManager 	运维经理        
 * </br>RiskManagementCommissioner	风控专员）
 * @param passInfo 拥有权限的操作,当这个参数为null时，会返回BAN_ICO（i标签）
 * @param banInfo 没有权限的操作,当这个参数为null时，会返回BAN_ICO（i标签）
 */
function checkRole(passRoleList, passInfo, forbidInfo){
	var i = 0;
	for(i in passRoleList){
		if(loginUser.role == passRoleList[i]){
			if(passInfo == null)
				return BAN_ICO;
			else{
				return passInfo;
			}
		}
	}
	if(i == passRoleList.length-1){
		if(forbidInfo == null){
			return BAN_ICO;
		}else{
			return forbidInfo;
		}
	}
}

function relogin(){
	if (loginUser!=null) {
		var l=eval(loginUser);
		$.jsonRPC.request('sessionService.login', {
			params : [l.token],
			success : function(result) {
				locStorage("loginUser", loginUser);
			}
		});
	}else{
		locStorage("loginUser", loginUser);
		window.parent.open("./../common/login.html", "_self");
	}
}


function moveToTop(){
	document.body.scrollTop = document.documentElement.scrollTop = 0;//回到顶部
}

function fmoney(s, n)   
{   
   n = n > 0 && n <= 20 ? n : 2;   
   s = parseFloat((s + "").replace(/[^\d\.-]/g, "")).toFixed(n) + "";   
   var l = s.split(".")[0].split("").reverse(),   
   r = s.split(".")[1];   
   t = "";   
   for(i = 0; i < l.length; i ++ )   
   {   
      t += l[i] + ((i + 1) % 3 == 0 && (i + 1) != l.length ? "," : "");   
   }   
   return t.split("").reverse().join("") + "." + r;   
}

function rmoney(s)   
{   
   return parseFloat(s.replace(/[^\d\.-]/g, ""));   
}


Number.prototype.toMoney = function(n){
    return fmoney(this,n);
}


/**
 * 在global.js里面定位
 */
function localUserGlobaly(userId){
	if(typeof(localById) === "function"){
		localById(userId);
	}else if(typeof(parent.localById) === "function" ){
		parent.localById(userId);
	}
}

function goSeeProfile(userId,isAgent){
	if(isAgent != null && isAgent){//目标是代理,直接跳到个人页面
		window.location.href = './../admin/see_profile.html?id='+userId+'&backurl='+window.location.href;
		return;
	}else{
		//判断是不是代理
		$.jsonRPC.request("admin2UserService.getOne", {
			params : [userId],
			success : function(result) {	
				if(result.vipGrade > 0){//是代理
					window.location.href = './../admin/see_agent.html?id='+userId+'&backurl='+window.location.href;
				}else{
					window.location.href = './../admin/see_profile.html?id='+userId+'&backurl='+window.location.href;
				}
			}
		});
	}
}


/**
 * 检查登录角色是否是财务，否则隐藏代为入金的链接
 */
function hideFinanceDeposit(){
	if(loginUser.role == "FinancialStaff" || loginUser.role == "FinancialSuperior" || loginUser.role == "OperationsManager"){
		//显示代为入金的连接
	}else{//隐藏代为入金的链接
		var lastA = $(".container > div > a:last");
		$(lastA).hide();
	}
}



//function drawNoticeNumGlobaly (count) {
//	$(".frist-menu dd").each(function (i,element) {
//		if($(element).html() == "合规"){
//			alert("合规:"+i);
//		}
//	});
//}

/**
 * 合规——提醒备忘
 */
function showWhatisRemind(selector){
	catchInfo("什么是备忘？", "备忘是你给这位用户打的备忘录，到了提醒时间应该做什么事。比如：查看用户的银行卡信息。<br/>\
			注意，写了提醒备忘就必须选择提醒时间，并且只有您自己和后台合规以及超级管理员可见。",selector);
}
/**
 * 合规——提醒备忘的时间
 */
function showWhatisRemindDate(selector){
	catchInfo("为什么要设置提醒时间？", "因为有可能在合规完成后需要再次检查合规申请，此时如果设置了提醒时间，则在该时间以后就会在“提醒”中显示，以便合规人员再次检查或者重新审核，它应该与备注配合使用",selector);
}

/**
 * 合规——上传文件
 */
function showWhatisRemindFile(selector){
	catchInfo("合规文件？", "合规文件不是必须上传的。但是如果合规人员在合规申请时，用户有提交特别文件，合规可以上传该文件作为后续参考文件。",selector);
}

/**
 * 个人资料——地址证明
 */
function showWhatisAddrProf(selector){
	catchInfo("地址证明？", "地址证明可用以下证明材料：驾照、户口本本人页和户主页、水、电、煤气、信用卡3个月内的纸质账单、手机账单、物业缴费单、房产证、租房合同和银行流水账单均可。",selector);
}

function showWhatisAmountFrozen(selector){
	catchInfo("冻结余额？", "冻结余额：在用户有佣金收入时，佣金不是直接返到用户账户，而是暂存于冻结余额，待佣金发放后，冻结余额将会自动转入账户余额，变为可用余额。",selector);
}

function showWhatisWithdrawaling(selector){
	catchInfo("正在出金的金额？", "正在出金的金额：用户成功提交了出金申请，但是款项还未汇出到用户的银行账户，是正在处理的金额。",selector);
}


function catchInfo(title, html, parenr_selector){
	
	layer.open({
        type: 1,
        title:title,
        skin: 'layui-layer-demo',
        closeBtn: 2,
        area: '350px',
        shift: 2,
        shadeClose: true,
        content: '<div style="padding:20px;background:#56a787;color:white">'+html+'</div>'
      });
}
