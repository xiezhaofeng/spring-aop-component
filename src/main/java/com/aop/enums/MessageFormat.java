/**
 * 日期：12-2-10
 */
package com.aop.enums;

import org.springframework.util.StringUtils;

/**
 * 支持的响应的格式类型
 */
public enum MessageFormat {

    xml("application/xml"), json("application/json"), stream("application/json");
    
    private String value;
    
    private MessageFormat(String v){
    	value = v;
    }

    public String getValue(){
    	return value;
    }

	public static MessageFormat getFormat(String value)
	{
		return getFormat(value, null	);
	}
	public static MessageFormat getFormat(String format, MessageFormat defaultFormat)
	{
		if (StringUtils.hasText(format))
		{
			try
			{
				return MessageFormat.valueOf(format.toLowerCase());
			}
			catch (IllegalArgumentException e)
			{

			}
		}
		return defaultFormat;
	}

	public static MessageFormat getFormatValue(String value, MessageFormat defaultFormat)
	{
		if (StringUtils.hasText(value))
		{
			try
			{
				for (MessageFormat mf : MessageFormat.values())
				{
					if (mf.getValue().equalsIgnoreCase(value)) { return mf; }
				}
			}
			catch (IllegalArgumentException e)
			{
			}
		}
		return defaultFormat;
	}
    public static MessageFormat getFormatValue(String value) {
        return getFormatValue(value, null);
    }

    public static boolean isValidFormat(String value) {
        if (!StringUtils.hasText(value)) {
            return true;
        }else{
            try {
                MessageFormat.valueOf(value.toLowerCase());
                return true;
            } catch (IllegalArgumentException e) {
                return false;
            }
        }
    }


}
