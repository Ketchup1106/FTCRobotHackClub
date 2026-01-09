package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;


@TeleOp(name = "random shitter")
@Disabled
public class randomShooter extends OpMode {

    public DcMotorEx shooter1;
    public DcMotorEx shooter2;

    private Servo hood;
    double low = 0;
    double high = 500;

    private final double lowPos = .52;
    private final double highPos = .31;
    boolean tuningservo = false;
    double targetServo = 0;


    double currTargetVelocity = high;

    double[] stepSizes = {100, 10, 1};
    double[] stepSizesServo = {0.1, 0.01, 0.001};
    int stepIndex = 0;
    int stepIndexServo = 0;

    @Override
    public void init(){
        shooter1 = hardwareMap.get(DcMotorEx.class, "sm1");
        shooter2 = hardwareMap.get(DcMotorEx.class, "sm2");

        shooter1.setDirection(DcMotorSimple.Direction.REVERSE);
        shooter2.setDirection(DcMotorSimple.Direction.REVERSE);

        shooter1.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        shooter2.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);

        PIDFCoefficients pidfCoefficients = new PIDFCoefficients(120, 0, 0, 17.7        );
        shooter1.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidfCoefficients);
        shooter2.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidfCoefficients);

        hood = hardwareMap.get(Servo.class, "hoodAngle");

        hood.setDirection(Servo.Direction.REVERSE);
        hood.setPosition(0);
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
        hood.setPosition(targetServo);
        shooter1.setVelocity(currTargetVelocity);
        shooter2.setVelocity(currTargetVelocity);

        if(gamepad1.bWasPressed()){
            stepIndex = (stepIndex + 1) % stepSizes.length;
            stepIndexServo = (stepIndexServo + 1) % stepSizesServo.length;
        }
        if(gamepad1.dpadRightWasPressed()){
            high += stepSizes[stepIndex];
        }
        if(gamepad1.dpadLeftWasPressed()){
            high -= stepSizes[stepIndex];
        }
        if(gamepad1.dpadUpWasPressed()){
            targetServo += stepSizesServo[stepIndexServo];
        }
        if(gamepad1.dpadDownWasPressed()){
            targetServo -= stepSizesServo[stepIndexServo];
        }

        double curVel1 = shooter1.getVelocity();

        double curVel2 = shooter2.getVelocity();




        telemetry.addData("target Velocity: ", currTargetVelocity);
        telemetry.addData("Current sm1 Vel: ", curVel1);
        telemetry.addData("Current sm2 Vel: ", curVel2);
        telemetry.addData("servoPos", targetServo);
        telemetry.addData("High Vel: ", high);
        telemetry.addData("Step Size: ", stepSizes[stepIndex]);

    }
}

