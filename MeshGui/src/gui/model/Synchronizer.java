package gui.model;

import java.util.concurrent.CountDownLatch;

/* THIS CLASS WILL BE THE RENDEZVOUS POINT FOR THE LOGIC ORDER OF EACH THREAD */
public class Synchronizer 
{
	private static Synchronizer instance = null;
	CountDownLatch linkLevel;
	CountDownLatch networkLevel;
	
	protected Synchronizer() {
		linkLevel = new CountDownLatch(1); //In the link level we are just gonna need the connection service
		networkLevel = new CountDownLatch(1); //In the network level we are just gonna need the olsr daemon
	}
	
	public static Synchronizer getInstance() 
	{
		if(instance == null)
			instance = new Synchronizer();
		return instance;
	}
	
	public CountDownLatch getLinkLevelLatch()
	{
		return linkLevel;
	}
	
	public CountDownLatch getNetworkLevelLatch()
	{
		return networkLevel;
	}
	
	public void synchronize(IMeshService service)
	{
		if(service.getServiceLevel() == ServiceLevel.LINK)
			service.synchronize(null);
		else if(service.getServiceLevel() == ServiceLevel.NETWORK)
			service.synchronize(linkLevel);
		else
			service.synchronize(networkLevel);
	}
}
