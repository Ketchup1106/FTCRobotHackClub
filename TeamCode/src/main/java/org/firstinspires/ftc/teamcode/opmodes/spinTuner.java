package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.subsystems.TestDexer;


@TeleOp(name = "spindexer PID tuner")
@Disabled
public class spinTuner extends OpMode {
    double gearRatio = 103.0/26;

    double ticksPerDegree = (537 * gearRatio)/360;

    TestDexer testDexer = new TestDexer();

    public Servo hoodAngle;

    final double encoderFactor = 8192/360.0; //ticks of encoder per degree
    double pos1 = 45*ticksPerDegree;
    double high = 100 *ticksPerDegree;


    double currTargetPosition = high;

    double I = 0;

    double P = 0;
    double D = 0;

    double[] stepSizes = {10.0, 1.0, 0.1, 0.001, 0.0001};
    int stepIndex = 0;

    @Override
    public void init(){

        testDexer.init(hardwareMap);

        telemetry.addLine("Init Complete");

    }


    @Override
    public void loop(){
        if(gamepad1.yWasPressed()){
            if(currTargetPosition == high){
                currTargetPosition = low;
            }
            else{
                currTargetPosition = high;
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



        if(gamepad1.rightBumperWasPressed()){

        }




        double curVel1 = ;


        double error1 = currTargetPosition - curVel1;




        telemetry.addData("target pos: ", currTargetPosition/ticksPerDegree);
        telemetry.addData("Current pos: ", curVel1/ticksPerDegree);

        telemetry.addData("Error ", error1/ticksPerDegree);

        telemetry.addData("Tuning P: ", P);
        telemetry.addData("Tuning I: ", I);
        telemetry.addData("Tuning D: ", D);
        telemetry.addData("Step Size: ", stepSizes[stepIndex]);
        telemetry.addLine("Y: switches target pose\nB: increase step size");

        //P 75

        //F 20.16   (20.4 for 800 vel)
    }
}


