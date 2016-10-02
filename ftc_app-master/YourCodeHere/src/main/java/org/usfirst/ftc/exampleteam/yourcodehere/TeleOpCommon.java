package org.usfirst.ftc.exampleteam.yourcodehere;

import com.qualcomm.robotcore.hardware.*;

import org.swerverobotics.library.*;
import org.swerverobotics.library.interfaces.*;

//import org.avalancherobotics.standalone.interfaces.ILayeredOpMode;
import org.avalancherobotics.standalone.interfaces.*;

/**
 * Version 1.0 of Team Avalanche 6253's TeleOp program for Robot version 2.0.
 * Currently most distance and position values are arbitrary due to not having a complete robot we can test values on.
 * Nearly finished, all methods save loadDispenser are written, needs some fixing, values, and a lot of testing.
 * After testing of this code, the next step is to add PID Controls
 */
public class TeleOpCommon
{
    // Variables
    private ILayeredOpMode opMode;

    //Declare ModifiedBoolean methods. These methods decide whether an action is running or not. (auto sets to false when not specified)
    private ModifiedBoolean testRunning = new ModifiedBoolean();
    private ModifiedBoolean runningExtendSlide = new ModifiedBoolean();
    private ModifiedBoolean runningRetractSlide = new ModifiedBoolean();

    //Declare global variable PID
    private PIDControl motorSlidePID;

    //Enums

    //Used to tell where the servoSlide is (whether it's in a scoring position or neutral position
    SlidePosition slidePosition;

    //shows what step score method is on
    private int scoreToggle = 0;

    //How long it has been since the Tape has been called
    private long tapeStartTime = 0;

    // defaults to blue alliance
    private boolean isBlue = true;

    //tells whether triggers are in resting position or are down/active, starts at rest
    private boolean atRestTriggers = true;

    //tells whether conveyor is running automatically in score method
    private boolean autoConveyorRunning = false;

    // methods with these variables need values
    private final double ARBITRARYDOUBLE = 0;
    private final int ARBITRARYINT = 0;

    //Declare starting positions for tape, arm, and slide motors
    private int startPosTape;
    private int startPosArm;
    private int startPosSlide;
    private int maxTapeLength;
    private double botPosition;
    private double midPosition;
    private double topPosition;
    //farthest slide can extend without damage
    private int maxSlideLength;

    //sees if this is the first time pressing the button
    private boolean firstPressedDPad = true;
    private boolean firstPressedLeftTrigger = true;

    private final double RIGHT_ZIP_UP = 1; //ARBITRARY
    private final double RIGHT_ZIP_DOWN = 0; //ARBITRARY
    private final double LEFT_ZIP_UP = 1; //ARBITRARY
    private final double LEFT_ZIP_DOWN = 0; //ARBITRARY
    private final int TICKS_IN_DEGREE_ARM = ARBITRARYINT;
    private final int TICKS_IN_INCH_SLIDE = 1200; //ARBITRARY
    private final double LOCK_ENGAGED = 1; //ARBITRARY
    private final double LOCK_DISENGAGED = 0; //ARBITRARY
    private final double CONSTANT_DRIVE_SPEED = 1; //TESTING PURPOSES


    // Declare drive motors
    IDcMotor motorLeftFore;
    IDcMotor motorLeftAft;
    IDcMotor motorRightFore;
    IDcMotor motorRightAft;

    // Declare drawer slide motor and servos
    // motor extends/retracts slides
    // servoSlide(continuous) slides the deposit box laterally
    // servoConveyor runs the conveyor belt, dispensing blocks over the side
    // servoLock is the servo for locking the tape measure in place once hanging.
    IDcMotor motorSlide;
    IServo servoSlide;
    IServo servoConveyor;
    IServo servoLock;

    // Declare tape measure motor and servo
    // motor extends/retracts tape
    // servo(continuous) angles tape
    IDcMotor motorTape;
    IServo servoTape;

    // Declare motor that spins the harvester
    IDcMotor motorHarvest;

    // Declare motor that raises and lowers the collection arm
    IDcMotor motorArm;

    // Declare zipline flipping servos
    IServo servoLeftZip;
    IServo servoRightZip;

    // Declare sensors
    //GyroSensor gyro;

    //Declare color sensors and enalbe led
    //ColorSensor colorLeft;
    //ColorSensor colorRight;

    // TODO: Input handling could be simplified if IInputDevice supported
    //   a "getControlByKeyCode" method, or better yet, a
    //   "visitControlsMatchingKeyCode(ControlVisitor)" method (avoids
    //   memory allocations).  InputSynchronizer should cache a tree of all
    //   controls so that this becomes one tree lookup rather than O(k) tree
    //   lookups, where k is the number of connected inputs (could be bad
    //   if sensors get in on the game!).  This would probably require the
    //   IInputDevice interface to force implementations to provide events
    //   for state changes; in this case there would need to be an event
    //   for whenever Controls are added, removed, or change key code.
    //   Another method would need to be written:
    //     boolean isKeyPressed(IInputDevice device, int keyCode)
    //   which would use the visitControlsMatchingKeyCode method to efficiently
    //   scan for pressed key codes that match the given keyCode.
    //   With that done, it would be possible to apply PlayerActions directly
    //   in the inputHandling method.  There would probably be no need to have
    //   a PlayerActions class at all.  When an event is received, just query
    //   the existing state with isKeyPressed and fall back on inspecting the
    //   event whenever it doesn't have a commutative relationship with some
    //   other state.
    //   (err... this could get slightly complicated, which is why I'm not
    //   solving it /right now/).
    private class PlayerActions
    {
		boolean doTapeExtend    = false;
		boolean doTapeRetract   = false;
		boolean joinBlueTeam    = false;
		boolean joinRedTeam     = false;
		boolean toggleHarvester = false;
	}

	private PlayerActions actions = new PlayerActions();

    //@Override
    //public void main() throws InterruptedException {
    public void run() throws InterruptedException {
        hardwareMapping();

        // TODO: revive this!
        //  Or better yet, move it into the Android main() function.
        //  It might be practical to call hardwareMapping() from both main() functions.
        //waitForStart();

        // Go go gadget robot!
        while (opModeIsActive()) {

            //Checks to see if something changed on the controller ie. a button is pressed or a trigger is bumped
            if (updateGamepads()) {

                //AUTOMATIC CONTROLS//
                setAllAutoMethods();

                //TESTING PURPOSES
                if (gamepad1.b) {
                    testRunning.toggle();
                    if (testRunning.getValue()) {
                        motorSlide.setPower(0);
                    } else {
                        motorSlide.setTargetPosition(motorSlide.getCurrentPosition() + 1680 * 5);
                    }
                }

                //MANUAL CONTROLS
                manualMethods();
            }

            //Runs autonomous methods- takes the set values and moves motors to them
            //based off power curve
            runAllAutoMethods();

            //read sensors and adjust accordingly
            sensorChanges();

            if (testRunning.getValue()) {
                testContMotor();
            }

            idle();
        }
    }


    private void handleInput(IInputDevice.IEvent event)
    {
		if ( event instanceof InputBooleanEvent )
			handleBooleanEvents((InputBooleanEvent)event);
		else
		if ( event instanceof InputAnalogEvent )
			handleAnalogEvents((InputAnalogEvent)event);
    }

    private void handleBooleanEvents(InputBooleanEvent event)
    {
		final IInputDevice           device     = event.getDevice();
		final InputBooleanControl    key        = event.getControl();
		final int                    keyCode    = key.getKeyCode();
		final KeyCodes               kcodes     = KeyCodes.getDefaults();

		final int PRESS = 0;
		final int RELEASE = 1;

		final int eventType;
		switch(event.getType())
		{
			case InputBooleanEvent.Type.PRESS:   eventType = PRESS;   break;
			case InputBooleanEvent.Type.RELEASE: eventType = RELEASE; break;
			default:
				throw new Exception("Unimplemented InputBooleanEvent type: "+
					event.getType());
		}

        //Toggle Team (if we need to score on an opponent's ramp)
        if (keyCode == kcodes.ANDROID_BACK && keyCode == kcodes.ANDROID_BUTTON_B)
        {
			switch(eventType) {
				case PRESS:   actions.joinRedTeam = true;  break;
				case RELEASE: actions.joinRedTeam = false; break;
			}
		}

        if (keyCode == kcodes.ANDROID_BACK && keyCode == kcodes.ANDROID_BUTTON_X)
        {
			switch(eventType) {
				case PRESS:   actions.joinBlueTeam = true;  break;
				case RELEASE: actions.joinBlueTeam = false; break;
			}
		}



		switch ( device.getDeviceName() )
		{
			case "gamepad#1":
			{
				break;
			}

			case "gamepad#2":
			{
				if ( keyCode == kcodes.ANDROID_DPAD_UP && eventType == PRESS )
					actions.doTapeExtend = true;
				else
				if ( keyCode == kcodes.ANDROID_DPAD_UP && eventType == RELEASE )
					actions.doTapeExtend = false;
				else
				if ( keyCode == kcodes.ANDROID_DPAD_DOWN && eventType == PRESS )
					actions.doTapeRetract = true;
				else
				if ( keyCode == kcodes.ANDROID_DPAD_DOWN && eventType == RELEASE )
					actions.doTapeRetract = false;
				break;
			}

		}
    }

    private void handleAnalogEvent(InputAnalogEvent event)
    {
		final IInputDevice  device = event.getDevice();
		final String        name   = event.getName();

		switch ( device.getDeviceName() )
		{
			case "gamepad#1":
			{
				if ( name == "L2 - Left Trigger" && event.getNewValue(0) > 0.8 )
					actions.toggleHarvester = true;
				else
					actions.toggleHarvester = false;
			}
			case "gamepad#2":
			{
			}
		}
    }

    /**
     * in the sensorChanges method we put the methods that depend on
     * sensor input do decide when to start, stop, etc.
     * It is the last mehtod to run in the main loop
     */

    private void sensorChanges() {

        //stops the slide and updates position when reaches desired position
        //slideAutoStop();

        /** THIS IS A TEST METHOD FOR THE COLOR SENSOR
         if (colorLeft.blue() + colorLeft.red() + colorLeft.green() > 50)
         motorHarvest.setPower(0);
         else
         motorHarvest.setPower(1);
         */
    }

    private void manualMethods() {
        //Toggle Team (if we need to score on an opponent's ramp)
        if (actions.joinRedTeam  && !actions.joinBlueTeam)
            isBlue = false;

        if (actions.joinBlueTeam && !actions.joinRedTeam)
            isBlue = true;


        //starts and stops harvester
        if (gamepad1.left_trigger > .8 && firstPressedLeftTrigger) {
            toggleHarvester(1);
            firstPressedLeftTrigger = false;
        } else
            firstPressedLeftTrigger = true;

        //toggles harvester spin direction
        if (gamepad1.left_bumper)
            reverseHarvester();

        //Read Joystick Data and Update Speed of Left and Right Motors
        //If joystick buttons are pressed, sets drive power to preset value
        manualDriveControls(true);


        //toggles zip line position
        if (gamepad2.x && !gamepad2.back)
            triggerZipline();


        //stops all motors and cancels all motor operations, basically a panic button that stops all functions
        if ((gamepad1.back && gamepad1.start) || (gamepad2.back && gamepad2.start))
            cancelAll();


        //Stops any auto methods using slides and manually controls power with joysticks
        if (gamepad2.left_stick_y < -.2 || gamepad2.left_stick_y > .2) { //Threshold so you don't accidentally start running the slides manually
            if (motorSlide.getCurrentPosition() > startPosSlide && motorSlide.getCurrentPosition() < maxSlideLength)
                motorSlide.setPower(scaleInput(gamepad2.left_stick_y));
            else {
                if (motorSlide.getCurrentPosition() > startPosSlide)
                    motorSlide.setPower(1); //ARBITRARY NEEDS TESTING
                else
                    motorSlide.setPower(-1); //ARBITRARY NEEDS TESTING
            }
            runningExtendSlide.setFalse();
            runningRetractSlide.setFalse();
        } else if (!runningExtendSlide.getValue() && !runningRetractSlide.getValue())
            motorSlide.setPower(0);


        //adjust the slide servos manually
        if (gamepad2.left_bumper) {
            servoSlide.setPosition(0); //COULD BE 1 ARBITRARY
        }
        if (gamepad2.right_bumper) {
            servoSlide.setPosition(1); //COULD BE 0 ARBITRARY
        }


        //manually adjust the conveyors
        if (gamepad2.right_trigger > .2 || gamepad2.left_trigger > .2) {
            if (gamepad2.right_trigger > .2) //.2 is a threshold so you don't accidentally press it
                servoConveyor.setPosition(gamepad2.right_trigger / 2 + .5);
            else if (gamepad2.left_trigger > .2)
                servoConveyor.setPosition(-gamepad2.left_trigger / 2 + .5);
            autoConveyorRunning = false;
        } else {
            if (!autoConveyorRunning)
                servoConveyor.setPosition(.5);
        }

        //adjust angle of tape
        servoTape.setPosition(scaleInput(gamepad2.right_stick_y / 2 + .5));


    }

    /**
     * put all autonomous setters in here, when the code updates the gamepad
     * it should look at this section to see if we're starting any auto methods
     * DO NOT PUT MANUAL METHODS- Separate Wrapper Method
     */
    private void setAllAutoMethods() {

        /**
         * Scoring methods
         * First button press extends slides and shuttle
         * Second button press starts conveyor
         * Third button press stops conveyor, and restores slides and shuttle to default positions
         */
        scoreSet(Height.BOT, gamepad2.a);
        scoreSet(Height.MID, gamepad2.b && !gamepad2.back);
        scoreSet(Height.TOP, gamepad2.y);

    }

    /**
     * put all autonomous functions in here, to be run after code finishes reading gamepad
     * this method runs approx every 35 ms
     * this section is for setting the motor power for all of the automatic
     * methods that we call in the gamepad section
     */
    private void runAllAutoMethods() {
        scoreRun();

        //Controls for motorTape - Also automatically sets the tapeLock after a specified amount of time
        if (actions.doTapeExtend || actions.doTapeRetract) {
            // if it's the first time you loop after holding down the button.
            if ((motorTape.getPower() == 0) && !firstPressedDPad) {
                tapeStartTime = System.currentTimeMillis();
                firstPressedDPad = true;
            }
            if ((System.currentTimeMillis() - tapeStartTime) > 500) {    /*Test for THRESH, the time which it takes for the lock to unlock  ARBITRARY*/
                if (actions.doTapeExtend) {
                    //if (motorTape.getCurrentPosition()) //< maxTapeLength) UNCOMMENT ON FINAL ARBITRARY
                    motorTape.setPower(1); //MAY BE 1 OR 0 NEEDS TESTING
                } else {
                    //if (motorTape.getCurrentPosition()) //UNCOMMENT ON FINAL ARBITRARY  > startPosTape) //COULD BE GREATER OR LESS DEPENDING ON TESTING THIS IS SO YOU DON'T RETRACT TOO FAR
                    motorTape.setPower(-1); //MAY BE 1 OR -1 NEEDS TESTING
                }
            } else {
                //motorTape.setPower(ARBITRARYDOUBLE) //REVERSE THE MOTOR BEFORE DISENGAGING THE LOCK TO PREVENT GETTING STUCK
                servoLock.setPosition(LOCK_DISENGAGED);
            }
        } else {
            motorTape.setPower(0.0);
            servoLock.setPosition(LOCK_ENGAGED);
            firstPressedDPad = false;
        }

    }

    //Initialize and Map All Hardware
    private void hardwareMapping() throws InterruptedException {
        // Initialize drive motors
        motorLeftFore = hardwareMap.get(IDcMotor.class).get("motorLeftFore");
        motorLeftAft = hardwareMap.get(IDcMotor.class).get("motorLeftAft");
        motorRightFore = hardwareMap.get(IDcMotor.class).get("motorRightFore");
        motorRightAft = hardwareMap.get(IDcMotor.class).get("motorRightAft");

        //Left and right motors are on opposite sides and must spin opposite directions to go forward
        motorLeftAft.setDirection(DcMotor.Direction.REVERSE);
        motorLeftFore.setDirection(DcMotor.Direction.REVERSE);

        // Initialize drawer slide motor and servos
        motorSlide = hardwareMap.get(IDcMotor.class).get("motorSlide");
        servoSlide = hardwareMap.get(IServo.class).get("servoSlide");
        servoConveyor = hardwareMap.get(IServo.class).get("servoConveyor");

        // Initialize tape measure motor, servo tape, and servo lock.
        motorTape = hardwareMap.get(IDcMotor.class).get("motorTape");
        servoTape = hardwareMap.get(IServo.class).get("servoTape");
        servoLock = hardwareMap.get(IServo.class).get("servoLock");

        // Initialize motor that spins the harvester
        motorHarvest = hardwareMap.get(IDcMotor.class).get("motorHarvest");

        // Initialize motor that raises and lowers the collection arm
        motorArm = hardwareMap.get(IDcMotor.class).get("motorArm");

        // Initialize zipline flipping servos
        servoLeftZip = hardwareMap.get(IServo.class).get("servoLeftZip");
        servoRightZip = hardwareMap.get(IServo.class).get("servoRightZip");

        // Initialize sensors

        //Initialize color sensor and turn on led
        //colorLeft = hardwareMap.colorSensor.get("colorLeft");
        //colorLeft.enableLed(true);

        //Initialize gyro
        //gyro = hardwareMap.gyroSensor.get("gyro");
        //Initialize touch sensors
        //touchLeftPos = hardwareMap.touchSensor.get("touchLeftPos");
        //touchRightPos = hardwareMap.touchSensor.get("touchRightPos");
        //touchNeutralPos = hardwareMap.touchSensor.get("touchNeutralPos");

        //gyro.calibrate();

        // Reset encoders
        this.motorLeftAft.setMode(IDcMotorController.RunMode.RESET_ENCODERS);
        this.motorLeftFore.setMode(IDcMotorController.RunMode.RESET_ENCODERS);
        this.motorTape.setMode(IDcMotorController.RunMode.RESET_ENCODERS);
        this.motorHarvest.setMode(IDcMotorController.RunMode.RESET_ENCODERS);
        this.motorArm.setMode(IDcMotorController.RunMode.RESET_ENCODERS);
        this.motorRightAft.setMode(IDcMotorController.RunMode.RESET_ENCODERS);
        this.motorRightFore.setMode(IDcMotorController.RunMode.RESET_ENCODERS);
        this.motorSlide.setMode(IDcMotorController.RunMode.RESET_ENCODERS);

        //Set Runmode for all motors to run using encoders
        motorLeftFore.setMode(IDcMotorController.RunMode.RUN_USING_ENCODERS);
        motorRightFore.setMode(IDcMotorController.RunMode.RUN_USING_ENCODERS);
        motorLeftAft.setMode(IDcMotorController.RunMode.RUN_USING_ENCODERS);
        motorRightAft.setMode(IDcMotorController.RunMode.RUN_USING_ENCODERS);
        motorSlide.setMode(IDcMotorController.RunMode.RUN_USING_ENCODERS);
        motorTape.setMode(IDcMotorController.RunMode.RUN_USING_ENCODERS);
        motorArm.setMode(IDcMotorController.RunMode.RUN_USING_ENCODERS);
        motorHarvest.setMode(IDcMotorController.RunMode.RUN_USING_ENCODERS);

        //keep track of the starting positions of arm, slide, and tape motors
        startPosArm = motorArm.getCurrentPosition();
        startPosSlide = motorSlide.getCurrentPosition();
        startPosTape = motorSlide.getCurrentPosition();

        //Score heights for slides
        botPosition = startPosTape + 3000; //ARBITRARY
        midPosition = startPosTape + 6000; //ARBITRARY
        topPosition = startPosTape + 9000; //ARBITRARY
        maxSlideLength = startPosTape + 12000;

        //keep track of max length tape should extend
        maxTapeLength = motorTape.getCurrentPosition() + 6000; //ARBITRARY

        //initializes to neutral because that's where the starting position is
        slidePosition = SlidePosition.NEUTRAL;

        motorSlidePID = new PIDControl(motorSlide);
        motorSlidePID.setConstants(.001, .001, .001); //ARBITRARY
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                                                                                            //
    //                                     main methods                                           //
    //                                                                                            //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void triggerZipline() {
        if (isBlue) {
            if (atRestTriggers)
                servoRightZip.setPosition(RIGHT_ZIP_UP); //Needs resting and active servo positions
            else
                servoRightZip.setPosition(RIGHT_ZIP_DOWN);
        } else {
            if (atRestTriggers)
                servoLeftZip.setPosition(LEFT_ZIP_UP);
            else
                servoLeftZip.setPosition(LEFT_ZIP_DOWN);
        }
        atRestTriggers = !atRestTriggers;
    }

    private void toggleHarvester(double power) {
        if (motorHarvest.getPower() == 0.0)
            motorHarvest.setPower(power);
        else
            motorHarvest.setPower(0.0);
    }

    private void reverseHarvester() {
        motorHarvest.setPower(-motorHarvest.getPower());
    }

    /**
     * UNUSED FOR NOW
     * //return robot to initialization position
     * private void initPositionSet () {
     * //sets harvester and drive power to 0
     * motorHarvest.setPower(0);
     * setLeftDrivePower(0);
     * setRightDrivePower(0);
     * <p>
     * //sets the target position for arm, slide, and tape to the init positions
     * setPosMotor(motorArm, runningCancelAllArm, 1, startPosArm);
     * setPosMotor(motorTape, runningCancelAllTape, 1, startPosTape);
     * setPosMotor(motorSlide, runningCancelAllSlide, 1, startPosSlide);
     * }
     * <p>
     * //runs until the motors hit the initialization position
     * <p>
     * private void initPositionRun() {
     * if (runningCancelAllArm.getValue() || runningCancelAllSlide.getValue() || runningCancelAllTape.getValue()) {
     * runToPos(motorArm, 50, runningCancelAllArm, null);
     * runToPos(motorArm, 50, runningCancelAllTape, null);
     * runToPos(motorSlide, 50, runningCancelAllTape, null);
     * }
     * }
     */

    //stop all motors and ends all processes
    private void cancelAll() {
        motorArm.setPower(0);
        motorHarvest.setPower(0);
        motorTape.setPower(0);
        motorSlide.setPower(0);
        setLeftDrivePower(0);
        setRightDrivePower(0);
        servoSlide.setPosition(.5);
        servoConveyor.setPosition(.5);

        testRunning.setFalse();
        runningExtendSlide.setFalse();
        runningRetractSlide.setFalse();
        autoConveyorRunning = false;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                                                                                            //
    //                                     support methods                                        //
    //                                                                                            //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void setPosMotor(DcMotor motor, ModifiedBoolean isRunning, int ticksInInch, double targetLocation) {
        isRunning.toggle();
        if (!isRunning.getValue()) {
            motor.setPower(0);
        } else
            motor.setTargetPosition((int) (ticksInInch * targetLocation));
    }

/*
    //HOPEFULLY WILL REPLACE THIS METHOD WITH SOMETHING THAT IS MORE BETTER (PID?)
    private void runToPos(DcMotor motor, double minPower, ModifiedBoolean isRunning, ModifiedBoolean runNext) {
        if (isRunning.getValue()) {
            if (motor.getCurrentPosition() - 10 < motor.getTargetPosition() && motor.getCurrentPosition() < motor.getTargetPosition() + 10) {
                motor.setPower(0);
                isRunning.setFalse();
                if (runNext != null)
                    runNext.setTrue();
            } else
                setCurvedPower(motor, 30, 1, minPower); //ARBITRARY (30)
        }
    }
    */

    //new run to pos method
    private void runToPos(DcMotor motor, PIDControl c, ModifiedBoolean isRunning, ModifiedBoolean runNext) {
        if (isRunning.getValue()) {
            if (motor.getCurrentPosition() < motor.getTargetPosition()) {
                c.updatePower(System.currentTimeMillis(), motor.getTargetPosition(), motor.getCurrentPosition());
            } else {
                motor.setPower(0);
                isRunning.setFalse();
                if (runNext != null)
                    runNext.setTrue();
                c.reset();
            }
        }
    }


    private void setLeftDrivePower(double power) {
        motorLeftFore.setPower(power);
        motorLeftAft.setPower(power);
    }

    private void setRightDrivePower(double power) {
        motorRightFore.setPower(power);
        motorRightAft.setPower(power);
    }

    /*Currently sets power based on a logarithmic scale- stays at full power till thresh
    thresh is how many ticks away before it starts to slow down
    inputPower is the max running speed
    NEEDS TO BE CONFIGURED TO WORK BOTH DIRECTIONS (FORWARDS AND REVERSE)
    NOT USED DUE TO PID */
    private void setCurvedPower(DcMotor motor, double thresh, double inputPower, double minPower) { //NEED TO DO TESTING TO SEE BEST THRESH
        int target = motor.getTargetPosition();
        if (target - motor.getCurrentPosition() > thresh)
            motor.setPower(inputPower);
        else {
            double overThr = motor.getCurrentPosition() - target + thresh;
            double power = (inputPower - minPower) / (1 + Math.pow(Math.E, Math.pow(overThr / thresh - .5, -4) * 12)) + minPower;
            motor.setPower(power);
        }
    }

    //Default Scale Input Method created by FTC- We will use this one until someone creates a better one.
    //Used for scaling joysticks, basically is a floor function that is squared
    double scaleInput(double dVal) {
        double[] scaleArray = {0.0, 0.05, 0.09, 0.10, 0.12, 0.15, 0.18, 0.24,
                0.30, 0.36, 0.43, 0.50, 0.60, 0.72, 0.85, 1.00, 1.00};

        // get the corresponding index for the scaleInput array.
        int index = (int) (dVal * 16.0);
        if (index < 0)
            index = -index;
        if (index > 16)
            index = 16;


        double dScale;
        if (dVal < 0)
            dScale = -scaleArray[index];
        else
            dScale = scaleArray[index];

        return dScale;
    }

    //Test method to see if writing code without whiles works. (It does)
    private void testContMotor() {
        if (motorSlide.getCurrentPosition() < motorSlide.getTargetPosition()) {
            setCurvedPower(motorSlide, 1000, 1, 0.3);
        } else {
            motorSlide.setPower(0);
            testRunning.setFalse();
        }
    }

    //This method is a combination of autonomous and manual controls
    //Certain actions such as the conveyor run on button prompts while
    //Other automated actions such as shuttling run on their own
    private void scoreSet(Height height, boolean button) {  // can be either top mid or bot
        if (button) {
            if (scoreToggle == 0) {
                //extend slides to specified height
                motorSlidePID.reset();
                extendSlideSet(height);
                shuttle(isBlue);
                scoreToggle++;
                return;
            }

            if (scoreToggle == 1) {
                //start the conveyor
                if (isBlue)
                    servoConveyor.setPosition(1); //MAY BE 0
                else
                    servoConveyor.setPosition(0);
                autoConveyorRunning = true;
                scoreToggle++;
                return;
            }
            if (scoreToggle == 2) {
                runningExtendSlide.setFalse(); //Just in case extendSlide is still running set it to false
                //stop conveyor and return to retracted position
                servoConveyor.setPosition(.5);
                autoConveyorRunning = false;
                shuttle(isBlue); //Runs using touch sensor
                motorSlidePID.reset();
                setPosMotor(motorSlide, runningRetractSlide, 1, startPosSlide);
                scoreToggle = 0;
                shuttle(isBlue);
            }
            /**
             * NEED TO WRITE METHOD THAT CAN SHUTTLE THE DISPENSER TO A SPECIFIC DISTANCE - most likely
             * will be time based and refined through testing since continuous servos don't have
             * encoders or positions
             */
        }
    }

    /* Old non-PID
    private void scoreRun() {
        runToPos(motorSlide, 30, runningExtendSlide, null); //ARBITRARY
        runToPos(motorSlide, 30, runningRetractSlide, null); //ARBITRARY
    }
    */

    //new score run
    private void scoreRun() { //you must loop this method!
        runToPos(motorSlide, motorSlidePID, runningExtendSlide, null);
        runToPos(motorSlide, motorSlidePID, runningRetractSlide, null);
    }

    private void loadDispenserSet() {

    }

    private void loadDispenserRun() {

    }

    //returns and updates are for testing purposes
    private void extendSlideSet(Height height) { //Can be either top mid or bot
        if (height == Height.TOP) {
            setPosMotor(motorSlide, runningExtendSlide, TICKS_IN_INCH_SLIDE, topPosition);
            return;
        }

        if (height == Height.MID) {
            setPosMotor(motorSlide, runningExtendSlide, TICKS_IN_INCH_SLIDE, midPosition);
            return;
        }

        if (height == Height.BOT) {
            setPosMotor(motorSlide, runningExtendSlide, TICKS_IN_INCH_SLIDE, botPosition);
            return;
        }

        telemetry.addData("NOT A VALID HEIGHT FOR SLIDES", "FIX CODE");
        telemetry.update();
    }

    /*
    Runs tank drive and arcade drive
    If boolean is true, it runs tank if false, it runs arcade
    with tank, each joystick's y value controls each side
     ex. left joystick controls left wheels right joystick controls right wheels
     With Arcade drive, the left trigger y controls drive and right trigger x controls turning
     kind of like a video game
     */
    private void manualDriveControls(boolean usingTankDrive) {
        if (usingTankDrive) {
            if (gamepad1.right_trigger > .7 && (gamepad1.right_stick_y > .2 || gamepad1.right_stick_y < -.2)) {
                if (gamepad1.left_stick_y > 0)
                    setLeftDrivePower(CONSTANT_DRIVE_SPEED);
                else
                    setLeftDrivePower(-CONSTANT_DRIVE_SPEED);
            } else
                setLeftDrivePower(scaleInput(gamepad1.left_stick_y));

            if (gamepad1.right_trigger > .7 && (gamepad1.right_stick_y > .2 || gamepad1.right_stick_y < -.2)) {
                if (gamepad1.right_stick_y > 0)
                    setRightDrivePower(CONSTANT_DRIVE_SPEED);
                else
                    setRightDrivePower(-CONSTANT_DRIVE_SPEED);
            } else
                setRightDrivePower(scaleInput(gamepad1.right_stick_y));
        } else {
            if (gamepad1.right_trigger > .7 && (gamepad1.right_stick_y > .2 || gamepad1.right_stick_y < -.2)) {
                if (gamepad1.left_stick_y > 0) {
                    setLeftDrivePower(CONSTANT_DRIVE_SPEED);
                    setRightDrivePower(CONSTANT_DRIVE_SPEED);
                } else {
                    setLeftDrivePower(-CONSTANT_DRIVE_SPEED);
                    setRightDrivePower(-CONSTANT_DRIVE_SPEED);
                }
            } else {
                setLeftDrivePower(scaleInput(gamepad1.left_stick_y) + scaleInput(gamepad1.right_stick_x));
                setRightDrivePower(scaleInput(gamepad1.left_stick_y) - scaleInput(gamepad1.right_stick_x));
            }
        }
    }


    //Method for controlling dispenser movement to and from scoring buckets
    private void shuttle(boolean directionLeft) {
        if (!(slidePosition == SlidePosition.NEUTRAL)) { //if shuttle is currently in an out position it returns to mid
            if (Math.abs(servoSlide.getPosition() - .5) == .5) {
                servoSlide.setPosition(.5);
            } else {
                if (directionLeft) //not yet sure if this value is 0 or 1 need to test
                    servoSlide.setPosition(1);
                else
                    servoSlide.setPosition(0);
            }
        } else {
            if (Math.abs(servoSlide.getPosition() - .5) == .5) { //if it's running to the left or right, (the power is 1 or 0) stops the servo
                servoSlide.setPosition(.5); //shuts off power
            } else {
                if (directionLeft)
                    servoSlide.setPosition(0); //not yet sure if this value is 0 or 1 need to test
                else
                    servoSlide.setPosition(1);
            }
        }
    }

    /**
     * updates slidePosition (the enum for keeping track of where the slide is)
     * and also stops the motor once it reaches the desired position by turning
     * off the power. This code runs every main loop in the sensorUpdates method
     */

    /**
     private void slideAutoStop() {
     if (slidePosition == SlidePosition.NEUTRAL) {
     if ((colorLeft.red() + colorLeft.green()+ colorLeft.blue()) > ARBITRARYDOUBLE) {
     motorSlide.setPower(.5);
     slidePosition = SlidePosition.LEFT;
     }

     if (touchRightPos.isPressed()) {
     motorSlide.setPower(.5);
     slidePosition = SlidePosition.RIGHT;
     }

     } else {
     if (touchNeutralPos.isPressed()) {
     motorSlide.setPower(.5);
     slidePosition = SlidePosition.NEUTRAL;
     }
     }
     }
     */
}
