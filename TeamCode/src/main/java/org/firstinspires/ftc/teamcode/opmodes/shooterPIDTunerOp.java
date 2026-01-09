package org.firstinspires.ftc.teamcode.opmodes;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.subsystems.AprilTagStuff;
import org.firstinspires.ftc.teamcode.subsystems.ArcadeDrive;
import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.TouchySensor;
import org.firstinspires.ftc.teamcode.subsystems.testShooter;
import org.firstinspires.ftc.teamcode.subsystems.testShooter;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;


@TeleOp(name = "shoter pid tuner")
@Disabled
public class shooterPIDTunerOp extends OpMode {

    public DcMotorEx shooter1;
    public DcMotorEx shooter2;

    public Servo hoodAngle;

    double low = 600;
    double high = 1200;

    double hoodAnglePos = 0;

    double currTargetVelocity = high;

    double F = 0;

    double P = 0;

    double[] stepSizes = {10.0, 1.0, 0.1, 0.001, 0.0001};
    int stepIndex = 1;

    @Override
    public void init(){
        shooter1 = hardwareMap.get(DcMotorEx.class, "sm1");
        shooter2 = hardwareMap.get(DcMotorEx.class, "sm2");

        hoodAngle = hardwareMap.get(Servo.class, "hoodAngle");

        shooter1.setDirection(DcMotorSimple.Direction.REVERSE);
        shooter2.setDirection(DcMotorSimple.Direction.REVERSE);

        shooter1.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        shooter2.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);


        PIDFCoefficients pidfCoefficients = new PIDFCoefficients(P, 0, 0, F);
        shooter1.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidfCoefficients);
        shooter2.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidfCoefficients);

        telemetry.addLine("Init Complete");

    }


    @Override
    public void loop(){
        if(gamepad1.yWasPressed()){
            if(currTargetVelocity == high){
                currTargetVelocity= low;
            }
            else{
                currTargetVelocity = high;
            }
        }
        if(gamepad1.bWasPressed()){
            stepIndex = (stepIndex + 1) % stepSizes.length;
        }
        if(gamepad1.dpadLeftWasPressed()){
            F -= stepSizes[stepIndex];
        }
        if(gamepad1.dpadRightWasPressed()){
            F += stepSizes[stepIndex];
        }
        if(gamepad1.dpadUpWasPressed()){
            P += stepSizes[stepIndex];
        }
        if(gamepad1.dpadDownWasPressed()){
            P -= stepSizes[stepIndex];
        }

        if(gamepad1.xWasPressed()){
            hoodAnglePos += stepSizes[stepIndex];
            hoodAngle.setPosition(hoodAnglePos);
        }


        PIDFCoefficients pidfCoefficients = new PIDFCoefficients(P, 0, 0, F);
        shooter1.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidfCoefficients);
        shooter2.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidfCoefficients);

        shooter1.setVelocity(currTargetVelocity);
        shooter2.setVelocity(currTargetVelocity);

        double curVel1 = shooter1.getVelocity();

        double curVel2 = shooter2.getVelocity();

        double error1 = currTargetVelocity - curVel1;

        double error2 = currTargetVelocity - curVel2;



        telemetry.addData("target Velocity: ", currTargetVelocity);
        telemetry.addData("Current sm1 Velocity: ", curVel1);
        telemetry.addData("Current sm2 Vel: ", curVel2);
        telemetry.addData("Error sm1: ", error1);
        telemetry.addData("Error sm2: ", error2);
        telemetry.addData("Tuning P: ", P);
        telemetry.addData("Tuning F: ", F);
        telemetry.addData("Step Size: ", stepSizes[stepIndex]);
        telemetry.addLine("Y: switches velocity\nB: increase step size");

        //P 75

        //F 20.16   (20.4 for 800 vel)
    }
}


