package org.avalancherobotics.standalone.input;

// Implementation NOTE: The standalone package shall never depend on the
//   org.avalancherobotics.library package, the org.avalancherobotics.desktop package,
//   or any other implementors.  It should also use only Java code that will
//   be found on all platforms (ex: Android + Desktop), which means that only
//   things in java.util are remotely safe.
//

import org.avalancherobotics.standalone.interfaces.IInputDevice;

import java.util.EventObject;
import java.util.ArrayList;

/**
 *  Describes a single input event, such as a key press, mouse motion,
 *  or joystick motion.
 *  <p>
 *  Determining the type of event should be done with downcasting:
 *  <pre>
 *  {@code
 *  InputEvent e = ...;
 *  if ( e instanceof InputKeyEvent )
 *      // Code for handling key events and button presses.
 *  else if ( e instanceof InputAnalogEvent )
 *      // Code for handling mouse motion, joystick, and other analog events.
 *  }
 *  </pre>
 */
public abstract class InputEvent extends EventObject implements IInputDevice.IEvent
{
	private long timestamp;
	private Buffer<? extends InputEvent> buffer = null;
	private int bufferSlotID = 0;

	/** */
	public InputEvent()
	{
		super(null);
	}

	/** The input device that generated the event. */
	@Override public final /*@NonNull*/ IInputDevice getDevice()
	{
		return getControl().getDevice();
	}

	/** The control used and modified in the event.
	 *  <p>
	 *  This returns the same value as {@link #getControl()}.  If you are
	 *  working with an InputEvent instance, then it is probably more readable
	 *  to call {@link #getControl()}.  This method exists to ensure that
	 *  the EventObject.getSource() method will return the correct value.
	 */
	@Override public InputControl getSource()
	{
		Object control = super.getSource();
		if ( !(control instanceof InputControl) )
			throw new ClassCastException(
				"An input event originated from something that isn't a control: "+
				control.getClass().getName());
		return (InputControl)control;
	}

	/** The control used and modified in the event. */
	@Override public /*@NonNull*/ InputControl getControl() { return this.getSource(); }

	/**
	 *  Set the control responsible for this event.
	 *  <p>
	 *  This should only be set by the class firing the event.
	 */
	public void setControl(/*@NonNull*/ InputControl control)
	{
		if ( control == null )
			throw new IllegalArgumentException("The control parameter must be non-null.");
		this.source = control;
	}

	/**
	 *  Changes the control's state to what it should be after the event
	 *  occurs.
	 */
	@Override public abstract void applyToControl();

	/**
	 *  Gets the event's timestamp in Unix time, which is the number of
	 *  milliseconds since January 1, 1970, 00:00:00 GMT..
	 */
	@Override public long getTimestamp()
	{
		return timestamp;
	}

	/**
	 *  Sets the event's timestamp in Unix time, which is the number of
	 *  milliseconds since January 1, 1970, 00:00:00 GMT..
	 */
	public void setTimestamp(long timestamp)
	{
		this.timestamp = timestamp;
	}

	/** */
	public Buffer<? extends InputEvent> getEnclosingBuffer() { return this.buffer; }

	/** This is an implementation detail of InputEvent.Buffer&lt;...&gt;.
	 *  Any classes inheriting from InputEvent are advised to NOT call this.
	 */
	// NOTE: The buffer may change the slotID during an operation
	// on the InputEvent object.
	protected final int getEnclosingBufferSlotID() { return this.bufferSlotID; }

	/** This is an implementation detail of InputEvent.Buffer&lt;...&gt;.
	 *  Any classes inheriting from InputEvent are advised to NOT call this.
	 */
	protected final void setBufferAndSlotID(Buffer<? extends InputEvent> value, int slotID)
	{
		this.buffer = value;
		this.bufferSlotID = slotID;
	}
	
	/**
	 *  This class implements an event buffer that allows InputEvent objects to
	 *  be reused efficiently.
	 */
	public static class Buffer <T extends InputEvent>
	{
		private /*@NonNull*/ Class<T> classInfo;
		private /*@NonNull*/ ArrayList</*@NonNull*/ T>
			internalBuf  = new ArrayList<>(32);

		public Buffer( Class<T> classInfo )
		{
			this.classInfo = classInfo;
		}

		// To avoid spurious heap allocations, we hold onto any buffered InputEvent
		// objects, even after they have been received.  We can fill them with
		// new state on a later frame without having to deallocate+reallocate
		// inbetween.  However, this means the .size() method on the internalBuf
		// object will not be sufficient for tracking the number of "alive"
		// InputEvent objects.  Instead, we will have to track this quantity
		// separately.
		private int nEventsUsed = 0;

		/**
		 *  The buffer does not necessarily know how to instantiate the InputEvents
		 *  that it buffers, so it needs a caller-provided factory to do this.
		 */
		public interface Factory<U extends InputEvent>
		{
			/** */
			public /*@NonNull*/ U createEventObject();
		}

		/** */
		public /*@NonNull*/ T allocate(/*@NonNull*/ Factory<T> factory)
		{
			T event = null;
			if ( nEventsUsed < internalBuf.size() )
				event = internalBuf.get(nEventsUsed);
			else
			{
				event = factory.createEventObject();
				internalBuf.add(event);
			}
			event.<T>setBufferAndSlotID(this, nEventsUsed);

			nEventsUsed++;
			return event;
		}

		/** */
		public void deallocate(/*@NonNull*/ InputEvent event)
		{
			// Note that the parameter is an InputEvent and not a T.
			// This is done intentionally, because unlike calls to allocate,
			// which tend to appear in a specific device that knows the
			// most-specific InputEvent type, calls to deallocate may happen
			// in very generic abstractions that are not aware of what event
			// types they are dispatching.

			if ( event.getEnclosingBuffer() != this )
				throw new IllegalArgumentException(
					"Attempt to deallocate an event from a buffer that it does not belong to.  "+
					"The type of 'event' is "+ event.getClass().getName() +" and the type of "+
					"the buffer is "+ classInfo.getName() +".");

			if ( !classInfo.isAssignableFrom(event.getClass()) )
				throw new IllegalArgumentException(
					"Attempt to deallocate event of type "+ event.getClass().getName()+
					" from buffer containing events of type "+ classInfo.getName() +".");

			final int beforeID = event.getEnclosingBufferSlotID();
			if ( beforeID < 0 || beforeID >= internalBuf.size() )
				throw new IndexOutOfBoundsException(
					"Attempt to deallocate an event that has an invalid buffer slot ID."+
					"  ID is "+ beforeID+"; Valid range is 0 inclusive through "+
					internalBuf.size()+" exclusive.");
			if ( beforeID >= nEventsUsed )
				throw new IllegalArgumentException("Attempt to deallocate an event twice.");

			final int endOfBufferID = nEventsUsed-1;
			if ( beforeID != endOfBufferID )
			{
				// Reposition the event-to-be-deallocated to the end of the
				// event buffer in O(1) time by swapping it with the event
				// that is already at the end of the buffer.
				T temp = internalBuf.get(endOfBufferID);

				internalBuf.set(endOfBufferID, classInfo.cast(event));
				event.setBufferAndSlotID(this, endOfBufferID);

				internalBuf.set(beforeID, temp);
				temp.setBufferAndSlotID(this, beforeID);
			}

			// After this decrement, the event's slot will reside in the
			// "free" space at the end of the internalBuf array, thus
			// designating it for later re-use (deallocating it).
			this.nEventsUsed--;
		}

		/** */
		public void clear()
		{
			nEventsUsed = 0;
		}
	}

}