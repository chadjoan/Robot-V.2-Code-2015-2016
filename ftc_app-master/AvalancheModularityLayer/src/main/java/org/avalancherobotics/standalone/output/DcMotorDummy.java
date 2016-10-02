package org.avalancherobotics.standalone.output;

// Implementation NOTE: The standalone package shall never depend on the
//   org.avalancherobotics.library package, the org.avalancherobotics.desktop package,
//   or any other implementors.  It should also use only Java code that will
//   be found on all platforms (ex: Android + Desktop), which means that only
//   things in java.util are remotely safe.
//

import org.avalancherobotics.standalone.internal.DeviceConnectivity;
import org.avalancherobotics.standalone.interfaces.IDcMotor;
import org.avalancherobotics.standalone.interfaces.IDcMotorController;
import org.avalancherobotics.standalone.device.HardwareDeviceDummy;

/**
 *  This class persists the same state (fields, variables, etc) as the
 *  com.qualcomm.robotcore.hardware.DcMotor interface,
 *  but does not implement any of the same functionality or connectivity.
 *  <p>
 *  Interface taken from
 *  http://ftckey.com/apis/ftc/com/qualcomm/robotcore/hardware/DcMotor.html
 *  on 2016-02-11.
 *
 *  @see org.avalancherobotics.standalone.interfaces.IDcMotor
 */
public class DcMotorDummy extends HardwareDeviceDummy
	implements IDcMotor, DeviceConnectivity.Spoke<Integer>
{
	// -------------------------------------------------------------------------
	//                       Integration/Internals
	// -------------------------------------------------------------------------
	private SpokeImpl                     spokePort;
	private DcMotorControllerDummy        controller  = null;
	private int                           portNumber  = -1;
	private IDcMotor.Direction            direction   = IDcMotor.Direction.FORWARD;
	private IDcMotorController.RunMode    channelMode = IDcMotorController.RunMode.RESET_ENCODERS; // TODO: is this the correct default?
	private double                        power       = 0.0;
	private boolean                       isFloating  = false;
	private int                           position    = 0;
	private int                           targetPos   = 0;


	/**
	 *  Constructs a DcMotorDummy with the given 'deviceName' and 'controller'.
	 *  <p>
	 *  Passing a null value for the 'controller' parameter will indicate
	 *  that the DcMotorDummy is to begin its existence in a disconnected state.
	 */
	public DcMotorDummy(String deviceName, DcMotorControllerDummy controller, int motorNumber)
	{
		super(deviceName);
		this.controller = controller;
		this.portNumber = motorNumber; // TODO: this is an assumption.  Someone please check it.
		this.spokePort = new SpokeImpl(this);

		// Do this step of construction as late as possible.  The intent is to
		// ensure that any new connection is mutual (symmetrical), but we must
		// also avoid calling connect() with a partially uninitialized object.
		if ( controller != null )
		{
			try {
				DeviceConnectivity.<Integer>connect(controller, this, motorNumber);
			}
			catch ( Exception e )
			{
				this.controller = null;
				this.portNumber = -1;
				throw e;
			}
		}
	}

	// -------------------------------------------------------------------------
	//                    Platform Specific Helpers (go here)
	// -------------------------------------------------------------------------

	/**
	 *  Convenience function that allows testing code to retrieve the controller
	 *  name.
	 *  If the controller is unassigned, this will return a distinctive
	 *  string of text that indicates this status.
	 *  <p>
	 *  This is intended to be called from test harnesses only, and not from
	 *  the logic that uses the device.  It has no equivalent in the device
	 *  hierarchies that it mimics, and will thus not port between OpMode
	 *  implementations.
	 */
	public String probeControllerName()
	{
		if ( this.controller == null )
			return "<controller is null>";
		else
			return controller.getDeviceName();
	}

	/**
	 *  Returns the controller's device mode, or
	 *  {@link #org.avalancherobotics.standalone.interfaces.IDcMotorController.DeviceMode.READ_WRITE}
	 *  if there is no controller assigned ({@link #getController()} returns null).
	 *  <p>
	 *  This is intended to be called from test harnesses only, and not from
	 *  the logic that uses the device.  It has no equivalent in the device
	 *  hierarchies that it mimics, and will thus not port between OpMode
	 *  implementations.
	 */
	public IDcMotorController.DeviceMode probeMotorControllerDeviceMode()
	{
		if ( this.controller == null )
			return IDcMotorController.DeviceMode.READ_WRITE;
		else
			return this.controller.getMotorControllerDeviceMode();
	}

	/**
	 *  Get the direction, regardless of the controller's device mode.
	 *  <p>
	 *  This is intended to be called from test harnesses only, and not from
	 *  the logic that uses the device.  It has no equivalent in the device
	 *  hierarchies that it mimics, and will thus not port between OpMode
	 *  implementations.
	 */
	public IDcMotor.Direction probeDirection() { return this.direction; }

	/**
	 *  Get the current channel mode, regardless of the controller's device mode.
	 *  <p>
	 *  This is intended to be called from test harnesses only, and not from
	 *  the logic that uses the device.  It has no equivalent in the device
	 *  hierarchies that it mimics, and will thus not port between OpMode
	 *  implementations.
	 */
	public IDcMotorController.RunMode probeChannelMode() { return this.channelMode; }

	/**
	 *  Get the current motor power, regardless of the controller's device mode.
	 *  <p>
	 *  This is intended to be called from test harnesses only, and not from
	 *  the logic that uses the device.  It has no equivalent in the device
	 *  hierarchies that it mimics, and will thus not port between OpMode
	 *  implementations.
	 */
	public double probePower() { return this.power; }

	/**
	 *  Get the motor's floating status, regardless of the controller's device mode.
	 *  <p>
	 *  This is intended to be called from test harnesses only, and not from
	 *  the logic that uses the device.  It has no equivalent in the device
	 *  hierarchies that it mimics, and will thus not port between OpMode
	 *  implementations.
	 *
	 *  @return true if the motor is floating, false otherwise.
	 *  @see #getPowerFloat()
	 */
	public boolean probePowerFloating() { return this.isFloating; }

	/**
	 *  Get the current encoder value, regardless of the controller's device mode.
	 *  <p>
	 *  This is intended to be called from test harnesses only, and not from
	 *  the logic that uses the device.  It has no equivalent in the device
	 *  hierarchies that it mimics, and will thus not port between OpMode
	 *  implementations.
	 */
	public int probeCurrentPosition() { return this.position; }

	/**
	 *  Get the current motor target position, regardless of the controller's device mode.
	 *  <p>
	 *  This is intended to be called from test harnesses only, and not from
	 *  the logic that uses the device.  It has no equivalent in the device
	 *  hierarchies that it mimics, and will thus not port between OpMode
	 *  implementations.
	 */
	public int probeTargetPosition() { return this.targetPos; }

	// TODO
	//public void advanceTime(double timeDelta) { ... }

	// -------------------------------------------------------------------------
	//            Implementation : DeviceConnectivity.Spoke<Integer>
	// -------------------------------------------------------------------------

	private class SpokeImpl implements DeviceConnectivity.SpokePort<Integer>
	{
		private /*@NonNull*/ DcMotorDummy motor;
		public SpokeImpl(DcMotorDummy motor) { this.motor = motor; }

		public DeviceConnectivity.Hub  getHub()        { return motor.controller; }
		public Integer                 getIndexOnHub() { return motor.portNumber; }
		public void  setHub(DeviceConnectivity.Hub<Integer> hub, Integer index)
		{
			this.motor.controller = (DcMotorControllerDummy)hub;
			this.motor.portNumber = index;
		}
	}

	@Override
	public DeviceConnectivity.SpokePort<Integer> getSpokePort() { return this.spokePort; }

	// -------------------------------------------------------------------------
	//                    Implementation : IDcMotor
	// -------------------------------------------------------------------------

	/**
	 *  Throws an exception if the controller is not set to a device mode
	 *  that allows for reading (ex: IDcMotorController.DeviceMode.READ_ONLY or
	 *  IDcMotorController.DeviceMode.READ_WRITE).
	 */
	public void enforceReadMode()
	{
		if ( this.controller != null )
			this.controller.enforceReadMode();
	}

	/**
	 *  Throws an exception if the controller is not set to a device mode
	 *  that allows for writing (ex: IDcMotorController.DeviceMode.WRITE_ONLY or
	 *  IDcMotorController.DeviceMode.READ_WRITE).
	 */
	public void enforceWriteMode()
	{
		if ( this.controller != null )
			this.controller.enforceWriteMode();
	}

	/** Get the current channel mode */
	@Override
	public IDcMotorController.RunMode getChannelMode()
	{
		enforceReadMode();
		return this.channelMode;
	}

	/** Get DC motor controller */
	@Override
	public IDcMotorController getController() { return this.controller; }

	/** Get the current encoder value */
	@Override
	public int getCurrentPosition()
	{
		enforceReadMode();
		return this.position;
	}

	/** Get the direction */
	@Override
	public IDcMotor.Direction getDirection()
	{
		enforceReadMode();
		return this.direction;
	}

	/** Get port number */
	@Override
	public int getPortNumber() { return this.portNumber; }

	/** Get the current motor power */
	@Override
	public double getPower()
	{
		enforceReadMode();
		return this.power;
	}

	/**
	 *  Is motor power set to float?
	 *  <p>
	 *  From <a href="http://ftcforum.usfirst.org/showthread.php?4459-SetPowerFloat-Questions">
	 *    hexafraction's forum post</a>:<br>
	 *  Motor float mode will disconnect both motor terminals from power,
	 *  without shorting them to each other, at the motor controller.
	 *  The motor should then free-wheel when enough force is applied.
	 *  (the other mode, when set to just 0, will cause the motor to actively
	 *  brake by shorting the motor terminals to each other).
	 *  <p>
	 *  Editor's note: The "other mode" mentioned is probably the mode entered
	 *  when {@link #setPower} is called.
	 */
	@Override
	public boolean getPowerFloat()
	{
		enforceReadMode();
		return this.isFloating;
	}

	/** Get the current motor target position */
	@Override
	public int getTargetPosition()
	{
		enforceReadMode();
		return this.targetPos;
	}

	/**
	 *  Is the motor busy?
	 *  <p>
	 *  This implementation returns false if (-0.01 < power < 0.01).
	 */
	@Override
	public boolean isBusy()
	{
		enforceReadMode();
		return !( -0.01 < this.power && this.power < 0.01 );
	}

	/** Set the current channel mode */
	@Override
	public void setChannelMode(IDcMotorController.RunMode mode)
	{
		enforceWriteMode();
		this.channelMode = mode;
		if ( mode == IDcMotorController.RunMode.RESET_ENCODERS )
			this.power = 0.0;
	}

	/** Set the direction */
	@Override
	public void setDirection(IDcMotor.Direction direction)
	{
		enforceWriteMode();
		this.direction = direction;
	}

	/** Set the current motor power */
	@Override
	public void setPower(double power)
	{
		enforceWriteMode();

		// TODO: Is this order-of-operations correct?
		this.isFloating = false;

		if ( this.channelMode == IDcMotorController.RunMode.RESET_ENCODERS )
			return;

		this.power = power;
	}

	/**
	 *  Allow motor to float.
	 *  <p>
	 *  This implementation just sets the power to 0 and puts the dummy in
	 *  "floating" mode ({@link #getPowerFloat} will return true).
	 *  Because the dummy motor isn't actually physically connected to anything,
	 *  there is no way to emulate the change in behavior between this and
	 *  calling .setPower(0).
	 *  If future versions of this class do some amount of simulating, then
	 *  this might obtain distinctive behavior.
	 *
	 *  @see org.avalancherobotics.standalone.interfaces.IDcMotor#setPowerFloat
	 */
	@Override
	public void setPowerFloat()
	{
		enforceWriteMode();
		this.power = 0.0;
		this.isFloating = true;
	}

	/** Set the motor target position, using an integer. */
	@Override
	public void setTargetPosition(int position)
	{
		enforceWriteMode();

		this.targetPos = position;

		// These days, motors are REALLY FAST.
		// TODO: It'd be nice to at least make a cheesey attempt at realistic motion.
		this.position = position;
	}

}