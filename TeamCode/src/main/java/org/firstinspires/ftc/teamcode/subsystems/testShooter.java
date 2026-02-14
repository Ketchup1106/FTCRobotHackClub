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
        shooter1.setDirection(DcMotorEx.Direction.REVERSE);
        shooter2.setDirection(DcMotorEx.Direction.FORWARD);
        
        //transferServo.setDirection(Servo.Direction.FORWARD);

        shooter1.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        shooter2.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);

        this.telemetry = telemetry;
        shooter1.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        shooter2.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        hoodAngle.setDirection(Servo.Direction.REVERSE);



        shooter1.setPIDFCoefficients(DcMotorEx.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(
                180, 0, 0, 17.7
        )); // 2.75 10
        shooter2.setPIDFCoefficients(DcMotorEx.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(
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

    public void updateState(double velocity, double spinPos, double targetPos){

        switch(launchState){

            case IDLE:
                spinUp(1300);
                if(isActive) {
                    isActive = false;
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
                if(MathFunctions.roughlyEquals(spinPos, targetPos, 100)) {
                    launchState = LaunchState.RECOVER;
                }
                break;
            case RECOVER:
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
                (6.25175*distance)+726.08392
                ,0, 2000);
    }
    public void setHood(double distance){
        hoodAngle.setPosition(
                MathFunctions.clamp(
                ((3.32168*(1/(Math.pow(10, 8)))) * Math.pow(distance, 4)) -
                (0.0000116822* Math.pow(distance, 3)) +
                (0.00132861*Math.pow(distance, 2)) -
                (0.0435148*distance) +
                (0.350932),
                0, 1));
    }
}




