package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class ColorSensorBackTemp {
    NormalizedColorSensor colorSensor2;


    public enum detectedColor {
        PURPLE,
        GREEN,
        UNKNOWN
    }

    public void init(HardwareMap hwMap){
        colorSensor2 = hwMap.get(NormalizedColorSensor.class, "color1");
        colorSensor2.setGain(4);
    }

    public detectedColor getDetectedColor(Telemetry telemetry){
        NormalizedRGBA colors = colorSensor2.getNormalizedColors();

        float normRed, normGreen, normBlue;

        normRed = colors.red/colors.alpha;
        normGreen = colors.green/colors.alpha;
        normBlue = colors.blue/colors.alpha;

        telemetry.addData("red", normRed);
        telemetry.addData("blue", normBlue);
        telemetry.addData("green", normGreen);


        /*
        green - .01 - .06; .04 - .1; .04 - .1;
        purple - .02 - .07; .04 - .11; .02 - .1         */

        if(normRed > .01 && normRed < .06 && normBlue > .04 && normBlue < .1 && normGreen > .04 && normGreen < .1){
            return detectedColor.GREEN;
        }
        else if(normRed > .02 && normRed < .07 && normBlue > .04 && normBlue < .11 && normGreen > .02 && normGreen < .1){
            return detectedColor.PURPLE;
        }
        else{
            return detectedColor.UNKNOWN;
        }
    }

}
