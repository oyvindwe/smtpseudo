/*
 * $Id: RejectException.java 327 2009-06-04 00:49:46Z lhoriman $
 * $Source: /cvsroot/Similarity4/src/java/com/similarity/mbean/BindStatisticsManagerMBean.java,v $
 */
package org.subethamail.smtp;

/**
 * Thrown to reject an SMTP command with a specific code.
 * 
 * @author Jeff Schnitzer
 */
@SuppressWarnings("serial")
public class RejectException extends Exception
{
	int code;
	
	/** */
	public RejectException()
	{
		this("Transaction failed");
	}

	/** */
	public RejectException(String message)
	{
		this(554, message);
	}

	/** */
	public RejectException(int code, String message)
	{
		super(code + " " + message);
		
		this.code = code;
	}

	/** */
	public int getCode()
	{
		return this.code;
	}
}
