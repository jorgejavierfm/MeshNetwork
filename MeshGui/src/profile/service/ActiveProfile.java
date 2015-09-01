package profile.service;

import profile.service.Profile;
import profile.service.ProfileActivatedEvent;
import profile.service.ProfileActivatedListener;

public class ActiveProfile
{
	private static ActiveProfile instance = null;
	private Profile activeProfile  = null;
	
	protected ActiveProfile(){
	}
	
	public static ActiveProfile getInstance() {
	      if(instance == null) {
	         instance = new ActiveProfile();
	      }
	      return instance;
	   }
	
	public Profile getActiveProfile(){
		return activeProfile;
	}
	
	public void createProfile(String interfaceOlsr, String ssid, String ip, String netmask)
	{
		if(activeProfile != null)
			activeProfile = null;
		
		activeProfile = new Profile(interfaceOlsr,ssid,ip,netmask);		
	}
}
