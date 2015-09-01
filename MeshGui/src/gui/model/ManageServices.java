package gui.model;

import org.graphstream.graph.Graph;

import connection.service.ConnectionController;
import olsrdaemon.service.OlsrProtocolDaemon;
import olsrinfo.service.DataOlsrdWorker;
import message.service.Receiver;
import profile.service.ActiveProfile;
import profile.service.Profile;
import sniffer.service.Sniffer;
import topology.service.TopologyVisWorker;


public class ManageServices
{
	private Receiver messageReceiver = null; //Port 7077
	private ActiveProfile actProfile = null;
	private DataOlsrdWorker dow = null;
	private TopologyVisWorker tpw = null;
	
	private ConnectionController adhoc = null;
	private OlsrProtocolDaemon olsr = null;
	
	private TableModels tm = null;
	private Graph graph = null;
	Sniffer sniffer = null;
	
	private PacketGUI pckG;
	
	public ManageServices(TableModels _tm,Graph _graph,PacketGUI _pckG)
	{
		tm = _tm;
		graph = _graph;
		
		messageReceiver = new Receiver(7077);
		messageReceiver.start();
		
		/* Class representing the active profile */
		actProfile = ActiveProfile.getInstance();
		pckG = _pckG;
	}
	
	public void init()
	{	
		if(actProfile.getActiveProfile() == null)
			System.out.println("No existe un perfil activo");
		else
		{	
			Profile profile = actProfile.getActiveProfile();
			adhoc = new ConnectionController(profile.getInterfaceOlsr(),profile.getSsid(),profile.getIp(),profile.getNetmask());
			adhoc.start();
			
			olsr = new OlsrProtocolDaemon(profile.getInterfaceOlsr());
			olsr.start();
			
			/* Polling rate 1000ms */
			dow = new DataOlsrdWorker(tm.getAllModels(),1000);
			dow.execute();
			
			/* Polling rate 1000ms */
			tpw = new TopologyVisWorker(graph,1000);
			tpw.execute();
			
			sniffer= new Sniffer(profile.getInterfaceOlsr(),pckG);
			sniffer.start();
		}
	}
	
	public void stop()
	{
		if(adhoc != null){
			adhoc.terminateThread();
			adhoc = null;
		}
		if(olsr != null){
			olsr.stopOlsr();
			olsr = null;
		}
		if(dow != null){
			dow.cancel(true);
			dow = null;
		}
		if(tpw != null){
			tpw.cancel(true);
			tpw = null;
		}
		if(sniffer != null){
			sniffer.close();
			sniffer = null;
		}
	}
	
	public Receiver getMessageReceiver() {
		return messageReceiver;
	}
}
