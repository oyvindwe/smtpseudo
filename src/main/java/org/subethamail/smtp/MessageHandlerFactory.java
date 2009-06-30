/*
 * $Id: MessageHandlerFactory.java 308 2009-05-06 02:25:23Z lhoriman $
 * $URL: https://subethasmtp.googlecode.com/svn/trunk/src/org/subethamail/smtp/MessageHandlerFactory.java $
 */
package org.subethamail.smtp;



/**
 * The primary interface to be implemented by clients of the SMTP library.
 * This factory is called for every message to be exchanged in an SMTP
 * conversation.  If multiple messages are transmitted in a single connection
 * (via RSET), multiple handlers will be created from this factory.
 * 
 * @author Jeff Schnitzer
 */
public interface MessageHandlerFactory
{
	/**
	 * Called for the exchange of a single message during an SMTP conversation.
	 * 
	 * @param ctx provides information about the client.
	 */
	public MessageHandler create(MessageContext ctx);
}
