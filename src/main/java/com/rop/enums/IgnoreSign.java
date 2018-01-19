/**
 *
 * 日    期：12-2-10
 */
package com.rop.enums;


import java.lang.annotation.*;

/**
 * <pre>
 *     如果标注在请求类的属性上，则表示该属性无需进行签名，如下所示：
 * 请求对象（{@link com.rop.core.RopRequest}）中不需要签名校验的属性（默认都要签名）。
 * </pre>
 *
 * @version 1.0
 */
@Target({ElementType.FIELD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IgnoreSign {
}
