package com.rop.core;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * 
  * @Title: RequestService.java 
  * @Package com.rop
  * @Description  TODO
  * @author  XZF
  * @date 2017年5月26日 下午5:47:03 
  * @version   
  *
  *
 */
public interface RequestService
{
	/**
	 * process request
	 * @param request
	 * @param response
	 * @param module
	 * @param method
	 * @param v
	 * @param appId
	 * @param param
	 * @return
	 * @throws Exception
	 */
	String processRequest(HttpServletRequest request, HttpServletResponse response, String module, String method, String v, String appId, String param) throws Exception;
}
