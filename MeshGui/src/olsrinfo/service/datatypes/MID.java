package olsrinfo.service.datatypes;

import java.util.Collection;
import java.util.Vector;

/**
 * A network interface used in MID (Multiple Interface Declaration) setups.
 * 
 */
public class MID {
	public String ipAddress;
	public Collection<MIDAlias> aliases;
}
