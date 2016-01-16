package org.usfirst.ftc.exampleteam.yourcodehere;

import com.qualcomm.robotcore.hardware.*;

import org.swerverobotics.library.*;
import org.swerverobotics.library.interfaces.*;

/**
 * Version 1.0 of Team Avalanche 6253's TeleOp program for Robot version 2.0.
 * Currently most distance and position values are arbitrary due to not having a complete robot we can test values on.
 */
@TeleOp(name = "TeleOpV1")
public class TeleOpV1 extends SynchronousOpMode {
    // Variables

    //Declare ModifiedBoolean methods. These methods decide whether an action is running or not.
    private ModifiedBoolean testRunning = new ModifiedBoolean(false);
    private ModifiedBoolean runningExtendTapeAuto = new ModifiedBoolean(false);
    private ModifiedBoolean runningCancelAllArm = new ModifiedBoolean(false);
    private ModifiedBoolean runningCancelAllTape = new ModifiedBoolean(false);
    private ModifiedBoolean runningCancelAllSlide = new ModifiedBoolean(false);


    // defaults to blue alliance
    private boolean isBlue = true;

    //tells whether triggers are in resting position or are down/active, starts at rest
    private boolean atRestTriggers = true;

    // methods with these variables need values
    private final double ARBITRARYDOUBLE = 0;
    private final int ARBITRARYINT = 0;
    private final boolean NEEDS_BUTTON_MAPPED = false;

    //Declare starting positions for tape, arm, and slide motors
    private int startPosTape;
    private int startPosArm;
    private int startPosSlide;

    private final int DISTANCE_TO_HANG = ARBITRARYINT;
    private final double NEEDS_THRESH = ARBITRARYDOUBLE;
    private final double RIGHT_ZIP_UP = ARBITRARYDOUBLE;
    private final double RIGHT_ZIP_DOWN = ARBITRARYDOUBLE;
    private final double LEFT_ZIP_UP = ARBITRARYDOUBLE;
    private final double LEFT_ZIP_DOWN = ARBITRARYDOUBLE;
    private final int TICKS_IN_INCH_AFT = ARBITRARYINT;
    private final int TICKS_IN_INCH_FORE = ARBITRARYINT;
    private final int TICKS_IN_INCH_TAPE = ARBITRARYINT;
    private final int TICKS_IN_DEGREE_ARM = ARBITRARYINT;
    private final int TICKS_IN_INCH_SLIDE = ARBITRARYINT;
    // Declare drive motors
    DcMotor motorLeftFore;
    DcMotor motorLeftAft;
    DcMotor motorRightFore;
    DcMotor motorRightAft;

    // Declare drawer slide motor and servos
    // motor extends/retracts slides
    // servoSlide(continuous) slides the deposit box laterally
    // servoBlockRelease(s) open flaps on the bottom of the bucket, releasing blocks/climbers
    DcMotor motorSlide;
    Servo servoSlide;
    Servo servoBlockReleaseLeft;
    Servo servoBlockReleaseRight;

    // Declare tape measure motor and servo
    // motor extends/retracts tape
    // servo(continuous) angles tape
    DcMotor motorTape;
    Servo servoTape;

    // Declare motor that spins the harvester
    DcMotor motorHarvest;

    // Declare motor that raises and lowers the collection arm
    DcMotor motorArm;

    // Declare zipline flipping servos
    Servo servoLeftZip;
    Servo servoRightZip;

    // Declare sensors
    GyroSensor gyro;

    @Override
    public void main() throws InterruptedException {
        hardwareMapping();

        waitForStart();

        // Go go gadget robot!
        while (opModeIsActive()) {
            if (updateGamepads()) {

                //AUTOMATIC CONTROLS//

                if (gamepad1.x) {
                    telemetry.addData("Button Works!", "Test");
                    telemetry.update();
                }

                if (NEEDS_BUTTON_MAPPED) {
                    initPositionSet();
                }

                if (gamepad1.b) {
                    testRunning.toggle();
                    if (testRunning.getValue()) {
                        motorSlide.setPower(0);
                    } else {
                        motorSlide.setTargetPosition(motorSlide.getCurrentPosition() + 1680 * 5);
                    }
                }



                //MANUAL CONTROLS//
                extendTapeManual(NEEDS_BUTTON_MAPPED);
                retractTapeManual(NEEDS_BUTTON_MAPPED);

                //starts and stops harvester
                if(NEEDS_BUTTON_MAPPED)
                    toggleHarvester(1);

                //toggles harvester spin direction
                if (NEEDS_BUTTON_MAPPED)
                    reverseHarvester();


                //Read Joystick Data and Update Speed of Left and Right Motors
                setLeftDrivePower(scaleInput(gamepad1.left_stick_y));
                setRightDrivePower(scaleInput(gamepad1.right_stick_y));


                if (NEEDS_BUTTON_MAPPED)
                    initPositionSet();

                if (NEEDS_BUTTON_MAPPED)
                    triggerZipline();

            }

            initPositionRun();

            //Run tape motor till it reaches hang distance
            runToPos(motorTape, ARBITRARYDOUBLE, runningExtendTapeAuto, null);
            if (testRunning.getValue()) {
                testContMotor();
            }
            idle();
        }
    }

    //Initialize and Map All Hardware
    private void hardwareMapping() throws InterruptedException {
        // Initialize drive motors
        motorLeftFore = hardwareMap.dcMotor.get("motorLeftFore");
        motorLeftAft = hardwareMap.dcMotor.get("motorLeftAft");
        motorRightFore = hardwareMap.dcMotor.get("motorRightFore");
        motorRightAft = hardwareMap.dcMotor.get("motorRightAft");

        //Left and right motors are on opposite sides and must spin opposite directions to go forward
        motorLeftAft.setDirection(DcMotor.Direction.REVERSE);
        motorLeftFore.setDirection(DcMotor.Direction.REVERSE);

        // Initialize drawer slide motor and servos
        motorSlide = hardwareMap.dcMotor.get("motorSlide");
        servoSlide = hardwareMap.servo.get("servoSlide");
        servoBlockReleaseLeft = hardwareMap.servo.get("servoBlockReleaseLeft");
        servoBlockReleaseRight = hardwareMap.servo.get("servoBlockReleaseRight");

        // Initialize tape measure motor and servo
        motorTape = hardwareMap.dcMotor.get("motorTape");
        servoTape = hardwareMap.servo.get("servoTape");

        // Initialize motor that spins the harvester
        motorHarvest = hardwareMap.dcMotor.get("motorHarvest");

        // Initialize motor that raises and lowers the collection arm
        motorArm = hardwareMap.dcMotor.get("motorArm");

        // Initialize zipline flipping servos
        servoLeftZip = hardwareMap.servo.get("servoLeftZip");
        servoRightZip = hardwareMap.servo.get("servoRightZip");

        // Initialize sensors
        gyro = hardwareMap.gyroSensor.get("gyro");

        gyro.calibrate();

        // Reset encoders
        this.motorLeftAft.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        this.motorLeftFore.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        this.motorTape.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        this.motorHarvest.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        this.motorArm.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        this.motorRightAft.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        this.motorRightFore.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        this.motorSlide.setMode(DcMotorController.RunMode.RESET_ENCODERS);

        //Set Runmode for all motors to run using encoders
        motorLeftFore.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        motorRightFore.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        motorLeftAft.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        motorRightAft.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        motorSlide.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        motorTape.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        motorArm.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        motorHarvest.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);

        //keep track of the starting positions of arm, slide, and tape motors
        startPosArm = motorArm.getCurrentPosition();
        startPosSlide = motorSlide.getCurrentPosition();
        startPosTape = motorSlide.getCurrentPosition();
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                                                                                            //
    //                                     main methods                                           //
    //                                                                                            //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void triggerZipline() {
        if (isBlue) {
            if (atRestTriggers)
                servoRightZip.setPosition(RIGHT_ZIP_UP); //Needs resting and active servo positions
            else
                servoRightZip.setPosition(RIGHT_ZIP_DOWN);
        } else {
            if (atRestTriggers)
                servoLeftZip.setPosition(RIGHT_ZIP_UP);
            else
                servoLeftZip.setPosition(RIGHT_ZIP_DOWN);
        }
        atRestTriggers = !atRestTriggers;
    }

/*    public void extendTapeAuto() throws InterruptedException {
        if (motorTape.isBusy())
            motorTape.setPower(0);
        else {
            //moveToPosTicks(DISTANCE_TO_HANG, motorTape, NEEDS_THRESH); //First arbitrary int is how many ticks we want the motor to extend, second arbitrary double is where we want the tape to start slowing down.
            while (motorTape.isBusy())
                this.idle();
        }
    }
  */

    //extends the tape measure until it reaches the correct position to hang from the regular hanging position.
    public void extendTapeAuto() {
        runToPos(motorTape, ARBITRARYDOUBLE, runningExtendTapeAuto, null);
    }

    //retracts the tape until it reaches just a little above the starting position at the time of initialization
    public void retractTapeAuto() throws InterruptedException {
        if (motorTape.isBusy())
            motorTape.setPower(0);
        else {
            // moveToPosTicks(motorTapeStartPos, motorTape, NEEDS_THRESH);
            while (motorTape.isBusy())
                this.idle();
        }
    }

    public void extendTapeManual(boolean b) {
        if (b)
            motorTape.setPower(1);
        else
            motorTape.setPower(0.0);
    }

    public void retractTapeManual(boolean b) {
        if (b)
            motorTape.setPower(-1);
        else
            motorTape.setPower(0.0);
    }

    public void toggleHarvester(double power) {
        if (motorHarvest.getPower() == 0.0)
            motorHarvest.setPower(power);
        else
            motorHarvest.setPower(0.0);
    }

    public void reverseHarvester() {
        motorHarvest.setDirection(DcMotor.Direction.REVERSE);
    }

    //return robot to initialization position
    public void initPositionSet() {
        //sets harvester and drive power to 0
        motorHarvest.setPower(0);
        setLeftDrivePower(0);
        setRightDrivePower(0);

        //sets the target position for arm, slide, and tape to the init positions
        setPosMotor(motorArm, runningCancelAllArm, 1, startPosArm);
        setPosMotor(motorTape, runningCancelAllTape, 1, startPosTape);
        setPosMotor(motorSlide, runningCancelAllSlide, 1, startPosSlide);
    }

    //runs until the motors hit the initialization position
    public void initPositionRun() {
        if (runningCancelAllArm.getValue() || runningCancelAllSlide.getValue() || runningCancelAllTape.getValue()) {
            runToPos(motorArm, 50, runningCancelAllArm, null);
            runToPos(motorArm, 50, runningCancelAllTape, null);
            runToPos(motorSlide, 50, runningCancelAllTape, null);
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                                                                                            //
    //                                     support methods                                        //
    //                                                                                            //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void setPosMotor(DcMotor motor, ModifiedBoolean isRunning, int ticksInInch, double distance) {
        isRunning.toggle();
        if (!isRunning.getValue()) {
            motor.setPower(0);
        } else
            motor.setTargetPosition((int) (motor.getCurrentPosition() + ticksInInch * distance));
    }


    public void runToPos(DcMotor motor, double minPower, ModifiedBoolean isRunning, ModifiedBoolean runNext) {
        if (isRunning.getValue()) {
            if (motor.getCurrentPosition() - 10 < motor.getTargetPosition() && motor.getCurrentPosition() < motor.getTargetPosition() + 10) {
                motor.setPower(0);
                isRunning.setFalse();
                if (runNext != null)
                    runNext.setTrue();
            } else
                setCurvedPower(motor, ARBITRARYDOUBLE, 1, minPower);
        }
    }

    public void setLeftDrivePower(double power) {
        motorLeftFore.setPower(power);
        motorLeftAft.setPower(power);
    }

    public void setRightDrivePower(double power) {
        motorRightFore.setPower(power);
        motorRightAft.setPower(power);
    }


    //QUADRATIC POWER SLOPE

    //thresh: distance from target at which motor slows
    /*public void setCurvedPower(DcMotor motor, double thresh, double inputPower, double minPower) {
        int target = motor.getTargetPosition();
        if(target - motor.getCurrentPosition() > thresh)
            motor.setPower(inputPower);
        else {
            double overThr = motor.getCurrentPosition() - target + thresh;
            double power = inputPower-overThr*overThr/thresh/thresh*(inputPower - minPower);
            motor.setPower(power);
        }
    }
    */

    public void setCurvedPower(DcMotor motor, double thresh, double inputPower, double minPower) {
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

    public void testContMotor() {
        if (motorSlide.getCurrentPosition() < motorSlide.getTargetPosition()) {
            setCurvedPower(motorSlide, 1000, 1, 0.3);
        } else {
            motorSlide.setPower(0);
            testRunning.setFalse();
        }
    }

    public void retractSlide(String height) { //Can be either top mid or bot

    }

    public void score(String height) {  // can be either top mid or bot

    }

    public void loadDispenser() {

    }


    //stop all motors
    public void cancelAll() {
        motorArm.setPower(0);
        motorHarvest.setPower(0);
        motorTape.setPower(0);
        motorSlide.setPower(0);
        setLeftDrivePower(0);
        setRightDrivePower(0);
    }


    public void dumpDispenser() {

    }

    public void extendSlide(String height) { //Can be either top mid or bot
        if (height.equals("top")) {

        }

        if (height.equals("mid")) {
        }

        if (height.equals("bot")) {

        }
    }

}
