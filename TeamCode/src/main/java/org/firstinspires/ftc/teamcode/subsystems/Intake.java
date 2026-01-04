package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

public class Intake {

    private DcMotorEx intake;


    public void init(HardwareMap hwMap){

        intake = hwMap.get(DcMotorEx.class, "intake");


        intake.setDirection(DcMotorSimple.Direction.REVERSE);
        intake.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);


//        intake.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(
//                10, 0, 0, 0
//        )); //change to run without encoder?
    }



    public void run(){
        intake.setPower(1);
    }
    public void runReverse(){
        intake.setPower(-1);
    }
    public void stop(){
        intake.setPower(0);
    }
    public int getPos() {
        return intake.getCurrentPosition();
    }

}
