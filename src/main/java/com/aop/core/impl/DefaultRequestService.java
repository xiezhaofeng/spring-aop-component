package com.aop.core.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.aop.core.AbstractRopRequest;
import com.aop.core.InfoService;
import com.aop.core.RequestService;
import com.aop.core.RopContext;
import com.aop.core.ServiceMethodDefinition;
import com.aop.core.ServiceMethodHandler;
import com.aop.core.SystemParameterNames;
import com.aop.enums.MessageFormat;
import com.aop.enums.SignType;
import com.aop.exception.RopException;
import com.aop.marshaller.JsonMarshallerService;
import com.aop.marshaller.MarshallerManager;
import com.aop.marshaller.XmlMarshallerService;
import com.aop.util.RopUtil;

public class DefaultRequestService implements RequestService {

	public static final String EMPTY_STRING = "";
	public static final String SEMICOLON = ";";
	private RopContext ropContext;

	private MarshallerManager marshallerManager;

	private InfoService infoService;

	private Validator validator;

	private ServletRequestContextBuilder servletRequestContextBuilder;

	public DefaultRequestService(RopContext ropContext) {
		servletRequestContextBuilder = new ServletRequestContextBuilder();
		//init info serviced
		infoService = new DefaultInfoService();
		this.ropContext = ropContext;
		initMarshallerManager();
		initValudator();
	}

	@Override
	public String processRequest(HttpServletRequest request, HttpServletResponse response, String method, String v, String param, String methodPrefix) throws Exception
	{
		MessageFormat requestFormat = MessageFormat.getFormatValue(request.getContentType());
		if (requestFormat == null) { throw new RopException("Unsupported contentType request type"); }
		
		boolean isFormatJson = MessageFormat.json.equals(requestFormat);
		
		try
		{
			SimpleRopRequestContext requestContext = servletRequestContextBuilder.buildBySysParams(ropContext, request, response, method, v, requestFormat, methodPrefix);

			ServiceMethodDefinition serviceMethod = requestContext.getServiceMethodDefinition();

			if (serviceMethod.isObsoleted()) { throw new RopException("The service method has been deprecated"); }
			
			// 签名认证
			if (!serviceMethod.isIgnoreSign())
			{
				Map<String, String> paramValues = new HashMap<String, String>();
				paramValues.put(SystemParameterNames.METHOD, method);
				paramValues.put(SystemParameterNames.VERSION, v);
				paramValues.put(SystemParameterNames.REQUEST_ID, requestContext.getRequestId());
				paramValues.put(SystemParameterNames.TOKEN, requestContext.getToken());
				paramValues.put(SystemParameterNames.PARAM, param);
				paramValues.put(SystemParameterNames.TIMESTAMP, String.valueOf(requestContext.getTimestamp()));
				
				SignType signType = SignType.get(requestContext.getSignType());
				String sign = RopUtil.sign(paramValues, infoService.getSecret(requestContext.getToken()), signType.getValue());
				
				if (RopUtil.notEquals(sign, requestContext.getSign()))
				{ 
					throw new RopException("signature failed");
				}
			}
			// //登录判断,因应用auto2.0这句不执行
			// if(serviceMethod.isNeedInSession()){
			//
			// }
			ServiceMethodHandler serviceMethodHandler = requestContext.getServiceMethodHandler();
			
			AbstractRopRequest requestObject = marshallerManager.readvalue(param, serviceMethodHandler.getRequestType(), requestFormat);
			
			validate(requestObject);

			requestObject.setRopRequestContext(requestContext);

			Object result = serviceMethodHandler.getHandlerMethod().invoke(serviceMethodHandler.getHandler(), requestObject);

			return marshallerManager.messageFormat(requestFormat, result);
		}
		catch (Exception e)
		{
			throw new RopException(e instanceof RopException?e.getMessage():"Processing failed, returning null data.", isFormatJson);
		}
	}

	public  <T> void validate(T obj){
		String error = EMPTY_STRING;
		Set<ConstraintViolation<T>> set = validator.validate(obj);
		if(set != null && !set.isEmpty() ){
			for(ConstraintViolation<T> cv : set){
				error += cv.getPropertyPath().toString()+cv.getMessage()+ SEMICOLON;
			}
		}
		if(!EMPTY_STRING.equals(error)){
			throw new RopException(error);
		}
	}

	public void initMarshallerManager(){
		marshallerManager = new MarshallerManager();
		marshallerManager.addMarshaller(MessageFormat.json, new JsonMarshallerService());
		marshallerManager.addMarshaller(MessageFormat.xml, new XmlMarshallerService());
	}

	/**
	 * init validator
	 */
	public void initValudator(){
		LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();
		localValidatorFactoryBean.afterPropertiesSet();
		this.validator = localValidatorFactoryBean;
	}
}
