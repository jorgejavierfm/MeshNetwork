package sniffer.service;

import message.service.MsgEvent;

public interface PacketListener 
{
	public void pckReceived(PacketEvent event);
}
