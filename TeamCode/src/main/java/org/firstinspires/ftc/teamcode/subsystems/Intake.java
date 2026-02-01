package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Intake {

    private DcMotorEx frontIntake;
    private DcMotorEx backIntake;


    public void init(HardwareMap hwMap){

        frontIntake = hwMap.get(DcMotorEx.class, "frontIntake");
        frontIntake.setDirection(DcMotorSimple.Direction.REVERSE);
        frontIntake.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backIntake = hwMap.get(DcMotorEx.class, "backIntake");
        backIntake.setDirection(DcMotorSimple.Direction.FORWARD);
        backIntake.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);


//        frontIntake.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(
//                10, 0, 0, 0
//        )); //change to runFront without encoder?
    }


    public void runFront(){
        frontIntake.setPower(-1);
    }
    public void runFrontReverse(){
        frontIntake.setPower(1);
    }
    public void stopFront(){
        frontIntake.setPower(0);
    }
    public void runBack(){
        backIntake.setPower(-1);
    }
    public void runBackReverse(){
        backIntake.setPower(1);
    }
    public void stopBack(){
        backIntake.setPower(0);
    }

}
