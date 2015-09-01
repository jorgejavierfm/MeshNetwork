package message.service;

import java.util.EventObject;

public class MsgEvent extends EventObject 
{
	private String msg;
	
	public MsgEvent(Object source,String _msg)
	{
		super(source);
		msg = _msg;
	}
	
	public String msg()
	{
		return msg;
	}
}
