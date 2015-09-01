package message.service;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.SwingWorker;

import olsrinfo.service.datatypes.Interface;
import olsrinfo.service.datatypes.Link;

public class Receiver extends Thread
{
	private final int MAX_MESSAGE_LENGHT = 256;
	private List<MsgListener> listeners;
	private int receiverPort;
	private boolean cancel;
	private DatagramSocket socket;
	
	public Receiver(int _receiverPort)
	{
		receiverPort = _receiverPort;
		listeners = new ArrayList<MsgListener>();
		cancel = false;
	}
	
	public void run()
	{
		try {
			socket = new DatagramSocket(receiverPort);
			
			byte[] buff = new byte[MAX_MESSAGE_LENGHT];
			DatagramPacket packet = new DatagramPacket(buff,buff.length);

			while (!cancel)
			{
				try
				{
					socket.receive(packet);
					String msg = new String(packet.getData(),0,packet.getLength());
					fireMsgEvent(msg);
				}
				catch (Exception e){
					e.printStackTrace();
				}
			}
			
			socket.close();
		} catch (SocketException e) {
			System.out.println(e.getMessage());
		}
	}
	
	public void stopReceiver()
	{
		cancel = true;
	}
	
	public synchronized void addMsgListener(MsgListener msgl)
	{
		listeners.add(msgl);
	}
	
	public synchronized void removeMsgListener(MsgListener msgl)
	{
		listeners.remove(msgl);
	}
	
	private synchronized void fireMsgEvent(String msg)
	{
		MsgEvent event = new MsgEvent(this,msg);
		Iterator<MsgListener> it = listeners.iterator();
		while(it.hasNext())
			it.next().msgReceived(event);
	}
}
