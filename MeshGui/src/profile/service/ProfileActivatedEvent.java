package profile.service;

import java.util.EventObject;

public class ProfileActivatedEvent extends EventObject 
{
	private Profile profile;
	
	public ProfileActivatedEvent(Object source,Profile _profile)
	{
		super(source);
		profile = _profile;
	}
	
	public Profile getProfile()
	{
		return profile;
	}
}