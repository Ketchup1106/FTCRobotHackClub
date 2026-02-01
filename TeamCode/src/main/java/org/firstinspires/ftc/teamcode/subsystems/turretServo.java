package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;

public class turretServo {
    public Servo turret;


    double gearRatio = 26/103.0;

    public double posPerDegree = 1.0/360;
    public double posPerRadian = 1/(2 * Math.PI);
    double posLimit = 0.6; //manually tune
    public boolean isHomed = true;
    public double turnNeeded;

    public void init(HardwareMap hwMap){
        turret = hwMap.get(Servo.class, "turret");
        turret.setDirection(Servo.Direction.REVERSE);
        turret.scaleRange(0, .45);
        turret.setPosition(0);
    }
    public double calculateTurnBlue(double goalAngle, double robotAngle) { //input in rads
        robotAngle = fixNegativeHeading(Math.toDegrees(robotAngle)); //returns in rads
        turnNeeded = (Math.abs(goalAngle * posPerRadian - robotAngle * posPerRadian));
        if(robotAngle < (goalAngle - Math.toRadians(10)) || robotAngle > (goalAngle + Math.toRadians(180))){
            return 0;
        }
        return turnNeeded;
    }
    public double calculateTurnRed(double goalAngle, double robotAngle) {
        robotAngle = fixNegativeHeading(Math.toDegrees(robotAngle));
        turnNeeded = (robotAngle * posPerRadian - goalAngle * posPerRadian);
        if(turnNeeded > posLimit || turnNeeded < 0){
            return 0;
        }
        return turnNeeded;
    }
    public void rotateToGoal(int turn){
        turret.setPosition(turn);
    }


    public double getCurrentPos(){
        return turret.getPosition();
    }
    public double getPosRadians(){
        return turret.getPosition()/posPerRadian;
    }

    public double getPosDegrees(){
        return turret.getPosition()/posPerDegree;
    }

    public void rotate(){
        if(turret.getPosition() + (2 * posPerDegree) > posLimit){
            return;
        }
        turret.setPosition((int)(turret.getPosition() + (2 * posPerDegree)));
    }
    public void rotateReverse(){
        if(turret.getPosition() + (-2 * posPerDegree) < 0){
            return;
        }
        turret.setPosition((int)(turret.getPosition() + (-2 * posPerDegree)));
    }
    public double fixNegativeHeading(double angle){
        if(angle < 0){
            angle = (180 - Math.abs(angle)) + 180;
        }
        return Math.toRadians(angle);
    }
}
