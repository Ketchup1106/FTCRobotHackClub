package org.firstinspires.ftc.teamcode.opmodes;

import android.util.Log;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.pedropathing.math.MathFunctions;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.subsystems.AprilTagStuff;
import org.firstinspires.ftc.teamcode.subsystems.ArcadeDrive;
import org.firstinspires.ftc.teamcode.subsystems.Intake;
//import org.firstinspires.ftc.teamcode.subsystems.Shooter;
import org.firstinspires.ftc.teamcode.subsystems.TestDexer;
import org.firstinspires.ftc.teamcode.subsystems.testShooter;
import org.firstinspires.ftc.teamcode.subsystems.turretServo;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;


@TeleOp(name = "FAR Blue Teleop")
public class testOp extends OpMode {

    double P = 1.4;

    double I = 0;

    double D = 0.00002593;

    ElapsedTime runtime = new ElapsedTime();
    ArcadeDrive drive = new ArcadeDrive();
    Follower follower;
    testShooter shooter = new testShooter();
    TestDexer testDexer = new TestDexer();
    turretServo turret = new turretServo();

    //SpinDexer spindexer = new SpinDexer();
    double goalX = 0;

    double goalY = 144;
    double turretXOffset;
    double turretYOffset;

    double disX;
    double disY;
    String tag = "spin power";

    Intake intake = new Intake();

    //TouchySensor touchy1 = new TouchySensor();
    //AprilTagStuff aprilTagStuff = new AprilTagStuff();


    boolean automatedDrive = false;

    double goalDist;
    double goalAngle;
    public String order = "None yet";
    double powerSetter = .2;
    double targetVel = 0;
    int ballCount = 0;
    int shoot = 0;
    public double robotHeading;
    double hoodAngle;
    double velocity;
    boolean locked = false;

    double desiredTurretAngle;
    String intakeSide = "front";
    //double[] stepSizes = {10.0, 1.0, 0.1, 0.001, 0.0001, 0.00001, 0.000001, 0.0000001, 0.00000001};
    //10000000 - 10
    //1000000 - 1
    //100000 - 0.1
    //10000 - 0.001
    //1000 - 0.0001
    //100 - 0.00001
    //10 - 0.000001
    //1 - 0.0000001
    //0.1 - 0.00000001

    // 8 clicks to go to lowest
    //increased by 6 at 8 clicks

    //P - 1.4
    //I - 0
    //D - 0.0000025
    int stepIndex = 0;


    boolean slowMode = false;
    boolean turningToShoot = false;
    boolean isCheckingForApril = true;
    ElapsedTime aprilTimer = new ElapsedTime();
    ElapsedTime spindexerDelayTimer = new ElapsedTime();
    boolean launched = false;
    boolean wentBack = false;

    TestDexer.SpinState lastSpinState;



    @Override
    public void init(){
        if(RobotConstants.side.equals("red")){
            goalX = 144;
        }
        drive.init(hardwareMap);
        shooter.init(hardwareMap, telemetry);
        intake.init(hardwareMap);
        follower = Constants.createFollower(hardwareMap);
        //follower.setStartingPose(new Pose(32, 135.5,  Math.toRadians(90)));
        follower.setStartingPose(RobotConstants.autoEnd);
        follower.poseTracker.update();
        //touchy1.init(hardwareMap);
        turret.init(hardwareMap);
        testDexer.init(hardwareMap);


    }
    public void start(){
        shooter.spinUp(1350);
    }

    @Override
    public void loop(){
//        Log.d(tag, "power" + testDexer.power);
//        Log.d("Target Pos", "target Pos: " + testDexer.getTargetPos());
//        Log.d("looptime/s", "loop time" + runtime.seconds());

        follower.poseTracker.update();

        robotHeading = follower.getHeading(); //will always be something plus that starting of 180
        double poseX = follower.getPose().getX(); //get robot pose
        double poseY = follower.getPose().getY();
        turretXOffset = 3.17582677*Math.sin((robotHeading)); //calculate offset of turret
        turretYOffset = -3.17582677*Math.cos((robotHeading));
        poseX += turretXOffset; //add that offset to robot
        poseY += turretYOffset;
//        if((Math.atan2(144 - follower.getPose().getY(), 0 - follower.getPose().getX())) > Math.toRadians(135)){
//            goalX = 0;
//            goalY = 135.5;
//        }
//        else{
//            goalX = 8.5;
//            goalY = 144;
//        }
        disX = goalX - poseX; //calculate difference
        disY = goalY - poseY;

        int spinPos = testDexer.updatePos();
        goalDist = Math.sqrt(Math.pow(disX, 2) + Math.pow(disY, 2)); //pythagorean theorem
        goalAngle = (Math.atan2(disY, disX)); //inverse trig
        if(RobotConstants.side.equals("blue")){
            desiredTurretAngle = turret.calculateTurnBlue(goalAngle, robotHeading);
        }else{
            desiredTurretAngle = turret.calculateTurnRed(goalAngle, robotHeading);
        }

        //follower.setTeleOpDrive(-gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);
        drive.drive(gamepad1.left_stick_y, -gamepad1.left_stick_x, -gamepad1.right_stick_x, 1);


        //NEW CONTROLS _______________________________________________________________________________

        if(gamepad1.right_trigger > 0.1){
            intake.runFront();
            if(testDexer.getSpinState() == TestDexer.SpinState.MANUAL_OVERRIDE && intakeSide.equals("back")){
                testDexer.targetPos = 238749;
                testDexer.spinToNextManual("front", spinPos);
            }
            intakeSide = "front";
            if(ballCount < 3 && testDexer.getSpinState() != TestDexer.SpinState.GO_BACK && testDexer.getSpinState() != TestDexer.SpinState.MANUAL_OVERRIDE){
                testDexer.setSpinState(1);
            }

        }
        else if(gamepad1.right_bumper){
            intake.runFrontReverse();
        }
        else{
            intake.stopFront();
        }
        if(gamepad1.left_trigger > .1){
            intake.runBackReverse();

            if(testDexer.getSpinState() == TestDexer.SpinState.MANUAL_OVERRIDE && intakeSide.equals("front")){
                testDexer.targetPos = 238749;
                testDexer.spinToNextManual("back", spinPos);
            }
            intakeSide = "back";
            if(ballCount < 3 && testDexer.getSpinState() != TestDexer.SpinState.GO_BACK && testDexer.getSpinState() != TestDexer.SpinState.MANUAL_OVERRIDE){
                testDexer.setSpinState(1);
            }


        }
        else if(gamepad1.left_bumper){
            intake.runBack();
        }
        else{
            intake.stopBack();
        }
        if(gamepad1.yWasPressed()){
            slowMode = !slowMode;
        }
        if(gamepad2.yWasPressed()){
            shooter.shoot3();
            testDexer.setSpinState(4);
            wentBack = false;

        }
        if(gamepad2.xWasPressed()){
            if(!wentBack){
                lastSpinState = testDexer.getSpinState();
                testDexer.lastTargetPos = testDexer.targetPos;
                testDexer.setSpinState(5);
            }
            else{
                 //exit manual override, go back to pose
                wentBack = false;
                testDexer.targetPos = testDexer.lastTargetPos;
                testDexer.goToLastState(lastSpinState);
            }
        }
        switch (testDexer.getSpinState()){
            case MOVE_TO_INTAKE:
                if(intakeSide.equals("front")){
                    testDexer.spinToFront(ballCount);
                }
                else{
                    testDexer.spinToBack(ballCount);
                }
                if(MathFunctions.roughlyEquals(testDexer.difference, 0, 50)){
                    testDexer.setSpinState(2);
                }//wait until spindexer has moved
                break;
            case INTAKING:
                if(ballCount < 3) {
                    testDexer.checkForBalls();
                    if ((testDexer.checkForColorAtSpot('P', ballCount + 1) || testDexer.checkForColorAtSpot('G', ballCount + 1)) && spindexerDelayTimer.seconds() > .5) { //wait until color
                        ballCount++;
                        testDexer.spinToNext(intakeSide);
                        spindexerDelayTimer.reset();
                    }
                }
                else{
                    testDexer.setSpinState(3);
                }
                break;
            case PREPARE_FOR_SHOT:
                testDexer.setUpForShooting(order);
                testDexer.setSpinState(0);
                break;
            case SHOOT:
                if(shooter.getLauunchState() == testShooter.LaunchState.LAUNCH && !launched){
                    testDexer.shoot();
                    launched = true;
                }
                if(shooter.getLauunchState() == testShooter.LaunchState.RECOVER){
                    testDexer.setSpinState(0);
                    ballCount = 0;
                    launched = false;
                }
                break;
            case GO_BACK:
                if(!wentBack) {
                    testDexer.goBack(testDexer.updatePos());
                    wentBack = true;
                }
                break;
        }
        if(gamepad2.aWasPressed()){
            if( testDexer.getSpinState() != TestDexer.SpinState.MANUAL_OVERRIDE) {
                testDexer.setSpinState(6);
            }
            testDexer.spinToNextManual(intakeSide, spinPos);

        }

//        if(gamepad1.aWasPressed()){
//            turret.turrConst -= .01;
//        }
//        if(gamepad1.bWasPressed()){
//            turret.turrConst += .01;
//        }

        if(slowMode){
            powerSetter = 0.6;
        }
        else{
            powerSetter = 0.2;
        }
        if(gamepad2.dpadDownWasPressed()){ //corner calib
            EXECUTEBIGDADDYPRATHAMHOMINGFUNCTION();
        }

//        if(testDexer.resetNeeded){
//            if(spinPos == testDexer.getTargetPos()){
//                testDexer.encoder.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
//                testDexer.encoder.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
//                testDexer.setTargetPos(0);
//            }
//            if(spinPos == 0){
//                testDexer.resetNeeded = false;
//            }
//        }


        testDexer.setPowerToPosition2(spinPos, runtime.seconds());
//        if(!locked){
        targetVel = shooter.setVel(goalDist);
        shooter.updateState(targetVel, spinPos, testDexer.targetPos);
        shooter.setHood(goalDist);
        turret.rotateToGoal(desiredTurretAngle);
//        }
//        else{ //shoot from close
//            targetVel = 1200;
//            shooter.setHood(.5);
//            turret.rotateToGoal(0);
//        }
//        if(gamepad2.aWasPressed()){ //Should not have to do this if you home
//            lockShooter();
//        }




//        telemetry.addData("Testdexer Target Pos", "Target Pos: " + testDexer.getTargetPos());
//        ;
//        //telemetry.addData("Button status: ", touchy1.detectTouch());
////        telemetry.addData("detectedColor Front: ", testDexer.getDetectedColorFront());
////        telemetry.addData("robot pose: ", follower.getPose());
////        telemetry.addData("detectedColor Back: ", testDexer.getDetectedColorBack());
////        telemetry.addData("turret pos ", desiredTurretAngle);
////        telemetry.addData("goalangle ", Math.toDegrees(goalAngle));
//          telemetry.addData("Difference: ", testDexer.difference);
////        telemetry.addData("power", testDexer.power);
////        telemetry.addData("angle difference from goal", Math.toDegrees(goalAngle) - Math.toDegrees(follower.getHeading()));
////        telemetry.addData("shooter target velocity: ", targetVel);
////        telemetry.addData("shootervel: ", shooter.getVelocity1());
////        telemetry.addData("shooter state: ", shooter.getLauunchState());
//
          telemetry.addData("spindexer pos", spinPos);
        telemetry.addData("spinstate ", testDexer.getSpinState());
        telemetry.addData("spindexer target ", testDexer.getTargetPos());
        telemetry.addData("Turret Constant: ", " " + turret.turrConst);
//        telemetry.addData("ballcount ", ballCount);
////        telemetry.addData("current order ",testDexer.currentOrder);
////        telemetry.addData("pos readings ", testDexer.getPatternOrSpots("1") + testDexer.getPatternOrSpots("2") + testDexer.getPatternOrSpots("3"));
////        telemetry.addData("P: ", P);
////        telemetry.addData("I: ", I);
////        telemetry.addData("D: ", D*1000000);
////        telemetry.addData("stepSize: ", stepSizes[stepIndex] * 1000000);
//
//        //telemetry.addData("tuningservo pos", shooter.getServo());
////        telemetry.addData("Amount to Shoot: ", shooter.getAmountTOShoot());
////        telemetry.addData("Follower X: ", follower.getPose().getX());
////        telemetry.addData("Follower Y ", follower.getPose().getY());
////        telemetry.addData("Follower heading rads ", follower.getPose().getHeading());
////        telemetry.addData("Follower heading degs ", Math.toDegrees(follower.getPose().getHeading()));
////        telemetry.addData("Goal Dist: ", goalDist);
////        telemetry.addData("difference of turret to goal", Math.toDegrees(goalAngle) - Math.toDegrees(turret.getPosWithoutSubtractionFactor()) + 90 );
////        telemetry.addData("angle from robot to goal", Math.toDegrees(goalAngle));
////        telemetry.addData("is turning?", turningToShoot);
////        telemetry.addData("is busy?", turret.getTurretStatus());
////        telemetry.addData("turret angle: ", turret.getCurrentPos()/turret.ticksPerDegree);
////        telemetry.addData("turret angle: ", turret.getCurrentPos());
////        telemetry.addData("turnneeded", Math.toDegrees(turret.turnNeeded/turret.ticksPerRadian));
//
////        telemetry.update();
    }
    public void EXECUTEBIGDADDYPRATHAMHOMINGFUNCTION(){
        follower.setPose(new Pose(8.95, 8.5965,  Math.toRadians(180))); //change later
    }
    public void lockShooter(){ //should never have to be used
        locked = !locked;
    }

}
