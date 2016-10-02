package org.avalancherobotics.standalone.interfaces;

// Implementation NOTE: The standalone package shall never depend on the
//   org.avalancherobotics.library package, the org.avalancherobotics.desktop package,
//   or any other implementors.  It should also use only Java code that will
//   be found on all platforms (ex: Android + Desktop), which means that only
//   things in java.util are remotely safe.
//

import org.avalancherobotics.standalone.interfaces.IDevice;

/**
 *  This interface exposes the same set of features as the
 *  com.qualcomm.robotcore.hardware.DcMotorController interface,
 *  but is not bundled with classes that have hard dependencies on Android.
 *  <p>
 *  Interface taken from
 *  http://ftckey.com/apis/ftc/com/qualcomm/robotcore/hardware/DcMotorController.html
 *  on 2016-02-11.
 */
public interface IDcMotorController extends IDevice
{
	/** */
	public enum DeviceMode
	{
		READ_ONLY,
		READ_WRITE,
		SWITCHING_TO_READ_MODE,
		SWITCHING_TO_WRITE_MODE,
		WRITE_ONLY
	}

	/**
	 *  RunMode documentation was derived from
	 *  <a href="http://ftcforum.usfirst.org/showthread.php?5369-DC-motor-controller-run-mode-definitions">
	 *  this helpful post</a>.
	 */
	public enum RunMode
	{
		/**
		 *  The robot will NOT move in this mode.
		 *  It's used to reset the encoder values back to zero.
		 */
		RESET_ENCODERS,

		/**
		 *  This is closed-loop position control.
		 *  <p>
		 *  Encoders are required for this mode. setPower() is simply saying
		 *  what the top speed is to reach the desired encoder position.
		 *  <p>
		 *  To control the movement of the robot you actually set the target
		 *  encoder position for each motor. The motor controller will provide
		 *  power to the motors in order to get them to reach the requested
		 *  position (may be a short or very long distance) as quickly as
		 *  possible, and then hold that position.
		 */
		RUN_TO_POSITION,

		/**
		 *  This is closed-loop speed control.
		 *  <p>
		 *  Encoders are required for this mode. SetPower() is actually
		 *  requesting a certain speed, based on the top speed of encoder
		 *  4000 pulses per second.
		 *  <p>
		 *  The Motor controllers automatically adjust the voltage to the
		 *  motors to obtain the requested speed. This is like a car's cruise
		 *  control. It's great at producing slow speeds. It's also great
		 *  for making both sides of the robot run at the same speed to get
		 *  straighter driving.
		 */
		RUN_USING_ENCODERS,

		/**
		 *  This is open-loop speed control.
		 *  This means that setPower() simply sets the voltage level applied
		 *  to the motors. How fast you move depends on battery voltage, robot
		 *  friction, slope etc.
		 */
		RUN_WITHOUT_ENCODERS
	}

	/** Get the current channel mode. */
	public IDcMotorController.RunMode getMotorChannelMode(int motor);

	/**
	 *  Get the current device mode (read, write, or read/write).
	 *  Note: on USB devices, this will always return "READ_WRITE" mode.
	 */
	public IDcMotorController.DeviceMode getMotorControllerDeviceMode();

	/** Get the current motor position */
	public int  getMotorCurrentPosition(int motor);

	/** Get the current motor power */
	public double  getMotorPower(int motor);

	/** Is motor power set to float? */
	public boolean  getMotorPowerFloat(int motor);

	/** Get the current motor target position */
	public int  getMotorTargetPosition(int motor);

	/** Is the motor busy? */
	public boolean  isBusy(int motor);

	/** Set the current channel mode. */
	public void  setMotorChannelMode(int motor, IDcMotorController.RunMode mode);

	/**
	 *  Set the device into read, write, or read/write modes.
	 *  Note: If you are using the NxtDcMotorController, you need to switch the
	 *  controller into "read" mode before doing a read, and into "write" mode
	 *  before doing a write.
	 */
	public void  setMotorControllerDeviceMode(IDcMotorController.DeviceMode mode);

	/**
	 *  Set the current motor power.
	 */
	public void  setMotorPower(int motor, double power);

	/**
	 *  Allow motor to float.
	 *  <p>
	 *  See {@link org.avalancherobotics.standalone.interfaces.IDcMotor#setPowerFloat}
	 *  for a more detailed explanation.
	 */
	public void  setMotorPowerFloat(int motor);

	/** Set the motor target position. */
	public void  setMotorTargetPosition(int motor, int position);

}