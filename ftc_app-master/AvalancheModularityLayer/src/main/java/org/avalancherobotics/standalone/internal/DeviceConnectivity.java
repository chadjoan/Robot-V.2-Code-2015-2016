package org.avalancherobotics.standalone.internal;

// Implementation NOTE: The standalone package shall never depend on the
//   org.avalancherobotics.library package, the org.avalancherobotics.desktop package,
//   or any other implementors.  It should also use only Java code that will
//   be found on all platforms (ex: Android + Desktop), which means that only
//   things in java.util are remotely safe.
//

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

/**
 *  This class provides a reusable way to establish a hub-and-spoke relationship
 *  between objects.
 *  <p>
 *  This can be used to implement the device&lt;-&gt;controller relationship
 *  found in a number of devices (such as
 *  {@link org.avalancherobotics.standalone.interfaces.IDcMotor} and
 *  {@link org.avalancherobotics.standalone.interfaces.IDcMotorController}).
 */
public class DeviceConnectivity
{
	/** */
	public interface Hub<INDEX_TYPE>
	{
		/** */
		public /*@NonNull*/ HubPorts<INDEX_TYPE> getHubPorts();
	}

	/** */
	public interface Spoke<INDEX_TYPE>
	{
		/** */
		public /*@NonNull*/ SpokePort<INDEX_TYPE> getSpokePort();
	}

	/** */
	public interface HubPorts<INDEX_TYPE>
	{
		/**
		 *  The hub owning these ports shall override this method to provide
		 *  connectivity methods with a way to probe the port at the given index.
		 *  <p>
		 *  If the hub does not have any spokes connected to it at the port
		 *  given by 'index', then it shall return null from this method.
		 */
		public Spoke<INDEX_TYPE> getSpoke(INDEX_TYPE index);

		/**
		 *  The hub owning these ports shall override this method to provide
		 *  a controlled way to manipulate its internal list of spokes.
		 *  <p>
		 *  If 'spoke' is null, then the Hub owning these ports shall remove
		 *  the spoke at the specified 'index' from its list of spokes.
		 *  If 'spoke is null and the port given by 'index' already lacked a
		 *  connection to a spoke, then this method may do nothing (but it
		 *  shall not throw any exceptions).
		 *  <p>
		 *  This operation shall not manipulate the 'spoke' in any way.  Only
		 *  the hub is responsible for activity in this case.  This method is
		 *  a primitive for implementing other connectivity routines, and those
		 *  higher level routines will be responsible for ensuring things
		 *  like connection symmetry.
		 */
		public void setSpoke(INDEX_TYPE index, Spoke<INDEX_TYPE> spoke);
	}

	/**
	 *  An implementation of the HubPorts class that wraps an ArrayList object
	 *  that is owned by the hub.
	 *  <p>
	 *  This implementation assumes that the caller will not manipulate the
	 *  underlying ArrayList anymore after placing it into the
	 *  HubPortsAsArrayList object.  The caller must use {@link #setSpoke} to
	 *  affect any further changes on the ArrayList.
	 *  In other words: the caller assigns ownership of the ArrayList object
	 *  over to the HubPortsAsArrayList object.
	 */
	public static class HubPortsAsArrayList<E extends Spoke<Integer>>
		implements HubPorts<Integer>
	{
		private /*@NonNull*/ Class<E>     elementClassInfo;
		private /*@NonNull*/ ArrayList<E> spokes;
		private int spokeCount = 0;

		/** */
		public HubPortsAsArrayList(
			/*@NonNull*/ ArrayList<E> listToWrap,
			/*@NonNull*/ Class<E>     elementClassInfo )
		{
			this.elementClassInfo = elementClassInfo;
			this.setList(listToWrap);
		}

		private void countSpokes()
		{
			int count = 0;
			for( Spoke<Integer> spoke : this.spokes )
				if ( spoke != null )
					count++;
			this.spokeCount = count;
		}

		/**
		 *  The hub may extend the class to gain access to this method if the
		 *  spokes list ever changes and the hub needs a way to update its
		 *  HubPorts object.
		 */
		protected void setList( /*@NonNull*/ ArrayList<E> listToWrap )
		{
			if ( listToWrap == null )
				throw new IllegalArgumentException("The listToWrap parameter must be non-null.");
			this.spokes = listToWrap;
			countSpokes();
		}

		/**
		 *  Obligatory complement to {@link #setList}.
		 */
		protected /*@NonNull*/ ArrayList<E> getList()
		{
			return this.spokes;
		}

		/**
		 *  @return null if the given 'port' has no Spoke connected to it, or if
		 *      the given 'port' is out of bounds for the underlying ArrayList.
		 */
		@Override
		public E getSpoke(Integer port)
		{
			if ( port < 0 || port >= this.spokes.size() )
				return null;
			return this.spokes.get(port);
		}

		/**
		 *  Sets the element in the underlying ArrayList at the index given
		 *  by 'port' to reference the given 'spoke' object.
		 *  <p>
		 *  This will enlarge the ArrayList as necessary to ensure there is
		 *  an open slot for the port.
		 *  <p>
		 *  'port' must be a zero or positive integer.
		 */
		@Override
		public void setSpoke(Integer port, Spoke<Integer> spoke)
		{
			// Precondition.
			if ( port < 0 )
				throw new IndexOutOfBoundsException("Attempt to connect to a negative port number ("+ port+ ").");

			// Ensure that there are enough ports for the spoke.
			int sizeBefore = this.spokes.size();
			if ( sizeBefore <= port )
			{
				this.spokes.ensureCapacity(port+1);
				for(int i = sizeBefore; i <= port; i++)
					this.spokes.add(null);
			}

			// Track spoke count.
			E spokeBefore = this.spokes.get(port);
			if ( spokeBefore == null && spoke != null )
				this.spokeCount++;
			else
			if ( spokeBefore != null && spoke == null )
				this.spokeCount--;

			// Plug it in.
			this.spokes.set(port, elementClassInfo.cast(spoke));
		}

		/**
		 *  Throws an IndexOutOfBoundsException with a descriptive error message
		 *  if there is no connection on the given 'port'.
		 *
		 *  @param port      The port to check for connectivity.
		 *  @param spokeNoun The text that should appear in the error message
		 *                   to represent the spoke, if an error occurs.
		 *                   Examples: "motor" or "servo".
		 *  @param hubNoun   The text that should appear in the error message
		 *                   to represent the hub, if an error occurs.
		 *                   Example: "controller".
		 */
		public void enforceConnection(int port, String spokeNoun, String hubNoun)
		{
			if ( this.getSpoke(port) == null )
			{
				StringBuilder s = new StringBuilder();
				s.append("There is no "+ spokeNoun+ " number "+ port+ " connected to this "+ hubNoun+ ".  ");
				if ( this.spokeCount == 0 )
					s.append("This "+ hubNoun+ " has no connections.");
				else
				{
					s.append("The connected "+ spokeNoun+ " numbers are as follows: {");

					for(int i = 0; i < spokes.size(); i++)
					{
						if ( spokes.get(i) != null )
						{
							s.append(i);
							s.append(",");
						}
					}

					s.setCharAt(s.length()-1,'}');
				}

				throw new IndexOutOfBoundsException(s.toString());
			}
		}
	}

	/** */
	public interface SpokePort<INDEX_TYPE>
	{
		/**
		 *  The spoke owning this port shall override this method to provide
		 *  connectivity methods with a way to determine which hub the spoke
		 *  is connected to.
		 *  <p>
		 *  If the spoke is not connected to a hub when this method is called,
		 *  then it shall return null.
		 */
		public Hub<INDEX_TYPE> getHub();

		/** */
		public INDEX_TYPE getIndexOnHub();

		/**
		 *  The spoke owning this port shall override this method to provide
		 *  a controlled way for connectivity methods to assign it a hub
		 *  reference.
		 *  <p>
		 *  If the 'hub' parameter is passed a null value, then the Spoke owning
		 *  this port shall set its internal hub reference to null and ignore
		 *  the 'index' parameter.
		 *  If the 'hub' parameter is passed a null value and spoke already had
		 *  no hub connection, then this method may do nothing (but it shall
		 *  not throw any exceptions).
		 *  <p>
		 *  This operation shall not manipulate the 'hub' in any way.  Only
		 *  the spoke is responsible for activity in this case.  This method is
		 *  a primitive for implementing other connectivity routines, and those
		 *  higher level routines will be responsible for ensuring things
		 *  like connection symmetry.
		 */
		public void setHub(Hub<INDEX_TYPE> hub, INDEX_TYPE index);
	}

//	/*
//	 *  A basic implementation of the SpokePort class that wraps a reference
//	 *  to a Hub object (presumably stored by the Spoke that owns the SpokePort)
//	 *  and a reference to an index.
//	 */
//	public class SpokePortAsReference<INDEX_TYPE> extends SpokePort<INDEX_TYPE>
//	{
//		private /*@NonNull*/ AtomicReference<Hub<INDEX_TYPE>> hubRef;
//		private /*@NonNull*/ AtomicReference<INDEX_TYPE> indexRef;
//
//		public SpokePortAsReference(
//			/*@NonNull*/ AtomicReference<Hub<INDEX_TYPE>> hubToWrap,
//			/*@NonNull*/ AtomicReference<INDEX_TYPE> indexToWrap
//	}

	/**
	 *  Connects the given Spoke to the given Hub while guaranteeing symmetry
	 *  on the new connection as well as any connections broken.
	 *  <p>
	 *  In this case, the symmetrical outcome for the new connection is defined
	 *  as this expression being equivalent to true:
	 *  (hub.getHubPorts().getSpoke(index) == spoke) &amp;&amp;
	 *  (spoke.getSpokePort().getHub() == hub) &amp;&amp;
	 *  (spoke.getSpokePort().getIndexOnHub() == index).
	 *  <p>
	 *  If the given 'spoke' was already connected to another hub, then
	 *  it will be disconnected as part of this operation.
	 *  <p>
	 *  If the given 'hub' already had a spoke connected on that port index,
	 *  then it will be disconnected as part of this operation.
	 */
	public static <T> void connect(/*@NonNull*/ Hub<T> hub, /*@NonNull*/ Spoke<T> spoke, T index)
	{
		HubPorts<T> hubPorts = hub.getHubPorts();
		SpokePort<T> spokePort = spoke.getSpokePort();

		// Make sure that the hub's port is free.
		Spoke<T> spokeBefore = hubPorts.getSpoke(index);
		if ( spokeBefore != null && spokeBefore != spoke )
			DeviceConnectivity.<T>disconnect(hub, spokeBefore);

		// Ensure that the spoke is not already connected to
		// another hub.
		Hub<T> hubBefore = spokePort.getHub();
		if ( hubBefore != null && hubBefore != hub )
			DeviceConnectivity.<T>disconnect(hubBefore, spoke);

		// Now we can finally create one side of the link.
		spokePort.setHub(hub, index);

		// Carefully create the other side of the link:
		// ensure that thrown exceptions will not result in a one-sided connection!
		try {
			hubPorts.setSpoke(index, spoke);
		}
		catch ( Exception e )
		{
			DeviceConnectivity.<T>disconnect(hub, spoke);
			throw e;
		}
	}

	/** */
	public static <INDEX_TYPE> void disconnect(Hub<INDEX_TYPE> hub, Spoke<INDEX_TYPE> spoke)
	{
		HubPorts<INDEX_TYPE>  hubPorts  = hub.getHubPorts();
		SpokePort<INDEX_TYPE> spokePort = spoke.getSpokePort();
		INDEX_TYPE index = spokePort.getIndexOnHub();

		hubPorts.setSpoke(index, null);
		spokePort.setHub(null, index);
	}
}