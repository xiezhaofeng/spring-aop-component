package com.aop.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.aop.enums.SignType;
import com.aop.exception.RopException;

/**
 * 
  * @Title: MD5Util.java 
  * @Package com.aop.util
  * @Description  TODO
  * @author  XZF
  * @date 2017年5月31日 上午10:53:36 
  * @version   
  *
  *
 */
public class RopUtil
{
	 private static final String UTF_8 = "utf-8";

	/**
     * 使用<code>secret</code>对paramValues按以下算法进行签名： <br/>
     * uppercase(hex(sha1(secretkey1value1key2value2...secret))
     *
     * @param paramValues 参数列表
     * @param secret
     * @return
     */
    public static String sign(Map<String, String> paramValues, String secret, String signType) {
        return sign(paramValues,null,secret, signType);
    }

    /**
     * 对paramValues进行签名，其中ignoreParamNames这些参数不参与签名
     * @param paramValues
     * @param ignoreParamNames
     * @param secret
     * @return
     */
    public static String sign(Map<String, String> paramValues, List<String> ignoreParamNames,String secret, String signType) {
        try {
            StringBuilder sb = new StringBuilder();
            List<String> paramNames = new ArrayList<String>(paramValues.size());
            paramNames.addAll(paramValues.keySet());
            if(ignoreParamNames != null && ignoreParamNames.size() > 0){
                for (String ignoreParamName : ignoreParamNames) {
                    paramNames.remove(ignoreParamName);
                }
            }
            Collections.sort(paramNames);

            sb.append(secret);
            for (String paramName : paramNames) {
                sb.append(paramName).append(paramValues.get(paramName));
            }
            sb.append(secret);
            byte[] sha1Digest = getDigest(sb.toString(), signType);
            return byte2hex(sha1Digest);
        } catch (IOException e) {
            throw new RopException(e);
        }
    }

    public static String md5(String value){
        try {
            byte[] sha1Digest = getDigest(value, SignType.MD5.getValue());
            return byte2hex(sha1Digest);
        } catch (IOException e) {
            throw new RopException(e);
        }
    }
    public static String utf8Encoding(String value, String sourceCharsetName) {
        try {
            return new String(value.getBytes(sourceCharsetName), UTF_8);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static byte[] getDigest(String data, String signType) throws IOException {
        byte[] bytes = null;
        try {
            MessageDigest md = MessageDigest.getInstance(signType);
            bytes = md.digest(data.getBytes(UTF_8));
        } catch (GeneralSecurityException gse) {
            throw new IOException(gse.getMessage());
        }
        return bytes;
    }


    /**
     * 二进制转十六进制字符串
     *
     * @param bytes
     * @return
     */
    private static String byte2hex(byte[] bytes) {
        StringBuilder sign = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                sign.append("0");
            }
            sign.append(hex.toUpperCase());
        }
        return sign.toString();
    }

    public static String getUUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString().toUpperCase();
    }

    public static boolean equals(String... strs) {
        if(strs == null) {
            return false;
        } else if(strs.length < 2) {
            throw new RuntimeException("Parameters can not be less than 2");
        } else {
            for(int i = 1; i < strs.length; ++i) {
                if(!equals(strs[i - 1], strs[i])) {
                    return false;
                }
            }

            return true;
        }
    }

    public static boolean equalsIgnoreCase(String... strs) {
        if(strs == null) {
            return false;
        } else if(strs.length < 2) {
            throw new RuntimeException("Parameters can not be less than 2");
        } else {
            for(int i = 1; i < strs.length; ++i) {
                if(!equalsIgnoreCase(strs[i - 1], strs[i])) {
                    return false;
                }
            }

            return true;
        }
    }
    public static boolean notEquals(String str1, String str2) {
        return !equals(str1, str2);
    }

    public static boolean notEqualsIgnoreCase(String str1, String str2) {
        return !equalsIgnoreCase(str1, str2);
    }
    
    public static boolean equals(String str1, String str2)
	{
		return str1 == null && str2 == null ? true : str1 == null ? false : str2 == null ? false : str1.equals(str2);
	}

	public static boolean equalsIgnoreCase(String str1, String str2)
	{
		return str1 == null && str2 == null ? true : str1 == null ? false : str2 == null ? false : str1.equalsIgnoreCase(str2);
	}
}
