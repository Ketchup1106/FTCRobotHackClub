package org.firstinspires.ftc.teamcode.opmodes;


import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;


import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.subsystems.Color;
import org.firstinspires.ftc.teamcode.subsystems.ColorSensorFrontTemp;


@TeleOp(name = "servo booster tets")

public class servoBoostTest extends OpMode {
    Servo test;
    boolean isAtFar = true;
    double pos = 0;

    @Override
    public void init(){
        test = hardwareMap.get(Servo.class, "turret");
        test.scaleRange(0.56, .95);
        test.setDirection(Servo.Direction.REVERSE);
    }

    //0 is 180, .45 is 0

    @Override
    public void loop(){
       if(gamepad1.aWasPressed()){
           pos += .01;
       }
       if(gamepad1.bWasPressed()){
           pos -= .01;
       }
       test.setPosition(pos);
       telemetry.addData("pos", pos);
    }
}

