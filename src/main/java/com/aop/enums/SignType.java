package com.aop.enums;

/**
 * 
  * @Title: SignType.java 
  * @Package com.aop.enums
  * @Description  签名类型，目前支持：md5和sha-1,默认sha-1
  * @author  XZF
  * @date 2017年5月31日 上午11:00:27 
  * @version   
  *
  *
 */
public enum SignType
{
	MD5("MD5"),SHA_1("SHA-1");
	
	private String value;
	
	private SignType(String v){
		value = v;
	}
	
	public String getValue(){
		return value;
	}
	
	public static SignType get(String value){
		SignType[] values = SignType.values();
		for (SignType signType : values)
		{
			if(signType.getValue().equals(value)){
				return signType;
			}
		}
		return SignType.SHA_1;
	}
}
