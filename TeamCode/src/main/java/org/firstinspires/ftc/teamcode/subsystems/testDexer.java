package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.Objects;

public class testDexer {
    public Servo s1;
    public Servo s2;
    Telemetry telemetry;
    public TouchySensor frontTouchy = new TouchySensor();
    public TouchySensor rearTouchy = new TouchySensor();
    Color colorSensorFront = new Color();
    Color colorSensorBack = new Color();



    final double gearRatio = 5.0/3;
    double frontPos = 0;
    double frontSecondIntakePos = (1.0/3)/gearRatio;
    double frontThirdIntakePos = 736478236; //random number
    double backPos = .5/gearRatio;
    double backSecondIntakePos = 56565545; //randopm number
    double backThirdIntakePos = 6767676; //random number

    String pos1;
    String pos2;
    String pos3;
    String currentOrder = "UUU";
    public void init(HardwareMap hwMap){
        s1 = hwMap.get(Servo.class, "spin1");
        s2 = hwMap.get(Servo.class, "spin2");

        s1.setDirection(Servo.Direction.FORWARD);
        s2.setDirection(Servo.Direction.REVERSE);

        frontTouchy.init(hwMap);
        rearTouchy.init(hwMap);

        colorSensorFront.init(hwMap);
        colorSensorBack.init(hwMap);

        s1.setPosition(frontPos);
        s2.setPosition(frontPos); //0 is front intake
    }
    //step1: detect intake through touch senspr
    public boolean isFrontBeingUsed(){
        return !frontTouchy.detectTouch();
    }
    public boolean isBackBeingUsed(){
        return !rearTouchy.detectTouch();
    }
    //step2: spin to desired intake
    public void spinToFront(){
        if(isFrontBeingUsed()){
            if (checkForColorAtSpot("U", 1)){
                s1.setPosition(frontPos);
                s2.setPosition(frontPos);
            }else if (checkForColorAtSpot("U", 2)){
                s1.setPosition(frontSecondIntakePos);
                s2.setPosition(frontSecondIntakePos);
            }else if(checkForColorAtSpot("U", 3)){
                s1.setPosition(frontThirdIntakePos);
                s2.setPosition(frontThirdIntakePos);
            }
        }
    }
    public void spinToBack(){
        if(isBackBeingUsed()){
            if (checkForColorAtSpot("U", 1)){
                s1.setPosition(backPos);
                s2.setPosition(backPos);
            }else if (checkForColorAtSpot("U", 2)){
                s1.setPosition(backSecondIntakePos);
                s2.setPosition(backSecondIntakePos);
            }else if(checkForColorAtSpot("U", 3)){
                s1.setPosition(backThirdIntakePos);
                s2.setPosition(backThirdIntakePos);
            }
        }
    }
    //helper methods
    public void assignColorToPosition(){
        if(s1.getPosition() == frontPos){
            //if idli dish 1 is facing front intake then check for the color and assign it to pos1
            if(colorSensorFront.getDetectedColor(telemetry) == Color.detectedColor.PURPLE){
                pos1 = "P";
            }
            else if(colorSensorFront.getDetectedColor(telemetry) == Color.detectedColor.GREEN){
                pos1 = "G";
            }
            else if(colorSensorFront.getDetectedColor(telemetry) == Color.detectedColor.UNKNOWN){
                pos1 = "U";
            }
        }
        if(s1.getPosition() == backPos){
            //if idli dish 1 is facing back intake then check for the color and assign it to pos1
            if(colorSensorBack.getDetectedColor(telemetry) == Color.detectedColor.PURPLE){
                pos1 = "P";
            }
            else if(colorSensorBack.getDetectedColor(telemetry) == Color.detectedColor.GREEN){
                pos1 = "G";
            }
            else if(colorSensorBack.getDetectedColor(telemetry) == Color.detectedColor.UNKNOWN){
                pos1 = "U";
            }
        }
        if(s1.getPosition() == frontSecondIntakePos){
            //if idli dish 2 is facing front intake then check for the color and assign it to pos2
            if(colorSensorFront.getDetectedColor(telemetry) == Color.detectedColor.PURPLE){
                pos2 = "P";
            }
            else if(colorSensorFront.getDetectedColor(telemetry) == Color.detectedColor.GREEN){
                pos2 = "G";
            }
            else if(colorSensorFront.getDetectedColor(telemetry) == Color.detectedColor.UNKNOWN){
                pos2 = "U";
            }
        }
        if(s1.getPosition() == backSecondIntakePos){
            //if idli dish 2 is facing back intake then check for the color and assign it to pos2
            if(colorSensorBack.getDetectedColor(telemetry) == Color.detectedColor.PURPLE){
                pos2 = "P";
            }
            else if(colorSensorBack.getDetectedColor(telemetry) == Color.detectedColor.GREEN){
                pos2 = "G";
            }
            else if(colorSensorBack.getDetectedColor(telemetry) == Color.detectedColor.UNKNOWN){
                pos2 = "U";
            }
        }
        if(s1.getPosition() == frontThirdIntakePos){
            //if idli dish 3 is facing front intake then check for the color and assign it to pos3
            if(colorSensorFront.getDetectedColor(telemetry) == Color.detectedColor.PURPLE){
                pos3 = "P";
            }
            else if(colorSensorFront.getDetectedColor(telemetry) == Color.detectedColor.GREEN){
                pos3 = "G";
            }
            else if(colorSensorFront.getDetectedColor(telemetry) == Color.detectedColor.UNKNOWN){
                pos3 = "U";
            }
        }
        if(s1.getPosition() == backSecondIntakePos){
            //if idli dish 3 is facing back intake then check for the color and assign it to pos3
            if(colorSensorBack.getDetectedColor(telemetry) == Color.detectedColor.PURPLE){
                pos3 = "P";
            }
            else if(colorSensorBack.getDetectedColor(telemetry) == Color.detectedColor.GREEN){
                pos3 = "G";
            }
            else if(colorSensorBack.getDetectedColor(telemetry) == Color.detectedColor.UNKNOWN){
                pos3 = "U";
            }
        }
        currentOrder = pos1 + pos2 + pos3;
    }
    public boolean checkForColorAtSpot(String ballColor, int spot){
        if(currentOrder.indexOf(ballColor) == spot-1){
            return true;
        }
        return false;
    }
}
