package olsrinfo.service.datatypes;

import java.util.Vector;

/**
 * A mesh link between two nodes in the mesh.
 * 
 */
public class Link {
	public String localIP;
	public String remoteIP;
	public int validityTime;
	public float linkQuality;
	public float neighborLinkQuality;
	public int linkCost;
	
	public static Vector columnNames()
	{
		Vector<String> col = new Vector<String>();
		col.add("Local IP");
		col.add("Remote IP");
		col.add("Hyst. (ms)");
		col.add("LQ (%)");
		col.add("NLQ (%)");
		col.add("Cost");
		
		return col;
	}
	
	public String columnValues()
	{
		StringBuilder values = new StringBuilder();
		values.append(localIP + ",").append(remoteIP + ",");
		values.append(validityTime + ",").append(linkQuality + ",");
		values.append(neighborLinkQuality + ",").append(linkCost);
		
		return values.toString();
	}
}
