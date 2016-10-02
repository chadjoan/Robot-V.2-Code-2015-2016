package org.avalancherobotics.standalone.output;

// Implementation NOTE: The standalone package shall never depend on the
//   org.avalancherobotics.library package, the org.avalancherobotics.desktop package,
//   or any other implementors.  It should also use only Java code that will
//   be found on all platforms (ex: Android + Desktop), which means that only
//   things in java.util are remotely safe.
//

import org.avalancherobotics.standalone.device.HardwareDeviceDummy;
import org.avalancherobotics.standalone.internal.DeviceConnectivity;
import org.avalancherobotics.standalone.interfaces.IDcMotorController;
import org.avalancherobotics.standalone.output.DcMotorDummy;

import java.util.ArrayList;

/**
 *  This class persists the same state (fields, variables, etc) as the
 *  com.qualcomm.robotcore.hardware.DcMotorController interface,
 *  but does not implement any of the same functionality or connectivity.
 *  <p>
 *  Interface taken from
 *  http://ftckey.com/apis/ftc/com/qualcomm/robotcore/hardware/DcMotorController.html
 *  on 2016-02-11.
 *
 *  @see org.avalancherobotics.standalone.interfaces.IDcMotorController
 */
public class DcMotorControllerDummy extends HardwareDeviceDummy
	implements IDcMotorController, DeviceConnectivity.Hub<Integer>
{
	// -------------------------------------------------------------------------
	//                       Integration/Internals
	// -------------------------------------------------------------------------
	private /*@NonNull*/ DeviceConnectivity.HubPortsAsArrayList<DcMotorDummy> hubPorts;
	private /*@NonNull*/ ArrayList<DcMotorDummy> motors = new ArrayList<>();
	private IDcMotorController.DeviceMode deviceMode = IDcMotorController.DeviceMode.READ_WRITE; // TODO: is this the correct default?

	public DcMotorControllerDummy(/*@NonNull*/ String deviceName)
	{
		super(deviceName);
		this.hubPorts = new DeviceConnectivity.HubPortsAsArrayList(motors, DcMotorDummy.class);
	}

	/**
	 *  @return null if there is no motor connected to the given 'port'.
	 */
	public DcMotorDummy getMotor(int port)
	{
		return (DcMotorDummy)this.hubPorts.getSpoke(port);
	}

	private void enforceConnection(int motor)
	{
		hubPorts.enforceConnection(motor, "motor", "controller");
	}

	/**
	 *  Throws an exception if the controller is not set to a device mode
	 *  that allows for reading (ex: IDcMotorController.DeviceMode.READ_ONLY or
	 *  IDcMotorController.DeviceMode.READ_WRITE).
	 */
	public void enforceReadMode()
		throws IllegalStateException
	{
		if ( this.deviceMode != IDcMotorController.DeviceMode.READ_WRITE
		&&   this.deviceMode != IDcMotorController.DeviceMode.READ_ONLY )
		{
			StackTraceElement ste = Thread.currentThread().getStackTrace()[1];
			throw new IllegalStateException("Cannot read while in this mode: "+
				this.deviceMode.toString()+ " from method: "+
				ste.getClassName()+"."+ste.getMethodName());
		}
	}

	/**
	 *  Throws an exception if the controller is not set to a device mode
	 *  that allows for writing (ex: IDcMotorController.DeviceMode.WRITE_ONLY or
	 *  IDcMotorController.DeviceMode.READ_WRITE).
	 */
	public void enforceWriteMode()
		throws IllegalStateException
	{
		if ( this.deviceMode != IDcMotorController.DeviceMode.READ_WRITE
		&&   this.deviceMode != IDcMotorController.DeviceMode.WRITE_ONLY )
		{
			StackTraceElement ste = Thread.currentThread().getStackTrace()[1];
			throw new IllegalStateException("Cannot read while in this mode: "+
				this.deviceMode.toString()+ " from method: "+
				ste.getClassName()+"."+ste.getMethodName());
		}
	}

	// -------------------------------------------------------------------------
	//            Implementation : DeviceConnectivity.Hub<int>
	// -------------------------------------------------------------------------

	@Override
	public DeviceConnectivity.HubPorts<Integer> getHubPorts() { return this.hubPorts; }

	// -------------------------------------------------------------------------
	//                  Implementation : IDcMotorController
	// -------------------------------------------------------------------------

	/** Get the current channel mode. */
	@Override
	public IDcMotorController.RunMode getMotorChannelMode(int motor)
	{
		enforceConnection(motor);
		enforceReadMode();
		return this.getMotor(motor).getChannelMode();
	}

	/**
	 *  Get the current device mode (read, write, or read/write).
	 *  Note: on USB devices, this will always return "READ_WRITE" mode.
	 */
	@Override
	public IDcMotorController.DeviceMode getMotorControllerDeviceMode()
	{
		return this.deviceMode;
	}

	/** Get the current motor position */
	@Override
	public int  getMotorCurrentPosition(int motor)
	{
		enforceConnection(motor);
		enforceReadMode();
		return this.getMotor(motor).getCurrentPosition();
	}

	/** Get the current motor power */
	@Override
	public double  getMotorPower(int motor)
	{
		enforceConnection(motor);
		enforceReadMode();
		return this.getMotor(motor).getPower();
	}

	/** Is motor power set to float? */
	@Override
	public boolean  getMotorPowerFloat(int motor)
	{
		enforceConnection(motor);
		enforceReadMode();
		return this.getMotor(motor).getPowerFloat();
	}

	/** Get the current motor target position */
	@Override
	public int  getMotorTargetPosition(int motor)
	{
		enforceConnection(motor);
		enforceReadMode();
		return this.getMotor(motor).getTargetPosition();
	}

	/** Is the motor busy? */
	@Override
	public boolean  isBusy(int motor)
	{
		enforceConnection(motor);
		enforceReadMode();
		return this.getMotor(motor).isBusy();
	}

	/** Set the current channel mode. */
	@Override
	public void  setMotorChannelMode(int motor, IDcMotorController.RunMode mode)
	{
		enforceConnection(motor);
		enforceWriteMode();
		this.getMotor(motor).setChannelMode(mode);
	}

	/**
	 *  Set the device into read, write, or read/write modes.
	 *  Note: If you are using the NxtDcMotorController, you need to switch the
	 *  controller into "read" mode before doing a read, and into "write" mode
	 *  before doing a write.
	 */
	@Override
	public void  setMotorControllerDeviceMode(IDcMotorController.DeviceMode mode)
	{
		this.deviceMode = mode;
	}

	/**
	 *  Set the current motor power.
	 */
	@Override
	public void  setMotorPower(int motor, double power)
	{
		enforceConnection(motor);
		enforceWriteMode();
		this.getMotor(motor).setPower(power);
	}

	/**
	 *  Allow motor to float.
	 *  <p>
	 *  See {@link org.avalancherobotics.standalone.interfaces.IDcMotor#setPowerFloat}
	 *  for a more detailed explanation.
	 */
	@Override
	public void  setMotorPowerFloat(int motor)
	{
		enforceConnection(motor);
		enforceWriteMode();
		this.getMotor(motor).setPowerFloat();
	}

	/** Set the motor target position. */
	@Override
	public void  setMotorTargetPosition(int motor, int position)
	{
		enforceConnection(motor);
		enforceWriteMode();
		this.getMotor(motor).setTargetPosition(position);
	}
}