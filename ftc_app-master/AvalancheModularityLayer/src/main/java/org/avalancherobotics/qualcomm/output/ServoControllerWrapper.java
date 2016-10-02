package org.avalancherobotics.standalone.library.output;

import org.avalancherobotics.qualcomm.device.HardwareDeviceWrapper;
import org.avalancherobotics.standalone.interfaces.IServoController;

import com.qualcomm.robotcore.hardware.ServoController;

public class ServoControllerWrapper
	extends HardwareDeviceWrapper
	implements IServerController
{
	// -------------------------------------------------------------------------
	//                           Integration
	// -------------------------------------------------------------------------

	private /*@NonNull*/ ServoController wrappedController;

	@Override
	protected final /*@NonNull*/ HardwareDevice getWrappedDevice() { return wrappedController; }

	/**
	 *  Most callers will want to call
	 *  {@link org.avalancherobotics.qualcomm.device.DeviceWrapperFactory#createWrapper(ServoController)}
	 *  from an appropriate DeviceWrapperFactory instance, rather than calling
	 *  this constructor directly.
	 */
	public ServoControllerWrapper( /*@NonNull*/ ServoController wrappedController )
	{
		this.wrappedController = wrappedController;
		if ( wrappedController == null )
			throw new IllegalArgumentException("The wrappedController parameter must be non-null.");
	}

	/**
	 *  Exposes the reference to the underlying Qualcomm-based ServoController object.
	 */
	public /*@NonNull*/ ServoController getWrappedObject() { return wrappedController; }

	// -------------------------------------------------------------------------
	//                         Enum Conversion
	// -------------------------------------------------------------------------

	/**
	 *  Converts a standalone IServoController.PwmStatus value into a proprietary
	 *  ServoController.PwmStatus value.
	 */
	public static ServoController.PwmStatus toProprietaryPwmStatus(IServoController.PwmStatus pwmStatus)
	{
		switch(pwmStatus)
		{
			case ENABLED:  return ServoController.PwmStatus.ENABLED;
			case DISABLED: return ServoController.PwmStatus.DISABLED;
			default:
				throw new Exception(
					"Unimplemented or unsupported conversion: attempt to convert "+
					"value "+pwmStatus+" of type IServoController.PwmStatus into a value "+
					"of type ServoController.PwmStatus");
		}
	}

	/**
	 *  Converts a proprietary ServoController.PwmStatus value into a standalone
	 *  IServoController.PwmStatus value.
	 */
	public static IServoController.PwmStatus toStandalonePwmStatus(ServoController.PwmStatus pwmStatus)
	{
		switch(pwmStatus)
		{
			case ENABLED:  return IServoController.PwmStatus.ENABLED;
			case DISABLED: return IServoController.PwmStatus.DISABLED;
			default:
				throw new Exception(
					"Unimplemented or unsupported conversion: attempt to convert "+
					"value "+pwmStatus+" of type ServoController.PwmStatus into a value "+
					"of type IServoController.PwmStatus");
		}
	}

	// -------------------------------------------------------------------------
	//                         Implementation
	// -------------------------------------------------------------------------

	/** Get the PWM status */
	public IServoController.PwmStatus  getPwmStatus()
	{
		return ServoControllerWrapper.toStandalonePwmStatus(wrappedController.getPwmStatus());
	}

	/** Get the position of a servo at a given channel */
	public double  getServoPosition(int channel) { return wrappedController.getServoPosition(channel); }

	/** PWM disable */
	public void  pwmDisable() { wrappedController.pwmDisable(); }

	/** PWM enable */
	public void  pwmEnable() { wrappedController.pwmEnable(); }

	/** Set the position of a servo at the given channel */
	public void  setServoPosition(int channel, double position)
	{ wrappedController.setServoPosition(channel,position); }
}