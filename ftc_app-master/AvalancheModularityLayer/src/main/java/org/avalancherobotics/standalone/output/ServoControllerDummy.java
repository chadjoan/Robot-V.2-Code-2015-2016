package org.avalancherobotics.standalone.output;

// Implementation NOTE: The standalone package shall never depend on the
//   org.avalancherobotics.library package, the org.avalancherobotics.desktop package,
//   or any other implementors.  It should also use only Java code that will
//   be found on all platforms (ex: Android + Desktop), which means that only
//   things in java.util are remotely safe.
//

import org.avalancherobotics.standalone.device.HardwareDeviceDummy;
import org.avalancherobotics.standalone.internal.DeviceConnectivity;
import org.avalancherobotics.standalone.interfaces.IServoController;
import org.avalancherobotics.standalone.output.ServoDummy;

import java.util.ArrayList;

/**
 *  This class persists the same state (fields, variables, etc) as the
 *  com.qualcomm.robotcore.hardware.ServoController interface,
 *  but does not implement any of the same functionality or connectivity.
 *  <p>
 *  Interface taken from
 *  http://ftckey.com/apis/ftc/com/qualcomm/robotcore/hardware/ServoController.html
 *  on 2016-02-17.
 *
 *  @see org.avalancherobotics.standalone.interfaces.IServoController
 */
public class ServoControllerDummy extends HardwareDeviceDummy
	implements IServoController, DeviceConnectivity.Hub<Integer>
{
	// -------------------------------------------------------------------------
	//                       Integration/Internals
	// -------------------------------------------------------------------------
	private /*@NonNull*/ DeviceConnectivity.HubPortsAsArrayList<ServoDummy> hubPorts;
	private /*@NonNull*/ ArrayList<ServoDummy> servos = new ArrayList<>();
	private IServoController.PwmStatus pwmStatus = IServoController.PwmStatus.ENABLED; // TODO: is this the correct default?

	public ServoControllerDummy(/*@NonNull*/ String deviceName)
	{
		super(deviceName);
		this.hubPorts = new DeviceConnectivity.HubPortsAsArrayList(servos, ServoDummy.class);
	}

	/**
	 *  @return null if there is no servo connected to the given 'channel'.
	 */
	public ServoDummy getServo(int channel)
	{
		return (ServoDummy)this.hubPorts.getSpoke(channel);
	}

	private void enforceConnection(int channel)
	{
		hubPorts.enforceConnection(channel, "servo", "controller");
	}

	// -------------------------------------------------------------------------
	//            Implementation : DeviceConnectivity.Hub<int>
	// -------------------------------------------------------------------------

	@Override
	public DeviceConnectivity.HubPorts<Integer> getHubPorts() { return this.hubPorts; }

	// -------------------------------------------------------------------------
	//                  Implementation : IServoController
	// -------------------------------------------------------------------------

	/** Get the PWM status */
	public IServoController.PwmStatus  getPwmStatus() { return this.pwmStatus; }

	/** Get the position of a servo at a given channel */
	public double  getServoPosition(int channel)
	{
		enforceConnection(channel);
		return this.getServo(channel).getPosition();
	}

	/** PWM disable */
	public void  pwmDisable() { this.pwmStatus = IServoController.PwmStatus.DISABLED; }

	/** PWM enable */
	public void  pwmEnable() { this.pwmStatus = IServoController.PwmStatus.ENABLED; }

	/** Set the position of a servo at the given channel */
	public void  setServoPosition(int channel, double position)
	{
		enforceConnection(channel);
		this.getServo(channel).setPosition(position);
	}
}