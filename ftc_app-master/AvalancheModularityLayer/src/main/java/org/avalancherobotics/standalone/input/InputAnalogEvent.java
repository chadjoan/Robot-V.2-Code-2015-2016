package org.avalancherobotics.standalone.input;

// Implementation NOTE: The standalone package shall never depend on the
//   org.avalancherobotics.library package, the org.avalancherobotics.desktop package,
//   or any other implementors.  It should also use only Java code that will
//   be found on all platforms (ex: Android + Desktop), which means that only
//   things in java.util are remotely safe.
//

/**
 *  Describes a single analog input event, such as a mouse movement, a joystick
 *  movement, or movement on an analog button.
 *  <p>
 *  Not all analog controls have the same number of degrees of freedom, so
 *  the returned values will have a different array size depending on the
 *  .getControl().getDegreesOfFreedom() value.
 *  <p>
 *  By convention, the first element in these arrays is the X axis, the second
 *  element is the Y axis, and the third is the Z axis.
 */
public class InputAnalogEvent extends InputEvent
{
	private /*@NonNull*/ double[] oldValues;
	private /*@NonNull*/ double[] newValues;
	
	/** */
	public InputAnalogEvent(int degreesOfFreedom)
	{
		super();

		this.oldValues = new double[degreesOfFreedom];
		this.newValues = new double[degreesOfFreedom];
		nanify();
	}

	/**
	 *  The values in each axis before the event.
	 *  <p>
	 *  Index these arrays with values from org.avalancherobotics.standalone.Axis
	 *  to make them more readable.
	 *  <p>
	 *  Example:
	 *  <pre>
	 *  {@code
	 *  double x = event.getOldValue(Axis.X);
	 *  double y = event.getOldValue(Axis.Y);
	 *  }
	 *  </pre>
	 */
	public final double getOldValue(int axis) { return oldValues[axis]; }

	/**
	 *  The values in each axis after the event.
	 *  <p>
	 *  Index these arrays with values from org.avalancherobotics.standalone.Axis
	 *  to make them more readable.
	 *  <p>
	 *  Example:
	 *  <pre>
	 *  {@code
	 *  double x = event.getNewValue(Axis.X);
	 *  double y = event.getNewValue(Axis.Y);
	 *  }
	 *  </pre>
	 */
	public final double getNewValue(int axis) { return newValues[axis]; }

	/** This should only be called by the code generating the event. */
	public final void setOldValue(double value, int axis)
	{
		this.oldValues[axis] = value;
	}

	/** This should only be called by the code generating the event. */
	public final void setNewValue(double value, int axis)
	{
		this.newValues[axis] = value;
	}

	/**
	 *  The control used and modified in the event.
	 *  <p>
	 *  This method returns the same control returned by
	 *  InputEvent.getControl(), but typed as the more specific
	 *  InputAnalogControl.
	 */
	@Override public InputAnalogControl getControl()
	{
		InputControl control = super.getControl();
		if ( control instanceof InputAnalogControl )
			return (InputAnalogControl)control;
		else
			throw new ClassCastException(
				"An analog input event originated from a non-analog control of type "+
				control.getClass().getName());
	}
	
	/** */
	@Override public void setControl(/*@NonNull*/ InputControl control)
	{
		if ( !(control instanceof InputAnalogControl) )
			throw new IllegalArgumentException("The control parameter must be an InputAnalogControl class. "+
				"Instead, a(n) "+ control.getClass().getName()+ " was passed.");

		int oldDof = this.getControl().getDegreesOfFreedom();
		super.setControl(control);
		
		int newDof = ((InputAnalogControl)control).getDegreesOfFreedom();
		if ( oldDof != newDof )
		{
			this.oldValues = new double[newDof];
			this.newValues = new double[newDof];
		}

		// Whenever the control changes, the coordinates no longer have meaning.
		// To obtain consistent behavior, clear all coordinate values.
		nanify();
	}

	private void nanify()
	{
		int dof = newValues.length;
		for ( int i = 0; i < dof; i++ )
		{
			this.oldValues[i] = Double.NaN;
			this.newValues[i] = Double.NaN;
		}
	}

	/**
	 *  Changes the control's state to what it should be after the event
	 *  occurs.
	 */
	@Override public void applyToControl()
	{
		InputAnalogControl control = this.getControl();
		int dof = newValues.length;
		for ( int axis = 0; axis < dof; axis++ )
			control.setCoord(this.newValues[axis],axis);
	}
}
