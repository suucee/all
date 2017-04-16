package com.maxivetech.backoffice.sendmail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.maxivetech.backoffice.BackOffice;


public class HelperSendmail {
	private static String smtp;// smtp服务器
	private static String from;// 邮件显示名称
	private static String username;// 发件人真实的账户名
	private static String password;// 发件人密码
	private static boolean isInited = false;
	
	private static String path = HelperSendmail.class.getResource("/").getFile().substring(1)+"../";

	/**
	 * 同步发送邮件
	 * 
	 * @param fileName
	 * @param emailVal
	 * @param to
	 * @param copyto
	 * @return
	 */
	public static boolean send(String fileName, HashMap<String, Object> emailVal, String to, String copyto) {
		//初始化
		if (!isInited) {
			smtp = BackOffice.getInst().SENDMAIL_SMTP;
			from = BackOffice.getInst().SENDMAIL_FROM;
			username = BackOffice.getInst().SENDMAIL_USERNAME;
			password = BackOffice.getInst().SENDMAIL_PASSWORD;
			
			isInited = true;
		}
		
		try {
			File file = new File(path + fileName);
			if (file.isFile() != true) {
				file.createNewFile(); // 读入数据
			}
			FileInputStream fis = new FileInputStream(file);
			String manager = null;
			byte[] text = new byte[(int) file.length()];
			fis.read(text);
			fis.close();
			manager = new String(text, "utf-8");
			
			// 获取页面的title
			String title = "";
			String regex;
			final List<String> list = new ArrayList<String>();
			regex = "<title>.*?</title>";
			final Pattern pt = Pattern.compile(regex, Pattern.CANON_EQ);
			final Matcher mc = pt.matcher(manager);
			while (mc.find()) {
				list.add(mc.group());
			}
			for (int i = 0; i < list.size(); i++) {
				title = title + list.get(i);
			}
			String subject = title.replace("<title>", "").replace("</title>", "");
			System.out.println(subject);
			HashMap<String, Object> rf = emailVal;
			Iterator it = rf.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				String key = entry.getKey().toString();
				String val = entry.getValue() == null ? "" : entry.getValue().toString();
				manager = manager.replace("%" + key + "%", val);
				subject = subject.replace("%" + key + "%", val);
			}
			String content = manager;
			System.out.println(content);
			
			/* System.out.println(content); */
			// 发送邮件
			return Eemail.sendAndCc(smtp, from, to, copyto, subject, content,
					username, password);
		} catch (Exception ex) {
			System.out.println("发送失败");
			return false;
		}
	}

	/**
	 * 异步发送邮件
	 * 
	 * @param fileName
	 * @param emailVal
	 * @param to
	 * @param copyto
	 * @return
	 */
	public static void sendAsync(final String fileName,
			final HashMap<String, Object> emailVal, final String to,
			final String copyto) {

		new Thread(new Runnable() {
			public void run() {
				try {

					long mm = 60000;
					for (int i = 1; i < 4; i++) {
						boolean f = send(fileName, emailVal, to, copyto);
						if (f) {
							break;

						} else {
							Thread.sleep(mm);
							mm = mm * 10;
						}
					}

				} catch (Exception ex) {
					System.out.println("发送失败");
				}
			}
		}).start();
	}
}
