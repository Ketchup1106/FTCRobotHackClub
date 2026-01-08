package org.firstinspires.ftc.teamcode.opmodes;


import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;


import org.firstinspires.ftc.teamcode.subsystems.Color;
import org.firstinspires.ftc.teamcode.subsystems.ColorSensorFrontTemp;


@TeleOp(name = "Color Calibration Front")
public class ColorCalibFront extends OpMode {
    ColorSensorFrontTemp color = new ColorSensorFrontTemp();


    @Override
    public void init(){
        color.init(hardwareMap);
    }

    /*
    Green:
    R - <0.4
    G - >0.85
    B - >0.7

    Purple:

    R -
    G -
    B -

     */

    @Override
    public void loop(){
        color.getDetectedColor(telemetry);
    }
}

