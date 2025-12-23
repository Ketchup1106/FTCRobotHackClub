package org.firstinspires.ftc.teamcode.subsystems;

import java.util.*;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Spindexer {

//    public Servo s1;
//    public Servo s2;
//
//    public TouchySensor frontTouchy = new TouchySensor();
//    public TouchySensor rearTouchy = new TouchySensor();
//    Color colorSensorFront = new Color();
//    Color colorSensorBack = new Color();
//    ArrayList<Color.detectedColor> idli = new ArrayList<>(3);
//    ArrayList<String> circles = new ArrayList<>(3);
//
//    double shoot1 = 0;
//    double shoot2 = 0;
//    double shoot3 = 0;
//    double FrontIntake1 = 0;
//    double FrontIntake2 = 0;
//    double FrontIntake3 = 0;
//    double BackIntake1 = 0;
//    double BackIntake2 = 0;
//    double BackIntake3 = 0;
//    Double[] positions = {shoot1, FrontIntake1, BackIntake1, shoot2, FrontIntake2, BackIntake2, shoot3, FrontIntake3, BackIntake3};
//    String currOrder = "UUU";
//    boolean isFull = false;
//
//    public void init(HardwareMap hwMap){
//        s1 = hwMap.get(Servo.class, "spin1");
//        s2 = hwMap.get(Servo.class, "spin2");
//
//        s1.setDirection(Servo.Direction.FORWARD);
//        s2.setDirection(Servo.Direction.REVERSE);
//
//        frontTouchy.init(hwMap);
//        rearTouchy.init(hwMap);
//
//        colorSensorFront.init(hwMap);
//        colorSensorBack.init(hwMap);
//
//        s1.setPosition(0);
//        s2.setPosition(0);
//    }
//
//    public void updateCurrOrder(){
//        for(int i = 0; i < idli.size(); i++){
//            if(idli.get(i) == Color.detectedColor.GREEN){
//                circles.set(i, "GREEN");
//            }
//            if(idli.get(i) == Color.detectedColor.PURPLE){
//                circles.set(i, "PURPLE");
//            }
//            if(idli.get(i) == Color.detectedColor.UNKNOWN){
//                circles.set(i, "UNKNOWN");
//            }
//        }
//        currOrder = circles.get(0).substring(0,1) + circles.get(1).substring(0,1) + circles.get(2).substring(0,1);
//    }
//    public void update(){
//        if(s1.getPosition() == positions[1]){
//           idli.set(0, colorSensorFront.getDetectedColor());
//        }
//        if(s1.getPosition() == positions[2]){
//            idli.set(0, colorSensorBack.getDetectedColor());
//        }
//        if(s1.getPosition() == positions[4]){
//            idli.set(1, colorSensorFront.getDetectedColor());
//        }
//        if(s1.getPosition() == positions[5]){
//            idli.set(1, colorSensorBack.getDetectedColor());
//        }
//        if(s1.getPosition() == positions[7]){
//            idli.set(2, colorSensorFront.getDetectedColor());
//        }
//        if(s1.getPosition() == positions[8]){
//            idli.set(2, colorSensorBack.getDetectedColor());
//        }
//    }
//
//
//    public void rotateToShoot(String order){
//        if(currOrder.equals("PGP")){
//            if(order.equals("PGP")){
//                return;
//            }
//            if(order.equals("GPP")){
//                s1.setPosition(positions[6]); //change if needed
//                s2.setPosition(positions[6]); //change if needed
//            }
//            if(order.equals("PPG")){
//                s1.setPosition(positions[3]); //change if needed
//                s2.setPosition(positions[3]); //change if needed
//            }
//        }
//        else if(currOrder.equals("PPG")){
//            if(order.equals("PGP")){
//                s1.setPosition(positions[6]); //change if needed
//                s2.setPosition(positions[6]); //change if needed
//            }
//            if(order.equals("GPP")){
//                s1.setPosition(positions[3]); //change if needed
//                s2.setPosition(positions[3]); //change if needed
//            }
//            if(order.equals("PPG")){
//                return;
//            }
//        }
//        else if(currOrder.equals("GPP")){
//            if(order.equals("PGP")){
//                s1.setPosition(positions[6]); //change if needed
//                s2.setPosition(positions[6]); //change if needed
//            }
//            if(order.equals("GPP")){
//                return;
//            }
//            if(order.equals("PPG")){
//                s1.setPosition(positions[3]); //change if needed
//                s2.setPosition(positions[3]); //change if needed
//            }
//        }
//        else{
//            s1.setPosition(positions[0]); //change if needed
//            s2.setPosition(positions[0]);
//        }
//    }
//    public void shoot(){
//        s1.setPosition(1); //change if needed
//        s2.setPosition(-1); //change if needed
//    }
//
//
//    public boolean frontTouchyActive(){
//        return frontTouchy.detectTouch();
//    }
//    public boolean rearTouchyActive(){
//        return rearTouchy.detectTouch();
//    }
//
//    public void rotateToFront(){
//        if (currOrder.contains("U")){
//            if(currOrder.indexOf("U") == 0){
//                s1.setPosition(positions[1]);
//                s2.setPosition(positions[1]);
//            }else if(currOrder.indexOf("U") == 1){
//                s1.setPosition(positions[4]);
//                s2.setPosition(positions[4]);
//            }else if(currOrder.indexOf("U") == 2){
//                s1.setPosition(positions[7]);
//                s2.setPosition(positions[7]);
//            }else{
//                isFull = true;
//            }
//        }
//    }
//    public void rotateToBack(){
//
//    }
}
