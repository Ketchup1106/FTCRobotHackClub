package org.firstinspires.ftc.teamcode.opmodes;


import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;


import org.firstinspires.ftc.teamcode.subsystems.Color;
import org.firstinspires.ftc.teamcode.subsystems.ColorSensorFrontTemp;


@TeleOp(name = "Color Calibration")
public class ColorCalib extends OpMode {
    ColorSensorFrontTemp color = new ColorSensorFrontTemp();


    @Override
    public void init(){
        color.init(hardwareMap);
    }


    @Override
    public void loop(){
        color.getDetectedColor(telemetry);
    }
}

