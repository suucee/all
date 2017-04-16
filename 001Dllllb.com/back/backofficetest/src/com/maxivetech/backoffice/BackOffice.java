package com.maxivetech.backoffice;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import com.maxivetech.backoffice.hooks.CompanyHook;

public class BackOffice {
	private static BackOffice _inst = new BackOffice();

	public static BackOffice getInst() {
		return _inst;
	}

	private BackOffice() {
		Properties p = new Properties();
		
		try {
			InputStream in = BackOffice.class.getResourceAsStream("/backoffice-test.properties");
			p.load(in);
			in.close();

			COMPANY_NAME = p.getProperty("COMPANY_NAME");
			URL_ROOT = p.getProperty("URL_ROOT");
			UPLOAD_REAL_ROOT = p.getProperty("UPLOAD_REAL_ROOT");
			UPLOAD_URL_ROOT = p.getProperty("UPLOAD_URL_ROOT");
			LOCAL_CONTROLLER_ROOT = p.getProperty("LOCAL_CONTROLLER_ROOT");
			DLL_ROOT = p.getProperty("DLL_ROOT");
			
			CERT_PATH = p.getProperty("CERT_PATH");
			CERT_SECRET = p.getProperty("CERT_SECRET");
			CERT_MERCHANT_CODE = p.getProperty("CERT_MERCHANT_CODE");

			DEPOSIT_URL = p.getProperty("DEPOSIT_URL");

			SENDMAIL_SMTP = p.getProperty("SENDMAIL_SMTP");
			SENDMAIL_FROM = p.getProperty("SENDMAIL_FROM");
			SENDMAIL_USERNAME = p.getProperty("SENDMAIL_USERNAME");
			SENDMAIL_PASSWORD = p.getProperty("SENDMAIL_PASSWORD");

			SMS_USERNAME = p.getProperty("SMS_USERNAME");
			SMS_PASSWORD = p.getProperty("SMS_PASSWORD");
		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}

	public String VERSION = "1.1.0";
	public String LANG = "cn";
	public double TIMEZONE = 8;

	public String COMPANY_NAME;
	public String URL_ROOT;
	public String UPLOAD_REAL_ROOT;
	public String UPLOAD_URL_ROOT;
	public String LOCAL_CONTROLLER_ROOT;
	public String DLL_ROOT;
	
	//证书
	public String CERT_PATH;
	public String CERT_SECRET;
	public String CERT_MERCHANT_CODE;
	
	public String DEPOSIT_URL;
	
	//发邮件
	public String SENDMAIL_SMTP;
	public String SENDMAIL_FROM;
	public String SENDMAIL_USERNAME;
	public String SENDMAIL_PASSWORD;
	
	//发短信
	public String SMS_USERNAME;
	public String SMS_PASSWORD;
	
	public CompanyHook companyHook = new CompanyHook();
	public int PWD_ERROR_COUNT;
	public int PWD_ERROR_DURATION_MS;
	public int PWD_ERROR_TRIGGER;
}
