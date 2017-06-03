package com.aop.core.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.propertyeditors.LocaleEditor;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.ServletRequestDataBinder;

import com.aop.core.AbstractRopRequest;
import com.aop.enums.MessageFormat;
import com.aop.core.RequestContextBuilder;
import com.aop.core.RopContext;
import com.aop.core.RopRequest;
import com.aop.core.RopRequestContext;
import com.aop.core.ServiceMethodHandler;
import com.aop.core.SystemParameterNames;
import com.aop.enums.HttpAction;

/**
 * 
  * @Title: ServletRequestContextBuilder.java 
  * @Package com.aop.core.impl
  * @Description  TODO
  * @author  XZF
  * @date 2017年5月27日 下午3:10:21 
  * @version   
  *
  *
 */
public class ServletRequestContextBuilder implements RequestContextBuilder {

    //通过前端的负载均衡服务器时，请求对象中的IP会变成负载均衡服务器的IP，因此需要特殊处理，下同。
    public static final String X_REAL_IP = "X-Real-IP";

    public static final String X_FORWARDED_FOR = "X-Forwarded-For";

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private FormattingConversionService conversionService;

    private Validator validator;

    public ServletRequestContextBuilder() {
        
    }
    
    public ServletRequestContextBuilder(FormattingConversionService conversionService) {
        this.conversionService = conversionService;
    }

    public SimpleRopRequestContext buildBySysParams(RopContext ropContext, HttpServletRequest request,HttpServletResponse response) {

        SimpleRopRequestContext requestContext = new SimpleRopRequestContext(ropContext);

        //设置请求对象及参数列表
        requestContext.setRawRequestObject(request);
        if (response != null) {
            requestContext.setRawResponseObject(response);
        }
        requestContext.setAllParams(getRequestParams(request));
        requestContext.setIp(getRemoteAddr(request)); //感谢melin所指出的BUG

        //设置服务的系统级参数
        requestContext.setAppId(request.getParameter(SystemParameterNames.APP_ID));
        requestContext.setSessionId(request.getParameter(SystemParameterNames.SESSION_ID));
        requestContext.setMethod(request.getParameter(SystemParameterNames.METHOD));
        requestContext.setToken(request.getParameter(SystemParameterNames.TOKEN));
        requestContext.setTimestamp(Long.valueOf(request.getParameter(SystemParameterNames.TIMESTAMP)));
        requestContext.setVersion(request.getParameter(SystemParameterNames.VERSION));
        requestContext.setLocale(getLocale(request));
        requestContext.setFormat(getFormat(request));
        requestContext.setMessageFormat(getResponseFormat(request));
        requestContext.setSign(request.getParameter(SystemParameterNames.SIGN));
        requestContext.setHttpAction(HttpAction.fromValue(request.getMethod()));

        //设置服务处理器
        ServiceMethodHandler serviceMethodHandler =
                ropContext.getServiceMethodHandler(requestContext.getMethod(), requestContext.getVersion());
        requestContext.setServiceMethodHandler(serviceMethodHandler);

        return requestContext;
    }
    
    public SimpleRopRequestContext buildBySysParams(RopContext ropContext, HttpServletRequest request,HttpServletResponse response, String method, String version) {

        SimpleRopRequestContext requestContext = new SimpleRopRequestContext(ropContext);

        //设置请求对象及参数列表
        requestContext.setRawRequestObject(request);
        if (response != null) {
            requestContext.setRawResponseObject(response);
        }
        requestContext.setAllParams(getRequestParams(request));
        requestContext.setIp(getRemoteAddr(request)); //感谢melin所指出的BUG

        //设置服务的系统级参数
        requestContext.setAppId(request.getParameter(SystemParameterNames.APP_ID));
        requestContext.setSessionId(request.getParameter(SystemParameterNames.SESSION_ID));
        requestContext.setMethod(method);
        requestContext.setToken(request.getParameter(SystemParameterNames.TOKEN));
        requestContext.setTimestamp(Long.valueOf(request.getParameter(SystemParameterNames.TIMESTAMP)));
        requestContext.setVersion(version);
        requestContext.setLocale(getLocale(request));
        requestContext.setFormat(getFormat(request));
        requestContext.setMessageFormat(getResponseFormat(request));
        requestContext.setSign(request.getParameter(SystemParameterNames.SIGN));
        requestContext.setHttpAction(HttpAction.fromValue(request.getMethod()));

        //设置服务处理器
        ServiceMethodHandler serviceMethodHandler =
                ropContext.getServiceMethodHandler(requestContext.getMethod(), requestContext.getVersion());
        requestContext.setServiceMethodHandler(serviceMethodHandler);

        return requestContext;
    }

    private String getRemoteAddr(HttpServletRequest request) {
        String remoteIp = request.getHeader(X_REAL_IP); //nginx反向代理
        if (StringUtils.hasText(remoteIp)) {
            return remoteIp;
        } else {
            remoteIp = request.getHeader(X_FORWARDED_FOR);//apache反射代理
            if (StringUtils.hasText(remoteIp)) {
                String[] ips = remoteIp.split(",");
                for (String ip : ips) {
                    if (!"null".equalsIgnoreCase(ip)) {
                        return ip;
                    }
                }
            }
            return request.getRemoteAddr();
        }
    }

    /**
     * 将{@link HttpServletRequest}的数据绑定到{@link com.rop.RopRequestContext}的{@link com.rop.RopRequest}中，同时使用
     * JSR 303对请求数据进行校验，将错误信息设置到{@link com.rop.RopRequestContext}的属性列表中。
     *
     * @param ropRequestContext
     */

    public RopRequest buildRopRequest(RopRequestContext ropRequestContext) {
        AbstractRopRequest ropRequest = null;
        if (ropRequestContext.getServiceMethodHandler().isRopRequestImplType()) {
            HttpServletRequest request =
                    (HttpServletRequest) ropRequestContext.getRawRequestObject();
            BindingResult bindingResult = doBind(request, ropRequestContext.getServiceMethodHandler().getRequestType());
            ropRequest = buildRopRequestFromBindingResult(ropRequestContext, bindingResult);

            List<ObjectError> allErrors = bindingResult.getAllErrors();
            ropRequestContext.setAttribute(SimpleRopRequestContext.SPRING_VALIDATE_ERROR_ATTRNAME, allErrors);
        } else {
            ropRequest = new DefaultRopRequest();
        }
        ropRequest.setRopRequestContext(ropRequestContext);
        return ropRequest;
    }


    private String getFormat(HttpServletRequest servletRequest) {
        String messageFormat = servletRequest.getParameter(SystemParameterNames.FORMAT);
        if (messageFormat == null) {
            return MessageFormat.xml.name();
        } else {
            return messageFormat;
        }
    }

    public static Locale getLocale(HttpServletRequest webRequest) {
        if (webRequest.getParameter(SystemParameterNames.LOCALE) != null) {
            try {
                LocaleEditor localeEditor = new LocaleEditor();
                localeEditor.setAsText(webRequest.getParameter(SystemParameterNames.LOCALE));
                Locale locale = (Locale) localeEditor.getValue();
                if (isValidLocale(locale)) {
                    return locale;
                }
            } catch (Exception e) {
                return Locale.SIMPLIFIED_CHINESE;
            }
        }
        return Locale.SIMPLIFIED_CHINESE;
    }

    private static boolean isValidLocale(Locale locale) {
       return false;
    }


    public static MessageFormat getResponseFormat(HttpServletRequest servletRequest) {
        String messageFormat = servletRequest.getParameter(SystemParameterNames.FORMAT);
        if (MessageFormat.isValidFormat(messageFormat)) {
            return MessageFormat.getFormat(messageFormat);
        } else {
            return MessageFormat.xml;
        }
    }

    private AbstractRopRequest buildRopRequestFromBindingResult(RopRequestContext ropRequestContext, BindingResult bindingResult) {
        AbstractRopRequest ropRequest = (AbstractRopRequest) bindingResult.getTarget();
        if (ropRequest instanceof AbstractRopRequest) {
            AbstractRopRequest abstractRopRequest = ropRequest;
            abstractRopRequest.setRopRequestContext(ropRequestContext);
        } else {
            logger.warn(ropRequest.getClass().getName() + "不是扩展于" + AbstractRopRequest.class.getName() +
                    ",无法设置" + RopRequestContext.class.getName());
        }
        return ropRequest;
    }

    private HashMap<String, String> getRequestParams(HttpServletRequest request) {
        Map srcParamMap = request.getParameterMap();
        HashMap<String, String> destParamMap = new HashMap<String, String>(srcParamMap.size());
        for (Object obj : srcParamMap.keySet()) {
            String[] values = (String[]) srcParamMap.get(obj);
            if (values != null && values.length > 0) {
                destParamMap.put((String) obj, values[0]);
            } else {
                destParamMap.put((String) obj, null);
            }
        }
        return destParamMap;
    }


    private BindingResult doBind(HttpServletRequest webRequest, Class<? extends RopRequest> requestType) {
        RopRequest bindObject = BeanUtils.instantiateClass(requestType);
        ServletRequestDataBinder dataBinder = new ServletRequestDataBinder(bindObject, "bindObject");
        dataBinder.setConversionService(getFormattingConversionService());
        dataBinder.setValidator(getValidator());
        dataBinder.bind(webRequest);
        dataBinder.validate();
        return dataBinder.getBindingResult();
    }

    private Validator getValidator() {
        if (this.validator == null) {
            LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();
            localValidatorFactoryBean.afterPropertiesSet();
            this.validator = localValidatorFactoryBean;
        }
        return this.validator;
    }

    public FormattingConversionService getFormattingConversionService() {
        return conversionService;
    }

    //默认的{@link RopRequest}实现类
    private class DefaultRopRequest extends AbstractRopRequest {
    }
}