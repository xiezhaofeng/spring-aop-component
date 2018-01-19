/**
 * 版权声明： 版权所有 违者必究 2012
 * 日    期：12-7-30
 */
package com.rop.enums;

/**
 * <pre>
 *   服务方法是否已经过期，过期的服务方法不能再访问
 * </pre>
 *
 * @version 1.0
 */
public enum  ObsoletedType {
    YES, NO, DEFAULT;

     public static boolean isObsoleted(ObsoletedType type) {
         if (YES == type ) {
             return true;
         } else {
             return false;
         }
     }
}

