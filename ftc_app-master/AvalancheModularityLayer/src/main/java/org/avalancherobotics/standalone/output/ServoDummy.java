package org.avalancherobotics.standalone.output;

// Implementation NOTE: The standalone package shall never depend on the
//   org.avalancherobotics.library package, the org.avalancherobotics.desktop package,
//   or any other implementors.  It should also use only Java code that will
//   be found on all platforms (ex: Android + Desktop), which means that only
//   things in java.util are remotely safe.
//

import org.avalancherobotics.standalone.internal.DeviceConnectivity;
import org.avalancherobotics.standalone.interfaces.IServo;
import org.avalancherobotics.standalone.interfaces.IServoController;
import org.avalancherobotics.standalone.device.HardwareDeviceDummy;

/**
 *  This class persists the same state (fields, variables, etc) as the
 *  com.qualcomm.robotcore.hardware.Servo interface,
 *  but does not implement any of the same functionality or connectivity.
 *  <p>
 *  Interface taken from
 *  http://ftckey.com/apis/ftc/com/qualcomm/robotcore/hardware/Servo.html
 *  on 2016-02-17.
 *
 *  @see org.avalancherobotics.standalone.interfaces.IServo
 */
public class ServoDummy extends HardwareDeviceDummy
	implements IServo, DeviceConnectivity.Spoke<Integer>
{
	// -------------------------------------------------------------------------
	//                       Integration/Internals
	// -------------------------------------------------------------------------
	private SpokeImpl                     spokePort;
	private ServoControllerDummy          controller  = null;
	private int                           channel     = 0;
	private double                        minPosition = 0.0;
	private double                        maxPosition = 0.0;
	private double                        position    = 0.0;
	private IServo.Direction              direction   = IServo.Direction.FORWARD;

	/**
	 *  Constructs a ServoDummy with the given 'deviceName' and 'controller'.
	 *  <p>
	 *  Passing a null value for the 'controller' parameter will indicate
	 *  that the ServoDummy is to begin its existence in a disconnected state.
	 */
	public ServoDummy(String deviceName, ServoControllerDummy controller, int channel)
	{
		super(deviceName);
		this.controller = controller;
		this.channel = channel; // TODO: this is an assumption.  Someone please check it.
		this.spokePort = new SpokeImpl(this);

		// Do this step of construction as late as possible.  The intent is to
		// ensure that any new connection is mutual (symmetrical), but we must
		// also avoid calling connect() with a partially uninitialized object.
		if ( controller != null )
		{
			try {
				DeviceConnectivity.<Integer>connect(controller, this, channel);
			}
			catch ( Exception e )
			{
				this.controller = null;
				this.channel = -1;
				throw e;
			}
		}
	}

	// -------------------------------------------------------------------------
	//                    Platform Specific Helpers (go here)
	// -------------------------------------------------------------------------

	/**
	 *  Convenience function that allows testing code to retrieve the controller
	 *  name.
	 *  If the controller is unassigned, this will return a distinctive
	 *  string of text that indicates this status.
	 *  <p>
	 *  This is intended to be called from test harnesses only, and not from
	 *  the logic that uses the device.  It has no equivalent in the device
	 *  hierarchies that it mimics, and will thus not port between OpMode
	 *  implementations.
	 */
	public String probeControllerName()
	{
		if ( this.controller == null )
			return "<controller is null>";
		else
			return controller.getDeviceName();
	}

	/**
	 *  Gets the servo's assigned minimum position.
	 *  <p>
	 *  This is intended to be called from test harnesses only, and not from
	 *  the logic that uses the device.  It has no equivalent in the device
	 *  hierarchies that it mimics, and will thus not port between OpMode
	 *  implementations.
	 */
	public double probeMinPosition() { return this.minPosition; }

	/**
	 *  Gets the servo's assigned maximum position.
	 *  <p>
	 *  This is intended to be called from test harnesses only, and not from
	 *  the logic that uses the device.  It has no equivalent in the device
	 *  hierarchies that it mimics, and will thus not port between OpMode
	 *  implementations.
	 */
	public double probeMaxPosition() { return this.maxPosition; }

	// TODO
	//public void advanceTime(double timeDelta) { ... }

	// -------------------------------------------------------------------------
	//            Implementation : DeviceConnectivity.Spoke<int>
	// -------------------------------------------------------------------------

	private class SpokeImpl implements DeviceConnectivity.SpokePort<Integer>
	{
		private /*@NonNull*/ ServoDummy servo;
		public SpokeImpl(ServoDummy servo) { this.servo = servo; }

		public DeviceConnectivity.Hub  getHub()        { return servo.controller; }
		public Integer                 getIndexOnHub() { return servo.channel; }
		public void  setHub(DeviceConnectivity.Hub<Integer> hub, Integer index)
		{
			this.servo.controller = (ServoControllerDummy)hub;
			this.servo.channel    = index;
		}
	}

	@Override
	public DeviceConnectivity.SpokePort<Integer> getSpokePort() { return this.spokePort; }

	// -------------------------------------------------------------------------
	//                    Implementation : IServo
	// -------------------------------------------------------------------------

	/** */
	@Override
	public double getDefaultMinPosition() { return 0.0; }

	/** */
	@Override
	public double getDefaultMaxPosition() { return 1.0; }

	/** Get Servo Controller */
	@Override
	public IServoController  getController() { return this.controller; }

	/** Get the direction */
	@Override
	public IServo.Direction  getDirection() { return this.direction; }

	/** Get Channel */
	@Override
	public int  getPortNumber() { return this.channel; }

	/**
	 *  Get the position of the servo
	 *  @return position, scaled from 0.0 to 1.0
	 */
	@Override
	public double  getPosition() { return this.position; }


	/**
	 *  Automatically scale the position of the servo.
	 *  <p>
	 *  For example, if scaleRange(0.2, 0.8) is set; then servo positions will be scaled to fit in that range.
	 *  <br>setPosition(0.0) scales to 0.2
	 *  <br>setPosition(1.0) scales to 0.8
	 *  <br>setPosition(0.5) scales to 0.5
	 *  <br>setPosition(0.25) scales to 0.35
	 *  <br>setPosition(0.75) scales to 0.65
	 *  <p>
	 *  This is useful if you don't want the servo to move past a given
	 *  position, but don't want to manually scale the input to setPosition
	 *  each time. getPosition() will scale the value back to a value between
	 *  0.0 and 1.0. If you need to know the actual position use
	 *  Servo.getController().getServoPosition(Servo.getChannel()).
	 *
	 *  @param min  minimum position of the servo from 0.0 to 1.0
	 *  @param max  maximum position of the servo from 0.0 to 1.0
	 *  @throws java.lang.IllegalArgumentException if out of bounds, or min >= max
	 */
	@Override
	public void  scaleRange(double min, double max)
	{
		// TODO: This is assumed to set the min and max position variables,
		//       but is that really how it works?
		if ( min > max )
			throw new IllegalArgumentException(
				"Attempted to create an inconsitent scaleRange.  "+
				"Existing scaleRange:  ["+ this.minPosition+ ","+ this.maxPosition+ "];  "+
				"Attempted scaleRange: ["+ min+ ","+ max+ "];");

		if ( min == max )
			throw new IllegalArgumentException(
				"Attempted to create a scaleRange that always sets position to a single value.  "+
				"Existing scaleRange:  ["+ this.minPosition+ ","+ this.maxPosition+ "];  "+
				"Attempted scaleRange: ["+ min+ ","+ max+ "];");

		if ( min < 0 || max > 0 )
			throw new IllegalArgumentException(
				"Attempted to create an out of bounds scaleRange.  "+
				"Existing scaleRange:  ["+ this.minPosition+ ","+ this.maxPosition+ "];  "+
				"Attempted scaleRange: ["+ min+ ","+ max+ "];");

		this.minPosition = min;
		this.maxPosition = max;
	}

	/** Set the direction */
	@Override
	public void  setDirection(IServo.Direction direction) { this.direction = direction; }

	/**
	 *  Set the position of the servo
	 *  @param position from 0.0 to 1.0
	 */
	@Override
	public void  setPosition(double position)
	{
		this.position = (position * (this.maxPosition - this.minPosition)) + this.minPosition;
	}
}