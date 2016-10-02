package org.avalancherobotics.standalone.input;

// Implementation NOTE: The standalone package shall never depend on the
//   org.avalancherobotics.library package, the org.avalancherobotics.desktop package,
//   or any other implementors.  It should also use only Java code that will
//   be found on all platforms (ex: Android + Desktop), which means that only
//   things in java.util are remotely safe.
//

import org.avalancherobotics.standalone.interfaces.IInputDevice;

/**
 *  Describes a feature, such as a button or a stick, found on an input device.
 */
public abstract class InputControl implements IInputDevice.IControl
{
	private /*@NonNull*/ IInputDevice device;
	private /*@NonNull*/ String name;
	
	/** */
	public InputControl(/*@NonNull*/ IInputDevice device, /*@NonNull*/ String name)
	{
		this.device = device;
		this.name = name;
		if ( device == null )
			throw new IllegalArgumentException("The device parameter must be non-null.");
		if ( name == null )
			throw new IllegalArgumentException("The name parameter must be non-null.");
	}
	
	/** */
	@Override public /*@NonNull*/ IInputDevice getDevice() { return this.device; }

	/** */
	@Override public /*@NonNull*/ String getName() { return this.name; }

	/** */
	@Override public String toString()
	{
		return this.name;
	}
}
