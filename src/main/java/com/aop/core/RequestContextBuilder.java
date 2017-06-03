/**
 * 版权声明： 版权所有 违者必究 2012
 * 日    期：12-6-1
 */
package com.aop.core;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <pre>
 *    更改请求对象创建{@link RopRequestContext}实例,子类可以根据多种传输协议定义自己的创建器。
 * </pre>
 *
 * @author CXH
 * @version 1.0
 */
public interface RequestContextBuilder {
    /**
     * 根据reqeuest及response请求响应对象，创建{@link RopRequestContext}实例。绑定系统参数，请求对象
     * @param ropContext
     * @param request
     * @param response
     * @return
     */
    RopRequestContext buildBySysParams(RopContext ropContext, HttpServletRequest request,HttpServletResponse response);

    /**
     * 绑定业务参数
     *
     * @param ropRequestContext
     */
    RopRequest buildRopRequest(RopRequestContext ropRequestContext);
}

