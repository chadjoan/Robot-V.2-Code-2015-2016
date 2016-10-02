package org.avalancherobotics.desktop.input;

import java.util.ArrayList;
import java.util.List;

import java.awt.MouseInfo;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import org.avalancherobotics.standalone.interfaces.IInputDevice;
import org.avalancherobotics.standalone.input.InputAnalogControl;
import org.avalancherobotics.standalone.input.InputAnalogEvent;
import org.avalancherobotics.standalone.input.InputBooleanControl;
import org.avalancherobotics.standalone.input.InputBooleanEvent;
import org.avalancherobotics.standalone.input.InputControl;
import org.avalancherobotics.standalone.input.InputEvent;
import org.avalancherobotics.standalone.input.InputEventQueue;
import org.avalancherobotics.standalone.input.KeyCodes;
import org.avalancherobotics.standalone.input.QueuedInputDevice;

import org.avalancherobotics.desktop.input.InputDeviceCommon;

/**
 *  An input device based on receiving MouseEvents from the
 *  {@link java.awt.event.MouseListener} and
 *  {@link java.awt.event.MouseMotionListener} interfaces.
 *  @see org.avalancherobotics.standalone.interfaces.IInputDevice
 */
public class MouseDevice extends InputDeviceCommon
	implements IInputDevice, MouseListener, MouseMotionListener
{
	// Be sure these event buffers are only ever accessed from within a
	// synchronized(this.getSychronizationObject()) block.
	private /*@NonNull*/ InputEvent.Buffer<InputBooleanEvent> booleanEventBuf = new InputEvent.Buffer<>(InputBooleanEvent.class);
	private /*@NonNull*/ InputEvent.Buffer<InputAnalogEvent>  analogEventBuf  = new InputEvent.Buffer<>(InputAnalogEvent.class);

	private int pointerOldX = 0;
	private int pointerOldY = 0;
	private /*@NonNull*/ InputAnalogControl    pointer;
	// We don't know how many buttons the mouse has, so don't bother making
	// an array of mouse button controls.  Just let them fill up the
	// super class's dynamic control list as-needed.

	/** */
	public MouseDevice(/*@NonNull*/ String deviceName)
	{
		super(deviceName);
		KeyCodes keyCodes = KeyCodes.getDefaults();
		pointer = new InputAnalogControl(this, "Mouse Pointer", 2);
		// TODO: It should probably get button counts as below, but I don't
		//   feel like implementing that event registration right now.
		//
		//int nButtons = ??;
		//try
		//	nButtons = MouseInfo.getNumberOfButtons();
		//catch ( Exception e )
		//	assert(true);
		// ????
		//
		// NOTE: Don't do
		//   nButtons = Math.max(MouseInfo.getNumberOfButtons(),
		//                       KeyCodes.getDefaults().MOUSE_BUTTONS.length)
		// Because there is no guarantee that the physical mouse actually has
		// all of the buttons specified by KeyCodes.getDefaults().MOUSE_BUTTONS.
		// Putting in too many InputBooleanControl objects that way could cause
		// iteration of the controls to present untrue information (ex:
		// telling the user that buttons exist when they don't).

		super.setControls(emptyControlsList,new ArrayList</*@NonNull*/ InputControl>(0));
	}

	/** */
	public /*@NonNull*/ InputAnalogControl getPointer()
	{
		return this.pointer;
	}

	// -------------------------------------------------------------------------
	//                    Implementation : IDevice
	// -------------------------------------------------------------------------
	/** Get connection information about this device in a human readable format */
	public String  getConnectionInfo()
	{
		return "MouseDevice receiving MouseEvents by implementing the "+
			"java.awt.event.MouseListener and java.awt.event.MouseMotionListner interfaces.";
	}

	// -------------------------------------------------------------------------
	//                    Implementation : MouseListener
	// -------------------------------------------------------------------------
	/** Invoked when the mouse button has been clicked (pressed and released) on a component. */
	public void  mouseClicked(MouseEvent e) {}

	/** Invoked when the mouse enters a component. */
	public void  mouseEntered(MouseEvent e) {}

	/** Invoked when the mouse exits a component. */
	public void  mouseExited(MouseEvent e) {}

	/** Invoked when a mouse button has been pressed on a component. */
	public void  mousePressed(MouseEvent e)
	{
		queueButtonEvent(e, InputBooleanEvent.Type.PRESS);
	}

	/** Invoked when a mouse button has been released on a component. */
	public void  mouseReleased(MouseEvent e)
	{
		queueButtonEvent(e, InputBooleanEvent.Type.RELEASE);
	}

	// -------------------------------------------------------------------------
	//                    Implementation : MouseMotionListener
	// -------------------------------------------------------------------------
	/** Invoked when a mouse button is pressed on a component and then dragged. */
	public void  mouseDragged(MouseEvent e) {}

	/** Invoked when the mouse cursor has been moved onto a component but no buttons have been pushed. */
	public void  mouseMoved(MouseEvent e)
	{
		queueMouseMotionEvent(e);
	}

	private void queueButtonEvent(
		MouseEvent e,
		InputBooleanEvent.Type eventType )
	{
		// Convert the button number into a key code.
		KeyCodes keyCodes = KeyCodes.getDefaults();
		int keyCode = 0;
		int whichButton = e.getButton();
		if ( whichButton < keyCodes.MOUSE_BUTTONS.length )
			keyCode = keyCodes.MOUSE_BUTTONS[whichButton];

		// Lazily generate InputBooleanControl objects for keys as soon
		// as they are encountered.
		InputBooleanControl button = super.ensureControlForBooleanEvent(keyCode);

		// Now that we have an InputBooleanControl object, we can continue
		// the queueing process.
		long unixTime = System.currentTimeMillis() / 1000L;
		this.getEventQueue().queueBooleanEvent(
			booleanEventBuf, button, unixTime, eventType);
	}

	private void queueMouseMotionEvent( MouseEvent e )
	{
		int pointerNewX = e.getXOnScreen();
		int pointerNewY = e.getYOnScreen();

		long unixTime = System.currentTimeMillis() / 1000L;

		this.getEventQueue().queueAnalogEvent(
			analogEventBuf, pointer, unixTime,
			this.pointerOldX, this.pointerOldY,
			pointerNewX,      pointerNewY);

		this.pointerOldX = pointerNewX;
		this.pointerOldY = pointerNewY;
	}

	/**
	 *  A list of all buttons that have been pressed on the mouse since it
	 *  was instantiated, plus the expected 2-axis analog control.
	 *
	 *  @see org.avalancherobotics.standalone.interfaces.IInputDevice#getControls
	 */
	@Override
	public /*@NonNull*/ List</*@NonNull*/ ? extends IInputDevice.IControl> getControls()
	{
		return super.getControls();
	}
}
