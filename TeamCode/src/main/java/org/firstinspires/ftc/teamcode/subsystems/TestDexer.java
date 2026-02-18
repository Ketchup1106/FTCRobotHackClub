package org.firstinspires.ftc.teamcode.subsystems;

import com.pedropathing.math.MathFunctions;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class TestDexer {
    public CRServo s1;
    public CRServo s2;

    public DcMotorEx encoder;
    public double difference;
    Telemetry telemetry;
    //public TouchySensor frontTouchy = new TouchySensor();
    //public TouchySensor rearTouchy = new TouchySensor();
    ColorSensorFrontTemp colorSensorFront = new ColorSensorFrontTemp();
    ColorSensorBackTemp colorSensorBack = new ColorSensorBackTemp();



    final double encoderFactor = 8192/360.0; //ticks of encoder per degree
    public double frontPos = 0; //0
    public double frontSecondIntakePos = -2652;
    public double frontThirdIntakePos = -5405;
    public double backPos = -4050;
    public double backSecondIntakePos = -6790;
    public double backThirdIntakePos = -9522;
    public double shootStartingAtSpot1 = -10069; //subtracted 210
    public double shootStartingAtSpot2 = -14803;
    public double shootStartingAtSpot3 = -17524;
    double shootRotator; //will depend on whats being shot first
    public double targetPos = frontPos;
    double currentPos = 0;
    double previousTime = 0;
    double currTime = 0;
    double currError = 0;
    double pastError = 0;

    public boolean shooting;

    String spot1 = "U";
    String spot2 = "U";
    String spot3 = "U";
    int checkingNumber = 1;
    public String currentOrder = "UUU";
    String gamePattern;
    public double power = 1;
    public double lastTargetPos = frontPos;
    public void init(HardwareMap hwMap){
        s1 = hwMap.get(CRServo.class, "spin1");
        s2 = hwMap.get(CRServo.class, "spin2");

        encoder = hwMap.get(DcMotorEx.class, "backIntake");

        s1.setDirection(CRServo.Direction.FORWARD);
        s2.setDirection(CRServo.Direction.FORWARD);

        encoder.setDirection(DcMotorSimple.Direction.REVERSE);

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
        SHOOT,
        GO_BACK,
        MANUAL_OVERRIDE
    }
    private SpinState spinState;
    public SpinState getSpinState(){
        return spinState;
    }

    public int updatePos(){
        int currentPos = encoder.getCurrentPosition();
        return currentPos;
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
    public void spinToFront(int ballCount){ //check for openings
        //if(isFrontBeingUsed()){
        if (ballCount == 0){
            targetPos = frontPos;
        }else if (ballCount == 1){
            targetPos = frontSecondIntakePos;
        }else if(ballCount == 2){
            targetPos = frontThirdIntakePos;
        }
        //}
    }
    public void spinToBack(int ballCount){ //check for openings
        //if(isBackBeingUsed()){
        if (ballCount == 0){
//                s1.setPower(backPos);
//                s2.setPower(backPos);
            targetPos = backPos;
        }else if (ballCount == 1){
//                s1.setPower(backSecondIntakePos);
//                s2.setPower(backSecondIntakePos);
            targetPos = backSecondIntakePos;
        }else if(ballCount == 2){
//                s1.setPower(backThirdIntakePos);
//                s2.setPower(backThirdIntakePos);
            targetPos = backThirdIntakePos;
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
            else if(targetPos == frontSecondIntakePos){
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
            else if(targetPos == backSecondIntakePos){
//                s1.setPower(backThirdIntakePos);
//                s2.setPower(backThirdIntakePos);
                targetPos = backThirdIntakePos;
            }
        }
    }
    public void spinToNextManual(String frontOrBack){
        if(frontOrBack.equals("front")){
            if(targetPos == frontPos || targetPos == -16384){
                targetPos = frontSecondIntakePos - 16384;
            }
            else if(targetPos == frontSecondIntakePos || targetPos == frontSecondIntakePos - 16384){
                targetPos = frontThirdIntakePos - 16384;
            }
            else if(targetPos == frontThirdIntakePos || targetPos == frontThirdIntakePos - 16384 || targetPos == shootStartingAtSpot1 || targetPos == shootStartingAtSpot2 || targetPos == shootStartingAtSpot3){
                targetPos = frontPos - 16384;
            }
        }
        else if(frontOrBack.equals("back")){
            if(targetPos == backPos || targetPos == backPos-16384){
                targetPos = backSecondIntakePos - 16384;
            }
            else if(targetPos == backSecondIntakePos || targetPos == backSecondIntakePos - 16384){
                targetPos = backThirdIntakePos - 16384;
            }
            else if(targetPos == backThirdIntakePos ||  targetPos == backThirdIntakePos - 16384 || targetPos == shootStartingAtSpot1 || targetPos == shootStartingAtSpot2 || targetPos == shootStartingAtSpot3){
                targetPos = backPos - 16384;
            }
        }
    }
    //step5: set up for shooting
    public void setUpForShooting(String gameOrder) {
        if (currentOrder.equals("PGP")) {
            if (gameOrder.equals("PGP")) {
//                s1.setPower(shootStartingAtSpot3);
//                s2.setPower(shootStartingAtSpot3);
                targetPos = shootStartingAtSpot2;
            }
            else if (gameOrder.equals("GPP")) {
//                s1.setPower(shootStartingAtSpot2);
//                s2.setPower(shootStartingAtSpot2);
                targetPos = shootStartingAtSpot3;
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
        else if (currentOrder.equals("PPG")) {
            if (gameOrder.equals("PGP")) {
//                s1.setPower(shootStartingAtSpot1);
//                s2.setPower(shootStartingAtSpot1);
                targetPos = shootStartingAtSpot1;
            }
            else if (gameOrder.equals("GPP")) {
//                s1.setPower(shootStartingAtSpot3);
//                s2.setPower(shootStartingAtSpot3);
                targetPos = shootStartingAtSpot2;
            }
            else if (gameOrder.equals("PPG")) {
//                s1.setPower(shootStartingAtSpot2);
//                s2.setPower(shootStartingAtSpot2);
                targetPos = shootStartingAtSpot3;
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
        targetPos += 13000;
        currentOrder = "UUU";
        spot1 = "U";
        spot2 = "U";
        spot3 = "U";
    }
    //optional step: go back to previous pose (ONLY IF MANUAL OVERRIDE)
    public void goBack(double cuurent){
        lastTargetPos = targetPos;
        targetPos = cuurent + 500;

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
        if(targetPos == backThirdIntakePos){
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
    public boolean checkForColorAtSpot(char ballColor, int spot){
        if(currentOrder.charAt(spot-1) == ballColor){
            return true;
        }
        return false;
    }
    public void setGameOrder(String order){ //will be used in teleop once the camera picks up the apriltags
        gamePattern = order;
    }
    public void setPowerToPosition2(double curr, double currentTime){
        double pMult = 1; //0.75
        difference = targetPos - curr;
        double fullPowerTicks = 8192/2;
        double pVal = difference/fullPowerTicks;


        currTime = currentTime;
        currError = difference;
        double dVal = (currError - pastError)/(currentTime - previousTime);
        double dMult = 0.00002; //.000015

        double F = .048; //.048 if no oscillations needed .053 before
        double fMult= F*Math.signum(pVal);

//        iError += currError*(currTime-previousTime);
//        double iMult = 0;
//        double iVal = iError * iMult;
        if(MathFunctions.roughlyEquals(difference, 0, 30)){
            fMult = 0;
        }
        power = MathFunctions.clamp((pVal*pMult) + (dVal*dMult) + fMult, -1, 1);
//        if(shooting){
//            power /= 1.5;
//        }
//        if(shooting){
//            power = Math.abs(power);
//        }else{
//            power = -Math.abs(power);
//        }

        s1.setPower(power);
        s2.setPower(power);
        previousTime = currTime;
        pastError = currError;
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
        }else if(desiredState == 5){
            spinState = SpinState.GO_BACK;
        }else if(desiredState == 6){
            spinState = SpinState.MANUAL_OVERRIDE;
        } else if(desiredState == 0){
            spinState = SpinState.IDLE;
        }
    }
    public void goToLastState(SpinState state){
        spinState = state;
    }

    public double getTargetPos(){
        return targetPos;
    }

    public void setPower(double pow){
        s1.setPower(pow);
        s2.setPower(pow);
    }
    public String getDetectedColorFront(){
        if(colorSensorFront.getDetectedColor() == ColorSensorFrontTemp.detectedColor.PURPLE){
            return "purple";
        }
        else if(colorSensorFront.getDetectedColor() == ColorSensorFrontTemp.detectedColor.GREEN){
            return "green";
        }
        else{
            return "unknown";
        }
    }
    public String getDetectedColorBack(){
        if(colorSensorBack.getDetectedColor() == ColorSensorBackTemp.detectedColor.PURPLE){
            return "purple";
        }
        else if(colorSensorBack.getDetectedColor() == ColorSensorBackTemp.detectedColor.GREEN){
            return "green";
        }
        else{
            return "unknown";
        }
    }
    public boolean isBusy(){
        return power > .1;
    }

    public void setTargetPos(double pos){
        targetPos = pos;
    }

}
