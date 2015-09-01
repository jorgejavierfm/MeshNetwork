package message.service;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import javax.swing.SwingWorker;

import olsrinfo.service.JsonInfo;
import olsrinfo.service.datatypes.OlsrDataDump;
import profile.service.ActiveProfile;
import profile.service.Profile;

public class Sender
{
	private final int MAX_MESSAGE_LENGHT = 256;
	String destinationIp;
	int destinationPort;
	
	public Sender(String _destinationIp, int _destinationPort)
	{
		destinationIp = _destinationIp;
		destinationPort = _destinationPort;
	}
	
	public String sendMessage(String message)
	{
		String returnValue = null;
		DatagramSocket socket = null;
		Profile profile = ActiveProfile.getInstance().getActiveProfile();
		StringBuilder msg = new StringBuilder(profile.getIp()+ "\n");
		msg.append(message + "\n");
		
		try
		{
			socket = new DatagramSocket();
			byte buff[] = msg.toString().getBytes();
			int msgLenght = buff.length;
			boolean truncated = false;

			if(msgLenght > MAX_MESSAGE_LENGHT)
			{
				msgLenght = MAX_MESSAGE_LENGHT;
				truncated = true;
			}

			DatagramPacket packet = new DatagramPacket(buff,msgLenght,InetAddress.getByName(destinationIp),destinationPort);
			socket.send(packet);

			if(truncated)
				returnValue = "Message truncated and sent";
			else
				returnValue = "Message sent";
		}
		catch (Exception e) {
			e.printStackTrace();
			returnValue = "Error" + e.getMessage();
		}
		finally{
			if(socket != null)
				socket.close();
		}
		
		return returnValue;
	}
}
