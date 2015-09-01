package olsrinfo.service;

import java.util.Vector;

import olsrinfo.service.JsonInfo;
import olsrinfo.service.datatypes.Interface;
import olsrinfo.service.datatypes.Link;
import olsrinfo.service.datatypes.Neighbor;
import olsrinfo.service.datatypes.Node;
import olsrinfo.service.datatypes.OlsrDataDump;
import olsrinfo.service.datatypes.Route;

public class DataOlsrd
{	
	Vector<Vector<String>> linkTable;
	Vector<Vector<String>> neighborsTable;
	Vector<Vector<String>> topologyTable;
	Vector<Vector<String>> routeTable;
	Vector<Vector<String>> interfaceTable;
	
	public Vector<Vector<String>> getLinkTable() {
		if(linkTable == null)
			return new Vector<Vector<String>>();
		else
			return linkTable;
	}
	
	public Vector<Vector<String>> getNeighborsTable() {
		if(neighborsTable == null)
			return new Vector<Vector<String>>();
		else
			return neighborsTable;
	}
	
	public Vector<Vector<String>> getTopologyTable() {
		if(topologyTable == null)
			return new Vector<Vector<String>>();
		else
			return topologyTable;
	}
	
	public Vector<Vector<String>> getRouteTable() {
		if(routeTable == null)
			return new Vector<Vector<String>>();
		else
			return routeTable;
	}
	
	public Vector<Vector<String>> getInterfaceTable() {
		if(interfaceTable == null)
			return new Vector<Vector<String>>();
		else
			return interfaceTable;
	}
	
	public void udpate()
	{
		JsonInfo jsoninfo = new JsonInfo();
		OlsrDataDump dump = jsoninfo.all();
		Vector<String> columns;
		
		linkTable = new Vector<Vector<String>>();
		neighborsTable = new Vector<Vector<String>>();
		topologyTable = new Vector<Vector<String>>();
		routeTable = new Vector<Vector<String>>();
		interfaceTable = new Vector<Vector<String>>();
				
		for (Link l : dump.links)
		{
			columns = new Vector<String>();
			String[] values = l.columnValues().split(",");
			for(String s : values)
				columns.add(s);
			
			linkTable.add(columns);
		}
		
		for (Neighbor n : dump.neighbors)
		{
			columns = new Vector<String>();
			String[] values = n.columnValues().split(",");
			for(String s : values)
				columns.add(s);
			
			neighborsTable.add(columns);
		}
		
		for (Node node : dump.topology)
		{
			columns = new Vector<String>();
			String[] values = node.columnValues().split(",");
			
			if(!values[values.length-1].equals("0"))
			{
				for(String s : values)
					columns.add(s);
				
				topologyTable.add(columns);
			}
		}
		
		for(Route r : dump.routes)
		{
			columns = new Vector<String>();
			String[] values = r.columnValues().split(",");
			for(String s : values)
				columns.add(s);
			
			routeTable.add(columns);
		}
		
		for(Interface i : dump.interfaces)
		{
			columns = new Vector<String>();
			String[] values = i.columnValues().split(",");
			for(String s : values)
				columns.add(s);
			
			interfaceTable.add(columns);
		}
	}
}
