package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.subsystems.Turret;


@TeleOp(name = "turretTest")
public class turretTest extends OpMode {

    Turret turret = new Turret();

    @Override
    public void init(){
        turret.init(hardwareMap);
    }

    @Override
    public void loop(){

        if(gamepad1.aWasPressed()){
            turret.rotate();
        }

    }

}
