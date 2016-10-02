package org.avalancherobotics.standalone.library.output;

import org.avalancherobotics.qualcomm.device.HardwareDeviceWrapper;
import org.avalancherobotics.standalone.interfaces.IServo;
import org.avalancherobotics.standalone.interfaces.IServoController;
import org.avalancherobotics.standalone.internal.UnimplementedException;

import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoController;

/** */
public class ServoWrapper extends HardwareDeviceWrapper implements IServo
{
	// -------------------------------------------------------------------------
	//                           Integration
	// -------------------------------------------------------------------------
	private /*@NonNull*/ Servo            wrappedServo;
	private /*@NonNull*/ ServoController  wrappedController;
	private /*@NonNull*/ IServoController controller;

	@Override
	protected final /*@NonNull*/ HardwareDevice getWrappedDevice() { return wrappedServo; }

	/**
	 *  Most callers will want to call
	 *  {@link org.avalancherobotics.qualcomm.device.DeviceWrapperFactory#createWrapper(Servo)}
	 *  from an appropriate DeviceWrapperFactory instance, rather than calling
	 *  this constructor directly.
	 *  Note that, since constructing this requires passing an IServoController,
	 *  the caller will have to have to wrap the servo's ServoController first
	 *  (and <i>only</i> once per controller) before wrapping the Servo.
	 */
	public ServoWrapper( /*@NonNull*/ Servo servoToWrap, /*@NonNull*/ IServoController controller )
	{
		this.wrappedServo = servoToWrap;
		this.wrappedController = wrappedServo.getController();
		this.controller = controller;
		if ( servoToWrap == null )
			throw new IllegalArgumentException("The servoToWrap parameter must be non-null.");
		if ( controller == null )
			throw new IllegalArgumentException("The controller parameter must be non-null.");
		if ( controller.getWrappedObject() != wrappedController )
			throw new Exception(
				"The \"wrappedServo\"'s controller must match the given "+
				"\"controller\" parameter.  This one did not.");
	}
	// -------------------------------------------------------------------------
	//                         Enum Conversion
	// -------------------------------------------------------------------------

	/**
	 *  Converts a standalone IServo.Direction value into a proprietary
	 *  Servo.Direction value.
	 */
	public static Servo.Direction toProprietaryDirection(IServo.Direction direction)
	{
		switch(direction)
		{
			case FORWARD: return Servo.Direction.FORWARD;
			case REVERSE: return Servo.Direction.REVERSE;
			default:
				throw new IllegalArgumentException(
					"Unimplemented or unsupported conversion: attempt to convert "+
					"value "+direction+" of type IServo.Direction into a value "+
					"of type Servo.Direction");
		}
	}

	/**
	 *  Converts a proprietary Servo.Direction value into a standalone
	 *  IServo.Direction value.
	 */
	public static IServo.Direction toStandaloneDirection(Servo.Direction direction)
	{
		switch(direction)
		{
			case FORWARD: return IServo.Direction.FORWARD;
			case REVERSE: return IServo.Direction.REVERSE;
			default:
				throw new IllegalArgumentException(
					"Unimplemented or unsupported conversion: attempt to convert "+
					"value "+direction+" of type Servo.Direction into a value "+
					"of type IServo.Direction");
		}
	}

	// -------------------------------------------------------------------------
	//                         Implementation
	// -------------------------------------------------------------------------

	/** */
	protected double getDefaultMinPosition() { return Servo.MIN_POSITION; }

	/** */
	protected double getDefaultMaxPosition() { return Servo.MAX_POSITION; }

	/** Get Servo Controller */
	public IServoController  getController()
	{
		if ( controller.getWrappedObject() != wrappedController )
			throw new UnimplementedException("Underlying ServoController changed.  "+
				"The ServoWrapper class is not currently written to handle this situation.")
		return controller;
	}

	/** Get the direction */
	public IServo.Direction  getDirection()
	{
		return ServoWrapper.toStandaloneDirection(wrappedServo.getDirection());
	}

	/** Get Channel */
	public int  getPortNumber() { return wrappedServo.getPortNumber(); }

	/** Get the position of the servo */
	public double  getPosition() { return wrappedServo.getPosition(); }

	/** Automatically scale the position of the servo. */
	public void  scaleRange(double min, double max) { wrappedServo.scaleRange(min,max); }

	/** Set the direction */
	public void  setDirection(IServo.Direction direction)
	{
		wrappedServo.setDirection(ServoWrapper.toProprietaryDirection(direction));
	}

	/** Set the position of the servo */
	public void  setPosition(double position) { wrappedServo.setPosition(position); }
}