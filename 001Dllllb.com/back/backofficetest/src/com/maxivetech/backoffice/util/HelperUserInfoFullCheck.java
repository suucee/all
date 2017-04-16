package com.maxivetech.backoffice.util;

import java.util.HashMap;

import com.maxivetech.backoffice.entity.UserProfiles;
import com.maxivetech.backoffice.entity.Users;

public class HelperUserInfoFullCheck {
    public static HashMap<String,Object> checkInfoFull(Users users,UserProfiles userProfiles){
    	HashMap<String,Object> result=new HashMap<String,Object>();
    	//user
    	if(users!=null&&users.getPaymentPassword()!=null&&users.getPaymentPassword()!=""){
    		result.put("paymentpassword",users.getPaymentPassword());
    	}else{
    		result.put("paymentpassword", null);
    	}
    	//userprofile
    	
    	if(userProfiles!=null&&userProfiles.getUser().getEmail()!=null&&userProfiles.getUser().getEmail()!=""){
    		result.put("user_email",userProfiles.getUser().getEmail());
    	}else{
    		result.put("user_email", null);
    	}
    	if(userProfiles!=null&&userProfiles.getUser().getMobile()!=null&&userProfiles.getUser().getMobile()!=""){
    		result.put("user_phone",userProfiles.getUser().getMobile());
    	}else{
    		result.put("user_phone", null);
    	}    	
    	if(userProfiles!=null&&userProfiles.getCardType()!=null&&userProfiles.getCardType()!=""){
    		result.put("user_cardtype",userProfiles.getCardType());
    	}else{
    		result.put("user_cardtype", null);
    	}
    	if(userProfiles!=null&&userProfiles.getCompany()!=null&&userProfiles.getCompany()!=""){
    		result.put("user_comname",userProfiles.getCompany());
    	}else{
    		result.put("user_comname", null);
    	}
    	if(userProfiles!=null&&userProfiles.getUserEName()!=null&&userProfiles.getUserEName()!=""){
    		result.put("user_enname",userProfiles.getUserEName());
    	}else{
    		result.put("user_enname", null);
    	}
    	
    	if(userProfiles!=null&&userProfiles.getUserEsidentialAddress()!=null&&userProfiles.getUserEsidentialAddress()!=""){
    		result.put("user_esidentialaddress",userProfiles.getUserEsidentialAddress());
    	}else{
    		result.put("user_esidentialaddress", null);
    	}
    	if(userProfiles!=null&&userProfiles.getUserIdCard()!=null&&userProfiles.getUserIdCard()!=""){
    		result.put("user_idcard",userProfiles.getUserIdCard());
    	}else{
    		result.put("user_idcard", null);
    	}   	
    	if(userProfiles!=null&&userProfiles.getUserIndustry()!=null&&userProfiles.getUserIndustry()!=""){
    		result.put("user_industry",userProfiles.getUserIndustry());
    	}else{
    		result.put("user_industry", null);
    	}
    	if(userProfiles!=null&&userProfiles.getUserName()!=null&&userProfiles.getUserName()!=""){
    		result.put("user_name",userProfiles.getUserName());
    	}else{
    		result.put("user_name", null);
    	}
    	if(userProfiles!=null&&userProfiles.getUserNationality()!=null&&userProfiles.getUserNationality()!=""){
    		result.put("user_nationality",userProfiles.getUserNationality());
    	}else{
    		result.put("user_nationality", null);
    	}
    	if(userProfiles!=null&&userProfiles.getUserYearsIncom()!=null&&userProfiles.getUserYearsIncom()!=""){
    		result.put("user_year_income",userProfiles.getUserYearsIncom());
    	}else{
    		result.put("user_year_income", null);
    	}
    	return result;
    }
}
