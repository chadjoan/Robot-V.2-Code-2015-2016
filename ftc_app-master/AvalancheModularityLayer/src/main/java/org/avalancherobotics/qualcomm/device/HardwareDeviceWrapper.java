package org.avalancherobotics.standalone.library.device;

import org.avalancherobotics.standalone.interfaces.IDevice;

import com.qualcomm.robotcore.hardware.HardwareDevice;

/** */
public abstract class HardwareDeviceWrapper implements IDevice
{
	/**
	 *  Classes extending this class must override this method and make
	 *  it return the HardwareDevice object wrapped by the more specific class.
	 */
	protected abstract /*@NonNull*/ HardwareDevice getWrappedDevice();

	/**
	 *  @see package org.avalancherobotics.standalone.interfaces.IDevice#close
	 */
	public void  close() { getWrappedDevice().close(); }

	/**
	 *  @see package org.avalancherobotics.standalone.interfaces.IDevice#getConnectionInfo
	 */
	public java.lang.String  getConnectionInfo() { return getWrappedDevice().getConnectionInfo(); }

	/**
	 *  @see package org.avalancherobotics.standalone.interfaces.IDevice#getDeviceName
	 */
	public java.lang.String  getDeviceName() { return getWrappedDevice().getDeviceName(); }

	/**
	 *  @see package org.avalancherobotics.standalone.interfaces.IDevice#getVersion
	 */
	public int  getVersion() { return getWrappedDevice().getVersion(); }
}
