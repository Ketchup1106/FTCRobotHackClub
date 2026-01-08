package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.subsystems.ColorSensorFrontTemp;
import org.firstinspires.ftc.teamcode.subsystems.TestDexer;

@TeleOp (name = "spindexer test")
public class SpinDexerTest extends OpMode {
    ColorSensorFrontTemp frontSensor;
    ColorSensorFrontTemp backSensor;
    TestDexer spindexer;
    public void init(HardwareMap hwMap){
        frontSensor.init(hwMap);
        backSensor.init(hwMap);
        spindexer.init(hwMap);
    }
    public void loop(){
        spindexer.
    }
}
