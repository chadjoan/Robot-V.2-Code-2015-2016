package org.avalancherobotics.qualcomm;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.avalancherobotics.standalone.interfaces.ILayeredOpMode;
import org.avalancherobotics.qualcomm.device.HardwareMapWrapper;
import org.avalancherobotics.qualcomm.input.GamepadDevice;

import java.util.HashSet;

/**
 *  A wrapper that exposes the functionality of a Qualcomm-based OpMode object
 *  through the ILayeredOpMode interface.
 */
public class QualcommOpModeWrapper implements ILayeredOpMode
{
	private   /*@NonNull*/   OpMode              wrappedOpMode;

    private   /*@NonNull*/   GamepadDevice       gamepadDevice1;
    private   /*@NonNull*/   GamepadDevice       gamepadDevice2;

    private   /*@NonNull*/   Object              syncObject;

    private   /*@NonNull*/   InputSynchronizer   inputDeviceSet;

    private   /*@NonNull*/   HardwareMapWrapper  hardwareMapWrapper;

    private   /*@NonNull*/   ArrayList</*@NonNull*/ Gamepad.GamepadCallback>  gamepad1Callbacks = new ArrayList<>();
    private   /*@NonNull*/   ArrayList</*@NonNull*/ Gamepad.GamepadCallback>  gamepad2Callbacks = new ArrayList<>();

	/**
	 *  Creates an object that exposes the functionality of the given
	 *  Qualcomm-based OpMode object through the ILayeredOpMode interface.
	 *  <p>
	 *  Warning: This will replace the Gamepad instances found on the wrapped
	 *  OpMode object.  This is necessary to receive input events via the
	 *  Gamepad.GamepadCallback mechanism, because Gamepad does not provide
	 *  a way to register listeners with existing Gamepads: the registration
	 *  only accepts one listener and only upon construction.  Most code will
	 *  not notice this change; you only need to be concerned if your code
	 *  holds references to the opModeToWrap.gamepad1 or opModeToWrap.gamepad2
	 *  objects that existed before calling this constructor, or if your code
	 *  also constructs new opModeToWrap.gamepad1 or opModeToWrap.gamepad2
	 *  instances for the purpose of registering GamepadCallback object(s).
	 *  Once a QualcommOpModeWrapper has been constructed,
	 *  {@link #addGamepadCallback} can be used if the caller has a
	 *  Gamepad.GamepadCallback object that needs to be notified when the
	 *  OpMode's Gamepad objects update.  If possible, such requirements should
	 *  be satisfied by polling the input device set ({@link #getInputDeviceSet}).
	 */
	public QualcommOpModeWrapper( /*@NonNull*/ OpMode opModeToWrap )
	{
        Gamepad.GamepadCallback gamepadCallback =
			new Gamepad.GamepadCallback() {
				void gamepadChanged(Gamepad gamepad)
				{
					if ( gamepad == this.wrappedOpMode.gamepad1 )
						forwardEvent(gamepad, this.gamepad1Callbacks, this.gamepadDevice1);
					else if ( gamepad == this.wrappedOpMode.gamepad2 )
						forwardEvent(gamepad, this.gamepad2Callbacks, this.gamepadDevice2);
				}

				void forwardEvent(
					/*@NonNull*/ Gamepad                      gamepad,
					/*@NonNull*/ Set<Gamepad.GamepadCallback> callbacks,
					/*@NonNull*/ GamepadDevice                device
					)
				{
					synchronized(this.syncObject)
					{
						for ( Gamepad.GamepadCallback g : callbacks )
							g.gamepadChanged(gamepad);

						device.scanForEvents();
					}
				}
			};

		this.syncObject = this;

		this.wrappedOpMode = opModeToWrap;
        this.wrappedOpMode.gamepad1 = new Gamepad(gamepadCallback);
        this.wrappedOpMode.gamepad2 = new Gamepad(gamepadCallback);

        this.gamepadDevice1 = new GamepadDevice("gamepad#1",this.wrappedOpMode.gamepad1);
        this.gamepadDevice2 = new GamepadDevice("gamepad#2",this.wrappedOpMode.gamepad2);

        this.inputDeviceSet = new InputSynchronizer(this.syncObject);
        this.inputDeviceSet.add(this.gamepadDevice1);
        this.inputDeviceSet.add(this.gamepadDevice2);

        this.hardwareMapWrapper = new HardwareMapWrapper(this.wrappedOpMode.hardwareMap);
        this.hardwareMapWrapper.get<IFtcQualcommGamepad>().put(this.gamepadDevice1);
        this.hardwareMapWrapper.get<IFtcQualcommGamepad>().put(this.gamepadDevice2);
	}

	/**
	 *  Exposes the reference to the underlying Qualcomm-based OpMode object.
	 */
	public /*@NonNull*/ OpMode getWrappedOpMode() { return wrappedOpMode; }

	/**
	 *  Registers the given 'callback' object so that it is called whenever
	 *  the underlying OpMode.gamepad1 object calls its callback.
	 *  If callback is null, no exception is thrown and no action is performed.
	 */
	public void addGamepad1Callback(Gamepad.GamepadCallback callback)
	{
		if ( callback != null )
			gamepad1Callbacks.add(callback);
	}

	/**
	 *  Unregisters the given 'callback' object so that it is no longer called
	 *  whenever the underlying OpMode.gamepad1 object calls its callback.
	 *  This method performs no function, nor does it throw an exception, if
	 *  the callback specified by the argument was not previously added to
	 *  this object. If callback is null, no exception is thrown and no action
	 *  is performed.
	 */
	public void removeGamepad1Callback(Gamepad.GamepadCallback callback)
	{
		if ( callback != null )
			gamepad1Callbacks.remove(callback);
	}

	/**
	 *  Registers the given 'callback' object so that it is called whenever
	 *  the underlying OpMode.gamepad2 object calls its callback.
	 *  If callback is null, no exception is thrown and no action is performed.
	 */
	public void addGamepad2Callback(Gamepad.GamepadCallback callback)
	{
		if ( callback != null )
			gamepad2Callbacks.add(callback);
	}

	/**
	 *  Unregisters the given 'callback' object so that it is no longer called
	 *  whenever the underlying OpMode.gamepad2 object calls its callback.
	 *  This method performs no function, nor does it throw an exception, if
	 *  the callback specified by the argument was not previously added to
	 *  this object. If callback is null, no exception is thrown and no action
	 *  is performed.
	 */
	public void removeGamepad2Callback(Gamepad.GamepadCallback callback)
	{
		if ( callback != null )
			gamepad2Callbacks.remove(callback);
	}

	/**
	 *  Assigns the synchronization object used to perform any synchronized
	 *  operations (such as queueing gamepad events).
	 *  <p>
	 *  By default, any synchronization within the QualcommOpModeWrapper will
	 *  be performed upon its own 'this' instance.
	 *  <p>
	 *  This setter allows the caller to ensure that QualcommOpModeWrapper's
	 *  synchronized operations are synchronized with other operations
	 *  (such as the SwerveRobotics SynchronizedOpMode) that are external to
	 *  the QualcommOpModeWrapper.
	 *  <p>
	 *  Note: Calling this may change any synchronization objects used by
	 *  the objects owned by this class.  For example, the objects returned by
	 *  {@link #getInputDeviceSet} and {@link #getDeviceRegistry} may be
	 *  modified by calling this method.  The current implementation uses
	 *  an {@link org.avalancherobotics.standalone.input.InputSynchronizer}
	 *  instance to implement the {@link #getInputDeviceSet} getter, and
	 *  calling this setter will also set the InputSynchronizer instance's
	 *  synchronization object.  (Currently, no synchronization is performed
	 *  on IDevice objects in the device registry, because this functionality
	 *  can be obtained by using a Swerve Robotics SynchronousOpMode instance
	 *  as the underlying OpMode.)
	 *  <p>
	 *  Future directions: it might be a good idea to move all synchronization
	 *  code, such as the synchronization performed by InputSynchronizer, out
	 *  of the modularity layer.  This functionality needs to exist, but is
	 *  not directly related to modularity, and might be better off in another
	 *  library.
	 */
	public void setSynchronizationObject(/*@NonNull*/ Object obj)
	{
		if ( obj == null )
			throw new IllegalArgumentException("setSynchronizationObject: The obj parameter must be non-null.");
		this.inputDeviceSet.setSynchronizationObject(obj);
		this.syncObject = obj;
	}

	/**
	 *  Returns the synchronization object used to perform any synchronized
	 *  operations (such as queueing gamepad events).
	 *  If no synchronization object has been assigned with {@link #setSynchronizationObject},
	 *  then this will return a reference to the QualcommOpModeWrapper itself.
	 */
	public /*@NonNull*/ Object getSynchronizationObject() { return this.syncObject; }

	/**
	 *  Exposes the list of (pollable) input devices known to the ILayeredOpMode.
	 */
	public /*@NonNull*/ IInputDeviceSet getInputDeviceSet()
	{
		return inputDeviceSet;
	}

	/**
	 *  Exposes the registry of all devices known to the ILayeredOpMode.
	 */
	public /*@NonNull*/ IDeviceRegistry getDeviceRegistry()
	{
		return hardwareMapWrapper;
	}
}
