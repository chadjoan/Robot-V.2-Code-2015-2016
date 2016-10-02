package org.avalancherobotics.standalone.library.output;

import org.avalancherobotics.qualcomm.device.HardwareDeviceWrapper;
import org.avalancherobotics.standalone.interfaces.IDcMotor;
import org.avalancherobotics.standalone.interfaces.IDcMotorController;
import org.avalancherobotics.standalone.internal.UnimplementedException;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;

/** */
public class DcMotorWrapper extends HardwareDeviceWrapper implements IDcMotor
{
	// -------------------------------------------------------------------------
	//                           Integration
	// -------------------------------------------------------------------------
	private /*@NonNull*/ DcMotor            wrappedDcMotor;
	private /*@NonNull*/ DcMotorController  wrappedController;
	private /*@NonNull*/ IDcMotorController controller;

	@Override
	protected final /*@NonNull*/ HardwareDevice getWrappedDevice() { return wrappedDcMotor; }

	/**
	 *  Most callers will want to call
	 *  {@link org.avalancherobotics.qualcomm.device.DeviceWrapperFactory#createWrapper(DcMotor)}
	 *  from an appropriate DeviceWrapperFactory instance, rather than calling
	 *  this constructor directly.
	 *  Note that, since constructing this requires passing an IDcMotorController,
	 *  the caller will have to have to wrap the motor's DcMotorController first
	 *  (and <i>only</i> once per controller) before wrapping the DcMotor.
	 */
	public DcMotorWrapper( /*@NonNull*/ DcMotor dcMotorToWrap, /*@NonNull*/ IDcMotorController controller )
	{
		this.wrappedDcMotor = dcMotorToWrap;
		this.wrappedController = wrappedDcMotor.getController();
		this.controller = controller;
		if ( dcMotorToWrap == null )
			throw new IllegalArgumentException("The dcMotorToWrap parameter must be non-null.");
		if ( controller == null )
			throw new IllegalArgumentException("The controller parameter must be non-null.");
		if ( controller.getWrappedObject() != wrappedController )
			throw new Exception(
				"The \"wrappedDcMotor\"'s controller must match the given "+
				"\"controller\" parameter.  This one did not.");
	}

	/**
	 *  Exposes the reference to the underlying Qualcomm-based DcMotor object.
	 */
	public /*@NonNull*/ DcMotor getWrappedObject() { return wrappedDcMotor; }

	// -------------------------------------------------------------------------
	//                         Enum Conversion
	// -------------------------------------------------------------------------

	/**
	 *  Converts a standalone IDcMotor.Direction value into a proprietary
	 *  DcMotor.Direction value.
	 */
	public static DcMotor.Direction toProprietaryDirection(IDcMotor.Direction direction)
	{
		switch(direction)
		{
			case FORWARD: return DcMotor.Direction.FORWARD;
			case REVERSE: return DcMotor.Direction.REVERSE;
			default:
				throw new IllegalArgumentException(
					"Unimplemented or unsupported conversion: attempt to convert "+
					"value "+direction+" of type IDcMotor.Direction into a value "+
					"of type DcMotor.Direction");
		}
	}

	/**
	 *  Converts a proprietary DcMotor.Direction value into a standalone
	 *  IDcMotor.Direction value.
	 */
	public static IDcMotor.Direction toStandaloneDirection(DcMotor.Direction direction)
	{
		switch(direction)
		{
			case FORWARD: return IDcMotor.Direction.FORWARD;
			case REVERSE: return IDcMotor.Direction.REVERSE;
			default:
				throw new IllegalArgumentException(
					"Unimplemented or unsupported conversion: attempt to convert "+
					"value "+direction+" of type DcMotor.Direction into a value "+
					"of type IDcMotor.Direction");
		}
	}

	// -------------------------------------------------------------------------
	//                         Implementation
	// -------------------------------------------------------------------------

	/** Get the current channel mode */
	public IDcMotorController.RunMode getChannelMode()
	{
		return DcMotorControllerWrapper.toStandaloneRunMode(wrappedDcMotor.getChannelMode());
	}

	/** Get DC motor controller */
	public IDcMotorController getController()
	{
		if ( controller.getWrappedObject() != wrappedController )
			throw new UnimplementedException("Underlying DcMotorController changed.  "+
				"The DcMotorWrapper class is not currently written to handle this situation.")
		return controller;
	}

	/** Get the current encoder value */
	public int getCurrentPosition() { return wrappedDcMotor.getCurrentPosition(); }

	/** Get the direction */
	public IDcMotor.Direction getDirection()
	{
		return DcMotorWrapper.toStandaloneDirection(wrappedDcMotor.getDirection());
	}

	/** Get port number */
	public int getPortNumber() { return wrappedDcMotor.getPortNumber(); }

	/** Get the current motor power */
	public double getPower() { return wrappedDcMotor.getPower(); }

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
	public boolean getPowerFloat() { return wrappedDcMotor.getPowerFloat(); }

	/** Get the current motor target position */
	public int getTargetPosition() { return wrappedDcMotor.getTargetPosition(); }

	/** Is the motor busy? */
	public boolean isBusy() { return wrappedDcMotor.isBusy(); }

	/** Set the current channel mode */
	public void setChannelMode(IDcMotorController.RunMode mode)
	{
		wrappedDcMotor.setChannelMode(DcMotorControllerWrapper.toProprietaryRunMode(mode));
	}

	/** Set the direction */
	public void setDirection(IDcMotor.Direction direction)
	{
		wrappedDcMotor.setDirection(DcMotorWrapper.toProprietaryDirection(direction));
	}

	/** Set the current motor power */
	public void setPower(double power) { wrappedDcMotor.setPower(power); }

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
	public void setPowerFloat() { wrappedDcMotor.setPowerFloat(); }

	/** Set the motor target position, using an integer. */
	public void setTargetPosition(int position) { wrappedDcMotor.setTargetPosition(position); }
}