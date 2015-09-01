package topology.service;

import gui.model.IMeshService;
import gui.model.ServiceLevel;
import gui.model.Synchronizer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingWorker;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceDOT;

public class TopologyVisWorker extends SwingWorker<Void,Void> implements IMeshService
{
	Graph graph = null;
	FileSource fs = null;
	Socket sock = null;
	DynamicDotPipe ddp = null;
	long updateRate;
	
	public TopologyVisWorker(Graph _graph,long _updateRate) 
	{
		graph = _graph;
		graph.addAttribute("ui.quality");
		graph.addAttribute("ui.antialias");
		graph.addAttribute("ui.stylesheet",parseCSS("./resources/styleSheet.css"));
		updateRate = _updateRate;
		
		fs = new FileSourceDOT();
		ddp = new DynamicDotPipe(graph);
	}
	
	protected String parseCSS(String path)
	{
		StringBuilder sb = new StringBuilder();
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(path));
			String currentLine;
			
			while((currentLine = br.readLine())!= null)
				sb.append(currentLine);
			
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return sb.toString();
	}
	
	@Override
	protected Void doInBackground()
	{	
		Synchronizer.getInstance().synchronize(this);
		
		fs.addSink(ddp);
		ddp.addSink(graph);
		
		while(!isCancelled())
		{	
			if(sock == null || sock.isClosed())
			{
				try {
					sock = new Socket("127.0.0.1",2004);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if(sock != null)
			{
				try {
					fs.readAll(sock.getInputStream());
				} catch (IOException e) {
					e.printStackTrace();
				}

				ddp.updateGraph();

				for(Node node: graph.getEachNode()) 
				{		
					node.setAttribute("ui.label",node.getId());

					if(node.hasAttribute("shape"))
						node.addAttribute("ui.class","c" + node.getAttribute("shape"));
				}

				for(Edge edge: graph.getEachEdge())
				{
					if(edge.hasAttribute("style"))
					{
						if(edge.getAttribute("style").equals("dashed"))
							edge.setAttribute("ui.size",0.3);
						else
							edge.setAttribute("ui.size",1);
					}
				}
			}
			try {
				Thread.sleep(updateRate);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return null;
	}

	@Override
    protected void done() 
	{
		try 
		{
			if(fs!=null)
				fs.removeSink(graph);
			if(sock!=null && !sock.isClosed())
				sock.close();
		} 
		catch( IOException e) {
			e.printStackTrace();
		} 
	
		graph.clear();
    }

	@Override
	public ServiceLevel getServiceLevel() {
		return ServiceLevel.APPLICATION;
	}

	@Override
	public void synchronize(CountDownLatch sync) 
	{
		boolean success = false;

		try {
			success = sync.await(60,TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if(!success)
			this.cancel(true);

	}
}
