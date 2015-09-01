package connection.service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.freedesktop.NetworkManager;
import org.freedesktop.DBus.Properties;
import org.freedesktop.NetworkManager.Device;
import org.freedesktop.dbus.DBusConnection;
import org.freedesktop.dbus.DBusInterface;
import org.freedesktop.dbus.UInt32;
import org.freedesktop.dbus.exceptions.DBusException;

public class Utils 
{
	/**
	 * Returns the long format of the provided IP address.
	 *
	 * @param ipAddress the IP address
	 * @return the long format of <code>ipAddress</code>
	 * @throws IllegalArgumentException if <code>ipAddress</code> is invalid
	 */
	public static long toLong(String _ipAddress) 
	{
		String ipAddress = reverse(_ipAddress);

		if (ipAddress == null || ipAddress.isEmpty()) {
			throw new IllegalArgumentException("ip address cannot be null or empty");
		}
		String[] octets = ipAddress.split(java.util.regex.Pattern.quote("."));
		if (octets.length != 4) {
			throw new IllegalArgumentException("invalid ip address");
		}
		long ip = 0;
		for (int i = 3; i >= 0; i--) {
			long octet = Long.parseLong(octets[3 - i]);
			if (octet > 255 || octet < 0) {
				throw new IllegalArgumentException("invalid ip address");
			}
			ip |= octet << (i * 8);
		}
		return ip;
	}

	/**
	 * Returns the 32bit dotted format of the provided long ip.
	 *
	 * @param ip the long ip
	 * @return the 32bit dotted format of <code>ip</code>
	 * @throws IllegalArgumentException if <code>ip</code> is invalid
	 */
	public static String toString(long ip) 
	{
		// if ip is bigger than 255.255.255.255 or smaller than 0.0.0.0
		if (ip > 4294967295l || ip < 0) {
			throw new IllegalArgumentException("invalid ip");
		}
		StringBuilder ipAddress = new StringBuilder();
		for (int i = 3; i >= 0; i--) {
			int shift = i * 8;
			ipAddress.append((ip & (0xff << shift)) >> shift);
			if (i > 0) {
				ipAddress.append(".");
			}
		}
		return reverse(ipAddress.toString());
	}

	private static String reverse(String ip)
	{
		List<String> ipNumbers = Arrays.asList(ip.split("\\."));
		StringBuilder ipReverse = new StringBuilder();

		for(String i : ipNumbers)
		{
			ipReverse.insert(0,i);
			ipReverse.insert(0,".");
		}

		ipReverse.deleteCharAt(0);

		return ipReverse.toString();
	}

	public static long netmaskToCIDR(String netmask)
	{
		InetAddress inet = null;
		try {
			inet = InetAddress.getByName(netmask);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		byte[] netmaskBytes = inet.getAddress();
		int cidr = 0;
		boolean zero = false;
		for(byte b : netmaskBytes)
		{
			int mask = 0x80;
			for(int i = 0; i < 8; i++)
			{
				int result = b & mask;
				if(result == 0)
					zero = true;
				else if(zero)
					throw new IllegalArgumentException("Invalid netmask.");
				else 
					cidr++;

				mask >>>= 1;
			}
		}
		return cidr;
	}
	
	public static List<String> getPhysicalWirelessInterfaces() throws DBusException
	{
		List<String> listInterfaces = new ArrayList<String>();
		DBusConnection bus = DBusConnection.getConnection(DBusConnection.SYSTEM);
		NetworkManager nm = (NetworkManager) bus.getRemoteObject("org.freedesktop.NetworkManager","/org/freedesktop/NetworkManager");
		
		List<DBusInterface> ifaces = nm.GetDevices();
		for(DBusInterface dbi : ifaces)
		{
			String st = dbi.toString().substring(dbi.toString().indexOf("/"),dbi.toString().lastIndexOf(":"));
			Device dv = (Device) bus.getRemoteObject("org.freedesktop.NetworkManager",st);
			Properties p = (Properties) dv;
			int type = ((UInt32) p.Get("org.freedesktop.NetworkManager.Device","DeviceType")).intValue();
			
			//The value that is assigned to wireless interfaces is 2
			if(type == 2)
			{
				String toAdd = p.Get("org.freedesktop.NetworkManager.Device","Interface");
				listInterfaces.add(toAdd);
			}
		}
		
		bus.disconnect();
		return listInterfaces;
	}
}
