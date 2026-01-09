package org.firstinspires.ftc.teamcode.opmodes;


import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.subsystems.ColorSensorBackTemp;


@TeleOp(name = "Color Calibration Back")
@Disabled
public class ColorCalibBack extends OpMode {
    ColorSensorBackTemp color = new ColorSensorBackTemp();


    @Override
    public void init(){
        color.init(hardwareMap);
    }
/*
    Green:
    R -
    G -
    B -

    Purple:

    R -
    G -
    B -

     */

    @Override
    public void loop(){
        color.getDetectedColor();

    }
}

