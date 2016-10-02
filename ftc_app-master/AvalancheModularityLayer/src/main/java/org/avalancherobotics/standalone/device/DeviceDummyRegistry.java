package org.avalancherobotics.standalone.device;

// Implementation NOTE: The standalone package shall never depend on the
//   org.avalancherobotics.library package, the org.avalancherobotics.desktop package,
//   or any other implementors.  It should also use only Java code that will
//   be found on all platforms (ex: Android + Desktop), which means that only
//   things in java.util are remotely safe.
//

import org.avalancherobotics.standalone.device.DeviceRegistry;
import org.avalancherobotics.standalone.device.UnimplementedDeviceException;
import org.avalancherobotics.standalone.interfaces.IDcMotor;
import org.avalancherobotics.standalone.interfaces.IDcMotorController;
import org.avalancherobotics.standalone.interfaces.IDevice;
import org.avalancherobotics.standalone.interfaces.IDeviceDirectory;
import org.avalancherobotics.standalone.interfaces.IDeviceRegistry;
import org.avalancherobotics.standalone.interfaces.IServo;
import org.avalancherobotics.standalone.interfaces.IServoController;
import org.avalancherobotics.standalone.internal.DeviceConnectivity;

import org.avalancherobotics.standalone.output.DcMotorDummy;
import org.avalancherobotics.standalone.output.DcMotorControllerDummy;
import org.avalancherobotics.standalone.device.HardwareDeviceDummy;
import org.avalancherobotics.standalone.output.ServoDummy;
import org.avalancherobotics.standalone.output.ServoControllerDummy;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *  Create a DeviceRegistry that creates dummy devices lazily, as
 *  they are accessed in the caller's program's logic.
 *  <p>
 *  Note that every dummy device that connects to a controller will be created
 *  with a null controller by default.  If the calling code relies on
 *  controller&lt;-&gt;device relationships being set up a certain way, then it
 *  should call methods like {@link #defineRelation_DcMotor_DcMotorController} or
 *  {@link #defineRelation_Servo_ServoController}.
 *  <p>
 *  Most users of this modularity layer will use a
 *  {@link org.avalancherobotics.desktop.VirtualOpMode} or similar.
 *  Such an object creates its own instance of DeviceDummyRegistry and exposes
 *  it through the
 *  {@link org.avalancherobotics.standalone.interfaces.ILayeredOpMode#getDeviceRegistry}
 *  method, thus it will not be necessary to instantiate this separately.
 *  <p>
 *  This class will require modification to add support for more (dummy)
 *  hardware device types.  Without such modification, any code that attempts
 *  to access those devices will get null objects or thrown exceptions instead
 *  of the facades it expects.
 */
public class DeviceDummyRegistry extends DeviceRegistry
{
	// These hashmaps are used to record relationships between devices and
	// their controllers.

	private /*@NonNull*/ ArrayList<DeviceCreationListener> deviceCreationListeners = new ArrayList<>();

	private /*@NonNull*/ HashMap<String,DcMotorControllerDummy> dcMotorToController = new HashMap<>();
	private /*@NonNull*/ HashMap<String,DcMotorDummy          > controllerToDcMotor = new HashMap<>();

	private /*@NonNull*/ HashMap<String,ServoControllerDummy  > servoToController = new HashMap<>();
	private /*@NonNull*/ HashMap<String,ServoDummy            > controllerToServo = new HashMap<>();

	/** */
	public interface DeviceCreationListener
	{
		/** */
		public void onCreate(HardwareDeviceDummy device);
	}

	/** */
	public DeviceDummyRegistry()
	{
		super();
		this.sync();
	}

	private void sync()
	{
		clear();

		// Wrap as many Dummy device types as are implemented.
		put(new DeviceDummyDirectory<>(this, DcMotorDummy.class           , IDcMotor.class           ));
		put(new DeviceDummyDirectory<>(this, DcMotorControllerDummy.class , IDcMotorController.class ));
		put(new DeviceDummyDirectory<>(this, ServoDummy.class             , IServo.class             ));
		put(new DeviceDummyDirectory<>(this, ServoControllerDummy.class   , IServoController.class   ));
	}

	/**
	 *  Adds the specified DeviceCreationListener to receive device creation
	 *  events from this registry.
	 *  If 'listener' is null, no exception is thrown and no action is performed.
	 */
	public void addCreationListener(DeviceCreationListener listener)
	{
		if ( listener != null )
			this.deviceCreationListeners.add(listener);
	}

	/**
	 *  Removes the specified DeviceCreationListener so that it no longer
	 *  receives device creation events from this registry.
	 *  This method performs no function, nor does it throw an exception,
	 *  if 'listener' was not previously added to this registry.
	 *  If 'listener' is null, no exception is thrown and no action is performed.
	 */
	public void removeCreationListener(DeviceCreationListener listener)
	{
		if ( listener != null )
			this.deviceCreationListeners.remove(listener);
	}

	/**
	 *  @see #defineRelation_Servo_ServoController
	 */
	public void defineRelation_DcMotor_DcMotorController(
		String motorName, String controllerName, int motorNumber )
	{
		this.<
				IDcMotor,     IDcMotorController,
				DcMotorDummy, DcMotorControllerDummy>
			defineControllerRelation
				(motorName, controllerName, motorNumber,
				IDcMotor.class,     IDcMotorController.class,
				DcMotorDummy.class, DcMotorControllerDummy.class);
	}

	/**
	 *  @see #defineRelation_DcMotor_DcMotorController
	 */
	public void defineRelation_Servo_ServoController(
		String servoName, String controllerName, int channel )
	{
		this.<
				IServo,     IServoController,
				ServoDummy, ServoControllerDummy>
			defineControllerRelation
				(servoName, controllerName, channel,
				IServo.class,     IServoController.class,
				ServoDummy.class, ServoControllerDummy.class);
	}

	private <
		DEV_TYPE extends IDevice,
		CTRL_TYPE extends IDevice,
		DEV_DUMMY_TYPE extends HardwareDeviceDummy & DeviceConnectivity.Spoke<Integer>,
		CTRL_DUMMY_TYPE extends HardwareDeviceDummy & DeviceConnectivity.Hub<Integer>
		>
		void defineControllerRelation(
			String devName,  String controllerName,  int indexOnController,
			Class<DEV_TYPE>       devClass,      Class<CTRL_TYPE>       ctrlClass,
			Class<DEV_DUMMY_TYPE> devDummyClass, Class<CTRL_DUMMY_TYPE> ctrlDummyClass)
		throws UnimplementedDeviceException
	{
		// TODO: Is there a way to specify that (CTRL_DUMMY_TYPE is a CTRL_TYPE)
		//       so that some of these casts become unnecessary?
		//       Ditto for DEV_DUMMY_TYPE and DEV_TYPE.

		DeviceDummyDirectory<CTRL_DUMMY_TYPE, CTRL_TYPE> ctrlDir =
			(DeviceDummyDirectory<CTRL_DUMMY_TYPE, CTRL_TYPE>)this.<CTRL_TYPE>get(ctrlClass);
		CTRL_DUMMY_TYPE controller = ctrlDummyClass.cast(ctrlDir.getWithoutCreate(controllerName));
		if ( controller == null )
		{
			controller = this.<CTRL_DUMMY_TYPE>create(ctrlDummyClass, controllerName);
			ctrlDir.put(ctrlClass.cast(controller));
		}

		// Create the device as needed.  If it is needed, start it off
		// with the controller that we created in the previous step.
		// If the device already existed, then we will have to reassign
		// the controller in a later step.
		DeviceDummyDirectory<DEV_DUMMY_TYPE, DEV_TYPE> devDir =
			(DeviceDummyDirectory<DEV_DUMMY_TYPE, DEV_TYPE>)this.<DEV_TYPE>get(devClass);
		DEV_DUMMY_TYPE device = devDummyClass.cast(devDir.getWithoutCreate(devName));
		if ( device == null )
		{
			device = this.<DEV_DUMMY_TYPE>create(devDummyClass, devName, controller, indexOnController);
			devDir.put(devClass.cast(device));
		}

		//if ( device.getController() != controller )
		if ( device.getSpokePort().getHub() != controller )
		{
			// Presumably, the device already existed and had a different
			// controller than the one we are now defining.
			//

			/*
			// These checks seem unnecessary, since we are already forced to
			// assume that this.<T>get(...) returns a DeviceDummyDirectory.
			if ( !(device instanceof DEV_DUMMY_TYPE) )
				throw new ClassCastException("Device registry contained device of type "+
					device.getClass().getName()+
					" when a "+ devDummyClass.getName()+ " was expected.");

			if ( !(controller instanceof CTRL_DUMMY_TYPE) )
				throw new ClassCastException("Device registry contained device controller of type "+
					controller.getClass().getName()+
					" when a "+ ctrlDummyClass.getName()+ " was expected.");

			CTRL_DUMMY_TYPE dummyController = (CTRL_DUMMY_TYPE)controller;
			DEV_DUMMY_TYPE  dummyDevice     = (DEV_DUMMY_TYPE) device;

			DeviceConnectivity.<Integer>connect(dummyController, dummyDevice, indexOnController);
			*/
			DeviceConnectivity.<Integer>connect(controller, device, indexOnController);
		}
	}

	private void onCreate(HardwareDeviceDummy device)
	{
		for( DeviceCreationListener listener : this.deviceCreationListeners )
			listener.onCreate(device);
	}

	/** */
	public <DEVICE_TYPE extends HardwareDeviceDummy>
		DEVICE_TYPE create(
			/*@NonNull*/ Class<DEVICE_TYPE>  deviceDummyClass,
			/*@NonNull*/ String              deviceName)
		throws UnimplementedDeviceException
	{
		String className = deviceDummyClass.getName();
		HardwareDeviceDummy output = null;
		if ( DcMotorDummy.class.getName().equals(className) )
			output = new DcMotorDummy(deviceName, null, -1);
		else
		if ( DcMotorControllerDummy.class.getName().equals(className) )
			output = new DcMotorControllerDummy(deviceName);
		else
		if ( ServoDummy.class.getName().equals(className) )
			output = new ServoDummy(deviceName, null, -1);
		else
		if ( ServoControllerDummy.class.getName().equals(className) )
			output = new ServoControllerDummy(deviceName);
		else
			throw new UnimplementedDeviceException(
				"Could not construct device with class name "+className);

		onCreate(output);
		return deviceDummyClass.cast(output);
	}

	/** */
	public <DEVICE_TYPE extends HardwareDeviceDummy>
		DEVICE_TYPE create(
			/*@NonNull*/ Class<DEVICE_TYPE>  deviceDummyClass,
			/*@NonNull*/ String              deviceName,
			/*@NonNull*/ HardwareDeviceDummy controller,
			int port
			)
	{
		String className = deviceDummyClass.getName();
		HardwareDeviceDummy output = null;
		if ( DcMotorDummy.class.getName().equals(className) )
			output = new DcMotorDummy(deviceName, (DcMotorControllerDummy)controller, port);
		else
		if ( ServoDummy.class.getName().equals(className) )
			output = new ServoDummy(deviceName, (ServoControllerDummy)controller, port);
		else
			throw new UnimplementedDeviceException(
				"Could not construct device with class name "+className);

		onCreate(output);
		return deviceDummyClass.cast(output);
	}

	/** */
	public static class DeviceDummyDirectory<
			DUMMY_DEVICE_TYPE extends HardwareDeviceDummy,
			IFACE_DEVICE_TYPE extends IDevice
			>
		extends DeviceDirectory<IFACE_DEVICE_TYPE>
	{
		private /*@NonNull*/ DeviceDummyRegistry      registry;
		private /*@NonNull*/ Class<DUMMY_DEVICE_TYPE> deviceDummyClass;

		/** */
		public DeviceDummyDirectory(
			/*@NonNull*/ DeviceDummyRegistry      registry,
			/*@NonNull*/ Class<DUMMY_DEVICE_TYPE> deviceDummyClass,
			/*@NonNull*/ Class<IFACE_DEVICE_TYPE> deviceInterfaceClass)
			throws IllegalArgumentException
		{
			super(deviceInterfaceClass);
			this.registry = registry;
			this.deviceDummyClass = deviceDummyClass;
			if ( registry == null )
				throw new IllegalArgumentException("The registry parameter must be non-null.");
			if ( deviceInterfaceClass == null )
				throw new IllegalArgumentException("The deviceInterfaceClass parameter must be non-null.");
		}

		/**
		*  Returns the device with the given name if it already exists, otherwise
		*  creates a new HardwareDeviceDummy of the given DEVICE_TYPE, assigns it the
		*  given 'deviceName', and then returns that.
		*/
		@Override
		public /*@NonNull*/ IFACE_DEVICE_TYPE get(String deviceName)
		{
			IFACE_DEVICE_TYPE result = super.get(deviceName);
			if ( result == null )
			{
				result = this.getElementClass().cast(
					registry.<DUMMY_DEVICE_TYPE>create(deviceDummyClass, deviceName));

				super.put(result);
			}
			return result;
		}

		/**
		 *  Calls the underlying {@link org.avalancherobotics.standalone.device.DeviceDirectory#get}
		 *  method directly, without attempting to create missing devices.
		 */
		public IFACE_DEVICE_TYPE getWithoutCreate(String deviceName)
		{
			return super.get(deviceName);
		}
	}
}
