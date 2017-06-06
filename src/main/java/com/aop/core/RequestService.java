package com.aop.core;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * 
  * @Title: RequestService.java 
  * @Package com.aop 
  * @Description  TODO
  * @author  XZF
  * @date 2017年5月26日 下午5:47:03 
  * @version   
  *
  *
 */
public interface RequestService
{
	String processRequest(HttpServletRequest request, HttpServletResponse response, String method, String v, String param, String methodPrefix) throws Exception;
}
