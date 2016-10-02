package org.avalancherobotics.standalone.library.output;

import org.avalancherobotics.qualcomm.device.HardwareDeviceWrapper;
import org.avalancherobotics.standalone.interfaces.IDcMotorController;

import com.qualcomm.robotcore.hardware.DcMotorController;

public class DcMotorControllerWrapper
	extends HardwareDeviceWrapper
	implements IDcMotorController
{
	// -------------------------------------------------------------------------
	//                           Integration
	// -------------------------------------------------------------------------

	private /*@NonNull*/ DcMotorController wrappedController;

	@Override
	protected final /*@NonNull*/ HardwareDevice getWrappedDevice() { return wrappedController; }

	/**
	 *  Most callers will want to call
	 *  {@link org.avalancherobotics.qualcomm.device.DeviceWrapperFactory#createWrapper(DcMotorController)}
	 *  from an appropriate DeviceWrapperFactory instance, rather than calling
	 *  this constructor directly.
	 */
	public DcMotorControllerWrapper( /*@NonNull*/ DcMotorController wrappedController )
	{
		this.wrappedController = wrappedController;
		if ( wrappedController == null )
			throw new IllegalArgumentException("The wrappedController parameter must be non-null.");
	}

	/**
	 *  Exposes the reference to the underlying Qualcomm-based DcMotorController object.
	 */
	public /*@NonNull*/ DcMotorController getWrappedObject() { return wrappedController; }

	// -------------------------------------------------------------------------
	//                         Enum Conversion
	// -------------------------------------------------------------------------

	/**
	 *  Converts a standalone IDcMotorController.DeviceMode value into a proprietary
	 *  DcMotorController.DeviceMode value.
	 */
	public static DcMotorController.DeviceMode toProprietaryDeviceMode(IDcMotorController.DeviceMode deviceMode)
	{
		switch(deviceMode)
		{
			case READ_ONLY                : return DcMotorController.DeviceMode.READ_ONLY;
			case READ_WRITE               : return DcMotorController.DeviceMode.READ_WRITE;
			case SWITCHING_TO_READ_MODE   : return DcMotorController.DeviceMode.SWITCHING_TO_READ_MODE;
			case SWITCHING_TO_WRITE_MODE  : return DcMotorController.DeviceMode.SWITCHING_TO_WRITE_MODE;
			case WRITE_ONLY               : return DcMotorController.DeviceMode.WRITE_ONLY;
			default:
				throw new Exception(
					"Unimplemented or unsupported conversion: attempt to convert "+
					"value "+deviceMode+" of type IDcMotorController.DeviceMode into a value "+
					"of type DcMotorController.DeviceMode");
		}
	}

	/**
	 *  Converts a proprietary DcMotorController.DeviceMode value into a standalone
	 *  IDcMotorController.DeviceMode value.
	 */
	public static IDcMotorController.DeviceMode toStandaloneDeviceMode(DcMotorController.DeviceMode deviceMode)
	{
		switch(deviceMode)
		{
			case READ_ONLY                : return IDcMotorController.DeviceMode.READ_ONLY;
			case READ_WRITE               : return IDcMotorController.DeviceMode.READ_WRITE;
			case SWITCHING_TO_READ_MODE   : return IDcMotorController.DeviceMode.SWITCHING_TO_READ_MODE;
			case SWITCHING_TO_WRITE_MODE  : return IDcMotorController.DeviceMode.SWITCHING_TO_WRITE_MODE;
			case WRITE_ONLY               : return IDcMotorController.DeviceMode.WRITE_ONLY;
			default:
				throw new Exception(
					"Unimplemented or unsupported conversion: attempt to convert "+
					"value "+deviceMode+" of type DcMotorController.DeviceMode into a value "+
					"of type IDcMotorController.DeviceMode");
		}
	}

	/**
	 *  Converts a standalone IDcMotorController.RunMode value into a proprietary
	 *  DcMotorController.RunMode value.
	 */
	public static DcMotorController.RunMode toProprietaryRunMode(IDcMotorController.RunMode runMode)
	{
		switch(runMode)
		{
			case RESET_ENCODERS       : return DcMotorController.RunMode.RESET_ENCODERS;
			case RUN_TO_POSITION      : return DcMotorController.RunMode.RUN_TO_POSITION;
			case RUN_USING_ENCODERS   : return DcMotorController.RunMode.RUN_USING_ENCODERS;
			case RUN_WITHOUT_ENCODERS : return DcMotorController.RunMode.RUN_WITHOUT_ENCODERS;
			default:
				throw new Exception(
					"Unimplemented or unsupported conversion: attempt to convert "+
					"value "+runMode+" of type IDcMotorController.RunMode into a value "+
					"of type DcMotorController.RunMode");
		}
	}

	/**
	 *  Converts a proprietary DcMotorController.RunMode value into a standalone
	 *  IDcMotorController.RunMode value.
	 */
	public static IDcMotorController.RunMode toStandaloneRunMode(DcMotorController.RunMode runMode)
	{
		switch(runMode)
		{
			case RESET_ENCODERS       : return IDcMotorController.RunMode.RESET_ENCODERS;
			case RUN_TO_POSITION      : return IDcMotorController.RunMode.RUN_TO_POSITION;
			case RUN_USING_ENCODERS   : return IDcMotorController.RunMode.RUN_USING_ENCODERS;
			case RUN_WITHOUT_ENCODERS : return IDcMotorController.RunMode.RUN_WITHOUT_ENCODERS;
			default:
				throw new Exception(
					"Unimplemented or unsupported conversion: attempt to convert "+
					"value "+runMode+" of type DcMotorController.RunMode into a value "+
					"of type IDcMotorController.RunMode");
		}
	}

	// -------------------------------------------------------------------------
	//                         Implementation
	// -------------------------------------------------------------------------

	/** Get the current channel mode. */
	public IDcMotorController.RunMode getMotorChannelMode(int motor)
	{
		return DcMotorControllerWrapper.toStandaloneRunMode(
			wrappedController.getMotorChannelMode(motor));
	}

	/**
	 *  Get the current device mode (read, write, or read/write).
	 *  Note: on USB devices, this will always return "READ_WRITE" mode.
	 */
	public IDcMotorController.DeviceMode getMotorControllerDeviceMode()
	{
		return DcMotorControllerWrapper.toStandaloneDeviceMode(
			wrappedController.getMotorControllerDeviceMode());
	}

	/** Get the current motor position */
	public int  getMotorCurrentPosition(int motor) { return wrappedController.getMotorCurrentPosition(motor); }

	/** Get the current motor power */
	public double  getMotorPower(int motor) { return wrappedController.getMotorPower(motor); }

	/** Is motor power set to float? */
	public boolean  getMotorPowerFloat(int motor) { return wrappedController.getMotorPowerFloat(motor); }

	/** Get the current motor target position */
	public int  getMotorTargetPosition(int motor) { return wrappedController.getMotorTargetPosition(motor); }

	/** Is the motor busy? */
	public boolean  isBusy(int motor) { return wrappedController.isBusy(motor); }

	/** Set the current channel mode. */
	public void  setMotorChannelMode(int motor, IDcMotorController.RunMode mode)
	{
		wrappedController.setMotorChannelMode(
			motor, DcMotorControllerWrapper.toProprietaryRunMode(mode));
	}

	/**
	 *  Set the device into read, write, or read/write modes.
	 *  Note: If you are using the NxtDcMotorController, you need to switch the
	 *  controller into "read" mode before doing a read, and into "write" mode
	 *  before doing a write.
	 */
	public void  setMotorControllerDeviceMode(IDcMotorController.DeviceMode mode)
	{
		wrappedController.setMotorControllerDeviceMode(
			DcMotorControllerWrapper.toProprietaryDeviceMode(mode));
	}

	/** Set the current motor power */
	public void  setMotorPower(int motor, double power) { wrappedController.setMotorPower(motor,power); }

	/** Allow motor to float */
	public void  setMotorPowerFloat(int motor) { wrappedController.setMotorPowerFloat(motor); }

	/** Set the motor target position. */
	public void  setMotorTargetPosition(int motor, int position) { wrappedController.setMotorTargetPosition(motor,position); }

}