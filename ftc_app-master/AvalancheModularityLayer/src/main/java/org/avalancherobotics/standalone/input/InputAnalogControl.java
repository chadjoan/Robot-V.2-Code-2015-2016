package org.avalancherobotics.standalone.input;

// Implementation NOTE: The standalone package shall never depend on the
//   org.avalancherobotics.library package, the org.avalancherobotics.desktop package,
//   or any other implementors.  It should also use only Java code that will
//   be found on all platforms (ex: Android + Desktop), which means that only
//   things in java.util are remotely safe.
//

import org.avalancherobotics.standalone.interfaces.IInputDevice;

/** Describes things like joysticks, analog buttons, continuous knobs, mouse
 *  positions, and other controls best represented by one or more (double)
 *  floating point values.
 */
public class InputAnalogControl extends InputControl
{
	private int degreesOfFreedom;
	private double[] coords;
	
	/** */
	public InputAnalogControl(
		/*@NonNull*/ IInputDevice device,
		/*@NonNull*/ String name,
		int degreesOfFreedom)
	{
		super(device, name);
		this.degreesOfFreedom = degreesOfFreedom;
		this.coords = new double[degreesOfFreedom];
		for ( int i = 0; i < degreesOfFreedom; i++ )
			this.coords[i] = Double.NaN;
	}

	/** The number of analog values affected by this control.
	 *  <p>
	 *  For example, a joystick with 2 axes would have 2 degrees of freedom.
	 *  <p>
	 *  An analog button would have 1 degree of freedom.
	 */
	public int getDegreesOfFreedom() { return this.degreesOfFreedom; }

	/**
	 *  Reads coordinates from the control, by axis number.
	 */
	public double getCoord(int axis) { return coords[axis]; }

	/**
	 *  Writes coordinates to the control, by axis number.
	 */
	public void setCoord(double value, int axis) { coords[axis] = value; }
}
