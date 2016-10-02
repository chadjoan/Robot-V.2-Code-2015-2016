package org.usfirst.ftc.exampleteam.yourcodehere.desktop;

import org.usfirst.ftc.exampleteam.yourcodehere.*;

import org.avalancherobotics.desktop.VirtualOpMode;

import javax.swing.*;

public class DesktopMain {

	private VirtualOpMode opMode;

	/**
	 * Create the GUI and show it.  For thread safety,
	 * this method should be invoked from the
	 * event-dispatching thread.
	 */
	private static void createAndShowGUI() {
		System.out.println("GUI creation started.");

		JFrame frame = new JFrame("OpMode Test Bench");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//JLabel label = new
		//JLabel("OpMode Test Bench");
		//frame.add(label);

		frame.add(opMode.getPanel());
		frame.addKeyListener(opMode.getKeyboard());
		frame.addMouseListener(opMode.getMouse());
		frame.addMouseMotionListener(opMode.getMouse());

		//Display the window.
		frame.pack();
		frame.setVisible(true);

		System.out.println("GUI creation complete.");
	}

	public static void main(String[] args) {
		System.out.println("OpMode Test Bench begins execution.");

		System.out.println("Constructing OpMode.");
		// NOTE: This must happen before the .invokeLater call, because this
		//   is how we guarantee that the OpMode's JPanel has been created
		//   and will be available for the GUI construction event.
		opMode = new VirtualOpMode();
		System.out.println("OpMode constructed.");

		System.out.println("Registering GUI creation event.");
		//Schedule a job for the event-dispatching thread:
		//creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
		System.out.println("GUI creation event registered.");
	}

}