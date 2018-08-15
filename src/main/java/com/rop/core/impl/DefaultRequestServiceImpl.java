package com.rop.core.impl;

import com.rop.core.*;
import com.rop.enums.LogStatus;
import com.rop.enums.MessageFormat;
import com.rop.enums.SignType;
import com.rop.exception.RopException;
import com.rop.marshaller.MarshallerManager;
import com.rop.util.RopConstant;
import com.rop.util.RopUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author xzf
 */
public class DefaultRequestServiceImpl implements RequestService
{
	private static Logger logger = LoggerFactory.getLogger(DefaultRequestServiceImpl.class);

	private RopContext ropContext;

	private InfoService infoService;

	private LogService logService;

	private MarshallerManager marshallerManager;

	private Validator validator;

	private ServletRequestContextBuilder servletRequestContextBuilder;

	public DefaultRequestServiceImpl(RopContext ropContext, MarshallerManager marshallerManager)
	{
		servletRequestContextBuilder = new ServletRequestContextBuilder();
		infoService = new DefaultInfoServiceImpl();
		this.ropContext = ropContext;
		this.marshallerManager = marshallerManager;
		this.logService = new LogServiceImpl();

		initValidator();
	}

	@Override
	public String processRequest(HttpServletRequest request, HttpServletResponse response, String module, String method, String v, String appId, String param) throws Exception
	{
		long beginTime = System.currentTimeMillis();
		if(logger.isDebugEnabled()){
			logger.debug("processRequest method:{}, version:{}, param:{}, requestId:{}", method, v, param, request.getHeader(SystemParameterNames.REQUEST_ID));
		}
		MessageFormat requestFormat = MessageFormat.getFormatValue(request.getContentType());
		if (requestFormat == null) { throw new RopException(RopConstant.EXCEPTION_NOT_CONTENT_TYPE_MESSAGE); }

		SimpleRopRequestContext requestContext = servletRequestContextBuilder.buildBySysParams(ropContext, request, response, method, v, requestFormat, module, appId);

		ServiceMethodDefinition serviceMethod = requestContext.getServiceMethodDefinition();

		if (serviceMethod.isObsoleted()) { throw new RopException(RopConstant.METHOD_OBSOLETED_MESSAGE); }

		// 签名认证
		if (!serviceMethod.isIgnoreSign() && !"xzf".equals(appId))
		{
			validatorSign(method, v, param, requestContext);

		}
		// //登录判断,因应用auto2.0这句不执行
		// if(serviceMethod.isNeedInSession()){
		//
		// }
		ServiceMethodHandler serviceMethodHandler = requestContext.getServiceMethodHandler();

		AbstractRopRequest requestObject = marshallerManager.readvalue(param, serviceMethodHandler.getRequestType(), requestFormat);

		requestObject.setRopRequestContext(requestContext);
		//去除换行符
		param = handleParam(param);

		String responseString = RopConstant.EMPTY_STRING;

		try{
			//验证输入参数
			validateRequestObject(requestObject);

			Object result = serviceMethodHandler.getHandlerMethod().invoke(serviceMethodHandler.getHandler(), requestObject);
			responseString = marshallerManager.messageFormat(requestFormat, result);
		}catch(Exception e){

			Throwable cause = e.getCause();
			if(cause == null){
				cause = e;
			}

			responseString = cause.getMessage();
			logger.error("processRequest occurred exception,  method:{}, version:{}, module:{}, appId:{}, requestId:{}, message:{}", method, v, module, requestContext.getAppId(), requestContext.getRequestId(), responseString);
			if(e instanceof InvocationTargetException){
				throw (Exception)((InvocationTargetException) e).getTargetException();
			}
			if(cause != null){
				throw (Exception)cause;
			}
			throw e;
		}finally {
			//判断是否采集日志
			if(serviceMethodHandler.getCollectType().isCollect(logger.isDebugEnabled())){
				//获取logSn，优先级由高到低：logSn->orderNo->requestId
				String logSn = requestObject.getLogNo() == null||requestObject.getLogNo().trim().length() == 0 ?
						(requestObject.getOrderNo() ==null || requestObject.getOrderNo().trim().length() == 0?requestContext.getRequestId() : requestObject.getOrderNo())
						: requestObject.getLogNo();
				//进行日志采集
				collectLog(method, v, param, module, logSn, requestContext.getAppId(), System.currentTimeMillis() - beginTime, responseString, LogStatus.failure);
			}
		}

		long executeTime = System.currentTimeMillis() - beginTime;
		String log = responseString.length() > 256 ? responseString.substring(0, 256) : responseString;
		if(logger.isDebugEnabled()){
			logger.debug("processRequest end method:{}, version:{}, module:{}, time:{}, appId:{}, requestId:{}, response:{} ...", method, v, module, executeTime, requestContext.getAppId(), requestContext.getRequestId(), log);
		}
		response.setHeader("Content-Type", requestFormat.getValue());
		return responseString;
	}

	private void collectLog(String method, String v, String param, String module, String logSn, String appId, long l, String response, LogStatus logStatus) {
//		logger.info("process_request_collect |{}-{}-{}|{}|{}|{}|{}|{}|", module, method, v, appId
//						, logSn, l, param, response);
		logService.logRecord(logSn, param, response, logStatus, LocalDateTime.now(), method+v);
	}

	/**
	 * 去除\\n \\t
	 * @param param
	 * @return
	 */
	private String handleParam(String param) {
		if(param.indexOf(RopConstant.REGEX_R_N) > 0){
			param = param.replaceAll(RopConstant.REGEX_R_N, RopConstant.EMPTY_STRING);
		}
		if(param.indexOf(RopConstant.REGEX_R) > 0){
			param = param.replaceAll(RopConstant.REGEX_R, RopConstant.EMPTY_STRING);
		}
		if(param.indexOf(RopConstant.REGEX_N) > 0){
			param = param.replaceAll(RopConstant.REGEX_N, RopConstant.EMPTY_STRING);
		}
		return param;
	}

	private void validatorSign(String method, String v, String param, SimpleRopRequestContext requestContext)
	{
		Map<String, String> paramValues = new HashMap<String, String>(16);
		paramValues.put(SystemParameterNames.METHOD, method);
		paramValues.put(SystemParameterNames.VERSION, v);
		paramValues.put(SystemParameterNames.REQUEST_ID, requestContext.getRequestId());
		paramValues.put(SystemParameterNames.TOKEN, requestContext.getToken());
		paramValues.put(SystemParameterNames.PARAM, param);
		paramValues.put(SystemParameterNames.TIMESTAMP, String.valueOf(requestContext.getTimestamp()));
		paramValues.put(SystemParameterNames.APP_ID, requestContext.getAppId());

		SignType signType = SignType.get(requestContext.getSignType());
		
		String sign = RopUtil.sign(paramValues, infoService.getSecret(requestContext.getAppId()), signType.getValue());
		if(logger.isDebugEnabled()){
			logger.debug("validatorSign sign:{}, signType:{}", sign, signType);
		}
		if (RopUtil.notEqualsIgnoreCase(sign, requestContext.getSign())) { throw new RopException(RopConstant.SIGNATURE_FAILED_MESSAGE); }
	}

	public <T> void validateRequestObject(T obj)
	{
		List<String> list = null;
		Set<ConstraintViolation<T>> set = validator.validate(obj);
		if (set != null && !set.isEmpty())
		{
			list = new ArrayList<String>();
			for (ConstraintViolation<T> cv : set)
			{
				list.add(cv.getPropertyPath().toString() + cv.getMessage());
			}
		}
		if (list != null && !list.isEmpty()) { throw new RopException(list.stream().collect(Collectors.joining(RopConstant.SEMICOLON))); }
	}

	/**
	 * init validator
	 */
	public void initValidator()
	{
		LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();
		localValidatorFactoryBean.afterPropertiesSet();
		this.validator = localValidatorFactoryBean;
	}

	
	public DefaultRequestServiceImpl setInfoService(InfoService infoService)
	{
		this.infoService = infoService;
		return this;
	}

	public DefaultRequestServiceImpl setLogService(LogService logService) {
		this.logService = logService;
		return this;

	}
}
