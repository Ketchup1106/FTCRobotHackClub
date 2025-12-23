package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class TestDexer {
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
    double shootStartingAtSpot1 = 2983483; //random number
    double shootStartingAtSpot2 = 2983483; //random number
    double shootStartingAtSpot3 = 2983483; //random number
    double shootRotator; //random number



    String spot1 = "U";
    String spot2 = "U";
    String spot3 = "U";
    int checkingNumber = 1;
    String currentOrder = "UUU";
    String gamePattern;
    String side;
    ElapsedTime intakeTimer = new ElapsedTime();
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
    //step1: detect intake through touch sensor
    public enum SpinState{
        IDLE,
        MOVE_TO_INTAKE,
        INTAKING,
        MOVE_TO_NEXT_SLOT,
        PREPARE_FOR_SHOT,
        SHOOT
    }
    private SpinState spinState;
    public void updateState() //spindexer state machine that i 100% on john ftc himself did not steal from king ketchup
    {
        switch(spinState){
            case IDLE:
                return; //if nothings happening just stay in place
            case MOVE_TO_INTAKE:
                if (isFrontBeingUsed()){
                    side = "front";
                    spinToFront();
                }
                else if (isBackBeingUsed()){
                    side = "back";
                    spinToBack();
                }
                spinState = spinState.INTAKING;
                intakeTimer.reset();
                break;
            case INTAKING: //moves to next slot only after ball is seen or too long has passed
                checkForBalls();
                if(checkForColorAtSpot("P", checkingNumber) || checkForColorAtSpot("G", checkingNumber)){
                    spinState = SpinState.MOVE_TO_NEXT_SLOT;
                    break;
                }
                if(intakeTimer.seconds() > .25){ //needs to be really short
                    spinState = SpinState.MOVE_TO_NEXT_SLOT;
                    break;
                }
                break;
            case MOVE_TO_NEXT_SLOT:
                spinToNext(side);
                if(checkingNumber != 3){
                    checkingNumber++;
                    spinState = SpinState.INTAKING;
                    intakeTimer.reset();
                    break;
                }
                spinState = SpinState.PREPARE_FOR_SHOT;
                break;
            case PREPARE_FOR_SHOT:
                setUpForShooting(gamePattern);
                spinState = SpinState.IDLE;
            case SHOOT:
                shootRotator = s1.getPosition() + 360;//degrees; change to value between 0 and 1 if needed
                s1.setPosition(shootRotator);
                checkingNumber = 1;
                intakeTimer.reset();
                spinState = SpinState.IDLE;
        }
    }
    public boolean isFrontBeingUsed(){
        return !frontTouchy.detectTouch();
    }
    public boolean isBackBeingUsed(){
        return !rearTouchy.detectTouch();
    }
    //step2: spin to desired intake
    public void spinToFront(){ //check for openings
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
    public void spinToBack(){ //check for openings
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
    //step3:check for balls and store them
    public void checkForBalls(){
        assignColorToPosition();
    }
    //step4: rotate the spindexer to the next position
    public void spinToNext(String frontOrBack){
        if(frontOrBack.equals("front")){
            if(s1.getPosition() == frontPos){
                s1.setPosition(frontSecondIntakePos);
                s2.setPosition(frontSecondIntakePos);
            }
            if(s1.getPosition() == frontSecondIntakePos){
                s1.setPosition(frontThirdIntakePos);
                s2.setPosition(frontThirdIntakePos);
            }
        }else if(frontOrBack.equals("back")){
            if(s1.getPosition() == backPos){
                s1.setPosition(backSecondIntakePos);
                s2.setPosition(backSecondIntakePos);
            }
            if(s1.getPosition() == backSecondIntakePos){
                s1.setPosition(backThirdIntakePos);
                s2.setPosition(backThirdIntakePos);
            }
        }
    }
    //step5: set up for shooting
    public void setUpForShooting(String gameOrder) {
        if (currentOrder.equals("PGP")) {
            if (gameOrder.equals("PGP")) {
                s1.setPosition(shootStartingAtSpot1);
                s2.setPosition(shootStartingAtSpot1);
            }
            else if (gameOrder.equals("GPP")) {
                s1.setPosition(shootStartingAtSpot3);
                s2.setPosition(shootStartingAtSpot3);
            }
            else if (gameOrder.equals("PPG")) {
                s1.setPosition(shootStartingAtSpot2);
                s2.setPosition(shootStartingAtSpot2);
            }else{ //failsafe if the camera cant pickup the obelisk
                s1.setPosition(shootStartingAtSpot1);
                s2.setPosition(shootStartingAtSpot1);
            }
        }else if (currentOrder.equals("PPG")) {
            if (gameOrder.equals("PGP")) {
                s1.setPosition(shootStartingAtSpot3);
                s2.setPosition(shootStartingAtSpot3);
            }
            else if (gameOrder.equals("GPP")) {
                s1.setPosition(shootStartingAtSpot2);
                s2.setPosition(shootStartingAtSpot2);
            }
            else if (gameOrder.equals("PPG")) {
                s1.setPosition(shootStartingAtSpot1);
                s2.setPosition(shootStartingAtSpot1);
            }
            else{ //failsafe if the camera cant pickup the obelisk
                s1.setPosition(shootStartingAtSpot1);
                s2.setPosition(shootStartingAtSpot1);
            }
        }else if (currentOrder.equals("GPP")) {
            if (gameOrder.equals("PGP")) {
                s1.setPosition(shootStartingAtSpot2);
                s2.setPosition(shootStartingAtSpot2);
            }
            else if (gameOrder.equals("GPP")) {
                s1.setPosition(shootStartingAtSpot1);
                s2.setPosition(shootStartingAtSpot1);
            }
            else if (gameOrder.equals("PPG")) {
                s1.setPosition(shootStartingAtSpot3);
                s2.setPosition(shootStartingAtSpot3);
            }
            else{ //failsafe if the camera cant pickup the obelisk
                s1.setPosition(shootStartingAtSpot1);
                s2.setPosition(shootStartingAtSpot1);
            }
        } else {
            s1.setPosition(shootStartingAtSpot1);
            s2.setPosition(shootStartingAtSpot1); //default position in case spindexer order is messed up
        }
    }

    //helper methods
    public void assignColorToPosition(){
        if(s1.getPosition() == frontPos){
            //if idli dish 1 is facing front intake then check for the color and assign it to spot1
            if(colorSensorFront.getDetectedColor(telemetry) == Color.detectedColor.PURPLE){
                spot1 = "P";
            }
            else if(colorSensorFront.getDetectedColor(telemetry) == Color.detectedColor.GREEN){
                spot1 = "G";
            }
            else if(colorSensorFront.getDetectedColor(telemetry) == Color.detectedColor.UNKNOWN){
                spot1 = "U";
            }
        }
        if(s1.getPosition() == backPos){
            //if idli dish 1 is facing back intake then check for the color and assign it to spot1
            if(colorSensorBack.getDetectedColor(telemetry) == Color.detectedColor.PURPLE){
                spot1 = "P";
            }
            else if(colorSensorBack.getDetectedColor(telemetry) == Color.detectedColor.GREEN){
                spot1 = "G";
            }
            else if(colorSensorBack.getDetectedColor(telemetry) == Color.detectedColor.UNKNOWN){
                spot1 = "U";
            }
        }
        if(s1.getPosition() == frontSecondIntakePos){
            //if idli dish 2 is facing front intake then check for the color and assign it to spot2
            if(colorSensorFront.getDetectedColor(telemetry) == Color.detectedColor.PURPLE){
                spot2 = "P";
            }
            else if(colorSensorFront.getDetectedColor(telemetry) == Color.detectedColor.GREEN){
                spot2 = "G";
            }
            else if(colorSensorFront.getDetectedColor(telemetry) == Color.detectedColor.UNKNOWN){
                spot2 = "U";
            }
        }
        if(s1.getPosition() == backSecondIntakePos){
            //if idli dish 2 is facing back intake then check for the color and assign it to spot2
            if(colorSensorBack.getDetectedColor(telemetry) == Color.detectedColor.PURPLE){
                spot2 = "P";
            }
            else if(colorSensorBack.getDetectedColor(telemetry) == Color.detectedColor.GREEN){
                spot2 = "G";
            }
            else if(colorSensorBack.getDetectedColor(telemetry) == Color.detectedColor.UNKNOWN){
                spot2 = "U";
            }
        }
        if(s1.getPosition() == frontThirdIntakePos){
            //if idli dish 3 is facing front intake then check for the color and assign it to spot3
            if(colorSensorFront.getDetectedColor(telemetry) == Color.detectedColor.PURPLE){
                spot3 = "P";
            }
            else if(colorSensorFront.getDetectedColor(telemetry) == Color.detectedColor.GREEN){
                spot3 = "G";
            }
            else if(colorSensorFront.getDetectedColor(telemetry) == Color.detectedColor.UNKNOWN){
                spot3 = "U";
            }
        }
        if(s1.getPosition() == backSecondIntakePos){
            //if idli dish 3 is facing back intake then check for the color and assign it to spot3
            if(colorSensorBack.getDetectedColor(telemetry) == Color.detectedColor.PURPLE){
                spot3 = "P";
            }
            else if(colorSensorBack.getDetectedColor(telemetry) == Color.detectedColor.GREEN){
                spot3 = "G";
            }
            else if(colorSensorBack.getDetectedColor(telemetry) == Color.detectedColor.UNKNOWN){
                spot3 = "U";
            }
        }
        currentOrder = spot1 + spot2 + spot3;
    }
    public boolean checkForColorAtSpot(String ballColor, int spot){
        if(currentOrder.indexOf(ballColor) == spot-1){
            return true;
        }
        return false;
    }
    public void setGameOrder(String order){ //will be used in teleop once the camera picks up the apriltags
        gamePattern = order;
    }
}
