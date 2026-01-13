package org.firstinspires.ftc.teamcode.opmodes;

import com.pedropathing.math.MathFunctions;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.subsystems.TestDexer;


@TeleOp(name = "spindexer PID tuner")
public class spinTuner extends OpMode {
    double gearRatio = 103.0/26;

    double ticksPerDegree = (537 * gearRatio)/360;

    CRServo s1;
    CRServo s2;
    DcMotorEx encoder;
    double previousTime;

    double pastError;

    double iError = 0;

    public Servo hoodAngle;

    final double encoderFactor = 8192/360.0; //ticks of encoder per degree

    double difference = 0;

    double currTime = 0;
    double targetPos;
    double frontPos = 0;
    double frontSecondIntakePos = 120* encoderFactor;
    double frontThirdIntakePos = 240* encoderFactor;
    double backPos = 180* encoderFactor;
    double backSecondIntakePos = -60* encoderFactor; //300>250 so we do the negative equivalent
    double backThirdIntakePos = 60* encoderFactor;
    double shootStartingAtSpot1 = 90* encoderFactor;
    double shootStartingAtSpot2 = 210* encoderFactor;
    double shootStartingAtSpot3 = -30* encoderFactor;

    double I = 0;
    double P = 1;
    double D = 0;

    ElapsedTime runtime = new ElapsedTime();

    double[] stepSizes = {10.0, 1.0, 0.1, 0.001, 0.0001};
    int stepIndex = 0;

    @Override
    public void init(){

        encoder = hardwareMap.get(DcMotorEx.class, "intake");

        s1 = hardwareMap.get(CRServo.class, "spin1");
        s2 = hardwareMap.get(CRServo.class, "spin2");

        s1.setDirection(CRServo.Direction.FORWARD);
        s2.setDirection(CRServo.Direction.FORWARD);

        telemetry.addLine("Init Complete");

    }


    @Override
    public void loop(){
        double currPos = encoder.getCurrentPosition();
        if(gamepad1.yWasPressed()){
            if(targetPos == frontPos){
                targetPos = frontSecondIntakePos;
            }
            else{
                targetPos = frontPos;
            }
        }

        if(gamepad1.bWasPressed()){
            stepIndex = (stepIndex + 1) % stepSizes.length;
        }
        if(gamepad1.dpadLeftWasPressed()){
            I -= stepSizes[stepIndex];
        }
        if(gamepad1.dpadRightWasPressed()){
            I += stepSizes[stepIndex];
        }
        if(gamepad1.dpadUpWasPressed()){
            P += stepSizes[stepIndex];
        }
        if(gamepad1.dpadDownWasPressed()){
            P -= stepSizes[stepIndex];
        }
        if(gamepad1.xWasPressed()){
            D += stepSizes[stepIndex];
        }
        if(gamepad1.aWasPressed()){
            D -= stepSizes[stepIndex];
        }






        double curTarg= targetPos;

        double error1 = curTarg - currPos;
        setPowerToPosition3(currPos,runtime.seconds());




        telemetry.addData("target pos: ", curTarg);
        telemetry.addData("Current pos: ", currPos);

        telemetry.addData("Error ", error1);

        telemetry.addData("Tuning P: ", P);
        telemetry.addData("Tuning I: ", I);
        telemetry.addData("Tuning D: ", D);
        telemetry.addData("Step Size: ", stepSizes[stepIndex]);
        telemetry.addLine("Y: switches target pose\nB: increase step size");

        //P 75

        //F 20.16   (20.4 for 800 vel)
    }
    public void setPowerToPosition3(double curr, double currentTime){
        double pMult = P;
        difference = targetPos - Math.abs(curr);
        double fullPowerTicks = 8192/2;
        double pVal = difference/fullPowerTicks;

        currTime = runtime.seconds();
        double currError = difference;
        double dVal = (currError - pastError)/(currentTime - previousTime);
        double dMult = D; //.00001;

        iError += currError*(currTime-previousTime);
        double iMult = I; //0/00001
        double iVal = iError * iMult;
        double power = MathFunctions.clamp((pVal*pMult) + (dVal*dMult) + iVal , -1, 1);
        s1.setPower(power);
        s2.setPower(power);
        previousTime = currTime;
        pastError = currError;
    }
}


