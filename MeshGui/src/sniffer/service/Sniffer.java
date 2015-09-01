package sniffer.service;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import gui.model.IMeshService;
import gui.model.PacketGUI;
import gui.model.ServiceLevel;
import gui.model.Synchronizer;

import org.jnetpcap.JBufferHandler;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapBpfProgram;
import org.jnetpcap.PcapDumper;
import org.jnetpcap.PcapHeader;
import org.jnetpcap.nio.JBuffer;
import org.jnetpcap.protocol.tcpip.Udp;


public class Sniffer extends Thread implements IMeshService
{
	private Pcap pcap = null;
	private int snaplen;
	private int flags;
	private int timeout;
	private StringBuilder errbuff;
	private String wirelessInterface;
	private boolean cancel;
	
	private PacketHandler pckH;
	
	public Sniffer(String _wirelessInterface,PacketGUI pckG) 
	{
		snaplen = 64 * 1024;
		flags = Pcap.MODE_PROMISCUOUS;
		timeout = 10 * 1000;
		errbuff = new StringBuilder();
		wirelessInterface = _wirelessInterface;
		
		pckH = new PacketHandler();
		
		pckH.addPckListener(pckG);
	}
	
	@SuppressWarnings("static-access")
	public void run()
	{
		Synchronizer.getInstance().synchronize(this);
		
		if(!cancel)
		{
			PcapBpfProgram program = new PcapBpfProgram();
			pcap = Pcap.openLive(wirelessInterface,snaplen,flags,timeout,errbuff);

			/* Filter to get just the packets we want*/
			String expression = "udp port 7077 or udp port 698";
			int optimize = 0; //0 = false
			int netmask = 0xFFFFFF00; //255.255.255.0

			/* We make sure that pcap has initialized correctly*/
			if(pcap == null){
				System.err.printf("Error while opening device for capture: " + errbuff.toString());
				return;
			}

			/* The filter needs to be compiled*/
			if(pcap.compile(program,expression,optimize,netmask) != Pcap.OK){
				System.err.println(pcap.getErr());
				return;
			}

			/* Finally we set the filter for the capture*/
			if(pcap.setFilter(program)!= Pcap.OK){
				System.err.println(pcap.getErr());
				return;
			}

			System.out.println("device opened");

			pcap.loop(pcap.LOOP_INFINITE,pckH,"capture");
			pcap.close();
		}
	}

	@Override
	public ServiceLevel getServiceLevel() {
		return ServiceLevel.APPLICATION;
	}

	@Override
	public void synchronize(CountDownLatch sync) 
	{
		boolean successSync = false;

		try {
			successSync = sync.await(60,TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(!successSync)
			cancel = true;
	}
	
	public void close()
	{
		if(pcap != null)
			pcap.breakloop();
	}
}
