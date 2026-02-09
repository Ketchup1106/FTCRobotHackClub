package org.firstinspires.ftc.teamcode.opmodes.auto.blueAutos;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.math.MathFunctions;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.subsystems.AprilTagStuff;
import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.TestDexer;
import org.firstinspires.ftc.teamcode.subsystems.Turret;
import org.firstinspires.ftc.teamcode.subsystems.testShooter;
import org.firstinspires.ftc.teamcode.subsystems.turretServo;
import android.util.Log;

@Autonomous(name = "blue far test", group = "Autonomous", preselectTeleOp = "FAR Blue Teleop")

public class BlueFar extends OpMode {

    Follower follower;
    ElapsedTime spindexerDelayTimer = new ElapsedTime();

    ElapsedTime runtime = new ElapsedTime();
    testShooter shooter = new testShooter();
    Intake intake = new Intake();
    TestDexer testDexer = new TestDexer();
    turretServo turret = new turretServo();
    AprilTagStuff aprilTagStuff = new AprilTagStuff();
    // Paths
    PathChain preload, set2, grab2, emptyRamp, shoot2, set3, grab3, shoot3, set4, grab4, shoot4, park;

    int step = 0;
    double targetVel = 0;
    double goalX = 0;
    double goalY = 144;
    double goalDist;
    double goalAngle;
    String order = "null";
    double disX;
    double disY;
    String intakeSide = "front";
    int ballCount = 3;
    boolean launched = false;

    public double robotHeading;

    double desiredTurretAngle;

    boolean grabbing;

    boolean reset = true;
    int spinPos;
    ElapsedTime aprilTimer = new ElapsedTime();

    @Override
    public void init() {


        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(new Pose(56.95, 8.5965,  Math.toRadians(180)));
        turret.init(hardwareMap);
        testDexer.init(hardwareMap);
        shooter.init(hardwareMap, telemetry);
        intake.init(hardwareMap);
        aprilTagStuff.init(hardwareMap, telemetry);

        buildPaths();   // ← ONLY builds, does NOT runFront anything
        testDexer.currentOrder = "PPG";
        runtime.reset();
        grabbing = false;
        telemetry.addLine("Ready for start");
        telemetry.update();
        testDexer.setUpForShooting("blah");
        testDexer.encoder.setDirection(DcMotorEx.Direction.REVERSE);
    }
    public void init_loop(){
        testDexer.setPowerToPosition2(testDexer.updatePos(), runtime.seconds());
        telemetry.addData("Difference: ", testDexer.difference);
        telemetry.addData("spindexer pos", testDexer.updatePos());
        telemetry.addData("spinstate ", testDexer.getSpinState());
        telemetry.addData("spindexer target ", testDexer.getTargetPos());
        telemetry.addData("power", testDexer.power);

    }
    public void start(){
        follower.followPath(preload);
        runtime.reset();
        aprilTimer.reset();
        spindexerDelayTimer.reset();
    }

    public void loop(){
            Log.v("Spin State", testDexer.getSpinState().toString());
            follower.update();
            robotHeading = follower.getHeading(); //will always be something plus that starting of 180
            double poseX = follower.getPose().getX(); //get robot pose
            double poseY = follower.getPose().getY();
            double turretXOffset = 3.17582677*Math.sin((robotHeading)); //calculate offset of turret
            double turretYOffset = -3.17582677*Math.cos((robotHeading));
            poseX += turretXOffset; //add that offset to robot
            poseY += turretYOffset;
            disX = goalX - poseX; //calculate difference
            disY = goalY - poseY;

            goalDist = Math.sqrt(Math.pow(disX, 2) + Math.pow(disY, 2)); //pythagorean theorem
            goalAngle = (Math.atan2(disY, disX)); //inverse trig
            desiredTurretAngle = turret.calculateTurnBlue(goalAngle, robotHeading);
            turret.rotateToGoal(desiredTurretAngle);
            targetVel = shooter.setVel(goalDist);
            spinPos = testDexer.updatePos();
            shooter.setHood(goalDist);

            switch (testDexer.getSpinState()){
                case IDLE:
                    break;
                case MOVE_TO_INTAKE:
                    if(intakeSide.equals("front")){
                        testDexer.spinToFront(ballCount);
                    }else{
                        testDexer.spinToBack(ballCount);
                    }
                    if(MathFunctions.roughlyEquals(testDexer.difference, 0, 80)){
                        testDexer.setSpinState(2);
                    }
                    break;
                case INTAKING:
                    //if intaking and not full, check for color. if color, move to next slot. repeat until full.
                    if(ballCount < 3){
                        testDexer.checkForBalls();
                        if((testDexer.checkForColorAtSpot('P', ballCount +1) || testDexer.checkForColorAtSpot('G', ballCount +1)) && spindexerDelayTimer.seconds() > .5) { //wait until color
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
                    if(shooter.getLauunchState() == testShooter.LaunchState.LAUNCH && !launched) {
                        //testDexer.shoot(); re-add later
                        testDexer.shoot();
                        launched = true;
                    }
                    if(shooter.getLauunchState() == testShooter.LaunchState.RECOVER){
                        testDexer.setSpinState(0);
                        ballCount = 0;
                        launched = false;
                    }
                    break;
                //if idle just dont change anything
            }
            testDexer.setPowerToPosition2(spinPos, runtime.seconds());
            switch(step) {
                ///////////////////////////////////////////////////////////////////
                /// Step 1: shoot preload and rotate turret to goal
                ///////////////////////////////////////////////////////////////////
                case 0:
                    //condition that waits for shooter to turn on from para callback
                    if(follower.isBusy()){
                        break;
                    }
                    step++;
                    break;
                ///////////////////////////////////////////////////////////////////
                /// Step 2: wait until shooting is done
                ///////////////////////////////////////////////////////////////////
                case 1:
                    if(shooter.isActive){
                        shooter.updateState(targetVel);
                        if(shooter.getLauunchState() == testShooter.LaunchState.LAUNCH && !launched){
                            testDexer.setSpinState(4);
                            //Set launched to true?
                        }
                        break;
                    }
                    testDexer.setSpinState(0);
                    step++;
                    break;
                ///////////////////////////////////////////////////////////////////
                /// Step 3: set up for grabbing middle set
                ///////////////////////////////////////////////////////////////////
                case 2:
                    if(!follower.isBusy()){
                        follower.followPath(set2);
                        step++;
                    }
                    break;
                ///////////////////////////////////////////////////////////////////
                /// Step 4: intake set
                ///////////////////////////////////////////////////////////////////
                case 3:
                    if (!follower.isBusy()) {
                        follower.setMaxPower(0.25);
                        follower.followPath(grab2);
                        step++;
                    }
                    break;
                ///////////////////////////////////////////////////////////////////
                /// Step 5: go back to shoot
                ///////////////////////////////////////////////////////////////////
                case 4:

                    intake.runFront();
                    intakeSide = "front";
                    if(testDexer.getSpinState() == TestDexer.SpinState.IDLE){
                        testDexer.setSpinState(1);
                    }
                    if (!follower.isBusy()) {
                        intake.stopFront();
                        follower.setMaxPower(1);
                        follower.followPath(shoot2);
                        step++;
                    }
                    break;
                //add a step for shoot3
                ///////////////////////////////////////////////////////////////////
                /// Step 6: wait for shot to finish
                ///////////////////////////////////////////////////////////////////
                case 5:
                    if (follower.isBusy()){
                        break;
                    }
                    if(shooter.isActive){
                        shooter.updateState(targetVel);
                        if(shooter.getLauunchState() == testShooter.LaunchState.LAUNCH && !launched){
                            testDexer.setSpinState(4);
                        }
                        break;
                    }
                    testDexer.setSpinState(0);
                    step++;
                    break;
                ///////////////////////////////////////////////////////////////////
                /// Step 7: go get set closest to us
                ///////////////////////////////////////////////////////////////////
                case 6:

                    follower.followPath(set3);
                    step++;
                    break;
                ///////////////////////////////////////////////////////////////////
                /// Step 8: intake
                ///////////////////////////////////////////////////////////////////
                case 7:
                    if (!follower.isBusy()) {
                        intake.runFront();
                        intakeSide = "front";
                        testDexer.setSpinState(1);
                        follower.setMaxPower(0.25);
                        follower.followPath(grab3);
                        step++;
                    }
                    break;
                ///////////////////////////////////////////////////////////////////
                /// Step 9: go back to shoot
                ///////////////////////////////////////////////////////////////////
                case 8:
                    if (!follower.isBusy()) {
                        intake.stopFront();
                        follower.setMaxPower(1);
                        follower.followPath(shoot3);
                        step++;
                    }
                    break;
                ///////////////////////////////////////////////////////////////////
                /// Step 10: wait for shot
                ///////////////////////////////////////////////////////////////////
                case 9:
                    if(follower.isBusy()){
                        break;
                    }
                    step++;
                    break;
                case 10:
                    if(shooter.isActive){
                        shooter.updateState(targetVel);
                        if(shooter.getLauunchState() == testShooter.LaunchState.LAUNCH && !launched){
                            testDexer.setSpinState(4);
                        }
                        break;
                    }
                    testDexer.setSpinState(0);
                    step++;
                    break;
                ///////////////////////////////////////////////////////////////////
                /// Step 11: either get last set or stop here if we want
                ///////////////////////////////////////////////////////////////////
                case 11:
                    if (!follower.isBusy()) {

                        follower.followPath(set4);
                        step++;
                    }
                    break;

                case 12:
                    if (!follower.isBusy()) {
                        intake.runFrontReverse();
                        intakeSide = "front";
                        testDexer.setSpinState(1);
                        follower.setMaxPower(.25);
                        follower.followPath(grab4);
                        step++;
                    }
                    break;

                case 13:
                    if (!follower.isBusy()) {
                        intake.stopFront();
                        follower.setMaxPower(1);
                        follower.followPath(shoot4);
                        step++;
                    }
                    break;
                case 14:
                    if(follower.isBusy()){
                        break;
                    }
                    step++;
                    break;

                case 15:
                    if(shooter.isActive){
                        shooter.updateState(targetVel);
                        if(shooter.getLauunchState() == testShooter.LaunchState.LAUNCH && !launched){
                            testDexer.setSpinState(4);
                        }
                        break;
                    }
                    testDexer.setSpinState(0);
                    step++;
                    break;

                case 16:
                    follower.followPath(park);
                    step++;
                    break;

                case 17: //May have to add Parametric Call back if not enough time to reach this
                    if(!follower.isBusy()) {
                        terminateOpModeNow();
                    }
                    break;
            }

            // Debug Info
            telemetry.addData("detectedColor Front: ", testDexer.getDetectedColorFront());
            telemetry.addData("detectedColor Back: ", testDexer.getDetectedColorBack());
            telemetry.addData("Difference: ", testDexer.difference);
            telemetry.addData("runtime", runtime.seconds());
//        telemetry.addData("angle difference from goal", Math.toDegrees(goalAngle) - Math.toDegrees(follower.getHeading()));
//        telemetry.addData("shooter target velocity: ", targetVel);
//        telemetry.addData("shootervel: ", shooter.getVelocity1());
//        telemetry.addData("shooter state: ", shooter.getLauunchState());
            telemetry.addData("spindexer pos", spinPos);
            telemetry.addData("spinstate ", testDexer.getSpinState());
            telemetry.addData("spindexer target ", testDexer.getTargetPos());
            telemetry.addData("ballcount ", ballCount);
            telemetry.addData("current order ",testDexer.currentOrder);
            telemetry.addData("pos readings ", testDexer.getPatternOrSpots("1") + testDexer.getPatternOrSpots("2") + testDexer.getPatternOrSpots("3"));
//        telemetry.addData("P: ", P);
//        telemetry.addData("I: ", I);
//        telemetry.addData("D: ", D*1000000);
//        telemetry.addData("stepSize: ", stepSizes[stepIndex] * 1000000);

            //telemetry.addData("tuningservo pos", shooter.getServo());
//        telemetry.addData("Amount to Shoot: ", shooter.getAmountTOShoot());
//        telemetry.addData("Follower X: ", follower.getPose().getX());
//        telemetry.addData("Follower Y ", follower.getPose().getY());
//        telemetry.addData("Follower heading rads ", follower.getPose().getHeading());
//        telemetry.addData("Follower heading degs ", Math.toDegrees(follower.getPose().getHeading()));
//        telemetry.addData("Goal Dist: ", goalDist);
//        telemetry.addData("difference of turret to goal", Math.toDegrees(goalAngle) - Math.toDegrees(turret.getPosWithoutSubtractionFactor()) + 90 );
//        telemetry.addData("angle from robot to goal", Math.toDegrees(goalAngle));
//        telemetry.addData("is turning?", turningToShoot);
//        telemetry.addData("is busy?", turret.getTurretStatus());
//        telemetry.addData("turret angle: ", turret.getCurrentPos()/turret.ticksPerDegree);
//        telemetry.addData("turret angle: ", turret.getCurrentPos());
//        telemetry.addData("turnneeded", Math.toDegrees(turret.turnNeeded/turret.ticksPerRadian));

            telemetry.update();
    }
    // ---------------------------------------------------------------------
    // PATH GENERATION
    // ---------------------------------------------------------------------
    public void buildPaths() {

        final Pose startPose = new Pose(56.95, 8.5965,  Math.toRadians(180));
//        final Pose shootMid = new Pose(60, 8,  Math.toRadians(180));
        final Pose shootMid2 = new Pose(50, 50,  Math.toRadians(180));
        final Pose shootPoseFar = new Pose(60, 16, Math.toRadians(180)); //Change Coordinates
        final Pose grabPose1 = new Pose(48, 56, Math.toRadians(180));
        final Pose grabbed1 = new Pose(20, 56, Math.toRadians(180));
        final Pose rampMid = new Pose(22, 67, Math.toRadians(225));
        final Pose ramp = new Pose(15, 70, Math.toRadians(180));
        final Pose grabPose2 = new Pose(48, 32, Math.toRadians(180));
        final Pose grabbed2 = new Pose(20, 32, Math.toRadians(180));
        final Pose grabPose3 = new Pose(32, 16, Math.toRadians(180));
        final Pose grabbed3 = new Pose(10, 16, Math.toRadians(180));
        final Pose parkPose = new Pose(24, 70,   Math.toRadians(180));

        preload = follower.pathBuilder()
                .addPath(new BezierLine(startPose, shootPoseFar))
                .setLinearHeadingInterpolation(startPose.getHeading(), shootPoseFar.getHeading())
                .addParametricCallback(0.75, () -> {shooter.shoot3();})
                .build();

        set2 = follower.pathBuilder()
                .addPath(new BezierLine(shootPoseFar, grabPose1))
                .setLinearHeadingInterpolation(shootPoseFar.getHeading(), grabPose1.getHeading())
                .build();

        grab2 = follower.pathBuilder()
                .addPath(new BezierLine(grabPose1, grabbed1))
                .setLinearHeadingInterpolation(grabPose1.getHeading(), grabbed1.getHeading())
                .setVelocityConstraint(5)
                .build();
        emptyRamp = follower.pathBuilder()
                .addPath(new BezierCurve(grabbed1, rampMid, ramp))
                .setLinearHeadingInterpolation(grabbed1.getHeading(), rampMid.getHeading(), ramp.getHeading())
                .setVelocityConstraint(5)
                .build();
        shoot2 = follower.pathBuilder()
                .addPath(new BezierCurve(ramp, shootMid2, shootPoseFar))
                .setLinearHeadingInterpolation(ramp.getHeading(), shootMid2.getHeading(), shootPoseFar.getHeading())
                .addParametricCallback(0.75, () -> {shooter.shoot3();})
                .build();
        set3 = follower.pathBuilder()
                .addPath(new BezierLine(shootPoseFar, grabPose2))
                .setLinearHeadingInterpolation(shootPoseFar.getHeading(), grabPose2.getHeading())
                .build();

        grab3 = follower.pathBuilder()
                .addPath(new BezierLine(grabPose2, grabbed2))
                .setLinearHeadingInterpolation(grabPose2.getHeading(), grabbed2.getHeading())
                .build();

        shoot3 = follower.pathBuilder()
                .addPath(new BezierLine(grabbed2, shootPoseFar))
                .setLinearHeadingInterpolation(grabbed2.getHeading(), shootPoseFar.getHeading())
                .addParametricCallback(0.75, () -> {shooter.shoot3();})
                .build();
        set4 = follower.pathBuilder()
                .addPath(new BezierLine(shootPoseFar, grabPose3))
                .setLinearHeadingInterpolation(shootPoseFar.getHeading(), grabPose3.getHeading())
                .build();
        grab4 = follower.pathBuilder()
                .addPath(new BezierLine(grabPose3, grabbed3))
                .setLinearHeadingInterpolation(grabPose3.getHeading(), grabbed3.getHeading())
                .build();
        shoot4 = follower.pathBuilder()
                .addPath(new BezierLine(grabbed3, shootPoseFar))
                .setLinearHeadingInterpolation(grabbed3.getHeading(), shootPoseFar.getHeading())
                .addParametricCallback(0.75, () -> {shooter.shoot3();})
                .build();
        park = follower.pathBuilder()
                .addPath(new BezierLine(shootPoseFar, parkPose))
                .setLinearHeadingInterpolation(shootPoseFar.getHeading(), parkPose.getHeading())
                .build();
    }
}

