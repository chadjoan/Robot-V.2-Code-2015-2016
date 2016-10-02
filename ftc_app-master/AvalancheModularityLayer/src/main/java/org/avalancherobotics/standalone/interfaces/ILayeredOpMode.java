package org.avalancherobotics.standalone.interfaces;

// Implementation NOTE: The standalone package shall never depend on the
//   org.avalancherobotics.library package, the org.avalancherobotics.desktop package,
//   or any other implementors.  It should also use only Java code that will
//   be found on all platforms (ex: Android + Desktop), which means that only
//   things in java.util are remotely safe.
//

/**
 *  An interface for OpModes, allowing the creation of OpModes for different
 *  host platforms (ex: Android vs Desktop).
 */
public interface ILayeredOpMode
{
	/**
	 *  Exposes the list of (pollable) input devices known to the ILayeredOpMode.
	 */
	public /*@NonNull*/ IInputDeviceSet getInputDeviceSet();

	/**
	 *  Exposes the registry of all devices known to the ILayeredOpMode.
	 */
	public /*@NonNull*/ IDeviceRegistry getDeviceRegistry();

	// Earlier API design that didn't make the cut and should be removed in
	// a cleanup commit at some point:
	/*
	 *  Exposes the list of input devices known to the ILayeredOpMode.
	 *  <p>
	 *  The implementor shall ensure that the states of these devices remains
	 *  the same between calls to {@link #updateInputs()}.
	 */
	//public /*@NonNull*/ List</*@NonNull*/ IInputDevice> getInputDevices();

	/*
	 *  Updates all input devices.
	 *  <p>
	 *  If any of the input devices use an asynchronous event-based API, then
	 *  the implementor of the ILayeredOpMode shall accumulate any such events
	 *  and actualize them only when updateInputs() is called.
	 */
	//public boolean updateInputs();

	/*
	 *  Returns a list of all input events accumulated since the last call to
	 *  {@link #updateInputs()}.  If updateInputs() has not been called, then
	 *  this shall return all events since the ILayeredOpMode's construction.
	 *  <p>
	 *  If no input events have occured, then this shall return a zero-length
	 *  list.  pollInputs() shall never return null.
	 *  <p>
	 *  When calling this method, never store the returned IInputDevice.IEvent
	 *  objects beyond the next call to {@link #updateInputs()}.
	 *  Any IInputDevice.IEvent objects still alive at the time of a call to
	 *  {@link #updateInputs()} may be reused by the event polling logic.
	 *  This may be done to avoid excessive heap allocations and garbage
	 *  collection activity.
	 */
	//public /*@NonNull*/ List</*@NonNull*/ IInputDevice.IEvent> pollInputs();
}
