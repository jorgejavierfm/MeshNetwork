package gui.model;

import java.util.concurrent.CountDownLatch;

/* WITH THIS INTERFACE EACH SERVICE WILL DEFINE HIS CATEGORY IN A LAYERED WAY AND IMPLEMENT HIS REQUERIMENTS FOR SYNCHRONIZATION*/
public interface IMeshService
{	
	public ServiceLevel getServiceLevel();
	public void synchronize(CountDownLatch sync);
}
