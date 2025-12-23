package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

public class Turret {
    public DcMotorEx turret;

    double ticksPerRadian = 537.6/(2 * Math.PI);

    public void init(HardwareMap hwMap){
        turret = hwMap.get(DcMotorEx.class, "tm");
        turret.setDirection(DcMotorSimple.Direction.FORWARD);
        //turret.setTargetPosition(0);
        turret.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        turret.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);;
        turret.setPIDFCoefficients(DcMotorEx.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(10, 0, 0, 0));
    }
    public void rotateToGoal(double goalAngle){
        turret.setTargetPosition((int)(goalAngle * ticksPerRadian));
    }

    public void rotate(){

        //turret.setTargetPosition(turret.getCurrentPosition() + 1);
        turret.setPower(1);
    }


}
