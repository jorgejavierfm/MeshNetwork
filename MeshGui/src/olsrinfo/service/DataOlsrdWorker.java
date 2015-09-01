package olsrinfo.service;

import gui.model.IMeshService;
import gui.model.ServiceLevel;
import gui.model.Synchronizer;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import olsrinfo.service.datatypes.Interface;
import olsrinfo.service.datatypes.Link;
import olsrinfo.service.datatypes.Neighbor;
import olsrinfo.service.datatypes.Node;
import olsrinfo.service.datatypes.Route;

/* THE USE OF SWINGWORKER IS FOLLOWING ORACLE GUIDELINES FOR UPDATES ON THE GUI */
public class DataOlsrdWorker extends SwingWorker<Void,DataOlsrd> implements IMeshService
{
	//We need the model of each table in order to update them later,the models define the structure of each table
	DefaultTableModel linkTable; 
	DefaultTableModel neighborsTable;
	DefaultTableModel topologyTable;
	DefaultTableModel hnaTable;
	DefaultTableModel routeTable;
	DefaultTableModel interfaceTable;
	
	long updateRate;
	DataOlsrd tablesData;
	
	public DataOlsrdWorker(List<TableModel> model,long _updateRate)
	{
		//Each data table is in a general list to save parameters in the constructor
		//Predefined order of every table
		linkTable = (DefaultTableModel) model.get(0);
		neighborsTable = (DefaultTableModel) model.get(1);
		topologyTable = (DefaultTableModel) model.get(2);
		hnaTable = (DefaultTableModel) model.get(3);
		routeTable = (DefaultTableModel) model.get(4);
		interfaceTable = (DefaultTableModel) model.get(5);
		
		updateRate = _updateRate; //Update rate of our data 
		tablesData = new DataOlsrd(); //This class will handle the updates of our tables and their background logic in general
	}
	
	@Override
	protected Void doInBackground()
	{
		Synchronizer.getInstance().synchronize(this);
		
		while(!isCancelled())
		{
			tablesData.udpate(); //This update the data in each table
			publish(tablesData); //This will update our GUI with the current data
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
    protected void process(List<DataOlsrd> data) {
        DataOlsrd lastPublish = data.get(data.size() - 1); //Retrieve the last data update
        //Update each table with the latest data 
        linkTable.setDataVector(lastPublish.getLinkTable(),Link.columnNames()); 
        neighborsTable.setDataVector(lastPublish.getNeighborsTable(),Neighbor.columnNames());
        topologyTable.setDataVector(lastPublish.getTopologyTable(),Node.columnNames());
        routeTable.setDataVector(lastPublish.routeTable,Route.columnNames());
        interfaceTable.setDataVector(lastPublish.getInterfaceTable(),Interface.columnNames());
    }
	
	@Override
    protected void done() {
		//Once we are done we clear all the elements from the tables and reset
		linkTable.getDataVector().removeAllElements();
        linkTable.setDataVector(linkTable.getDataVector(),Link.columnNames());
        
        neighborsTable.getDataVector().removeAllElements();
        neighborsTable.setDataVector(neighborsTable.getDataVector(),Neighbor.columnNames());
        
        topologyTable.getDataVector().removeAllElements();
        topologyTable.setDataVector(topologyTable.getDataVector(),Node.columnNames());
        
        routeTable.getDataVector().removeAllElements();
        routeTable.setDataVector(routeTable.getDataVector(),Route.columnNames());
        
        interfaceTable.getDataVector().removeAllElements();
        interfaceTable.setDataVector(interfaceTable.getDataVector(),Interface.columnNames());
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
