package org.avalancherobotics.standalone.device;

// Implementation NOTE: The standalone package shall never depend on the
//   org.avalancherobotics.library package, the org.avalancherobotics.desktop package,
//   or any other implementors.  It should also use only Java code that will
//   be found on all platforms (ex: Android + Desktop), which means that only
//   things in java.util are remotely safe.
//

import org.avalancherobotics.standalone.interfaces.IDevice;
import org.avalancherobotics.standalone.interfaces.IDeviceDirectory;

import java.util.HashMap;
import java.util.Set;
import java.util.Map;
import java.util.Iterator;

/**
 *  An implementation of the
 *  {@link org.avalancherobotics.standalone.interfaces.IDeviceDirectory} interface.
 */
public class DeviceDirectory<DEVICE_TYPE extends IDevice>
	implements IDeviceDirectory<DEVICE_TYPE>
{
	private /*@NonNull*/ Class<DEVICE_TYPE> elementClass;

	// TODO: Maybe this should be a TreeMap or something?  Does it even need optimizing?
	private HashMap<String, /*@NonNull*/ DEVICE_TYPE> backing = new HashMap<>();

	public DeviceDirectory(Class<DEVICE_TYPE> deviceClass)
	{
		this.elementClass = deviceClass;
		if ( deviceClass == null )
			throw new IllegalArgumentException("The deviceClass parameter must be non-null.");
	}

	/**
	 *  Warning: this doesn't call close() on any of the contained devices,
	 *  and thus might have unintended consequences.
	 */
	protected void clear() { backing.clear(); }

	/** */
	@Override
	public Set<Map.Entry<String,DEVICE_TYPE>> entrySet() { return backing.entrySet(); }

	/** */
	@Override
	public DEVICE_TYPE get(String deviceName) { return backing.get(deviceName); }

	/** */
	@Override
	public Iterator<DEVICE_TYPE> iterator() { return backing.values().iterator(); }

	/** */
	@Override
	public void put(DEVICE_TYPE device) { backing.put(device.getDeviceName(), device); }

	/** */
	@Override
	public int size() { return backing.size(); }

	/** */
	@Override
	public /*@NonNull*/ Class<DEVICE_TYPE> getElementClass() { return this.elementClass; }
}
