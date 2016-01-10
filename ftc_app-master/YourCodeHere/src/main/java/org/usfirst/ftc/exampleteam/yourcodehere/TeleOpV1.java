package org.usfirst.ftc.exampleteam.yourcodehere;

import com.qualcomm.robotcore.hardware.*;
import org.swerverobotics.library.*;
import org.swerverobotics.library.interfaces.*;

/**
 * Version 1.0 of Team Avalanche 6253's TeleOp program for Robot version 2.0.
 * Currently most distance and position values are arbitrary due to not having a complete robot we can test values on.
 */
@TeleOp (name="TeleOpV1")
public class TeleOpV1 extends SynchronousOpMode
{
    // Variables

    private boolean testRunning = false; //test to see if cont method works

    // defaults to blue alliance
    private boolean isBlue = true;

    //tells whether triggers are in resting position or are down/active, starts at rest
    private boolean atRestTriggers = true;

    // methods with these variables need values
    private final double ARBITRARYDOUBLE = 0;
    private final int ARBITRARYINT = 0;

    //Starting Position of the Tape Measure
    private int motorTapeStartPos;
    private final int DISTANCE_TO_HANG = ARBITRARYINT;
    private final double NEEDS_THRESH = ARBITRARYDOUBLE;
    private final double RIGHT_ZIP_UP = ARBITRARYDOUBLE;
    private final double RIGHT_ZIP_DOWN = ARBITRARYDOUBLE;
    private final double LEFT_ZIP_UP = ARBITRARYDOUBLE;
    private final double LEFT_ZIP_DOWN = ARBITRARYDOUBLE;

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

    // Enable Booleans- basically so that you can tell if a button and released
    private boolean aEnable1 = true;
    private boolean bEnable1 = true;
    private boolean xEnable1 = true;
    private boolean yEnable1 = true;
    private boolean dUpEnable1 = true;
    private boolean dDownEnable1 = true;
    private boolean dLeftEnable1 = true;
    private boolean dRightEnable1 = true;
    private boolean leftBumperEnable1 = true;
    private boolean rightBumperEnable1 = true;
    private boolean leftStickButtonEnable1 = true;
    private boolean rightStickButtonEnable1 = true;
    private boolean startEnable1 = true;
    private boolean aEnable2 = true;
    private boolean bEnable2 = true;
    private boolean xEnable2 = true;
    private boolean yEnable2 = true;
    private boolean dUpEnable2 = true;
    private boolean dDownEnable2 = true;
    private boolean dLeftEnable2 = true;
    private boolean dRightEnable2 = true;
    private boolean leftBumperEnable2 = true;
    private boolean rightBumperEnable2 = true;
    private boolean leftTriggerEnable2 = true;
    private boolean rightTriggerEnable2 = true;
    private boolean leftStickButtonEnable2 = true;
    private boolean rightStickButtonEnable2 = true;
    private boolean leftStickXEnable2 = true;
    private boolean leftStickYEnable2 = true;
    private boolean rightStickXEnable2 = true;
    private boolean rightStickYEnable2 = true;
    private boolean startEnable2 = true;



    @Override
    public void main() throws InterruptedException
    {
        hardwareMapping();

        waitForStart();

        // Go go gadget robot!
        while (opModeIsActive())
        {
            if (updateGamepads()) {
                if (gamepad1.x && xEnable1) {
                    telemetry.addData("Button Works!", "Test");
                    telemetry.update();
                    xEnable1 = false;
                }

                if (gamepad1.b && bEnable1) {
                    testRunning = !testRunning;
                    if (testRunning == false)
                        motorSlide.setPower(0);
                    else {
                        motorSlide.setTargetPosition(motorSlide.getCurrentPosition() + 1680*2);
                        motorSlide.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
                    }
                    bEnable1 = false;
                }

                if (gamepad1.a && aEnable1)
                {
                    testContMotor2(1680*2);
                    bEnable1 = false;
                }


                //Read Joystick Data and Update Speed of Left and Right Motors
                setLeftDrivePower(scaleInput(gamepad1.left_stick_y));
                setRightDrivePower(gamepad1.right_stick_y);

            }

            ifHeld();

            telemetry.update();
            idle();

        }

        if (testRunning) {
            testContMotor();
        }
    }

    //Initialize and Map All Hardware
    private void hardwareMapping() throws InterruptedException {
        // Initialize drive motors
        motorLeftFore = hardwareMap.dcMotor.get("motorLeftFore");
        motorLeftAft = hardwareMap.dcMotor.get("motorLeftAft");
        motorRightFore = hardwareMap.dcMotor.get("motorRightFore");
        motorRightAft= hardwareMap.dcMotor.get("motorRightAft");

        //Left and right motors are on opposite sides and must spin opposite directions to go forward
        motorRightFore.setDirection(DcMotor.Direction.REVERSE);
        motorRightAft.setDirection(DcMotor.Direction.REVERSE);

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

        // Initialize tape tracking variable
        motorTapeStartPos = motorTape.getCurrentPosition();

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

    }

    private void ifHeld(){
        if (gamepad1.a && !aEnable1) {
            aEnable1 = true;
        }

        if (gamepad1.b && !bEnable1) {
            bEnable1 = true;
        }

        if (gamepad1.x && !xEnable1) {
            xEnable1 = true;
        }

        if (gamepad1.y && !yEnable1) {
            aEnable1 = true;
        }

        if (gamepad1.dpad_down && !dDownEnable1) {
            dDownEnable1 = true;
        }

        if (gamepad1.dpad_up && !dUpEnable1) {
            dUpEnable1 = true;
        }

        if (gamepad1.dpad_left && !dLeftEnable1) {
            dLeftEnable1 = true;
        }

        if (gamepad1.dpad_right && !dRightEnable1) {
            dRightEnable1 = true;
        }

        if (gamepad1.left_bumper && !leftBumperEnable1) {
            leftBumperEnable1 = true;
        }

        if (gamepad1.right_bumper && !rightBumperEnable1) {
            rightBumperEnable1 = true;
        }

        if (gamepad1.right_stick_button && !rightStickButtonEnable1) {
            rightStickButtonEnable1 = true;
        }

        if (gamepad1.left_stick_button && !leftStickButtonEnable1) {
            leftStickButtonEnable1 = true;
        }

        if (gamepad1.start && !startEnable1) {
            startEnable2 = true;
        }
        if (gamepad2.a && !aEnable1) {
            aEnable2 = true;
        }

        if (gamepad2.b && !bEnable1) {
            bEnable2 = true;
        }

        if (gamepad2.x && !xEnable1) {
            xEnable2 = true;
        }

        if (gamepad2.y && !yEnable1) {
            aEnable2 = true;
        }

        if (gamepad2.dpad_down && !dDownEnable1) {
            dDownEnable2 = true;
        }

        if (gamepad2.dpad_up && !dUpEnable1) {
            dUpEnable2 = true;
        }

        if (gamepad2.dpad_left && !dLeftEnable1) {
            dLeftEnable2 = true;
        }

        if (gamepad2.dpad_right && !dRightEnable1) {
            dRightEnable2 = true;
        }

        if (gamepad2.left_bumper && !leftBumperEnable1) {
            leftBumperEnable2 = true;
        }

        if (gamepad2.right_bumper && !rightBumperEnable1) {
            rightBumperEnable2 = true;
        }

        if (gamepad2.right_stick_button && !rightStickButtonEnable1) {
            rightStickButtonEnable2 = true;
        }

        if (gamepad2.left_stick_button && !leftStickButtonEnable1) {
            leftStickButtonEnable2 = true;
        }

        if (gamepad2.start && !startEnable1) {
            startEnable2 = true;
        }

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                                                                                            //
    //                                     main methods                                           //
    //                                                                                            //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void triggerZipline() {
        if (isBlue){
            if (atRestTriggers)
                servoRightZip.setPosition(RIGHT_ZIP_UP); //Needs resting and active servo positions
            else
                servoRightZip.setPosition(RIGHT_ZIP_DOWN);
        }
        else{
            if (atRestTriggers)
                servoLeftZip.setPosition(RIGHT_ZIP_UP);
            else
                servoLeftZip.setPosition(RIGHT_ZIP_DOWN);
        }
        atRestTriggers = !atRestTriggers;
    }

    public void extendTapeAuto() throws InterruptedException {
        if (motorTape.isBusy())
            motorTape.setPower(0);
        else {
            //moveToPosTicks(DISTANCE_TO_HANG, motorTape, NEEDS_THRESH); //First arbitrary int is how many ticks we want the motor to extend, second arbitrary double is where we want the tape to start slowing down.
            while (motorTape.isBusy())
                this.idle();
        }
    }

    public void retractTapeAuto() throws InterruptedException {
        if (motorTape.isBusy())
            motorTape.setPower(0);
        else {
           // moveToPosTicks(motorTapeStartPos, motorTape, NEEDS_THRESH);
            while (motorTape.isBusy())
                this.idle();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                                                                                            //
    //                                     support methods                                        //
    //                                                                                            //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void setLeftDrivePower(double power) {
        motorLeftFore.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        motorLeftAft.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        motorLeftFore.setPower(power);
        motorLeftAft.setPower(power);
    }

    public void setRightDrivePower(double power){
        motorRightAft.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        motorRightFore.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        motorRightFore.setPower(power);
        motorRightAft.setPower(power);
    }


    //thresh: distance from target at which motor slows

    /* THIS METHOD DOES NOT WORK - Cannot run other operations while this is running due to entering while loop and not exiting
    private void moveToPosTicks(int ticks, DcMotor motor, double thresh) {

        motor.setTargetPosition(motor.getCurrentPosition() + ticks);

        motor.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        if (motor.getCurrentPosition() > motor.getTargetPosition())
            while (motor.getCurrentPosition() > motor.getTargetPosition())
                setCurvedPower(motor, thresh, 100, 60); // ^ THESE ARE ARBITRARY NUMBERS THAT WE WILL REFINE THROUGH TESTING. They Are supposed to be the range in which the motors can stop giving power
        else
            while (motor.getCurrentPosition() < motor.getTargetPosition())
                setCurvedPower(motor, thresh, 100, 60);
        motor.setPower(0);
    }
    */

    //thresh: distance from target at which motor slows
    public void setCurvedPower(DcMotor motor, double thresh, double inputPower, double minPower) {
        int target = motor.getTargetPosition();
        if(target - motor.getCurrentPosition() > thresh)
            motor.setPower(inputPower);
        else {
            double overThr = motor.getCurrentPosition() - target + thresh;
            double power = inputPower-overThr*overThr/thresh/thresh*(inputPower - minPower);
            motor.setPower(power);
        }
    }

    //Default Scale Input Method created by FTC- We will use this one until someone creates a better one.
    //Used for scaling joysticks, basically is a floor function that is squared
    double scaleInput(double dVal)  {
        double[] scaleArray = { 0.0, 0.05, 0.09, 0.10, 0.12, 0.15, 0.18, 0.24,
                0.30, 0.36, 0.43, 0.50, 0.60, 0.72, 0.85, 1.00, 1.00 };

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

    public void testContMotor(){
        if (motorSlide.getCurrentPosition() < motorSlide.getTargetPosition()) {
            setCurvedPower(motorSlide, 200, 100, 60);
        }
        else {
            motorSlide.setPower(0);
            testRunning = false;
        }
    }

    public void testContMotor2(int ticks){
        motorSlide.setTargetPosition(ticks + motorSlide.getCurrentPosition());
        motorSlide.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
    }

    public void retractSlide(String height){ //Can be either top mid or bot

    }

    public void score(String height){  // can be either top mid or bot

    }

    public void toggleHarvester(double power){

    }

    public void loadDispenser(){

    }

    public void cancelAll(){

    }

    public void dumpDispenser(){

    }

    public void extendSlide(String height){ //Can be either top mid or bot

    }
}
