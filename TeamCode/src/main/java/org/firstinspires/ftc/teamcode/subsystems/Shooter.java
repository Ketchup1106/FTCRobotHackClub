package org.firstinspires.ftc.teamcode.subsystems;


import com.pedropathing.math.MathFunctions;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

//our next step here should be to create an algorithm that takes distance and adjusts the shooter pid values and angle
public class Shooter {

    private Telemetry telemetry;
    private final double FEED_TIME_SECONDS = 0.2;

    private double ticksPerRotation = 359;
    private double maxTicksPerMinute = 2154000;
    private double maxTicksPerSecond = maxTicksPerMinute/60;

    double amountToShoot;

    private DcMotorEx shooter1, shooter2;
    private Servo feeder;
    public boolean isActive = false;
    public int numShot = 1;
    private Intake intake = new Intake();

    public void init(HardwareMap hwMap, Telemetry telemetry){
        shooter1 = hwMap.get(DcMotorEx.class, "sm1");
        shooter2 = hwMap.get(DcMotorEx.class, "sm2");
        feeder = hwMap.get(Servo.class, "liftarm2");
//        intake = hwMap.get(DcMotorEx.class, "intake");
//        intake.setDirection(DcMotorSimple.Direction.FORWARD);
        shooter1.setDirection(DcMotorSimple.Direction.FORWARD);
        shooter2.setDirection(DcMotorSimple.Direction.FORWARD);

        shooter1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        shooter2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        this.telemetry = telemetry;
        shooter1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        shooter2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);




        shooter1.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(
                85, 0, 0, 20.4
        )); // 2.75 10
        shooter2.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(
                85    , 0, 0, 20.4
        )); //2.75 10




//        intake.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(
//                1, 0, 0, 10
//        ));
        feeder.setDirection(Servo.Direction.REVERSE);

        intake.init(hwMap);


        launchState = LaunchState.IDLE;
        stopFeeder();
        stopLauncher();
    }



    ElapsedTime feederTimer = new ElapsedTime();
    private final double lowPos = .52;
    private final double highPos = .31;


    final double targetVel = 820; //.052   //820


    public enum LaunchState{
        IDLE,
        SPIN_UP,
        LAUNCH,
        RECOVER
    }
    private LaunchState launchState;

    public void updateState(double velocity){
        switch(launchState){
            case IDLE:
                isActive = false;
                intake.stop();
                stopAll();
                break;
            case SPIN_UP:
                if(numShot == 1){
                    shooter1.setVelocity(-velocity);
                    shooter2.setVelocity(velocity);
                }if(numShot > 1){
                intake.runReverse();
            }
                if ((Math.abs(shooter1.getVelocity()) > targetVel - 21)){ //this will now wait for the motors BEFORE moving to launch
                    launchState = LaunchState.LAUNCH;
                    feederTimer.reset();
                }
                break;
            case LAUNCH:
                feeder.setPosition(highPos);
                launchState = LaunchState.RECOVER;
                feederTimer.reset();
                break;
            case RECOVER:

                if(feederTimer.seconds() < .3){ //maybe decrease
                    break;
                }
                feeder.setPosition(lowPos);
                intake.runReverse();

                if(amountToShoot == 1 || numShot == 3){
                    launchState = LaunchState.IDLE;
                    break;
                }

                if(feederTimer.seconds() < 1.2){ //decrease this as well
                    break;
                }
                intake.stop();
                numShot++;
                launchState = LaunchState.SPIN_UP;
                break;

        }
    }
    public void shoot3(){
        numShot = 1;
        isActive = true;
        launchState = LaunchState.SPIN_UP;
        amountToShoot = 3;
        feederTimer.reset();
    }
    public void shoot1(){
        isActive = true;
        numShot = 1;
        amountToShoot = 1;
        feederTimer.reset();
        launchState = LaunchState.SPIN_UP;
    }
    public void startFeeder(){
        feeder.setPosition(highPos);
    }
    public void stopFeeder(){
        feeder.setPosition(lowPos);
    }
    public void stopLauncher(){
        shooter1.setPower(0);
        shooter2.setPower(0);
        shooter1.setVelocity(0);
        shooter2.setVelocity(0);
    }

    public void spinUp(){
        shooter1.setVelocity(-targetVel);
        shooter2.setVelocity(targetVel);
    }
    public void stopAll(){
        intake.stop();
        stopFeeder();
        stopLauncher();
    }
    public double getAmountTOShoot(){
        return amountToShoot;
    }
    public double getVelocity1(){
        return shooter1.getVelocity();
    }
    public double getVelocity2(){
        return shooter2.getVelocity();
    }
    public double getServo(){
        return feeder.getPosition();
    }
    public LaunchState getLauunchState(){
        return launchState;
    }

    public double setVel(double distance){
        return MathFunctions.clamp(
                (0.0252784*Math.pow(distance, 2)) //0.0252784x^2
                        -(0.612831*distance) // 0.612831x
                        +(779.81988) //779.81988
                ,0, 1400);
    }
}




