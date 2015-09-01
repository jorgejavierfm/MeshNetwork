package gui.model;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import olsrinfo.service.datatypes.HNA;
import olsrinfo.service.datatypes.Interface;
import olsrinfo.service.datatypes.Link;
import olsrinfo.service.datatypes.Neighbor;
import olsrinfo.service.datatypes.Node;
import olsrinfo.service.datatypes.Route;

public class TableModels
{
	DefaultTableModel linkTableModel,neighborsTableModel;
	DefaultTableModel topologyTableModel,hnaTableModel;
	DefaultTableModel routeTableModel,interfaceTableModel;
	
	public TableModels()
	{
		linkTableModel = new DefaultTableModel(Link.columnNames(),0);
		neighborsTableModel = new DefaultTableModel(Neighbor.columnNames(),0);
		topologyTableModel = new DefaultTableModel(Node.columnNames(),0);
		hnaTableModel = new DefaultTableModel(HNA.columnNames(),0);
		routeTableModel = new DefaultTableModel(Route.columnNames(),0);
		interfaceTableModel = new DefaultTableModel(Interface.columnNames(),0);
	}

	public DefaultTableModel getLinkTableModel() {
		return linkTableModel;
	}

	public DefaultTableModel getNeighborsTableModel() {
		return neighborsTableModel;
	}

	public DefaultTableModel getTopologyTableModel() {
		return topologyTableModel;
	}

	public DefaultTableModel getHnaTableModel() {
		return hnaTableModel;
	}

	public DefaultTableModel getRouteTableModel() {
		return routeTableModel;
	}

	public DefaultTableModel getInterfaceTableModel() {
		return interfaceTableModel;
	}
	
	public List<TableModel> getAllModels()
	{
		List <TableModel> models = new ArrayList<TableModel>();
		models.add(linkTableModel);
		models.add(neighborsTableModel);
		models.add(topologyTableModel);
		models.add(hnaTableModel);
		models.add(routeTableModel);
		models.add(interfaceTableModel);
		
		return models;
	}
}
