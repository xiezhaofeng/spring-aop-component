/**
 * 版权声明： 版权所有 违者必究 2012
 * 日    期：12-6-6
 */
package com.rop.core;

import com.rop.enums.Temporary;

/**
 * <pre>
 *   所有请求对象应该通过扩展此抽象类实现
 * </pre>
 * @author
 * @version 1.0
 */
public abstract class AbstractRopRequest implements RopRequest {

    @Temporary
    private RopRequestContext ropRequestContext;

	private String logNo;

	public String getLogNo() {
		return logNo;
	}

	public void setLogNo(String logNo) {
		this.logNo = logNo;
	}

	public String getOrderNo(){
		return logNo;
	}

	@Override
	public RopRequestContext getRopRequestContext()
	{
		return ropRequestContext;
	}
	
	public void setRopRequestContext(RopRequestContext ropRequestContext)
	{
		this.ropRequestContext = ropRequestContext;
	}

    
}

