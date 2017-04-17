package com.yasinshaw.controller;

import java.io.OutputStream;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.view.AbstractView;
public class JsonRpcView extends AbstractView {

	@Override
	protected void renderMergedOutputModel(@SuppressWarnings("rawtypes") Map model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		response.setContentType("text/html;charset=UTF-8");
		OutputStream out = response.getOutputStream();
		byte[] bout = new byte[0];
		Object jsonRes = model.get("JsonResult");
		if (jsonRes != null) {
			bout = jsonRes.toString().getBytes("UTF-8");
		}
		response.setIntHeader("Content-Length", bout.length);

		out.write(bout);
		out.flush();
		out.close();
	}

}
