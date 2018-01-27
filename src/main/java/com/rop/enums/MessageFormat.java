/**
 * 日期：12-2-10
 */
package com.rop.enums;

import org.springframework.util.StringUtils;

/**
 * 支持的响应的格式类型
 * @author xzf
 */
public enum MessageFormat {

	/**
	 * application/xml
	 */
	xml("application/xml"),
	/**
	 * application/json
	 */
	json("application/json"),
	/**
	 * application/stream
	 */
	stream("application/stream");

	private String value;

	MessageFormat(String v){
		value = v;
	}

	public String getValue(){
		return value;
	}

	public static MessageFormat getFormat(String value)
	{
		if (StringUtils.hasText(value))
		{
			try
			{
				return MessageFormat.valueOf(value.toLowerCase());
			}
			catch (IllegalArgumentException e)
			{

			}
		}
		return null;
	}
	public static MessageFormat getFormatValue(String value) {

		return getFormatValue(value, null);
	}
	/**
	 *
	 * @Title getFormatValue
	 * @Description 根据content-type获取format，如果为空则返回默认format
	 * @param value
	 * @param defaultFormat
	 * @return MessageFormat
	 * @throws
	 */
	public static MessageFormat getFormatValue(String value, MessageFormat defaultFormat) {
		if (StringUtils.hasText(value))
		{
			try
			{
				for (MessageFormat mf : MessageFormat.values())
				{
					if (value.toLowerCase().contains(mf.value)) { return mf; }
				}
			}
			catch (IllegalArgumentException e)
			{
			}
		}
		return defaultFormat;
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

	public static MessageFormat getFormat(String format, MessageFormat defaultFormat)
	{
		MessageFormat f = getFormat(format);

		return f == null ? defaultFormat : f;
	}

}
