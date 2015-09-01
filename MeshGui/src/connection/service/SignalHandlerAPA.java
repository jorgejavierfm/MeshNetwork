package connection.service;

import java.util.Map;
import org.freedesktop.DBus.Properties;
import org.freedesktop.NetworkManager.AccessPoint;
import org.freedesktop.NetworkManager.Device.Wireless.AccessPointAdded;
import org.freedesktop.NetworkManager.Device.Wireless;
import org.freedesktop.dbus.DBusSigHandler;
import org.freedesktop.dbus.Variant;
import org.freedesktop.dbus.exceptions.DBusException;

public class SignalHandlerAPA implements DBusSigHandler<Wireless.AccessPointAdded>
{
	NMController nmc;
	String ssid,ipAddress,netmask,macAddress;
	
	public  SignalHandlerAPA(NMController _nmc, String _ssid,String _ipAddress,String _netmask, String _macAddress) 
	{
		nmc = _nmc;
		ssid = _ssid;
		ipAddress = _ipAddress;
		netmask = _netmask;
		macAddress = _macAddress;
	}
	
	@Override
	public void handle(AccessPointAdded arg) 
	{		
		Properties p = (Properties) arg.ap;
		AccessPoint ap = (AccessPoint) arg.ap;
		
		@SuppressWarnings("rawtypes")
		Map<String,Variant> apSettings = p.GetAll("org.freedesktop.NetworkManager.AccessPoint");
		String ssidI = new String((byte [])apSettings.get("Ssid").getValue());
			
		if(ssidI.equals(ssid))
		{
			try {
				nmc.ActivateConnection(ssidI,ipAddress,netmask,macAddress,ap);
			} catch (InterruptedException | DBusException e) {
				e.printStackTrace();
			}
		}
	}
}
