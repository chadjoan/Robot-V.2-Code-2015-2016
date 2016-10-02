package org.avalancherobotics.standalone.interfaces;

// Implementation NOTE: The standalone package shall never depend on the
//   org.avalancherobotics.library package, the org.avalancherobotics.desktop package,
//   or any other implementors.  It should also use only Java code that will
//   be found on all platforms (ex: Android + Desktop), which means that only
//   things in java.util are remotely safe.
//

import org.avalancherobotics.standalone.interfaces.IDeviceDirectory;

import java.util.Set;

/**
 *  This is similar to the com.qualcomm.robotcore.hardware.HardwareMap class
 *  in Qualcomm's FTC API.
 *  <p>
 *  This interface allows IDeviceDirectory objects to be added or removed,
 *  which allows external code to enlarge the registry's contents.
 *  This also allows it to be used as a basis for other implementations or
 *  used directly to mock up testing scenarios.
 *  <p>
 *  In the future, this could support more comprehensive operations.
 *  For now, this interface is overly simplistic due to time constraints
 *  and lacks possibly important features like iteration capabilities.
 *  Note that this may never implement the java.util.Map interface,
 *  or similar interfaces like java.util.NavigableMap, because this uses
 *  compile-time checkable generics as keys instead of runtime objects.  It
 *  could still potentially mirror those capabilities, and possible implement
 *  those as a less-safe dynamic alternative.
 *  <p>
 *  Note: The name was changed to avoid ambiguities introduced by the term
 *        "map", which can mean several different things in a Java or
 *        programming context.  In Java's java.util package, a Map interface
 *        describes a set of unique keys and their relationships to values.
 *        In many programming languages, 'map' is the name of a higher-order
 *        function that applies a given function to each element of a list,
 *        returning a list of results in the same order [1].  In this case,
 *        the intent of the "HardwareMap" object seemed to be the creation
 *        of a central object that exposes a comprehensive list of connected
 *        devices, which sounds more like a registry (in other words: a
 *        collection that devices can be registered with).
 *  <p>
 *  [1] Wikipedia: https://en.wikipedia.org/wiki/Map_%28higher-order_function%29
 */
public interface IDeviceRegistry
{
	/**
	 *  Returns a Set view of the device types (as class name strings)
	 *  registered in this registry.
	 *  The set is read-only.
	 */
	public Set<String> keySet();

	/**
	 *  Removes all of the directories from this registry.
	 *  The registry will be empty after this call returns.
	 */
	public void clear();

	/**
	 *  Places the specified directory into the registry, as identified by the given DEVICE_TYPE.
	 *  If the registry previously contained a directory associated with the
	 *  given DEVICE_TYPE, then the old directory is replaced.
	 *  @return the previous directory associated with DEVICE_TYPE, or null if
	 *          there was no directory associated with DEVICE_TYPE.
	 */
	public <DEVICE_TYPE extends IDevice>
		IDeviceDirectory<DEVICE_TYPE>
			put(
				/*@NonNull*/ IDeviceDirectory<DEVICE_TYPE> dir );

	/**
	 *  Removes the directory associated with the specified DEVICE_TYPE from the registry, if present.
	 *  @return the previous directory associated with DEVICE_TYPE, or null if
	 *          there was no directory associated with DEVICE_TYPE.
	 */
	public <DEVICE_TYPE extends IDevice>
		IDeviceDirectory<DEVICE_TYPE>
			remove(/*@NonNull*/ Class<DEVICE_TYPE> classInfo);

	/**
	 *  Returns the directory associated with the specified DEVICE_TYPE,
	 *  or null if this registry contains no directory for the DEVICE_TYPE.
	 */
	public <DEVICE_TYPE extends IDevice>
		IDeviceDirectory<DEVICE_TYPE>
			get(/*@NonNull*/ Class<DEVICE_TYPE> classInfo);
}
