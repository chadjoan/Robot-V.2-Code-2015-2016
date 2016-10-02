package org.avalancherobotics.standalone.input;

// Implementation NOTE: The standalone package shall never depend on the
//   org.avalancherobotics.library package, the org.avalancherobotics.desktop package,
//   or any other implementors.  It should also use only Java code that will
//   be found on all platforms (ex: Android + Desktop), which means that only
//   things in java.util are remotely safe.
//

import org.avalancherobotics.standalone.interfaces.IInputDeviceSet;
import org.avalancherobotics.standalone.interfaces.IInputDevice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicReference;

/**
 *  This class is designed to make it easier to implement the ILayeredOpMode
 *  interface.
 *  <p>
 *  There are two peices of functionality provided in this class:
 *  <ol>
 *  <li>Aggregation of events from multiple IInputDevice objects.</li>
 *  <li>Synchronization of the above aggregation.</li>
 *  </ol>
 *  The latter functionality is important when events could be generated
 *  in an asynchronous fashion, such as with the Qualcomm OpMode implementation.
 *  In other scenarios where the input sources reside in the same thread
 *  as the code that polls for events, the synchronization is an unnecessary
 *  benefit that should have negligable cost.
 *  <p>
 *  Future directions: it might be a good idea to move all synchronization
 *  code out of the modularity layer.  This functionality needs to exist, but
 *  is not directly related to modularity, and might be better off in another
 *  library.  Example: Move the aggregation functionality into a different class
 *  and then implement this class as an extension to the other class.  Such a
 *  change would be API breaking, but the breakage would probably be easy to
 *  fix in any code that calls into this class (edit the import statement).
 */
public class InputSynchronizer implements IInputDeviceSet
{
	// NOTE: I'm not actually sure if this /should/ implement IInputDevice, but
	//   it seems like it should always be possible to do that because the
	//   synchronizer can forward everything to its input devices.

	private /*@NonNull*/ LinkedList</*@NonNull*/ IInputDevice> devices;
	private /*@NonNull*/ ArrayList</*@NonNull*/ IInputDevice.IEvent> eventBuffer;
	private /*@NonNull*/ Object syncObject;

	/**
	 *  Constructs an InputSychronizer that uses the given synchronizationObject.
	 *  <p>
	 *  If null is passed into the 'synchronizationObject' parameter, then the
	 *  InputSynchronizer will create its own synchronization object.
	 *  <p>
	 *  Pass your own synchronization object if you wish to synchronize input
	 *  updating/polling with other components.  Call the zero-argument version
	 *  of this constructor if you wish to have input devices poll synchronously
	 *  between themselves, but asynchronously with the rest of the program.
	 */
	public InputSynchronizer( /*@NonNull*/ Object synchronizationObject )
	{
		this.devices = new LinkedList</*@NonNull*/ IInputDevice>();
		this.eventBuffer = new ArrayList</*@NonNull*/ IInputDevice.IEvent>();
		if ( synchronizationObject != null )
			this.syncObject = synchronizationObject;
		else
			this.syncObject = new Object();
	}

	/**
	 *  @see #InputSynchronizer(Object)
	 */
	public InputSynchronizer()
	{
		this(new Object());
	}

	/** This class's implementation of close() will call {@link clear}.
	 *  @see org.avalancherobotics.standalone.interfaces.IDevice#clear
	 */
	public void  close()
	{
		clear();
	}

	/** Get connection information about this device in a human readable format */
	public java.lang.String  getConnectionInfo() {
		return "InputSynchronizer: This device is a software abstraction; it does not have its own hardware connectivity.";
	}

	/**
	 *  Device Name
	 *  @return device manufacturer and name
	 */
	public java.lang.String  getDeviceName() {
		return "Java Object : "+ this.getClass().getName();
	}

	/** Version */
	public int  getVersion() { return 1; }

	/**
	 *  Returns the synchronization object used to synchronize input event
	 *  handling.
	 *  <p>
	 *  If no synchronization object has been assigned,
	 *  then this will return a reference to the InputSynchronizer itself.
	 */
	public /*@NonNull*/ Object getSynchronizationObject() { return this.syncObject; }

	/**
	 *  Assigns the synchronization object used to synchronize input event
	 *  handling.
	 *  <p>
	 *  This method exists to handle situations where the InputSynchronizer
	 *  is synchronizing with another component and the synchronization object
	 *  used to synchronize the two components is changed.
	 */
	public void setSynchronizationObject(/*@NonNull*/ Object synchronizationObject)
	{
		if ( synchronizationObject == null )
			throw new IllegalArgumentException("setSynchronizationObject: The synchronizationObject parameter must be non-null.");
		this.syncObject = synchronizationObject;
	}

	/**
	 *  Removes all devices from the list of synchronized devices.
	 *  <p>
	 *  This operation itself is not synchronized; the caller must provide
	 *  any synchronization.  The InputSynchronizer class will synchronize
	 *  the more frequent operations, like {@link #pollEvent} and
	 *  {@link #peekEvent}.
	 */
	public void clear()
	{
		devices.clear();
	}

	/**
	 *  Synchronize an additional device.
	 *  <p>
	 *  This operation itself is not synchronized; the caller must provide
	 *  any synchronization.  The InputSynchronizer class will synchronize
	 *  the more frequent operations, like {@link #pollEvent} and
	 *  {@link #peekEvent}.
	 *  <p>
	 *  Note that this will require O(n) execution time, where n is the number
	 *  of devices already synchronized.  This is required for preventing
	 *  duplicate devices.  The design assumes that the number of devices is
	 *  small (ex: less than 20), and may very well execute faster than an
	 *  implementation designed to provide a stronger guarantee on algorithmic
	 *  time requirements.
	 *
	 *  @return true if this set did not already contain the specified element
	 */
	public boolean addDevice(/*@NonNull*/ IInputDevice device)
	{
		if ( device == null )
			throw new IllegalArgumentException("The device parameter must be non-null.");

		// dedup.
		if ( devices.contains(device) )
			return false;

		devices.add(device);
		return true;
	}

	/**
	 *  Removes the given device from the list of synchronized devices.
	 *  <p>
	 *  This operation itself is not synchronized; the caller must provide
	 *  any synchronization.  The InputSynchronizer class will synchronize
	 *  the more frequent operations, like {@link #pollEvent} and
	 *  {@link #peekEvent}.
	 *
	 *  @return true if this list contained the specified element
	 */
	public boolean removeDevice(/*@NonNull*/ IInputDevice device)
	{
		if ( device == null )
			throw new IllegalArgumentException("The device parameter must be non-null.");
		return devices.remove(device);
	}

	/**
	 *  Lists the devices synchronized by InputSynchronizer.
	 *  <p>
	 *  This operation itself is not synchronized; the caller must provide
	 *  any synchronization.  The InputSynchronizer class will synchronize
	 *  the more frequent operations, like {@link #pollEvent} and
	 *  {@link #peekEvent}.
	 *
	 *  @see org.avalancherobotics.standalone.interfaces.IInputDeviceSet#getSourceDevices()
	 */
	@Override
	public /*@NonNull*/ List</*@NonNull*/ IInputDevice> getSourceDevices()
	{
		return Collections.unmodifiableList(devices);
	}

	/**
	 *  A list of all controls (buttons, keys, sticks, etc) found on the
	 *  input devices.
	 *  <p>
	 *  This is the union of all controls from all devices.  It may contain
	 *  things like buttons with duplicate keycodes, but such duplicates will
	 *  always have differing .getDevice() results.
	 *  <p>
	 *  NOTE: The current implementation is very cheap and may do a bunch
	 *  of unnecessary heap allocations and such.  Avoid using this in
	 *  realtime code by instead looking at the controls defined in individual
	 *  input devices (ex: using {@link #getSourceDevices()}).
	 *
	 *  @see org.avalancherobotics.standalone.interfaces.IInputDevice#getControls()
	 *  @see org.avalancherobotics.standalone.interfaces.IInputDeviceSet#getControls()
	 */
	public /*@NonNull*/ List</*@NonNull*/ IControl> getControls()
	{
		/*@NonNull*/ ArrayList</*@NonNull*/ IControl> controlsAgg = new ArrayList<>(this.devices.size()*4);
		for(IInputDevice device : this.devices)
			controlsAgg.addAll(device.getControls());

		return Collections.unmodifiableList(controlsAgg);
	}

	/**
	 *  Calls {@link org.avalancherobotics.standalone.interfaces.IInputDevice#fastForward}
	 *  on all devices synchronized by the {@link InputSynchronizer}.
	 */
	public void fastForward()
	{
		synchronized(this.syncObject) {
			for(IInputDevice device : this.devices)
				device.fastForward();
		}
	}

	// This is used to receive events from devices under our care.
	// It allows us to avoid allocating an AtomicReference every time
	// that this.pollEvent or this.peekEvent are called.
	private /*@NonNull*/ AtomicReference<IEvent> instanceEventRef = new AtomicReference<IEvent>(null);

	/**
	 *  Processes the earliest event queued by all of the devices synchronized
	 *  by the InputSynchronizer, and then places a reference to that event
	 *  into the <i>eventRef</i> parameter.
	 *
	 *  @see org.avalancherobotics.standalone.interfaces.IInputDevice#pollEvent
	 *  @see org.avalancherobotics.standalone.interfaces.IInputDeviceSet#pollEvent
	 */
	public boolean pollEvent(/*@NonNull*/ AtomicReference<IInputDevice.IEvent> eventRef)
	{
		eventRef.set(null);

		if ( devices.size() <= 0 )
			return false;

		synchronized(this.syncObject)
		{
			IInputDevice deviceWithEarliestEvent =
				peekEventNoSync(eventRef);

			if ( deviceWithEarliestEvent != null )
				deviceWithEarliestEvent.pollEvent(eventRef);
		}

		return (eventRef.get() != null);
	}

	/**
	 *  Returns the sum of the sizes of all event queues owned by devices
	 *  synchronized by this {@link InputSynchronizer} object.
	 *  <p>
	 *  Note that if eventQueueSize is being compared to 0 to determine
	 *  if the InputSynchronizer's "queue" is empty, then this operation
	 *  can be done potentially more efficiently by calling
	 *  {@link #peekEvent} with a 'which' parameter of 0 and inspecting
	 *  the return value.
	 *
	 *  @see org.avalancherobotics.standalone.interfaces.IInputDevice#eventQueueSize
	 *  @see org.avalancherobotics.standalone.interfaces.IInputDeviceSet#eventQueueSize
	 */
	public int eventQueueSize()
	{
		// If there are no devices, then avoid synchronization.
		if ( devices.size() <= 0 )
			return 0;

		int sum = 0;
		synchronized(this.syncObject) {
			sum = eventQueueSizeNoSync();
		}
		return sum;
	}

	private int eventQueueSizeNoSync()
	{
		int sum = 0;
		for(IInputDevice device : this.devices)
			sum += device.eventQueueSize();
		return sum;
	}

	private class EventLocation implements Comparable<EventLocation>
	{
		public IInputDevice device;
		public int          which;
		public long         timestamp;

		public void set( /*@NonNull*/ IInputDevice device, int which, long timestamp )
		{
			this.device = device;
			this.which = which;
			this.timestamp = timestamp;
		}

		public int compareTo(EventLocation o)
		{
			// TODO: unittest.
			return Long.signum(o.timestamp - this.timestamp);
		}
	}

	// Don't store peekCache this way:
	//   private ArrayList<IEvent> peekCache = null;
	// The IInputDevice.peekEvent(...) interface does not guarantee that
	// the IEvent objects can be stored past subsequent calls to
	// .peekEvent(...) , so storing them in an array would be a bug.
	// Instead, we store the information necessary to find the events.
	private ArrayList<EventLocation> peekCache = null;

	private long[] cacheEarliestEventTimestamps = null;
	private int[]  cacheQueueSizes = null;

	/**
	 *  @see org.avalancherobotics.standalone.interfaces.IInputDevice#eventQueueSize
	 *  @see org.avalancherobotics.standalone.interfaces.IInputDevice#peekEvent
	 *  @see org.avalancherobotics.standalone.interfaces.IInputDeviceSet#peekEvent
	 */
	public boolean peekEvent(/*@NonNull*/ AtomicReference<IEvent> eventRef, int which)
	{
		if ( devices.size() <= 0 )
			return false;

		synchronized(this.syncObject)
		{
			if ( which == 0 )
			{
				// Optimization: if the caller just wants the most recent event,
				//   then we can do that in O(k) time where k is the number of devices.
				//   This is already implemented in the nearby peekEventNoSync method.
				IInputDevice deviceWithEarliestEvent =
					peekEventNoSync(eventRef);
			}
			else
			{
				// Less optimal route: the caller wants some arbitrary event
				// in the queue, which forces us to do some allocation and sorting.
				// To prevent incurring these overheads for /every/ call,
				// we memoize the results.
				boolean isCacheValid = calculatePeekCacheValidity();

				// If we don't have a valid cache, then we will generate
				// it, and then return our result from the new cache.
				if ( !isCacheValid )
					generatePeekCache();

				// Retrieve the desired value from the cache.
				EventLocation eloc = this.peekCache.get(which);
				eloc.device.peekEvent(eventRef, eloc.which);
			}
		}
		return (eventRef.get() != null);
	}

	private boolean calculatePeekCacheValidity()
	{
		if (
			peekCache == null ||
			cacheEarliestEventTimestamps == null ||
			cacheEarliestEventTimestamps.length != devices.size() ||
			cacheQueueSizes == null ||
			cacheQueueSizes.length != devices.size()
			)
			return false;

		// If we have a cache at all, then assume it is valid,
		// and then try to prove that it is invalid.
		boolean isCacheValid = true;

		int deviceIndex = 0;
		for(IInputDevice device : this.devices)
		{
			if ( device.eventQueueSize() != cacheQueueSizes[deviceIndex] )
			{
				isCacheValid = false;
				break;
			}

			if ( cacheQueueSizes[deviceIndex] > 0 )
			{
				if ( !device.peekEvent(this.instanceEventRef,0) )
				{
					isCacheValid = false;
					break;
				}

				/*@NonNull*/ IEvent event = this.instanceEventRef.get();
				if ( cacheEarliestEventTimestamps[deviceIndex] != event.getTimestamp() )
				{
					isCacheValid = false;
					break;
				}
			}

			deviceIndex++;
		}
		return isCacheValid;
	}

	private void generatePeekCache()
	{
		final int queueSize = this.eventQueueSizeNoSync();
		final int nDevices = devices.size();

		// Allocate cache objects.
		if ( peekCache == null )
			peekCache = new ArrayList<>(queueSize);
		else
		{
			int delta = queueSize - peekCache.size();
			if ( delta > 0 )
			{
				// Expand.
				for( int i = 0; i < delta; i++ )
					peekCache.add(new EventLocation());
			}
			else if ( delta < 0 )
			{
				// Shrink.
				peekCache.subList(queueSize, peekCache.size()).clear();
				//peekCache.removeRange(queueSize,peekCache.size());
			}
		}

		if (
			cacheEarliestEventTimestamps == null ||
			cacheEarliestEventTimestamps.length != nDevices )
			cacheEarliestEventTimestamps = new long[nDevices];

		if ( cacheQueueSizes == null || cacheQueueSizes.length != nDevices )
			cacheQueueSizes = new int[nDevices];
		// End allocation.

		// Populate 
		int superQueueIndex = 0;
		int deviceIndex = 0;
		for(IInputDevice device : this.devices)
		{
			final int subQueueSize = device.eventQueueSize();
			
			// NOTE: this default value can end up in the array if
			//   subQueueSize is 0.
			long earliestTimestamp = -1;

			// Add the event to the cache, which is the array to be sorted.
			for ( int subQueueIndex = 0; subQueueIndex < subQueueSize; subQueueIndex++ )
			{
				if ( !device.peekEvent(this.instanceEventRef,subQueueIndex) )
					throw new NoSuchElementException(
						"Failed to iterate over event queue: "+
						"Expected event "+ subQueueIndex +" to exist, "+
						"but peekEvent returned false for it.");

				/*@NonNull*/ IEvent event = this.instanceEventRef.get();

				peekCache.get(superQueueIndex)
					.set(device, subQueueIndex, event.getTimestamp());

				if ( subQueueIndex == 0 )
					earliestTimestamp = event.getTimestamp();

				superQueueIndex++;
			}

			// Record cache validation information.
			cacheEarliestEventTimestamps[deviceIndex] = earliestTimestamp;
			cacheQueueSizes[deviceIndex] = subQueueSize;
			deviceIndex++;
		}

		// Sort.
		Collections.sort(peekCache);
	}

	// Returns the earliest queued event of all devices, without processing it.
	private boolean peekEvent(/*@NonNull*/ AtomicReference<IEvent> eventRef)
	{
		eventRef.set(null);

		if ( devices.size() <= 0 )
			return false;

		synchronized(this.syncObject)
		{
			IInputDevice deviceWithEarliestEvent =
				peekEventNoSync(eventRef);
		}

		return (eventRef.get() != null);
	}

	// Unlike the normal "peekEvent" method, this will return the IInputDevice
	// that the earliest event came from.  This is necessary because the caller
	// may need to poll that device to force the event to be processed.
	private IInputDevice peekEventNoSync( /*@NonNull*/ AtomicReference<IEvent> eventRef )
	{
		IEvent earliestEvent = null;
		IInputDevice deviceWithEarliestEvent = null;

		for(IInputDevice device : this.devices)
		{
			// This extra synchronization should be unnecessary
			// because this.instanceEventRef has the same scope as this.syncObject
			// thus making it impossible for a thread to enter this
			// code without already having locked /at least/ the entire
			// InputSynchronizer object (more if other code synchronizes
			// on the synchronization object referenced by this.syncObject).
			//synchronized(this.instanceEventRef)
			//{
				if ( !device.peekEvent(this.instanceEventRef,0) )
					continue;

				/*@NonNull*/ IEvent candidateEvent = this.instanceEventRef.get();
			//}

			if ( earliestEvent == null )
			{
				earliestEvent = candidateEvent;
				deviceWithEarliestEvent = device;
			}
			else
			{
				long timestampEarliest = earliestEvent.getTimestamp();
				long timestampCandidate = candidateEvent.getTimestamp();
				if ( timestampCandidate < timestampEarliest )
				{
					earliestEvent = candidateEvent;
					deviceWithEarliestEvent = device;
				}
			}
		}

		eventRef.set(earliestEvent);
		return deviceWithEarliestEvent;
	}

	/*
	 *  Returns a list of all input events accumulated by all input devices
	 *  since the last call to {@link #update()}.  This list will be support
	 *  random access.
	 *  <p>
	 *  ILayeredOpMode implementors can use this to implement .pollInputs().
	 *  <p>
	 *  This method assumes that no other threads will call .update() during
	 *  its execution on any of the input devices synchronized by this
	 *  InputSynchronizer.
	 */
//	public /*@NonNull*/ List</*@NonNull*/ IInputDevice.IEvent> poll()
//	{
//		synchronized(this.syncObject)
//		{
//			// We are guaranteed that the event queue won't change inbetween
//			// calls to .update(), so we can assume that the individual devices
//			// will have the same queue size and contents throughout this method.
//			// Thus, it is safe to count them and use the information for efficient
//			// buffering.
//			int eventCount = 0;
//			for(IInputDevice device : this.devices)
//				eventCount += device.poll().size();
//
//			// Allocate enough room all at once, for sake of efficiency.
//			eventBuffer.ensureCapacity(eventCount);
//
//			// Ensure the /size/ of the buffer.
//			int bufferSize = eventBuffer.size();
//			int delta = eventCount - bufferSize;
//			if ( delta > 0 )
//			{
//				// enlarge
//				for ( int i = 0; i < delta; i++ )
//					eventBuffer.add(null);
//			}
//			else if ( delta < 0 )
//			{
//				// shrink
//				eventBuffer.removeRange(bufferSize+delta,bufferSize);
//			}
//
//			// Now copy the event references from the source input devices into
//			// our own event queue.
//			int dstIndex = 0;
//			for(IInputDevice device : this.devices)
//			{
//				for(/*@NonNull*/ IInputDevice.IEvent event : device.poll())
//				{
//					eventBuffer.set(dstIndex, event);
//					dstIndex++;
//				}
//			}
//		}
//
//		return Collections.unmodifiableList(eventBuffer);
//	}


}
