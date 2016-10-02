package org.avalancherobotics.standalone.internal;

// Implementation NOTE: The standalone package shall never depend on the
//   org.avalancherobotics.library package, the org.avalancherobotics.desktop package,
//   or any other implementors.  It should also use only Java code that will
//   be found on all platforms (ex: Android + Desktop), which means that only
//   things in java.util are remotely safe.
//

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.RandomAccess;

public class ArrayQueue<E> extends ArrayDeque<E> implements RandomAccess
{
	private /*@NonNull*/ ArrayList<E> randomAccessCache = new ArrayList<>();
	private boolean randomAccessCacheIsStale = true;

	private void beforeWrite()
	{
		randomAccessCacheIsStale = true;
	}

	private void refreshCache()
	{
		randomAccessCache.clear();
		randomAccessCache.addAll(this);
		randomAccessCacheIsStale = false;
	}

	//private void ensureCacheValidity()
	//{
	//	if ( randomAccessCacheIsStale )
	//		refreshCache();
	//}

	/** Constructs an empty array queue with an initial capacity sufficient to hold 16 elements. */
	public ArrayQueue()
	{
		super();
	}

	/** Constructs a queue containing the elements of the specified collection, in the order they are returned by the collection's iterator. */
	public ArrayQueue(Collection<? extends E> c)
	{
		super(c);
	}

	/** Constructs an empty array queue with an initial capacity sufficient to hold the specified number of elements. */
	public ArrayQueue(int numElements)
	{
		super(numElements);
	}

	/** Returns the element at the specified position in this list. */
	public E get(int index)
	{
		if ( randomAccessCacheIsStale )
		{
			// Peephole optimization: avoid cache thrashing on access patterns
			// that ArrayDeque can already handle efficiently.
			if ( index == 0 )
			{
				try {
					return this.getFirst();
				}
				catch( java.util.NoSuchElementException e ) {
					throw new IndexOutOfBoundsException();
				}
			}
			else if ( index == (this.size()-1) )
			{
				try {
					return this.getLast();
				}
				catch( java.util.NoSuchElementException e ) {
					throw new IndexOutOfBoundsException();
				}
			}

			refreshCache();
		}
		return randomAccessCache.get(index);
	}

	/** Adds all of the elements in the specified collection to this collection. */
	@Override public boolean addAll(Collection<? extends E> c)
	{
		beforeWrite();
		return super.addAll(c);
	}

	/** Removes all of this collection's elements that are also contained in the specified collection. */
	@Override public boolean removeAll(Collection<?> c)
	{
		beforeWrite();
		return super.removeAll(c);
	}

	/** Inserts the specified element at the end of this deque. */
	@Override public boolean add(E e)
	{
		beforeWrite();
		return super.add(e);
	}

	/** Inserts the specified element at the front of this deque. */
	@Override public void addFirst(E e)
	{
		beforeWrite();
		super.addFirst(e);
	}

	/** Inserts the specified element at the end of this deque. */
	@Override public void addLast(E e)
	{
		beforeWrite();
		super.addLast(e);
	}

	/** Removes all of the elements from this deque. */
	@Override public void clear()
	{
		beforeWrite();
		super.clear();
	}

	/** Inserts the specified element at the end of this deque. */
	@Override public boolean offer(E e)
	{
		beforeWrite();
		return super.offer(e);
	}

	/** Inserts the specified element at the front of this deque. */
	@Override public boolean offerFirst(E e)
	{
		beforeWrite();
		return super.offerFirst(e);
	}

	/** Inserts the specified element at the end of this deque. */
	@Override public boolean offerLast(E e)
	{
		beforeWrite();
		return super.offerLast(e);
	}

	/** Retrieves and removes the head of the queue represented by this deque (in other words, the first element of this deque), or returns null if this deque is empty. */
	@Override public E poll()
	{
		beforeWrite();
		return super.poll();
	}

	/** Retrieves and removes the first element of this deque, or returns null if this deque is empty. */
	@Override public E pollFirst()
	{
		beforeWrite();
		return super.pollFirst();
	}

	/** Retrieves and removes the last element of this deque, or returns null if this deque is empty. */
	@Override public E pollLast()
	{
		beforeWrite();
		return super.pollLast();
	}

	/** Pops an element from the stack represented by this deque. */
	@Override public E pop()
	{
		beforeWrite();
		return super.pop();
	}

	/** Pushes an element onto the stack represented by this deque. */
	@Override public void push(E e)
	{
		beforeWrite();
		super.push(e);
	}

	/** Retrieves and removes the head of the queue represented by this deque. */
	@Override public E remove()
	{
		beforeWrite();
		return super.remove();
	}

	/** Removes a single instance of the specified element from this deque. */
	@Override public boolean remove(Object o)
	{
		beforeWrite();
		return super.remove(o);
	}

	/** Retrieves and removes the first element of this deque. */
	@Override public E removeFirst()
	{
		beforeWrite();
		return super.removeFirst();
	}

	/** Removes the first occurrence of the specified element in this deque (when traversing the deque from head to tail). */
	@Override public boolean removeFirstOccurrence(Object o)
	{
		beforeWrite();
		return super.removeFirstOccurrence(o);
	}

	/** Retrieves and removes the last element of this deque. */
	@Override public E removeLast()
	{
		beforeWrite();
		return super.removeLast();
	}

	/** Removes the last occurrence of the specified element in this deque (when traversing the deque from head to tail). */
	@Override public boolean removeLastOccurrence(Object o)
	{
		beforeWrite();
		return super.removeLastOccurrence(o);
	}

}
