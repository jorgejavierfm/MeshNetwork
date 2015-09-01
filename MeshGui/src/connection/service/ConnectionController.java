package connection.service;

import gui.model.IMeshService;
import gui.model.ServiceLevel;
import gui.model.Synchronizer;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

import org.freedesktop.DBus.Properties;
import org.freedesktop.NetworkManager.Device;
import org.freedesktop.dbus.Variant;
import org.freedesktop.NetworkManager.Device.Wireless;
import org.freedesktop.dbus.exceptions.DBusException;

public class ConnectionController extends Thread implements IMeshService
{	
	String wirelessInterface,ssid,ipAddress,netmask;
	Semaphore terminate = new Semaphore(0);
	NMController nmc;
	
	public ConnectionController(String _wirelessInterface,String _ssid,String _ipAddress,String _netmask)
	{
		wirelessInterface = _wirelessInterface;
		ssid = _ssid;
		ipAddress = _ipAddress;
		netmask = _netmask;
	}
	
	public void run() 
	{
		Synchronizer.getInstance().synchronize(this);
		
		try {
			nmc = new NMController(wirelessInterface);
		} catch (DBusException networkManagerException) {
			networkManagerException.printStackTrace();
		}
		
		/* MAC of the wireless */
		Properties p = (Properties) nmc.getWireless();
		@SuppressWarnings("rawtypes")
		Map<String,Variant> dvSettings = p.GetAll("org.freedesktop.NetworkManager.Device.Wireless");
		String localMac = (String) dvSettings.get("HwAddress").getValue(); //Needed if we discover a mesh node through AccessPointAdded
		SignalHandlerAPA apAppear = new SignalHandlerAPA(nmc,ssid,ipAddress,netmask,localMac);
		SignalHandlerSC connectionStatus = new SignalHandlerSC(nmc,wirelessInterface,ssid,ipAddress);
		
		try {
			nmc.getBus().addSigHandler(Wireless.AccessPointAdded.class,apAppear); //Detection of new AP that can appear
			nmc.getBus().addSigHandler(Device.StateChanged.class,connectionStatus); //To make sure that the connection is successfully created
			nmc.ActivateConnection(ssid,ipAddress,netmask,localMac,null);
			terminate.acquire();
			nmc.getBus().removeSigHandler(Wireless.AccessPointAdded.class,apAppear); //Remove the signal handlers
			nmc.getBus().removeSigHandler(Device.StateChanged.class,connectionStatus);
			Thread.sleep(10000);
		} catch (InterruptedException | DBusException e) {
			System.out.println(e.getMessage());
		}
		
		nmc.getBus().disconnect();
	}
	
	public void terminateThread()
	{
		terminate.release();
	}
	
	@Override
	public ServiceLevel getServiceLevel() {
		return ServiceLevel.LINK;
	}

	@Override
	public void synchronize(CountDownLatch sync) {
		return;
	}
}