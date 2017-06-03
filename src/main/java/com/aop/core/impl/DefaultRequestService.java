package com.aop.core.impl;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.aop.core.AbstractRopRequest;
import com.aop.core.InfoService;
import com.aop.enums.MessageFormat;
import com.aop.core.RequestService;
import com.aop.core.RopContext;
import com.aop.exception.RopException;
import com.aop.core.ServiceMethodDefinition;
import com.aop.core.ServiceMethodHandler;
import com.aop.core.SystemParameterNames;
import com.aop.enums.SignType;
import com.aop.util.RopMD5Util;
import com.xunxintech.ruyue.coach.io.json.JSONUtil;
import com.xunxintech.ruyue.coach.io.string.StringUtil;
import com.xunxintech.ruyue.coach.io.xml.XMLParser;

public class DefaultRequestService implements RequestService
{

	private RopContext ropContext;

	private InfoService infoService;

	private ServletRequestContextBuilder servletRequestContextBuilder;

	public DefaultRequestService(RopContext ropContext)
	{
		servletRequestContextBuilder = new ServletRequestContextBuilder();
		infoService = new DefaultInfoService();
		this.ropContext = ropContext;
	}

	@Override
	public String processRequest(HttpServletRequest request, HttpServletResponse response, String method, String v, String param) throws Exception
	{
		SimpleRopRequestContext requestContext = servletRequestContextBuilder.buildBySysParams(ropContext, request, response, method, v);

		ServiceMethodDefinition serviceMethod = requestContext.getServiceMethodDefinition();

		if (serviceMethod.isObsoleted()) { throw new RopException("The service method has been deprecated"); }

		MessageFormat requestFormat = MessageFormat.getFormatValue(request.getContentType());
		if (requestFormat == null) { throw new RopException("Unsupported contentType request type"); }

		// 签名认证
		if (!serviceMethod.isIgnoreSign())
		{
			Map<String, String> paramValues = new HashMap<String, String>();
			paramValues.put(SystemParameterNames.METHOD, method);
			paramValues.put(SystemParameterNames.VERSION, v);
			paramValues.put(SystemParameterNames.FORMAT, requestContext.getFormat());
			paramValues.put(SystemParameterNames.TIMESTAMP, String.valueOf(requestContext.getTimestamp()));
			paramValues.put(SystemParameterNames.TOKEN, requestContext.getToken());
			paramValues.put(SystemParameterNames.PARAM, param);
			
			SignType signType = SignType.get(requestContext.getSignType());
			String sign = RopMD5Util.sign(paramValues, infoService.getSecret(requestContext.getToken()), signType.getValue());
			
			if (StringUtil.notEquals(sign, requestContext.getSign())) { throw new RopException("signature failed"); }
		}
		// //登录判断,因应用auto2.0这句不执行
		// if(serviceMethod.isNeedInSession()){
		//
		// }
		AbstractRopRequest requestObject = null;
		ServiceMethodHandler serviceMethodHandler = requestContext.getServiceMethodHandler();
		if (MessageFormat.json.equals(requestFormat))
		{
			requestObject = (AbstractRopRequest) JSONUtil.objectMapper.readValue(param, serviceMethodHandler.getRequestType());
		}
		else
		{
			requestObject = (AbstractRopRequest) XMLParser.xmlMapper.readValue(param, serviceMethodHandler.getRequestType());
		}

		requestObject.setRopRequestContext(requestContext);

		Object result = serviceMethodHandler.getHandlerMethod().invoke(serviceMethodHandler.getHandler(), requestObject);

		MessageFormat responseFormat = MessageFormat.getFormat(requestContext.getFormat(), requestFormat);

		if (MessageFormat.json.equals(responseFormat)) { return JSONUtil.toJackson(result); }
		return XMLParser.toXmlString(result);
	}

}
