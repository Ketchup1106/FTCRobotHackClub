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
public class testShooter {

    private Telemetry telemetry;
    private final double FEED_TIME_SECONDS = 0.2;

    private double ticksPerRotation = 359;
    private double maxTicksPerMinute = 2154000;
    private double maxTicksPerSecond = maxTicksPerMinute/60;
    
    double amountToShoot;

    private DcMotorEx shooter1, shooter2;
    public boolean isActive = false;
    public int numShot = 1;
    private Intake intake = new Intake();

    //public Servo transferServo;
    public Servo hoodAngle;

    public void init(HardwareMap hwMap, Telemetry telemetry){
        shooter1 = hwMap.get(DcMotorEx.class, "sm1");
        shooter2 = hwMap.get(DcMotorEx.class, "sm2");
        
        //transferServo = hwMap.get(Servo.class, "transferServo");
        hoodAngle = hwMap.get(Servo.class, "hoodAngle");
        shooter1.setDirection(DcMotorSimple.Direction.REVERSE);
        shooter2.setDirection(DcMotorSimple.Direction.FORWARD);
        
        //transferServo.setDirection(Servo.Direction.FORWARD);

        shooter1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        shooter2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        this.telemetry = telemetry;
        shooter1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        shooter2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        hoodAngle.setDirection(Servo.Direction.REVERSE);



        shooter1.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(
                180, 0, 0, 17.7
        )); // 2.75 10
        shooter2.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(
                180    , 0, 0, 17.7
        )); //2.75 10

        intake.init(hwMap);

        launchState = LaunchState.IDLE;
//        stopFeeder();
        stopLauncher();
    }



    ElapsedTime feederTimer = new ElapsedTime();
    private final double lowPos = .52;
    private final double highPos = .31;


    final double targetVel = 820;


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
                if(isActive) {
                    isActive = false;
                    stopAll();
                }
                break;
            case SPIN_UP:
                spinUp(velocity);
                if ((Math.abs(shooter1.getVelocity()) > velocity - 21) && Math.abs(shooter1.getVelocity()) < velocity + 21){ //this will now wait for the motors BEFORE moving to launch
                    launchState = LaunchState.LAUNCH;
                    feederTimer.reset();
                }
                break;
            case LAUNCH:
                //transferServo.setPosition(lowPos);
                if(feederTimer.seconds()>2.5) {
                    launchState = LaunchState.RECOVER;
                }
                break;
            case RECOVER:
                stopAll();
                launchState = LaunchState.IDLE;
                break;
        }
    }
    public void shoot3(){
        isActive = true;
        launchState = LaunchState.SPIN_UP;
        //add spindexer func
        feederTimer.reset();
    }
    /*
       public void shoot1(){
           isActive = true;
           numShot = 1;
           amountToShoot = 1;
           feederTimer.reset();
           launchState = LaunchState.SPIN_UP;
       }
       */
//    public void startFeeder(){
//        feeder.setPosition(highPos);
//    }
//    public void stopFeeder(){
//        feeder.setPosition(lowPos);
//    }
    public void stopLauncher(){
        shooter1.setPower(0);
        shooter2.setPower(0);
        shooter1.setVelocity(0);
        shooter2.setVelocity(0);
    }

    public void spinUp(double velocity){
        shooter1.setVelocity(velocity);
        shooter2.setVelocity(velocity);
    }
    public void stopAll(){
//        stopFeeder();
        stopLauncher();
        //transferServo.setPosition(highPos);
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
    public LaunchState getLauunchState(){
        return launchState;
    }

    public double setVel(double distance){
        return MathFunctions.clamp(
                (0.00320771*Math.pow(distance, 2)) +
                        (5.16681*distance) +
                        (833.26389)
                ,0, 1800);
    }
    public void setHood(double distance){
        hoodAngle.setPosition(
                MathFunctions.clamp(
                (-(5.97813*(1/(Math.pow(10, 8)))) * Math.pow(distance, 4)) +
                (0.0000204064* Math.pow(distance, 3)) -
                (0.00250008*Math.pow(distance, 2)) +
                (0.141251*distance) -
                (2.8031),
                0, 1));
    }
}




