package org.firstinspires.ftc.teamcode.subsystems;

import com.pedropathing.math.MathFunctions;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class TestDexterManual {
    public CRServo s1;
    public CRServo s2;

    DcMotorEx encoder;
    public double difference;

    final double encoderFactor = 8192/360.0; //ticks of encoder per degree

    double shootRotator; //will depend on whats being shot first
    double targetPos = 0;
    double currentPos = 0;
    double previousTime = 0;
    double currTime = 0;
    double currError = 0;
    double pastError = 0;


    //the gear ratio has to increase for this to work

    //all positions are in degrees divided by 250 (the maximum degrees in one direction given the gear ratio
    public double power;
    double iError = 0;
    ElapsedTime intakeTimer = new ElapsedTime();
    public void init(HardwareMap hwMap){
        s1 = hwMap.get(CRServo.class, "spin1");
        s2 = hwMap.get(CRServo.class, "spin2");

        encoder = hwMap.get(DcMotorEx.class, "intake");

        s1.setDirection(CRServo.Direction.FORWARD);
        s2.setDirection(CRServo.Direction.FORWARD);

        targetPos = 0;
        spinState = SpinState.IDLE;
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
        currentPos = encoder.getCurrentPosition();
        return (int)currentPos;
    }

    public void spinToFront(){ //check for openings
        targetPos = encoder.getCurrentPosition() + 120 * encoderFactor;
    }
    public void spinToBack(){ //check for openings
        targetPos = encoder.getCurrentPosition() - 120 * encoderFactor;
    }
    //step3:check for balls and store them
    public void shoots(){
        targetPos = encoder.getCurrentPosition() + 400 * encoderFactor;
    }

    //step4: rotate the spindexer to the next position
    public void setPowerToPosition2(double currentTime){
        double pMult = 1;
        difference = targetPos - encoder.getCurrentPosition();
        double fullPowerTicks = 8192/2;
        double pVal = difference/fullPowerTicks;

        currTime = currentTime;
        currError = difference;
        double dVal = (currError - pastError)/(currentTime - previousTime);
        double dMult = .00001;

        iError += currError*(currTime-previousTime);
        double iMult = 0.00001;
        double iVal = iError * iMult;
        power = MathFunctions.clamp((pVal*pMult) + (dVal*dMult) + iVal , -1, 1);
        s1.setPower(power);
        s2.setPower(power);
        previousTime = currTime;
        pastError = currError;
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

}
