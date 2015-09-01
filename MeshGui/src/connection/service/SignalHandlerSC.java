package connection.service;

import gui.model.Synchronizer;

import java.util.Map;
import java.util.concurrent.Semaphore;

import org.freedesktop.DBus.Properties;
import org.freedesktop.NetworkManager.Device;
import org.freedesktop.NetworkManager.Settings.Connection;
import org.freedesktop.dbus.UInt32;
import org.freedesktop.dbus.Variant;
import org.freedesktop.dbus.exceptions.DBusException;
import org.freedesktop.NetworkManager.Device.StateChanged;
import org.freedesktop.dbus.DBusSigHandler;

public class SignalHandlerSC implements DBusSigHandler<Device.StateChanged>
{
	Semaphore lock;
	NMController nmc;
	String wirelessInterface,ssid,ipAddress;
	
	public SignalHandlerSC(NMController _nmc,String _wirelessInterface,String _ssid,String _ipAddress) 
	{
		nmc = _nmc;
		ssid = _ssid;
		ipAddress = _ipAddress;
		wirelessInterface = _wirelessInterface;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void handle(StateChanged arg0) 
	{
		/* We need to check if the connection has the right parameters */
		if(arg0.newState.longValue() == 100)
		{
			/* First we need to check if the signal came from the wireless interface */
			try {
				Properties p = (Properties) nmc.getNetworkManager();				
				Device dv = (Device) nmc.getBus().getRemoteObject("org.freedesktop.NetworkManager",arg0.getPath().toString());
				p = (Properties) dv;
				String toCheck = p.Get("org.freedesktop.NetworkManager.Device","Interface");
				if(toCheck.equals(wirelessInterface))
				{
					/* Second we check for the ip address */
					toCheck = Utils.toString(((UInt32)p.Get("org.freedesktop.NetworkManager.Device","Ip4Address")).longValue());
					if(toCheck.equals(ipAddress))
					{
						/* Finally we check the ssid */
						p = (Properties) nmc.getNetworkManager();
						Map<String,Variant> properties = p.GetAll("org.freedesktop.NetworkManager");
						
						/* We can have more than one active connection (ethernet or wireless, 2 wireless, etc..) */
						String activeConnectionsHolder = properties.get("ActiveConnections").getValue().toString();

						/* We do it this way for compatibility reasons */
						if(!activeConnectionsHolder.equals("[]"))
						{
							activeConnectionsHolder = activeConnectionsHolder.substring(1,activeConnectionsHolder.lastIndexOf("]"));
							String[] activeConnections = activeConnectionsHolder.split(",");

							for(String it : activeConnections)
							{
								it = it.trim();
								/* Properties of the active connection */
								p = nmc.getBus().getRemoteObject("org.freedesktop.NetworkManager", it, Properties.class);
								String settingsPath = p.Get("org.freedesktop.NetworkManager.Connection.Active", "Connection").toString();

								Connection ct = (Connection) nmc.getBus().getRemoteObject("org.freedesktop.NetworkManager",settingsPath);
								Map<String,Map<String,Variant>> ctSettings =  ct.GetSettings();
								String id = ctSettings.get("connection").get("id").getValue().toString();

								if(id.equals(ssid))
									Synchronizer.getInstance().getLinkLevelLatch().countDown(); //If all checked out we release the lock 
							}
						}
					}
				}
			} catch (DBusException e) {
				e.printStackTrace();
			}
		}
	}
}
