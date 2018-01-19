package com.rop.core.impl;

import com.rop.core.InfoService;
import com.rop.util.RopUtil;

/**
 * 
  * @Title: DefaultInfoServiceImpl.java
  * @Package com.rop.core.impl
  * @Description  TODO
  * @author  XZF
  * @date 2017年5月31日 上午11:18:24 
  * @version   
  *
  *
 */
public class DefaultInfoServiceImpl implements InfoService
{

	@Override
	public String getSecret(String token)
	{
		return RopUtil.md5(token);
	}

}
