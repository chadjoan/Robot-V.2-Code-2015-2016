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
 *  com.qualcomm.robotcore.hardware.DcMotor class, but is not bundled with
 *  classes that have hard dependencies on Android.
 *  <p>
 *  Interface taken from
 *  http://ftckey.com/apis/ftc/com/qualcomm/robotcore/hardware/DcMotor.html
 *  on 2016-02-11.
 */
public interface IDcMotor extends IDevice
{
	/** Motor direction */
	public enum Direction
	{
		FORWARD,
		REVERSE
	}

	/** Get the current channel mode */
	public IDcMotorController.RunMode getChannelMode();

	/** Get DC motor controller */
	public IDcMotorController getController();

	/** Get the current encoder value */
	public int getCurrentPosition();

	/** Get the direction */
	public IDcMotor.Direction getDirection();

	/** Get port number */
	public int getPortNumber();

	/** Get the current motor power */
	public double getPower();

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
	public boolean getPowerFloat();

	/** Get the current motor target position */
	public int getTargetPosition();

	/** Is the motor busy? */
	public boolean isBusy();

	/** Set the current channel mode */
	public void setChannelMode(IDcMotorController.RunMode mode);

	/** Set the direction */
	public void setDirection(IDcMotor.Direction direction);

	/** Set the current motor power */
	public void setPower(double power);

	/**
	 *  Allow motor to float.
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
	public void setPowerFloat();

	/** Set the motor target position, using an integer. */
	public void setTargetPosition(int position);
}