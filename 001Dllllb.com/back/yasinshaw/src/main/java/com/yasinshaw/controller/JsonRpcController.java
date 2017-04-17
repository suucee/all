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
package com.yasinshaw.controller;

import java.io.BufferedReader;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.metaparadigm.jsonrpc.JSONRPCBridge;
import com.metaparadigm.jsonrpc.JSONRPCResult;
import com.yasinshaw.service.StudentService;


/**
 * A controller for Spring MVC and JSON-RPC-JAVA integration
 * 
 * @author Dinstone
 * 
 */
@RequestMapping("/")
@Controller
public class JsonRpcController {
	private final static int buf_size = 4096;

	@Autowired
	StudentService studentService;
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.web.servlet.mvc.AbstractController#handleRequestInternal(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	@RequestMapping(value = "rpc.do")
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
		JSONObject jsono = new JSONObject(param);
		jsonRes = bridge.call(new Object[] { req }, jsono);
		// create view
		return new ModelAndView(new JsonRpcView(), "JsonResult", jsonRes);
	}
	
	/**
	 * 检查Token
	 * @param req
	 * @param resp
	 * @return
	 */

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
		
//		基本Services
		bridge.registerObject("studentService", studentService);
		
		//用户Services
		//bridge.registerObject("userService", userService);
		
		//管理Services
//		bridge.registerObject("userService", userService);
	}
}
