package org.avalancherobotics.standalone.device;

// Implementation NOTE: The standalone package shall never depend on the
//   org.avalancherobotics.library package, the org.avalancherobotics.desktop package,
//   or any other implementors.  It should also use only Java code that will
//   be found on all platforms (ex: Android + Desktop), which means that only
//   things in java.util are remotely safe.
//

import org.avalancherobotics.standalone.interfaces.IDevice;
import org.avalancherobotics.standalone.interfaces.IDeviceDirectory;
import org.avalancherobotics.standalone.interfaces.IDeviceRegistry;

import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

/**
 *  A basic implementation of the
 *  {@link org.avalancherobotics.standalone.interfaces.IDeviceRegistry} interface.
 *
 *  @see org.avalancherobotics.standalone.interfaces.IDeviceRegistry
 */
public class DeviceRegistry implements IDeviceRegistry
{

	// TODO: Maybe this should be a TreeMap or something?  Does it even need optimizing?
	private HashMap<String, /*@NonNull*/ Object> backing = new HashMap<>();

	/**
	 *  Returns a Set view of the device types (as class name strings)
	 *  registered in this registry.
	 *  The set is read-only.
	 */
	@Override
	public Set<String> keySet()
	{
		return Collections.unmodifiableSet(backing.keySet());
	}

	/**
	 *  Removes all of the directories from this registry.
	 *  The registry will be empty after this call returns.
	 */
	@Override
	public void clear()
	{
		backing.clear();
	}

	/**
	 *  Places the specified directory into the registry, as identified by the given DEVICE_TYPE.
	 *  If the registry previously contained a directory associated with the
	 *  given DEVICE_TYPE, then the old directory is replaced.
	 *  @return the previous directory associated with DEVICE_TYPE, or null if
	 *          there was no directory associated with DEVICE_TYPE.
	 */
	@Override
	public <DEVICE_TYPE extends IDevice>
		IDeviceDirectory<DEVICE_TYPE>
			put(
				/*@NonNull*/ IDeviceDirectory<DEVICE_TYPE> dir )
	{
		if ( dir == null )
			throw new IllegalArgumentException("The dir parameter must be non-null.");
		return (IDeviceDirectory<DEVICE_TYPE>)backing.put(dir.getElementClass().getName(), dir);
	}

	/**
	 *  Removes the directory associated with the specified DEVICE_TYPE from the registry, if present.
	 *  @return the previous directory associated with DEVICE_TYPE, or null if
	 *          there was no directory associated with DEVICE_TYPE.
	 */
	@Override
	public <DEVICE_TYPE extends IDevice>
		IDeviceDirectory<DEVICE_TYPE> remove(/*@NonNull*/ Class<DEVICE_TYPE> classInfo)
	{
		return (IDeviceDirectory<DEVICE_TYPE>)backing.remove(classInfo.getName());
	}

	/**
	 *  Returns the directory associated with the specified DEVICE_TYPE,
	 *  or null if this registry contains no directory for the DEVICE_TYPE.
	 */
	@Override
	public <DEVICE_TYPE extends IDevice>
		IDeviceDirectory<DEVICE_TYPE> get(/*@NonNull*/ Class<DEVICE_TYPE> classInfo)
	{
		return (IDeviceDirectory<DEVICE_TYPE>)backing.get(classInfo.getName());
	}
}