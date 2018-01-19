/**
 *
 * 日    期：12-2-11
 */
package com.rop.core.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import com.rop.core.AbstractRopRequest;
import com.rop.core.RopContext;
import com.rop.exception.RopException;
import com.rop.core.RopRequest;
import com.rop.core.ServiceMethodDefinition;
import com.rop.core.ServiceMethodHandler;
import com.rop.core.SystemParameterNames;
import com.rop.annotation.RopMethod;
import com.rop.annotation.RopService;
import com.rop.enums.IgnoreSign;
import com.rop.enums.IgnoreSignType;
import com.rop.enums.NeedInSessionType;
import com.rop.enums.ObsoletedType;
import com.rop.enums.Temporary;

/**
 * <pre>
 *    ROP框架的上下文
 * </pre>
 *
 * @author CXH
 * @version 1.0
 */
public class DefaultRopContext implements RopContext
{

	protected static Logger logger = LoggerFactory.getLogger(DefaultRopContext.class);

	private final Map<String, ServiceMethodHandler> serviceHandlerMap = new ConcurrentHashMap<String, ServiceMethodHandler>();

	private final Set<String> serviceMethods = new HashSet<String>();

	private boolean signEnable;

	public DefaultRopContext(ApplicationContext context)
	{
		registerFromContext(context);
	}

	@Override
	public void addServiceMethod(String methodName, String version, ServiceMethodHandler serviceMethodHandler, String module)
	{
		serviceMethods.add(methodName);
		serviceHandlerMap.put(ServiceMethodHandler.methodWithVersion(module, methodName, version), serviceMethodHandler);
	}

	@Override
	public ServiceMethodHandler getServiceMethodHandler(String module, String methodName, String version)
	{
		return serviceHandlerMap.get(ServiceMethodHandler.methodWithVersion(module, methodName, version));
	}

	@Override
	public boolean isValidMethod(String methodName)
	{
		return serviceMethods.contains(methodName);
	}

	@Override
	public boolean isValidVersion(String module, String methodName, String version)
	{
		return serviceHandlerMap.containsKey(ServiceMethodHandler.methodWithVersion(module, methodName, version));
	}

	@Override
	public boolean isVersionObsoleted(String methodName, String version)
	{
		return false; // To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public Map<String, ServiceMethodHandler> getAllServiceMethodHandlers()
	{
		return serviceHandlerMap;
	}

	@Override
	public boolean isSignEnable()
	{
		return signEnable;
	}

	public void setSignEnable(boolean signEnable)
	{
		this.signEnable = signEnable;
	}

	/**
	 * 扫描Spring容器中的Bean，查找有标注{@link RopMethod}注解的服务方法，将它们注册到{@link RopContext}中缓存起来。
	 *
	 * @throws org.springframework.beans.BeansException
	 *
	 */
	private void registerFromContext(final ApplicationContext context) throws BeansException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("对Spring上下文中的Bean进行扫描，查找ROP服务方法: " + context);
		}
		String[] beanNames = context.getBeanNamesForType(Object.class);
		for (final String beanName : beanNames)
		{
			Class<?> handlerType = context.getType(beanName);
			// 只对标注 ServiceMethodBean的Bean进行扫描
			if (AnnotationUtils.findAnnotation(handlerType, RopService.class) != null)
			{
				ReflectionUtils.doWithMethods(handlerType, new ReflectionUtils.MethodCallback()
				{

					@SuppressWarnings("unchecked")
					@Override
					public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException
					{
						ReflectionUtils.makeAccessible(method);

						RopMethod serviceMethod = AnnotationUtils.findAnnotation(method, RopMethod.class);
						RopService serviceMethodBean = AnnotationUtils.findAnnotation(method.getDeclaringClass(), RopService.class);

						ServiceMethodDefinition definition = null;
						if (serviceMethodBean != null)
						{
							definition = buildServiceMethodDefinition(serviceMethodBean, serviceMethod);
						}
						else
						{
							definition = buildServiceMethodDefinition(serviceMethod);
						}
						ServiceMethodHandler serviceMethodHandler = new ServiceMethodHandler();
						serviceMethodHandler.setServiceMethodDefinition(definition);

						// 1.set handler
						serviceMethodHandler.setHandler(context.getBean(beanName)); // handler
						serviceMethodHandler.setHandlerMethod(method); // handler'method

						if (method.getParameterTypes().length > 1)
						{// handler method's parameter
							throw new RopException(method.getDeclaringClass().getName() + "." + method.getName() + "的入参只能是" + RopRequest.class.getName() + "或无入参。");
						}
						else if (method.getParameterTypes().length == 1)
						{
							Class<?> paramType = method.getParameterTypes()[0];
							if (!ClassUtils.isAssignable(RopRequest.class, paramType)) { 
								throw new RopException(method.getDeclaringClass().getName() + "." + method.getName() + "的入参必须是" + RopRequest.class.getName()); 
							}
							boolean ropRequestImplType = !(paramType.isAssignableFrom(RopRequest.class) || paramType.isAssignableFrom(AbstractRopRequest.class));
							serviceMethodHandler.setRopRequestImplType(ropRequestImplType);
							serviceMethodHandler.setRequestType((Class<? extends RopRequest>) paramType);
						}
						else
						{
							logger.info(method.getDeclaringClass().getName() + "." + method.getName() + "无入参");
						}

						// 2.set sign fieldNames
						serviceMethodHandler.setIgnoreSignFieldNames(getIgnoreSignFieldNames(serviceMethodHandler.getRequestType()));

						// 3.set fileItemFieldNames
						// serviceMethodHandler.setUploadFileFieldNames(getFileItemFieldNames(serviceMethodHandler.getRequestType()));

						addServiceMethod(definition.getMethod(), definition.getVersion(), serviceMethodHandler, definition.getModule());

						if (logger.isDebugEnabled())
						{
							logger.debug("注册服务方法：" + method.getDeclaringClass().getCanonicalName() + "#" + method.getName() + "(..)");
						}
					}
				}, new ReflectionUtils.MethodFilter()
				{

					@Override
					public boolean matches(Method method)
					{
						return !method.isSynthetic() && AnnotationUtils.findAnnotation(method, RopMethod.class) != null;
					}
				});
			}
		}
		if (context.getParent() != null)
		{
			registerFromContext(context.getParent());
		}
		if (logger.isInfoEnabled())
		{
			logger.info("共注册了" + serviceHandlerMap.size() + "个服务方法");
		}
	}

	private ServiceMethodDefinition buildServiceMethodDefinition(RopMethod serviceMethod)
	{
		ServiceMethodDefinition definition = new ServiceMethodDefinition();
		definition.setMethod(serviceMethod.method());
		definition.setMethodTitle(serviceMethod.title());
		definition.setModule(serviceMethod.module());
		definition.setMethodGroup(serviceMethod.group());
		definition.setMethodGroupTitle(serviceMethod.groupTitle());
		definition.setTags(serviceMethod.tags());
		definition.setTimeout(serviceMethod.timeout());
		definition.setIgnoreSign(IgnoreSignType.isIgnoreSign(serviceMethod.ignoreSign()));
		definition.setVersion(serviceMethod.version());
		definition.setNeedInSession(NeedInSessionType.isNeedInSession(serviceMethod.needInSession()));
		definition.setObsoleted(ObsoletedType.isObsoleted(serviceMethod.obsoleted()));
		definition.setHttpAction(serviceMethod.httpAction());
		return definition;
	}

	private ServiceMethodDefinition buildServiceMethodDefinition(RopService serviceMethodBean, RopMethod serviceMethod)
	{
		ServiceMethodDefinition definition = new ServiceMethodDefinition();
		definition.setMethodGroup(serviceMethodBean.group());
		definition.setMethodGroupTitle(serviceMethodBean.groupTitle());
		definition.setTags(serviceMethodBean.tags());
		definition.setTimeout(serviceMethodBean.timeout());
		definition.setIgnoreSign(IgnoreSignType.isIgnoreSign(serviceMethodBean.ignoreSign()));
		definition.setVersion(serviceMethodBean.version());
		definition.setNeedInSession(NeedInSessionType.isNeedInSession(serviceMethodBean.needInSession()));
		definition.setHttpAction(serviceMethodBean.httpAction());
		definition.setObsoleted(ObsoletedType.isObsoleted(serviceMethodBean.obsoleted()));

		// 如果ServiceMethod所提供的值和ServiceMethodGroup不一样，覆盖之
		definition.setMethod(serviceMethod.method());
		definition.setMethodTitle(serviceMethod.title());
		definition.setModule(serviceMethod.module());

		if (!ServiceMethodDefinition.DEFAULT_GROUP.equals(serviceMethod.group()))
		{
			definition.setMethodGroup(serviceMethod.group());
		}

		if (!ServiceMethodDefinition.DEFAULT_GROUP_TITLE.equals(serviceMethod.groupTitle()))
		{
			definition.setMethodGroupTitle(serviceMethod.groupTitle());
		}

		if (serviceMethod.tags() != null && serviceMethod.tags().length > 0)
		{
			definition.setTags(serviceMethod.tags());
		}

		if (serviceMethod.timeout() > 0)
		{
			definition.setTimeout(serviceMethod.timeout());
		}

		if (serviceMethod.ignoreSign() != IgnoreSignType.DEFAULT)
		{
			definition.setIgnoreSign(IgnoreSignType.isIgnoreSign(serviceMethod.ignoreSign()));
		}

		if (StringUtils.hasText(serviceMethod.version()))
		{
			definition.setVersion(serviceMethod.version());
		}

		if (serviceMethod.needInSession() != NeedInSessionType.DEFAULT)
		{
			definition.setNeedInSession(NeedInSessionType.isNeedInSession(serviceMethod.needInSession()));
		}

		if (serviceMethod.obsoleted() != ObsoletedType.DEFAULT)
		{
			definition.setObsoleted(ObsoletedType.isObsoleted(serviceMethod.obsoleted()));
		}

		if (serviceMethod.httpAction().length > 0)
		{
			definition.setHttpAction(serviceMethod.httpAction());
		}

		return definition;
	}

	public static List<String> getIgnoreSignFieldNames(Class<? extends RopRequest> requestType)
	{
		final ArrayList<String> igoreSignFieldNames = new ArrayList<String>(1);
		igoreSignFieldNames.add(SystemParameterNames.SIGN);
		if (requestType != null)
		{
			if (logger.isDebugEnabled())
			{
				logger.debug("获取" + requestType.getCanonicalName() + "不需要签名的属性");
			}
			ReflectionUtils.doWithFields(requestType, new ReflectionUtils.FieldCallback()
			{

				@Override
				public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException
				{
					igoreSignFieldNames.add(field.getName());
				}
			}, new ReflectionUtils.FieldFilter()
			{

				@Override
				public boolean matches(Field field)
				{

					// 属性类标注了@IgnoreSign
					IgnoreSign typeIgnore = AnnotationUtils.findAnnotation(field.getType(), IgnoreSign.class);

					// 属性定义处标注了@IgnoreSign
					IgnoreSign varIgnoreSign = field.getAnnotation(IgnoreSign.class);

					// 属性定义处标注了@Temporary
					Temporary varTemporary = field.getAnnotation(Temporary.class);

					return typeIgnore != null || varIgnoreSign != null || varTemporary != null;
				}
			});
			if (igoreSignFieldNames.size() > 1 && logger.isDebugEnabled())
			{
				logger.debug(requestType.getCanonicalName() + "不需要签名的属性:" + igoreSignFieldNames.toString());
			}
		}
		return igoreSignFieldNames;
	}

	// private List<String> getFileItemFieldNames(Class<? extends RopRequest> requestType) {
	// final ArrayList<String> fileItemFieldNames = new ArrayList<String>(1);
	// if (requestType != null) {
	// if (logger.isDebugEnabled()) {
	// logger.debug("获取" + requestType.getCanonicalName() + "类型为FileItem的字段名");
	// }
	//
	// ReflectionUtils.doWithFields(requestType, new ReflectionUtils.FieldCallback() {
	// public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
	// fileItemFieldNames.add(field.getName());
	// }
	// },
	// new ReflectionUtils.FieldFilter() {
	// public boolean matches(Field field) {
	// return ClassUtils.isAssignable(UploadFile.class, field.getType());
	// }
	// }
	// );
	//
	// }
	// return fileItemFieldNames;
	// }

}
