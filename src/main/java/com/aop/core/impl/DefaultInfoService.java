package com.aop.core.impl;

import org.apache.commons.codec.digest.DigestUtils;

import com.aop.core.InfoService;

/**
 * 
  * @Title: DefaultInfoService.java 
  * @Package com.aop.core.impl
  * @Description  TODO
  * @author  XZF
  * @date 2017年5月31日 上午11:18:24 
  * @version   
  *
  *
 */
public class DefaultInfoService implements InfoService
{

	@Override
	public String getSecret(String token)
	{
		return DigestUtils.md5Hex(token);
	}

}
