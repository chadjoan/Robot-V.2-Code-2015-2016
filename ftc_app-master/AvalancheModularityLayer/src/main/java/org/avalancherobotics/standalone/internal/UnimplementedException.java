package org.avalancherobotics.standalone.internal;

// Implementation NOTE: The standalone package shall never depend on the
//   org.avalancherobotics.library package, the org.avalancherobotics.desktop package,
//   or any other implementors.  It should also use only Java code that will
//   be found on all platforms (ex: Android + Desktop), which means that only
//   things in java.util are remotely safe.
//

import java.lang.RuntimeException;

/** This is thrown whenever code encounters a branch that would require it
 *  to have implemented functionality that it does not actually implement.
 *  @see org.avalancherobotics.standalone.internal.UnimplementedPossibilityException
 *  @see org.avalancherobotics.standalone.device.UnimplementedDeviceException
 */
public class UnimplementedException extends RuntimeException
{
	public UnimplementedException(String message) {
		super(message);
	}
}