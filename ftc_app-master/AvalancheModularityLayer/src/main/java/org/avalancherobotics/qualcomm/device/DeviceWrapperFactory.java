package org.avalancherobotics.qualcomm.device;

import org.avalancherobotics.qualcomm.output.DcMotorWrapper;
import org.avalancherobotics.qualcomm.output.DcMotorControllerWrapper;
import org.avalancherobotics.qualcomm.device.HardwareDeviceWrapper;
import org.avalancherobotics.qualcomm.output.ServoWrapper;
import org.avalancherobotics.qualcomm.output.ServoControllerWrapper;

import org.avalancherobotics.standalone.interfaces.IDcMotor;
import org.avalancherobotics.standalone.interfaces.IDcMotorController;
import org.avalancherobotics.standalone.interfaces.IDevice;
import org.avalancherobotics.standalone.interfaces.IServo;
import org.avalancherobotics.standalone.interfaces.IServoController;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoController;

/** */
public class DeviceWrapperFactory
{
	// These hashmaps are used to ensure that each controller receives only
	// one wrapper.

	private /*@NonNull*/ HashMap<DcMotorController, /*@NonNull*/ DcMotorControllerWrapper>
		dcMotorControllerWrappers = new HashMap<>();

	private /*@NonNull*/ HashMap<ServoController, /*@NonNull*/ ServoControllerWrapper>
		serverControllerWrappers = new HashMap<>();

	/**
	 *  Destroys all tables of controller wrapper instances, thus forcing
	 *  the creation of new controller wrappers whenever they are encountered.
	 */
	public void clear() {}

	/** */
	public IDcMotor createWrapper(DcMotor toWrap)
	{ return new DcMotorWrapper(toWrap,createWrapper(toWrap.getController())); }

	/** */
	public IDcMotorController createWrapper(DcMotorController toWrap)
	{
		IDcMotorController wrapper = dcMotorControllerWrappers.get(toWrap);
		if ( wrapper == null )
		{
			wrapper = new DcMotorControllerWrapper(toWrap);
			dcMotorControllerWrappers.put(toWrap, wrapper);
		}
		return wrapper;
	}

	/** */
	public IServo createWrapper(Servo toWrap)
	{ return new ServoWrapper(toWrap,createWrapper(toWrap.getController())); }

	/** */
	public IServoController createWrapper(ServoController toWrap)
	{
		IServoController wrapper = serverControllerWrappers.get(toWrap);
		if ( wrapper == null )
		{
			wrapper = new ServoControllerWrapper(toWrap);
			serverControllerWrappers.put(toWrap, wrapper);
		}
		return wrapper;
	}
}
