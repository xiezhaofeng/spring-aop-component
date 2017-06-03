/**
 * 版权声明： 版权所有 违者必究 2012
 * 日    期：12-6-8
 */
package com.aop.enums;

/**
 * <pre>
 *   请求类型的方法
 * </pre>
 *
 * @author CXH
 * @version 1.0
 */
public enum HttpAction {

    GET, POST;

    public static HttpAction fromValue(String value) {
        if (GET.name().equalsIgnoreCase(value)) {
            return GET;
        } else if (POST.name().equalsIgnoreCase(value)) {
            return POST;
        } else {
            return POST;
        }
    }
}

