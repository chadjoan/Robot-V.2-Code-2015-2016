package org.avalancherobotics.standalone.input;

// Implementation NOTE: The standalone package shall never depend on the
//   org.avalancherobotics.library package, the org.avalancherobotics.desktop package,
//   or any other implementors.  It should also use only Java code that will
//   be found on all platforms (ex: Android + Desktop), which means that only
//   things in java.util are remotely safe.
//

import java.util.ArrayList;

import org.avalancherobotics.standalone.input.InputAnalogControl;
import org.avalancherobotics.standalone.input.InputAnalogEvent;
import org.avalancherobotics.standalone.input.InputBooleanControl;
import org.avalancherobotics.standalone.input.InputBooleanEvent;
import org.avalancherobotics.standalone.input.InputEvent;
import org.avalancherobotics.standalone.interfaces.IInputDevice;
import org.avalancherobotics.standalone.internal.ArrayQueue;
import org.avalancherobotics.standalone.internal.UnimplementedException;
import org.avalancherobotics.standalone.internal.UnimplementedPossibilityException;

import java.util.concurrent.atomic.AtomicReference;

/**
 *  This class implements event queueing and polling code commonly used by
 *  classes implementing the
 *  {@link org.avalancherobotics.standalone.interfaces.IInputDevice}
 *  interface.
 *  <p>
 *  This class does no synchronization.  The calling class is expected to
 *  perform any necessary input synchronization, since it will have a better
 *  view of which threads access which resources.
 */
public class InputEventQueue extends ArrayQueue<InputEvent>
{
	private static final int nAxesAllocated = 3;
	private /*@NonNull*/ ArrayList</*@NonNull*/
		InputEvent.Buffer.Factory<InputAnalogEvent>> analogEventFactories;

	/** Constructs an empty event queue with an initial capacity sufficient to hold 16 elements. */
	public InputEventQueue()
	{
		this(16);
	}

	/** Constructs an empty array queue with an initial capacity sufficient to hold the specified number of elements. */
	public InputEventQueue(int numElements)
	{
		super(numElements);
		analogEventFactories = new ArrayList<>(nAxesAllocated);
		for(int i = 0; i < nAxesAllocated; i++)
		{
			analogEventFactories.add(
				new InputEvent.Buffer.Factory<InputAnalogEvent>()
				{
					int ii;

					public /*@NonNull*/ InputAnalogEvent createEventObject()
					{
						//   i |  #Axes  |  Which Axes
						// ----+---------+--------------
						//   0 |     1   |  X
						//   1 |     2   |  X, Y
						//   2 |     3   |  X, Y, Z
						//
						return new InputAnalogEvent(ii+1);
					}

					public InputEvent.Buffer.Factory<InputAnalogEvent> init(int i)
					{
						this.ii = i;
						return this;
					}
				}.init(i)
			);
		}
	}

	/**
	 *  Generates a 1 axis InputAnalogEvent in the given 'eventBuf', populates
	 *  it with values provided by the other arguments, and places it at
	 *  the end of the queue.
	 *
	 *  @param eventBuf  The buffer to use for allocating InputEvent objects.
	 *  @param control   The resulting event will return this control from its getControl() method.
	 *  @param timestamp The event's timestamp in Unix time, which is the number
	 *                   of milliseconds since January 1, 1970, 00:00:00 GMT.
	 *  @param oldX      The value of X before the event happened.
	 *  @param newX      The value of X that now exists because of the event.
	 */
	public void queueAnalogEvent(
		/*@NonNull*/ InputEvent.Buffer<InputAnalogEvent> eventBuf,
		/*@NonNull*/ InputAnalogControl                  control,
		long timestamp,
		double oldX,
		double newX
		)
	{
		queueAnalogEvent(
			eventBuf, control, 1, timestamp,
			oldX, Double.NaN, Double.NaN,
			newX, Double.NaN, Double.NaN);
	}

	/**
	 *  Generates a 2 axis InputAnalogEvent in the given 'eventBuf', populates
	 *  it with values provided by the other arguments, and places it at
	 *  the end of the queue.
	 *
	 *  @param eventBuf  The buffer to use for allocating InputEvent objects.
	 *  @param control   The resulting event will return this control from its getControl() method.
	 *  @param timestamp The event's timestamp in Unix time, which is the number
	 *                   of milliseconds since January 1, 1970, 00:00:00 GMT.
	 *  @param oldX      The value of X before the event happened.
	 *  @param oldY      The value of Y before the event happened.
	 *  @param newX      The value of X that now exists because of the event.
	 *  @param newY      The value of Y that now exists because of the event.
	 */
	public void queueAnalogEvent(
		/*@NonNull*/ InputEvent.Buffer<InputAnalogEvent> eventBuf,
		/*@NonNull*/ InputAnalogControl                  control,
		long timestamp,
		double oldX, double oldY,
		double newX, double newY
		)
	{
		queueAnalogEvent(
			eventBuf, control, 2, timestamp,
			oldX, oldY, Double.NaN,
			newX, newY, Double.NaN);
	}

	/**
	 *  Generates a 3 axis InputAnalogEvent in the given 'eventBuf', populates
	 *  it with values provided by the other arguments, and places it at
	 *  the end of the queue.
	 *
	 *  @param eventBuf  The buffer to use for allocating InputEvent objects.
	 *  @param control   The resulting event will return this control from its getControl() method.
	 *  @param timestamp The event's timestamp in Unix time, which is the number
	 *                   of milliseconds since January 1, 1970, 00:00:00 GMT.
	 *  @param oldX      The value of X before the event happened.
	 *  @param oldY      The value of Y before the event happened.
	 *  @param oldZ      The value of Z before the event happened.
	 *  @param newX      The value of X that now exists because of the event.
	 *  @param newY      The value of Y that now exists because of the event.
	 *  @param newZ      The value of Z that now exists because of the event.
	 */
	public void queueAnalogEvent(
		/*@NonNull*/ InputEvent.Buffer<InputAnalogEvent> eventBuf,
		/*@NonNull*/ InputAnalogControl                  control,
		long timestamp,
		double oldX, double oldY, double oldZ,
		double newX, double newY, double newZ
		)
	{
		queueAnalogEvent(
			eventBuf, control, 3, timestamp,
			oldX, oldY, oldZ,
			newX, newY, newZ);
	}

	private void queueAnalogEvent(
		/*@NonNull*/ InputEvent.Buffer<InputAnalogEvent> eventBuf,
		/*@NonNull*/ InputAnalogControl                  control,
		int  nAxes,
		long timestamp,
		double oldX, double oldY, double oldZ,
		double newX, double newY, double newZ
		)
	{
		if ( eventBuf == null )
			throw new IllegalArgumentException("The 'eventBuf' parameter must be non-null.");

		if ( control == null )
			throw new IllegalArgumentException("The 'control' parameter must be non-null.");

		if ( nAxes < 1 )
			throw new IllegalArgumentException("The 'nAxes' parameter must be 1 or greater.");

		if ( nAxes >= 4 )
			throw new UnimplementedException("4+ Axis analog events are not yet implemented in InputEventQueue.");

		InputAnalogEvent event = eventBuf.allocate(analogEventFactories.get(nAxes-1));
		event.setControl(control);

		if ( nAxes >= 1 ) event.setOldValue(oldX, Axis.X);
		if ( nAxes >= 2 ) event.setOldValue(oldY, Axis.Y);
		if ( nAxes >= 3 ) event.setOldValue(oldY, Axis.Z);

		if ( nAxes >= 1 ) event.setNewValue(newX, Axis.X);
		if ( nAxes >= 2 ) event.setNewValue(newY, Axis.Y);
		if ( nAxes >= 3 ) event.setNewValue(newY, Axis.Z);

		event.setTimestamp(timestamp);

		this.addLast(event);
	}

	private InputEvent.Buffer.Factory<InputBooleanEvent> booleanEventFactory =
		new InputEvent.Buffer.Factory<InputBooleanEvent>()
	{
		public /*@NonNull*/ InputBooleanEvent createEventObject()
		{
			return new InputBooleanEvent();
		}
	};

	/**
	 *  Generates an InputBooleanEvent in the given 'eventBuf', populates
	 *  it with values provided by the other arguments, and places it at
	 *  the end of the queue.
	 *
	 *  @param eventBuf  The buffer to use for allocating InputEvent objects.
	 *  @param control   The resulting event will return this control from its getControl() method.
	 *  @param timestamp The event's timestamp in Unix time, which is the number
	 *                   of milliseconds since January 1, 1970, 00:00:00 GMT.
	 *  @param eventType Whether the button/key/etc was PRESSED or RELEASED.
	 */
	public void queueBooleanEvent(
		/*@NonNull*/ InputEvent.Buffer<InputBooleanEvent> eventBuf,
		/*@NonNull*/ InputBooleanControl                  control,
		long                                          timestamp,
		InputBooleanEvent.Type                        eventType )
	{
		InputBooleanEvent event = eventBuf.allocate(booleanEventFactory);
		event.setControl(control);

		event.setType(eventType);
		switch(eventType)
		{
			case PRESS:
				control.setState(InputBooleanControl.State.PRESSED);
				break;

			case RELEASE:
				control.setState(InputBooleanControl.State.RELEASED);
				break;

			default:
				throw new UnimplementedPossibilityException(
					"Unimplemented InputBooleanEvent.Type: "+ eventType);
		}

		event.setTimestamp(timestamp);

		this.addLast(event);
	}

	// This instance member is used by fastForward to pass into pollEvent
	// without needing to perform an allocation every time fastForward is
	// called.  The result is always discarded, so it shouldn't cause
	// synchronization problems either.
	private AtomicReference<IInputDevice.IEvent> ffEventRef = new AtomicReference<>();

	/**
	 *  A basic implementation of
	 *  {@link org.avalancherobotics.standalone.interfaces.IInputDevice#fastForward}.
	 *  <p>
	 *  This implementation simply calls {@link #pollEvent} repeatedly until
	 *  there are no more events to process.
	 */
	public void fastForward()
	{
		while(pollEvent(ffEventRef))
			assert(true);
	}

	/**
	 *  A basic implementation of
	 *  {@link org.avalancherobotics.standalone.interfaces.IInputDevice#pollEvent}.
	 */
	public boolean pollEvent(/*@NonNull*/ AtomicReference<IInputDevice.IEvent> eventRef)
	{
		// Retrieve/pop the event from the queue.
		InputEvent event = this.pollFirst();
		if ( event == null )
		{
			eventRef.set(null);
			return false;
		}

		// Apply the event to our control state.
		event.applyToControl();

		// Free up the event's slot in the event buffer.
		event.getEnclosingBuffer().deallocate(event);

		// Pass the event to the caller.
		eventRef.set(event);

		return true;
	}

	/**
	 *  A basic implementation of
	 *  {@link org.avalancherobotics.standalone.interfaces.IInputDevice#peekEvent}.
	 *  <p>
	 *  This implementation just returns InputEventQueue.size(), but is included
	 *  because InputEventQueue implements most IInputDevice methods.
	 */
	public int eventQueueSize() { return this.size(); }

	/**
	 *  A basic implementation of
	 *  {@link org.avalancherobotics.standalone.interfaces.IInputDevice#peekEvent}.
	 */
	public boolean peekEvent(/*@NonNull*/ AtomicReference<IInputDevice.IEvent> eventRef, int which)
	{
		if ( which >= 0 && which < this.size() )
		{
			eventRef.set(this.get(which));
			return true;
		}
		else
		{
			eventRef.set(null);
			return false;
		}
	}
}
