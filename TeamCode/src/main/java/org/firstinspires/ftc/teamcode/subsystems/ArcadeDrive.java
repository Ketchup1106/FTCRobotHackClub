package org.firstinspires.ftc.teamcode.subsystems;


import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;


public class ArcadeDrive {
    private DcMotorEx frontLeftDrive;
    private DcMotorEx backLeftDrive;
    private DcMotorEx frontRightDrive;
    private DcMotorEx backRightDrive;







    private double SPIN_DAMPING = 2.0; //higher means slower turning


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


//        double frontLeftPower = (axial + lateral + (yaw * powerMult));
//        double frontRightPower = (axial - lateral - (yaw * powerMult));
//        double backLeftPower = (axial - lateral + (yaw * powerMult));
//        double backRightPower = (axial + lateral - (yaw * powerMult));

        double frontLeftPower = (axial + lateral + yaw);
        double frontRightPower = (axial - lateral - yaw);
        double backLeftPower = (axial - lateral + yaw);
        double backRightPower = (axial + lateral - yaw);

        double max;
        max = Math.max(Math.abs(frontLeftPower), Math.abs(frontRightPower));
        max = Math.max(max, Math.abs(backLeftPower));
        max = Math.max(max, Math.abs(backRightPower));


        if (max > 1.0) {
            frontLeftPower /= max;
            frontRightPower /= max;
            backLeftPower /= max;
            backRightPower /= max;
        }
//        frontLeftPower *= powerMult;
//        frontRightPower *= powerMult;
//        backLeftPower *= powerMult;
//        backRightPower*=powerMult;

        frontLeftDrive.setPower(frontLeftPower);
        frontRightDrive.setPower(frontRightPower);
        backLeftDrive.setPower(backLeftPower);
        backRightDrive.setPower(backRightPower);


    }


}

