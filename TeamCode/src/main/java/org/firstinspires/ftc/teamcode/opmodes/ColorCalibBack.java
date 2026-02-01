package org.firstinspires.ftc.teamcode.opmodes;


import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.subsystems.ColorSensorBackTemp;


@TeleOp(name = "Color Calibration Back")

public class ColorCalibBack extends OpMode {
    ColorSensorBackTemp color = new ColorSensorBackTemp();

    double minValRed = Integer.MAX_VALUE;
    double maxValRed = Integer.MIN_VALUE;

    double minValBlue = Integer.MAX_VALUE;
    double maxValBlue = Integer.MIN_VALUE;

    double minValGreen = Integer.MAX_VALUE;
    double maxValGreen = Integer.MIN_VALUE;

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

        if(color.getRed() < minValRed){
            minValRed = color.getRed();
        }
        if(color.getBlue() < minValBlue){
            minValBlue = color.getBlue();
        }
        if(color.getGreen() < minValGreen){
            minValGreen = color.getGreen();
        }
        if(color.getRed() > maxValRed){
            maxValRed = color.getRed();
        }
        if(color.getBlue() > maxValBlue){
            maxValBlue = color.getBlue();
        }
        if(color.getGreen() > maxValGreen){
            maxValGreen = color.getGreen();
        }
        telemetry.addData("Red: ", color.getRed());
        telemetry.addData(" ", " ");
        telemetry.addData("Max Red: ", maxValRed);
        telemetry.addData(" ", " ");
        telemetry.addData("Min Red: ", minValRed);
        telemetry.addData(" ", " ");
        telemetry.addData("Blue: ", color.getBlue());
        telemetry.addData(" ", " ");
        telemetry.addData("Max Blue: ", maxValBlue);
        telemetry.addData(" ", " ");
        telemetry.addData("Min Blue: ", minValBlue);
        telemetry.addData(" ", " ");
        telemetry.addData("Green: ", color.getGreen());
        telemetry.addData(" ", " ");
        telemetry.addData("Max Green: ", maxValGreen);
        telemetry.addData(" ", " ");
        telemetry.addData("Min Green: ", minValGreen);
        telemetry.addData("doistance", color.getDist());
    }
}

