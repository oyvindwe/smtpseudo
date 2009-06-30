/*
 * $Id: MessageContext.java 319 2009-05-20 04:15:35Z lhoriman $
 * $URL: https://subethasmtp.googlecode.com/svn/trunk/src/org/subethamail/smtp/MessageContext.java $
 */
package org.subethamail.smtp;

import java.net.SocketAddress;

import org.subethamail.smtp.server.SMTPServer;


/**
 * Interface which provides context to the message handlers.
 * 
 * @author Jeff Schnitzer
 */
public interface MessageContext
{
	/**
	 * @return the SMTPServer object.
	 */
	public SMTPServer getSMTPServer();
	
	/**
	 * @return the IP address of the remote server.
	 */
	public SocketAddress getRemoteAddress();
	
	/**
	 * @return the handler instance that was used to authenticate.
	 */
	public AuthenticationHandler getAuthenticationHandler();
}
