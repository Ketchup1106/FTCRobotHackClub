package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;


@TeleOp(name = "turet pid tuner")
public class turretTuner extends OpMode {
    double gearRatio = 103.0/26;

    double ticksPerDegree = (537 * gearRatio)/360;

    public DcMotorEx shooter1;
    public DcMotorEx shooter2;
    public DcMotorEx turret;

    public Servo hoodAngle;

    double low = 45*ticksPerDegree;
    double high = 135 *ticksPerDegree;

    double hoodAnglePos = 0;

    double currTargetPosition = high;

    double I = 0;

    double P = 0;
    double D = 0;

    double[] stepSizes = {10.0, 1.0, 0.1, 0.001, 0.0001};
    int stepIndex = 0;

    @Override
    public void init(){
//        shooter1 = hardwareMap.get(DcMotorEx.class, "sm1");
//        shooter2 = hardwareMap.get(DcMotorEx.class, "sm2");
        turret = hardwareMap.get(DcMotorEx.class, "tm");
        turret.setDirection(DcMotorSimple.Direction.REVERSE);
        turret.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        turret.setTargetPosition(0);
        turret.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
        turret.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        hoodAngle = hardwareMap.get(Servo.class, "hoodAngle");

//        shooter1.setDirection(DcMotorSimple.Direction.REVERSE);
//        shooter2.setDirection(DcMotorSimple.Direction.REVERSE);

//        shooter1.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
//        shooter2.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);


        PIDFCoefficients pidfCoefficients = new PIDFCoefficients(P, I, D, 0);
        turret.setPIDFCoefficients(DcMotor.RunMode.RUN_TO_POSITION, pidfCoefficients);
//        shooter2.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidfCoefficients);

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


        if(gamepad1.bWasPressed()){
            turret.setTargetPosition((int) currTargetPosition);
        }


        PIDFCoefficients pidfCoefficients = new PIDFCoefficients(P, I, D, 0);
        turret.setPIDFCoefficients(DcMotor.RunMode.RUN_TO_POSITION, pidfCoefficients);



        double curVel1 = turret.getCurrentPosition();


        double error1 = currTargetPosition - curVel1;




        telemetry.addData("target pos: ", currTargetPosition);
        telemetry.addData("Current pos: ", curVel1);

        telemetry.addData("Error ", error1);

        telemetry.addData("Tuning P: ", P);
        telemetry.addData("Tuning I: ", I);
        telemetry.addData("Tuning D: ", D);
        telemetry.addData("Step Size: ", stepSizes[stepIndex]);
        telemetry.addLine("Y: switches target pose\nB: increase step size");

        //P 75

        //F 20.16   (20.4 for 800 vel)
    }
}


