package org.avalancherobotics.standalone.internal;

// Implementation NOTE: The standalone package shall never depend on the
//   org.avalancherobotics.library package, the org.avalancherobotics.desktop package,
//   or any other implementors.  It should also use only Java code that will
//   be found on all platforms (ex: Android + Desktop), which means that only
//   things in java.util are remotely safe.
//

import org.avalancherobotics.standalone.internal.UnimplementedException;

/** This is thrown when a switch-case statement (or similar construct)
 *  encounteres a constant that it was not designed to handle.
 *  This can happen if the called method was not updated to handle newer
 *  features implemented by related classes or methods.
 *  <p>
 *  This exception is intended for handling both enumerations and integer
 *  constants, as well as any other model of a set of possibilities.
 *  <p>
 *  This arises from the good practice of guarding branching statements
 *  against unexpected or future changes, like so:
 *  <code>
 *  switch(displayTechnology)
 *  {
 *      case FILM_PROJECTOR:       ... break;
 *      case CATHODE_RAY_TUBE:     ... break;
 *      case PLASMA:               ... break;
 *      case LIQUID_CRYSTAL:       ... break;
 *      case ELECTRONIC_INK:       ... break;
 *      case LIGHT_EMITTING_DIODE: ... break;
 *      ...
 *      default:
 *          // Some future technology that we don't know about yet.
 *          throw new UnimplementedPossibilityException(
 *              "The display technology '"+ displayTechnology +"' is not implemented here.");
 *  }
 *  </code>
 */
public class UnimplementedPossibilityException extends UnimplementedException
{
	public UnimplementedPossibilityException(String message) {
		super(message);
	}
}