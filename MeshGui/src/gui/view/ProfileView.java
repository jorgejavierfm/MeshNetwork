package gui.view;

import gui.model.ProfileUtils;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.border.TitledBorder;

import java.awt.Font;

import javax.swing.UIManager;

import java.awt.Color;

import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import javax.swing.border.LineBorder;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.AttributeSet;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JComboBox;

public class ProfileView extends JFrame {

	private String action;
	
	private JButton dynamicButton;
	private JButton cancelButton;
	
	private JTextField nameTextField;
	private JTextField ssidTextField;
	private JComboBox<String> wirelessComboBox;
	
	private JTextField ipFirstTextField;
	private JTextField ipSecondTextField;
	private JTextField ipThirdTextField;
	private JTextField ipFourthTextField;
	
	private JTextField nmFirstTextField;
	private JTextField nmSecondTextField;
	private JTextField nmThirdTextField;
	private JTextField nmFourthTextField;
	
	private ProfileUtils utils;
	
	/**
	 * Create the frame.
	 */
	public ProfileView(String _action) 
	{
		utils = new ProfileUtils();
		
		setResizable(false);
		action = _action;
		
		
		drawInterface();
		addListeners();
		interfacesComboBox();
	}
	
	private void drawInterface()
	{
		setTitle("Profile");
		setBounds(100, 100, 255, 290);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				
		//================================================================================
	    // BUTTONS
	    //================================================================================
		
		dynamicButton = new JButton(action);
		cancelButton = new JButton("Cancel");
		
		//================================================================================
	    // GENERAL LAYOUT
	    //================================================================================
		
		JPanel configurationPanel = new JPanel();
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(configurationPanel, GroupLayout.DEFAULT_SIZE, 229, Short.MAX_VALUE)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(dynamicButton, GroupLayout.PREFERRED_SIZE, 89, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(cancelButton, GroupLayout.PREFERRED_SIZE, 89, GroupLayout.PREFERRED_SIZE)))
					.addGap(14))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(configurationPanel, GroupLayout.DEFAULT_SIZE, 197, Short.MAX_VALUE)
					.addGap(18)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(dynamicButton, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
						.addComponent(cancelButton, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE))
					.addContainerGap())
		);
		
		//================================================================================
	    // Configuration Panel LAYOUT
	    //================================================================================
		
		configurationPanel.setFont(new Font("Tahoma", Font.PLAIN, 11));
		configurationPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Configuration", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		GridBagLayout gbl_configurationPanel = new GridBagLayout();
		gbl_configurationPanel.columnWidths = new int[]{74, 34, 34, 34, 29};
		gbl_configurationPanel.rowHeights = new int[]{27, 23, 27, 29, 29};
		configurationPanel.setLayout(gbl_configurationPanel);
		
		//================================================================================
	    // Name
	    //================================================================================
		
		JLabel lblName = new JLabel("Name");
		GridBagConstraints gbc_lblName = new GridBagConstraints();
		gbc_lblName.fill = GridBagConstraints.BOTH;
		gbc_lblName.insets = new Insets(0, 0, 5, 5);
		gbc_lblName.gridx = 0;
		gbc_lblName.gridy = 0;
		configurationPanel.add(lblName, gbc_lblName);
		lblName.setHorizontalAlignment(SwingConstants.CENTER);
		lblName.setFont(new Font("Tahoma", Font.PLAIN, 12));
		
		nameTextField = new JTextField();
		nameTextField.setBorder(new LineBorder(new Color(171, 173, 179)));
		GridBagConstraints gbc_nameTextField = new GridBagConstraints();
		gbc_nameTextField.gridwidth = 4;
		gbc_nameTextField.gridx = 1;
		gbc_nameTextField.fill = GridBagConstraints.BOTH;
		gbc_nameTextField.insets = new Insets(0, 0, 5, 0);
		gbc_nameTextField.gridx = 1;
		gbc_nameTextField.gridy = 0;
		configurationPanel.add(nameTextField, gbc_nameTextField);
		nameTextField.setColumns(10);
		
		//================================================================================
	    // Interface
	    //================================================================================
		
		JLabel lblInterface = new JLabel("Interface");
		lblInterface.setHorizontalAlignment(SwingConstants.CENTER);
		lblInterface.setFont(new Font("Tahoma", Font.PLAIN, 12));
		GridBagConstraints gbc_lblInterface = new GridBagConstraints();
		gbc_lblInterface.fill = GridBagConstraints.BOTH;
		gbc_lblInterface.insets = new Insets(0, 0, 5, 5);
		gbc_lblInterface.gridx = 0;
		gbc_lblInterface.gridy = 1;
		configurationPanel.add(lblInterface, gbc_lblInterface);
		
		wirelessComboBox = new JComboBox<String>();
		GridBagConstraints gbc_wirelessComboBox = new GridBagConstraints();
		gbc_wirelessComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_wirelessComboBox.gridwidth = 4;
		gbc_wirelessComboBox.insets = new Insets(0, 0, 5, 0);
		gbc_wirelessComboBox.gridx = 1;
		gbc_wirelessComboBox.gridy = 1;
		configurationPanel.add(wirelessComboBox, gbc_wirelessComboBox);
		
		//================================================================================
	    // SSID
	    //================================================================================
		
		JLabel lblSSID = new JLabel("SSID");
		lblSSID.setHorizontalAlignment(SwingConstants.CENTER);
		lblSSID.setFont(new Font("Tahoma", Font.PLAIN, 12));
		GridBagConstraints gbc_lblSSID = new GridBagConstraints();
		gbc_lblSSID.fill = GridBagConstraints.BOTH;
		gbc_lblSSID.insets = new Insets(0, 0, 5, 5);
		gbc_lblSSID.gridx = 0;
		gbc_lblSSID.gridy = 2;
		configurationPanel.add(lblSSID, gbc_lblSSID);
		
		ssidTextField = new JTextField();
		ssidTextField.setBorder(new LineBorder(new Color(171, 173, 179)));
		GridBagConstraints gbc_ssidTextField = new GridBagConstraints();
		gbc_ssidTextField.gridwidth = 4;
		gbc_ssidTextField.fill = GridBagConstraints.BOTH;
		gbc_ssidTextField.insets = new Insets(0, 0, 5, 0);
		gbc_ssidTextField.gridx = 1;
		gbc_ssidTextField.gridy = 2;
		configurationPanel.add(ssidTextField, gbc_ssidTextField);
		ssidTextField.setColumns(10);
		
		//================================================================================
	    // IP Label
	    //================================================================================
		
		JLabel lblIpAddress = new JLabel("IP Address");
		lblIpAddress.setHorizontalAlignment(SwingConstants.CENTER);
		lblIpAddress.setFont(new Font("Tahoma", Font.PLAIN, 12));
		GridBagConstraints gbc_lblIpAddress = new GridBagConstraints();
		gbc_lblIpAddress.fill = GridBagConstraints.BOTH;
		gbc_lblIpAddress.insets = new Insets(0, 0, 5, 5);
		gbc_lblIpAddress.gridx = 0;
		gbc_lblIpAddress.gridy = 3;
		configurationPanel.add(lblIpAddress, gbc_lblIpAddress);
		
		//================================================================================
	    // IP TextFields
	    //================================================================================
		
		ipFirstTextField = new JTextField();
		ipFirstTextField.setHorizontalAlignment(SwingConstants.CENTER);
		ipFirstTextField.setBorder(new LineBorder(new Color(171, 173, 179)));
		GridBagConstraints gbc_ipFirstTextField = new GridBagConstraints();
		gbc_ipFirstTextField.fill = GridBagConstraints.BOTH;
		gbc_ipFirstTextField.insets = new Insets(0, 0, 5, 5);
		gbc_ipFirstTextField.gridx = 1;
		gbc_ipFirstTextField.gridy = 3;
		configurationPanel.add(ipFirstTextField, gbc_ipFirstTextField);
		ipFirstTextField.setColumns(10);
		
		ipSecondTextField = new JTextField();
		ipSecondTextField.setHorizontalAlignment(SwingConstants.CENTER);
		ipSecondTextField.setBorder(new LineBorder(new Color(171, 173, 179)));
		GridBagConstraints gbc_ipSecondTextField = new GridBagConstraints();
		gbc_ipSecondTextField.insets = new Insets(0, 0, 5, 5);
		gbc_ipSecondTextField.fill = GridBagConstraints.BOTH;
		gbc_ipSecondTextField.gridx = 2;
		gbc_ipSecondTextField.gridy = 3;
		configurationPanel.add(ipSecondTextField, gbc_ipSecondTextField);
		ipSecondTextField.setColumns(10);
		
		ipThirdTextField = new JTextField();
		ipThirdTextField.setHorizontalAlignment(SwingConstants.CENTER);
		ipThirdTextField.setBorder(new LineBorder(new Color(171, 173, 179)));
		GridBagConstraints gbc_ipThirdTextField = new GridBagConstraints();
		gbc_ipThirdTextField.insets = new Insets(0, 0, 5, 5);
		gbc_ipThirdTextField.fill = GridBagConstraints.BOTH;
		gbc_ipThirdTextField.gridx = 3;
		gbc_ipThirdTextField.gridy = 3;
		configurationPanel.add(ipThirdTextField, gbc_ipThirdTextField);
		ipThirdTextField.setColumns(10);
		
		ipFourthTextField = new JTextField();
		ipFourthTextField.setHorizontalAlignment(SwingConstants.CENTER);
		ipFourthTextField.setBorder(new LineBorder(new Color(171, 173, 179)));
		GridBagConstraints gbc_ipFourthTextField = new GridBagConstraints();
		gbc_ipFourthTextField.insets = new Insets(0, 0, 5, 0);
		gbc_ipFourthTextField.fill = GridBagConstraints.BOTH;
		gbc_ipFourthTextField.gridx = 4;
		gbc_ipFourthTextField.gridy = 3;
		configurationPanel.add(ipFourthTextField, gbc_ipFourthTextField);
		ipFourthTextField.setColumns(10);
		
		//================================================================================
	    // Netmask label
	    //================================================================================
		
		JLabel lblNetmask = new JLabel("Netmask");
		lblNetmask.setHorizontalAlignment(SwingConstants.CENTER);
		lblNetmask.setFont(new Font("Tahoma", Font.PLAIN, 12));
		GridBagConstraints gbc_lblNetmask = new GridBagConstraints();
		gbc_lblNetmask.fill = GridBagConstraints.BOTH;
		gbc_lblNetmask.insets = new Insets(0, 0, 5, 5);
		gbc_lblNetmask.gridx = 0;
		gbc_lblNetmask.gridy = 4;
		configurationPanel.add(lblNetmask, gbc_lblNetmask);
		
		//================================================================================
	    // Netmask TextFields
	    //================================================================================
		
		nmFirstTextField = new JTextField();
		nmFirstTextField.setHorizontalAlignment(SwingConstants.CENTER);
		nmFirstTextField.setBorder(new LineBorder(new Color(171, 173, 179)));
		GridBagConstraints gbc_nmFirstTextField = new GridBagConstraints();
		gbc_nmFirstTextField.insets = new Insets(0, 0, 5, 5);
		gbc_nmFirstTextField.fill = GridBagConstraints.BOTH;
		gbc_nmFirstTextField.gridx = 1;
		gbc_nmFirstTextField.gridy = 4;
		configurationPanel.add(nmFirstTextField, gbc_nmFirstTextField);
		nmFirstTextField.setColumns(10);
		
		nmSecondTextField = new JTextField();
		nmSecondTextField.setHorizontalAlignment(SwingConstants.CENTER);
		nmSecondTextField.setBorder(new LineBorder(new Color(171, 173, 179)));
		GridBagConstraints gbc_nmSecondTextField = new GridBagConstraints();
		gbc_nmSecondTextField.insets = new Insets(0, 0, 5, 5);
		gbc_nmSecondTextField.fill = GridBagConstraints.BOTH;
		gbc_nmSecondTextField.gridx = 2;
		gbc_nmSecondTextField.gridy = 4;
		configurationPanel.add(nmSecondTextField, gbc_nmSecondTextField);
		nmSecondTextField.setColumns(10);
		
		nmThirdTextField = new JTextField();
		nmThirdTextField.setHorizontalAlignment(SwingConstants.CENTER);
		nmThirdTextField.setBorder(new LineBorder(new Color(171, 173, 179)));
		GridBagConstraints gbc_nmThirdTextField = new GridBagConstraints();
		gbc_nmThirdTextField.insets = new Insets(0, 0, 5, 5);
		gbc_nmThirdTextField.fill = GridBagConstraints.BOTH;
		gbc_nmThirdTextField.gridx = 3;
		gbc_nmThirdTextField.gridy = 4;
		configurationPanel.add(nmThirdTextField, gbc_nmThirdTextField);
		nmThirdTextField.setColumns(10);
		
		nmFourthTextField = new JTextField();
		nmFourthTextField.setHorizontalAlignment(SwingConstants.CENTER);
		nmFourthTextField.setBorder(new LineBorder(new Color(171, 173, 179)));
		GridBagConstraints gbc_nmFourthTextField = new GridBagConstraints();
		gbc_nmFourthTextField.insets = new Insets(0, 0, 5, 0);
		gbc_nmFourthTextField.fill = GridBagConstraints.BOTH;
		gbc_nmFourthTextField.gridx = 4;
		gbc_nmFourthTextField.gridy = 4;
		configurationPanel.add(nmFourthTextField, gbc_nmFourthTextField);
		nmFourthTextField.setColumns(10);
		getContentPane().setLayout(groupLayout);
	}	
	private void addListeners()
	{
		//================================================================================
	    // Dynamic Button
	    //================================================================================
		
		dynamicButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dynamicButton();
			}
		});
		
		//================================================================================
	    // Cancel Button
	    //================================================================================
		
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});	
		
		//================================================================================
	    // TextField Filter (To avoid 4+ character in the Ip and Netmask fields)
	    //================================================================================
		
		DocumentFilter filter = new DocumentFilter()
		{
			@Override
		    public void replace(DocumentFilter.FilterBypass fb, int offset,int length, String text, AttributeSet attrs) throws BadLocationException 
			{   
		        if ((fb.getDocument().getLength() + text.length()) <= 3)
					super.insertString(fb, offset, text, attrs);
		    }
		};
		
		((AbstractDocument) ipFirstTextField.getDocument()).setDocumentFilter(filter);
		((AbstractDocument) ipSecondTextField.getDocument()).setDocumentFilter(filter);
		((AbstractDocument) ipThirdTextField.getDocument()).setDocumentFilter(filter);
		((AbstractDocument) ipFourthTextField.getDocument()).setDocumentFilter(filter);
		
		((AbstractDocument) nmFirstTextField.getDocument()).setDocumentFilter(filter);
		((AbstractDocument) nmSecondTextField.getDocument()).setDocumentFilter(filter);
		((AbstractDocument) nmThirdTextField.getDocument()).setDocumentFilter(filter);
		((AbstractDocument) nmFourthTextField.getDocument()).setDocumentFilter(filter);
	}
	private void interfacesComboBox()
	{
		if(action.equals("Save"))
		{
			List<String> listInterfaces = utils.getPhysicalWirelessInterfaces();
			if(listInterfaces != null)
				for(String s : listInterfaces)
					wirelessComboBox.addItem(s);
		}
	}
	
	private void dynamicButton()
	{
		//We collect the info in the form
		String name = nameTextField.getText();
		String wrInterface = (String) wirelessComboBox.getSelectedItem();
		String ssid = ssidTextField.getText();
		
		//We form the ip with the 4 boxes
		StringBuilder sbIp = new StringBuilder(ipFirstTextField.getText());
		sbIp.append(".").append(ipSecondTextField.getText()).append(".");
		sbIp.append(ipThirdTextField.getText()).append(".").append(ipFourthTextField.getText());
		
		//We form the netmask with the 4 boxes
		StringBuilder sbNetmask = new StringBuilder(nmFirstTextField.getText());
		sbNetmask.append(".").append(nmSecondTextField.getText()).append(".");
		sbNetmask.append(nmThirdTextField.getText()).append(".").append(nmFourthTextField.getText());
		
		if(action.equals("Ok"))
		{
			if(utils.activateProfile(name,wrInterface, ssid, sbIp.toString(), sbNetmask.toString()))
				dispose();
			
			return;
		}
		
		if(action.equals("Save"))
		{
			JFileChooser file = new JFileChooser();
			StringBuilder toSavePath = new StringBuilder(System.getProperty("user.dir"));
			File toSave = new File(toSavePath.append("/").append(name).toString());
			file.setSelectedFile(toSave);
			
			int result = file.showSaveDialog(this);
			
			if(result == JFileChooser.APPROVE_OPTION)
			{	
				if(utils.saveProfile(name,wrInterface, ssid, sbIp.toString(), sbNetmask.toString(),file.getSelectedFile()))
					dispose();
			}
				
			return;
		}
		
		if(action.equals("Load"))
		{
			JFileChooser file = new JFileChooser();
			file.setCurrentDirectory(new File(System.getProperty("user.dir")));
			int result = file.showOpenDialog(this);
		
			if(result == JFileChooser.APPROVE_OPTION)
			{
				action = "Ok";
				dynamicButton.setText(action);
				try {
					HashMap<String,String> info = utils.loadProfile(file.getSelectedFile());
					nameTextField.setText(info.get("Name"));
					wirelessComboBox.addItem(info.get("Interface"));
					ssidTextField.setText(info.get("SSID"));
					
					//We load each section of the ip in his right box
					String[] txt = info.get("IP").split("\\.");
					ipFirstTextField.setText(txt[0]);
					ipSecondTextField.setText(txt[1]);
					ipThirdTextField.setText(txt[2]);
					ipFourthTextField.setText(txt[3]);
					
					//We load each section of the netmask in his right box
					txt = info.get("Netmask").split("\\.");
					nmFirstTextField.setText(txt[0]);
					nmSecondTextField.setText(txt[1]);
					nmThirdTextField.setText(txt[2]);
					nmFourthTextField.setText(txt[3]);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			return;
		}
	}
}
