package olsrinfo.service.datatypes;

import java.util.Vector;

/**
 * A Host and Network Association (HNA) between the current mesh network
 * and another network that is accessible via a given node in the mesh.
 *  
 */
public class HNA {
	public String destination;
	public int genmask;
	public String gateway;
	public int validityTime;
	
	public static Vector columnNames()
	{
		Vector<String> col = new Vector<String>();
		col.add("Destination");
		col.add("Gateway");
		
		return col;
	}
}
