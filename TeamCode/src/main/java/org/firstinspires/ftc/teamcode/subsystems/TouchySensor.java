package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.TouchSensor;

public class TouchySensor {

    DigitalChannel toucher;
    public void init(HardwareMap hwMap){
        toucher = hwMap.get(DigitalChannel.class, "touchy1");

    }
    public boolean detectTouch(){
        return toucher.getState();
    }
}
