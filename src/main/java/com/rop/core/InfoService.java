package com.rop.core;

/**
 * @author xzf
 */
public interface InfoService
{
	/**
	 * get secret by appid
	 * @param token
	 * @return
	 */
	String getSecret(String token);
}
