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
 *  com.qualcomm.robotcore.hardware.Servo interface,
 *  but is not bundled with classes that have hard dependencies on Android.
 *  <p>
 *  Interface taken from
 *  http://ftckey.com/apis/ftc/com/qualcomm/robotcore/hardware/Servo.html
 *  on 2016-02-17.
 */
public interface IServo extends IDevice
{
	/** */
	public enum Direction
	{
		FORWARD,
		REVERSE
	}

	// NOTE: Min/Max position used to be "protected" visibility.
	//       However, Java does not allow protected visibility inside interfaces.
	//       Public will have to be good enough for now.
	/** */
	public double getDefaultMinPosition();

	/** */
	public double getDefaultMaxPosition();

	/** Get Servo Controller */
	public IServoController  getController();

	/** Get the direction */
	public IServo.Direction  getDirection();

	/** Get Channel */
	public int  getPortNumber();

	/**
	 *  Get the position of the servo
	 *  @return position, scaled from 0.0 to 1.0
	 */
	public double  getPosition();

	/**
	 *  Automatically scale the position of the servo.
	 *  <p>
	 *  For example, if scaleRange(0.2, 0.8) is set; then servo positions will be scaled to fit in that range.
	 *  <br>setPosition(0.0) scales to 0.2
	 *  <br>setPosition(1.0) scales to 0.8
	 *  <br>setPosition(0.5) scales to 0.5
	 *  <br>setPosition(0.25) scales to 0.35
	 *  <br>setPosition(0.75) scales to 0.65
	 *  <p>
	 *  This is useful if you don't want the servo to move past a given
	 *  position, but don't want to manually scale the input to setPosition
	 *  each time. getPosition() will scale the value back to a value between
	 *  0.0 and 1.0. If you need to know the actual position use
	 *  Servo.getController().getServoPosition(Servo.getChannel()).
	 *
	 *  @param min  minimum position of the servo from 0.0 to 1.0
	 *  @param max  maximum position of the servo from 0.0 to 1.0
	 *  @throws java.lang.IllegalArgumentException if out of bounds, or min >= max
	 */
	public void  scaleRange(double min, double max);

	/** Set the direction */
	public void  setDirection(IServo.Direction direction);

	/**
	 *  Set the position of the servo
	 *  @param position from 0.0 to 1.0
	 */
	public void  setPosition(double position);
}