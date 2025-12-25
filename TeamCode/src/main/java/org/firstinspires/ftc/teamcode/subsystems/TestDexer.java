package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class TestDexer {
    public CRServo s1;
    public CRServo s2;
    Shooter shooter = new Shooter();
    Telemetry telemetry;
    public TouchySensor frontTouchy = new TouchySensor();
    public TouchySensor rearTouchy = new TouchySensor();
    Color colorSensorFront = new Color();
    Color colorSensorBack = new Color();



    final double encoderFactor = 8192/360.0; //ticks of encoder per degree
    double frontPos = 0;
    double frontSecondIntakePos = 120* encoderFactor;
    double frontThirdIntakePos = 240* encoderFactor;
    double backPos = 180* encoderFactor;
    double backSecondIntakePos = -60* encoderFactor; //300>250 so we do the negative equivalent
    double backThirdIntakePos = 60* encoderFactor;
    double shootStartingAtSpot1 = 90* encoderFactor;
    double shootStartingAtSpot2 = 210* encoderFactor;
    double shootStartingAtSpot3 = -30* encoderFactor;
    double shootRotator; //will depend on whats being shot first
    double targetPos = frontPos;
    double currentPos = 0;
    //the gear ratio has to increase for this to work

    //all positions are in degrees divided by 250 (the maximum degrees in one direction given the gear ratio
    String spot1 = "U";
    String spot2 = "U";
    String spot3 = "U";
    int checkingNumber = 1;
    String currentOrder = "UUU";
    String gamePattern;
    String side;
    ElapsedTime intakeTimer = new ElapsedTime();
    public void init(HardwareMap hwMap){
        s1 = hwMap.get(CRServo.class, "spin1");
        s2 = hwMap.get(CRServo.class, "spin2");

        s1.setDirection(CRServo.Direction.FORWARD);
        s2.setDirection(CRServo.Direction.REVERSE);

        frontTouchy.init(hwMap);
        rearTouchy.init(hwMap);

        colorSensorFront.init(hwMap);
        colorSensorBack.init(hwMap);

        //s1.setPower(frontPos);
        //s2.setPower(frontPos); //0 is front intake
        shooter.init(hwMap, telemetry);
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
                s1.setPower(setPowerToPosition(targetPos));
                s2.setPower(setPowerToPosition(targetPos));
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
                //add condition that accounts for no usage being detected
                if(setPowerToPosition(targetPos) != 0) {
                    s1.setPower(setPowerToPosition(targetPos));
                    s2.setPower(setPowerToPosition(targetPos));
                }
                if(setPowerToPosition(targetPos) == 0){
                    spinState = spinState.INTAKING;
                    intakeTimer.reset();
                }
                break;
            case INTAKING: //moves to next slot only after ball is seen or too long has passed
                checkForBalls(); //calls the assign color to pos method
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
                spinToNext(side); //moves to the next slot as long as were not already on slot 3
                if(setPowerToPosition(targetPos) != 0) {
                    s1.setPower(setPowerToPosition(targetPos));
                    s2.setPower(setPowerToPosition(targetPos));
                    break;
                }
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
                shootRotator = currentPos + (360*encoderFactor);//degrees; change to value between 0 and 1 if needed
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
                s1.setPower(frontPos);
                s2.setPower(frontPos);
                targetPos = frontPos;
            }else if (checkForColorAtSpot("U", 2)){
                s1.setPower(frontSecondIntakePos);
                s2.setPower(frontSecondIntakePos);
                targetPos = frontSecondIntakePos;
            }else if(checkForColorAtSpot("U", 3)){
                s1.setPower(frontThirdIntakePos);
                s2.setPower(frontThirdIntakePos);
                targetPos = frontThirdIntakePos;
            }
        }
    }
    public void spinToBack(){ //check for openings
        if(isBackBeingUsed()){
            if (checkForColorAtSpot("U", 1)){
                s1.setPower(backPos);
                s2.setPower(backPos);
                targetPos = backPos;
            }else if (checkForColorAtSpot("U", 2)){
                s1.setPower(backSecondIntakePos);
                s2.setPower(backSecondIntakePos);
                targetPos = backSecondIntakePos;
            }else if(checkForColorAtSpot("U", 3)){
                s1.setPower(backThirdIntakePos);
                s2.setPower(backThirdIntakePos);
                targetPos = backSecondIntakePos;
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
            if(targetPos == frontPos){
                s1.setPower(frontSecondIntakePos);
                s2.setPower(frontSecondIntakePos);
                targetPos = frontSecondIntakePos;
            }
            if(targetPos == frontSecondIntakePos){
                s1.setPower(frontThirdIntakePos);
                s2.setPower(frontThirdIntakePos);
                targetPos = frontThirdIntakePos;
            }
        }else if(frontOrBack.equals("back")){
            if(targetPos == backPos){
                s1.setPower(backSecondIntakePos);
                s2.setPower(backSecondIntakePos);
                targetPos = backSecondIntakePos;
            }
            if(targetPos == backSecondIntakePos){
                s1.setPower(backThirdIntakePos);
                s2.setPower(backThirdIntakePos);
                targetPos = backThirdIntakePos;
            }
        }
    }
    //step5: set up for shooting
    public void setUpForShooting(String gameOrder) {
        if (currentOrder.equals("PGP")) {
            if (gameOrder.equals("PGP")) {
//                s1.setPower(shootStartingAtSpot1);
//                s2.setPower(shootStartingAtSpot1);
                targetPos = shootStartingAtSpot1;
            }
            else if (gameOrder.equals("GPP")) {
//                s1.setPower(shootStartingAtSpot3);
//                s2.setPower(shootStartingAtSpot3);
                targetPos = shootStartingAtSpot3;
            }
            else if (gameOrder.equals("PPG")) {
//                s1.setPower(shootStartingAtSpot2);
//                s2.setPower(shootStartingAtSpot2);
                targetPos = shootStartingAtSpot2;
            }
            else{ //failsafe if the camera cant pickup the obelisk
//                s1.setPower(shootStartingAtSpot1);
//                s2.setPower(shootStartingAtSpot1);
                targetPos = shootStartingAtSpot1;
            }
        }
        else if (currentOrder.equals("PPG")) {
            if (gameOrder.equals("PGP")) {
//                s1.setPower(shootStartingAtSpot3);
//                s2.setPower(shootStartingAtSpot3);
                targetPos = shootStartingAtSpot3;
            }
            else if (gameOrder.equals("GPP")) {
//                s1.setPower(shootStartingAtSpot2);
//                s2.setPower(shootStartingAtSpot2);
                targetPos = shootStartingAtSpot2;
            }
            else if (gameOrder.equals("PPG")) {
//                s1.setPower(shootStartingAtSpot1);
//                s2.setPower(shootStartingAtSpot1);
                targetPos = shootStartingAtSpot1;
            }
            else{ //failsafe if the camera cant pickup the obelisk
//                s1.setPower(shootStartingAtSpot1);
//                s2.setPower(shootStartingAtSpot1);
                targetPos = shootStartingAtSpot1;
            }
        }
        else if (currentOrder.equals("GPP")) {
            if (gameOrder.equals("PGP")) {
//                s1.setPower(shootStartingAtSpot2);
//                s2.setPower(shootStartingAtSpot2);
                targetPos = shootStartingAtSpot2;
            }
            else if (gameOrder.equals("GPP")) {
//                s1.setPower(shootStartingAtSpot1);
//                s2.setPower(shootStartingAtSpot1);
                targetPos = shootStartingAtSpot1;
            }
            else if (gameOrder.equals("PPG")) {
//                s1.setPower(shootStartingAtSpot3);
//                s2.setPower(shootStartingAtSpot3);
                targetPos = shootStartingAtSpot3;
            }
            else{ //failsafe if the camera cant pickup the obelisk
//                s1.setPower(shootStartingAtSpot1);
//                s2.setPower(shootStartingAtSpot1);
                targetPos = shootStartingAtSpot1;
            }
        }
        else {
//            s1.setPower(shootStartingAtSpot1);
//            s2.setPower(shootStartingAtSpot1); //default position in case spindexer order is messed up
            targetPos = shootStartingAtSpot1;
        }
    }

    //helper methods
    public void assignColorToPosition(){
        if(targetPos == frontPos){
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
        if(targetPos == backPos){
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
        if(targetPos == frontSecondIntakePos){
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
        if(targetPos == backSecondIntakePos){
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
        if(targetPos == frontThirdIntakePos){
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
        if(targetPos == backSecondIntakePos){
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
    public double setPowerToPosition(double target){
        double ticksPerDegree = 8192/360.0;
        double difference = target - currentPos;
        if(difference > 2* ticksPerDegree){
            return 1;
        }
        else if(difference <-2){
            return -1;
        }
        return 0;
    }

    public void setStateToShoot(){
        spinState = spinState.SHOOT;
    }
    public void setStateToIdle(){
        spinState = spinState.IDLE;
    }
    public void setStateToMoveToIntake(){
        spinState = spinState.MOVE_TO_INTAKE;
    }

}
