package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

public class Turret {
    public DcMotorEx turret;

    double ticksPerRadian = 537.6/(2 * Math.PI);

    double gearRatio = 4/1;

    public void init(HardwareMap hwMap){
        turret = hwMap.get(DcMotorEx.class, "tm");
        turret.setDirection(DcMotorSimple.Direction.FORWARD);
        turret.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        turret.setTargetPosition(0);
        turret.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
        turret.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);;
        turret.setPIDFCoefficients(DcMotorEx.RunMode.RUN_TO_POSITION, new PIDFCoefficients(10, 0, 0, 0));
    }
    public void rotateToGoal(double goalAngle){
        turret.setTargetPosition((int)(goalAngle * ticksPerRadian * gearRatio));
        turret.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        turret.setPower(0.5);
    }

    public double getCurrentPosition(){
        return turret.getCurrentPosition()/ticksPerRadian;
    }

    public void stop(){
        turret.setPower(0);
    }

    public void rotate(){
        turret.setTargetPosition((int)(turret.getCurrentPosition() + (ticksPerRadian * gearRatio * (Math.PI/4))));
        turret.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        turret.setPower(0.5);
    }

//    In TeleOp have a condition:
//    if(turret.getCurrentPosition() == turret.getTargetPosition()){
//        turret.setPower(0);
//    }



}
