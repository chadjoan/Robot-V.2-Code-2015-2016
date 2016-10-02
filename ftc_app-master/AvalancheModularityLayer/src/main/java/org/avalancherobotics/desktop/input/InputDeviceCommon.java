package org.avalancherobotics.desktop.input;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

import org.avalancherobotics.standalone.interfaces.IInputDevice;
import org.avalancherobotics.standalone.input.InputBooleanControl;
import org.avalancherobotics.standalone.input.InputControl;
import org.avalancherobotics.standalone.input.InputEvent;
import org.avalancherobotics.standalone.input.InputEventQueue;
import org.avalancherobotics.standalone.input.QueuedInputDevice;

/**
 *  A place to put code that is common to both keyboard and mouse input devices.
 *  <p>
 *  NOTE: Any non-AWT, non-desktop, specific code should somehow be moved
 *  into a more well-structured class living in the .standalone directory
 *  so that other classes with similar constraints can use it.  At the time
 *  of writing, the author does not know of a sufficiently re-usable way
 *  to write the code written here.
 */
public abstract class InputDeviceCommon extends QueuedInputDevice implements IInputDevice
{
	// Note on synchronization:
	// Most of the data structures contained by this class, with the exception
	// of this.controlsSync, are potentially mutated whenever KeyEvents are
	// received.  Since KeyEvents are possibly received on a different thread
	// than the rest of the program, it is important that these data structures
	// only be accessed by code inside the InputDeviceCommon, and only within a
	// block synchronized on this.getSynchronizationObject().

	private /*@NonNull*/ TreeMap<Integer, /*@NonNull*/ InputBooleanControl> controlsByKeyCode = new TreeMap<>();

	// ASSUMPTIONS:
	// <li>
	// <ul>The constControls ArrayList is populated in the constructor and then
	//     never touched again.</ul>
	// <ul>The dynamicControls ArrayList is only ever mutated by the addition of
	//     elements.  Any other operations, like swapping elements, removing
	//     elements, or changing cell contents, could cause non-deterministic
	//     behavior, such as changes that never get updated to publishedControls.</ul>
	// </li>
	private final /*@NonNull*/ ArrayList</*@NonNull*/ InputControl>  constControls   = new ArrayList<>();
	private final /*@NonNull*/ ArrayList</*@NonNull*/ InputControl>  dynamicControls = new ArrayList<>();

	// This controls list is only mutated by calling .replicateControlsList().
	// This list is therefore safe to expose to the outside world without any
	// threading catastrophes.  You may want to call .replicateControlsList()
	// before handing it to anyone, or it could end up rather out-of-date.
	private final /*@NonNull*/ ArrayList</*@NonNull*/ InputControl>  publishedControls = new ArrayList<>();

	/**
	 *  This can be used by implementing classes to indicate permanent empty
	 *  sets of controls, such as when there are no known "constant" controls.
	 */
	protected static final /*@NonNull*/ ArrayList</*@NonNull*/ InputControl>
		emptyControlsList = new ArrayList<>(0);

	/**
	 *  @param deviceName     Necessary for implementing the
	 *    {@link org.avalancherobotics.standalone.interfaces.IDevice} interface.
	 */
	public InputDeviceCommon(/*@NonNull*/ String deviceName)
	{
		super(deviceName);
	}

	/**
	 *  Establishes the controls currently defined for the device.
	 *  <p>
	 *  This is a potentially expensive operation which will do a bunch
	 *  of copying.  It should only be necessary to call this once:
	 *  near the end of the derived class's initialization, after the class
	 *  has built a constControls list, and no later.  Derived classes may
	 *  call this at any time, but it should be unnecessary to call this after
	 *  the device has been released into the wild and begins receiving events.
	 *  <p>
	 *  Caller is responsible for any necessary synchronization.  If this is
	 *  being called as part initialization, then synchronization will usually
	 *  not be necessary.
	 *
	 *  @param constControls    A list of controls that are known (at compile
	 *    time - before the program begins execution) to be present on the
	 *    device.  After the derived class passes that list into this
	 *    constructor, it should never edit that list again.  This class will
	 *    assume the list to be constant.  (It will actually make a copy, and
	 *    any changes made by the deriving class will be ignored.)
	 *  @param dynamicControls  A list of any controls that have been
	 *    dynamically generated by listening to events.  If this method is
	 *    being called in a derived class's initialization routine, then this
	 *    will probably be an empty list.
	 */
	protected void setControls(
		/*@NonNull*/ List</*@NonNull*/ InputControl> constControls,
		/*@NonNull*/ List</*@NonNull*/ InputControl> dynamicControls )
	{
		if ( constControls == null )
			throw new IllegalArgumentException("The 'constControls' parameter must be non-null.");

		if ( dynamicControls == null )
			throw new IllegalArgumentException("The 'dynamicControls' parameter must be non-null.");

		this.controlsByKeyCode.clear();
		this.constControls.clear();
		this.dynamicControls.clear();

		// Make a copy, just incase the derived class is irresponsible and
		// modifies the constControls List despite us telling them not to.
		// This allows at least the algorithms within this class to continue
		// doing predictable things, even though the hypothetical incorrectly
		// implemented derived class could do surprising things #notmyfault.
		this.constControls.addAll(constControls);

		// Let's copy this too.  If the caller mess with this.dynamicControls
		// without our knowledge, then that will also mess things up.  Having
		// a copy prevents that confusion.
		this.dynamicControls.addAll(dynamicControls);

		// Give publishedControls a healthy start.
		this.replicateControlsListFromScratch();

		// If the constControl list contained any keys/buttons, then add them
		// to the keyCode lookup so that we don't add duplicates into the
		// dynamic control list when we encounter them.
		for ( InputControl control : this.publishedControls )
		{
			if ( control instanceof InputBooleanControl )
			{
				InputBooleanControl bcontrol = (InputBooleanControl)control;
				controlsByKeyCode.put(bcontrol.getKeyCode(), bcontrol);
			}
		}
	}

	/**
	 *  Retrieves the InputBooleanControl matching 'keyCode' from this class's
	 *  dynamic controls list.
	 *  If 'keyCode' hasn't been encountered yet, this will create a
	 *  new InputBooleanControl and add it to the dynamic controls list.
	 *  <p>
	 *  This method performs no synchronization on its own.
	 *  Calling code is likely to be receiving events on a thread different
	 *  from the OpMode or main program thread, so it should probably
	 *  make sure this is called from within a synchronized() block that
	 *  synchronizes on {@link #getSynchronizationObject()}.
	 */
	protected InputBooleanControl ensureControlForBooleanEvent( int keyCode )
	{
		InputBooleanControl control = controlsByKeyCode.get(keyCode);
		if ( control == null )
		{
			control = new InputBooleanControl(this,keyCode);
			control.setState(InputBooleanControl.State.RELEASED);
			dynamicControls.add(control);
			controlsByKeyCode.put(keyCode, control);
		}
		return control;
	}

	/**
	 *  Exposes a read-only list of constant controls that were given at the
	 *  time of the instance's construction.
	 *  <p>
	 *  NOTE: There is currently no equivalent getter for the dynamic controls
	 *    list.  This is intentional.  There has so far been no need to directly
	 *    access the dynamic controls list (use the published list from
	 *    {@link #getControls()} instead), and accessing the dynamic controls
	 *    could cause synchronization/multithreading problems, since the
	 *    dynamic controls list is likely to be mutated in a thread that is not
	 *    the program's main thread.
	 */
	protected List</*@NonNull*/ InputControl> getConstControls()
	{
		return Collections.unmodifiableList(this.constControls);
	}

	// -------------------------------------------------------------------------
	//                    Implementation : IInputDevice
	// -------------------------------------------------------------------------
	/**
	 *  A list of all controls that have been activated on this device since
	 *  it was instantiated, plus any controls known at compile-time that were
	 *  added by the deriving class(es).
	 *  <p>
	 *  This will expose a copy of the combined constant+dynamic control lists.
	 *  The copy is only mutated by the thread calling getControls(), and that
	 *  mutation is synchronized with any threads that might be modifying
	 *  the dynamic control list.  This makes it safe for the caller to perform
	 *  operations on it (ex: iteration) without using any synchronization
	 *  primitives.
	 *  @see org.avalancherobotics.standalone.interfaces.IInputDevice#getControls()
	 */
	@Override
	public /*@NonNull*/ List</*@NonNull*/ ? extends IInputDevice.IControl> getControls()
	{
		synchronized(this.getSynchronizationObject()) {
			replicateControlsListIncrementally();
		}
		return Collections.unmodifiableList(this.publishedControls);
	}

	private void replicateControlsListFromScratch()
	{
		this.publishedControls.clear();
		this.publishedControls.ensureCapacity(
			this.constControls.size() + this.dynamicControls.size());
		this.publishedControls.addAll(this.constControls);
		this.publishedControls.addAll(this.dynamicControls);
	}

	private void replicateControlsListIncrementally()
	{
		// A size comparison is probably sufficient: we never remove
		// elements, alter elements, shuffle, or any other kinds of
		// mutation.  The only mutation done on this ArrayList is the
		// appending of elements to its end.  This makes size() an
		// acceptable test for (in)equality.
		int dynAfterCount  = this.dynamicControls.size();
		int dynBeforeCount = this.publishedControls.size() - this.constControls.size();
		if ( dynAfterCount > dynBeforeCount )
		{
			// Here, we lean on the assumption of a strictly-expanding
			// InputBooleanControl list even more, because it allows us
			// to avoid copying the entire controls list every time
			// replication is required.  Instead, we only need to copy
			// over the new elements.
			this.publishedControls.addAll(
				this.dynamicControls.subList(dynBeforeCount,dynAfterCount));
		}
	}
}