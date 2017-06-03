/**
 *
 * 日    期：12-2-27
 */
package com.aop.core.impl;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import com.aop.enums.MessageFormat;
import com.aop.core.RopContext;
import com.aop.core.RopRequest;
import com.aop.core.RopRequestContext;
import com.aop.core.ServiceMethodDefinition;
import com.aop.core.ServiceMethodHandler;
import com.aop.enums.HttpAction;

/**
 * @author CXH
 * @version 1.0
 */
public class SimpleRopRequestContext implements RopRequestContext {

    public static final String SPRING_VALIDATE_ERROR_ATTRNAME = "$SPRING_VALIDATE_ERROR_ATTRNAME";

    private RopContext ropContext;

    private String method;

    private String version;

    private String appId;

    private String sessionId;

    private Locale locale;

    private String format;

    public static ThreadLocal<MessageFormat> messageFormat = new ThreadLocal<MessageFormat>();

    private String sign;
    
    private Long timestamp;
    
    private String token;
    
    private String signType;

    private Map<String, Object> attributes = new HashMap<String, Object>();

    private ServiceMethodHandler serviceMethodHandler;

    private Object ropResponse;

    private RopRequest ropRequest;

    private long serviceBeginTime = -1;

    private long serviceEndTime = -1;

    private String ip;

    private HttpAction httpAction;

    private Object rawRequestObject;

    private Object rawResponseObject;

    private Map<String, String> allParams;

    private String requestId = UUID.randomUUID().toString();


    public long getServiceBeginTime() {
        return this.serviceBeginTime;
    }


    public long getServiceEndTime() {
        return this.serviceEndTime;
    }


    public void setServiceBeginTime(long serviceBeginTime) {
        this.serviceBeginTime = serviceBeginTime;
    }


    public void setServiceEndTime(long serviceEndTime) {
        this.serviceEndTime = serviceEndTime;
    }


    public String getFormat() {
        return this.format;
    }

    public void setFormat(String format) {
        this.format = format;
    }


    public Object getRawRequestObject() {
        return this.rawRequestObject;
    }


    public Object getRawResponseObject() {
        return this.rawResponseObject;
    }

    public void setRawRequestObject(Object rawRequestObject) {
        this.rawRequestObject = rawRequestObject;
    }

    public void setRawResponseObject(Object rawResponseObject) {
        this.rawResponseObject = rawResponseObject;
    }

    public SimpleRopRequestContext(RopContext ropContext) {
        this.ropContext = ropContext;
        this.serviceBeginTime = System.currentTimeMillis();
    }



    public String getIp() {
        return this.ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }


    public RopContext getRopContext() {
        return ropContext;
    }


    public String getMethod() {
        return this.method;
    }

    public void setMethod(String method) {
        this.method = method;
    }


    public String getSessionId() {
        return this.sessionId;
    }


    public Locale getLocale() {
        return this.locale;
    }

    public ServiceMethodHandler getServiceMethodHandler() {
        return this.serviceMethodHandler;
    }

    public MessageFormat getMessageFormat() {
        return messageFormat.get();
    }

    public Object getRopResponse() {
        return this.ropResponse;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public void setServiceMethodHandler(ServiceMethodHandler serviceMethodHandler) {
        this.serviceMethodHandler = serviceMethodHandler;
    }

    public void setMessageFormat(MessageFormat messageFormat) {
        this.messageFormat.set(messageFormat);
    }

    public void setRopResponse(Object ropResponse) {
        this.ropResponse = ropResponse;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public Object getAttribute(String name) {
        return this.attributes.get(name);
    }

    public void setAttribute(String name, Object value) {
        this.attributes.put(name, value);
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public boolean isSignEnable() {
        return ropContext.isSignEnable() && !getServiceMethodDefinition().isIgnoreSign();
    }

    public ServiceMethodDefinition getServiceMethodDefinition() {
        return serviceMethodHandler.getServiceMethodDefinition();
    }

    public Map<String, String> getAllParams() {
        return this.allParams;
    }

    public void setAllParams(Map<String, String> allParams) {
        this.allParams = allParams;
    }

    public String getParamValue(String paramName) {
        if (allParams != null) {
            return allParams.get(paramName);
        } else {
            return null;
        }
    }

    public void setHttpAction(HttpAction httpAction) {
        this.httpAction = httpAction;
    }

    public HttpAction getHttpAction() {
        return this.httpAction;
    }

    public String getRequestId() {
        return this.requestId;
    }
	
	public String getAppId()
	{
		return appId;
	}
	
	public void setAppId(String appId)
	{
		this.appId = appId;
	}
	
	public Long getTimestamp()
	{
		return timestamp;
	}
	
	public void setTimestamp(Long timestamp)
	{
		this.timestamp = timestamp;
	}

	public String getToken()
	{
		return token;
	}

	public void setToken(String token)
	{
		this.token = token;
	}
	
	public String getSignType()
	{
		return signType;
	}
	
	public void setSignType(String signType)
	{
		this.signType = signType;
	}
    
    
}

