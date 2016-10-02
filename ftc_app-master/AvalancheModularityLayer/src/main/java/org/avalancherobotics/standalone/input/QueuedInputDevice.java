package org.avalancherobotics.standalone.input;

// Implementation NOTE: The standalone package shall never depend on the
//   org.avalancherobotics.library package, the org.avalancherobotics.desktop package,
//   or any other implementors.  It should also use only Java code that will
//   be found on all platforms (ex: Android + Desktop), which means that only
//   things in java.util are remotely safe.
//

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.avalancherobotics.standalone.interfaces.IInputDevice;
import org.avalancherobotics.standalone.input.InputEvent;
import org.avalancherobotics.standalone.input.InputEventQueue;

/**
 *  A class used to house functionality common to input devices that use
 *  the {@link org.avalancherobotics.standalone.input.InputEventQueue} class.
 *  <p>
 *  All event-handling methods here will synchronize on an object exposed
 *  by the {@link #getSynchronizationObject()} and {@link #setSynchronizationObject(Object)}
 *  methods.  The exact methods that do this synchronization will mention it
 *  in their documentation.
 *  <p>
 *  Any derived class that receives input events from asynchronous sources
 *  (ex: the java.awt.event.* classes or qualcomm Gamepad objects) must
 *  synchronize on the synchronization object exposed by {@link #getSynchronizationObject()}
 *  to avoid undefined and non-deterministic behavior in the event queue's
 *  data structure.
 *  <p>
 *  Any derived class that receives input events from synchronous sources
 *  can safely ignore the synchronization object.  Event handling methods
 *  will still synchronize on it, but if they are only ever accessed by
 *  a single thread, then there will be no contention and the performance
 *  impact of the synchronization should be negligable.
 */
public abstract class QueuedInputDevice implements IInputDevice
{
    private /*@NonNull*/ Object  syncObject = new Object();

    /**
	 *  All event-handling methods in this class will synchronize on this object.
	 *  The exact methods that do this synchronization will mention it
	 *  in their documentation.
	 *  <p>
	 *  Any derived class that receives input events from asynchronous sources
	 *  (ex: the java.awt.event.* classes or qualcomm Gamepad objects) must
	 *  synchronize on the synchronization object exposed by {@link #getSynchronizationObject()}
	 *  to avoid undefined and non-deterministic behavior in the event queue's
	 *  data structure.
	 *  <p>
	 *  Any derived class that receives input events from synchronous sources
	 *  can safely ignore the synchronization object.  Event handling methods
	 *  will still synchronize on it, but if they are only ever accessed by
	 *  a single thread, then there will be no contention and the performance
	 *  impact of the synchronization should be negligable.
	 *
	 *  @see #setSynchronizationObject(Object)
     */
    protected /*@NonNull*/ Object getSynchronizationObject() { return this.syncObject; }

    /**
     *  @see #getSynchronizationObject()
     */
    protected void setSynchronizationObject(/*@NonNull*/ Object obj)
    {
		if ( obj == null )
			throw new IllegalArgumentException("The 'obj' parameter must be non-null.");
		this.syncObject = obj;
	}

	private /*@NonNull*/ InputEventQueue eventQueue = new InputEventQueue(32);

	/**
	 *  Exposes the underlying InputEventQueue object.
	 *  <p>
	 *  Implementing classes will likely need to call this getter and fill
	 *  the queue with events as events are received from other sources
	 *  (ex: java.awt.event.KeyEvent or qualcomm/ftc Gamepad callback
	 *  information).
	 */
	public /*@NonNull*/ InputEventQueue getEventQueue() { return eventQueue; }

	// Used to implement the IDevice interface (required by IInputDevice).
	private /*@NonNull*/ String deviceName;

	/** */
	public QueuedInputDevice(/*@NonNull*/ String deviceName)
	{
		this.deviceName = deviceName;
		if ( deviceName == null )
			throw new IllegalArgumentException("The 'deviceName' parameter must be non-null.");
	}

	// -------------------------------------------------------------------------
	//                    Implementation : IDevice
	// -------------------------------------------------------------------------
	/**
	 *  Does nothing.
	 *  <p>
	 *  This method exists to implement the IDevice interface requirements.
	 */
	@Override
	public void  close() {}

	/**
	 *  Get connection information about this device in a human readable format.
	 *  <p>
	 *  Any class deriving {@link QueuedInputDevice} will be responsible for
	 *  implementing this method.
	 */
	@Override abstract public String  getConnectionInfo();

	/**
	 *  Device Name
	 *  @return device manufacturer and name
	 */
	@Override
	public String  getDeviceName()
	{
		return this.deviceName;
	}

	/** Version */
	@Override
	public int  getVersion() { return 1; }

	// -------------------------------------------------------------------------
	//                    Implementation : IInputDevice
	// -------------------------------------------------------------------------
	/**
	 *  A list of all keys that have been pressed on the keyboard since it
	 *  was instantiated.
	 *  <p>
	 *  Any class deriving {@link QueuedInputDevice} will be responsible for
	 *  implementing this method.
	 *
	 *  @see org.avalancherobotics.standalone.interfaces.IInputDevice#getControls
	 */
	@Override abstract public /*@NonNull*/ List</*@NonNull*/ ? extends IInputDevice.IControl> getControls();

	/**
	 *  Processes all queued events, thus updating the state of
	 *  {@link #getControls} to the most recent known physical status.
	 *  <p>
	 *  This produces the same results as calling {@link #pollEvent} repeatedly.
	 *  <p>
	 *  This implementation calls
	 *  {@link org.avalancherobotics.standalone.input.InputEventQueue#fastForward}.
	 *  <p>
	 *  Synchronizes on the object exposed by {@link #getSynchronizationObject()}.
	 *  @see org.avalancherobotics.standalone.interfaces.IInputDevice#fastForward
	 */
	@Override
	public void fastForward()
	{
		// In the unlikely event that this requires optimization, programmers
		// should consider modifying the InputEventQueue implementation instead
		// of creating a new implementation here.
		synchronized(this.syncObject) {
			eventQueue.fastForward();
		}
	}

	/**
	 *  Processes the earliest event in the event queue and then places a
	 *  reference to that event into the <i>eventRef</i> parameter.
	 *  <p>
	 *  Synchronizes on the object exposed by {@link #getSynchronizationObject()}.
	 *  @see org.avalancherobotics.standalone.interfaces.IInputDevice#pollEvent
	 */
	@Override
	public boolean pollEvent(/*@NonNull*/ AtomicReference<IInputDevice.IEvent> eventRef)
	{
		synchronized(this.syncObject) {
			return eventQueue.pollEvent(eventRef);
		}
	}

	/**
	 *  The size of the current event queue.
	 *  <p>
	 *  Synchronizes on the object exposed by {@link #getSynchronizationObject()}.
	 *  @see org.avalancherobotics.standalone.interfaces.IInputDevice#eventQueueSize
	 */
	@Override
	public int eventQueueSize()
	{
		synchronized(this.syncObject) {
			return eventQueue.eventQueueSize();
		}
	}

	/**
	 *  Places a reference to a queued event into the <i>eventRef</i> parameter
	 *  without processing the event.
	 *  <p>
	 *  Synchronizes on the object exposed by {@link #getSynchronizationObject()}.
	 *  @see org.avalancherobotics.standalone.interfaces.IInputDevice#peekEvent
	 */
	@Override
	public boolean peekEvent(/*@NonNull*/ AtomicReference<IInputDevice.IEvent> eventRef, int which)
	{
		synchronized(this.syncObject) {
			return eventQueue.peekEvent(eventRef,which);
		}
	}
}
