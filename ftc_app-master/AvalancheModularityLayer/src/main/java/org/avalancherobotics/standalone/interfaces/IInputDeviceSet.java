package org.avalancherobotics.standalone.interfaces;

// Implementation NOTE: The standalone package shall never depend on the
//   org.avalancherobotics.library package, the org.avalancherobotics.desktop package,
//   or any other implementors.  It should also use only Java code that will
//   be found on all platforms (ex: Android + Desktop), which means that only
//   things in java.util are remotely safe.
//

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 *  Describes an input device set, which aggregates events from other
 *  input devices.
 *  <p>
 *  It defines the features of an IInputDevice, but provides the additional
 *  ability to enumerate the devices that are aggregated by this tree.
 */
public interface IInputDeviceSet extends IInputDevice
{
	/**
	 *  A list of devices that feed input events into this device.
	 */
	public /*@NonNull*/ List</*@NonNull*/ IInputDevice> getSourceDevices();

	/**
	 *  A list of all controls (buttons, keys, sticks, etc) found on all
	 *  source input devices.
	 *  <p>
	 *  This is the union of all controls from all devices.  It may contain
	 *  things like buttons with duplicate keycodes, but such duplicates will
	 *  always have differing .getDevice() results.
	 *  <p>
	 *  @see org.avalancherobotics.standalone.interfaces.IInputDevice#getControls()
	 */
	@Override
	public /*@NonNull*/ List</*@NonNull*/ IControl> getControls();

	/**
	 *  Calls {@link org.avalancherobotics.standalone.interfaces.IInputDevice#fastForward}
	 *  on all source input devices.
	 *
	 *  @see org.avalancherobotics.standalone.interfaces.IInputDevice#fastForward()
	 */
	@Override
	public void fastForward();

	/**
	 *  Processes the earliest event queued by any of the source devices,
	 *  and then places a reference to that event into the <i>eventRef</i>
	 *  parameter.
	 *
	 *  @see org.avalancherobotics.standalone.interfaces.IInputDevice#pollEvent
	 */
	@Override
	public boolean pollEvent(/*@NonNull*/ AtomicReference<IEvent> eventRef);

	/**
	 *  Returns the sum of the sizes of all event queues owned by
	 *  source devices.
	 *  <p>
	 *  Note that if eventQueueSize is being compared to 0 to determine
	 *  if the IInputDeviceSet's "queue" is empty, then this operation
	 *  can be done potentially more efficiently by calling
	 *  {@link #peekEvent} with a 'which' parameter of 0 and inspecting
	 *  the return value.
	 *
	 *  @see org.avalancherobotics.standalone.interfaces.IInputDevice#eventQueueSize
	 */
	@Override
	public int eventQueueSize();

	/**
	 *  Performs the same operation as
	 *  {@link org.avalancherobotics.standalone.interfaces.IInputDevice#peekEvent},
	 *  but across all source input devices.
	 *  <p>
	 *  This effectively merge-sorts the events from all source input devices
	 *  so that increasing values of <i>which</i> will provide successively
	 *  more recent events.  A <i>which</i> value of 0 will produce the
	 *  earliest event available in any of the source input device queues,
	 *  which is what would be processed if {@link pollEvent} were called
	 *  at that point in time.  A <i>which</i> value of
	 *  {@link #eventQueueSize()}-1 will return the most recent event in
	 *  any of the source input device queues.
	 *  <p>
	 *  Note that not all implementations will efficiently peek when (<i>which</i>&lt0).
	 *  They may allocate more memory than normal and may initiate algorithms
	 *  with time costs greater than O(n), such as sorting algorithms.
	 *  <p>
	 *  Implementations should optimize the case where (<i>which</i>==0) and
	 *  simply iterate over the source input devices while calling peekEvent(ref,0)
	 *  without allocating any memory for data structures, much like what
	 *  would be done for implementing {@link #pollEvent}.
	 *
	 *  @see org.avalancherobotics.standalone.interfaces.IInputDevice#peekEvent
	 */
	@Override
	public boolean peekEvent(/*@NonNull*/ AtomicReference<IEvent> eventRef, int which);
}
