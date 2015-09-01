package olsrinfo.service.datatypes;

/**
 * A gateway that <tt>olsrd</tt> is aware of.
 * 
 */
public class Gateway {
	public String ipv4Status;
	public String ipv6Status;
	public String ipType;
	public boolean ipv4;
	public boolean ipv4Nat;
	public boolean ipv6;
	public String ipAddress;
	public int tcPathCost;
	public int hopCount;
	public int uplinkSpeed;
	public int downlinkSpeed;
	public String externalPrefix;
}
