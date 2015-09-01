package topology.service;

import java.util.HashMap;
import java.util.Iterator;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.AbstractElement.AttributeChangeEvent;
import org.graphstream.stream.PipeBase;

public class DynamicDotPipe extends PipeBase{

	Graph graph = null;
	HashMap<String,String> dd = null;
	
	public DynamicDotPipe(Graph _graph) {
		super();
		graph = _graph;
		dd = new HashMap<String, String>();
	}
	
	@Override
	public void nodeAdded(String graphId, long timeId, String nodeId)
	{
		for(Node node: graph.getEachNode())
		{
			if(node.getId().equals(nodeId))
			{
				dd.replace(nodeId,"renew");
				return;
			}
		}
		
		dd.put(nodeId,"new");
		super.nodeAdded(graphId, timeId, nodeId);
	}
	
	@Override
	public void edgeAdded(String graphId, long timeId, String edgeId, String fromNodeId, String toNodeId, boolean directed)
	{
		boolean reverse = false;
		
		for(Edge edge: graph.getEachEdge())
		{
			if(edge.getId().equals(edgeId))
				return;
			else if(edge.getSourceNode().getId().equals(toNodeId) && edge.getTargetNode().getId().equals(fromNodeId))
				reverse = true; //To solve the overlap problem with returning edges
		}
		
		super.edgeAdded(graphId, timeId, edgeId, fromNodeId, toNodeId, directed);
		
		if(reverse)
			graph.getEdge(edgeId).addAttribute("ui.class","reverse");
	}
	
	@Override
	public void nodeAttributeAdded(String graphId, long timeId, String nodeId,String attribute, Object value)
	{
		//Manually added because the super method add the node again producing an error in the graph with the Singleton nodes
		super.sendAttributeChangedEvent(graphId,timeId,nodeId,ElementType.NODE,attribute,AttributeChangeEvent.ADD,null,value);
	}
	
	public void updateGraph()
	{
		if(dd.size()!=0)
		{
			Iterator<String> it= dd.keySet().iterator();
			while(it.hasNext())
			{
				String key = (String) it.next();
				if(dd.get(key).equals("old"))
				{
					graph.removeNode(key); //The node gets removed if it is not new or renewed
					it.remove();
				}
				else
					dd.replace(key,"old"); //To finalize we make all the nodes old so they have to be renewed
			}
		}
	}
}
