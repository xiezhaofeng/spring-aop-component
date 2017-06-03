package com.aop.core.impl;

import com.aop.core.*;
import com.aop.enums.MessageFormat;
import com.aop.enums.SignType;
import com.aop.exception.RopException;
import com.aop.marshaller.JsonMarshallerService;
import com.aop.marshaller.MarshallerManager;
import com.aop.marshaller.XmlMarshallerService;
import com.aop.util.RopMD5Util;
import com.xunxintech.ruyue.coach.io.string.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DefaultRequestService implements RequestService {

	public static final String EMPTY_STRING = "";
	public static final String SEMICOLON = ";";
	private RopContext ropContext;

	private MarshallerManager marshallerManager;

	private InfoService infoService;

	private FormattingConversionService conversionService;

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
	public String processRequest(HttpServletRequest request, HttpServletResponse response, String method, String v, String param) throws Exception {
		SimpleRopRequestContext requestContext = servletRequestContextBuilder.buildBySysParams(ropContext, request, response, method, v);

		ServiceMethodDefinition serviceMethod = requestContext.getServiceMethodDefinition();

		if (serviceMethod.isObsoleted()) {
			throw new RopException("The service method has been deprecated");
		}

		MessageFormat requestFormat = MessageFormat.getFormatValue(request.getContentType());
		if (requestFormat == null) {
			throw new RopException("Unsupported contentType request type");
		}

		// 签名认证
		if (!serviceMethod.isIgnoreSign()) {
			Map<String, String> paramValues = new HashMap<String, String>();
			paramValues.put(SystemParameterNames.METHOD, method);
			paramValues.put(SystemParameterNames.VERSION, v);
			paramValues.put(SystemParameterNames.FORMAT, requestContext.getFormat());
			paramValues.put(SystemParameterNames.TIMESTAMP, String.valueOf(requestContext.getTimestamp()));
			paramValues.put(SystemParameterNames.TOKEN, requestContext.getToken());
			paramValues.put(SystemParameterNames.PARAM, param);

			SignType signType = SignType.get(requestContext.getSignType());
			String sign = RopMD5Util.sign(paramValues, infoService.getSecret(requestContext.getToken()), signType.getValue());

			if (StringUtil.notEquals(sign, requestContext.getSign())) {
				throw new RopException("signature failed");
			}
		}
		// //登录判断,因应用auto2.0这句不执行
		// if(serviceMethod.isNeedInSession()){
		//
		// }
		ServiceMethodHandler serviceMethodHandler = requestContext.getServiceMethodHandler();

		AbstractRopRequest requestObject  = marshallerManager.readvalue(param, serviceMethodHandler.getRequestType(), requestFormat);
//		if (MessageFormat.json.equals(requestFormat)) {
//			requestObject = (AbstractRopRequest) JSONUtil.objectMapper.readValue(param, serviceMethodHandler.getRequestType());
//		} else {
//			requestObject = (AbstractRopRequest) XMLParser.xmlMapper.readValue(param, serviceMethodHandler.getRequestType());
//		}
		validate(requestObject);

		requestObject.setRopRequestContext(requestContext);

		Object result = serviceMethodHandler.getHandlerMethod().invoke(serviceMethodHandler.getHandler(), requestObject);

		MessageFormat responseFormat = MessageFormat.getFormat(requestContext.getFormat(), requestFormat);

		return marshallerManager.messageFormat(responseFormat, result);
	}

	public  <T> void validate(T obj){
		String error = EMPTY_STRING;
		Set<ConstraintViolation<T>> set = validator.validate(obj);
		if( CollectionUtils.isNotEmpty(set) ){
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
