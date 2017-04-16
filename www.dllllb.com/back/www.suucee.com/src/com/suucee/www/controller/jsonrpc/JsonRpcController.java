/*
 * JsonRpcController - a controller for Spring MVC and JSON-RPC-JAVA integration
 * 
 * Copyright Dinstone 2009
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under 
 * the License.
 * 
 */
package com.suucee.www.controller.jsonrpc;

import java.io.BufferedReader;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.maxivetech.backoffice.controller.jsonrpc.JsonRpcView;
import com.maxivetech.backoffice.dao.TokenDao;
import com.maxivetech.backoffice.entity.Tokens;
import com.maxivetech.backoffice.pojo.PojoSession;
import com.maxivetech.backoffice.service.admin.AdminAdminService;
import com.maxivetech.backoffice.service.admin.AdminAgentService;
import com.maxivetech.backoffice.service.admin.AdminBranchCompanyService;
import com.maxivetech.backoffice.service.admin.AdminCheckBankAccountService;
import com.maxivetech.backoffice.service.admin.AdminCheckService;
import com.maxivetech.backoffice.service.admin.AdminDepositService;
import com.maxivetech.backoffice.service.admin.AdminRebateSchemeService;
import com.maxivetech.backoffice.service.admin.AdminRebateService;
import com.maxivetech.backoffice.service.admin.AdminReportService;
import com.maxivetech.backoffice.service.admin.AdminRiskService;
import com.maxivetech.backoffice.service.admin.AdminSettingService;
import com.maxivetech.backoffice.service.admin.AdminUserService;
import com.maxivetech.backoffice.service.admin.AdminWithdrawalService;
import com.maxivetech.backoffice.service.user.SessionService;
import com.maxivetech.backoffice.service.user.UserAccountService;
import com.maxivetech.backoffice.service.user.UserDepositService;
import com.maxivetech.backoffice.service.user.UserRankService;
import com.maxivetech.backoffice.service.user.UserRebateService;
import com.maxivetech.backoffice.service.user.UserReportService;
import com.maxivetech.backoffice.service.user.UserService;
import com.maxivetech.backoffice.service.user.UserWithdrawalService;
import com.maxivetech.backoffice.service.user.impl.SessionServiceImpl;
import com.metaparadigm.jsonrpc.JSONRPCBridge;
import com.metaparadigm.jsonrpc.JSONRPCResult;
import com.suucee.www.service.user.AdminColumnService;
import com.suucee.www.service.user.AdminImagesService;
import com.suucee.www.service.user.AdminLinkService;
import com.suucee.www.service.user.AdminNewsService;


/**
 * A controller for Spring MVC and JSON-RPC-JAVA integration
 * 
 * @author Dinstone
 * 
 */
@RequestMapping("/api")
@Controller
public class JsonRpcController {
	private final static int buf_size = 4096;

	@Autowired
	private SessionService sessionService;
	@Autowired
	private UserService userService;
	@Autowired
	private AdminUserService adminUserService;
	@Autowired
	private AdminNewsService adminNewsService;
	@Autowired
	private AdminLinkService adminLinkService;
	@Autowired
	private AdminColumnService adminColumnService;
	@Autowired
	private AdminImagesService  adminImagesService;
	@Autowired
	private UserDepositService userDepositService;
	@Autowired
	private AdminCheckService adminCheckService;
	@Autowired
	private AdminDepositService adminDepositService;
	@Autowired
	private UserAccountService userAccountService;
	@Autowired
	private UserRankService userRankService;
	@Autowired
	private UserRebateService userRebateService;
	@Autowired
	private AdminAdminService adminAdminService;
	@Autowired
	private AdminSettingService adminSettingService;
	
    
    @Autowired
	private AdminCheckBankAccountService adminCheckBankAccountService;
    
    
	@Autowired
	private TokenDao tokenDao;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.web.servlet.mvc.AbstractController#handleRequestInternal(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	@RequestMapping(value = "v1.do")
	protected final ModelAndView handleRequestInternal(
			HttpServletRequest req,
			HttpServletResponse res) throws Exception {
		//没有初始化时则初始化
		if (req.getSession().getAttribute("JSONRPCBridge") == null) {
			registerAjaxServices(req);
		}
		
		// get the parameters
		String param = getParameter(req);
		// get the Json RPC Bridge from session
		JSONRPCBridge bridge = getBridge(req);
		// call the RPC method
		JSONRPCResult jsonRes = null;

		
		try {
			JSONObject jsono = new JSONObject(param);
			
			//如果是登錄或註銷，直接放行，否則要檢查是否已經登錄
			String method = jsono.getString("method");
			
			
			if (method.indexOf("sessionService.") == 0 ||
				sessionService.checkLogined(req.getSession()) != null ||
				checkToken(req, res))
			{
				//登录验证通过（sessionService下的方法无需登录）
				//检查登录状态(JSESSIONID)或TOKEN通过
				//调用
				jsonRes = bridge.call(new Object[] { req }, jsono);
			}
			else {
				//因未登錄而不放行，返回未登錄信息
				jsonRes = new JSONRPCResult(-10000, null, "请先登录，或者您的登录已超时！");
			}
		} catch (Exception e) {
			jsonRes = new JSONRPCResult(JSONRPCResult.CODE_ERR_PARSE, null, JSONRPCResult.MSG_ERR_PARSE);
		}

		// create view
		return new ModelAndView(new JsonRpcView(), "JsonResult", jsonRes);
	}
	
	/**
	 * 检查Token
	 * @param req
	 * @param resp
	 * @return
	 */
	private boolean checkToken(HttpServletRequest req, HttpServletResponse resp) {
		Cookie[] cookies = req.getCookies();
        
        if (cookies == null) {
            return false;
        } else {
            for (Cookie cookie : cookies) {
                String name = cookie.getName();
                String value = cookie.getValue();
                
                if (name.equals("TOKEN")) {
                	Tokens token = tokenDao.findByUUID(value);
                	
                	if (token != null) {
                		//检查IP、过期、禁用状态
                		if ((token.getExpirationTime() == null || token.getExpirationTime().after(new Date()))
                			&& req.getRemoteAddr().equals(token.getIpAddress()) &&
                			
                			(token.getUser() != null && !token.getUser().isDisable() || 
                			token.getAdmin() != null && !token.getAdmin().isDisabled())
                			) {
                			
                			//通过了，更新最新授权时间
                			token.setLastAuthorizationTime(new Date());
                			tokenDao.commit();
                			//设置登录标识
                			
                			PojoSession pojo = null;
                			if (token.getUser() != null) {
                				pojo = new PojoSession(token.getUser());
                			} else if (token.getAdmin() != null) {
                				pojo = new PojoSession(token.getAdmin());
                			}
                			
                			req.getSession().setAttribute(SessionServiceImpl.SESSION_KEY, pojo);
                			req.getSession().setAttribute(SessionServiceImpl.SESSION_USER_KEY, token.getUser());
                			req.getSession().setAttribute(SessionServiceImpl.SESSION_ADMIN_KEY, token.getAdmin());
                			
                			return true;
                		} else {
                			//未通过，删除令牌
                			tokenDao.delete(token);
                			tokenDao.commit();
                			
                			//删除Cookie
                            cookie.setMaxAge(0);
                            cookie.setValue(null);
                            resp.addCookie(cookie);
                		}
                		
                	}
                	
            		//找到处理token后，退出循环
            		break;
                }
            }
        }
        
        return false;
	}

	private JSONRPCBridge getBridge(HttpServletRequest request) {
		HttpSession session = request.getSession(true);
		JSONRPCBridge jsonBridge = null;
		jsonBridge = (JSONRPCBridge) session.getAttribute("JSONRPCBridge");

		if (jsonBridge == null) {
			jsonBridge = new JSONRPCBridge();
			session.setAttribute("JSONRPCBridge", jsonBridge);
			session.setAttribute("RemoteAddr", request.getRemoteAddr());
		}

		return jsonBridge;
	}

	/**
	 * get the parameter from request
	 * 
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	private String getParameter(HttpServletRequest request)
			throws UnsupportedEncodingException, IOException {
		String charset = request.getCharacterEncoding();
		if (charset == null) {
			charset = "UTF-8";
		}
		BufferedReader in = new BufferedReader(new InputStreamReader(request
				.getInputStream(), charset));

		// Read the request
		CharArrayWriter data = new CharArrayWriter();
		char buf[] = new char[buf_size];
		int ret;
		while ((ret = in.read(buf, 0, buf_size)) != -1) {
			data.write(buf, 0, ret);
		}
		return data.toString();
	}

	/**
	 * register AJAX Services for this controller
	 * 
	 * @param req
	 * @param res
	 * @throws Exception
	 * @throws ServletException
	 */
	private final void registerAjaxServices(HttpServletRequest req) {
		// use session bridge
		JSONRPCBridge bridge = getBridge(req);
		
		//基本Services
		bridge.registerObject("sessionService", sessionService);
		
		//用户Services
		bridge.registerObject("userService", userService);
		
		//管理Services
		bridge.registerObject("adminUserService", adminUserService);
		bridge.registerObject("adminColumnService", adminColumnService);
		bridge.registerObject("adminNewsService", adminNewsService);
		bridge.registerObject("adminLinkService", adminLinkService);
		bridge.registerObject("adminImagesService", adminImagesService);
		bridge.registerObject("userDepositService", userDepositService);
		bridge.registerObject("adminCheckService", adminCheckService);
		bridge.registerObject("adminDepositService", adminDepositService);
		bridge.registerObject("userAccountService", userAccountService);
		bridge.registerObject("userRankService", userRankService);
/*		bridge.registerObject("userCustomerService", userCustomerService);
*/		bridge.registerObject("userRebateService", userRebateService);
		bridge.registerObject("adminAdminService", adminAdminService);
		bridge.registerObject("adminSettingService", adminSettingService);
		bridge.registerObject("adminCheckBankAccountService", adminCheckBankAccountService);
	}
}
