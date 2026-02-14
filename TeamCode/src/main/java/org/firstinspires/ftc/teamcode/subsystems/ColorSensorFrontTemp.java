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
        colorSensor1.setGain(140);
    }

    float normRed, normGreen, normBlue;

    public detectedColor getDetectedColor(){
        NormalizedRGBA colors = colorSensor1.getNormalizedColors();



        normRed = colors.red;
        normGreen = colors.green;
        normBlue = colors.blue;


        if(getDist() < 1.35){ // close
            if(normRed < .267 && normRed > .1355 && normBlue < .5682 && normBlue > .8588 && normGreen <= 1 && normGreen > .752){
                return detectedColor.GREEN;
            }
            else if(normRed < .5362 && normRed > .3653 && normBlue < .6708 && normBlue > .4486 && normGreen < .5896 && normGreen > .4059){
                return detectedColor.PURPLE;
            }
            //return detectedColor.PURPLE;
        }
        else if(getDist() > 1.35 && getDist() < 2.5){ //further balls
            if(normRed < .1431 && normRed > .0897 && normBlue < .2841 && normBlue > .1645 && normGreen < .5042 && normGreen > .2841){
                return detectedColor.GREEN;
            }
            else if(normRed < .3781 && normRed > .1559 && normBlue < .455 && normBlue > .1944 && normGreen < .4443 && normGreen > .1987){
                return detectedColor.PURPLE;
            }
            //return detectedColor.PURPLE;
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
