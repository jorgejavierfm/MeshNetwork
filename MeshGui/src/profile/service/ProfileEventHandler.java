package profile.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class ProfileEventHandler 
{
	private List<ProfileActivatedListener> listeners = new ArrayList<ProfileActivatedListener>();
	
	public void createProfile(String interfaceOlsr, String ssid, String ip, String netmask)
	{
		Profile profile = new Profile(interfaceOlsr,ssid,ip,netmask);
		fireProfileActivatedEvent(profile);
	}
	
	public synchronized void addProfileActivatedListener(ProfileActivatedListener pal)
	{
		listeners.add(pal);
	}
	
	public synchronized void removeProfileActivatedLister(ProfileActivatedListener pal)
	{
		listeners.remove(pal);
	}
	
	private synchronized void fireProfileActivatedEvent(Profile profile)
	{
		ProfileActivatedEvent event = new ProfileActivatedEvent(this,profile);
		Iterator<ProfileActivatedListener> it = listeners.iterator();
		while(it.hasNext())
			it.next().profileActivated(event);
	}
}
