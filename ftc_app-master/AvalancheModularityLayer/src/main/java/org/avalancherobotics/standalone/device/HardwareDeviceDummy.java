package org.avalancherobotics.standalone.device;

// Implementation NOTE: The standalone package shall never depend on the
//   org.avalancherobotics.library package, the org.avalancherobotics.desktop package,
//   or any other implementors.  It should also use only Java code that will
//   be found on all platforms (ex: Android + Desktop), which means that only
//   things in java.util are remotely safe.
//

import org.avalancherobotics.standalone.interfaces.IDevice;

/**
 *  This class persists the same state (fields, variables, etc) as the
 *  com.qualcomm.robotcore.hardware.HardwareDevice interface,
 *  but does not implement any of the same functionality or connectivity.
 *  <p>
 *  Interface taken from
 *  http://ftckey.com/apis/ftc/com/qualcomm/robotcore/hardware/HardwareDevice.html
 *  on 2016-02-17.
 */
public abstract class HardwareDeviceDummy implements IDevice
{
	private /*@NonNull*/ String deviceName;
	private boolean isClosed;
	private int version;

	public HardwareDeviceDummy(/*@NonNull*/ String deviceName)
	{
		this.deviceName = deviceName;
		this.isClosed = false;
		this.version = 1;
		if ( deviceName == null )
			throw new IllegalArgumentException("The deviceName parameter must be non-null.");
	}

	/**
	 *  Close this device.
	 *  <p>
	 *  This implementation will change the device's {@link #isClosed()} result
	 *  to true.
	 *  Implementations (of dummy devices) overriding this class may add more
	 *  functionality, but are encouraged to ensure that super.close() is called
	 *  whenever the derived .close() method is called (so that the private
	 *  state is tracked).  This could potentially help test logic determine
	 *  if things are getting closed prematurely somehow.
	 *  <p>
	 *  It is possible that Qualcomm intended this as some manner of
	 *  finalization routine that frees system resources (ex: file streams,
	 *  locks, and so on) allocated by whatever devices need them.  Anyone
	 *  with actual specific knowledge is welcome to make a pull request to
	 *  update this documentation.
	 *  @see #open()
	 *  @see #isClosed()
	 */
	@Override
	public void  close() { this.isClosed = true; }

	/**
	 *  Does the opposite of {@link #close()}: sets this device's {@link #isClosed()}
	 *  result to false.
	 *  <p>
	 *  This has no equivalent call in the original qualcomm/ftc API.  It is
	 *  intended to provide test code a way to reset the "isClosed" state if
	 *  necessary (eg. for further tests).
	 *  @see #close()
	 #  @see #isClosed()
	 */
	public void  open() { this.isClosed = false; }

	/**
	 *  Used to determine if {@link #close()} has been called on this object.
	 *  <p>
	 *  For dummy objects, this state can be reset by calling {@link #open()}.
	 *  @return true if {@link #close()} has been called, false otherwise.
	 *  @see #close()
	 *  @see #open()
	 */
	public boolean isClosed() { return this.isClosed; }

	// TODO: Is there a unified format for this string?
	/** Get connection information about this device in a human readable format */
	@Override
	public java.lang.String  getConnectionInfo()
	{
		return "Device "+ getDeviceName()+ " does not need a connection. "+
			"(It is a HardwareDeviceDummy instance.)";
	}

	// TODO: Is there a unified format for this string?
	/**
	 *  Device Name
	 *  @return device manufacturer and name
	 */
	@Override
	public /*@NonNull*/ String  getDeviceName() { return deviceName; }

	/**
	 *  Device Name
	 */
	public void  setDeviceName(/*@NonNull*/ String name)
	{
		if ( name == null )
			throw new IllegalArgumentException("The name parameter must be non-null.");
		this.deviceName = name;
	}

	/** Version */
	@Override
	public int  getVersion() { return this.version; }

	/** Version */
	public void setVersion(int version) { this.version = version; }
}