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


        if(normRed < .2861 && normRed > .1526 && normBlue < .8011  && normBlue > .3586 && normGreen > .4616 && (getDist() < 2)){
            return detectedColor.GREEN;
        }
        else if(normRed < .4959 && normRed > .2174 && normBlue < .9499 && normBlue > .4005 && normGreen < .6065 && normGreen > .3052 && (getDist() < 2)){
            return detectedColor.PURPLE;
        }
        else{
            return detectedColor.UNKNOWN;
        }
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
