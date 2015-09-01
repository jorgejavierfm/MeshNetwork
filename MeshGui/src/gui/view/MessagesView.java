package gui.view;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JComboBox;

import javax.swing.JScrollPane;

import message.service.MsgEvent;
import message.service.MsgListener;
import message.service.Receiver;
import message.service.Sender;
import olsrinfo.service.JsonInfo;
import olsrinfo.service.datatypes.OlsrDataDump;
import olsrinfo.service.datatypes.Route;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

public class MessagesView extends JFrame implements MsgListener
{
	private JTextArea HystMessageTextArea;
	private JTextArea writeMsgTextArea;
	private JComboBox<String> destinationComboBox;
	private JButton btnSend;
	
	/**
	 * Create the frame.
	 */
	public MessagesView(Receiver receiver) 
	{
		receiver.addMsgListener(this);
		drawInterface();
		addListeners();
	}

	private void drawInterface()
	{
		setTitle("Messages");
		setBounds(100, 100, 428, 305);
		
		//================================================================================
	    // MessageArea Box
	    //================================================================================
		
		destinationComboBox = new JComboBox<String>();
		JLabel lblTo = new JLabel("To :");
		
		JScrollPane scrollPaneTop = new JScrollPane();
		HystMessageTextArea = new JTextArea();
		scrollPaneTop.setViewportView(HystMessageTextArea);
		HystMessageTextArea.setLineWrap(true);
		HystMessageTextArea.setBorder(null);
		HystMessageTextArea.setEditable(false);
		
		//================================================================================
	    // Send Box
	    //================================================================================
		
		JScrollPane scrollPaneBot = new JScrollPane();
		writeMsgTextArea = new JTextArea();
		writeMsgTextArea.setBorder(null);
		scrollPaneBot.setViewportView(writeMsgTextArea);
		writeMsgTextArea.setLineWrap(true);
		btnSend = new JButton("Send");
			
		//================================================================================
	    // General Layout
	    //================================================================================
		
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(12)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(scrollPaneTop, GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(lblTo, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
							.addGap(3)
							.addComponent(destinationComboBox, GroupLayout.PREFERRED_SIZE, 182, GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(scrollPaneBot, GroupLayout.DEFAULT_SIZE, 328, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnSend)))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(21)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(5)
							.addComponent(lblTo))
						.addComponent(destinationComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(8)
					.addComponent(scrollPaneTop, GroupLayout.DEFAULT_SIZE, 188, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(scrollPaneBot, GroupLayout.PREFERRED_SIZE, 43, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnSend, GroupLayout.PREFERRED_SIZE, 43, GroupLayout.PREFERRED_SIZE))
					.addGap(9))
		);
		getContentPane().setLayout(groupLayout);
	}	
	private void addListeners()
	{
		//================================================================================
	    // Destination ComboBox
	    //================================================================================
		
		destinationComboBox.addFocusListener(new FocusAdapter() 
		{
			@Override
			public void focusGained(FocusEvent e) {
				destinationComboBox.removeAllItems();
				JsonInfo jsoninfo = new JsonInfo();
				OlsrDataDump dump = jsoninfo.all();
				
				for(Route r : dump.routes)
					destinationComboBox.addItem(r.destination);
			}
		});
		
		//================================================================================
	    // Send Action
	    //================================================================================
		
		btnSend.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) {
				Sender sender = new Sender(destinationComboBox.getSelectedItem().toString(),7077);
				sender.sendMessage(writeMsgTextArea.getText());
				
				StringBuilder contentTextArea = new StringBuilder(HystMessageTextArea.getText());
				contentTextArea.append("Message to " + destinationComboBox.getSelectedItem().toString() + ": " + writeMsgTextArea.getText()+"\n");
				HystMessageTextArea.setText(contentTextArea.toString());
				
				writeMsgTextArea.setText("");
			}
		});
	}
	
	@Override
	public void msgReceived(MsgEvent event) 
	{
		String msg = event.msg();
		String from = msg.substring(0,msg.indexOf("\n"));
		String content = msg.substring(msg.indexOf("\n")+1);
		
		StringBuilder contentTextArea = new StringBuilder(HystMessageTextArea.getText());
		contentTextArea.append("Message from " + from + ": " + content);
		
		HystMessageTextArea.setText(contentTextArea.toString());
	}
}
