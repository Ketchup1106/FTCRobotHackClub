package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

public class Turret {
    public DcMotorEx turret;



    double gearRatio = 26/103.0;

    public double ticksPerDegree = (384.5 / gearRatio)/360;
    public double ticksPerRadian = (384.5 / gearRatio) /(2 * Math.PI);
    double tickLimit = 827; //manually tune
    double subtractionAmount = 0;
    public boolean isHomed = true;
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
        turret.setVelocityPIDFCoefficients(10, .25, 1, 0);
    }
    public int calculateTurn(double goalAngle, double robotAngle) {
        robotAngle = fixNegativeHeading(Math.toDegrees(robotAngle));
        turnNeeded = (int) (Math.abs(goalAngle * ticksPerRadian - robotAngle * ticksPerRadian));
        if(robotAngle < (goalAngle - Math.toRadians(10)) || robotAngle > (goalAngle + Math.toRadians(180))){
            return 0;
        }
        return turnNeeded;
    }
    public void rotateToGoal(int turn){


//        if((turnNeeded > tickLimit) || (turnNeeded < 0)) {
//            return;
//        }
        turret.setTargetPosition(turn);
        turret.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        turret.setPower(1);
    }
    public boolean getTurretStatus(){
        return turret.isBusy();
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
        double roboAngle = fixNegativeHeading(robotAngle);
        turnNeeded = (int)((Math.toRadians(100) + (roboAngle - (goalAngle + Math.toRadians(90)))) * ticksPerRadian);
//        if((turnNeeded > tickLimit) || (turnNeeded < 0)) {
//            return;
//        }
        if(roboAngle < (goalAngle - Math.toRadians(10)) || roboAngle > (goalAngle + Math.toRadians(180))){
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

    public void rotate(double t){
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
            turret.setTargetPosition((int)(-11*ticksPerDegree));
            turret.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            turret.setPower(-0.5);

        }else{
            turret.setTargetPosition((int)(getCurrentPos()));
            turret.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            turret.setPower(0);
            turret.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        }

    }
    public double fixNegativeHeading(double angle){
        if(angle < 0){
            angle = (180 - Math.abs(angle)) + 180;
        }
//        if(angle > 180){
//            angle = -1*(angle-180);
//        }else if(angle < -180){
//            angle = -1*(angle+180);
//        }
        return Math.toRadians(angle);
    }
//    In TeleOp have a condition:
//    if(turret.getCurrentPosition() == turret.getTargetPosition()){
//        turret.setPower(0);
//    }



}
