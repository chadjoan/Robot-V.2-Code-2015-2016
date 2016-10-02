package org.avalancherobotics.standalone.interfaces;

// Implementation NOTE: The standalone package shall never depend on the
//   org.avalancherobotics.library package, the org.avalancherobotics.desktop package,
//   or any other implementors.  It should also use only Java code that will
//   be found on all platforms (ex: Android + Desktop), which means that only
//   things in java.util are remotely safe.
//

/**
 *  This interface exposes the same set of features as the
 *  com.qualcomm.robotcore.hardware.HardwareDevice interface,
 *  but is not bundled with classes that have hard dependencies on Android.
 *  <p>
 *  Interface taken from
 *  http://ftckey.com/apis/ftc/com/qualcomm/robotcore/hardware/HardwareDevice.html
 *  on 2016-02-17.
 */
public interface IDevice
{
	/**
	 *  Close this device.
	 *  <p>
	 *  Note that there are many devices for which this will do nothing.
	 *  <p>
	 *  It is possible that Qualcomm intended this as some manner of
	 *  finalization routine that frees system resources (ex: file streams,
	 *  locks, and so on) allocated by whatever devices need them.  Anyone
	 *  with actual specific knowledge is welcome to make a pull request to
	 *  update this documentation.
	 */
	public void  close();

	// TODO: Is there a unified format for this string?
	/** Get connection information about this device in a human readable format */
	public java.lang.String  getConnectionInfo();

	// TODO: Is there a unified format for this string?
	/**
	 *  Device Name
	 *  @return device manufacturer and name
	 */
	public java.lang.String  getDeviceName();

	/** Version */
	public int  getVersion();
}
