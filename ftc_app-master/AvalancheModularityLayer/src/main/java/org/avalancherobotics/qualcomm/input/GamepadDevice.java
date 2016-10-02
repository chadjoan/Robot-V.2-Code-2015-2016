package org.avalancherobotics.qualcomm.input;

import org.avalancherobotics.standalone.input.*;
import org.avalancherobotics.standalone.interface.IInputDevice;
import org.avalancherobotics.standalone.interface.IFtcQualcommGamepad;
import org.avalancherobotics.standalone.internal.ArrayQueue;
import com.qualcomm.robotcore.hardware.Gamepad;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

// NOTE: Documentation for the Qualcomm Gamepad object can be found here:
// http://ftckey.com/apis/ftc/com/qualcomm/robotcore/hardware/Gamepad.html

public class GamepadDevice implements IFtcQualcommGamepad
{
	// This is the gamepad that gets asynchronously updated by Qualcomm's
	// proprietary code.
	private Gamepad qualcommGamepad;
	
	// This is the gamepad that gets updated whenever .scanForEvents() is called.
	// We diff qualcommGamepad against this to determine if there are new
	// events to queue.
	private Gamepad lastScanGamepad;

	// Used to implement the IDevice interface (required by IFtcQualcommGamepad->IInputDevice).
	private /*@NonNull*/ String deviceName;

	// This is used to synchronize the thread(s) from Gamepads' callbacks that
	// are calling scanForEvents with the thread from the OpMode or main
	// program that are calling things like pollEvent and peekEvent.
	private Object syncObject = new Object();

	private static final nControls = 17;
	private /*@NonNull*/ ArrayList</*@NonNull*/ InputControl> controls = new ArrayList<>(nControls);

	// Event buffers.
	private /*@NonNull*/ InputEventBuffer<InputAnalogEvent>  stickEventBuf   = new InputEventBuffer<>();
	private /*@NonNull*/ InputEventBuffer<InputAnalogEvent>  triggerEventBuf = new InputEventBuffer<>();
	private /*@NonNull*/ InputEventBuffer<InputBooleanEvent> booleanEventBuf = new InputEventBuffer<>();

	private /*@NonNull*/ InputEventQueue  eventQueue = new InputEventQueue(32);

	private InputAnalogControl leftStick;
	private InputAnalogControl rightStick;
	private InputBooleanControl dpadUp;
	private InputBooleanControl dpadDown;
	private InputBooleanControl dpadLeft;
	private InputBooleanControl dpadRight;
	private InputBooleanControl a;
	private InputBooleanControl b;
	private InputBooleanControl x;
	private InputBooleanControl y;
	private InputBooleanControl guide;
	private InputBooleanControl start;
	private InputBooleanControl back;
	private InputBooleanControl leftBumper;
	private InputBooleanControl rightBumper;
	private InputBooleanControl leftTrigger;
	private InputBooleanControl rightTrigger;
	
	public GamepadDevice(String deviceName, Gamepad qualcommGamepad)
	{
		this.deviceName = deviceName;
		this.qualcommGamepad = qualcommGamepad;
		lastScanGamepad = new Gamepad();
		gamepadAssign(lastScanGamepad,qualcommGamepad);

		KeyCodes kcodes = KeyCodes.getDefaults();
		leftStick        = new InputAnalogControl(this, "Left Stick", 2);
		rightStick       = new InputAnalogControl(this, "Right Stick", 2);
		leftStickButton  = new InputBooleanControl(this, "Left Stick Button",  kcodes.ANDROID_BUTTON_THUMBL);
		rightStickButton = new InputBooleanControl(this, "Right Stick Button", kcodes.ANDROID_BUTTON_THUMBR);
		dpadUp           = new InputBooleanControl(this, "DPad UP",            kcodes.ANDROID_DPAD_UP);
		dpadDown         = new InputBooleanControl(this, "DPad DOWN",          kcodes.ANDROID_DPAD_DOWN);
		dpadLeft         = new InputBooleanControl(this, "DPad LEFT",          kcodes.ANDROID_DPAD_LEFT);
		dpadRight        = new InputBooleanControl(this, "DPad RIGHT",         kcodes.ANDROID_DPAD_RIGHT);
		a                = new InputBooleanControl(this, "Button A",           kcodes.ANDROID_BUTTON_A);
		b                = new InputBooleanControl(this, "Button B",           kcodes.ANDROID_BUTTON_B);
		x                = new InputBooleanControl(this, "Button X",           kcodes.ANDROID_BUTTON_X);
		y                = new InputBooleanControl(this, "Button Y",           kcodes.ANDROID_BUTTON_Y);
		// TODO: which is which?
		// The qualcomm docs provide 'guide' and 'back'.
		// The Logitech F310 provides 'mode' and 'back'.
		// The android.view.KeyEvent key codes provide 'mode' and 'select'.
		// So when the qualcomm API changes a button, which key code do we send?
		guide            = new InputBooleanControl(this, "Guide Button",       kcodes.ANDROID_BUTTON_SELECT);
		start            = new InputBooleanControl(this, "Start Button",       kcodes.ANDROID_BUTTON_START);
		back             = new InputBooleanControl(this, "Back Button",        kcodes.ANDROID_BACK);
		leftBumper       = new InputBooleanControl(this, "L1 - Left Bumper",   kcodes.ANDROID_BUTTON_L1);
		rightBumper      = new InputBooleanControl(this, "R1 - Right Bumper",  kcodes.ANDROID_BUTTON_R1);
		leftTrigger      = new InputAnalogControl(this, "L2 - Left Trigger",  1);
		rightTrigger     = new InputAnalogControl(this, "R2 - Right Trigger", 1);

		controls.ensureCapacity(17);
		controls.add(leftStick);
		controls.add(rightStick);
		controls.add(dpadUp);
		controls.add(dpadDown);
		controls.add(dpadLeft);
		controls.add(dpadRight);
		controls.add(a);
		controls.add(b);
		controls.add(x);
		controls.add(y);
		controls.add(guide);
		controls.add(start);
		controls.add(back);
		controls.add(leftBumper);
		controls.add(rightBumper);
		controls.add(leftTrigger);
		controls.add(rightTrigger);
	}

	// -------------------------------------------------------------------------
	//                    Implementation : IDevice
	// -------------------------------------------------------------------------
	/**
	 *  Does nothing.
	 *  <p>
	 *  This method exists to implement the IDevice interface requirements.
	 *  @see package org.avalancherobotics.standalone.interfaces.IDevice#close()
	 */
	@Override
	public void  close() {}

	/**
	 *  @see package org.avalancherobotics.standalone.interfaces.IDevice#getConnectionInfo()
	 */
	@Override
	public java.lang.String  getConnectionInfo()
	{
		return "Connection established through Qualcomm Gamepad java class.";
	}

	/**
	 *  @see package org.avalancherobotics.standalone.interfaces.IDevice#getDeviceName()
	 */
	public java.lang.String  getDeviceName()
	{
		return this.deviceName;
	}

	/**
	 *  @see package org.avalancherobotics.standalone.interfaces.IDevice#getVersion()
	 */
	public int  getVersion() { return 0; }

	// -------------------------------------------------------------------------
	//                    Implementation : IInputDevice
	// -------------------------------------------------------------------------

	/**
	 *  @see {@link org.avalancherobotics.standalone.interfaces.IInputDevice#getControls()}
	 */
	@Override
	public /*@NonNull*/ List</*@NonNull*/ InputControl> getControls()
	{
		return Collections.unmodifiableList(controls);
	}

	// Allocate it once, then reuse.
	private AtomicReference<IInputDevice.IEvent> eventRef = new AtomicReference<>();


	/**
	 *  Implements {@link org.avalancherobotics.standalone.interfaces.IInputDevice#fastForward()}.
	 *  <p>
	 *  This will call {@link #scanForEvents} before processing all events.
	 */
	@Override public void fastForward()
	{
		synchronized(this.syncObject)
		{
			scanForEventsNoSync();
			eventQueue.fastForward();
		}
	}

	/**
	 *  Implements {@link org.avalancherobotics.standalone.interfaces.IInputDevice#pollEvent}.
	 *  <p>
	 *  This will call {@link #scanForEvents} before processing the event.
	 */
	@Override public boolean pollEvent(/*@NonNull*/ AtomicReference<IInputDevice.IEvent> eventRef)
	{
		return pollEvent(eventRef, true);
	}

	/**
	 *  This is a version of {@link #pollEvent(AtomicReference)} that allows
	 *  the caller to decide whether {@link #scanForEvents} gets called (or not)
	 *  before event processing.
	 */
	public boolean pollEvent(/*@NonNull*/ AtomicReference<IInputDevice.IEvent> eventRef, boolean doScanFirst)
	{
		synchronized(this.syncObject)
			pollEventNoSync(eventRef,doScanFirst);
	}

	private boolean pollEventNoSync(/*@NonNull*/ AtomicReference<IInputDevice.IEvent> eventRef, boolean doScanFirst)
	{
		if ( doScanFirst )
			scanForEventsNoSync();

		return eventQueue.pollEvent(eventRef);
	}

	/**
	 *  The size of the current event queue.
	 *  @return the size of the event queue, which is how many times
	 *    {@link #pollEvent} can be called before it will return false,
	 *    as well as one greater than the maximum allowed <i>which</i> value
	 *    supplied to {@link #peekEvent}.
	 */
	public int eventQueueSize()
	{
		synchronized(this.syncObject)
			return eventQueue.eventQueueSize();
	}

	/**
	 *  Implements {@link org.avalancherobotics.standalone.interfaces.IInputDevice#peekEvent}.
	 *  <p>
	 *  Unlike pollEvent's default behavior, this does not call {@link #scanForEvents}.
	 *  This makes it possible to inspect the event queue without mutating it.
	 *  <p>
	 *  If the caller expects the absolutely most up-to-date list of events,
	 *  then they should call {@link #scanForEvents} right before calling this.
	 */
	@Override public boolean peekEvent(/*@NonNull*/ AtomicReference<IInputDevice.IEvent> eventRef, int which)
	{
		synchronized(this.syncObject)
			return eventQueue.peekEvent(eventRef, which);
	}

	// -------------------------------------------------------------------------
	//                        Integration/Internals
	// -------------------------------------------------------------------------

	/** Copy the state of one gamepad into another */
	public static void gamepadAssign(Gamepad dst, Gamepad src)
	{
		dst.left_stick_x = src.left_stick_x;
		dst.left_stick_y = src.left_stick_y;
		dst.right_stick_x = src.right_stick_x;
		dst.right_stick_y = src.right_stick_y;
		dst.left_stick_button = src.left_stick_button;
		dst.right_stick_button = src.right_stick_button;
		dst.dpad_up = src.dpad_up;
		dst.dpad_down = src.dpad_down;
		dst.dpad_left = src.dpad_left;
		dst.dpad_right = src.dpad_right;
		dst.a = src.a;
		dst.b = src.b;
		dst.x = src.x;
		dst.y = src.y;
		dst.guide = src.guide;
		dst.start = src.start;
		dst.back = src.back;
		dst.left_bumper = src.left_bumper;
		dst.right_bumper = src.right_bumper;
		dst.left_trigger = src.left_trigger;
		dst.right_trigger = src.right_trigger;
		dst.user = src.user;
		dst.id = src.id;
		dst.timestamp = src.timestamp;
	}

	/**
	 *  Compares the qualcomm gamepad updated by the qualcomm OpMode object
	 *  against the reference state stored in the GamepadDevice object, and
	 *  converts any differences into queued InputEvent objects.
	 *  <p>
	 *  This will be called any time 
	 *  It may be useful to call this whenever the gamepad's callback is called.
	 */
	public final int scanForEvents()
	{
		synchronized(this.syncObject)
			return scanForEventsNoSync();
	}

	private int scanForEventsNoSync()
	{
		return scanForEvents(lastScanGamepad, qualcommGamepad);
	}

	private int scanForEventsNoSync(Gamepad last, Gamepad next)
	{
		int nEvents = 0;

		// TODO: Is the com.qualcomm.robotcore.hardware.Gamepad.timestamp
		//   field actually stored in milliseconds since Unix epoch, or is
		//   it some other kind of timestamp?  The documentation doesn't
		//   explain.  It even calls it "relative" (to what?).  If it turns
		//   out not to be trustworthy, then the timestamp value used should
		//   be obtained from System.currentTimeMillis(), possible with some
		//   way for the caller to provide frame-specific (lower granularity)
		//   values incase the system call is expensive.


		// ----- 2D Analog Events -----
		if (last.left_stick_x != next.left_stick_x
		||  last.left_stick_y != next.left_stick_y)
		{
			queueStickEvent(leftStick, next.timestamp,
				last.left_stick_x, last.left_stick_y,
				next.left_stick_x, next.left_stick_y);
			nEvents++;
		}

		if (last.right_stick_x != next.right_stick_x
		||  last.right_stick_y != next.right_stick_y)
		{
			queueStickEvent(rightStick, next.timestamp,
				last.right_stick_x, last.right_stick_y,
				next.right_stick_x, next.right_stick_y);
			nEvents++;
		}

		// ----- 1D Analog Events -----
		if (last.left_trigger != next.left_trigger)
		{
			queueTriggerEvent(leftTrigger, next.timestamp,
				last.left_trigger, next.left_trigger);
			nEvents++;
		}

		if (last.right_trigger != next.right_trigger)
		{
			queueTriggerEvent(rightTrigger, next.timestamp,
				last.right_trigger, next.right_trigger);
			nEvents++;
		}

		// ----- Boolean Events -----
		nEvents += scanButton(leftStickButton,  next.timestamp,  last.left_stick_button,   next.left_stick_button)  ? 1 : 0;
		nEvents += scanButton(rightStickButton, next.timestamp,  last.right_stick_button,  next.right_stick_button) ? 1 : 0;
		nEvents += scanButton(dpadUp,           next.timestamp,  last.dpad_up,             next.dpad_up)            ? 1 : 0;
		nEvents += scanButton(dpadDown,         next.timestamp,  last.dpad_down,           next.dpad_down)          ? 1 : 0;
		nEvents += scanButton(dpadLeft,         next.timestamp,  last.dpad_left,           next.dpad_left)          ? 1 : 0;
		nEvents += scanButton(dpadRight,        next.timestamp,  last.dpad_right,          next.dpad_right)         ? 1 : 0;
		nEvents += scanButton(a,                next.timestamp,  last.a,                   next.a)                  ? 1 : 0;
		nEvents += scanButton(b,                next.timestamp,  last.b,                   next.b)                  ? 1 : 0;
		nEvents += scanButton(x,                next.timestamp,  last.x,                   next.x)                  ? 1 : 0;
		nEvents += scanButton(y,                next.timestamp,  last.y,                   next.y)                  ? 1 : 0;
		nEvents += scanButton(guide,            next.timestamp,  last.guide,               next.guide)              ? 1 : 0;
		nEvents += scanButton(start,            next.timestamp,  last.start,               next.start)              ? 1 : 0;
		nEvents += scanButton(back,             next.timestamp,  last.back,                next.back)               ? 1 : 0;
		nEvents += scanButton(leftBumper,       next.timestamp,  last.left_bumper,         next.left_bumper)        ? 1 : 0;
		nEvents += scanButton(rightBumper,      next.timestamp,  last.right_bumper,        next.right_bumper)       ? 1 : 0;
		// TODO: Events for user/id changes?
		//if (last.user != next.user) return false;
		//if (last.id != next.id) return false;

		gamepadAssign(last,next);
		return nEvents;
	}

	private boolean scanButton(
		InputBooleanControl button,
		long    timestamp,
		boolean oldState,
		boolean newState)
	{
		if ( oldState && !newState )
		{
			queueButtonEvent(button, timestamp, InputBooleanEvent.Type.RELEASE);
			return true;
		}
		else if ( !oldState && newState )
		{
			queueButtonEvent(button, timestamp, InputBooleanEvent.Type.PRESS);
			return true;
		}
		else
			return false;
	}

	private void queueTriggerEvent(
		InputAnalogControl trigger,
		long timestamp,
		double oldX, double newX
		)
	{
		InputEventQueue.queueAnalogEvent(
			stickEventBuf, trigger, timestamp, oldX, newX);
	}
	
	private void queueStickEvent(
		InputAnalogControl stick,
		long timestamp,
		double oldX, double oldY,
		double newX, double newY
		)
	{
		InputEventQueue.queueAnalogEvent(
			stickEventBuf, stick, timestamp,
			oldX, oldY,
			newX, newY);
	}

	private void queueButtonEvent(
		InputBooleanControl    button,
		long                   timestamp,
		InputBooleanEvent.Type eventType )
	{
		InputEventQueue.queueAnalogEvent(
			booleanEventBuf, button, timestamp, eventType);
	}
}
