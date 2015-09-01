package sniffer.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import message.service.MsgEvent;
import message.service.MsgListener;

import org.jnetpcap.PcapHeader;
import org.jnetpcap.packet.JPacket;
import org.jnetpcap.packet.Payload;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;
import org.jnetpcap.packet.format.FormatUtils;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Udp;

public class PacketHandler implements PcapPacketHandler<Object> 
{
	Payload payload;
	private List<PacketListener> listeners;
	
	public PacketHandler() {
		listeners = new ArrayList<PacketListener>();
		payload = new Payload();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void nextPacket(PcapPacket packet, Object arg1) 
	{
		if(packet.hasHeader(payload))
		{
			Udp udp = packet.getHeader(new Udp());
			PcapHeader frame = packet.getCaptureHeader();
			
			Ip4 ip4 = packet.getHeader(new Ip4());
			String origen = FormatUtils.ip(ip4.source());
			String destination = FormatUtils.ip(ip4.destination());
			int port = udp.destination();
			
			Date date = new Date(frame.timestampInMillis());
			
			StringBuilder sb = new StringBuilder();
			
			if(port!=7077)
			{
				List<String>packets = parse(payload.toHexdump());
				if(packets != null)
				{
					for(String s : packets)
					{
						sb.append(origen + " ---> " + destination + " : " );
						sb.append(s + " Timestamp [");
						sb.append(date.getHours()+ ":").append(date.getMinutes() + ":").append(date.getSeconds() + "]");
						firePckEvent(sb.toString());
						sb.setLength(0);
					}
				}
			}
			else
			{
				sb.append(origen + " ---> " + destination + " : " + "Mensaje");
				firePckEvent(sb.toString());
				sb.setLength(0);
			}
		}	
	}
	
	private List<String> parse(String base)
    {
		String[] chunksN = base.split(" ");
		List <String> packets = new ArrayList<String>();
		
		for(String s : chunksN)
		{
			if(s.equals("c9")) //Codigo en hex correspondiente a los paquetes 
				packets.add("Paquete HELLO");
			if(s.equals("04")) //Codigo en hex correspondiente a los paquetes
				packets.add("Paquete HNA");
			if(s.equals("ca")) //Codigo en hex correspondiente a los paquetes
				packets.add("Paquete TC");	
		}
		
		return packets;
    }
	
	public synchronized void addPckListener(PacketListener pckl)
	{
		listeners.add(pckl);
	}
	
	public synchronized void removePckListener(PacketListener pckl)
	{
		listeners.remove(pckl);
	}
	
	private synchronized void firePckEvent(String pck)
	{
		PacketEvent event = new PacketEvent(this,pck);
		Iterator<PacketListener> it = listeners.iterator();
		while(it.hasNext())
			it.next().pckReceived(event);
	}
}
