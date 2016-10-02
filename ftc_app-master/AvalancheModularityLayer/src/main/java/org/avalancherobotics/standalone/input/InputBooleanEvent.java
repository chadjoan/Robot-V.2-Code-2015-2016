package org.avalancherobotics.standalone.input;

// Implementation NOTE: The standalone package shall never depend on the
//   org.avalancherobotics.library package, the org.avalancherobotics.desktop package,
//   or any other implementors.  It should also use only Java code that will
//   be found on all platforms (ex: Android + Desktop), which means that only
//   things in java.util are remotely safe.
//

import org.avalancherobotics.standalone.internal.UnimplementedPossibilityException;

/**
 *  Describes a single digital (boolean) input event, such as a key press,
 *  button press, or anything else that toggles a boolean control between
 *  its on or off states.
 */
public class InputBooleanEvent extends InputEvent
{
	/** */
	public enum Type
	{
		PRESS,
		RELEASE
	}

	private Type type;
	
	/** Whether the key/button event was a press or release. */
	public Type getType() { return type; }
	
	/** Whether the key/button event was a press or release. */
	public void setType(Type value) { type = value; }
	
	/** */
	public int getKeyCode() { return this.getControl().getKeyCode(); }

	/**
	 *  The control used and modified in the event.
	 *  <p>
	 *  This method returns the same control returned by
	 *  InputEvent.getControl(), but typed as the more specific
	 *  InputBooleanControl.
	 */
	@Override public InputBooleanControl getControl()
	{
		InputControl control = super.getControl();
		if ( control instanceof InputBooleanControl )
			return (InputBooleanControl)control;
		else
			throw new ClassCastException(
				"A boolean input event originated from a non-boolean control of type "+
				control.getClass().getName());
	}

	@Override public void setControl(/*@NonNull*/ InputControl control)
	{
		if ( !(control instanceof InputBooleanControl) )
			throw new IllegalArgumentException("The control parameter must be an InputBooleanControl class. "+
				"Instead, a(n) "+ control.getClass().getName()+ " was passed.");
		else
			super.setControl(control);
	}

	/**
	 *  Changes the control's state to what it should be after the event
	 *  occurs.
	 */
	@Override public void applyToControl()
	{
		switch(type)
		{
			case PRESS:
				this.getControl().setState(InputBooleanControl.State.PRESSED);
				break;

			case RELEASE:
				this.getControl().setState(InputBooleanControl.State.RELEASED);
				break;

			default:
				throw new UnimplementedPossibilityException(
					"InputBooleanEvent type "+ type +" is not implemented here.");
		}
	}
}

