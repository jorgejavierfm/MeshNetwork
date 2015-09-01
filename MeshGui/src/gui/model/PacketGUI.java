package gui.model;

import javax.swing.JTextArea;

import sniffer.service.PacketEvent;
import sniffer.service.PacketListener;

public class PacketGUI implements PacketListener
{
	JTextArea toUpdateElement;
	
	public PacketGUI(JTextArea toUpdate) {
		toUpdateElement = toUpdate;
	}

	@Override
	public void pckReceived(PacketEvent packet) {
		StringBuilder tmp =  new StringBuilder(toUpdateElement.getText());
		if(tmp.length() >= 10000)
			tmp.setLength(0);
		tmp.append(packet.getPack()).append("\n");
		toUpdateElement.setText(tmp.toString());
	}

}
