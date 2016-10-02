package org.avalancherobotics.standalone.device;

// Implementation NOTE: The standalone package shall never depend on the
//   org.avalancherobotics.library package, the org.avalancherobotics.desktop package,
//   or any other implementors.  It should also use only Java code that will
//   be found on all platforms (ex: Android + Desktop), which means that only
//   things in java.util are remotely safe.
//

import org.avalancherobotics.standalone.internal.UnimplementedException;

/** This would naturally be a specific subclass of a
 *  javax.lang.model.type.UnknownTypeException.
 *  However, UnknownTypeException may not be available on all platforms
 *  (ex: Android), so it is derived from RuntimeException instead.
 */
public class UnimplementedDeviceException extends UnimplementedException
{
	public UnimplementedDeviceException(String message) {
		super(message);
	}
}