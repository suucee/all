var file=getParam("page","");
$(function(){
	$("#show_content").attr("src", file);
});

function delCookie(name) {
	var exp = new Date();
	exp.setTime(exp.getTime() - 1);
	var cval = getCookie(name);
	if (cval != null)
		document.cookie = name + "=" + cval + ";path=/;expires=" + exp.toGMTString();
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
	return formatStr.replace(/"[^"]*"|'[^']*'|\b(?:d{1,4}|M{1,4}|yy(?:yy)?|([hHmstT])\1?|[lLZ])\b/g, function($0) {
		switch ($0) {
		case 'd':
			return date.getDate();
		case 'dd':
			return zeroize(date.getDate());
		case 'ddd':
			return ['Sun', 'Mon', 'Tue', 'Wed', 'Thr', 'Fri', 'Sat'][date.getDay()];
		case 'dddd':
			return ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'][date.getDay()];
		case 'M':
			return date.getMonth() + 1;
		case 'MM':
			return zeroize(date.getMonth() + 1);
		case 'MMM':
			return ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'][date.getMonth()];
		case 'MMMM':
			return ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December'][date.getMonth()];
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
	var result = location.search.match(new RegExp("[\?\&]" + name + "=([^\&]+)", "i"));
	if (result == null || result.length < 1) {
		return default_value;
	}
	return result[1];
}