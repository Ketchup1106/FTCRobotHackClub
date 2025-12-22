package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Spindexer {

    public Servo s1;
    public Servo s2;

    public TouchySensor frontTouchy = new TouchySensor();
    public TouchySensor rearTouchy = new TouchySensor();

    ArrayList<Color> idli = new ArrayList<Color>();


    public void init(HardwareMap hwMap){
        s1 = hwMap.get(Servo.class, "spin1");
        s2 = hwMap.get(Servo.class, "spin2");

        s1.setDirection(Servo.Direction.FORWARD);
        s2.setDirection(Servo.Direction.REVERSE);
        frontTouchy.init(hwMap);
        rearTouchy.init(hwMap);

    }

    public void update(){
        idli.set(0, color1.getDetectedColor());
        idli.set(1, color2.getDetectedColor());
        idli.set(2, color3.getDetectedColor());
    }
    public void rotateToShoot(String order){
        String s = "";
        for(int i = 0; i < idli.size(); i++){
            if(idli.get(i).equals("Purple")){
                s += "P";
            }
            if(idli.get(i).equals("Green")){
                s += "G";
            }
        }
        if(s.equals(order)){
            return;
        }
        //ADD CONDITIONS FOR ROTATING

    }

    public boolean frontTouchyActive(){
        return frontTouchy.detectTouch();
    }
    public boolean rearTouchyActive(){
        return rearTouchy.detectTouch();
    }

    public void rotateToFront(){

    }
    public void rotateToBack(){

    }
}
