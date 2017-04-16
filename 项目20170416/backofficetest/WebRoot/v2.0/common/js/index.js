
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


loginUser = locStorage("loginUser");

//补登录
if (loginUser != null) {
		window.open('common/index.html', '_self');
} else {
	window.open('common/login.html', '_self');
}