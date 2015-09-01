package gui.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.net.util.SubnetUtils;
import org.apache.commons.net.util.SubnetUtils.SubnetInfo;
import org.freedesktop.dbus.exceptions.DBusException;

import connection.service.Utils;
import profile.service.ActiveProfile;

public class ProfileUtils 
{
	private boolean checkValidInfo(String name,String wireless, String ssid, String ip, String netmask)
	{
		if(name.equals("") || ssid.equals(""))
			return false;
		if(wireless == null || wireless == "")
			return false;
		
		try{
			//This constructor verify the ip and the netmask, if one of them are invalid an exception is thrown
			SubnetInfo netInfo = new SubnetUtils(ip,netmask).getInfo();
			if(!netInfo.isInRange(ip))
				return false;
		}
		catch(IllegalArgumentException e){
			System.out.println(e.getMessage());
			return false;
		}
		
		return true;
	}
	public List<String> getPhysicalWirelessInterfaces()
	{
		List<String> listIface = null;
		
		try {
			listIface = Utils.getPhysicalWirelessInterfaces();
		} catch (DBusException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return listIface;
	}
	public boolean activateProfile(String name,String wirelessInterface, String ssid, String ip, String netmask)
	{
		if(checkValidInfo(name,wirelessInterface,ssid,ip,netmask))
		{
			ActiveProfile.getInstance().createProfile(wirelessInterface, ssid, ip, netmask);
			return true;
		}
		
		return false;
	}	
	public boolean saveProfile(String name,String wirelessInterface, String ssid, String ip, String netmask,File toSave)
	{
		if(checkValidInfo(name, wirelessInterface, ssid, ip, netmask))
		{
			try
			{
				HashMap<String,String> info = new HashMap<String,String>();
				info.put("Name",name);
				info.put("Interface",wirelessInterface);
				info.put("SSID",ssid);
				info.put("IP",ip);
				info.put("Netmask",netmask);
					
				if (!toSave.exists())
					toSave.createNewFile();

				toSave.setWritable(true);
				
				FileWriter fw = new FileWriter(toSave.getAbsoluteFile());
				BufferedWriter bw = new BufferedWriter(fw);
				
				Iterator<Entry<String, String>> it= info.entrySet().iterator();
						
				while(it.hasNext())
				{
					Map.Entry<String,String> pairs = (Entry<String, String>)it.next();
			        bw.write(pairs.getValue());
			        bw.newLine();
			        it.remove();  // avoids a ConcurrentModificationException
				}
				
				toSave.setReadOnly();
				bw.close();
			} catch (IOException exc) {
				exc.printStackTrace();
			}
			
			return true;
		}
		
		return false;
	}
	public HashMap<String,String> loadProfile(File file) throws IOException
	{
		HashMap<String,String> info = new HashMap<String,String>();
		
		info.put("Name",file.getName());
		info.put("Interface","");
		info.put("SSID","");
		info.put("IP","");
		info.put("Netmask","");
		
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		
		Iterator<String> it= info.keySet().iterator();
		
		while(it.hasNext())
		{
			String key = (String) it.next();
			String line = br.readLine();
			if(line != null)
				info.replace(key,"",line);
		}
		
		br.close();
		return info;
	}
}
