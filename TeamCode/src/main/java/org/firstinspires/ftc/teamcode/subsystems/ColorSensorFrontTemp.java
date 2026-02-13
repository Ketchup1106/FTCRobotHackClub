package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class ColorSensorFrontTemp {
    NormalizedColorSensor colorSensor1;


    public enum detectedColor {
        PURPLE,
        GREEN,
        UNKNOWN
    }

    public void init(HardwareMap hwMap){
        colorSensor1 = hwMap.get(NormalizedColorSensor.class, "color1");
        colorSensor1.setGain(250);
    }

    float normRed, normGreen, normBlue;

    public detectedColor getDetectedColor(){
        NormalizedRGBA colors = colorSensor1.getNormalizedColors();



        normRed = colors.red;
        normGreen = colors.green;
        normBlue = colors.blue;


        if(getDist() < 1.6){ // close
            if(normRed < .2022 && normRed > .1355 && normBlue < .6256 && normBlue > .3815 && normGreen < .7439 && normGreen > .4807){
                return detectedColor.GREEN;
            }
//            else if(normRed < .3777 && normRed > .2174 && normBlue < .9384 && normBlue > .5188 && normGreen < .5188 && normGreen > .3281){
//                return detectedColor.PURPLE;
//            }
            return detectedColor.PURPLE;
        }
        else if(getDist() > 1.6 && getDist() < 2.5){ //further balls
            if(normRed < .1355 && normRed > .0916 && normBlue < .2747 && normBlue > .1755 && normGreen < .4349 && normGreen > .2213){
                return detectedColor.GREEN;
            }
//            else if(normRed < .2365 && normRed > .1259 && normBlue < .5417 && normBlue > .2747 && normGreen < .3433 && normGreen > .2022){
//                return detectedColor.PURPLE;
//            }
            return detectedColor.PURPLE;
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
        return ((DistanceSensor)colorSensor1).getDistance(DistanceUnit.INCH);
    }

}
