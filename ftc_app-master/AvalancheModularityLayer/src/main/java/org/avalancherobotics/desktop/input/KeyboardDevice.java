package org.avalancherobotics.desktop.input;

import java.util.ArrayList;
import java.util.List;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import org.avalancherobotics.standalone.interfaces.IInputDevice;
import org.avalancherobotics.standalone.input.InputBooleanControl;
import org.avalancherobotics.standalone.input.InputBooleanEvent;
import org.avalancherobotics.standalone.input.InputControl;
import org.avalancherobotics.standalone.input.InputEvent;
import org.avalancherobotics.standalone.input.InputEventQueue;
import org.avalancherobotics.standalone.input.QueuedInputDevice;

import org.avalancherobotics.desktop.input.InputDeviceCommon;

/**
 *  An input device based on receiving KeyEvents from the
 *  {@link java.awt.event.KeyListener} interface.
 *  @see org.avalancherobotics.standalone.interfaces.IInputDevice
 */
public class KeyboardDevice extends InputDeviceCommon implements IInputDevice, KeyListener
{
	// Be sure this event buffer is only ever accessed from within a
	// synchronized(this.getSychronizationObject()) block.
	private /*@NonNull*/ InputEvent.Buffer<InputBooleanEvent>
		booleanEventBuf = new InputEvent.Buffer<>(InputBooleanEvent.class);

	/** */
	public KeyboardDevice(/*@NonNull*/ String deviceName)
	{
		super(deviceName);

		// There are currently no constant controls for the keyboard.
		// Maybe someday someone will figure out which keys are guaranteed to exist
		// on all keyboards that possibly work with java.awt and then add them to
		// this list.  Maybe.
		super.setControls(emptyControlsList,new ArrayList</*@NonNull*/ InputControl>(0));
	}

	// -------------------------------------------------------------------------
	//                    Implementation : IDevice
	// -------------------------------------------------------------------------
	/** Get connection information about this device in a human readable format */
	public String  getConnectionInfo()
	{
		return "KeyboardDevice receiving KeyEvents by implementing the java.awt.event.KeyListener interface.";
	}

	// -------------------------------------------------------------------------
	//                    Implementation : KeyListener
	// -------------------------------------------------------------------------
	/** Invoked when a key has been pressed. */
	public void  keyPressed(KeyEvent e)
	{
		synchronized(this.getSynchronizationObject()) {
			queueKeyEvent(e, InputBooleanEvent.Type.PRESS);
		}
	}

	/** Invoked when a key has been released. */
	public void  keyReleased(KeyEvent e)
	{
		synchronized(this.getSynchronizationObject()) {
			queueKeyEvent(e, InputBooleanEvent.Type.RELEASE);
		}
	}

	private void queueKeyEvent(
		KeyEvent e,
		InputBooleanEvent.Type eventType )
	{
		// Lazily generate InputBooleanControl objects for keys as soon
		// as they are encountered.
		InputBooleanControl key = super.ensureControlForBooleanEvent(e.getKeyCode());

		// Now that we have an InputBooleanControl object, we can continue
		// the queueing process.
		long unixTime = System.currentTimeMillis() / 1000L;
		this.getEventQueue().queueBooleanEvent(
			booleanEventBuf, key, unixTime, eventType);
	}

	/** Invoked when a key has been typed. */
	public void  keyTyped(KeyEvent e) {}

	// -------------------------------------------------------------------------
	//                    Implementation : IInputDevice
	// -------------------------------------------------------------------------
	/**
	 *  A list of all keys that have been pressed on the keyboard since it
	 *  was instantiated.
	 *
	 *  @see org.avalancherobotics.standalone.interfaces.IInputDevice#getControls
	 */
	@Override
	public /*@NonNull*/ List</*@NonNull*/ ? extends IInputDevice.IControl> getControls()
	{
		return super.getControls();
	}
}
