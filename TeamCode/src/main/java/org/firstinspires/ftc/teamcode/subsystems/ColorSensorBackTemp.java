package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class ColorSensorBackTemp {
    NormalizedColorSensor colorSensor2;


    public enum detectedColor {
        PURPLE,
        GREEN,
        UNKNOWN
    }

    public void init(HardwareMap hwMap){
        colorSensor2 = hwMap.get(NormalizedColorSensor.class, "color2");
        colorSensor2.setGain(200);
    }

    float normRed, normGreen, normBlue;

    public detectedColor getDetectedColor(){
        NormalizedRGBA colors = colorSensor2.getNormalizedColors();

        normRed = colors.red;
        normGreen = colors.green;
        normBlue = colors.blue;
        if(getDist() < 1.3) {
            if (normRed < .3143 && normRed > .1526 && normBlue < .8881 && normBlue > .6226 && normGreen > .8) {
                return detectedColor.GREEN;
            } else if (normRed < .4959 && normRed > .3021 && normBlue < .9499 && normBlue > .5676 && normGreen < .6165 && normGreen > .3052) {
                return detectedColor.PURPLE;
            }
            //return detectedColor.PURPLE;
            //}
        }
        else if(getDist() > 1.3 && getDist() < 2){
            if (normRed < .2014 && normRed > .1343 && normBlue < .4913 && normBlue > .3052 && normGreen < .647 && normGreen > .3052) {
                return detectedColor.GREEN;
            } else if (normRed < .4059 && normRed > .2174 && normBlue < .7782 && normBlue > .4005 && normGreen < .5219 && normGreen > .3052) {
                return detectedColor.PURPLE;
            }
        }
        return detectedColor.UNKNOWN;

    }


    public float getRed(){
        return normRed;
    }
    public float getBlue(){
        return normBlue;
    }
    public float getGreen(){
        return normGreen;
    }
    public double getDist(){
        return ((DistanceSensor)colorSensor2).getDistance(DistanceUnit.INCH);
    }

}
