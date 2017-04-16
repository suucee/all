
function getWithdrawalLabelClassName(state){
	var labelClassName = new Array(2);
	switch (state) {
	case 'WAITING':
		labelClassName[0] = 'label-pending';
		labelClassName[1] = '待审核';
		break;
	case 'PENDING_SUPERVISOR':
		labelClassName[0] = 'label-pending';
		labelClassName[1] = '待财务主管审核';
		break;
	case 'AUDITED':
		labelClassName[0] = 'label-pending';
		labelClassName[1] = '待汇款';
		break;
	case 'REMITTED':
		labelClassName[0] = 'label-success';
		labelClassName[1] = '已汇出';
		break;
	case 'REJECTED':
		labelClassName[0] = 'label-cancel';
		labelClassName[1] = '已驳回';
		break;
	case 'BACK':
		labelClassName[0] = 'label-cancel';
		labelClassName[1] = '银行退回';
		break;
	case 'CANCELED':
		labelClassName[0] = 'label-pending';
		labelClassName[1] = '用户取消';
		break;
	}
	return labelClassName;
}


function getDepositLabelClassName(state){
	var labelClassName = new Array(2);
	switch (state) {
	case 'DEPOSITED':
	case 'ACCEPTED':
		labelClassName[0] = 'label-success';
		labelClassName[1] = '已到账';
		break;
	case 'PENDING_PAY':
		labelClassName[0] = 'label-pending';
		labelClassName[1] = '待支付';
		break;
	case 'PENDING_AUDIT':
		labelClassName[0] = 'label-pending';
		labelClassName[1] = '审核中';
		break;
	case 'PENDING_SUPERVISOR':
		labelClassName[0] = 'label-pending';
		labelClassName[1] = '待财务主管审核';
		break;
	case 'REJECTED':
		labelClassName[0] = 'label-important';
		labelClassName[1] = '已驳回';
		break;
	}
	return labelClassName;
}


function getBankLabelClassName(state){
	var labelClassName = new Array(2);
	switch (state) {
	case 'WAITING':
		labelClassName[0] = 'label-pending';
		labelClassName[1] = '待审核';
		break;
	case 'AUDITED':
		labelClassName[0] = 'label-success';
		labelClassName[1] = '已审核';
		break;
	case 'REJECTED':
		labelClassName[0] = 'label-important';
		labelClassName[1] = '已驳回';
		break;
	default:
		label_class = 'label-pending';
		label_name = '无状态';
		break;
	}
	return labelClassName;
}



/**
 * 获取用户资料状态
 * @param state 状态
 * @param isUser 是否是用户,用户看到的和管理员看到的提示不一样
 * @param withTips 是否有提示。如，有：资料不全（客户资料不全，无法激活账户）/无：资料不全
 * @returns {Array}
 */
function getUserProfileLabelClassNameTip(state, isUser, withTips){
	var labelClassNameTip = new Array(3);
	switch (state) {
	case "UNVERIFIED":
		labelClassNameTip[0] = 'label-pending';
		labelClassNameTip[1] = '资料不全';
		if(withTips){
			if(isUser){			labelClassNameTip[2]= '补全资料才可以激活账户';		}
			else{				labelClassNameTip[2]= '等待客户补全资料';			}
		}
		break;
	case "AUDITING":
		labelClassNameTip[0] = 'label-pending';
		if(isUser){				labelClassNameTip[1] = '审核中';}
		else{					labelClassNameTip[1] = '待审核';}
		
		if(withTips){
			if(isUser){			labelClassNameTip[2]= '可修改或追加资料';	}
			else{				labelClassNameTip[2]= '客户仍客户可修改或追加资料';}
		}
		break;
	case "REJECTED":
		labelClassNameTip[0] = 'label-important';
		labelClassNameTip[1] = '已驳回';
		if(withTips){
			if(isUser){			labelClassNameTip[2]= '需要修改资料重新提交';		}
			else{				labelClassNameTip[2]= '客户需修改资料重新提交';			}
		}
		break;
	case "VERIFIED":
		labelClassNameTip[0] = 'label-success';
		labelClassNameTip[1] = '已审核';
		if(withTips){
			if(isUser){			labelClassNameTip[2]= '不可更改资料';		}
			else{				labelClassNameTip[2]= '客户不可更改资料';			}
		}
		break;
	}
	return labelClassNameTip;
}



/**
 * 
 * @param state
 * @param type withdrawals/deposits
 */
function getLabelClassName(state, type){
	if(type=="withdrawals"){
		return getWithdrawalLabelClassName(state);
	}else if(type=="deposits"){
		return getDepositLabelClassName(state);
	}else if(type == "log"){
		var labelClassName = new Array(2);
		labelClassName[0] = 'label-success';
		labelClassName[1] = '已处理';
		return labelClassName;
	}
}