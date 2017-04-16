package com.maxivetech.backoffice.sms;

import java.net.Socket;
import java.net.URLEncoder;

import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpClientConnection;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.params.SyncBasicHttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpRequestExecutor;
import org.apache.http.protocol.ImmutableHttpProcessor;
import org.apache.http.protocol.RequestConnControl;
import org.apache.http.protocol.RequestContent;
import org.apache.http.protocol.RequestExpectContinue;
import org.apache.http.protocol.RequestTargetHost;
import org.apache.http.protocol.RequestUserAgent;
import org.apache.http.util.EntityUtils;

import com.maxivetech.backoffice.BackOffice;

public class HelperSms {
	// 字符编码成HEX
	public static String SEND_SMS_IP = "210.51.190.233";
	public static int SEND_SMS_PORT = 8085;
	public static String SEND_SMS_NAME = "misswst_1314";
	public static String SEND_SMS_PASSWORD = "12345678";
	public static String SEND_SMS_SERVICE_ID = "SEND";
	public static String SEND_SMS_SENDER = "8613701234567";

	public HelperSms() {
		this.sendURL = "/mt/MT3.ashx";
		params = new SyncBasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, "GB2312");
		HttpProtocolParams.setUserAgent(params, "HttpComponents/1.1");
		HttpProtocolParams.setUseExpectContinue(params, true);

		httpproc = new ImmutableHttpProcessor(new HttpRequestInterceptor[] {
				// Required protocol interceptors
				new RequestContent(), new RequestTargetHost(),
				// Recommended protocol interceptors
				new RequestConnControl(), new RequestUserAgent(),
				new RequestExpectContinue() });

		httpexecutor = new HttpRequestExecutor();
		context = new BasicHttpContext(null);
		host = new HttpHost(SEND_SMS_IP, SEND_SMS_PORT);
		
		SEND_SMS_NAME = BackOffice.getInst().SMS_USERNAME;
		SEND_SMS_PASSWORD = BackOffice.getInst().SMS_PASSWORD;

		conn = new DefaultHttpClientConnection();
		connStrategy = new DefaultConnectionReuseStrategy();
		context.setAttribute(ExecutionContext.HTTP_CONNECTION, conn);
		context.setAttribute(ExecutionContext.HTTP_TARGET_HOST, host);
	}

	HttpParams params;
	HttpProcessor httpproc;
	HttpRequestExecutor httpexecutor;
	HttpContext context;
	HttpHost host;
	DefaultHttpClientConnection conn;
	ConnectionReuseStrategy connStrategy;
	String sendURL = "http://210.51.190.233:8085/mt/MT3.ashx";

	public static String send(String dest, String msg) {
		return (new HelperSms()).sendGetMessage(dest, msg);
	}
	
	public String sendGetMessage(String dest, String msg) {
		String msgid = "";
		try {
			if (!conn.isOpen()) {
				Socket socket = new Socket(host.getHostName(), host.getPort());
				conn.bind(socket, params);
			}
			String hex = encodeHexStr(8, msg);
			hex = hex.trim() + "&codec=8";
		
			String par = sendURL.trim()
					+ "?src=%s&pwd=%s&ServiceID=%s&dest=%s&sender=%s&msg=%s";
			String url = String.format(par, SEND_SMS_NAME, SEND_SMS_PASSWORD, SEND_SMS_SERVICE_ID, dest, SEND_SMS_SENDER,
					hex);
			// String url = String.format(par, user, pwd, ServiceID, dest,
			// sender, URLEncoder.encode(msg, "GB2312"));
			BasicHttpEntityEnclosingRequest request = new BasicHttpEntityEnclosingRequest(
					"GET", url);

			System.out.println(">> Request URI: "
					+ request.getRequestLine().getUri());

			request.setParams(params);
			httpexecutor.preProcess(request, httpproc, context);
			HttpResponse response = httpexecutor
					.execute(request, conn, context);
			response.setParams(params);

			httpexecutor.postProcess(response, httpproc, context);

			debug("<< Response: " + response.getStatusLine());
			msgid = EntityUtils.toString(response.getEntity());
			debug(msgid);
			debug("==============");
			if (!connStrategy.keepAlive(response, context)) {
				conn.close();
			} else {
				System.out.println("Connection kept alive...");
				conn.close();
			}

		} catch (Exception e) {
			msgid = "";
		}
		
		return msgid;
	}

	public String sendPostMessage(String dest, String msg) {
		String msgid = "";
		
		try {
			String hex = encodeHexStr(8, msg);
			hex = hex.trim() + "&codec=8";
		
			String parf = "src=%s&pwd=%s&ServiceID=%s&dest=%s&sender=%s&msg=%s";
			String par = String.format(parf, SEND_SMS_NAME, SEND_SMS_PASSWORD, SEND_SMS_SERVICE_ID, dest,
					SEND_SMS_SENDER, hex);
			String url = String.format(par, SEND_SMS_NAME, SEND_SMS_PASSWORD, SEND_SMS_SERVICE_ID, dest, SEND_SMS_SENDER,
					URLEncoder.encode(hex, "GB2312"));
			BasicHttpEntityEnclosingRequest request = new BasicHttpEntityEnclosingRequest(
					"POST", sendURL.trim());
			System.out.println(">> Request URI: "
					+ request.getRequestLine().getUri());

			/* System.out.println(">> Request URI:" + par); */
			byte[] data1 = par.getBytes("ASCII");
			ByteArrayEntity entiy = new ByteArrayEntity(data1);
			// hwRequest.ContentType = "application/x-www-form-urlencoded";
			// hwRequest.ContentLength = bData.Length;
			entiy.setContentType("application/x-www-form-urlencoded");
			request.setEntity(entiy);

			System.out.println(">> Request URI: "
					+ request.getRequestLine().getMethod());
			request.setParams(params);

			if (!conn.isOpen()) {
				Socket socket = new Socket(host.getHostName(), host.getPort());
				conn.bind(socket, params);
			}

			HeaderIterator it = request.headerIterator();
			while (it.hasNext()) {
				Header hesd = it.nextHeader();
				System.out.println(">> Request Header: " + hesd.getName()
						+ " : " + hesd.getValue());
			}

			httpexecutor.preProcess(request, httpproc, context);
			HttpResponse response = httpexecutor
					.execute(request, conn, context);
			response.setParams(params);

			httpexecutor.postProcess(response, httpproc, context);

			debug("<< Response: " + response.getStatusLine());
			msgid = EntityUtils.toString(response.getEntity());
			debug(msgid);
			debug("==============");
			if (!connStrategy.keepAlive(response, context)) {
				conn.close();
			} else {
				System.out.println("Connection kept alive...");
				conn.close();
			}

		} catch (Exception e) {
			msgid = "";
		}

		return msgid;
	}

	public static void debug(Object obj) {
		System.out.println(obj);
	}


	public static String encodeHexStr(int dataCoding, String realStr) {
		String strhex = "";
		try {
			byte[] bytSource = null;
			if (dataCoding == 15) {
				bytSource = realStr.getBytes("GBK");
			} else if (dataCoding == 3) {
				bytSource = realStr.getBytes("ISO-8859-1");
			} else if (dataCoding == 8) {
				bytSource = realStr.getBytes("UTF-16BE");
			} else {
				bytSource = realStr.getBytes("ASCII");
			}
			strhex = bytesToHexString(bytSource);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return strhex;
	}

	/** */
	/**
	 * 把字节数组转换成16进制字符串
	 * 
	 * @param bArray
	 * @return
	 */
	public static final String bytesToHexString(byte[] bArray) {
		StringBuffer sb = new StringBuffer(bArray.length);
		String sTemp;
		for (int i = 0; i < bArray.length; i++) {
			sTemp = Integer.toHexString(0xFF & bArray[i]);
			if (sTemp.length() < 2)
				sb.append(0);
			sb.append(sTemp.toUpperCase());
		}
		return sb.toString();
	}

	// 字符编码成HEX
	public static String toHexString(String s) {
		String str = "af";
		for (int i = 0; i < s.length(); i++) {
			int ch = (int) s.charAt(i);
			String s4 = Integer.toHexString(ch);
			str = str + s4;
		}
		return "0x" + str;// 0x表示十六进制
	}

	// 转换十六进制编码为字符串
	public static String toStringHex(String s) {
		if ("0x".equals(s.substring(0, 2))) {
			s = s.substring(2);
		}
		byte[] baKeyword = new byte[s.length() / 2];
		for (int i = 0; i < baKeyword.length; i++) {
			try {
				baKeyword[i] = (byte) (0xff & Integer.parseInt(
						s.substring(i * 2, i * 2 + 2), 16));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		try {
			s = new String(baKeyword, "GBK");// UTF-16le:Not
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return s;
	}

	// hex编码还原成字符
	public static String decodeHexStr(int dataCoding, String hexStr) {
		String strReturn = "";
		try {
			int len = hexStr.length() / 2;
			byte[] bytSrc = new byte[len];
			for (int i = 0; i < len; i++) {
				String s = hexStr.substring(i * 2, 2);
				bytSrc[i] = Byte.parseByte(s, 512);
				Byte.parseByte(s, i);
				// bytSrc[i] = Byte.valueOf(s);
				// bytSrc[i] = Byte.Parse(s,
				// System.Globalization.NumberStyles.AllowHexSpecifier);
			}

			if (dataCoding == 15) {
				strReturn = new String(bytSrc, "GBK");
			} else if (dataCoding == 3) {
				strReturn = new String(bytSrc, "ISO-8859-1");
			} else if (dataCoding == 8) {
				strReturn = new String(bytSrc, "UTF-16BE");
				// strReturn = Encoding.BigEndianUnicode.GetString(bytSrc);
			} else {
				strReturn = new String(bytSrc, "ASCII");
				// strReturn =
				// System.Text.ASCIIEncoding.ASCII.GetString(bytSrc);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strReturn;
	}
}
