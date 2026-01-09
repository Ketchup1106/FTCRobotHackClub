package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class ColorSensorFrontTemp {
    NormalizedColorSensor colorSensor1;


    public enum detectedColor {
        PURPLE,
        GREEN,
        UNKNOWN
    }

    public void init(HardwareMap hwMap){
        colorSensor1 = hwMap.get(NormalizedColorSensor.class, "color1");
        colorSensor1.setGain(50);
    }

    public detectedColor getDetectedColor(){
        NormalizedRGBA colors = colorSensor1.getNormalizedColors();

        float normRed, normGreen, normBlue;

        normRed = colors.red/colors.alpha;
        normGreen = colors.green/colors.alpha;
        normBlue = colors.blue/colors.alpha;



        if(normRed > 0.18 && normRed < .3 && normBlue > .56 && normBlue < 0.8 && normGreen > .67){
            return detectedColor.GREEN;
        }
        else if(normRed > 0.28 && normRed < .44 && normBlue > .68 && normGreen < 0.55 && normGreen > 0.4){
            return detectedColor.PURPLE;
        }
        else{
            return detectedColor.UNKNOWN;
        }
    }

}
