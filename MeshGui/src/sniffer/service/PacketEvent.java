package sniffer.service;

import java.util.EventObject;

public class PacketEvent extends EventObject
{
	private String pck;

	public PacketEvent(Object source,String _pck) {
		super(source);
		pck = _pck;
	}
	
	public String getPack()
	{
		return pck;
	}

}
