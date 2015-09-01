package olsrinfo.service.datatypes;

import java.util.Vector;

/**
 * A node in the whole mesh topology view.
 * 
 */
public class Node {
	public String destinationIP;
	public String lastHopIP;
	public float linkQuality;
	public float neighborLinkQuality;
	public int tcEdgeCost;
	public int validityTime;
	
	public static Vector columnNames()
	{
		Vector<String> col = new Vector<String>();
		col.add("Dest. IP");
		col.add("Last hop IP");
		col.add("LQ (%)");
		col.add("NLQ (%)");
		col.add("Cost");
		col.add("Hyst. (ms)");
		return col;
	}
	
	public String columnValues()
	{
		StringBuilder values = new StringBuilder();
		values.append(destinationIP + ",").append(lastHopIP + ",");
		values.append(linkQuality + ",").append(neighborLinkQuality + ",");
		values.append(tcEdgeCost + ",").append(validityTime);
		
		return values.toString();
	}
}
