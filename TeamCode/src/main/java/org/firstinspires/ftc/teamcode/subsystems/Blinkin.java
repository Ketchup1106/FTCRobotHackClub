package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Blinkin {
    RevBlinkinLedDriver lights;
    public void init(HardwareMap hardwareMap){
        lights = hardwareMap.get(RevBlinkinLedDriver.class, "lights");
        lights.setPattern(RevBlinkinLedDriver.BlinkinPattern.BLACK);
    }

    public void setPatternOnePurp(){
        lights.setPattern(RevBlinkinLedDriver.BlinkinPattern.CP1_END_TO_END_BLEND_TO_BLACK);
    }

    public void setPatternTwoPurp(){
        lights.setPattern(RevBlinkinLedDriver.BlinkinPattern.VIOLET);
    }

    public void setPatternOneGreen(){
        lights.setPattern(RevBlinkinLedDriver.BlinkinPattern.CP2_END_TO_END_BLEND_TO_BLACK);
    }

    public void setPatternTwoGreen(){
        lights.setPattern(RevBlinkinLedDriver.BlinkinPattern.GREEN);
    }

    public void setPatternMixed(){
        lights.setPattern(RevBlinkinLedDriver.BlinkinPattern.CP1_2_END_TO_END_BLEND_1_TO_2);
    }
}
