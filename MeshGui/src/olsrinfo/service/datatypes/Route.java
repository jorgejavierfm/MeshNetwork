package olsrinfo.service.datatypes;

import java.util.Vector;

/**
 * A class representing a mesh route for a given node.
 * 
 */
public class Route {
	public String destination;
	public int genmask;
	public String gateway;
	public int metric;
	public int rtpMetricCost;
	public String networkInterface;
	
	public static Vector columnNames()
	{
		Vector<String> col = new Vector<String>();
		col.add("Destination");
		col.add("Gateway");
		col.add("Metric");
		col.add("EXT");
		col.add("Interface");
		
		return col;
	}
	
	public String columnValues()
	{
		StringBuilder values = new StringBuilder();
		values.append(destination + "/" + genmask + ",").append(gateway + ",");
		values.append(metric + ",").append(rtpMetricCost + ",");
		values.append(networkInterface);
		
		return values.toString();
	}
}
