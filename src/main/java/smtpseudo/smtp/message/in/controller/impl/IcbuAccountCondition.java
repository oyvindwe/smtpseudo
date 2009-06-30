package smtpseudo.smtp.message.in.controller.impl;

/**
 * Created by IntelliJ IDEA.
 * User: hanishi
 * Date: 2009/06/26
 * Time: 20:30:20
 * To change this template use File | Settings | File Templates.
 */
import java.io.Serializable;

public class IcbuAccountCondition implements Serializable
{
	private String icbuEmailAddress;

	private String columnName;

	public IcbuAccountCondition()
	{
	}

	public IcbuAccountCondition(String icbuEmailAddress, String columnName)
    {
	    this.icbuEmailAddress = icbuEmailAddress;
	    this.columnName = columnName;
    }

	public String getIcbuEmailAddress()
    {
    	return icbuEmailAddress;
    }

	public void setIcbuEmailAddress(String icbuEmailAddress)
    {
    	this.icbuEmailAddress = icbuEmailAddress;
    }

	public String getColumnName()
    {
    	return columnName;
    }

	public void setColumnName(String columnName)
    {
    	this.columnName = columnName;
    }

}
