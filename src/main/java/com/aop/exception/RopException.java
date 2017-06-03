package com.aop.exception;

public class RopException extends RuntimeException
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RopException()
	{
	}

	public RopException(String message)
	{
		super(message);
	}

	public RopException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public RopException(Throwable cause)
	{
		super(cause);
	}

}
