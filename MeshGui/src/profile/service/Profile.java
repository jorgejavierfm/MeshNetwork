package profile.service;

public class Profile 
{
	private String interfaceOlsr;
	private String ssid;
	private String ip;
	private String netmask;
	
	public Profile(String _interfaceOlsr,String _ssid,String _ip,String _netmask)
	{
		interfaceOlsr = _interfaceOlsr;
		ssid = _ssid;
		ip = _ip;
		netmask = _netmask;
	}

	public String getInterfaceOlsr() {
		return interfaceOlsr;
	}

	public String getSsid() {
		return ssid;
	}

	public String getIp() {
		return ip;
	}

	public String getNetmask() {
		return netmask;
	}
	
	
}
