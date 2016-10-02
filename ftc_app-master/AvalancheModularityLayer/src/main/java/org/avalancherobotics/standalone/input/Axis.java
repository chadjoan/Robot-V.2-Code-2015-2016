package org.avalancherobotics.standalone.input;

// Implementation NOTE: The standalone package shall never depend on the
//   org.avalancherobotics.library package, the org.avalancherobotics.desktop package,
//   or any other implementors.  It should also use only Java code that will
//   be found on all platforms (ex: Android + Desktop), which means that only
//   things in java.util are remotely safe.
//

/**
 *  Constants that map analog control/event array indices onto axes.
 *
 *  @see InputAnalogEvent
 */
public class Axis
{
	/** */ public static final int X = 0;
	/** */ public static final int Y = 1;
	/** */ public static final int Z = 2;
}
