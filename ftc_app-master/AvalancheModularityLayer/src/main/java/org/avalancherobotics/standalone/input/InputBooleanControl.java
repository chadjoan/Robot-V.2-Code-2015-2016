package org.avalancherobotics.standalone.input;

// Implementation NOTE: The standalone package shall never depend on the
//   org.avalancherobotics.library package, the org.avalancherobotics.desktop package,
//   or any other implementors.  It should also use only Java code that will
//   be found on all platforms (ex: Android + Desktop), which means that only
//   things in java.util are remotely safe.
//

import org.avalancherobotics.standalone.interfaces.IInputDevice;

/** Describes things like keys or digital buttons: controls that have only two
 *  states, such as pressed or released.
 */
public class InputBooleanControl extends InputControl
{
	/** */
	public enum State
	{
		PRESSED,
		RELEASED
	}

	private int keyCode;
	private State state;
	
	/** */
	public InputBooleanControl( /*@NonNull*/ IInputDevice device, int keyCode )
	{
		super(device, KeyCodes.getDefaults().getNamesAsStr(keyCode));
		this.keyCode = keyCode;
		this.state = InputBooleanControl.State.RELEASED;
	}

	/** The key code that this control is responsible for. */
	public final int getKeyCode() { return keyCode; }

	/** The key code that this control is responsible for. */
	public final void setKeyCode(int value) { keyCode = value; }

	/** Whether the button is pressed or released. */
	public State getState() { return state; }

	/** Whether the button is pressed or released. */
	public void setState(State value) { state = value; }
}