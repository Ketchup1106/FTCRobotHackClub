package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

public class Turret {
    public DcMotorEx turret;



    double gearRatio = 4/1;

    double ticksPerRadian = (537.6 * gearRatio) /(2 * Math.PI);
    double tickLimit = 827; //manually tune
    double subtractionAmount = 0;
    public boolean isHomed = false;
    int turnNeeded;
    TouchySensor turretLimitSwitch = new TouchySensor();

    public void init(HardwareMap hwMap){
        turret = hwMap.get(DcMotorEx.class, "tm");
        turretLimitSwitch.init(hwMap);
        turret.setDirection(DcMotorSimple.Direction.REVERSE);
        turret.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        turret.setTargetPosition(0);
        turret.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
        turret.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);;
        turret.setPIDFCoefficients(DcMotorEx.RunMode.RUN_TO_POSITION, new PIDFCoefficients(10, 0, 0, 0));
    }
    public void rotateToGoal(double goalAngle){
        turnNeeded = (int)(goalAngle*ticksPerRadian);
        if(turnNeeded > tickLimit - getCurrentPos()) {
            return;
        }
        turret.setTargetPosition((int)(turnNeeded + getCurrentPos()));
        turret.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        turret.setPower(0.5);
    }

    public double getCurrentPos(){
        return turret.getCurrentPosition() - subtractionAmount;
    }
    public double getPosWithoutSubtractionFactor(){
        return turret.getCurrentPosition();
    }

    public void stop(){
        turret.setPower(0);
    }

    public void rotate(){
        turret.setTargetPosition((int)(turret.getCurrentPosition() + (ticksPerRadian * gearRatio * (Math.PI/4))));
        turret.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        turret.setPower(0.5);
    }
    public void home(){
        if(turretLimitSwitch.detectTouch()){
            isHomed = true;
        }
        if(!isHomed){
            turret.setPower(-.2);
        }else{
            turret.setPower(0);
            subtractionAmount = turret.getCurrentPosition();
        }

    }
//    In TeleOp have a condition:
//    if(turret.getCurrentPosition() == turret.getTargetPosition()){
//        turret.setPower(0);
//    }



}
