//需要引入font-awesome.min.css
const TAG_ICO = '<i class="fa fa-tags" aria-hidden="true"></i>';
const LOCAL_ICO = '<i class="fa fa-thumb-tack" aria-hidden="true" onclick="local(this)" title="定位"></i>';
const HEAD_ICO = '<span></span>';

var isMyCustoms = true;

var myCustoms = null;//我的客户
var cpCustoms = null;//公司客户
var childrens = null;//我的直接下级
var rootId = null;//根节点
var callback = null;//点击节点时要触发的函数
var canBroaden = true;//结构树能否拓宽,默认可以拓宽


var timeInterval = 500; //每隔多少秒检查
var timeOut = 3 * 1000;//等3秒后如果还是获取不到数据，自动放弃

/**
 * 初始化完成调用的函数
 */
//var onInitTreeSuccessFuns = new Array();


/**
 * 获取数据,并且设置好树，刷新作用同样如此
 * @param {Object} customType "MY或者CP：MY表示获取所有数据，但是设置为自己的，反之设置客户的"
 */
function getTreeData (customType, getCallBack) {
	$.jsonRPC.request('admin2UserService.getTreeList', {
		params : [],
		success : function(result) {
			if(result != null){
				childrens = result.map.nextCustomers.list;
				rootId = result.map.rootId;
				myCustoms = result.map.customers.list;
				cpCustoms = result.map.allocationCustomers.list;
				if(customType == "MY"){
					init_Tree(myCustoms,rootId);
				}
				else{
					init_Tree(cpCustoms,rootId);
				}
				
				if(getCallBack != null){
					getCallBack.call();
				}
				
			}
		}
	});
}
//结构树的初始化参数
var setting = {
	view: {
		showLine: true,//显示结构线
		selectedMulti: false,//多选
		addHoverDom: addHoverDom,//鼠标移入事件
		removeHoverDom: removeHoverDom,//鼠标移出事件
		fontCss: setFontCss//设置字体
	},
	data: {
		simpleData: {
			enable: true
		}
	},
	callback: {
		onClick: zTreeOnClick,
	}
};


/**
 * 根据数据初始化我的客户结构树
 * @param {Object} customs 公司客户还是自己的客户
 * @param {Number} xrootId 根节点
 */
function init_Tree (customs, xrootId, initCallBak) {
	$.fn.zTree.init($("#tree"), setting, customs);//初始化状态树
	
	setTreeData (xrootId);
	
	var treeObj = $.fn.zTree.getZTreeObj("tree");
	//默认展开根节点
	var node = treeObj.getNodeByParam("id", xrootId, null);
	//Function(treeNode, expandFlag, sonSign, focus, callbackFlag)
//	展开 /折叠 指定的节点,true 表示 展开,true 表示 全部子孙节点,设置焦点保证此焦点进入可视区域内,true 表示执行此方法时触发回调
	treeObj.expandNode(node,true,false,true,true);//展开
	
	if(initCallBak != null){
		initCallBak.call();
	}
}


/**
 * 设置数据
 * @param {Number} xrootId 根节点
 */
function setTreeData (xrootId) {
	var zTree = $.fn.zTree.getZTreeObj("tree");
	
	var nodes = zTree.transformToArray(zTree.getNodes());
	//将身份职位和名字一起显示
	for(var i in nodes){
		//设置不同颜色的图标，由css完成
		var iconSkin = getIconSkin(nodes[i].vipGrade, nodes[i].userLevel);
		nodes[i].iconSkin = iconSkin;
		//设置显示格式为：姓名 [身份]
		var grade = nodes[i].grade;
		var name = nodes[i].name;
		nodes[i].name = name +"["+grade+"]";
		if(nodes[i].tags != undefined && nodes[i].tags != "" && nodes[i].pId==loginUser.id)//有tag。，并且是当前用户的直接下级
		{
			nodes[i].name += "("+nodes[i].tags+")";
		}
	}
	zTree.updateNode(nodes);
	
	//设置根节点为蓝色图标(改变css样式)
	var node = zTree.getNodeByParam("id", xrootId, null);
	if(node){
		node.iconSkin = "tree_root";//css中有tree_root_open和tree_root_close这两个
		zTree.updateNode(node);
	}
}









/**
 * 设置字体颜色
 * @param {Object} treeId
 * @param {Object} treeNode
 */
function setFontCss(treeId, treeNode) {
	return {
		color: "black"
	};
}

/**
 * 鼠标移入的事件
 * @param {Object} treeId
 * @param {Object} treeNode
 */
function addHoverDom(treeId, treeNode) {
	var aObj = $("#" + treeNode.tId + "_a");
	
	for(var i in childrens){
		if(treeNode.id == childrens[i].id  && treeNode.id != rootId){
			if($("#diyspan_" + treeNode.id).length > 0) {
				$("#diyspan_" + treeNode.id).css("display", "inline");
				return;
			} else {
				var editStr = "<span class='diyspan' display='inline' id=diyspan_" + treeNode.id + ">" + TAG_ICO +
					"<input class='diyinput' id='diyput_" + treeNode.id + 
					"' maxlength='6' size='8' placeholder='标签' title='这是您给TA打的标签,且您只能给您的直接下级打标签' onclick='event.stopPropagation();' "+
					"value='"+treeNode.tags+"' onblur='blurTag(" + treeNode.id +")' onkeypress='pressTag(" + treeNode.id +",event)' onfocus='focusTag(" + treeNode.id +")'></input></span>";
				aObj.append(editStr);
			}
		}
		else{continue;}
	}
};


/**
 * 鼠标移出的事件
 * @param {Object} treeId
 * @param {Object} treeNode
 */
function removeHoverDom(treeId, treeNode) {
	$("#diyspan_" + treeNode.id).css("display", "none");
};

/**
 * 1、搜索框调用的时候应该保证参数为空（如果参数为null的话，用搜索框的数据的）
 * 2、其他地方调用的时候参数不能为空
 * @param {Object} keyword  关键字
 */
function search (keyword) {
	
	$("#search").css("animation","");//移除特效
	var treeObj = $.fn.zTree.getZTreeObj("tree");
	var resultNodes = null;//treeObj.getNodesByParamFuzzy返回数组
	var resultNode = null;//treeObj.getNodeByParam返回对象
	$(".search_result").html("");
	var htmlStr = "";//动态添加结果
	if(keyword == null){
		keyword = $("#text_search_customer").val().trim();//获取搜索框的数据
	}
	if(keyword == null || keyword.length==0){
		return;
	}
	keyword = keyword.trim();
	//注意+86.15989505805会被认为是数字
	if(isNaN(keyword)){//不是一个数的话，按照搜索用户名，标签，邮箱，电话，的顺序来搜索（参数只能是字符串）
		resultNodes = treeObj.getNodesByParamFuzzy("name", keyword, null);
		if(resultNodes.length == 0){
			resultNodes = treeObj.getNodesByParamFuzzy("tags", keyword, null);
			if(resultNodes.length == 0){
				resultNodes = treeObj.getNodesByParamFuzzy("email", keyword, null);
				if(resultNodes.length == 0){
					resultNodes = treeObj.getNodesByParamFuzzy("ename", keyword, null);
				}
			}
		}
	}
	else if(keyword.length>0){//ID,getNodesByParamFuzzy只能匹配字符串
		if(keyword.startsWith("+86.") || keyword.length>=8){//初步认为是电话号码
			resultNodes = treeObj.getNodesByParamFuzzy("mobile", keyword, null);
		}else{//认为是id
			resultNode = treeObj.getNodeByParam("id", keyword, null);
			if(resultNode == null){
				$(".search_result").html("<li>没有搜索结果。</li>");
				$(".search_result_div").focus();
				animateSearchResult ();
				return;
			}else{//因为最多只有一个结果，所以，可以单独在树里面展示出来
//				htmlStr +="<li id='search_"+resultNode.id+"'>"+LOCAL_ICO+HEAD_ICO+"<a onclick='resultOnClick(this)'>"+resultNode.name+"</a></li>";
				htmlStr +="<li id='search_"+resultNode.id+"'>"+HEAD_ICO+"<a onclick='resultOnClick(this)'>"+resultNode.name+"</a></li>";
				$(".search_result").append(htmlStr);
				$(".clear_search_result").show();//显示清除按钮
				return;
			}
		}
	}
	else{
		$(".clear_search_result").hide();//隐藏清除按钮
		return;
	}
	if(resultNodes.length == 0){
		$(".search_result").append("<li>没有搜索结果。</li>");
		$(".search_result_div").focus();
		animateSearchResult ();
		return;
	}
	for(var i in resultNodes){
//		htmlStr +="<li id='search_"+resultNodes[i].id+"'>"+LOCAL_ICO+HEAD_ICO+"<a onclick='resultOnClick(this)'>"+resultNodes[i].name+"</a></li>";
		htmlStr +="<li id='search_"+resultNodes[i].id+"'>"+HEAD_ICO+"<a onclick='resultOnClick(this)'>"+resultNodes[i].name+"</a></li>";
	}
	$(".search_result").append(htmlStr);
	$(".clear_search_result").show();//显示清除按钮
	animateSearchResult ();
}

function animateSearchResult () {
	$("#search").css("top","-250px");
	$("#search").css("animation","0.5s moveDown both");
	$("#search").css("top","0px");
	$(".search_contain")[0].scrollTop = 0;//回到搜索顶部
}


/**
 * 在输入前的文本
 */
var beforeInputTagText = null;

/**
 * 输入后的文本
 */
var afterInputTagText = null;

/**
 * 当用户点击了输入框时，将输入前的文本保存下来
 * @param {Object} nodeId
 */
function focusTag (nodeId) {
	beforeInputTagText = $("#diyput_"+nodeId).val().trim();
}

/**
 * 当用户点击了标签文本框再次失去焦点时，检查提交数据
 * @param {Object} nodeId
 */
function blurTag (nodeId) {
	afterInputTagText = $("#diyput_"+nodeId).val().trim();
	if(afterInputTagText != beforeInputTagText){
//		提交应有的数据到后台
		saveUserTags(nodeId,afterInputTagText);
	}
}

/**
 * 当用户按下键盘时，判断是否是回车，检查提交数据
 * @param {Object} nodeId
 */
function pressTag (nodeId, e) {
	afterInputTagText = $("#diyput_"+nodeId).val().trim();
	if(e.which == 13){
		if(afterInputTagText != beforeInputTagText){
	//		提交应有的数据到后台，不用回车方式提交，因为到时候任然会失去焦点
		}
	}
}


/**
 * 提交标签数据到后台
 * @param {Object} user_id 用户的ID
 * @param {Object} tags 标签
 */
function saveUserTags(user_id,tags) {
	$.jsonRPC.request('admin2UserService.saveUserTags', {
		params : [user_id,tags],
		success : function(result) {
			if(result != true){
				layer.msg("对不起，由于网络原因，标签更改失败。",{time:2000,icon:2});
			}
		}
	});
}




/**
 * 根据身份的不同选择不一样的节点图标，返回的字符串会在ztree.css里面有类似
 * .ztree li span.button.xxxxxxxx_ico_open的css样式表
 * @param {Object} vipGrade
 * @param {Object} level
 */
function getIconSkin(vipGrade, level) {
		if (level >= 1 && level <= 3) {
			switch (level) {
			case 1://				return "公司";
				return "tree_comp";
			case 2://				return "经理";
				return "tree_mana";
			case 3://				return "员工";
				return "tree_staff";
			}
		} else {
			switch (vipGrade) {
			case 0:				//return "客户";
			return "tree_cust";
			case 1:				//return "1级代理";
			return "tree_agent1";
			case 2:				//return "2级代理";
			return "tree_agent2";
			case 3:				//return "3级代理";
			return "tree_agent3";

			case 100:				//return "个人代理";
			return "tree_pagent";
			case 101:				//return "1级+个人代理";
			return "tree_pagent1";
			case 102:				//return "2级+个人代理";
			return "tree_pagent2";
			case 103:				//return "3级+个人代理";
			return "tree_pagent3";
				
			case 200:				//return "公司代理";
			return "tree_cagent";
			case 201:				//return "1级+公司代理";
			return "tree_cagent1";
			case 202:				//return "2级+公司代理";
			return "tree_cagent2";
			case 203:				//return "3级+公司代理";
			return "tree_cagent3";
				
			case 10000:				//return "公司";
			return "tree_comp";
			}
		}
		return "tree_cust";
	}

/**
 * 由搜索结果定位到关系树里面
 * @param {Object} obj 定位图标自己
 */
function local (obj) {
	var id = $(obj).parent().attr("id").replace("search_","");
	var treeObj = $.fn.zTree.getZTreeObj("tree");
	var node = treeObj.getNodeByParam("id", id, null);
	treeObj.expandNode(node,true,false,true,true);//展开
	treeObj.selectNode(node);//选中
}


function expandFirst(treeObj){
	if(treeObj ==null){
		treeObj = $.fn.zTree.getZTreeObj("tree");
	}
	//折叠所有节点
	treeObj.expandAll(false);
	//默认展开根节点
	var node = treeObj.getNodeByParam("id", rootId, null);
	treeObj.expandNode(node,true,false,true,true);//展开
	var nodes = treeObj.getSelectedNodes();
	//取消选中节点
	if (nodes.length>0) { 
		for(i in nodes){
			treeObj.cancelSelectedNode(nodes[i]);
		}
	}
	return;
}


/**
 * 由搜索结果定位到关系树里面
 * @param {Object} id 定位的目标用户id,当id==0时，全部折叠
 * @param {reInit} 是否需要重新加载节点
 */
function localById (id, reInit) {
	var treeObj = null;
	if(reInit){
		refreshTreeData();
		var interID = setInterval(function(){
			treeObj = $.fn.zTree.getZTreeObj("tree");
//			console.log(treeObj);
			if(treeObj != null){
				clearInterval(interID);
				if(id==0){
					expandFirst();
				}
				var node = treeObj.getNodeByParam("id", id, null);
				treeObj.expandNode(node,true,false,true,true);//展开
				treeObj.selectNode(node);//选中
				treeObj.expandNode(node,true,false,true,true);//展开
				return;
			}
		}, timeInterval);
		
		setTimeout(function () {
			clearInterval(interID);
		},timeOut);
		
	}else{
		treeObj = $.fn.zTree.getZTreeObj("tree");
	}
	if(treeObj == null){
		return;
	}
	if(id==0){
		expandFirst();
	}
	console.log("===================");
	var node = treeObj.getNodeByParam("id", id, null);
	treeObj.expandNode(node,true,false,true,true);//展开
	treeObj.selectNode(node);//选中
	treeObj.expandNode(node,true,false,true,true);//展开
}


/**
 * 显示我的客户
 */
function showMyCustomers() {
	clear_search_result ();
	$("#my_customer_showTip").html("我的客户");
	$(".my_customer_showTip i").css("color","deepskyblue");
	isMyCustoms = true;
	if(myCustoms!=null){//判断有没有数据？
		init_Tree(myCustoms,rootId);
	}else{//从远程获取数据
		getTreeData("MY");
	}
}

/**
 * 显示公司分配的客户
 */
function showCpCustomers() {
	clear_search_result ();
	$("#my_customer_showTip").html("分配客户");
	$(".my_customer_showTip i").css("color","orange");
	isMyCustoms = false;
	if(cpCustoms!=null){//判断有没有数据？
		init_Tree(cpCustoms,rootId);
	}else{//从远程获取数据
		getTreeData("CP");
	}
}

/**
 * 清空结果
 */
function clear_search_result () {
	$("#search").css("animation","");
	$("#search").animate({"top":"-250px"},300,"linear",function () {
		$(".search_result").html("");//清空结果
	});
	$(".clear_search_result").hide();//隐藏删除按钮
}

/**
 * 刷新数据
 */
function refreshTreeData () {
	myCustoms = null;
	if(isMyCustoms){
		getTreeData("MY");
	}
	else{
		getTreeData("CP");
	}
	
}


function resultOnClick (obj) {
	var id = $(obj).parent().attr("id").replace("search_","");
	if(id != null){
		localById(id);
		
		var treeObj = $.fn.zTree.getZTreeObj("tree");
		var node = treeObj.getNodeByParam("id", id, null);
		if(node.vipGrade > 0){
			callback(id,true);
		}else{
			callback(id,false);
		}
	}
}


/**
 * 鼠标点击节点的事件(在结构树中)
 */
function zTreeOnClick () {
	var zTree = $.fn.zTree.getZTreeObj("tree");
	var nodes = zTree.getSelectedNodes();
	if(nodes.length>0){
		
		var treeObj = $.fn.zTree.getZTreeObj("tree");
		var node = treeObj.getNodeByParam("id", nodes[0].id, null);
		if(node.vipGrade > 0){
			callback(nodes[0].id,true);
		}else{
			callback(nodes[0].id,false);
		}
	}
}

function getStaffList () {
	search ("员工");
}
function getAgentList () {
	search ("代理");
}

/**
 * 显示、隐藏客户分类DIV
 * @param display 显示：true， 隐藏：false；
 */
function display(display){
	if(display){
		$(".type").show(200);
	}else{
		$(".type").hide(200);
	}
}
