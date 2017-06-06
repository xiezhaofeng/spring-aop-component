package com.aop.exception;

public class RopException extends RuntimeException
{

	/**
	 * 
	 */
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private boolean isFormatJson = true;

	public RopException()
	{
	}

	public RopException(boolean formatJson)
	{
		isFormatJson = formatJson;
	}

	public RopException(String message)
	{
		super(message);
	}
	public RopException(String message, boolean formatJson)
	{
		super(message);
		isFormatJson = formatJson;
	}
	public RopException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	public RopException(String message, boolean formatJson, Throwable cause)
	{
		super(message, cause);
		isFormatJson = formatJson;
	}


	public RopException(boolean formatJson, Throwable cause)
	{
		super(cause);
		isFormatJson = formatJson;
	}
	
	public RopException(Throwable cause)
	{
		super(cause);
	}

	
	public boolean isFormatJson()
	{
		return isFormatJson;
	}

	
	public void setFormatJson(boolean isFormatJson)
	{
		this.isFormatJson = isFormatJson;
	}

}
