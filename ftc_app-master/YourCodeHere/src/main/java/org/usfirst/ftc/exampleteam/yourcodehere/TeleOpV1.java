package org.usfirst.ftc.exampleteam.yourcodehere;

import com.qualcomm.robotcore.hardware.*;
import org.swerverobotics.library.*;
import org.swerverobotics.library.interfaces.*;

/**
 * Version 1.0 of Team Avalanche 6253's TeleOp program for Robot version 2.0.
 * Currently most distance and position values are arbitrary due to not having a complete robot we can test values on.
 */
@TeleOp(name="TeleOp V1")
public class TeleOpV1 extends SynchronousOpMode
{
    // Variables
    // defaults to blue alliance
    private boolean isBlue = true;
    //tells whether triggers are in resting position or are down/active, starts at rest
    private boolean atRestTriggers = true;
    // methods with these variables need values
    private final double ARBITRARYDOUBLE = 0;
    private final float ARBITRARYFLOAT = 0;
    private final boolean ARBITRARYBOOLEAN = false;
    public static final double TICKS_PER_INCH = 133.7;

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
    public void main() throws InterruptedException
    {
        hardwareMapping();

        while (!isStarted()){
            if(updateGamepads()){
                //////////////////////////////////////////////// Selects alliance
                if(gamepad1.x) {                              // x button for blue (button color is blue)
                    isBlue = true;                            //
                    telemetry.addData("alliance", "blue");    //
                }                                             //
                if(gamepad1.b) {                              // b button for red (button color is red)
                    isBlue = false;                           //
                    telemetry.addData("alliance", "red");     //
                }                                             //
                telemetry.update();                           //
                ////////////////////////////////////////////////
            }
        }

        // Go go gadget robot!
        while (opModeIsActive())
        {
            if (updateGamepads()) {
                if (gamepad1.a) {
                    telemetry.addData("Button Works!", "Test");
                    telemetry.update();
                }

                if (gamepad1.left_stick_y) {

                }

                }
            }

            telemetry.update();
            idle();
        }
    }

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

        motorArm.setDirection(DcMotor.Direction.REVERSE);

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

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                                                                                            //
    //                                     support methods                                        //
    //                                                                                            //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void triggerZipline() {
        if (isBlue){
            if (atRestTriggers)
                servoRightZip.setPosition(ARBITRARYDOUBLE);
                servoRightZip.setPosition(ARBITRARYDOUBLE);
        }
        else{
            if (atRestTriggers)
                servoLeftZip.setPosition(ARBITRARYDOUBLE); //ARBITRARY NUMBER, NEEDS VALUE
            else
                servoLeftZip.setPosition(ARBITRARYDOUBLE);
        }
        atRestTriggers = !atRestTriggers;
    }

    public void setLeftDrivePower(double power){
        motorLeftFore.setPower(power);
        motorLeftAft.setPower(power);
    }

    public void setRightDrivePower(double power){
        motorRightFore.setPower(power);
        motorRightAft.setPower(power);
    }
//FINISH
    public void extendTapeAuto() {
        if (motorTape.isBusy()){
            motorTape.setTargetPosition(0);
        }
        else
            motorTape.setPower(1);
    }

    public void moveSlide(int distance) throws InterruptedException{
        int ticks = distance * 1120; //1120 ticks in one motor rotation
        motorSlide.setTargetPosition(motorSlide.getCurrentPosition() + ticks);

        motorSlide.setPower(ARBITRARYDOUBLE);

        motorSlide.setMode(DcMotorController.RunMode.RUN_TO_POSITION);

        while (motorSlide.isBusy())
            this.idle();

        motorSlide.setPower(0);

    }

    private void moveMotorTicks(int ticks, int power, DcMotor motor) throws InterruptedException{

        motor.setTargetPosition(motor.getCurrentPosition() + ticks);

        motor.setPower(((double) power) / 100);    // USE POWER CURVE ONCE CODED

        motor.setMode(DcMotorController.RunMode.RUN_TO_POSITION);

        while (motor.isBusy())
            this.idle();

        motor.setPower(0);
    }

    public double powerCurve(double imputPower, boolean positive) {
        return ARBITRARYDOUBLE; //NEED TO COME UP WITH FORMULA FOR POWER CURVE
    }

}
