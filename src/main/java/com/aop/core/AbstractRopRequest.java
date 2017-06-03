/**
 * 版权声明： 版权所有 违者必究 2012
 * 日    期：12-6-6
 */
package com.aop.core;

import com.aop.enums.Temporary;

/**
 * <pre>
 *   所有请求对象应该通过扩展此抽象类实现
 * </pre>
 *
 * @author CXH
 * @version 1.0
 */
public abstract class AbstractRopRequest implements RopRequest {

    @Temporary
    private RopRequestContext ropRequestContext;

	
	public RopRequestContext getRopRequestContext()
	{
		return ropRequestContext;
	}
	
	public void setRopRequestContext(RopRequestContext ropRequestContext)
	{
		this.ropRequestContext = ropRequestContext;
	}

    
}

