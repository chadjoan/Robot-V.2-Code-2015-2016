package org.usfirst.ftc.exampleteam.yourcodehere.android;

import org.avalanche.sal.*;

import org.usfirst.ftc.exampleteam.yourcodehere.*;


public class DefineImplementation<X,Y> extends X implements Y {}

public class QualcommDcMotor     extends DefineImplementation<DcMotor,IDcMotor> {}
public class QualcommServo       extends DefineImplementation<Servo,IServo> {}
public class QualcommGyroSensor  extends DefineImplementation<GyroSensor,IGyroSensor> {}
public class QualcommColorSensor extends DefineImplementation<ColorSensor,IColorSensor> {}
public class QualcommGamepad     extends DefineImplementation<Gamepad,IGamepad> {}
public class SwerveHardwareMap   extends DefineImplementation<HardwareMap,IHardwareMap<QualcommDcMotor>> {}
public class SwerveSynchronousOpMode extends
	DefineImplementation<SynchronousOpMode,
		ISynchronousOpMode<
			HardwareMap,
			Gamepad
		>
	>
{}


public class HardwareMapProxy<T> extends T implements IHardwareMap

@TeleOp(name = "TeleOpCommon")
public class TeleOpCommon extends SynchronousOpMode {
    @Override
    public void main() throws InterruptedException {
		
    }
}