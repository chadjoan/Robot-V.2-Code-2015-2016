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
 *  Describes an input device, such as a keyboard, mouse, or game controller.
 *  <p>
 *  This interface has two distinct features:<ol>
 *  <li>A list of <b>controls</b> that describe the <i>retained</i> state of
 *      the device (ex: which keys/buttons are pressed, where cursors are
 *      positioned).
 *  <li>The ability to retrieve <b>events</b> that describe changes in device's
 *      <i>physical state</i> while optionally writing those changes into the
 *      IInputDevice's <i>retained state</i>.
 *  </ol>
 *  This duality allows code that processes events to see what the input device
 *  looked like at the time that the event happened.
 *  <p>
 *  Note that if the interface were to provide the device's current <i>physical
 *  state</i> at all times instead of its <i>retained state</i>, then it would
 *  be possible to get information from events that conflicts with information
 *  from controls.  For example, an event consumer might see an event for the
 *  "p" key being pressed, then look at the control for the "p" key and see
 *  that it is currently released (because a later event, still queued, is
 *  for the release of the "p" key).  To avoid these kinds of paradoxes,
 *  IInputDevice guarantees that the state it publishes will be the state of
 *  the device at the time of the last event polled: so if an event shows that
 *  the "p" key is pressed, then the "p" key will show pressed.
 */
public interface IInputDevice extends IDevice
{
	/**
	 *  Describes a feature, such as a button or a stick, found on an input device.
	 */
	public interface IControl
	{
		/** */
		public /*@NonNull*/ IInputDevice getDevice();

		/** */
		public /*@NonNull*/ String getName();
	}

	/**
	 *  Describes a single input event, such as a key press, mouse motion,
	 *  or joystick motion.
	 */
	public interface IEvent
	{
		/** The input device that generated the event. */
		public /*@NonNull*/ IInputDevice getDevice();

		/** The control used and modified in the event.
		*  <p>
		*  This returns the same value as {@link #getControl()}.  If you are
		*  working with an InputEvent instance, then it is probably more readable
		*  to call {@link #getControl()}.  This method exists to ensure that
		*  the EventObject.getSource() method will return the correct value.
		*/
		public Object getSource();

		/** The control used and modified in the event. */
		public /*@NonNull*/ IInputDevice.IControl getControl();

		/**
		 *  Changes the control's state to what it should be after the event
		 *  occurs.
		 *  <p>
		 *  This should be called by the IInputDevice's pollEvent method, so
		 *  any other code will probably never need to call this.
		 */
		public void applyToControl();

		/**
		 *  The event's timestamp in Unix time, which is the number of
		 *  milliseconds since January 1, 1970, 00:00:00 GMT..
		 */
		public long getTimestamp();
	}

	/**
	 *  A list of all controls (buttons, keys, sticks, etc) found on the
	 *  input device.
	 *  <p>
	 *  Because not all physical devices enumerate their possible actions to
	 *  the IInputDevice implementor, it is possible that the list returned
	 *  from this method could start of empty when the IInputDevice is
	 *  constructed, and then grow as actual events prove the existance of
	 *  the device's controls.  For example: keyboards are likely to
	 *  behave this way, since it is often impossible for IInputDevice
	 *  implementors to know exactly which keys are present on a particular
	 *  keyboard.  It is therefore better to think of this as an enumeration
	 *  of all controls used on the device so far, rather than an exhaustive
	 *  description of the device's functionalities.
	 *  <P>
	 *  IInputDevice implementors are encouraged to define as many controls
	 *  as possible, provided that they can reasonably prove that those
	 *  controls are actually physically present on the device.
	 */
	public /*@NonNull*/ List</*@NonNull*/ ? extends IInputDevice.IControl> getControls();

	/*
	 *  Updates all input devices.
	 *  <p>
	 *  If the underlying input device uses an asynchronous event-based API,
	 *  then the implementor of the IInputDevice shall accumulate any such
	 *  events and actualize them only when update() is called.
	 */
	//public boolean update();
	/**
	 *  Processes all queued events, thus updating the state of
	 *  {@link #getControls()} to the most recent known physical status.
	 *  <p>
	 *  This produces the same results as calling {@link #pollEvent(AtomicReference)}
	 *  repeatedly.
	 *  <p>
	 *  Some implementors of IInputDevice may be able to implement this more
	 *  efficiently, since it could relax synchronization constraints or
	 *  simplify buffer reclamation.
	 */
	public void fastForward();

	/*
	 *  Returns a list of all input events accumulated since the last call to
	 *  {@link #update()}.  If update() has not been called, then
	 *  this shall return all events since the IInputDevice's construction.
	 *  <p>
	 *  If no input events have occured, then this shall return a zero-length
	 *  list.  poll() shall never return null.
	 *  <p>
	 *  When calling this method, never store the returned IInputDevice.IEvent
	 *  objects beyond the next call to {@link #update()}.
	 *  Any IInputDevice.IEvent objects still alive at the time of a call to
	 *  {@link #update()} may be reused by the event polling logic.
	 *  This may be done to avoid excessive heap allocations and garbage
	 *  collection activity.
	 */
	//public /*@NonNull*/ List</*@NonNull*/ IEvent> pollEvents();
	
	/**
	 *  Processes the earliest event in the event queue and then places a
	 *  reference to that event into the <i>eventRef</i> parameter.
	 *  <p>
	 *  If there are no events left in the event queue, then false will be
	 *  returned and {@code eventRef.get()} will return null.
	 *  <p>
	 *  The event object returned by {@code eventRef.get()} will only be
	 *  valid until the next call to {@link #pollEvent}.  If the caller needs
	 *  to retain this information between calls, then the caller should
	 *  copy the necessary information into other variables or objects before
	 *  calling {@link #pollEvent} again.  This limitation exists to allow
	 *  IInputDevice implementors to avoid spurious heap allocations
	 *  every time {@link #pollEvent} is called.
	 *  <p>
	 *  Calling this method will likely change the device's state as seen
	 *  through the {@link #getControls()} method, because the referenced event
	 *  will be applied to the device's state before the {@link #pollEvent}
	 *  method returns.  To avoid altering the device's state, call
	 *  {@link #peekEvent} instead.
	 *  <p>
	 *  Example:
	 *  <pre>
	 *  {@code
	 *  IInputDevice device = ...;
	 *  AtomicReference<IEvent> ref = new AtomicReference<>(null);
	 *  while (true)
	 *  {
	 *      while ( device.pollEvent(ref) )
	 *      {
	 *          IInputDevice.IEvent event = ref.get();
	 *          // handle your event here
	 *      }
	 *      // do some other stuff here -- spam twitter, ping yahoo, etc.
	 *  }
	 *  }
	 *  </pre>
	 *  @return true when an event has been returned by reference, false when
	 *    there are no events to process
	 */
	public boolean pollEvent(/*@NonNull*/ AtomicReference<IInputDevice.IEvent> eventRef);

	/**
	 *  The size of the current event queue.
	 *  @return the size of the event queue, which is how many times
	 *    {@link #pollEvent} can be called before it will return false,
	 *    as well as one greater than the maximum allowed <i>which</i> value
	 *    supplied to {@link #peekEvent}.
	 */
	public int eventQueueSize();

	/**
	 *  Places a reference to a queued event into the <i>eventRef</i> parameter
	 *  without processing the event.
	 *  <p>
	 *  The event object returned by {@code eventRef.get()} will only be
	 *  valid until the next call to {@link #pollEvent}.  If the caller needs
	 *  to retain this information between calls, then the caller should
	 *  copy the necessary information into other variables or objects before
	 *  calling {@link #pollEvent}.  This limitation exists to allow
	 *  IInputDevice implementors to avoid spurious heap allocations
	 *  every time {@link #pollEvent} is called.
	 *  <p>
	 *  @param eventRef  A reference to be populated with a reference to a
	 *                   queued event.  {@code eventRef.get()} will return
	 *                   null if the number passed in the <i>which</i> parameter
	 *                   does not refer to a valid queue position.
	 *  @param which     Selects the queue position to inspect.  0 refers to the
	 *                   earliest queued event, while a value of
	 *                   {@link #eventQueueSize()}-1 will return the most
	 *                   recent queued event.
	 *  @return true if the operation was successful, false if the <i>which</i>
	 *          parameter does not refer to a valid queue position.
	 */
	public boolean peekEvent(/*@NonNull*/ AtomicReference<IInputDevice.IEvent> eventRef, int which);
}
