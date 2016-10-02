package org.avalancherobotics.standalone.input;

// Implementation NOTE: The standalone package shall never depend on the
//   org.avalancherobotics.library package, the org.avalancherobotics.desktop package,
//   or any other implementors.  It should also use only Java code that will
//   be found on all platforms (ex: Android + Desktop), which means that only
//   things in java.util are remotely safe.
//

import java.util.Collections;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import org.avalancherobotics.standalone.internal.UnimplementedException;

/**
 *  This class implements an overridable list of keycode constants.
 *  <p>
 *  This allows code that runs on multiple platforms to leverage key constants
 *  that exist on any platform implemented by this class.
 *  <p>
 *  Currently, key constant stubs are defined for these platforms:<ul>
 *  <li>Android</li>
 *  <li>Java AWT (Desktop)</li>
 *  </ul>
 *  <p>
 *  Any platform-specific implementation must provide key codes that map to
 *  all key codes defined in this class.  This is done by populating all of
 *  the key code fields with appropriate integer values from their platform's
 *  input handling API, and then then call {@link #registerAndroidKeyCodes()}
 *  and {@link #registerAwtKeyCodes()}.
 *  <p>
 *  Classes implementing completely novel systems that are neither Android
 *  nor AWT-based should add their system's list of key codes to this
 *  class, create a register*() method for registering the new fields, and
 *  update the documentation to reflect the new requirements.  This is a
 *  long-term ideal, so implementors can get work done on their own projects
 *  immediately by extending this class, providing their own field list,
 *  and having their code use their own KeyCodes extending class
 *  until their changes can be pulled into upstream.
 */
public abstract class KeyCodes
{
	/**
	 *  This value is used to indicate that the platform-specific KeyCodes
	 *  class deriving from KeyCodes has not implemented a keycode.
	 */
	public static final int UNIMPLEMENTED_KEYCODE = -1;

	/**
	 *  Thrown when {@link #registerKeyCode()} is called on a keyCode whose
	 *  value is equivalent to {@link #UNIMPLEMENTED_KEYCODE}.
	 */
	public final class UnimplementedKeyCodeException extends UnimplementedException
	{
		private /*@NonNull*/ String keyCodeName;
		
		/** */
		public final String getKeyCodeName() { return keyCodeName; }

		public UnimplementedKeyCodeException(
			/*@NonNull*/ String keyCodeName,
			/*@NonNull*/ String message
			)
		{
			super(message);
			this.keyCodeName = keyCodeName;
		}
	}

	private class KeyCodeEntry
	{
		public int keyCode;
		public /*@NonNull*/ LinkedList</*@NonNull*/ String> names = new LinkedList<>();

		public KeyCodeEntry( int keyCode, /*@NonNull*/ String firstName )
		{
			this.keyCode = keyCode;
			this.names.set(0,firstName);
		}

		@Override public String toString()
		{
			StringBuilder result = new StringBuilder(names.get(0));
			if ( names.size() >= 4 )
			{
				result.append(" (AKA ");
				for( String s : names.subList(1,names.size()-1) )
				{
					result.append(s);
					result.append(", ");
				}
				result.append("and ");
				result.append(names.get(names.size()-1));
				result.append(")");
			}
			else if ( names.size() == 3 )
			{
				result.append(" (AKA ");
				result.append(names.get(1));
				result.append(" and ");
				result.append(names.get(2));
				result.append(")");
			}
			else if ( names.size() == 2 )
			{
				result.append(" (AKA ");
				result.append(names.get(1));
				result.append(")");
			}
			return result.toString();
		}
	}

	private static KeyCodes defaults = null;

	private LinkedList<UnimplementedKeyCodeException> unimplementedKeyCodesList = null;
	private /*@NonNull*/ TreeMap<Integer,KeyCodeEntry> entriesByKeyCode = new TreeMap<>();
	private /*@NonNull*/ Hashtable<String,KeyCodeEntry> entriesByName = new Hashtable<>();
	private int highestKeyCode = 0;
	private int lowestKeyCode = 0;
	
	/**
	 *  Any implementations must override this constructor and then populate
	 *  all key code fields, then call {@link #registerAndroidKeyCodes()} and
	 *  {@link #registerAwtKeyCodes()}.
	 */
	public KeyCodes()
	{
	}

	/**
	 *  Get the defaults for the current platform that were established by an
	 *  earlier call to {@link #setDefaults()}.
	 *  <p>
	 *  If {@link #setDefaults()} has not been called by the time getDefaults()
	 *  is called, then a {@link NullPointerException} will be thrown.
	 */
	public static /*@NonNull*/ KeyCodes getDefaults()
	{
		if ( defaults == null )
			throw new NullPointerException(
				"Attempt to access KeyCodes.getDefaults() while it is null. "+
				"Please call KeyCodes.setDefaults(yourPlatformsKeyCodeObject) first.");
		return defaults;
	}

	/**
	 *  Sets the default set of key codes to a platform-specific instance
	 *  defined by the 'defaults' parameter.
	 *  <p>
	 *  This should be called during initialization, preferably right after
	 *  main() is entered.  It should otherwise be called at some other point
	 *  before execution of input processing code that might call
	 *  {@link #getDefaults()}.
	 */
	public static void setDefaults(/*@NonNull*/ KeyCodes _defaults)
	{
		if ( _defaults == null )
			throw new IllegalArgumentException("The _defaults parameter must be non-null.");
		KeyCodes.defaults = _defaults;
	}

	/** Returns the highest possible key code that is registered in this set
	 *  of key codes.
	 */
	public int getHighestKeyCode()
	{
		return this.highestKeyCode;
	}
	
	/** Returns the lowest possible key code that is registered in this set
	 *  of key codes.
	 */
	public int getLowestKeyCode()
	{
		return this.lowestKeyCode;
	}

	/**
	 *  Get the names associated with the given keyCode and format them into
	 *  a single string (ex: "AWT_0 (AKA ANDROID_0)").
	 */
	public String getNamesAsStr(int keyCode)
	{
		KeyCodeEntry entryByCode = entriesByKeyCode.get(keyCode);
		if ( entryByCode == null )
			return null;
		return entryByCode.toString();
	}

	/**
	 *  Get the names, as a list, associated with the given keyCode.
	 */
	public List</*@NonNull*/ String> getNames(int keyCode)
	{
		KeyCodeEntry entryByCode = entriesByKeyCode.get(keyCode);
		if ( entryByCode == null )
			return null;
		return Collections.unmodifiableList(entryByCode.names);
	}

	/**
	 *  Used by {@link #registerAndroidKeyCodes()} and
	 *  {@link #registerAwtKeyCodes()} to register keyCode-name
	 *  pairs.
	 *  <p>
	 *  This can also be used by an extending class to define additional
	 *  key codes that are not known on any of the given platforms.  This will
	 *  allow those key codes to be have names (so they can be printed sensibly)
	 *  and fall within the range of known key codes (so code that allocates an
	 *  array large enough to hold all possible keycodes will not throw an
	 *  IndexOutOfBoundsException when it encounters a new special keycode).
	 *  <p>
	 *  Attempting to register the same keyCode-name pair twice will result
	 *  in no changes to the KeyCodes object.
	 */
	protected final void registerKeyCode( int keyCode, /*@NonNull*/ String name )
		throws UnimplementedKeyCodeException
	{
		UnimplementedKeyCodeException e =
			registerKeyCodeCarefully(keyCode, name);
		if ( e != null )
			throw e;
	}

	// To avoid potential java slowdowns due to large numbers of exceptions
	// being thrown during key code probing, this version of the method will
	// return the UnimplementedKeyCodeException exception instead of throwing
	// it.
	// If the return value is null, then everything went fine.
	// This becomes the basis for both the throwing and the recording versions
	// of this method.
	private UnimplementedKeyCodeException
		registerKeyCodeCarefully( int keyCode, /*@NonNull*/ String name )
	{
		if ( name == null )
			throw new IllegalArgumentException("The name parameter must be non-null.");
		if ( keyCode == UNIMPLEMENTED_KEYCODE )
			return new UnimplementedKeyCodeException(
				name,
				"The keycode for "+name+" has not been defined by "+
				"the class extending the KeyCodes class.  It must be defined for "+
				"key code registration to work.");

		// Check for situations where this field (key code name) might already
		// have a value.  Sometimes this is fine: we're just adding the same
		// keyCode-name pair over again, and we can return peacefully.  Other
		// times there might be something screwy going on, like a keyCode being
		// redefined to something else (which is probably unintentional).
		KeyCodeEntry entryByName = entriesByName.get(name);
		if ( entryByName != null )
		{
			if ( entryByName.keyCode == keyCode )
				return null; // Do nothing; this keyCode-name pair is already defined.
			else
				// There is already an entry by this name, and it isn't the same keyCode we are adding.
				throw new IllegalArgumentException(
					"The key code named '"+ name+ "' already has a key code value. "+
					"Its value is "+ entryByName.keyCode+ ".");
		}

		KeyCodeEntry entryByCode = entriesByKeyCode.get(keyCode);
		if ( entryByCode != null )
		{
			// The key code already has an entry, but the name is new.
			// In this case, we just add the name to this entry's list of names,
			// and update the entriesByName map.
			entryByCode.names.add(name);
			entriesByName.put(name, entryByCode);
			return null;
		}

		// If we get this far, then we have a completely novel key code.
		// Now it makes sense to make a new entry and expand the low/high
		// trackers.
		KeyCodeEntry newEntry = new KeyCodeEntry(keyCode, name);
		entriesByKeyCode.put(keyCode, newEntry);
		entriesByName.put(name, newEntry);
		highestKeyCode = Math.max(highestKeyCode, keyCode);
		lowestKeyCode = Math.min(lowestKeyCode, keyCode);

		return null;
	}

	// This is a version of registerKeyCode that records registration failures
	// instead of throwing them.
	private void register(
		/*@NonNull*/ LinkedList<UnimplementedKeyCodeException> unimplementedList,
		int keyCode,
		/*@NonNull*/ String name
		)
	{
		UnimplementedKeyCodeException e =
			registerKeyCodeCarefully(keyCode,name);
		if ( e != null )
			unimplementedList.add(e);
	}

	/**
	 *  Implementors may use this to define non-native key codes that do not
	 *  have any sensible mapping from native key codes.
	 *  <p>
	 *  To be effective, this must be called after all manually defined
	 *  key codes have been registered.  In other words, this should be
	 *  called /after/ {@link #registerAndroidKeyCodes()} or
	 *  {@link #registerAwtKeyCodes()}, whichever is native to your system.
	 *  <p>
	 *  The caller must also assign the returned value into whatever field
	 *  they are populating with an arbitrary key code number.
	 */
	protected int assignAndRegisterArbitraryKeyCode( /*@NonNull*/ String name )
		throws UnimplementedKeyCodeException
	{
		int newKeyCode = Math.max(getHighestKeyCode()+1, 1024);
		registerKeyCode(newKeyCode, name);
		return newKeyCode;
	}

	/** */
	public final List<UnimplementedKeyCodeException>
		getUnimplementedKeyCodesList()
	{
		if ( unimplementedKeyCodesList == null )
			return null;
		return Collections.unmodifiableList(unimplementedKeyCodesList);
	}
	
	/**
	 *  ALL classes implementing the keyCodes class must call this after
	 *  calling {@link #registerAndroidKeyCodes()} and
	 *  {@link #registerAwtKeyCodes()}.
	 *  <p>
	 *  This method tests all key code fields known to the KeyCodes class and
	 *  throws an {@link #UnimplementedKeyCodeException} if there are any
	 *  still holding a value of {@link #UNIMPLEMENTED_KEYCODE}.
	 */
	protected final void testAllKeyCodeFields()
	{
		/*@NonNull*/ LinkedList<UnimplementedKeyCodeException> unimplementedList
			= new LinkedList<>();

		// Registration is idempotent, so we can do it repeatedly and use
		// it as a test for any registrations that the implementor might have
		// missed.
		registerAndroidKeyCodesNoThrow(unimplementedList);
		registerAwtKeyCodesNoThrow(unimplementedList);
		// Future additions go here.

		setUnimplementedKeyCodesListAndMaybeThrow(unimplementedList);
	}

	private void setUnimplementedKeyCodesListAndMaybeThrow(
		/*@NonNull*/ LinkedList<UnimplementedKeyCodeException> unimplementedList)
	{
		if ( unimplementedList.size() > 0 )
		{
			this.unimplementedKeyCodesList = unimplementedList;
			throw unimplementedList.get(0);
		}
		else
		{
			this.unimplementedKeyCodesList = null;
		}
	}

	/**
	 *  ALL classes implementing the keyCodes class must call this.
	 *  <p>
	 *  This will call {@link #registerKeyCode()} on all Android native
	 *  keyCodes, which will give them runtime name strings and calculate
	 *  useful values like {@link #getHighestKeyCode()} and
	 *  {@link #getLowestKeyCode()}.  This must be called even on non-Android
	 *  platforms, because some code may expect the Android key codes to be
	 *  populated with reasonable replacements from the actual host platform's
	 *  key code list.
	 *  <p>
	 *  If any constants have not been populated, this will throw an
	 *  {@link #UnimplementedKeyCodeException}
	 *  to indicate that the implementor has not thoroughly defined all
	 *  key codes.  Remember: the implementor must define ALL keycode constants,
	 *  even constants for other platforms besides the host, by mapping host
	 *  constants to foreign constants or providing unique arbitrary values.
	 *  <p>
	 *  If an {@link #UnimplementedKeyCodeException} is thrown,
	 *  all valid keycodes will be added and the list of failures can be
	 *  inspected by the caller by (catching the exception and) calling
	 *  {@link #getUnimplementedKeyCodesList()}.
	 */
	protected final void registerAndroidKeyCodes()
	{
		/*@NonNull*/ LinkedList<UnimplementedKeyCodeException> unimplementedList
			= new LinkedList<>();
		registerAndroidKeyCodesNoThrow(unimplementedList);
		setUnimplementedKeyCodesListAndMaybeThrow(unimplementedList);
	}

	/**
	 *  ALL classes implementing the keyCodes class must call this.
	 *  <p>
	 *  This will call {@link #registerKeyCode()} on all AWT native
	 *  keyCodes, which will give them runtime name strings and calculate
	 *  useful values like {@link #getHighestKeyCode()} and
	 *  {@link #getLowestKeyCode()}.  This must be called even on non-AWT
	 *  platforms, because some code may expect the AWT key codes to be
	 *  populated with reasonable replacements from the actual host platform's
	 *  key code list.
	 *  <p>
	 *  If any constants have not been populated, this will throw an
	 *  {@link #UnimplementedKeyCodeException}
	 *  to indicate that the implementor has not thoroughly defined all
	 *  key codes.  Remember: the implementor must define ALL keycode constants,
	 *  even constants for other platforms besides the host, by mapping host
	 *  constants to foreign constants or providing unique arbitrary values.
	 *  <p>
	 *  If an {@link #UnimplementedKeyCodeException} is thrown,
	 *  all valid keycodes will be added and the list of failures can be
	 *  inspected by the caller by (catching the exception and) calling
	 *  {@link #getUnimplementedKeyCodesList()}.
	 */
	protected final void registerAwtKeyCodes()
	{
		/*@NonNull*/ LinkedList<UnimplementedKeyCodeException> unimplementedList
			= new LinkedList<>();
		registerAwtKeyCodesNoThrow(unimplementedList);
		setUnimplementedKeyCodesListAndMaybeThrow(unimplementedList);
	}


	/**
	 *  ALL classes implementing the keyCodes class must call this.
	 *  <p>
	 *  This will call {@link #registerKeyCode()} on all MOUSE
	 *  keyCodes, which will give them runtime name strings and calculate
	 *  useful values like {@link #getHighestKeyCode()} and
	 *  {@link #getLowestKeyCode()}.  This must be called even on all
	 *  platforms, because some code may expect the MOUSE key codes to be
	 *  populated with reasonable replacements from the actual host platform's
	 *  key code list.
	 *  <p>
	 *  If any constants have not been populated, this will throw an
	 *  {@link #UnimplementedKeyCodeException}
	 *  to indicate that the implementor has not thoroughly defined all
	 *  key codes.  Remember: the implementor must define ALL keycode constants,
	 *  even constants for other platforms besides the host, by mapping host
	 *  constants to foreign constants or providing unique arbitrary values.
	 *  <p>
	 *  If an {@link #UnimplementedKeyCodeException} is thrown,
	 *  all valid keycodes will be added and the list of failures can be
	 *  inspected by the caller by (catching the exception and) calling
	 *  {@link #getUnimplementedKeyCodesList()}.
	 *  <p>
	 *  The caller may be interested in calling {@link #assignArbitraryMouseKeyCodes()}
	 *  if they do not plan on assigning any static values to the mouse key
	 *  codes.
	 *
	 *  @see #assignArbitraryMouseKeyCodes()
	 */
	protected final void registerMouseKeyCodes()
	{
		/*@NonNull*/ LinkedList<UnimplementedKeyCodeException> unimplementedList
			= new LinkedList<>();
		registerMouseKeyCodesNoThrow(unimplementedList);
		setUnimplementedKeyCodesListAndMaybeThrow(unimplementedList);
	}

	private void registerMouseKeyCodesNoThrow(
		/*@NonNull*/ LinkedList<UnimplementedKeyCodeException> unimplementedList)
	{
		// AWT does not define keycodes for mouse button presses, so we
		// define our own.  Other systems are welcome to use these as well.
		register(unimplementedList,MOUSE_BUTTONS[0],"MOUSE_BUTTON0");
		register(unimplementedList,MOUSE_BUTTONS[1],"MOUSE_BUTTON1");
		register(unimplementedList,MOUSE_BUTTONS[2],"MOUSE_BUTTON2");
		register(unimplementedList,MOUSE_BUTTONS[3],"MOUSE_BUTTON3");
		register(unimplementedList,MOUSE_BUTTONS[4],"MOUSE_BUTTON4");
		register(unimplementedList,MOUSE_BUTTONS[5],"MOUSE_BUTTON5");
		register(unimplementedList,MOUSE_BUTTONS[6],"MOUSE_BUTTON6");
		register(unimplementedList,MOUSE_BUTTONS[7],"MOUSE_BUTTON7");
		register(unimplementedList,MOUSE_BUTTONS[8],"MOUSE_BUTTON8");
	}

	/**
	 *  Assigns arbitrary numbers to the MOUSE_BUTTON* keycodes.
	 *  <p>
	 *  This should be called on all platforms that don't have native keyCodes
	 *  to assign to these.  This call should happen before calling
	 *  {@link #registerMouseKeyCodesNoThrow(LinkedList)}.
	 */
	protected final void assignArbitraryMouseKeyCodes()
	{
		MOUSE_BUTTONS[0] = assignAndRegisterArbitraryKeyCode("MOUSE_NOBUTTON");
		MOUSE_BUTTONS[1] = assignAndRegisterArbitraryKeyCode("MOUSE_BUTTON1");
		MOUSE_BUTTONS[2] = assignAndRegisterArbitraryKeyCode("MOUSE_BUTTON2");
		MOUSE_BUTTONS[3] = assignAndRegisterArbitraryKeyCode("MOUSE_BUTTON3");
		MOUSE_BUTTONS[4] = assignAndRegisterArbitraryKeyCode("MOUSE_BUTTON4");
		MOUSE_BUTTONS[5] = assignAndRegisterArbitraryKeyCode("MOUSE_BUTTON5");
		MOUSE_BUTTONS[6] = assignAndRegisterArbitraryKeyCode("MOUSE_BUTTON6");
		MOUSE_BUTTONS[7] = assignAndRegisterArbitraryKeyCode("MOUSE_BUTTON7");
		MOUSE_BUTTONS[8] = assignAndRegisterArbitraryKeyCode("MOUSE_BUTTON8");
	}

	private void registerAndroidKeyCodesNoThrow(
		/*@NonNull*/ LinkedList<UnimplementedKeyCodeException> unimplementedList)
	{
		// =========================================================================
		// ------------------ android.view.KeyEvent definitions --------------------
		// -------------------------------------------------------------------------
		register(unimplementedList,ANDROID_0,"ANDROID_0");
		register(unimplementedList,ANDROID_1,"ANDROID_1");
		register(unimplementedList,ANDROID_11,"ANDROID_11");
		register(unimplementedList,ANDROID_12,"ANDROID_12");
		register(unimplementedList,ANDROID_2,"ANDROID_2");
		register(unimplementedList,ANDROID_3,"ANDROID_3");
		register(unimplementedList,ANDROID_3D_MODE,"ANDROID_3D_MODE");
		register(unimplementedList,ANDROID_4,"ANDROID_4");
		register(unimplementedList,ANDROID_5,"ANDROID_5");
		register(unimplementedList,ANDROID_6,"ANDROID_6");
		register(unimplementedList,ANDROID_7,"ANDROID_7");
		register(unimplementedList,ANDROID_8,"ANDROID_8");
		register(unimplementedList,ANDROID_9,"ANDROID_9");
		register(unimplementedList,ANDROID_A,"ANDROID_A");
		register(unimplementedList,ANDROID_ALT_LEFT,"ANDROID_ALT_LEFT");
		register(unimplementedList,ANDROID_ALT_RIGHT,"ANDROID_ALT_RIGHT");
		register(unimplementedList,ANDROID_APOSTROPHE,"ANDROID_APOSTROPHE");
		register(unimplementedList,ANDROID_APP_SWITCH,"ANDROID_APP_SWITCH");
		register(unimplementedList,ANDROID_ASSIST,"ANDROID_ASSIST");
		register(unimplementedList,ANDROID_AT,"ANDROID_AT");
		register(unimplementedList,ANDROID_AVR_INPUT,"ANDROID_AVR_INPUT");
		register(unimplementedList,ANDROID_AVR_POWER,"ANDROID_AVR_POWER");
		register(unimplementedList,ANDROID_B,"ANDROID_B");
		register(unimplementedList,ANDROID_BACK,"ANDROID_BACK");
		register(unimplementedList,ANDROID_BACKSLASH,"ANDROID_BACKSLASH");
		register(unimplementedList,ANDROID_BOOKMARK,"ANDROID_BOOKMARK");
		register(unimplementedList,ANDROID_BREAK,"ANDROID_BREAK");
		register(unimplementedList,ANDROID_BRIGHTNESS_DOWN,"ANDROID_BRIGHTNESS_DOWN");
		register(unimplementedList,ANDROID_BRIGHTNESS_UP,"ANDROID_BRIGHTNESS_UP");
		register(unimplementedList,ANDROID_BUTTON_1,"ANDROID_BUTTON_1");
		register(unimplementedList,ANDROID_BUTTON_10,"ANDROID_BUTTON_10");
		register(unimplementedList,ANDROID_BUTTON_11,"ANDROID_BUTTON_11");
		register(unimplementedList,ANDROID_BUTTON_12,"ANDROID_BUTTON_12");
		register(unimplementedList,ANDROID_BUTTON_13,"ANDROID_BUTTON_13");
		register(unimplementedList,ANDROID_BUTTON_14,"ANDROID_BUTTON_14");
		register(unimplementedList,ANDROID_BUTTON_15,"ANDROID_BUTTON_15");
		register(unimplementedList,ANDROID_BUTTON_16,"ANDROID_BUTTON_16");
		register(unimplementedList,ANDROID_BUTTON_2,"ANDROID_BUTTON_2");
		register(unimplementedList,ANDROID_BUTTON_3,"ANDROID_BUTTON_3");
		register(unimplementedList,ANDROID_BUTTON_4,"ANDROID_BUTTON_4");
		register(unimplementedList,ANDROID_BUTTON_5,"ANDROID_BUTTON_5");
		register(unimplementedList,ANDROID_BUTTON_6,"ANDROID_BUTTON_6");
		register(unimplementedList,ANDROID_BUTTON_7,"ANDROID_BUTTON_7");
		register(unimplementedList,ANDROID_BUTTON_8,"ANDROID_BUTTON_8");
		register(unimplementedList,ANDROID_BUTTON_9,"ANDROID_BUTTON_9");
		register(unimplementedList,ANDROID_BUTTON_A,"ANDROID_BUTTON_A");
		register(unimplementedList,ANDROID_BUTTON_B,"ANDROID_BUTTON_B");
		register(unimplementedList,ANDROID_BUTTON_C,"ANDROID_BUTTON_C");
		register(unimplementedList,ANDROID_BUTTON_L1,"ANDROID_BUTTON_L1");
		register(unimplementedList,ANDROID_BUTTON_L2,"ANDROID_BUTTON_L2");
		register(unimplementedList,ANDROID_BUTTON_MODE,"ANDROID_BUTTON_MODE");
		register(unimplementedList,ANDROID_BUTTON_R1,"ANDROID_BUTTON_R1");
		register(unimplementedList,ANDROID_BUTTON_R2,"ANDROID_BUTTON_R2");
		register(unimplementedList,ANDROID_BUTTON_SELECT,"ANDROID_BUTTON_SELECT");
		register(unimplementedList,ANDROID_BUTTON_START,"ANDROID_BUTTON_START");
		register(unimplementedList,ANDROID_BUTTON_THUMBL,"ANDROID_BUTTON_THUMBL");
		register(unimplementedList,ANDROID_BUTTON_THUMBR,"ANDROID_BUTTON_THUMBR");
		register(unimplementedList,ANDROID_BUTTON_X,"ANDROID_BUTTON_X");
		register(unimplementedList,ANDROID_BUTTON_Y,"ANDROID_BUTTON_Y");
		register(unimplementedList,ANDROID_BUTTON_Z,"ANDROID_BUTTON_Z");
		register(unimplementedList,ANDROID_C,"ANDROID_C");
		register(unimplementedList,ANDROID_CALCULATOR,"ANDROID_CALCULATOR");
		register(unimplementedList,ANDROID_CALENDAR,"ANDROID_CALENDAR");
		register(unimplementedList,ANDROID_CALL,"ANDROID_CALL");
		register(unimplementedList,ANDROID_CAMERA,"ANDROID_CAMERA");
		register(unimplementedList,ANDROID_CAPS_LOCK,"ANDROID_CAPS_LOCK");
		register(unimplementedList,ANDROID_CAPTIONS,"ANDROID_CAPTIONS");
		register(unimplementedList,ANDROID_CHANNEL_DOWN,"ANDROID_CHANNEL_DOWN");
		register(unimplementedList,ANDROID_CHANNEL_UP,"ANDROID_CHANNEL_UP");
		register(unimplementedList,ANDROID_CLEAR,"ANDROID_CLEAR");
		register(unimplementedList,ANDROID_COMMA,"ANDROID_COMMA");
		register(unimplementedList,ANDROID_CONTACTS,"ANDROID_CONTACTS");
		register(unimplementedList,ANDROID_CTRL_LEFT,"ANDROID_CTRL_LEFT");
		register(unimplementedList,ANDROID_CTRL_RIGHT,"ANDROID_CTRL_RIGHT");
		register(unimplementedList,ANDROID_D,"ANDROID_D");
		register(unimplementedList,ANDROID_DEL,"ANDROID_DEL");
		register(unimplementedList,ANDROID_DPAD_CENTER,"ANDROID_DPAD_CENTER");
		register(unimplementedList,ANDROID_DPAD_DOWN,"ANDROID_DPAD_DOWN");
		register(unimplementedList,ANDROID_DPAD_LEFT,"ANDROID_DPAD_LEFT");
		register(unimplementedList,ANDROID_DPAD_RIGHT,"ANDROID_DPAD_RIGHT");
		register(unimplementedList,ANDROID_DPAD_UP,"ANDROID_DPAD_UP");
		register(unimplementedList,ANDROID_DVR,"ANDROID_DVR");
		register(unimplementedList,ANDROID_E,"ANDROID_E");
		register(unimplementedList,ANDROID_EISU,"ANDROID_EISU");
		register(unimplementedList,ANDROID_ENDCALL,"ANDROID_ENDCALL");
		register(unimplementedList,ANDROID_ENTER,"ANDROID_ENTER");
		register(unimplementedList,ANDROID_ENVELOPE,"ANDROID_ENVELOPE");
		register(unimplementedList,ANDROID_EQUALS,"ANDROID_EQUALS");
		register(unimplementedList,ANDROID_ESCAPE,"ANDROID_ESCAPE");
		register(unimplementedList,ANDROID_EXPLORER,"ANDROID_EXPLORER");
		register(unimplementedList,ANDROID_F,"ANDROID_F");
		register(unimplementedList,ANDROID_F1,"ANDROID_F1");
		register(unimplementedList,ANDROID_F10,"ANDROID_F10");
		register(unimplementedList,ANDROID_F11,"ANDROID_F11");
		register(unimplementedList,ANDROID_F12,"ANDROID_F12");
		register(unimplementedList,ANDROID_F2,"ANDROID_F2");
		register(unimplementedList,ANDROID_F3,"ANDROID_F3");
		register(unimplementedList,ANDROID_F4,"ANDROID_F4");
		register(unimplementedList,ANDROID_F5,"ANDROID_F5");
		register(unimplementedList,ANDROID_F6,"ANDROID_F6");
		register(unimplementedList,ANDROID_F7,"ANDROID_F7");
		register(unimplementedList,ANDROID_F8,"ANDROID_F8");
		register(unimplementedList,ANDROID_F9,"ANDROID_F9");
		register(unimplementedList,ANDROID_FOCUS,"ANDROID_FOCUS");
		register(unimplementedList,ANDROID_FORWARD,"ANDROID_FORWARD");
		register(unimplementedList,ANDROID_FORWARD_DEL,"ANDROID_FORWARD_DEL");
		register(unimplementedList,ANDROID_FUNCTION,"ANDROID_FUNCTION");
		register(unimplementedList,ANDROID_G,"ANDROID_G");
		register(unimplementedList,ANDROID_GRAVE,"ANDROID_GRAVE");
		register(unimplementedList,ANDROID_GUIDE,"ANDROID_GUIDE");
		register(unimplementedList,ANDROID_H,"ANDROID_H");
		register(unimplementedList,ANDROID_HEADSETHOOK,"ANDROID_HEADSETHOOK");
		register(unimplementedList,ANDROID_HELP,"ANDROID_HELP");
		register(unimplementedList,ANDROID_HENKAN,"ANDROID_HENKAN");
		register(unimplementedList,ANDROID_HOME,"ANDROID_HOME");
		register(unimplementedList,ANDROID_I,"ANDROID_I");
		register(unimplementedList,ANDROID_INFO,"ANDROID_INFO");
		register(unimplementedList,ANDROID_INSERT,"ANDROID_INSERT");
		register(unimplementedList,ANDROID_J,"ANDROID_J");
		register(unimplementedList,ANDROID_K,"ANDROID_K");
		register(unimplementedList,ANDROID_KANA,"ANDROID_KANA");
		register(unimplementedList,ANDROID_KATAKANA_HIRAGANA,"ANDROID_KATAKANA_HIRAGANA");
		register(unimplementedList,ANDROID_L,"ANDROID_L");
		register(unimplementedList,ANDROID_LANGUAGE_SWITCH,"ANDROID_LANGUAGE_SWITCH");
		register(unimplementedList,ANDROID_LAST_CHANNEL,"ANDROID_LAST_CHANNEL");
		register(unimplementedList,ANDROID_LEFT_BRACKET,"ANDROID_LEFT_BRACKET");
		register(unimplementedList,ANDROID_M,"ANDROID_M");
		register(unimplementedList,ANDROID_MANNER_MODE,"ANDROID_MANNER_MODE");
		register(unimplementedList,ANDROID_MEDIA_AUDIO_TRACK,"ANDROID_MEDIA_AUDIO_TRACK");
		register(unimplementedList,ANDROID_MEDIA_CLOSE,"ANDROID_MEDIA_CLOSE");
		register(unimplementedList,ANDROID_MEDIA_EJECT,"ANDROID_MEDIA_EJECT");
		register(unimplementedList,ANDROID_MEDIA_FAST_FORWARD,"ANDROID_MEDIA_FAST_FORWARD");
		register(unimplementedList,ANDROID_MEDIA_NEXT,"ANDROID_MEDIA_NEXT");
		register(unimplementedList,ANDROID_MEDIA_PAUSE,"ANDROID_MEDIA_PAUSE");
		register(unimplementedList,ANDROID_MEDIA_PLAY,"ANDROID_MEDIA_PLAY");
		register(unimplementedList,ANDROID_MEDIA_PLAY_PAUSE,"ANDROID_MEDIA_PLAY_PAUSE");
		register(unimplementedList,ANDROID_MEDIA_PREVIOUS,"ANDROID_MEDIA_PREVIOUS");
		register(unimplementedList,ANDROID_MEDIA_RECORD,"ANDROID_MEDIA_RECORD");
		register(unimplementedList,ANDROID_MEDIA_REWIND,"ANDROID_MEDIA_REWIND");
		register(unimplementedList,ANDROID_MEDIA_SKIP_BACKWARD,"ANDROID_MEDIA_SKIP_BACKWARD");
		register(unimplementedList,ANDROID_MEDIA_SKIP_FORWARD,"ANDROID_MEDIA_SKIP_FORWARD");
		register(unimplementedList,ANDROID_MEDIA_STEP_BACKWARD,"ANDROID_MEDIA_STEP_BACKWARD");
		register(unimplementedList,ANDROID_MEDIA_STEP_FORWARD,"ANDROID_MEDIA_STEP_FORWARD");
		register(unimplementedList,ANDROID_MEDIA_STOP,"ANDROID_MEDIA_STOP");
		register(unimplementedList,ANDROID_MEDIA_TOP_MENU,"ANDROID_MEDIA_TOP_MENU");
		register(unimplementedList,ANDROID_MENU,"ANDROID_MENU");
		register(unimplementedList,ANDROID_META_LEFT,"ANDROID_META_LEFT");
		register(unimplementedList,ANDROID_META_RIGHT,"ANDROID_META_RIGHT");
		register(unimplementedList,ANDROID_MINUS,"ANDROID_MINUS");
		register(unimplementedList,ANDROID_MOVE_END,"ANDROID_MOVE_END");
		register(unimplementedList,ANDROID_MOVE_HOME,"ANDROID_MOVE_HOME");
		register(unimplementedList,ANDROID_MUHENKAN,"ANDROID_MUHENKAN");
		register(unimplementedList,ANDROID_MUSIC,"ANDROID_MUSIC");
		register(unimplementedList,ANDROID_MUTE,"ANDROID_MUTE");
		register(unimplementedList,ANDROID_N,"ANDROID_N");
		register(unimplementedList,ANDROID_NAVIGATE_IN,"ANDROID_NAVIGATE_IN");
		register(unimplementedList,ANDROID_NAVIGATE_NEXT,"ANDROID_NAVIGATE_NEXT");
		register(unimplementedList,ANDROID_NAVIGATE_OUT,"ANDROID_NAVIGATE_OUT");
		register(unimplementedList,ANDROID_NAVIGATE_PREVIOUS,"ANDROID_NAVIGATE_PREVIOUS");
		register(unimplementedList,ANDROID_NOTIFICATION,"ANDROID_NOTIFICATION");
		register(unimplementedList,ANDROID_NUM,"ANDROID_NUM");
		register(unimplementedList,ANDROID_NUMPAD_0,"ANDROID_NUMPAD_0");
		register(unimplementedList,ANDROID_NUMPAD_1,"ANDROID_NUMPAD_1");
		register(unimplementedList,ANDROID_NUMPAD_2,"ANDROID_NUMPAD_2");
		register(unimplementedList,ANDROID_NUMPAD_3,"ANDROID_NUMPAD_3");
		register(unimplementedList,ANDROID_NUMPAD_4,"ANDROID_NUMPAD_4");
		register(unimplementedList,ANDROID_NUMPAD_5,"ANDROID_NUMPAD_5");
		register(unimplementedList,ANDROID_NUMPAD_6,"ANDROID_NUMPAD_6");
		register(unimplementedList,ANDROID_NUMPAD_7,"ANDROID_NUMPAD_7");
		register(unimplementedList,ANDROID_NUMPAD_8,"ANDROID_NUMPAD_8");
		register(unimplementedList,ANDROID_NUMPAD_9,"ANDROID_NUMPAD_9");
		register(unimplementedList,ANDROID_NUMPAD_ADD,"ANDROID_NUMPAD_ADD");
		register(unimplementedList,ANDROID_NUMPAD_COMMA,"ANDROID_NUMPAD_COMMA");
		register(unimplementedList,ANDROID_NUMPAD_DIVIDE,"ANDROID_NUMPAD_DIVIDE");
		register(unimplementedList,ANDROID_NUMPAD_DOT,"ANDROID_NUMPAD_DOT");
		register(unimplementedList,ANDROID_NUMPAD_ENTER,"ANDROID_NUMPAD_ENTER");
		register(unimplementedList,ANDROID_NUMPAD_EQUALS,"ANDROID_NUMPAD_EQUALS");
		register(unimplementedList,ANDROID_NUMPAD_LEFT_PAREN,"ANDROID_NUMPAD_LEFT_PAREN");
		register(unimplementedList,ANDROID_NUMPAD_MULTIPLY,"ANDROID_NUMPAD_MULTIPLY");
		register(unimplementedList,ANDROID_NUMPAD_RIGHT_PAREN,"ANDROID_NUMPAD_RIGHT_PAREN");
		register(unimplementedList,ANDROID_NUMPAD_SUBTRACT,"ANDROID_NUMPAD_SUBTRACT");
		register(unimplementedList,ANDROID_NUM_LOCK,"ANDROID_NUM_LOCK");
		register(unimplementedList,ANDROID_O,"ANDROID_O");
		register(unimplementedList,ANDROID_P,"ANDROID_P");
		register(unimplementedList,ANDROID_PAGE_DOWN,"ANDROID_PAGE_DOWN");
		register(unimplementedList,ANDROID_PAGE_UP,"ANDROID_PAGE_UP");
		register(unimplementedList,ANDROID_PAIRING,"ANDROID_PAIRING");
		register(unimplementedList,ANDROID_PERIOD,"ANDROID_PERIOD");
		register(unimplementedList,ANDROID_PICTSYMBOLS,"ANDROID_PICTSYMBOLS");
		register(unimplementedList,ANDROID_PLUS,"ANDROID_PLUS");
		register(unimplementedList,ANDROID_POUND,"ANDROID_POUND");
		register(unimplementedList,ANDROID_POWER,"ANDROID_POWER");
		register(unimplementedList,ANDROID_PROG_BLUE,"ANDROID_PROG_BLUE");
		register(unimplementedList,ANDROID_PROG_GREEN,"ANDROID_PROG_GREEN");
		register(unimplementedList,ANDROID_PROG_RED,"ANDROID_PROG_RED");
		register(unimplementedList,ANDROID_PROG_YELLOW,"ANDROID_PROG_YELLOW");
		register(unimplementedList,ANDROID_Q,"ANDROID_Q");
		register(unimplementedList,ANDROID_R,"ANDROID_R");
		register(unimplementedList,ANDROID_RIGHT_BRACKET,"ANDROID_RIGHT_BRACKET");
		register(unimplementedList,ANDROID_RO,"ANDROID_RO");
		register(unimplementedList,ANDROID_S,"ANDROID_S");
		register(unimplementedList,ANDROID_SCROLL_LOCK,"ANDROID_SCROLL_LOCK");
		register(unimplementedList,ANDROID_SEARCH,"ANDROID_SEARCH");
		register(unimplementedList,ANDROID_SEMICOLON,"ANDROID_SEMICOLON");
		register(unimplementedList,ANDROID_SETTINGS,"ANDROID_SETTINGS");
		register(unimplementedList,ANDROID_SHIFT_LEFT,"ANDROID_SHIFT_LEFT");
		register(unimplementedList,ANDROID_SHIFT_RIGHT,"ANDROID_SHIFT_RIGHT");
		register(unimplementedList,ANDROID_SLASH,"ANDROID_SLASH");
		register(unimplementedList,ANDROID_SLEEP,"ANDROID_SLEEP");
		register(unimplementedList,ANDROID_SOFT_LEFT,"ANDROID_SOFT_LEFT");
		register(unimplementedList,ANDROID_SOFT_RIGHT,"ANDROID_SOFT_RIGHT");
		register(unimplementedList,ANDROID_SPACE,"ANDROID_SPACE");
		register(unimplementedList,ANDROID_STAR,"ANDROID_STAR");
		register(unimplementedList,ANDROID_STB_INPUT,"ANDROID_STB_INPUT");
		register(unimplementedList,ANDROID_STB_POWER,"ANDROID_STB_POWER");
		register(unimplementedList,ANDROID_SWITCH_CHARSET,"ANDROID_SWITCH_CHARSET");
		register(unimplementedList,ANDROID_SYM,"ANDROID_SYM");
		register(unimplementedList,ANDROID_SYSRQ,"ANDROID_SYSRQ");
		register(unimplementedList,ANDROID_T,"ANDROID_T");
		register(unimplementedList,ANDROID_TAB,"ANDROID_TAB");
		register(unimplementedList,ANDROID_TV,"ANDROID_TV");
		register(unimplementedList,ANDROID_TV_ANTENNA_CABLE,"ANDROID_TV_ANTENNA_CABLE");
		register(unimplementedList,ANDROID_TV_AUDIO_DESCRIPTION,"ANDROID_TV_AUDIO_DESCRIPTION");
		register(unimplementedList,ANDROID_TV_AUDIO_DESCRIPTION_MIX_DOWN,"ANDROID_TV_AUDIO_DESCRIPTION_MIX_DOWN");
		register(unimplementedList,ANDROID_TV_AUDIO_DESCRIPTION_MIX_UP,"ANDROID_TV_AUDIO_DESCRIPTION_MIX_UP");
		register(unimplementedList,ANDROID_TV_CONTENTS_MENU,"ANDROID_TV_CONTENTS_MENU");
		register(unimplementedList,ANDROID_TV_DATA_SERVICE,"ANDROID_TV_DATA_SERVICE");
		register(unimplementedList,ANDROID_TV_INPUT,"ANDROID_TV_INPUT");
		register(unimplementedList,ANDROID_TV_INPUT_COMPONENT_1,"ANDROID_TV_INPUT_COMPONENT_1");
		register(unimplementedList,ANDROID_TV_INPUT_COMPONENT_2,"ANDROID_TV_INPUT_COMPONENT_2");
		register(unimplementedList,ANDROID_TV_INPUT_COMPOSITE_1,"ANDROID_TV_INPUT_COMPOSITE_1");
		register(unimplementedList,ANDROID_TV_INPUT_COMPOSITE_2,"ANDROID_TV_INPUT_COMPOSITE_2");
		register(unimplementedList,ANDROID_TV_INPUT_HDMI_1,"ANDROID_TV_INPUT_HDMI_1");
		register(unimplementedList,ANDROID_TV_INPUT_HDMI_2,"ANDROID_TV_INPUT_HDMI_2");
		register(unimplementedList,ANDROID_TV_INPUT_HDMI_3,"ANDROID_TV_INPUT_HDMI_3");
		register(unimplementedList,ANDROID_TV_INPUT_HDMI_4,"ANDROID_TV_INPUT_HDMI_4");
		register(unimplementedList,ANDROID_TV_INPUT_VGA_1,"ANDROID_TV_INPUT_VGA_1");
		register(unimplementedList,ANDROID_TV_MEDIA_CONTEXT_MENU,"ANDROID_TV_MEDIA_CONTEXT_MENU");
		register(unimplementedList,ANDROID_TV_NETWORK,"ANDROID_TV_NETWORK");
		register(unimplementedList,ANDROID_TV_NUMBER_ENTRY,"ANDROID_TV_NUMBER_ENTRY");
		register(unimplementedList,ANDROID_TV_POWER,"ANDROID_TV_POWER");
		register(unimplementedList,ANDROID_TV_RADIO_SERVICE,"ANDROID_TV_RADIO_SERVICE");
		register(unimplementedList,ANDROID_TV_SATELLITE,"ANDROID_TV_SATELLITE");
		register(unimplementedList,ANDROID_TV_SATELLITE_BS,"ANDROID_TV_SATELLITE_BS");
		register(unimplementedList,ANDROID_TV_SATELLITE_CS,"ANDROID_TV_SATELLITE_CS");
		register(unimplementedList,ANDROID_TV_SATELLITE_SERVICE,"ANDROID_TV_SATELLITE_SERVICE");
		register(unimplementedList,ANDROID_TV_TELETEXT,"ANDROID_TV_TELETEXT");
		register(unimplementedList,ANDROID_TV_TERRESTRIAL_ANALOG,"ANDROID_TV_TERRESTRIAL_ANALOG");
		register(unimplementedList,ANDROID_TV_TERRESTRIAL_DIGITAL,"ANDROID_TV_TERRESTRIAL_DIGITAL");
		register(unimplementedList,ANDROID_TV_TIMER_PROGRAMMING,"ANDROID_TV_TIMER_PROGRAMMING");
		register(unimplementedList,ANDROID_TV_ZOOM_MODE,"ANDROID_TV_ZOOM_MODE");
		register(unimplementedList,ANDROID_U,"ANDROID_U");
		register(unimplementedList,ANDROID_UNKNOWN,"ANDROID_UNKNOWN");
		register(unimplementedList,ANDROID_V,"ANDROID_V");
		register(unimplementedList,ANDROID_VOICE_ASSIST,"ANDROID_VOICE_ASSIST");
		register(unimplementedList,ANDROID_VOLUME_DOWN,"ANDROID_VOLUME_DOWN");
		register(unimplementedList,ANDROID_VOLUME_MUTE,"ANDROID_VOLUME_MUTE");
		register(unimplementedList,ANDROID_VOLUME_UP,"ANDROID_VOLUME_UP");
		register(unimplementedList,ANDROID_W,"ANDROID_W");
		register(unimplementedList,ANDROID_WAKEUP,"ANDROID_WAKEUP");
		register(unimplementedList,ANDROID_WINDOW,"ANDROID_WINDOW");
		register(unimplementedList,ANDROID_X,"ANDROID_X");
		register(unimplementedList,ANDROID_Y,"ANDROID_Y");
		register(unimplementedList,ANDROID_YEN,"ANDROID_YEN");
		register(unimplementedList,ANDROID_Z,"ANDROID_Z");
		register(unimplementedList,ANDROID_ZENKAKU_HANKAKU,"ANDROID_ZENKAKU_HANKAKU");
		register(unimplementedList,ANDROID_ZOOM_IN,"ANDROID_ZOOM_IN");
		register(unimplementedList,ANDROID_ZOOM_OUT,"ANDROID_ZOOM_OUT");
	}

	private void registerAwtKeyCodesNoThrow(
		/*@NonNull*/ LinkedList<UnimplementedKeyCodeException> unimplementedList)
	{
		// =========================================================================
		// ----------------- java.awt.event.KeyEvent definitions -------------------
		// -------------------------------------------------------------------------
		/* AWT_0 thru AWT_9 are the same as ASCII '0' thru '9' (0x30 - 0x39) */
		register(unimplementedList,AWT_0,"AWT_0");
		register(unimplementedList,AWT_1,"AWT_1");
		register(unimplementedList,AWT_2,"AWT_2");
		register(unimplementedList,AWT_3,"AWT_3");
		register(unimplementedList,AWT_4,"AWT_4");
		register(unimplementedList,AWT_5,"AWT_5");
		register(unimplementedList,AWT_6,"AWT_6");
		register(unimplementedList,AWT_7,"AWT_7");
		register(unimplementedList,AWT_8,"AWT_8");
		register(unimplementedList,AWT_9,"AWT_9");
		/* AWT_A thru AWT_Z are the same as ASCII 'A' thru 'Z' (0x41 - 0x5A) */
		register(unimplementedList,AWT_A,"AWT_A");
		/* Constant for the Accept or Commit function key. */
		register(unimplementedList,AWT_ACCEPT,"AWT_ACCEPT");
		register(unimplementedList,AWT_ADD,"AWT_ADD");
		register(unimplementedList,AWT_AGAIN,"AWT_AGAIN");
		/* Constant for the All Candidates function key. */
		register(unimplementedList,AWT_ALL_CANDIDATES,"AWT_ALL_CANDIDATES");
		/* Constant for the Alphanumeric function key. */
		register(unimplementedList,AWT_ALPHANUMERIC,"AWT_ALPHANUMERIC");
		register(unimplementedList,AWT_ALT,"AWT_ALT");
		/* Constant for the AltGraph function key. */
		register(unimplementedList,AWT_ALT_GRAPH,"AWT_ALT_GRAPH");
		register(unimplementedList,AWT_AMPERSAND,"AWT_AMPERSAND");
		register(unimplementedList,AWT_ASTERISK,"AWT_ASTERISK");
		/* Constant for the "@" key. */
		register(unimplementedList,AWT_AT,"AWT_AT");
		register(unimplementedList,AWT_B,"AWT_B");
		register(unimplementedList,AWT_BACK_QUOTE,"AWT_BACK_QUOTE");
		register(unimplementedList,AWT_BACK_SLASH,"AWT_BACK_SLASH");
		register(unimplementedList,AWT_BACK_SPACE,"AWT_BACK_SPACE");
		/* Constant for the Begin key. */
		register(unimplementedList,AWT_BEGIN,"AWT_BEGIN");
		register(unimplementedList,AWT_BRACELEFT,"AWT_BRACELEFT");
		register(unimplementedList,AWT_BRACERIGHT,"AWT_BRACERIGHT");
		register(unimplementedList,AWT_C,"AWT_C");
		register(unimplementedList,AWT_CANCEL,"AWT_CANCEL");
		register(unimplementedList,AWT_CAPS_LOCK,"AWT_CAPS_LOCK");
		/* Constant for the "^" key. */
		register(unimplementedList,AWT_CIRCUMFLEX,"AWT_CIRCUMFLEX");
		register(unimplementedList,AWT_CLEAR,"AWT_CLEAR");
		register(unimplementedList,AWT_CLOSE_BRACKET,"AWT_CLOSE_BRACKET");
		/* Constant for the Code Input function key. */
		register(unimplementedList,AWT_CODE_INPUT,"AWT_CODE_INPUT");
		/* Constant for the ":" key. */
		register(unimplementedList,AWT_COLON,"AWT_COLON");
		register(unimplementedList,AWT_COMMA,"AWT_COMMA");
		/* Constant for the Compose function key. */
		register(unimplementedList,AWT_COMPOSE,"AWT_COMPOSE");
		/* Constant for the Microsoft Windows Context Menu key. */
		register(unimplementedList,AWT_CONTEXT_MENU,"AWT_CONTEXT_MENU");
		register(unimplementedList,AWT_CONTROL,"AWT_CONTROL");
		/* Constant for the Convert function key. */
		register(unimplementedList,AWT_CONVERT,"AWT_CONVERT");
		register(unimplementedList,AWT_COPY,"AWT_COPY");
		register(unimplementedList,AWT_CUT,"AWT_CUT");
		register(unimplementedList,AWT_D,"AWT_D");
		register(unimplementedList,AWT_DEAD_ABOVEDOT,"AWT_DEAD_ABOVEDOT");
		register(unimplementedList,AWT_DEAD_ABOVERING,"AWT_DEAD_ABOVERING");
		register(unimplementedList,AWT_DEAD_ACUTE,"AWT_DEAD_ACUTE");
		register(unimplementedList,AWT_DEAD_BREVE,"AWT_DEAD_BREVE");
		register(unimplementedList,AWT_DEAD_CARON,"AWT_DEAD_CARON");
		register(unimplementedList,AWT_DEAD_CEDILLA,"AWT_DEAD_CEDILLA");
		register(unimplementedList,AWT_DEAD_CIRCUMFLEX,"AWT_DEAD_CIRCUMFLEX");
		register(unimplementedList,AWT_DEAD_DIAERESIS,"AWT_DEAD_DIAERESIS");
		register(unimplementedList,AWT_DEAD_DOUBLEACUTE,"AWT_DEAD_DOUBLEACUTE");
		register(unimplementedList,AWT_DEAD_GRAVE,"AWT_DEAD_GRAVE");
		register(unimplementedList,AWT_DEAD_IOTA,"AWT_DEAD_IOTA");
		register(unimplementedList,AWT_DEAD_MACRON,"AWT_DEAD_MACRON");
		register(unimplementedList,AWT_DEAD_OGONEK,"AWT_DEAD_OGONEK");
		register(unimplementedList,AWT_DEAD_SEMIVOICED_SOUND,"AWT_DEAD_SEMIVOICED_SOUND");
		register(unimplementedList,AWT_DEAD_TILDE,"AWT_DEAD_TILDE");
		register(unimplementedList,AWT_DEAD_VOICED_SOUND,"AWT_DEAD_VOICED_SOUND");
		register(unimplementedList,AWT_DECIMAL,"AWT_DECIMAL");
		register(unimplementedList,AWT_DELETE,"AWT_DELETE");
		register(unimplementedList,AWT_DIVIDE,"AWT_DIVIDE");
		/* Constant for the "$" key. */
		register(unimplementedList,AWT_DOLLAR,"AWT_DOLLAR");
		/* Constant for the non-numpad down arrow key. */
		register(unimplementedList,AWT_DOWN,"AWT_DOWN");
		register(unimplementedList,AWT_E,"AWT_E");
		register(unimplementedList,AWT_END,"AWT_END");
		register(unimplementedList,AWT_ENTER,"AWT_ENTER");
		/* Constant for the equals key, "=" */
		register(unimplementedList,AWT_EQUALS,"AWT_EQUALS");
		register(unimplementedList,AWT_ESCAPE,"AWT_ESCAPE");
		/* Constant for the Euro currency sign key. */
		register(unimplementedList,AWT_EURO_SIGN,"AWT_EURO_SIGN");
		/* Constant for the "!" key. */
		register(unimplementedList,AWT_EXCLAMATION_MARK,"AWT_EXCLAMATION_MARK");
		register(unimplementedList,AWT_F,"AWT_F");
		register(unimplementedList,AWT_F1,"AWT_F1");
		register(unimplementedList,AWT_F10,"AWT_F10");
		register(unimplementedList,AWT_F11,"AWT_F11");
		register(unimplementedList,AWT_F12,"AWT_F12");
		register(unimplementedList,AWT_F13,"AWT_F13");
		register(unimplementedList,AWT_F14,"AWT_F14");
		register(unimplementedList,AWT_F15,"AWT_F15");
		register(unimplementedList,AWT_F16,"AWT_F16");
		register(unimplementedList,AWT_F17,"AWT_F17");
		register(unimplementedList,AWT_F18,"AWT_F18");
		register(unimplementedList,AWT_F19,"AWT_F19");
		register(unimplementedList,AWT_F2,"AWT_F2");
		register(unimplementedList,AWT_F20,"AWT_F20");
		register(unimplementedList,AWT_F21,"AWT_F21");
		register(unimplementedList,AWT_F22,"AWT_F22");
		register(unimplementedList,AWT_F23,"AWT_F23");
		register(unimplementedList,AWT_F24,"AWT_F24");
		register(unimplementedList,AWT_F3,"AWT_F3");
		register(unimplementedList,AWT_F4,"AWT_F4");
		register(unimplementedList,AWT_F5,"AWT_F5");
		register(unimplementedList,AWT_F6,"AWT_F6");
		register(unimplementedList,AWT_F7,"AWT_F7");
		register(unimplementedList,AWT_F8,"AWT_F8");
		register(unimplementedList,AWT_F9,"AWT_F9");
		register(unimplementedList,AWT_FINAL,"AWT_FINAL");
		register(unimplementedList,AWT_FIND,"AWT_FIND");
		register(unimplementedList,AWT_FULL_WIDTH,"AWT_FULL_WIDTH");
		/* Constant for the Full-Width Characters function key. */
		register(unimplementedList,AWT_G,"AWT_G");
		register(unimplementedList,AWT_GREATER,"AWT_GREATER");
		register(unimplementedList,AWT_H,"AWT_H");
		/* Constant for the Half-Width Characters function key. */
		register(unimplementedList,AWT_HALF_WIDTH,"AWT_HALF_WIDTH");
		register(unimplementedList,AWT_HELP,"AWT_HELP");
		/* Constant for the Hiragana function key. */
		register(unimplementedList,AWT_HIRAGANA,"AWT_HIRAGANA");
		register(unimplementedList,AWT_HOME,"AWT_HOME");
		register(unimplementedList,AWT_I,"AWT_I");
		/* Constant for the input method on/off key. */
		register(unimplementedList,AWT_INPUT_METHOD_ON_OFF,"AWT_INPUT_METHOD_ON_OFF");
		register(unimplementedList,AWT_INSERT,"AWT_INSERT");
		/* Constant for the inverted exclamation mark key. */
		register(unimplementedList,AWT_INVERTED_EXCLAMATION_MARK,"AWT_INVERTED_EXCLAMATION_MARK");
		register(unimplementedList,AWT_J,"AWT_J");
		/* Constant for the Japanese-Hiragana function key. */
		register(unimplementedList,AWT_JAPANESE_HIRAGANA,"AWT_JAPANESE_HIRAGANA");
		/* Constant for the Japanese-Katakana function key. */
		register(unimplementedList,AWT_JAPANESE_KATAKANA,"AWT_JAPANESE_KATAKANA");
		/* Constant for the Japanese-Roman function key. */
		register(unimplementedList,AWT_JAPANESE_ROMAN,"AWT_JAPANESE_ROMAN");
		register(unimplementedList,AWT_K,"AWT_K");
		register(unimplementedList,AWT_KANA,"AWT_KANA");
		/* Constant for the locking Kana function key. */
		register(unimplementedList,AWT_KANA_LOCK,"AWT_KANA_LOCK");
		register(unimplementedList,AWT_KANJI,"AWT_KANJI");
		/* Constant for the Katakana function key. */
		register(unimplementedList,AWT_KATAKANA,"AWT_KATAKANA");
		/* Constant for the numeric keypad down arrow key. */
		register(unimplementedList,AWT_KP_DOWN,"AWT_KP_DOWN");
		/* Constant for the numeric keypad left arrow key. */
		register(unimplementedList,AWT_KP_LEFT,"AWT_KP_LEFT");
		/* Constant for the numeric keypad right arrow key. */
		register(unimplementedList,AWT_KP_RIGHT,"AWT_KP_RIGHT");
		/* Constant for the numeric keypad up arrow key. */
		register(unimplementedList,AWT_KP_UP,"AWT_KP_UP");
		register(unimplementedList,AWT_L,"AWT_L");
		/* Constant for the non-numpad left arrow key. */
		register(unimplementedList,AWT_LEFT,"AWT_LEFT");
		/* Constant for the "(" key. */
		register(unimplementedList,AWT_LEFT_PARENTHESIS,"AWT_LEFT_PARENTHESIS");
		register(unimplementedList,AWT_LESS,"AWT_LESS");
		register(unimplementedList,AWT_M,"AWT_M");
		register(unimplementedList,AWT_META,"AWT_META");
		/* Constant for the minus key, "-" */
		register(unimplementedList,AWT_MINUS,"AWT_MINUS");
		register(unimplementedList,AWT_MODECHANGE,"AWT_MODECHANGE");
		register(unimplementedList,AWT_MULTIPLY,"AWT_MULTIPLY");
		register(unimplementedList,AWT_N,"AWT_N");
		/* Constant for the Don't Convert function key. */
		register(unimplementedList,AWT_NONCONVERT,"AWT_NONCONVERT");
		register(unimplementedList,AWT_NUM_LOCK,"AWT_NUM_LOCK");
		/* Constant for the "#" key. */
		register(unimplementedList,AWT_NUMBER_SIGN,"AWT_NUMBER_SIGN");
		register(unimplementedList,AWT_NUMPAD0,"AWT_NUMPAD0");
		register(unimplementedList,AWT_NUMPAD1,"AWT_NUMPAD1");
		register(unimplementedList,AWT_NUMPAD2,"AWT_NUMPAD2");
		register(unimplementedList,AWT_NUMPAD3,"AWT_NUMPAD3");
		register(unimplementedList,AWT_NUMPAD4,"AWT_NUMPAD4");
		register(unimplementedList,AWT_NUMPAD5,"AWT_NUMPAD5");
		register(unimplementedList,AWT_NUMPAD6,"AWT_NUMPAD6");
		register(unimplementedList,AWT_NUMPAD7,"AWT_NUMPAD7");
		register(unimplementedList,AWT_NUMPAD8,"AWT_NUMPAD8");
		register(unimplementedList,AWT_NUMPAD9,"AWT_NUMPAD9");
		register(unimplementedList,AWT_O,"AWT_O");
		/* Constant for the open bracket key, "[" */
		register(unimplementedList,AWT_OPEN_BRACKET,"AWT_OPEN_BRACKET");
		register(unimplementedList,AWT_P,"AWT_P");
		register(unimplementedList,AWT_PAGE_DOWN,"AWT_PAGE_DOWN");
		register(unimplementedList,AWT_PAGE_UP,"AWT_PAGE_UP");
		register(unimplementedList,AWT_PASTE,"AWT_PASTE");
		register(unimplementedList,AWT_PAUSE,"AWT_PAUSE");
		/* Constant for the period key, "." */
		register(unimplementedList,AWT_PERIOD,"AWT_PERIOD");
		/* Constant for the "+" key. */
		register(unimplementedList,AWT_PLUS,"AWT_PLUS");
		/* Constant for the Previous Candidate function key. */
		register(unimplementedList,AWT_PREVIOUS_CANDIDATE,"AWT_PREVIOUS_CANDIDATE");
		register(unimplementedList,AWT_PRINTSCREEN,"AWT_PRINTSCREEN");
		register(unimplementedList,AWT_PROPS,"AWT_PROPS");
		register(unimplementedList,AWT_Q,"AWT_Q");
		register(unimplementedList,AWT_QUOTE,"AWT_QUOTE");
		register(unimplementedList,AWT_QUOTEDBL,"AWT_QUOTEDBL");
		register(unimplementedList,AWT_R,"AWT_R");
		/* Constant for the non-numpad right arrow key. */
		register(unimplementedList,AWT_RIGHT,"AWT_RIGHT");
		/* Constant for the ")" key. */
		register(unimplementedList,AWT_RIGHT_PARENTHESIS,"AWT_RIGHT_PARENTHESIS");
		/* Constant for the Roman Characters function key. */
		register(unimplementedList,AWT_ROMAN_CHARACTERS,"AWT_ROMAN_CHARACTERS");
		register(unimplementedList,AWT_S,"AWT_S");
		register(unimplementedList,AWT_SCROLL_LOCK,"AWT_SCROLL_LOCK");
		/* Constant for the semicolon key, ";" */
		register(unimplementedList,AWT_SEMICOLON,"AWT_SEMICOLON");
		/* Constant for the Numpad Separator key. */
		register(unimplementedList,AWT_SEPARATOR,"AWT_SEPARATOR");
		register(unimplementedList,AWT_SHIFT,"AWT_SHIFT");
		/* Constant for the forward slash key, "/" */
		register(unimplementedList,AWT_SLASH,"AWT_SLASH");
		register(unimplementedList,AWT_SPACE,"AWT_SPACE");
		register(unimplementedList,AWT_STOP,"AWT_STOP");
		register(unimplementedList,AWT_SUBTRACT,"AWT_SUBTRACT");
		register(unimplementedList,AWT_T,"AWT_T");
		register(unimplementedList,AWT_TAB,"AWT_TAB");
		register(unimplementedList,AWT_U,"AWT_U");
		/* This value is used to indicate that the keyCode is unknown. */
		register(unimplementedList,AWT_UNDEFINED,"AWT_UNDEFINED");
		/* Constant for the "_" key. */
		register(unimplementedList,AWT_UNDERSCORE,"AWT_UNDERSCORE");
		register(unimplementedList,AWT_UNDO,"AWT_UNDO");
		/* Constant for the non-numpad up arrow key. */
		register(unimplementedList,AWT_UP,"AWT_UP");
		register(unimplementedList,AWT_V,"AWT_V");
		register(unimplementedList,AWT_W,"AWT_W");

		/* Constant for the Microsoft Windows "Windows" key. */
		register(unimplementedList,AWT_WINDOWS,"AWT_WINDOWS");
		register(unimplementedList,AWT_X,"AWT_X");
		register(unimplementedList,AWT_Y,"AWT_Y");
		register(unimplementedList,AWT_Z,"AWT_Z");
	}

	// =========================================================================
	// -------------------- Custom Mouse Button Key Codes ----------------------
	// -------------------------------------------------------------------------
	// AWT does not define keycodes for mouse button presses, so we
	// define our own.  Other systems are welcome to use these as well.
	/**
	 *  This array represents mouse button key codes.
	 *  <p>
	 *  Note that the 0th element represents the "NOBUTTON" status found in
	 *  java.awt's MouseEvent class.  The 0th mouse button should never be
	 *  found in the wild, but it is at least theoretically possible to handle
	 *  it by looking for MOUSE_BUTTONS[0].
	 */
	public final int[] MOUSE_BUTTONS = {
		UNIMPLEMENTED_KEYCODE, // NOBUTTON
		UNIMPLEMENTED_KEYCODE, UNIMPLEMENTED_KEYCODE, // BUTTON1, BUTTON2
		UNIMPLEMENTED_KEYCODE, UNIMPLEMENTED_KEYCODE, // BUTTON3, 4
		UNIMPLEMENTED_KEYCODE, UNIMPLEMENTED_KEYCODE, // 5, 6
		UNIMPLEMENTED_KEYCODE, UNIMPLEMENTED_KEYCODE  // 7, 8
	};

	// =========================================================================
	// ----------------- java.awt.event.KeyEvent definitions -------------------
	// -------------------------------------------------------------------------
	/** AWT_0 thru AWT_9 are the same as ASCII '0' thru '9' (0x30 - 0x39) */
	public int AWT_0 = UNIMPLEMENTED_KEYCODE;
	public int AWT_1 = UNIMPLEMENTED_KEYCODE;
	public int AWT_2 = UNIMPLEMENTED_KEYCODE;
	public int AWT_3 = UNIMPLEMENTED_KEYCODE;
	public int AWT_4 = UNIMPLEMENTED_KEYCODE;
	public int AWT_5 = UNIMPLEMENTED_KEYCODE;
	public int AWT_6 = UNIMPLEMENTED_KEYCODE;
	public int AWT_7 = UNIMPLEMENTED_KEYCODE;
	public int AWT_8 = UNIMPLEMENTED_KEYCODE;
	public int AWT_9 = UNIMPLEMENTED_KEYCODE;
	/** AWT_A thru AWT_Z are the same as ASCII 'A' thru 'Z' (0x41 - 0x5A) */
	public int AWT_A = UNIMPLEMENTED_KEYCODE;
	/** Constant for the Accept or Commit function key. */
	public int AWT_ACCEPT = UNIMPLEMENTED_KEYCODE;
	public int AWT_ADD = UNIMPLEMENTED_KEYCODE;
	public int AWT_AGAIN = UNIMPLEMENTED_KEYCODE;
	/** Constant for the All Candidates function key. */
	public int AWT_ALL_CANDIDATES = UNIMPLEMENTED_KEYCODE;
	/** Constant for the Alphanumeric function key. */
	public int AWT_ALPHANUMERIC = UNIMPLEMENTED_KEYCODE;
	public int AWT_ALT = UNIMPLEMENTED_KEYCODE;
	/** Constant for the AltGraph function key. */
	public int AWT_ALT_GRAPH = UNIMPLEMENTED_KEYCODE;
	public int AWT_AMPERSAND = UNIMPLEMENTED_KEYCODE;
	public int AWT_ASTERISK = UNIMPLEMENTED_KEYCODE;
	/** Constant for the "@" key. */
	public int AWT_AT = UNIMPLEMENTED_KEYCODE;
	public int AWT_B = UNIMPLEMENTED_KEYCODE;
	public int AWT_BACK_QUOTE = UNIMPLEMENTED_KEYCODE;
	/** Constant for the back slash key, "\" */
	public int AWT_BACK_SLASH = UNIMPLEMENTED_KEYCODE;
	public int AWT_BACK_SPACE = UNIMPLEMENTED_KEYCODE;
	/** Constant for the Begin key. */
	public int AWT_BEGIN = UNIMPLEMENTED_KEYCODE;
	public int AWT_BRACELEFT = UNIMPLEMENTED_KEYCODE;
	public int AWT_BRACERIGHT = UNIMPLEMENTED_KEYCODE;
	public int AWT_C = UNIMPLEMENTED_KEYCODE;
	public int AWT_CANCEL = UNIMPLEMENTED_KEYCODE;
	public int AWT_CAPS_LOCK = UNIMPLEMENTED_KEYCODE;
	/** Constant for the "^" key. */
	public int AWT_CIRCUMFLEX = UNIMPLEMENTED_KEYCODE;
	public int AWT_CLEAR = UNIMPLEMENTED_KEYCODE;
	/** Constant for the close bracket key, "]" */
	public int AWT_CLOSE_BRACKET = UNIMPLEMENTED_KEYCODE;
	/** Constant for the Code Input function key. */
	public int AWT_CODE_INPUT = UNIMPLEMENTED_KEYCODE;
	/** Constant for the ":" key. */
	public int AWT_COLON = UNIMPLEMENTED_KEYCODE;
	/** Constant for the comma key, "," */
	public int AWT_COMMA = UNIMPLEMENTED_KEYCODE;
	/** Constant for the Compose function key. */
	public int AWT_COMPOSE = UNIMPLEMENTED_KEYCODE;
	/** Constant for the Microsoft Windows Context Menu key. */
	public int AWT_CONTEXT_MENU = UNIMPLEMENTED_KEYCODE;
	public int AWT_CONTROL = UNIMPLEMENTED_KEYCODE;
	/** Constant for the Convert function key. */
	public int AWT_CONVERT = UNIMPLEMENTED_KEYCODE;
	public int AWT_COPY = UNIMPLEMENTED_KEYCODE;
	public int AWT_CUT = UNIMPLEMENTED_KEYCODE;
	public int AWT_D = UNIMPLEMENTED_KEYCODE;
	public int AWT_DEAD_ABOVEDOT = UNIMPLEMENTED_KEYCODE;
	public int AWT_DEAD_ABOVERING = UNIMPLEMENTED_KEYCODE;
	public int AWT_DEAD_ACUTE = UNIMPLEMENTED_KEYCODE;
	public int AWT_DEAD_BREVE = UNIMPLEMENTED_KEYCODE;
	public int AWT_DEAD_CARON = UNIMPLEMENTED_KEYCODE;
	public int AWT_DEAD_CEDILLA = UNIMPLEMENTED_KEYCODE;
	public int AWT_DEAD_CIRCUMFLEX = UNIMPLEMENTED_KEYCODE;
	public int AWT_DEAD_DIAERESIS = UNIMPLEMENTED_KEYCODE;
	public int AWT_DEAD_DOUBLEACUTE = UNIMPLEMENTED_KEYCODE;
	public int AWT_DEAD_GRAVE = UNIMPLEMENTED_KEYCODE;
	public int AWT_DEAD_IOTA = UNIMPLEMENTED_KEYCODE;
	public int AWT_DEAD_MACRON = UNIMPLEMENTED_KEYCODE;
	public int AWT_DEAD_OGONEK = UNIMPLEMENTED_KEYCODE;
	public int AWT_DEAD_SEMIVOICED_SOUND = UNIMPLEMENTED_KEYCODE;
	public int AWT_DEAD_TILDE = UNIMPLEMENTED_KEYCODE;
	public int AWT_DEAD_VOICED_SOUND = UNIMPLEMENTED_KEYCODE;
	public int AWT_DECIMAL = UNIMPLEMENTED_KEYCODE;
	public int AWT_DELETE = UNIMPLEMENTED_KEYCODE;
	public int AWT_DIVIDE = UNIMPLEMENTED_KEYCODE;
	/** Constant for the "$" key. */
	public int AWT_DOLLAR = UNIMPLEMENTED_KEYCODE;
	/** Constant for the non-numpad down arrow key. */
	public int AWT_DOWN = UNIMPLEMENTED_KEYCODE;
	public int AWT_E = UNIMPLEMENTED_KEYCODE;
	public int AWT_END = UNIMPLEMENTED_KEYCODE;
	public int AWT_ENTER = UNIMPLEMENTED_KEYCODE;
	/** Constant for the equals key, "=" */
	public int AWT_EQUALS = UNIMPLEMENTED_KEYCODE;
	public int AWT_ESCAPE = UNIMPLEMENTED_KEYCODE;
	/** Constant for the Euro currency sign key. */
	public int AWT_EURO_SIGN = UNIMPLEMENTED_KEYCODE;
	/** Constant for the "!" key. */
	public int AWT_EXCLAMATION_MARK = UNIMPLEMENTED_KEYCODE;
	public int AWT_F = UNIMPLEMENTED_KEYCODE;
	public int AWT_F1 = UNIMPLEMENTED_KEYCODE;
	public int AWT_F10 = UNIMPLEMENTED_KEYCODE;
	public int AWT_F11 = UNIMPLEMENTED_KEYCODE;
	public int AWT_F12 = UNIMPLEMENTED_KEYCODE;
	public int AWT_F13 = UNIMPLEMENTED_KEYCODE;
	public int AWT_F14 = UNIMPLEMENTED_KEYCODE;
	public int AWT_F15 = UNIMPLEMENTED_KEYCODE;
	public int AWT_F16 = UNIMPLEMENTED_KEYCODE;
	public int AWT_F17 = UNIMPLEMENTED_KEYCODE;
	public int AWT_F18 = UNIMPLEMENTED_KEYCODE;
	public int AWT_F19 = UNIMPLEMENTED_KEYCODE;
	public int AWT_F2 = UNIMPLEMENTED_KEYCODE;
	public int AWT_F20 = UNIMPLEMENTED_KEYCODE;
	public int AWT_F21 = UNIMPLEMENTED_KEYCODE;
	public int AWT_F22 = UNIMPLEMENTED_KEYCODE;
	public int AWT_F23 = UNIMPLEMENTED_KEYCODE;
	public int AWT_F24 = UNIMPLEMENTED_KEYCODE;
	public int AWT_F3 = UNIMPLEMENTED_KEYCODE;
	public int AWT_F4 = UNIMPLEMENTED_KEYCODE;
	public int AWT_F5 = UNIMPLEMENTED_KEYCODE;
	public int AWT_F6 = UNIMPLEMENTED_KEYCODE;
	public int AWT_F7 = UNIMPLEMENTED_KEYCODE;
	public int AWT_F8 = UNIMPLEMENTED_KEYCODE;
	public int AWT_F9 = UNIMPLEMENTED_KEYCODE;
	public int AWT_FINAL = UNIMPLEMENTED_KEYCODE;
	public int AWT_FIND = UNIMPLEMENTED_KEYCODE;
	public int AWT_FULL_WIDTH = UNIMPLEMENTED_KEYCODE;
	/** Constant for the Full-Width Characters function key. */
	public int AWT_G = UNIMPLEMENTED_KEYCODE;
	public int AWT_GREATER = UNIMPLEMENTED_KEYCODE;
	public int AWT_H = UNIMPLEMENTED_KEYCODE;
	/** Constant for the Half-Width Characters function key. */
	public int AWT_HALF_WIDTH = UNIMPLEMENTED_KEYCODE;
	public int AWT_HELP = UNIMPLEMENTED_KEYCODE;
	/** Constant for the Hiragana function key. */
	public int AWT_HIRAGANA = UNIMPLEMENTED_KEYCODE;
	public int AWT_HOME = UNIMPLEMENTED_KEYCODE;
	public int AWT_I = UNIMPLEMENTED_KEYCODE;
	/** Constant for the input method on/off key. */
	public int AWT_INPUT_METHOD_ON_OFF = UNIMPLEMENTED_KEYCODE;
	public int AWT_INSERT = UNIMPLEMENTED_KEYCODE;
	/** Constant for the inverted exclamation mark key. */
	public int AWT_INVERTED_EXCLAMATION_MARK = UNIMPLEMENTED_KEYCODE;
	public int AWT_J = UNIMPLEMENTED_KEYCODE;
	/** Constant for the Japanese-Hiragana function key. */
	public int AWT_JAPANESE_HIRAGANA = UNIMPLEMENTED_KEYCODE;
	/** Constant for the Japanese-Katakana function key. */
	public int AWT_JAPANESE_KATAKANA = UNIMPLEMENTED_KEYCODE;
	/** Constant for the Japanese-Roman function key. */
	public int AWT_JAPANESE_ROMAN = UNIMPLEMENTED_KEYCODE;
	public int AWT_K = UNIMPLEMENTED_KEYCODE;
	public int AWT_KANA = UNIMPLEMENTED_KEYCODE;
	/** Constant for the locking Kana function key. */
	public int AWT_KANA_LOCK = UNIMPLEMENTED_KEYCODE;
	public int AWT_KANJI = UNIMPLEMENTED_KEYCODE;
	/** Constant for the Katakana function key. */
	public int AWT_KATAKANA = UNIMPLEMENTED_KEYCODE;
	/** Constant for the numeric keypad down arrow key. */
	public int AWT_KP_DOWN = UNIMPLEMENTED_KEYCODE;
	/** Constant for the numeric keypad left arrow key. */
	public int AWT_KP_LEFT = UNIMPLEMENTED_KEYCODE;
	/** Constant for the numeric keypad right arrow key. */
	public int AWT_KP_RIGHT = UNIMPLEMENTED_KEYCODE;
	/** Constant for the numeric keypad up arrow key. */
	public int AWT_KP_UP = UNIMPLEMENTED_KEYCODE;
	public int AWT_L = UNIMPLEMENTED_KEYCODE;
	/** Constant for the non-numpad left arrow key. */
	public int AWT_LEFT = UNIMPLEMENTED_KEYCODE;
	/** Constant for the "(" key. */
	public int AWT_LEFT_PARENTHESIS = UNIMPLEMENTED_KEYCODE;
	public int AWT_LESS = UNIMPLEMENTED_KEYCODE;
	public int AWT_M = UNIMPLEMENTED_KEYCODE;
	public int AWT_META = UNIMPLEMENTED_KEYCODE;
	/** Constant for the minus key, "-" */
	public int AWT_MINUS = UNIMPLEMENTED_KEYCODE;
	public int AWT_MODECHANGE = UNIMPLEMENTED_KEYCODE;
	public int AWT_MULTIPLY = UNIMPLEMENTED_KEYCODE;
	public int AWT_N = UNIMPLEMENTED_KEYCODE;
	/** Constant for the Don't Convert function key. */
	public int AWT_NONCONVERT = UNIMPLEMENTED_KEYCODE;
	public int AWT_NUM_LOCK = UNIMPLEMENTED_KEYCODE;
	/** Constant for the "#" key. */
	public int AWT_NUMBER_SIGN = UNIMPLEMENTED_KEYCODE;
	public int AWT_NUMPAD0 = UNIMPLEMENTED_KEYCODE;
	public int AWT_NUMPAD1 = UNIMPLEMENTED_KEYCODE;
	public int AWT_NUMPAD2 = UNIMPLEMENTED_KEYCODE;
	public int AWT_NUMPAD3 = UNIMPLEMENTED_KEYCODE;
	public int AWT_NUMPAD4 = UNIMPLEMENTED_KEYCODE;
	public int AWT_NUMPAD5 = UNIMPLEMENTED_KEYCODE;
	public int AWT_NUMPAD6 = UNIMPLEMENTED_KEYCODE;
	public int AWT_NUMPAD7 = UNIMPLEMENTED_KEYCODE;
	public int AWT_NUMPAD8 = UNIMPLEMENTED_KEYCODE;
	public int AWT_NUMPAD9 = UNIMPLEMENTED_KEYCODE;
	public int AWT_O = UNIMPLEMENTED_KEYCODE;
	/** Constant for the open bracket key, "[" */
	public int AWT_OPEN_BRACKET = UNIMPLEMENTED_KEYCODE;
	public int AWT_P = UNIMPLEMENTED_KEYCODE;
	public int AWT_PAGE_DOWN = UNIMPLEMENTED_KEYCODE;
	public int AWT_PAGE_UP = UNIMPLEMENTED_KEYCODE;
	public int AWT_PASTE = UNIMPLEMENTED_KEYCODE;
	public int AWT_PAUSE = UNIMPLEMENTED_KEYCODE;
	/** Constant for the period key, "." */
	public int AWT_PERIOD = UNIMPLEMENTED_KEYCODE;
	/** Constant for the "+" key. */
	public int AWT_PLUS = UNIMPLEMENTED_KEYCODE;
	/** Constant for the Previous Candidate function key. */
	public int AWT_PREVIOUS_CANDIDATE = UNIMPLEMENTED_KEYCODE;
	public int AWT_PRINTSCREEN = UNIMPLEMENTED_KEYCODE;
	public int AWT_PROPS = UNIMPLEMENTED_KEYCODE;
	public int AWT_Q = UNIMPLEMENTED_KEYCODE;
	public int AWT_QUOTE = UNIMPLEMENTED_KEYCODE;
	public int AWT_QUOTEDBL = UNIMPLEMENTED_KEYCODE;
	public int AWT_R = UNIMPLEMENTED_KEYCODE;
	/** Constant for the non-numpad right arrow key. */
	public int AWT_RIGHT = UNIMPLEMENTED_KEYCODE;
	/** Constant for the ")" key. */
	public int AWT_RIGHT_PARENTHESIS = UNIMPLEMENTED_KEYCODE;
	/** Constant for the Roman Characters function key. */
	public int AWT_ROMAN_CHARACTERS = UNIMPLEMENTED_KEYCODE;
	public int AWT_S = UNIMPLEMENTED_KEYCODE;
	public int AWT_SCROLL_LOCK = UNIMPLEMENTED_KEYCODE;
	/** Constant for the semicolon key, ";" */
	public int AWT_SEMICOLON = UNIMPLEMENTED_KEYCODE;
	/** Constant for the Numpad Separator key. */
	public int AWT_SEPARATOR = UNIMPLEMENTED_KEYCODE;
	public int AWT_SHIFT = UNIMPLEMENTED_KEYCODE;
	/** Constant for the forward slash key, "/" */
	public int AWT_SLASH = UNIMPLEMENTED_KEYCODE;
	public int AWT_SPACE = UNIMPLEMENTED_KEYCODE;
	public int AWT_STOP = UNIMPLEMENTED_KEYCODE;
	public int AWT_SUBTRACT = UNIMPLEMENTED_KEYCODE;
	public int AWT_T = UNIMPLEMENTED_KEYCODE;
	public int AWT_TAB = UNIMPLEMENTED_KEYCODE;
	public int AWT_U = UNIMPLEMENTED_KEYCODE;
	/** This value is used to indicate that the keyCode is unknown. */
	public int AWT_UNDEFINED = UNIMPLEMENTED_KEYCODE;
	/** Constant for the "_" key. */
	public int AWT_UNDERSCORE = UNIMPLEMENTED_KEYCODE;
	public int AWT_UNDO = UNIMPLEMENTED_KEYCODE;
	/** Constant for the non-numpad up arrow key. */
	public int AWT_UP = UNIMPLEMENTED_KEYCODE;
	public int AWT_V = UNIMPLEMENTED_KEYCODE;
	public int AWT_W = UNIMPLEMENTED_KEYCODE;

	/** Constant for the Microsoft Windows "Windows" key. */
	public int AWT_WINDOWS = UNIMPLEMENTED_KEYCODE;
	public int AWT_X = UNIMPLEMENTED_KEYCODE;
	public int AWT_Y = UNIMPLEMENTED_KEYCODE;
	public int AWT_Z = UNIMPLEMENTED_KEYCODE;

	// =========================================================================
	// ------------------ android.view.KeyEvent definitions --------------------
	// -------------------------------------------------------------------------
	public int ANDROID_0 = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_1 = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_11 = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_12 = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_2 = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_3 = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_3D_MODE = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_4 = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_5 = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_6 = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_7 = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_8 = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_9 = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_A = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_ALT_LEFT = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_ALT_RIGHT = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_APOSTROPHE = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_APP_SWITCH = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_ASSIST = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_AT = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_AVR_INPUT = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_AVR_POWER = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_B = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_BACK = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_BACKSLASH = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_BOOKMARK = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_BREAK = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_BRIGHTNESS_DOWN = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_BRIGHTNESS_UP = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_BUTTON_1 = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_BUTTON_10 = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_BUTTON_11 = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_BUTTON_12 = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_BUTTON_13 = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_BUTTON_14 = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_BUTTON_15 = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_BUTTON_16 = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_BUTTON_2 = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_BUTTON_3 = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_BUTTON_4 = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_BUTTON_5 = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_BUTTON_6 = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_BUTTON_7 = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_BUTTON_8 = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_BUTTON_9 = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_BUTTON_A = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_BUTTON_B = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_BUTTON_C = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_BUTTON_L1 = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_BUTTON_L2 = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_BUTTON_MODE = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_BUTTON_R1 = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_BUTTON_R2 = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_BUTTON_SELECT = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_BUTTON_START = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_BUTTON_THUMBL = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_BUTTON_THUMBR = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_BUTTON_X = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_BUTTON_Y = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_BUTTON_Z = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_C = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_CALCULATOR = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_CALENDAR = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_CALL = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_CAMERA = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_CAPS_LOCK = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_CAPTIONS = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_CHANNEL_DOWN = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_CHANNEL_UP = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_CLEAR = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_COMMA = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_CONTACTS = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_CTRL_LEFT = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_CTRL_RIGHT = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_D = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_DEL = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_DPAD_CENTER = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_DPAD_DOWN = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_DPAD_LEFT = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_DPAD_RIGHT = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_DPAD_UP = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_DVR = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_E = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_EISU = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_ENDCALL = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_ENTER = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_ENVELOPE = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_EQUALS = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_ESCAPE = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_EXPLORER = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_F = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_F1 = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_F10 = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_F11 = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_F12 = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_F2 = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_F3 = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_F4 = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_F5 = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_F6 = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_F7 = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_F8 = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_F9 = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_FOCUS = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_FORWARD = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_FORWARD_DEL = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_FUNCTION = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_G = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_GRAVE = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_GUIDE = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_H = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_HEADSETHOOK = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_HELP = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_HENKAN = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_HOME = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_I = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_INFO = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_INSERT = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_J = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_K = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_KANA = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_KATAKANA_HIRAGANA = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_L = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_LANGUAGE_SWITCH = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_LAST_CHANNEL = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_LEFT_BRACKET = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_M = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_MANNER_MODE = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_MEDIA_AUDIO_TRACK = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_MEDIA_CLOSE = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_MEDIA_EJECT = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_MEDIA_FAST_FORWARD = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_MEDIA_NEXT = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_MEDIA_PAUSE = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_MEDIA_PLAY = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_MEDIA_PLAY_PAUSE = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_MEDIA_PREVIOUS = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_MEDIA_RECORD = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_MEDIA_REWIND = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_MEDIA_SKIP_BACKWARD = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_MEDIA_SKIP_FORWARD = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_MEDIA_STEP_BACKWARD = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_MEDIA_STEP_FORWARD = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_MEDIA_STOP = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_MEDIA_TOP_MENU = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_MENU = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_META_LEFT = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_META_RIGHT = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_MINUS = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_MOVE_END = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_MOVE_HOME = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_MUHENKAN = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_MUSIC = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_MUTE = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_N = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_NAVIGATE_IN = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_NAVIGATE_NEXT = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_NAVIGATE_OUT = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_NAVIGATE_PREVIOUS = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_NOTIFICATION = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_NUM = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_NUMPAD_0 = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_NUMPAD_1 = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_NUMPAD_2 = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_NUMPAD_3 = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_NUMPAD_4 = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_NUMPAD_5 = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_NUMPAD_6 = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_NUMPAD_7 = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_NUMPAD_8 = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_NUMPAD_9 = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_NUMPAD_ADD = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_NUMPAD_COMMA = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_NUMPAD_DIVIDE = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_NUMPAD_DOT = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_NUMPAD_ENTER = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_NUMPAD_EQUALS = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_NUMPAD_LEFT_PAREN = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_NUMPAD_MULTIPLY = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_NUMPAD_RIGHT_PAREN = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_NUMPAD_SUBTRACT = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_NUM_LOCK = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_O = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_P = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_PAGE_DOWN = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_PAGE_UP = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_PAIRING = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_PERIOD = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_PICTSYMBOLS = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_PLUS = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_POUND = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_POWER = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_PROG_BLUE = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_PROG_GREEN = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_PROG_RED = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_PROG_YELLOW = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_Q = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_R = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_RIGHT_BRACKET = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_RO = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_S = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_SCROLL_LOCK = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_SEARCH = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_SEMICOLON = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_SETTINGS = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_SHIFT_LEFT = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_SHIFT_RIGHT = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_SLASH = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_SLEEP = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_SOFT_LEFT = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_SOFT_RIGHT = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_SPACE = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_STAR = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_STB_INPUT = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_STB_POWER = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_SWITCH_CHARSET = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_SYM = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_SYSRQ = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_T = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_TAB = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_TV = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_TV_ANTENNA_CABLE = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_TV_AUDIO_DESCRIPTION = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_TV_AUDIO_DESCRIPTION_MIX_DOWN = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_TV_AUDIO_DESCRIPTION_MIX_UP = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_TV_CONTENTS_MENU = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_TV_DATA_SERVICE = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_TV_INPUT = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_TV_INPUT_COMPONENT_1 = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_TV_INPUT_COMPONENT_2 = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_TV_INPUT_COMPOSITE_1 = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_TV_INPUT_COMPOSITE_2 = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_TV_INPUT_HDMI_1 = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_TV_INPUT_HDMI_2 = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_TV_INPUT_HDMI_3 = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_TV_INPUT_HDMI_4 = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_TV_INPUT_VGA_1 = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_TV_MEDIA_CONTEXT_MENU = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_TV_NETWORK = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_TV_NUMBER_ENTRY = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_TV_POWER = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_TV_RADIO_SERVICE = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_TV_SATELLITE = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_TV_SATELLITE_BS = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_TV_SATELLITE_CS = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_TV_SATELLITE_SERVICE = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_TV_TELETEXT = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_TV_TERRESTRIAL_ANALOG = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_TV_TERRESTRIAL_DIGITAL = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_TV_TIMER_PROGRAMMING = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_TV_ZOOM_MODE = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_U = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_UNKNOWN = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_V = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_VOICE_ASSIST = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_VOLUME_DOWN = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_VOLUME_MUTE = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_VOLUME_UP = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_W = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_WAKEUP = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_WINDOW = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_X = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_Y = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_YEN = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_Z = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_ZENKAKU_HANKAKU = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_ZOOM_IN = UNIMPLEMENTED_KEYCODE;
	public int ANDROID_ZOOM_OUT = UNIMPLEMENTED_KEYCODE;
}