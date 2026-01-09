package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.subsystems.ColorSensorBackTemp;
import org.firstinspires.ftc.teamcode.subsystems.ColorSensorFrontTemp;
import org.firstinspires.ftc.teamcode.subsystems.TestDexer;

@TeleOp (name = "spindexer test")
public class SpinDexerTest extends OpMode {
    ColorSensorFrontTemp frontSensor = new ColorSensorFrontTemp();
    ColorSensorBackTemp backSensor = new ColorSensorBackTemp();
    TestDexer spindexer = new TestDexer();



    @Override
    public void init() {
        frontSensor.init(hardwareMap);
        backSensor.init(hardwareMap);
        spindexer.init(hardwareMap);
    }

    @Override
    public void loop(){
        telemetry.addData("Color Sensor Front: ", frontSensor.getDetectedColor());
        if(frontSensor.getDetectedColor() == ColorSensorFrontTemp.detectedColor.GREEN){
            spindexer.setPower(0.5);
            spindexer.assignColorToPosition();
        }
        else if(frontSensor.getDetectedColor() == ColorSensorFrontTemp.detectedColor.PURPLE){
            spindexer.setPower(-0.5);
            spindexer.assignColorToPosition();
        }
        else{
            spindexer.setPower(0);
        }

        if(backSensor.getDetectedColor() == ColorSensorBackTemp.detectedColor.GREEN){

            spindexer.setPower(0.5);
            spindexer.assignColorToPosition();
        }
        else if(backSensor.getDetectedColor() == ColorSensorBackTemp.detectedColor.PURPLE){

            spindexer.setPower(-0.5);
            spindexer.assignColorToPosition();
        }
        else{
            spindexer.setPower(0);
        }
    }
}
