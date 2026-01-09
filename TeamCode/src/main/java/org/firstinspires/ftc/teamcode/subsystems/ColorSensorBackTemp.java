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
        colorSensor2 = hwMap.get(NormalizedColorSensor.class, "color2");
        colorSensor2.setGain(50);
    }

    public detectedColor getDetectedColor(){
        NormalizedRGBA colors = colorSensor2.getNormalizedColors();

        float normRed, normGreen, normBlue;

        normRed = colors.red/colors.alpha;
        normGreen = colors.green/colors.alpha;
        normBlue = colors.blue/colors.alpha;


        if(normRed < 0.3 && normBlue < 0.67  && normBlue > 0.5 && normGreen > 0.7){
            return detectedColor.GREEN;
        }
        else if(normRed > .3 && normRed < .41 && normBlue > .58 && normBlue < .72 && normGreen > .37 && normGreen < .52){
            return detectedColor.PURPLE;
        }
        else{
            return detectedColor.UNKNOWN;
        }
    }

}
