package olsrinfo.service.datatypes;

import java.util.Collection;
import java.util.Vector;

/**
 * A class representing a first-hop neighbor on the mesh.
 * 
 */
public class Neighbor {
	public String ipv4Address;
	public boolean symmetric;
	public boolean multiPointRelay;
	public boolean multiPointRelaySelector;
	public int willingness;
	public int twoHopNeighborCount;
	public Collection<String> twoHopNeighbors;
	
	public static Vector columnNames()
	{
		Vector<String> col = new Vector<String>();
		col.add("IP Address");
		col.add("SYM");
		col.add("MPR");
		col.add("MPRS");
		col.add("Will.");
		col.add("2-Hop Neighbors");
		
		return col;
	}
	
	public String columnValues()
	{
		StringBuilder values = new StringBuilder();
		values.append(ipv4Address + ",").append(symmetric + ",");
		values.append(multiPointRelay + ",").append(multiPointRelaySelector + ",");
		values.append(willingness + ",").append(twoHopNeighborCount);
		
		return values.toString();
	}
}
