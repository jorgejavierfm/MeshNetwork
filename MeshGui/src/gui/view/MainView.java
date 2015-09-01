package gui.view;

import gui.model.ManageServices;
import gui.model.PacketGUI;
import gui.model.TableModels;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.UIManager;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.JScrollPane;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.layout.springbox.implementations.SpringBox;
import org.graphstream.ui.swingViewer.View;
import org.graphstream.ui.swingViewer.Viewer;



import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JTabbedPane;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.BoxLayout;
import javax.swing.JTextArea;


public class MainView extends JFrame {
	
	private JMenuItem mntmActivate;
	private JMenuItem mntmDeactivate;
	private JMenuItem mntmCreateProfile;
	private JMenuItem mntmLoadProfile;
	private JMenuItem mntmNewMessage;
	private JMenuItem mntmAbout;
	
	private MessagesView messageWindow;
	private ProfileView profileWindow;

	private ManageServices ms;
	private TableModels tm;
	private Graph graph;
	private PacketGUI pckG;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) 
	{
		// For the topology render (read graphstream documentation)
		System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
		
		EventQueue.invokeLater(new Runnable() 
		{
			public void run() 
			{
				try {
					MainView frame = new MainView();
					frame.setVisible(true);	
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/* For the creation of the tables in the interface we need the Table Models first */
	/* In order to integrate the topology in the GUI we need to create the viewer with an initialized graph so we create it in drawIntefaces() */
	/* Next we create the thread that will handle all the other services */
	/* To conclude we add the listener for the menu items and the exit function */
	
	public MainView() 
	{
		tm = new TableModels();
		drawInterface(); 
		
		ms = new ManageServices(tm,graph,pckG);
		addListeners();
	}
	
	private void drawInterface()
	{
		
		//================================================================================
	    // Context
	    //================================================================================
		
		setTitle("Mesh Gui");
		setBounds(100, 100, 735, 600);
		
		JPanel panel = new JPanel();
		
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(panel, GroupLayout.DEFAULT_SIZE, 721, Short.MAX_VALUE)
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(panel, GroupLayout.DEFAULT_SIZE, 585, Short.MAX_VALUE)
					.addContainerGap())
		);
		getContentPane().setLayout(groupLayout);
		
		//================================================================================
	    // General Tabs
	    //================================================================================
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addComponent(tabbedPane, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 716, Short.MAX_VALUE)
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addComponent(tabbedPane, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 573, Short.MAX_VALUE)
		);
		panel.setLayout(gl_panel);
		
		//================================================================================
	    // Olsr Tab
	    //================================================================================
		
		JPanel olsrTab = new JPanel();
		tabbedPane.addTab("Olsr Info", null, olsrTab, null);
		olsrTab.setLayout(new BoxLayout(olsrTab, BoxLayout.Y_AXIS));
		
		//================================================================================
	    // Sniffer Tab
	    //================================================================================
		
		JPanel sniffer = new JPanel();
		tabbedPane.addTab("Sniffer", null, sniffer, null);
		
		JScrollPane scrollPaneDebug = new JScrollPane();
		JTextArea snifferText = new JTextArea();
		pckG = new PacketGUI(snifferText);
		scrollPaneDebug.setViewportView(snifferText);
		
		GroupLayout gl_sniffer = new GroupLayout(sniffer);
		gl_sniffer.setHorizontalGroup(
			gl_sniffer.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_sniffer.createSequentialGroup()
					.addGap(12)
					.addComponent(scrollPaneDebug, GroupLayout.DEFAULT_SIZE, 682, Short.MAX_VALUE)
					.addGap(12))
		);
		gl_sniffer.setVerticalGroup(
			gl_sniffer.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_sniffer.createSequentialGroup()
					.addGap(12)
					.addComponent(scrollPaneDebug, GroupLayout.DEFAULT_SIZE, 510, Short.MAX_VALUE)
					.addContainerGap())
		);
		sniffer.setLayout(gl_sniffer);
		
		//================================================================================
	    // Topology Tab
	    //================================================================================
		
		graph = new MultiGraph("embedded");
		Viewer viewer = new Viewer(graph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
		viewer.enableAutoLayout(new SpringBox());
		View view = viewer.addDefaultView(false);   // false indicates "no JFrame".
		tabbedPane.addTab("Topology", null,view, null);
		
		//================================================================================
	    // Links Panel (OLSR Tab)
	    //================================================================================
		
		JPanel linksPanel = new JPanel();
		linksPanel.setBorder(new TitledBorder(null, "Links", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		JScrollPane linkScrollPane = new JScrollPane();
		linkScrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		
		JTable linksTable = new JTable(tm.getLinkTableModel());
		linksTable.setEnabled(false);
		linksTable.setRowSelectionAllowed(false);
		linkScrollPane.setViewportView(linksTable);
		olsrTab.add(linksPanel);
		
		GroupLayout gl_linksPanel = new GroupLayout(linksPanel);
		gl_linksPanel.setHorizontalGroup(
			gl_linksPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_linksPanel.createSequentialGroup()
					.addContainerGap()
					.addComponent(linkScrollPane, GroupLayout.DEFAULT_SIZE, 672, Short.MAX_VALUE)
					.addContainerGap())
		);
		gl_linksPanel.setVerticalGroup(
			gl_linksPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_linksPanel.createSequentialGroup()
					.addGap(6)
					.addComponent(linkScrollPane, GroupLayout.DEFAULT_SIZE, 55, Short.MAX_VALUE))
		);
		linksPanel.setLayout(gl_linksPanel);
		
		//================================================================================
	    // Neighbors Panel (OLSR Tab)
	    //================================================================================
		
		JPanel neighborsPanel = new JPanel();
		neighborsPanel.setBorder(new TitledBorder(null, "Neighbors", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		JScrollPane neighborsScrollPanel = new JScrollPane();
		neighborsScrollPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
		
		JTable neighborsTable = new JTable(tm.getNeighborsTableModel());
		neighborsTable.setEnabled(false);
		neighborsTable.setRowSelectionAllowed(false);
		neighborsScrollPanel.setViewportView(neighborsTable);
		olsrTab.add(neighborsPanel);
		
		GroupLayout gl_neighborsPanel = new GroupLayout(neighborsPanel);
		gl_neighborsPanel.setHorizontalGroup(
			gl_neighborsPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_neighborsPanel.createSequentialGroup()
					.addContainerGap()
					.addComponent(neighborsScrollPanel, GroupLayout.DEFAULT_SIZE, 672, Short.MAX_VALUE)
					.addContainerGap())
		);
		gl_neighborsPanel.setVerticalGroup(
			gl_neighborsPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_neighborsPanel.createSequentialGroup()
					.addGap(6)
					.addComponent(neighborsScrollPanel, GroupLayout.DEFAULT_SIZE, 55, Short.MAX_VALUE))
		);
		neighborsPanel.setLayout(gl_neighborsPanel);
		
		
		//================================================================================
	    // Topology Panel (OLSR Tab)
	    //================================================================================
		
		JPanel topologyPanel = new JPanel();
		topologyPanel.setBorder(new TitledBorder(null, "Topology", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		JScrollPane topologyScrollPanel = new JScrollPane();
		topologyScrollPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
		
		JTable topologyTable = new JTable(tm.getTopologyTableModel());
		topologyTable.setEnabled(false);
		topologyTable.setRowSelectionAllowed(false);
		topologyScrollPanel.setViewportView(topologyTable);
		olsrTab.add(topologyPanel);
		
		GroupLayout gl_topologyPanel = new GroupLayout(topologyPanel);
		gl_topologyPanel.setHorizontalGroup(
			gl_topologyPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_topologyPanel.createSequentialGroup()
					.addContainerGap()
					.addComponent(topologyScrollPanel, GroupLayout.DEFAULT_SIZE, 672, Short.MAX_VALUE)
					.addContainerGap())
		);
		gl_topologyPanel.setVerticalGroup(
			gl_topologyPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_topologyPanel.createSequentialGroup()
					.addGap(6)
					.addComponent(topologyScrollPanel, GroupLayout.DEFAULT_SIZE, 55, Short.MAX_VALUE))
		);
		topologyPanel.setLayout(gl_topologyPanel);
		
		//================================================================================
	    // HNA Panel (OLSR Tab)
	    //================================================================================
		
		JPanel hnaPanel = new JPanel();
		hnaPanel.setBorder(new TitledBorder(null, "HNA", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		JScrollPane hnaScrollPanel = new JScrollPane();
		hnaScrollPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
		
		JTable hnaTable = new JTable(tm.getHnaTableModel());
		hnaTable.setEnabled(false);
		hnaTable.setRowSelectionAllowed(false);
		hnaScrollPanel.setViewportView(hnaTable);
		olsrTab.add(hnaPanel);
		
		GroupLayout gl_hnaPanel = new GroupLayout(hnaPanel);
		gl_hnaPanel.setHorizontalGroup(
			gl_hnaPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_hnaPanel.createSequentialGroup()
					.addContainerGap()
					.addComponent(hnaScrollPanel, GroupLayout.DEFAULT_SIZE, 672, Short.MAX_VALUE)
					.addContainerGap())
		);
		gl_hnaPanel.setVerticalGroup(
			gl_hnaPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_hnaPanel.createSequentialGroup()
					.addGap(6)
					.addComponent(hnaScrollPanel, GroupLayout.DEFAULT_SIZE, 55, Short.MAX_VALUE))
		);
		hnaPanel.setLayout(gl_hnaPanel);
		
		//================================================================================
	    // Routes Panel (OLSR Tab)
	    //================================================================================
		
		JPanel routePanel = new JPanel();
		routePanel.setBorder(new TitledBorder(null, "Routes", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		JScrollPane routeScrollPanel = new JScrollPane();
		routeScrollPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
		
		JTable routeTable = new JTable(tm.getRouteTableModel());
		routeTable.setEnabled(false);
		routeTable.setRowSelectionAllowed(false);
		routeScrollPanel.setViewportView(routeTable);
		olsrTab.add(routePanel);
		
		GroupLayout gl_routePanel = new GroupLayout(routePanel);
		gl_routePanel.setHorizontalGroup(
			gl_routePanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_routePanel.createSequentialGroup()
					.addContainerGap()
					.addComponent(routeScrollPanel, GroupLayout.DEFAULT_SIZE, 672, Short.MAX_VALUE)
					.addContainerGap())
		);
		gl_routePanel.setVerticalGroup(
			gl_routePanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_routePanel.createSequentialGroup()
					.addGap(6)
					.addComponent(routeScrollPanel, GroupLayout.DEFAULT_SIZE, 55, Short.MAX_VALUE))
		);
		routePanel.setLayout(gl_routePanel);
		
		//================================================================================
	    // Interface Panel (OLSR Tab)
	    //================================================================================
		
		JPanel interfacePanel = new JPanel();
		interfacePanel.setBorder(new TitledBorder(null, "Interfaces", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		JScrollPane interfaceScrollPanel = new JScrollPane();
		interfaceScrollPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
		
		JTable interfaceTable = new JTable(tm.getInterfaceTableModel());
		interfaceTable.setEnabled(false);
		interfaceTable.setRowSelectionAllowed(false);
		interfaceScrollPanel.setViewportView(interfaceTable);
		olsrTab.add(interfacePanel);
		
		GroupLayout gl_interfacePanel = new GroupLayout(interfacePanel);
		gl_interfacePanel.setHorizontalGroup(
			gl_interfacePanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_interfacePanel.createSequentialGroup()
					.addContainerGap()
					.addComponent(interfaceScrollPanel, GroupLayout.DEFAULT_SIZE, 672, Short.MAX_VALUE)
					.addContainerGap())
		);
		gl_interfacePanel.setVerticalGroup(
			gl_interfacePanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_interfacePanel.createSequentialGroup()
					.addGap(6)
					.addComponent(interfaceScrollPanel, GroupLayout.DEFAULT_SIZE, 55, Short.MAX_VALUE))
		);
		interfacePanel.setLayout(gl_interfacePanel);
		
        //================================================================================
	    // Menu Bar
	    //================================================================================
        
		JMenuBar menuBar = new JMenuBar();
		menuBar.setBorder(null);
		setJMenuBar(menuBar);
		
		//================================================================================
	    // SubMenu "Connection"
	    //================================================================================
		
		JMenu mnConnection = new JMenu("Connection");
		menuBar.add(mnConnection);
		
		mntmActivate = new JMenuItem("Activate...");
		mnConnection.add(mntmActivate);
		
		mntmDeactivate = new JMenuItem("Deactivate...");
		mnConnection.add(mntmDeactivate);
		
		//================================================================================
	    // SubMenu "Profile"
	    //================================================================================
		
		JMenu mnNewMenu = new JMenu("Profile");
		mnNewMenu.setBorder(UIManager.getBorder("Menu.border"));
		menuBar.add(mnNewMenu);
		
		mntmCreateProfile = new JMenuItem("Create Profile");
		mntmCreateProfile.setBorder(UIManager.getBorder("MenuItem.border"));
		mnNewMenu.add(mntmCreateProfile);
		
		mntmLoadProfile = new JMenuItem("Load Profile");
		mntmLoadProfile.setBorder(UIManager.getBorder("MenuItem.border"));
		mnNewMenu.add(mntmLoadProfile);
		
		//================================================================================
	    // SubMenu "Message"
	    //================================================================================
		
		JMenu mnMessage = new JMenu("Message");
		menuBar.add(mnMessage);
		
		mntmNewMessage = new JMenuItem("New message...");
		mnMessage.add(mntmNewMessage);
		
		//================================================================================
	    // SubMenu "Help"
	    //================================================================================
		
		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);
		
		mntmAbout = new JMenuItem("About");
		mnHelp.add(mntmAbout);
	}
	
	private void addListeners()
	{
		//================================================================================
	    // SubMenu "Connection" (Listeners)
	    //================================================================================
		
		mntmActivate.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0)
			{	
				ms.stop(); //Por si tenemos una conexion activada
				ms.init();
			}
		});
				
		mntmDeactivate.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e)
			{
				ms.stop();
			}
		});
		
		//================================================================================
	    // SubMenu "Profile" (Listeners)
	    //================================================================================
		
		mntmCreateProfile.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0) {
				profileWindow = new ProfileView("Save");
				profileWindow.setVisible(true);
			}
		});
		
		mntmLoadProfile.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) {
				profileWindow = new ProfileView("Load");
				profileWindow.setVisible(true);
			}
		});
		
		//================================================================================
	    // SubMenu "Message" (Listeners)
	    //================================================================================
		
		/* We initialize messageWindows because its needed to collect new messages if the windows is not open yet */
		/* It have to be after the initialization of ManageServices because we need the MessageReceiver thread so we can register the window to get the messages */
		messageWindow = new MessagesView(ms.getMessageReceiver());
		
		mntmNewMessage.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) {
				messageWindow.setVisible(true);
			}
		});
		
		//================================================================================
	    // About
	    //================================================================================
		
		mntmAbout.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) {
				AboutView about = new AboutView();
				about.setVisible(true);
			}
		});
				
				
		//================================================================================
	    // Exit function 
	    //================================================================================
		
		WindowListener exitListener = new WindowAdapter() 
		{
            @Override
            public void windowClosing(WindowEvent e) {
            	if(ms != null)
            		ms.stop();
                System.exit(0);
            }
        };
        
        this.addWindowListener(exitListener);
	}
}
