package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class TestDexer {
    public CRServo s1;
    public CRServo s2;

    DcMotorEx encoder;
    public double difference;
    Telemetry telemetry;
    //public TouchySensor frontTouchy = new TouchySensor();
    //public TouchySensor rearTouchy = new TouchySensor();
    ColorSensorFrontTemp colorSensorFront = new ColorSensorFrontTemp();
    ColorSensorBackTemp colorSensorBack = new ColorSensorBackTemp();



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

    boolean shooting;
    //the gear ratio has to increase for this to work

    //all positions are in degrees divided by 250 (the maximum degrees in one direction given the gear ratio
    String spot1 = "U";
    String spot2 = "U";
    String spot3 = "U";
    int checkingNumber = 1;
    String currentOrder = "UUU";
    String gamePattern;
    String side;
    String intakeSide = "waiting";
    ElapsedTime intakeTimer = new ElapsedTime();
    public void init(HardwareMap hwMap){
        s1 = hwMap.get(CRServo.class, "spin1");
        s2 = hwMap.get(CRServo.class, "spin2");

        encoder = hwMap.get(DcMotorEx.class, "intake");

        s1.setDirection(CRServo.Direction.FORWARD);
        s2.setDirection(CRServo.Direction.FORWARD);

        //frontTouchy.init(hwMap);
        //rearTouchy.init(hwMap);

        colorSensorFront.init(hwMap);
        colorSensorBack.init(hwMap);

//        s1.setPower(frontPos);
//        s2.setPower(frontPos); //0 is front intake

        targetPos = frontPos;
        spinState = SpinState.IDLE;
        shooting = false;
    }
    //step1: detect intake through touch sensor
    public enum SpinState{
        IDLE,
        MOVE_TO_INTAKE,
        INTAKING,
        PREPARE_FOR_SHOT,
        SHOOT
    }
    private SpinState spinState;
    public SpinState getSpinState(){
        return spinState;
    }

    public int updatePos(){
        currentPos = encoder.getCurrentPosition() / encoderFactor;
        return (int)currentPos;
    }
    /*
    public boolean isFrontBeingUsed(){
        return !frontTouchy.detectTouch();
    }
    public boolean isBackBeingUsed(){
        return !rearTouchy.detectTouch();
    }
     */
    //step2: spin to desired intake
    public void spinToFront(){ //check for openings
        //if(isFrontBeingUsed()){
        if (checkForColorAtSpot("U", 1)){
            targetPos = frontPos;
        }else if (checkForColorAtSpot("U", 2)){
            targetPos = frontSecondIntakePos;
        }else if(checkForColorAtSpot("U", 3)){
            targetPos = frontThirdIntakePos;
        }
        //}
    }
    public void spinToBack(){ //check for openings
        //if(isBackBeingUsed()){
        if (checkForColorAtSpot("U", 1)){
//                s1.setPower(backPos);
//                s2.setPower(backPos);
            targetPos = backPos;
        }else if (checkForColorAtSpot("U", 2)){
//                s1.setPower(backSecondIntakePos);
//                s2.setPower(backSecondIntakePos);
            targetPos = backSecondIntakePos;
        }else if(checkForColorAtSpot("U", 3)){
//                s1.setPower(backThirdIntakePos);
//                s2.setPower(backThirdIntakePos);
            targetPos = backSecondIntakePos;
        }
        //}
    }
    //step3:check for balls and store them
    public void checkForBalls(){
        assignColorToPosition();
    }
    //step4: rotate the spindexer to the next position
    public void spinToNext(String frontOrBack){
        if(frontOrBack.equals("front")){
            if(targetPos == frontPos){
//                s1.setPower(frontSecondIntakePos);
//                s2.setPower(frontSecondIntakePos);
                targetPos = frontSecondIntakePos;
            }
            if(targetPos == frontSecondIntakePos){
//                s1.setPower(frontThirdIntakePos);
//                s2.setPower(frontThirdIntakePos);
                targetPos = frontThirdIntakePos;
            }
        }else if(frontOrBack.equals("back")){
            if(targetPos == backPos){
//                s1.setPower(backSecondIntakePos);
//                s2.setPower(backSecondIntakePos);
                targetPos = backSecondIntakePos;
            }
            if(targetPos == backSecondIntakePos){
//                s1.setPower(backThirdIntakePos);
//                s2.setPower(backThirdIntakePos);
                targetPos = backThirdIntakePos;
            }
        }
    }
    public void spinToNextManual(String frontOrBack){
        if(frontOrBack.equals("front")){
            if(targetPos == frontPos){
                targetPos = frontSecondIntakePos;
            }
            else if(targetPos == frontSecondIntakePos){
                targetPos = frontThirdIntakePos;
            }
            else if(targetPos == frontThirdIntakePos){
                targetPos = frontPos;
            }
        }
        else if(frontOrBack.equals("back")){
            if(targetPos == backPos){
                targetPos = backSecondIntakePos;
            }
            else if(targetPos == backSecondIntakePos){
                targetPos = backThirdIntakePos;
            }
            else if(targetPos == backThirdIntakePos){
                targetPos = backPos;
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
    }//step6: shoot
    public void shoot(){
        shooting = true;
        currentPos+=400;
    }

    //helper methods
    public void assignColorToPosition(){
        if(targetPos == frontPos){
            //if idli dish 1 is facing front intake then check for the color and assign it to spot1
            if(colorSensorFront.getDetectedColor() == ColorSensorFrontTemp.detectedColor.PURPLE){
                spot1 = "P";
            }
            else if(colorSensorFront.getDetectedColor() == ColorSensorFrontTemp.detectedColor.GREEN){
                spot1 = "G";
            }
            else if(colorSensorFront.getDetectedColor() == ColorSensorFrontTemp.detectedColor.UNKNOWN){
                spot1 = "U";
            }
        }
        if(targetPos == backPos){
            //if idli dish 1 is facing back intake then check for the color and assign it to spot1
            if(colorSensorBack.getDetectedColor() == ColorSensorBackTemp.detectedColor.PURPLE){
                spot1 = "P";
            }
            else if(colorSensorBack.getDetectedColor() == ColorSensorBackTemp.detectedColor.GREEN){
                spot1 = "G";
            }
            else if(colorSensorBack.getDetectedColor() == ColorSensorBackTemp.detectedColor.UNKNOWN){
                spot1 = "U";
            }
        }
        if(targetPos == frontSecondIntakePos){
            //if idli dish 2 is facing front intake then check for the color and assign it to spot2
            if(colorSensorFront.getDetectedColor() == ColorSensorFrontTemp.detectedColor.PURPLE){
                spot2 = "P";
            }
            else if(colorSensorFront.getDetectedColor() == ColorSensorFrontTemp.detectedColor.GREEN){
                spot2 = "G";
            }
            else if(colorSensorFront.getDetectedColor() == ColorSensorFrontTemp.detectedColor.UNKNOWN){
                spot2 = "U";
            }
        }
        if(targetPos == backSecondIntakePos){
            //if idli dish 2 is facing back intake then check for the color and assign it to spot2
            if(colorSensorBack.getDetectedColor() == ColorSensorBackTemp.detectedColor.PURPLE){
                spot2 = "P";
            }
            else if(colorSensorBack.getDetectedColor() == ColorSensorBackTemp.detectedColor.GREEN){
                spot2 = "G";
            }
            else if(colorSensorBack.getDetectedColor() == ColorSensorBackTemp.detectedColor.UNKNOWN){
                spot2 = "U";
            }
        }
        if(targetPos == frontThirdIntakePos){
            //if idli dish 3 is facing front intake then check for the color and assign it to spot3
            if(colorSensorFront.getDetectedColor() == ColorSensorFrontTemp.detectedColor.PURPLE){
                spot3 = "P";
            }
            else if(colorSensorFront.getDetectedColor() == ColorSensorFrontTemp.detectedColor.GREEN){
                spot3 = "G";
            }
            else if(colorSensorFront.getDetectedColor() == ColorSensorFrontTemp.detectedColor.UNKNOWN){
                spot3 = "U";
            }
        }
        if(targetPos == backSecondIntakePos){
            //if idli dish 3 is facing back intake then check for the color and assign it to spot3
            if(colorSensorBack.getDetectedColor() == ColorSensorBackTemp.detectedColor.PURPLE){
                spot3 = "P";
            }
            else if(colorSensorBack.getDetectedColor() == ColorSensorBackTemp.detectedColor.GREEN){
                spot3 = "G";
            }
            else if(colorSensorBack.getDetectedColor() == ColorSensorBackTemp.detectedColor.UNKNOWN){
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
    public void setPowerToPosition(double curr){
        double ticksPerDegree = 8192/360.0;
        difference = targetPos - curr*ticksPerDegree;
        if(difference > 20 * ticksPerDegree){
            s1.setPower(1);
            s2.setPower(1);
        }
        else if(difference <-20*ticksPerDegree){
            s1.setPower(-1);
            s2.setPower(-1);
        }
        else if(difference > 3*ticksPerDegree){
            s1.setPower(.07);
            s2.setPower(.07);
        }
        else if(difference < -3*ticksPerDegree){
            s1.setPower(-.07);
            s2.setPower(-.07);
        }
        s1.setPower(0);
        s2.setPower(0);
//        if(shooting && Math.abs(difference) > 2 * ticksPerDegree){
//            return 1;
//        }
//        //maybe consider if we need to retreat or add an angle wrap
//        else if(Math.abs(difference) > 2 * ticksPerDegree){
//            return -1;
//        }

    }
    public String getPatternOrSpots(String desired){
        if(desired.equals("1")){
            return spot1;
        }
        else if(desired.equals("2")){
            return spot2;
        }else if(desired.equals("3")){
            return spot3;
        }
        return currentOrder;

    }
    public void setSpinState(int desiredState){
        if(desiredState == 1){
            spinState = SpinState.MOVE_TO_INTAKE;
        } else if(desiredState == 2){
            spinState = SpinState.INTAKING;
        }else if(desiredState == 3){
            spinState = SpinState.PREPARE_FOR_SHOT;
        }else if(desiredState == 4){
            spinState = SpinState.SHOOT;
        }else if(desiredState == 0){
            spinState = SpinState.IDLE;
        }
    }

    public double getTargetPos(){
        return targetPos;
    }

    public void setPower(double pow){
        s1.setPower(pow);
        s2.setPower(pow);
    }

}
