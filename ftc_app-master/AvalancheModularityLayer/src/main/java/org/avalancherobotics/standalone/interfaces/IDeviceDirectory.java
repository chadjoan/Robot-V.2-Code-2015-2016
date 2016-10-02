package org.avalancherobotics.standalone.interfaces;

// Implementation NOTE: The standalone package shall never depend on the
//   org.avalancherobotics.library package, the org.avalancherobotics.desktop package,
//   or any other implementors.  It should also use only Java code that will
//   be found on all platforms (ex: Android + Desktop), which means that only
//   things in java.util are remotely safe.
//

import java.util.Set;
import java.util.Map;
import java.util.Iterator;

/**
 *  This is similar to the com.qualcomm.robotcore.hardware.HardwareMap.DeviceMapping
 *  class in the Qualcomm API.
 */
public interface IDeviceDirectory<DEVICE_TYPE extends IDevice> extends java.lang.Iterable<DEVICE_TYPE>
{
	/** */
	public Set<Map.Entry<String,DEVICE_TYPE>> entrySet();

	/** */
	public DEVICE_TYPE get(String deviceName);

	/** */
	public Iterator<DEVICE_TYPE> iterator();

	/** */
	public void put(DEVICE_TYPE device);

	/** */
	public int size();

	/** */
	public /*@NonNull*/ Class<DEVICE_TYPE> getElementClass();
}
