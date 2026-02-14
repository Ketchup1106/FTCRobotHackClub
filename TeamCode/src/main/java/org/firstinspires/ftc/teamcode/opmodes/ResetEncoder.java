package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class ResetEncoder extends OpMode {
    public DcMotorEx encoder;
    public void init(){
        encoder = hardwareMap.get(DcMotorEx.class, "backIntake");
        encoder.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        encoder.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);

    }
    public void init_loop(){
        telemetry.addLine("you can leave now");
    }
    public void loop(){
        terminateOpModeNow();
    }
}
