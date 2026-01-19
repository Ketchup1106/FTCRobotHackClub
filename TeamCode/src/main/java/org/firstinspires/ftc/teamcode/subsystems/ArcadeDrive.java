package org.firstinspires.ftc.teamcode.subsystems;


import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;


public class ArcadeDrive {
    private DcMotorEx frontLeftDrive;
    private DcMotorEx backLeftDrive;
    private DcMotorEx frontRightDrive;
    private DcMotorEx backRightDrive;

    double frontLeftPower;
    double frontRightPower;
    double backLeftPower;
    double backRightPower;

    public void init(HardwareMap hwMap){
        frontLeftDrive = hwMap.get(DcMotorEx.class, "lf");
        backLeftDrive = hwMap.get(DcMotorEx.class, "lb");
        frontRightDrive = hwMap.get(DcMotorEx.class, "rf");
        backRightDrive = hwMap.get(DcMotorEx.class, "rb");


        frontLeftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);


        frontLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        backLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        frontRightDrive.setDirection(DcMotor.Direction.FORWARD);
        backRightDrive.setDirection(DcMotor.Direction.FORWARD);


        frontLeftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }


    public void drive(double axial, double lateral, double yaw, double powerMult){

        frontLeftPower = (axial + lateral + yaw);
        frontRightPower = (axial - lateral - yaw);
        backLeftPower = (axial - lateral + yaw);
        backRightPower = (axial + lateral - yaw);

        double max = 1;
        max = Math.max(max, Math.abs(frontLeftPower));
        max = Math.max(max, Math.abs(frontRightPower));
        max = Math.max(max, Math.abs(backLeftPower));
        max = Math.max(max, Math.abs(backRightPower));

        frontLeftPower /= max;
        frontRightPower /= max;
        backLeftPower /= max;
        backRightPower /= max;

        frontLeftDrive.setPower(powerMult *frontLeftPower);
        frontRightDrive.setPower(powerMult *frontRightPower);
        backLeftDrive.setPower(powerMult *backLeftPower);
        backRightDrive.setPower(powerMult *backRightPower);


    }
    public double getFrontLeftDrive(){
        return frontLeftPower;
    }
    public double getFrontRightDrive(){
        return frontRightPower;
    }
    public double getBackLeftDrive(){
        return backLeftPower;
    }
    public double getBackRightDrive(){
        return backRightPower;
    }


}

