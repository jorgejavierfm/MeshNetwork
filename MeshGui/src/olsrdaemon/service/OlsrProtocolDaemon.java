package olsrdaemon.service;

import gui.model.IMeshService;
import gui.model.ServiceLevel;
import gui.model.Synchronizer;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;

public class OlsrProtocolDaemon extends Thread implements IMeshService 
{
	private String interfaceOlsr;
	private boolean cancel;
	
	public OlsrProtocolDaemon(String _interfaceOlsr)
	{
		interfaceOlsr = _interfaceOlsr;
		cancel = false;
	}
	
	public void run()
	{
		Synchronizer.getInstance().synchronize(this);
		
		if(!cancel)
		{
			String permission = "chmod 755 ./resources/olsrd";
			String line = "./resources/olsrd -f ./resources/olsrd.conf.default -nofork -i " + interfaceOlsr;
			DefaultExecutor executor = new DefaultExecutor();
			DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
			
			try {
				executor.execute(CommandLine.parse(permission));
				executor.execute(CommandLine.parse(line),resultHandler);
				Synchronizer.getInstance().getNetworkLevelLatch().countDown();
				resultHandler.waitFor();
			} catch (IOException | InterruptedException e){
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
	}

	public void stopOlsr()
	{
		String line = "killall -v olsrd";
		DefaultExecutor executor = new DefaultExecutor();
		
		try {
			executor.execute(CommandLine.parse(line));
		} catch (ExecuteException e) {
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public ServiceLevel getServiceLevel() {
		return ServiceLevel.NETWORK;
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
}
