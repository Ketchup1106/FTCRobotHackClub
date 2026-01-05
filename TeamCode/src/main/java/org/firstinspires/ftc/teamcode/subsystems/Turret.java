package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

public class Turret {
    public DcMotorEx turret;



    double gearRatio = 103.0/26;

    public double ticksPerDegree = (537 * gearRatio)/360;
    public double ticksPerRadian = (537.6 * gearRatio) /(2 * Math.PI);
    double tickLimit = 827; //manually tune
    double subtractionAmount = 0;
    public boolean isHomed = false;
    public int turnNeeded;
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
    public void rotateToGoal(double goalAngle, double robotAngle){
        //robotAngle = condenseAngle(Math.toDegrees(robotAngle));
        turnNeeded = (int)(Math.abs(goalAngle*ticksPerRadian - robotAngle*ticksPerRadian));

//        if((turnNeeded > tickLimit) || (turnNeeded < 0)) {
//            return;
//        }
        if(robotAngle < (goalAngle - Math.toRadians(10)) || robotAngle > (goalAngle + Math.toRadians(180))){
            return;
        }
        turret.setTargetPosition(turnNeeded);
        turret.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        turret.setPower(1);
    }

//    public void rotateToGoalAnish(double goalAngle, double robotAngle){
//        double TurretAngleOnBot = Math.toRadians(-10) + (getCurrentPos()/ticksPerRadian);
//        double turretHeading = robotAngle - TurretAngleOnBot;
//        turnNeeded = (int)(ticksPerRadian*(goalAngle - turretHeading));
//        if((turnNeeded > tickLimit) || (turnNeeded < 0)) {
//            return;
//        }
//        turret.setTargetPosition(turnNeeded);
//        turret.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//        turret.setPower(0.5);
//    }

    public void rotateToGoalKetchup(double goalAngle, double robotAngle){
        turnNeeded = (int)((Math.toRadians(100) + (robotAngle - (goalAngle + Math.toRadians(90)))) * ticksPerRadian);
//        if((turnNeeded > tickLimit) || (turnNeeded < 0)) {
//            return;
//        }
        if(robotAngle < (goalAngle - Math.toRadians(10)) || robotAngle > (goalAngle + Math.toRadians(180))){
            return;
        }
        turret.setTargetPosition(turnNeeded);
        turret.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        turret.setPower(1);
    }


    public double getCurrentPos(){
        return turret.getCurrentPosition() + subtractionAmount;
    }
    public double getPosWithoutSubtractionFactor(){
        return turret.getCurrentPosition()/ticksPerRadian;
    }

    public void stop(){
        turret.setPower(0);
    }

    public void rotate(){
        turret.setTargetPosition((int)(turret.getCurrentPosition() + (2 * ticksPerDegree)));
        if(turret.getCurrentPosition() + (2 * ticksPerDegree) > tickLimit){
            return;
        }
        turret.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        turret.setPower(0.5);
    }
    public void rotateReverse(){
        turret.setTargetPosition((int)(turret.getCurrentPosition() - (2 * ticksPerDegree)));
        if(turret.getCurrentPosition() - (2 * ticksPerDegree) <= 0){
            return;
        }
        turret.setTargetPosition((int)(turret.getCurrentPosition() - (2 * ticksPerDegree)));
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
            turret.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        }

    }
    public double condenseAngle(double angle){
        int divisionFactor = (int)angle/360;
        angle -= 360*divisionFactor;

        if(angle > 180){
            angle = -1*(angle-180);
        }else if(angle < -180){
            angle = -1*(angle+180);
        }
        return Math.toRadians(angle);
    }
//    In TeleOp have a condition:
//    if(turret.getCurrentPosition() == turret.getTargetPosition()){
//        turret.setPower(0);
//    }



}
