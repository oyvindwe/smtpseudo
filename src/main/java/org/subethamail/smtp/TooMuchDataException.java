/*
 * $Id: TooMuchDataException.java 308 2009-05-06 02:25:23Z lhoriman $
 * $Source: /cvsroot/Similarity4/src/java/com/similarity/mbean/BindStatisticsManagerMBean.java,v $
 */
package org.subethamail.smtp;

import java.io.IOException;

/**
 * Thrown by message listeners if an input stream provides more data than the
 * listener can handle.
 * 
 * @author Jeff Schnitzer
 */
@SuppressWarnings("serial")
public class TooMuchDataException extends IOException
{
	/** */
	public TooMuchDataException()
	{
		super();
	}

	/** */
	public TooMuchDataException(String message)
	{
		super(message);
	}
}
