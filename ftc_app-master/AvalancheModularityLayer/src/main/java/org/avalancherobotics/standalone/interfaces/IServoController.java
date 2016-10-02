package org.avalancherobotics.standalone.interfaces;

// Implementation NOTE: The standalone package shall never depend on the
//   org.avalancherobotics.library package, the org.avalancherobotics.desktop package,
//   or any other implementors.  It should also use only Java code that will
//   be found on all platforms (ex: Android + Desktop), which means that only
//   things in java.util are remotely safe.
//

import org.avalancherobotics.standalone.interfaces.IDevice;

/**
 *  This interface exposes the same set of features as the
 *  com.qualcomm.robotcore.hardware.ServoController interface,
 *  but is not bundled with classes that have hard dependencies on Android.
 *  <p>
 *  Interface taken from
 *  http://ftckey.com/apis/ftc/com/qualcomm/robotcore/hardware/ServoController.html
 *  on 2016-02-17.
 */
public interface IServoController extends IDevice
{
	/** */
	public enum PwmStatus
	{
		DISABLED,
		ENABLED
	}

	/** Get the PWM status */
	public IServoController.PwmStatus  getPwmStatus();

	/** Get the position of a servo at a given channel */
	public double  getServoPosition(int channel);

	/** PWM disable */
	public void  pwmDisable();

	/** PWM enable */
	public void  pwmEnable();

	/** Set the position of a servo at the given channel */
	public void  setServoPosition(int channel, double position);
}