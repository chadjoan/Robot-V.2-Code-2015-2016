package org.avalancherobotics.desktop;

import org.avalancherobotics.standalone.device.DeviceDirectory;
import org.avalancherobotics.standalone.device.DeviceDummyRegistry;
import org.avalancherobotics.standalone.device.DeviceRegistry;
import org.avalancherobotics.standalone.device.HardwareDeviceDummy;
import org.avalancherobotics.standalone.input.Axis;
import org.avalancherobotics.standalone.input.InputBooleanControl;
import org.avalancherobotics.standalone.input.InputSynchronizer;
import org.avalancherobotics.standalone.input.KeyCodes;
import org.avalancherobotics.standalone.interfaces.ILayeredOpMode;
import org.avalancherobotics.standalone.interfaces.IDcMotorController;
import org.avalancherobotics.standalone.interfaces.IInputDevice;
import org.avalancherobotics.standalone.interfaces.IInputDeviceSet;
import org.avalancherobotics.standalone.interfaces.IDeviceRegistry;
import org.avalancherobotics.standalone.output.DcMotorDummy;
import org.avalancherobotics.standalone.output.DcMotorControllerDummy;
import org.avalancherobotics.standalone.output.ServoDummy;
import org.avalancherobotics.standalone.output.ServoControllerDummy;


import org.avalancherobotics.desktop.input.KeyboardDevice;
import org.avalancherobotics.desktop.input.MouseDevice;

import java.util.HashSet;

import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

/**
 *  A wrapper that exposes the functionality of a Qualcomm-based OpMode object
 *  through the ILayeredOpMode interface.
 */
public class VirtualOpMode implements ILayeredOpMode, DeviceDummyRegistry.DeviceCreationListener
{
	private   /*@NonNull*/   InputSynchronizer   inputDeviceSet;
	private   /*@NonNull*/   DeviceDummyRegistry deviceRegistry;

	private   /*@NonNull*/   KeyboardDevice      keyboardDevice;
	private   /*@NonNull*/   MouseDevice         mouseDevice;

	private   /*@NonNull*/   JPanel              panel;
	private   /*@NonNull*/   JScrollPane         scrollPane;

	// Stuff common to panels that display device information.
	// There is an intentional decision here to NOT inherit from JPanel:
	//   a swing class's method namespace is chalked full of stuff.  If we don't
	//   inherit from that, then there is no chance that our methods or symbols
	//   will collide with the class we are inheriting from.  Having a simple
	//   .getPanel() method makes the inheritence unnecessary.
	private abstract class DevicePanelBase
	{
		private   /*@NonNull*/ JPanel    panel;
		private   /*@NonNull*/ JTextPane textPane;
		protected /*@NonNull*/ StringBuilder stringBuilder;

		public DevicePanelBase(int initialTextCapacity)
		{
			this.stringBuilder = new StringBuilder(initialTextCapacity);
			this.panel = new JPanel();
			this.textPane = new JTextPane();
			this.textPane.setContentType("text/html");
			this.textPane.setEditable(false);
			this.panel.add(textPane);
		}

		public /*@NonNull*/ JPanel getPanel() { return this.panel; }

		protected final void beforeDataEnumeration()
		{
			stringBuilder.delete(0, stringBuilder.length());
		}

		protected final void afterDataEnumeration()
		{
			int len = stringBuilder.length();
			if ( len >= 5 && stringBuilder.substring(len-5,len).equals("<br>\n") );
				stringBuilder.delete(len-5,len);
			this.textPane.setText(stringBuilder.toString());
		}

		// Call this to put the most recent numbers into the text pane.
		// Override it to define which numbers/strings go into the text pane.
		protected abstract void updateData();

		protected <T> void line(String nameColumn, T valueColumn)
		{
			stringBuilder.append("<pre>");
			stringBuilder.append(nameColumn);
			stringBuilder.append(valueColumn);
			stringBuilder.append("</pre><br>\n");
		}
	}

	// Stuff common to both DcMotorPanel and DcMotorControllerPanel.
	private abstract class DcMotorPanelBase extends DevicePanelBase
	{

		public DcMotorPanelBase(int initialTextCapacity)
		{
			super(initialTextCapacity);
		}

		// Override this to determine how device mode should be determined
		// for the derived class.
		protected abstract IDcMotorController.DeviceMode getDeviceMode();

		// Line for metadata about the motor: what's its name, what does it
		//   reference, and so on.  Stuff about the motor that that doesn't
		//   describe readings from the motor itself.
		protected <T> void meta(String nameColumn, T valueColumn)
		{
			line(nameColumn,valueColumn);
		}

		// Line for data from the motor: how energized it is, what position,
		//   and son on.  These should describe readings from the motor itself.
		// In a more concrete sense: this should be the items that are affected
		//   by the controller's device mode.
		protected <T> void data(String nameColumn, T valueColumn)
		{
			/*@NonNull*/ final String htmlPrefix;
			/*@NonNull*/ final String htmlSuffix;
			switch ( getDeviceMode() )
			{
				case READ_ONLY:
					htmlPrefix = "<p style=\"color:Red\"><i><pre>";
					htmlSuffix = "</pre></i></p><br>\n";
					break;

				case READ_WRITE:
					htmlPrefix = "<p style=\"color:Blue\"><pre>";
					htmlSuffix = "</pre></p><br>\n";
					break;

				case SWITCHING_TO_READ_MODE:
					htmlPrefix = "<p style=\"color:Maroon\"><i><pre>";
					htmlSuffix = "</pre></i></p><br>\n";
					break;

				case SWITCHING_TO_WRITE_MODE:
					htmlPrefix = "<p style=\"color:Green\"><i><pre>";
					htmlSuffix = "</pre></i></p><br>\n";
					break;

				case WRITE_ONLY:
					htmlPrefix = "<p style=\"color:Lime\"><pre>";
					htmlSuffix = "</pre></p><br>\n";
					break;

				default:
					htmlPrefix = "<pre>";
					htmlSuffix = "</pre><br>\n";
					break;
			}
			stringBuilder.append(htmlPrefix);
			stringBuilder.append(nameColumn);
			stringBuilder.append((Object)valueColumn);
			stringBuilder.append(htmlSuffix);
		}
	}

	private class DcMotorPanel extends DcMotorPanelBase
	{
		private DcMotorDummy motor;

		public DcMotorPanel(/*@NonNull*/ DcMotorDummy motor)
		{
			super(400);
			this.motor = motor;
			if ( motor == null )
				throw new IllegalArgumentException("The motor parameter must be non-null.");
		}

		@Override
		protected IDcMotorController.DeviceMode getDeviceMode()
		{
			return motor.probeMotorControllerDeviceMode();
		}

		public void updateData()
		{
			beforeDataEnumeration();
			if ( motor == null )
				meta("Motor is null.","");
			else
			{
				meta("Device name:             ", motor.getDeviceName()       );
				meta("  Device type:           ", motor.getClass().getName()  );
				meta("  Closed:                ", motor.isClosed()            );
				meta("  Controller name:       ", motor.probeControllerName() );
				meta("  Controller port:       ", motor.getPortNumber()       );
				meta("  Device mode:           ", getDeviceMode()             );
				data("  Direction:             ", motor.probeDirection()      );
				data("  Channel mode:          ", motor.probeChannelMode()    );
				data("  Power:                 ", motor.probePower()          );
				data("  Floating:              ", motor.probePowerFloating()  );
				data("  Current position:      ", motor.probeCurrentPosition());
				data("  Target position:       ", motor.probeTargetPosition() );
			}
			afterDataEnumeration();
		}
	}

	private class DcMotorControllerPanel extends DcMotorPanelBase
	{
		private DcMotorControllerDummy controller;

		public DcMotorControllerPanel(/*@NonNull*/ DcMotorControllerDummy controller)
		{
			super(200);
			this.controller = controller;
			if ( controller == null )
				throw new IllegalArgumentException("The controller parameter must be non-null.");
		}

		@Override
		protected IDcMotorController.DeviceMode getDeviceMode()
		{
			return controller.getMotorControllerDeviceMode();
		}

		public void updateData()
		{
			beforeDataEnumeration();
			if ( controller == null )
				meta("Controller is null.","");
			else
			{
				meta("Device name:             ", controller.getDeviceName()       );
				meta("  Device type:           ", controller.getClass().getName()  );
				meta("  Closed:                ", controller.isClosed()            );
				meta("  Device mode:           ", getDeviceMode()                  );
			}
			afterDataEnumeration();
		}
	}

	private class ServoPanel extends DevicePanelBase {
		private ServoDummy servo;

		public ServoPanel(/*@NonNull*/ ServoDummy servo)
		{
			super(350);
			this.servo = servo;
			if ( servo == null )
				throw new IllegalArgumentException("The servo parameter must be non-null.");
		}


		public void updateData()
		{
			beforeDataEnumeration();
			if ( servo == null )
				line("Servo is null.","");
			else
			{
				line("Device name:             ", servo.getDeviceName()       );
				line("  Device type:           ", servo.getClass().getName()  );
				line("  Closed:                ", servo.isClosed()            );
				line("  Controller name:       ", servo.probeControllerName() );
				line("  Controller channel:    ", servo.getPortNumber()       );
				line("  Min Position:          ", servo.probeMinPosition()    );
				line("  Max Position:          ", servo.probeMaxPosition()    );
				line("  Position:              ", servo.getPosition()         );
				line("  Direction:             ", servo.getDirection()        );
			}
			afterDataEnumeration();
		}
	}

	private class ServoControllerPanel extends DevicePanelBase {
		private ServoControllerDummy controller;

		public ServoControllerPanel(/*@NonNull*/ ServoControllerDummy controller)
		{
			super(350);
			this.controller = controller;
			if ( controller == null )
				throw new IllegalArgumentException("The controller parameter must be non-null.");
		}


		public void updateData()
		{
			beforeDataEnumeration();
			if ( controller == null )
				line("Controller is null.","");
			else
			{
				line("Device name:             ", controller.getDeviceName()       );
				line("  Device type:           ", controller.getClass().getName()  );
				line("  Closed:                ", controller.isClosed()            );
				line("  PWM Status:            ", controller.getPwmStatus()        );
			}
			afterDataEnumeration();
		}
	}

	private class MouseDevicePanel extends DevicePanelBase {
		private MouseDevice mouse;

		public MouseDevicePanel(/*@NonNull*/ MouseDevice mouse)
		{
			super(350);
			this.mouse = mouse;
			if ( mouse == null )
				throw new IllegalArgumentException("The mouse parameter must be non-null.");
		}


		public void updateData()
		{
			beforeDataEnumeration();
			if ( mouse == null )
				line("Mouse is null.","");
			else
			{
				// TODO: Why did I think it had a .isClosed() member?
				line("Device name:             ", mouse.getDeviceName()               );
				line("  Device type:           ", mouse.getClass().getName()          );
				//line("  Closed:                ", mouse.isClosed()                    );
				line("  Mouse X:               ", mouse.getPointer().getCoord(Axis.X) );
				line("  Mouse Y:               ", mouse.getPointer().getCoord(Axis.Y) );
				int which = 0;
				for( IInputDevice.IControl control : mouse.getControls() )
				{
					if ( !(control instanceof InputBooleanControl) )
						continue;
					InputBooleanControl button = (InputBooleanControl)control;
					// TODO: There is probably a much more accurate way to get button number. (Ex: getKeyCode())
					line("  Mouse Button "+ which+ ":        ", button.getState());
				}
			}
			afterDataEnumeration();
		}
	}

	private class KeyboardDevicePanel extends DevicePanelBase {
		private KeyboardDevice keyboard;
		private /*@NonNull*/ StringBuilder keyCodesPressedStr = new StringBuilder(80);

		public KeyboardDevicePanel(/*@NonNull*/ KeyboardDevice keyboard)
		{
			super(350);
			this.keyboard = keyboard;
			if ( keyboard == null )
				throw new IllegalArgumentException("The keyboard parameter must be non-null.");
		}


		public void updateData()
		{
			beforeDataEnumeration();
			if ( keyboard == null )
				line("Keyboard is null.","");
			else
			{
				KeyCodes keyCodes = KeyCodes.getDefaults();

				// Create a list of all pressed keys.
				this.keyCodesPressedStr.delete(0, keyCodesPressedStr.length());
				for( IInputDevice.IControl control : keyboard.getControls() )
				{
					if ( !(control instanceof InputBooleanControl) )
						continue;
					InputBooleanControl key = (InputBooleanControl)control;
					if ( key.getState() != InputBooleanControl.State.PRESSED )
						continue;
					this.keyCodesPressedStr.append(keyCodes.getNamesAsStr(key.getKeyCode()));
					this.keyCodesPressedStr.append(", ");
				}

				// Chop off the last unnecessary ", ".
				int len = this.keyCodesPressedStr.length();
				if ( len > 2 )
					this.keyCodesPressedStr.delete(len-2,len);

				// TODO: Why did I think it had a .isClosed() member?
				line("Device name:             ", keyboard.getDeviceName()               );
				line("  Device type:           ", keyboard.getClass().getName()          );
				//line("  Closed:                ", keyboard.isClosed()                    );
				line("  Keys pressed:          ", this.keyCodesPressedStr.toString()     );
			}
			afterDataEnumeration();
		}
	}

	/**
	 *  Creates an OpMode that receives input from a Keyboard and Mouse via
	 *  Java's AWT library and displays hypothetical device states (over time)
	 *  as charts.
	 *  <p>
	 */
	public VirtualOpMode()
	{
		this.keyboardDevice = new KeyboardDevice("default_keyboard");
		this.mouseDevice = new MouseDevice("default_mouse");

		this.inputDeviceSet = new InputSynchronizer();
		this.inputDeviceSet.addDevice(this.keyboardDevice);
		this.inputDeviceSet.addDevice(this.mouseDevice);

		this.deviceRegistry = new DeviceDummyRegistry();
		this.deviceRegistry.addCreationListener(this);
		this.deviceRegistry.<KeyboardDevice>put(new DeviceDirectory<KeyboardDevice>(KeyboardDevice.class));
		this.deviceRegistry.<MouseDevice>put(new DeviceDirectory<MouseDevice>(MouseDevice.class));
		this.deviceRegistry.<KeyboardDevice>get(KeyboardDevice.class).put(this.keyboardDevice);
		this.deviceRegistry.<MouseDevice>get(MouseDevice.class).put(this.mouseDevice);

		this.panel = new JPanel(new GridBagLayout());

		this.scrollPane = new JScrollPane();
		this.panel.add(scrollPane, BorderLayout.CENTER);

		this.scrollPane.add((new KeyboardDevicePanel(this.keyboardDevice)).getPanel());
		this.scrollPane.add((new MouseDevicePanel(this.mouseDevice)).getPanel());
	}

	public /*@NonNull*/ JPanel getPanel()
	{
		return panel;
	}

	public void onCreate(HardwareDeviceDummy device)
	{
		if ( device instanceof DcMotorDummy )
			this.scrollPane.add((new DcMotorPanel((DcMotorDummy)device)).getPanel());
		else
		if ( device instanceof DcMotorControllerDummy )
			this.scrollPane.add((new DcMotorControllerPanel((DcMotorControllerDummy)device)).getPanel());
		else
		if ( device instanceof ServoDummy )
			this.scrollPane.add((new ServoPanel((ServoDummy)device)).getPanel());
		else
		if ( device instanceof ServoControllerDummy )
			this.scrollPane.add((new ServoControllerPanel((ServoControllerDummy)device)).getPanel());
	}

	/**
	 *  The caller should add this KeyboardDevice to the enclosing JFrame
	 *  (or whatever the top-level GUI component is) using
	 *  {@link java.awt.Component#addKeyListener} to allow keyboard events
	 *  to make it into the OpMode's system.
	 */
	public /*@NonNull*/ KeyboardDevice getKeyboard() { return this.keyboardDevice; }

	/**
	 *  The caller should add this MouseDevice to the enclosing JFrame
	 *  (or whatever the top-level GUI component is) using
	 *  {@link java.awt.Component#addMouseListener} and
	 *  {@link java.awt.Component#addMouseMotionListener} to allow mouse events
	 *  to make it into the OpMode's system.
	 */
	public /*@NonNull*/ MouseDevice getMouse() { return this.mouseDevice; }

	/**
	 *  Exposes the list of (pollable) input devices known to the ILayeredOpMode.
	 */
	public /*@NonNull*/ IInputDeviceSet getInputDeviceSet()
	{
		return inputDeviceSet;
	}

	/**
	 *  Exposes the registry of all devices known to the ILayeredOpMode.
	 */
	public /*@NonNull*/ IDeviceRegistry getDeviceRegistry()
	{
		return deviceRegistry;
	}
}
