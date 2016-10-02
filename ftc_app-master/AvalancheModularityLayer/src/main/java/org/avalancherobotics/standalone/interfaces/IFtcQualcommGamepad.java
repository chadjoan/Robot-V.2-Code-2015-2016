package org.avalancherobotics.standalone.interfaces;

// Implementation NOTE: The standalone package shall never depend on the
//   org.avalancherobotics.library package, the org.avalancherobotics.desktop package,
//   or any other implementors.  It should also use only Java code that will
//   be found on all platforms (ex: Android + Desktop), which means that only
//   things in java.util are remotely safe.
//

/**
 *  Basic interface to allow FTC Qualcomm Gamepad interaction to be mocked
 *  on other platforms.
 *  <p>
 *  Right now, this is just an IInputDevice.  It is useful to give it its own
 *  interface so that it can have its own directory in a IDeviceRegistry.
 */
interface IFtcQualcommGamepad extends IInputDevice {}