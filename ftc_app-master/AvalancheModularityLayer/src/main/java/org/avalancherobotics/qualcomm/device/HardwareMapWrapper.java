package org.avalancherobotics.qualcomm.device;

import org.avalancherobotics.standalone.device.DeviceRegistry;
import org.avalancherobotics.standalone.interfaces.IDevice;
import org.avalancherobotics.standalone.interfaces.IDeviceDirectory;
import org.avalancherobotics.standalone.interfaces.IDeviceRegistry;

import org.avalancherobotics.standalone.interfaces.IDcMotor;
import org.avalancherobotics.standalone.interfaces.IDcMotorController;
import org.avalancherobotics.standalone.interfaces.IServo;
import org.avalancherobotics.standalone.interfaces.IServoController;
import org.avalancherobotics.standalone.interfaces.IFtcQualcommGamepad;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoController;

/**
 *  Create an instance of this class to implement a DeviceRegistry that wraps
 *  all of the Qualcomm HardwareDevice objects with a platform-agnostic
 *  IDevice interface.
 *  <p>
 *  Most users of this modularity layer will use a
 *  {@link org.avalancherobotics.qualcomm.QualcommOpModeWrapper} or similar.
 *  Such an object creates its own instance of HardwareMapWrapper and exposes
 *  it through the
 *  {@link org.avalancherobotics.standalone.interfaces.ILayeredOpMode#getDeviceRegistry}
 *  method, thus it will not be necessary to instantiate this separately.
 *  <p>
 *  This class will require modification to add support for more hardware
 *  devices.  Without such modification, the affected hardware devices will
 *  appear in the original OpMode object's HardwareMap, but not in the
 *  {@link org.avalancherobotics.qualcomm.QualcommOpModeWrapper}'s device registry.
 */
public class HardwareMapWrapper extends DeviceRegistry
{
	private /*@NonNull*/ HardwareMap wrappedMap;

	private /*@NonNull*/ DeviceWrapperFactory wrapperFactory = new DeviceWrapperFactory();

	/** */
	public HardwareMapWrapper( /*@NonNull*/ hardwareMapToWrap )
	{
		this.wrappedMap = hardwareMapToWrap;
		if ( hardwareMapToWrap == null )
			throw new IllegalArgumentException("The hardwareMapToWrap parameter must be non-null.");

		this.sync();
	}

	private void sync()
	{
		clear();

		// Wrap as many HardwareMap device types as are implemented.
		put<IDcMotor          > (new DeviceMappingWrapper<DcMotor           , IDcMotor           >(wrapperFactory, wrappedMap.dcMotor           ));
		put<IDcMotorController> (new DeviceMappingWrapper<DcMotorController , IDcMotorController >(wrapperFactory, wrappedMap.dcMotorController ));
		put<IServo            > (new DeviceMappingWrapper<Servo             , IServo             >(wrapperFactory, wrappedMap.servo             ));
		put<IServoController  > (new DeviceMappingWrapper<ServoController   , IServoController   >(wrapperFactory, wrappedMap.servoController   ));

		// We will also make it possible to add GamepadDevices by default.
		put<IFtcQualcommGamepad>(new DeviceDirectory<IFtcQualcommGamepad>());
	}
}
