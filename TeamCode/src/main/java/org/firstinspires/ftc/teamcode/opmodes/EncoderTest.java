package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.robotcore.external.Telemetry;
@TeleOp(name = "encoder test")
public class EncoderTest extends OpMode {
    DcMotorEx encoder;
    public void init(){
        encoder = hardwareMap.get(DcMotorEx.class, "intake");
        encoder.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        encoder.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
    }
    public void loop(){
        telemetry.addData("pos", encoder.getCurrentPosition());
    }
}
