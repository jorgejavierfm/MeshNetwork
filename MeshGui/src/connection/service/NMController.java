package connection.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Semaphore;

import org.freedesktop.NetworkManager;
import org.freedesktop.DBus.Properties;
import org.freedesktop.NetworkManager.AccessPoint;
import org.freedesktop.NetworkManager.Device;
import org.freedesktop.NetworkManager.Settings;
import org.freedesktop.NetworkManager.Device.Wireless;
import org.freedesktop.NetworkManager.Settings.Connection;
import org.freedesktop.dbus.DBusConnection;
import org.freedesktop.dbus.DBusInterface;
import org.freedesktop.dbus.UInt32;
import org.freedesktop.dbus.Variant;
import org.freedesktop.dbus.exceptions.DBusException;
import org.freedesktop.dbus.exceptions.DBusExecutionException;

@SuppressWarnings("rawtypes")
public class NMController 
{
	private boolean reconnect;
	private DBusConnection bus;
	private NetworkManager nm;
	private Device dv;
	private Settings st;
	private AccessPoint apDummy;
	private Wireless wr;
	private Semaphore control;
	
	/**
	 * 
	 * @param wirelessInterface Interfaz inalambrica
	 * @throws DBusException
	 */
	public NMController(String wirelessInterface) throws DBusException
	{
		/**
		 * To control the updates on settings in the same connection
		 */
		reconnect = false;
		
		bus = DBusConnection.getConnection(DBusConnection.SYSTEM);
		nm = (NetworkManager) bus.getRemoteObject("org.freedesktop.NetworkManager","/org/freedesktop/NetworkManager");
		dv = (Device) nm.GetDeviceByIpIface(wirelessInterface);
		st = (Settings) bus.getRemoteObject("org.freedesktop.NetworkManager", "/org/freedesktop/NetworkManager/Settings"); 
		apDummy = bus.getRemoteObject("org.freedesktop.NetworkManager", "/org/freedesktop/NetworkManager/AccessPoint",AccessPoint.class);
		wr = (Wireless) bus.getRemoteObject("org.freedesktop.NetworkManager",dv.toString().substring(dv.toString().indexOf("/"),dv.toString().lastIndexOf(":"))); 
		
		/**
		 * Controlara las secciones criticas
		 */
		control = new Semaphore(1);
	}

	public DBusConnection getBus() {
		return bus;
	}
	public NetworkManager getNetworkManager() {
		return nm;
	}
	public Device getDevice() {
		return dv;
	}
	public Settings getSettings() {
		return st;
	}
	
	public Wireless getWireless() {
		return wr;
	}
	
	/**
	 *  This method will be called when we need to active a connection (main controller of the connection
	 * @param ssid
	 * @param ipAddress
	 * @param netmask
	 * @param macAddress
	 * @param ap Punto de Acceso
	 * @throws InterruptedException
	 * @throws DBusException
	 */
	
	public void ActivateConnection(String ssid, String ipAddress,String netmask,String macAddress,AccessPoint ap) throws InterruptedException, DBusException
	{
		/**
		 * Critical section 
		 */
		control.acquire();
		
		/**
		 * We first scan around us for the requested connection (ssid) 
		 */
		if(ap == null)
		{
			AccessPoint apDiscovered = ScanConnection(ssid);
			if(apDiscovered != null){
				/**
				 * If we found the requested connection in another AP
				 */
				ap = apDiscovered; //
			}
			else{
				
				/**
				 * We create our own connection
				 */
				ap = apDummy; 
			}
		}
		
		/**
		 * If we have the connection already created we check for the settings and update is necessary
		// If the settings get updated reconnect will be true and we reactivate the connection
		 * 
		 */
		Connection adhoc = isConnectionAlreadyCreated(st,ssid,ipAddress,netmask,macAddress);
		if(adhoc == null)
			adhoc = (Connection) st.AddConnection(ConnectionSettings(ssid,ipAddress,netmask,macAddress)); //We add the connection 

		if(reconnect)
		{
			/**
			 * The ap (dummy or not) used to activate the connection define if is external o not
			 */
			nm.ActivateConnection(adhoc,dv,ap);
			reconnect = false;
		}
		else if(!CheckConnectionStatus(ssid,ipAddress,netmask,macAddress)) //If the connection is already created we check if is up or reconnecting
			nm.ActivateConnection(adhoc,dv,ap); //The connection is down so we active it throw NetworkManager (ct - objeto conexion,dv - objeto device,ap - objeto access point)
		
		control.release();
	}
	
	/**
	 * We check if the connection is already created 
	 * @param st
	 * @param ssid
	 * @param ipAddress
	 * @param netmask
	 * @param macAddress
	 * @return
	 */
	private Connection isConnectionAlreadyCreated(Settings st, String ssid, String ipAddress, String netmask,String macAddress)
	{
		List <DBusInterface> configuredConnections  = st.ListConnections();

		for (DBusInterface dbI: configuredConnections)
		{
			Connection ct = (Connection) dbI;
			if(checkConnectionSettings(ct,ssid,ipAddress,netmask,macAddress))
				return ct;			
		}
		
		return null;
	}
	
	/**
	 * We check for the requested connection (ssid) around us 
	 * 
	 * @param ssid
	 * @return
	 * @throws InterruptedException
	 * @throws DBusException
	 */
	private AccessPoint ScanConnection(String ssid) throws InterruptedException, DBusException
	{
		Map<String,Variant> scanOptions = new HashMap<String,Variant>();
		try {
			wr.RequestScan(scanOptions);
		} catch (DBusExecutionException e) {
			System.out.println("Exception Scanning: " + e.getLocalizedMessage());
		}

		List <DBusInterface> accessPoints = wr.GetAccessPoints();
		for (DBusInterface apI: accessPoints)
		{
			AccessPoint ap = (AccessPoint) apI;
			Properties p = (Properties) apI;
			Map<String,Variant> apSettings = p.GetAll("org.freedesktop.NetworkManager.AccessPoint");

			String ssidI = new String((byte [])apSettings.get("Ssid").getValue());

			if(ssidI.equals(ssid))
				return ap;
		}
		
		return null;
	}
	
	/**
	 * Main method to set the configuration of the new connection
	 * 
	 * @param ssid
	 * @param ipAddress
	 * @param netmask
	 * @param macAddress
	 * @return
	 */
	private Map<String,Map<String,Variant>> ConnectionSettings(String ssid,String ipAddress,String netmask,String macAddress)
	{
		/* Change the hex representation of the mac to bytes */
		String[] macAddressParts = macAddress.split(":");
		byte[] macAddressBytes = new byte[6];
		for(int i=0; i<6; i++){
		    Integer hex = Integer.parseInt(macAddressParts[i], 16);
		    macAddressBytes[i] = hex.byteValue();
		}
		
		Map<String,Variant> section802 = new HashMap<String,Variant>();
		//section802.put("security",new Variant<String>("802-11-wireless-security"));
		//section802.put("band",new Variant<String>("bg"));
		//section802.put("channel", new Variant<UInt32>(new UInt32(1)));
		section802.put("ssid",new Variant<byte[]>(ssid.getBytes()));
		section802.put("mac-address",new Variant<byte[]>(macAddressBytes));
		section802.put("mode",new Variant<String>("adhoc"));

		Map<String,Variant> sectionConnection = new HashMap<String,Variant>();
		sectionConnection.put("id",new Variant<String>(ssid));
		sectionConnection.put("type", new Variant<String>("802-11-wireless"));
		sectionConnection.put("uuid",new Variant<String>(UUID.randomUUID().toString()));
		//sectionConnection.put("zone",new Variant<String>("public"));

		UInt32 [] element = {new UInt32(Utils.toLong(ipAddress)),new UInt32(Utils.netmaskToCIDR(netmask)),new UInt32(0)};
		UInt32 [][] adresses = {element};

		Map<String,Variant> sectionIpv4 = new HashMap<String,Variant>();
		sectionIpv4.put("addresses",new Variant<UInt32[][]>(adresses));
		sectionIpv4.put("method",new Variant<String>("manual"));

		//Map<String,Variant> sectionSecurity = new HashMap<String,Variant>();
		//sectionSecurity.put("key-mgmt",new Variant<String>("wpa-psk"));

		
		Map<String,Map<String,Variant>> sections = new HashMap<String,Map<String,Variant>>();
		sections.put("802-11-wireless",section802);
		sections.put("connection", sectionConnection);
		sections.put("ipv4",sectionIpv4);
		//sections.put("802-11-wireless-security",sectionSecurity);
		return sections;
	}
	
	/**
	 * Check the settings and status of the connection
	 * @param ssid
	 * @param ipAddress
	 * @param netmask
	 * @param macAddress
	 * @return
	 * @throws DBusException
	 */
	private boolean CheckConnectionStatus(String ssid, String ipAddress, String netmask, String macAddress) throws DBusException
	{
		UInt32 state;
		String settingsPath;
		Properties p = (Properties) nm;
		Map<String,Variant> properties = p.GetAll("org.freedesktop.NetworkManager");
		
		/* Here we can have more than one active connection (ethernet or wireless, 2 wireless, etc..) */
		String activeConnectionsHolder = properties.get("ActiveConnections").getValue().toString();
		
		if(!activeConnectionsHolder.equals("[]"))
		{
			activeConnectionsHolder = activeConnectionsHolder.substring(1,activeConnectionsHolder.lastIndexOf("]"));
			String[] activeConnections = activeConnectionsHolder.split(",");

			for(String it : activeConnections)
			{
				it = it.trim();
				/* Properties of the active connection */
				p = bus.getRemoteObject("org.freedesktop.NetworkManager", it, Properties.class);
				settingsPath = p.Get("org.freedesktop.NetworkManager.Connection.Active", "Connection").toString();
				state = p.Get("org.freedesktop.NetworkManager.Connection.Active", "State");

				/**
				 * We check if the connection already establish had the same settings that the one we have 
				 *  State 2 means connecting and State 1 means connected 
				 * 
				 */
				Connection ct = (Connection) bus.getRemoteObject("org.freedesktop.NetworkManager",settingsPath);
				if(checkConnectionSettings(ct, ssid, ipAddress, netmask, macAddress) && (state.equals(new UInt32(2)) || state.equals(new UInt32(1))))
					return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Method to check is the connection that we want to establish have the same settings (ip & netmask) that the one we have
	 * @param ct
	 * @param ssid
	 * @param ipAddress
	 * @param netmask
	 * @param macAddress
	 * @return
	 */
	
	private boolean checkConnectionSettings(Connection ct,String ssid, String ipAddress, String netmask, String macAddress)
	{
		String ipCheck = new UInt32(Utils.toLong(ipAddress)).toString();
		String netmaskCheck = new UInt32(Utils.netmaskToCIDR(netmask)).toString();
		
		
		Map<String,Map<String,Variant>> ctSettings = ct.GetSettings(); 
		String id = ctSettings.get("connection").get("id").getValue().toString(); //First we get the ssid of the active connection
		
		String temp = ctSettings.get("ipv4").get("addresses").getValue().toString();  //The ip of the current connection
		if(!temp.equals("[]") && id.equals(ssid))
		{
			String[] stConfigured = temp.substring(2,temp.length()-2).split(",");
		
			/* Check Point */
			if(stConfigured[0].trim().equals(ipCheck) && stConfigured[1].trim().equals(netmaskCheck))
				return true;

			/* If the parameters are not the same we update the settings and request a reconnection*/
			ct.Update(ConnectionSettings(ssid, ipAddress, netmask, macAddress));
			reconnect = reconnect == false ? true : false;
			return true;
		}
		
		return false;
	}
}
