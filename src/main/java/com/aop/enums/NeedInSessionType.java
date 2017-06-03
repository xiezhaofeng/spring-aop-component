/**
 * 版权声明： 版权所有 违者必究 2012
 * 日    期：12-5-31
 */
package com.aop.enums;

/**
 * <pre>
 * 功能说明：是否需求会话检查
 * </pre>
 *
 * @author CXH
 * @version 1.0
 */
public enum NeedInSessionType {
    YES, NO, DEFAULT;

    public static boolean isNeedInSession(NeedInSessionType type) {
        if (YES == type || DEFAULT == type) {
            return true;
        } else {
            return false;
        }
    }
}

