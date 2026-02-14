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
import com.qualcomm.robotcore.robot.Robot;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.opmodes.RobotConstants;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.subsystems.AprilTagStuff;
import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.TestDexer;
import org.firstinspires.ftc.teamcode.subsystems.Turret;
import org.firstinspires.ftc.teamcode.subsystems.testShooter;
import org.firstinspires.ftc.teamcode.subsystems.turretServo;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

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
    PathChain preload, set2, jerk2One, jerk2Two, grab2, emptyRamp, shoot2, set3, jerk3One, jerk3Two, grab3, shoot3, set4, grab4, shoot4, park;

    int step = 0;
    double targetVel = 0;
    double goalX = 6;
    double goalY = 138;
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
    boolean gottenBall = false;

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
        turret.turret.setPosition(0);
    }
    public void init_loop(){
        spinPos = testDexer.updatePos();
        //if(!MathFunctions.roughlyEquals(testDexer.difference, 0, 55)){ //dont let the thing jitter
            testDexer.setPowerToPosition2(spinPos, runtime.seconds());
        //}

        aprilTagStuff.update();
        AprilTagDetection id21 = aprilTagStuff.getTagById(21); //gpp
        aprilTagStuff.displayDetection(id21);
        AprilTagDetection id22 = aprilTagStuff.getTagById(22); //pgp
        aprilTagStuff.displayDetection(id22);
        AprilTagDetection id23 = aprilTagStuff.getTagById(23); //ppg
        aprilTagStuff.displayDetection(id23);

        if (aprilTagStuff.patternID == 21) {
            order = "GPP";
        }
        if (aprilTagStuff.patternID == 22) {
            order = "PGP";
        }
        if (aprilTagStuff.patternID == 23) {
            order = "PPG";
        }

        telemetry.addData("Order: ", order);
        telemetry.addData("Difference: ", testDexer.difference);
        telemetry.addData("spindexer pos", testDexer.updatePos());
        telemetry.addData("spinstate ", testDexer.getSpinState());
        telemetry.addData("spindexer target ", testDexer.getTargetPos());
        telemetry.addData("power", testDexer.power);
        telemetry.update();

    }
    public void start(){
        aprilTagStuff.stop();
        RobotConstants.order = order;
        runtime.reset();
        spindexerDelayTimer.reset();
        RobotConstants.side = "blue";
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
                        if((testDexer.checkForColorAtSpot('P', ballCount +1) || testDexer.checkForColorAtSpot('G', ballCount +1)) && !gottenBall ) { //wait until color
                            ballCount++;
                            testDexer.spinToNext(intakeSide);
                            spindexerDelayTimer.reset();
                            gottenBall = true;
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
                /// Step 1: shoot preload
                ///////////////////////////////////////////////////////////////////
                case 0:
                    shooter.shoot3();
                    step++;
                    break;
                ///////////////////////////////////////////////////////////////////
                /// Step 2: wait until shooting is done
                ///////////////////////////////////////////////////////////////////
                case 1:
                    if(shooter.isActive){
                        shooter.updateState(targetVel, spinPos, testDexer.targetPos);
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
                ///////////////////////////////////////////
                /// step 4: start intake, prep spindexer
                //////////////////////////////////////
                case 3:
                    if (!follower.isBusy()) {
                        intake.runFront();
                        intakeSide = "front";
                        testDexer.setSpinState(1);
                        step++;
                    }
                    break;
                ///////////////////////////////////////////////////////////////////
                /// Step 5: ball 1
                ///////////////////////////////////////////////////////////////////
                case 4:
                    if(!follower.isBusy()){
                        follower.followPath(jerk2One);
                        step++;
                        spindexerDelayTimer.reset();
                    }
                    break;
                ///////////////////////////////////////////////////////////////////
                /// Step 6: wait for the dexer to move or if an error has occurred, move to next step
                ///////////////////////////////////////////////////////////////////
                case 5: //wait for the dexer to move or if an error has occurred, move to next step
                    if(!follower.isBusy()) {
                        if (MathFunctions.roughlyEquals(spinPos, testDexer.frontSecondIntakePos, 50)) {
                            step++;
                        }else if(spindexerDelayTimer.seconds() > .5 && !gottenBall){
                            step++;
                            ballCount++;
                            testDexer.spinToNext(intakeSide);
                            gottenBall = true;
                        }
                    }
                    break;
                ///////////////////////////////////////////////////////////////////
                /// Step 7: ball 2
                ///////////////////////////////////////////////////////////////////
                case 6:
                    if(!follower.isBusy()){
                        follower.followPath(jerk2Two);
                        step++;
                        gottenBall = false;
                        spindexerDelayTimer.reset();
                    }
                    break;
                ///////////////////////////////////////////////////////////////////
                /// Step 8: wait for the dexer to move or if an error has occurred, move to next step
                ///////////////////////////////////////////////////////////////////
                case 7: //wait for the dexer to move or if an error has occurred, move to next step
                    if(!follower.isBusy()) {
                        if (MathFunctions.roughlyEquals(spinPos, testDexer.frontThirdIntakePos, 50)) {
                            step++;
                        }else if(spindexerDelayTimer.seconds() > .5 && !gottenBall){
                            step++;
                            ballCount++;
                            testDexer.spinToNext(intakeSide);
                            gottenBall = true;
                        }
                    }
                    break;
                ///////////////////////////////////////////////////////////////////
                /// Step 9: ball 3
                ///////////////////////////////////////////////////////////////////
                case 8:
                    if (!follower.isBusy()) {
                        follower.followPath(grab2);
                        gottenBall = false;
                        step++;
                    }
                    break;
                ///////////////////////////////////////////////////////////////////
                /// Step 10: go back to shoot
                ///////////////////////////////////////////////////////////////////
                case 9:
                    if (!follower.isBusy()) {
                        intake.stopFront();
                        follower.setMaxPower(1);
                        follower.followPath(shoot2);
                        step++;
                    }
                    break;
                //add a step for shoot3
                ///////////////////////////////////////////////////////////////////
                /// Step 11: wait for shot to finish
                ///////////////////////////////////////////////////////////////////
                case 10:
                    if (follower.isBusy()){
                        break;
                    }
                    if(shooter.isActive){
                        shooter.updateState(targetVel, spinPos, testDexer.targetPos);
                        if(shooter.getLauunchState() == testShooter.LaunchState.LAUNCH && !launched){
                            testDexer.setSpinState(4);
                        }
                        break;
                    }
                    testDexer.setSpinState(0);
                    step++;
                    break;
                ///////////////////////////////////////////////////////////////////
                /// Step 12: go get set closest to us
                ///////////////////////////////////////////////////////////////////
                case 11:
                    follower.followPath(set3);
                    step++;
                    break;
                ///////////////////////////////////////////////////////////////////
                /// Step 13: start intake, prep spindexer
                ///////////////////////////////////////////////////////////////////
                case 12:
                    if (!follower.isBusy()) {
                        intake.runFront();
                        intakeSide = "front";
                        testDexer.setSpinState(1);
                        step++;
                    }
                    break;
                ///////////////////////////////////////////////////////////////////
                /// Step 14: ball 4
                ///////////////////////////////////////////////////////////////////
                case 13:
                    if(!follower.isBusy()){
                        follower.followPath(jerk3One);
                        step++;
                    }

                    break;
                ///////////////////////////////////////////////////////////////////
                /// Step 15: wait for the dexer to move or if an error has occurred, move to next step
                ///////////////////////////////////////////////////////////////////
                case 14: //wait for the dexer to move or if an error has occurred, move to next step
                    if(!follower.isBusy()) {
                        if (MathFunctions.roughlyEquals(spinPos, testDexer.frontSecondIntakePos, 50)) {
                            step++;
                        }else if(spindexerDelayTimer.seconds() > .5){
                            step++;
                            ballCount++;
                            testDexer.spinToNext(intakeSide);
                        }
                    }
                    break;
                ///////////////////////////////////////////////////////////////////
                /// Step 16: ball 5
                ///////////////////////////////////////////////////////////////////
                case 15:
                    if(!follower.isBusy()){
                        follower.followPath(jerk3Two);
                        step++;
                    }
                    break;
                ///////////////////////////////////////////////////////////////////
                /// Step 17: wait for the dexer to move or if an error has occurred, move to next step
                ///////////////////////////////////////////////////////////////////
                case 16: //wait for the dexer to move or if an error has occurred, move to next step
                    if(!follower.isBusy()) {
                        if (MathFunctions.roughlyEquals(spinPos, testDexer.frontThirdIntakePos, 50)) {
                            step++;
                        }else if(spindexerDelayTimer.seconds() > .5){
                            step++;
                            ballCount++;
                            testDexer.spinToNext(intakeSide);
                        }
                    }
                    break;

                ///////////////////////////////////////////////////////////////////
                /// Step 18: ball 6
                ///////////////////////////////////////////////////////////////////
                case 17:
                    if (!follower.isBusy() && MathFunctions.roughlyEquals(spinPos, testDexer.frontThirdIntakePos, 50)) {
                        follower.followPath(grab3);
                        step++;
                    }
                    break;
                ///////////////////////////////////////////////////////////////////
                /// Step 19: go back to shoot
                ///////////////////////////////////////////////////////////////////
                case 18:
                    if (!follower.isBusy()) {
                        intake.stopFront();
                        follower.setMaxPower(1);
                        follower.followPath(shoot3);
                        step++;
                    }
                    break;
                ///////////////////////////////////////////////////////////////////
                /// Step 20: wait for shot
                ///////////////////////////////////////////////////////////////////
                case 19:
                    if(follower.isBusy()){
                        break;
                    }
                    step++;
                    break;
                ///////////////////////////////////////////////////////////////////
                /// Step 21: finish shot
                ///////////////////////////////////////////////////////////////////
                case 20:
                    if(shooter.isActive){
                        shooter.updateState(targetVel, spinPos, testDexer.targetPos);
                        if(shooter.getLauunchState() == testShooter.LaunchState.LAUNCH && !launched){
                            testDexer.setSpinState(4);
                        }
                        break;
                    }
                    testDexer.setSpinState(0);
                    step++;
                    break;
                ///////////////////////////////////////////////////////////////////
                /// Step 22: either get last set or stop here if we want
                ///////////////////////////////////////////////////////////////////
                case 21:
                    if (!follower.isBusy()) {
                        follower.followPath(set4);
                        step++;
                    }
                    break;

                case 22:
                    if (!follower.isBusy()) {
                        intake.runFront();
                        intakeSide = "front";
                        testDexer.setSpinState(1);
                        follower.followPath(grab4);
                        step++;
                    }
                    break;

                case 23:
                    if (!follower.isBusy()) {
                        intake.stopFront();
                        follower.setMaxPower(1);
                        follower.followPath(shoot4);
                        step++;
                    }
                    break;
                case 24:
                    if(follower.isBusy()){
                        break;
                    }
                    step++;
                    break;

                case 25:
                    if(shooter.isActive){
                        shooter.updateState(targetVel, spinPos, testDexer.targetPos);
                        if(shooter.getLauunchState() == testShooter.LaunchState.LAUNCH && !launched){
                            testDexer.setSpinState(4);
                        }
                        break;
                    }
                    testDexer.setSpinState(0);
                    step++;
                    break;

                case 26:
                    follower.followPath(park);
                    step++;
                    break;

                case 27: //May have to add Parametric Call back if not enough time to reach this
                    if(!follower.isBusy()) {
                        RobotConstants.autoEnd = follower.getPose();
                        terminateOpModeNow();
                    }
                    break;
            }
            RobotConstants.autoEnd = follower.getPose();

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
        final Pose grabPose1 = new Pose(48, 58, Math.toRadians(180));
        final Pose grabPose1One = new Pose(40, 58, Math.toRadians(180));
        final Pose grabPose1Two = new Pose(31, 58, Math.toRadians(180));
        final Pose grabbed1 = new Pose(20, 58, Math.toRadians(180));
        final Pose rampMid = new Pose(22, 67, Math.toRadians(225));
        final Pose ramp = new Pose(15, 70, Math.toRadians(180));
        final Pose grabPose2 = new Pose(48, 34, Math.toRadians(180));
        final Pose grabPose2One = new Pose(40, 34, Math.toRadians(180));
        final Pose grabPose2Two = new Pose(31, 34, Math.toRadians(180));
        final Pose grabbed2 = new Pose(20, 34, Math.toRadians(180));
        final Pose grabPose3 = new Pose(32, 16, Math.toRadians(180));
        final Pose grabbed3 = new Pose(10, 16, Math.toRadians(180));
        final Pose parkPose = new Pose(24, 70,   Math.toRadians(180));

        preload = follower.pathBuilder()
                .addPath(new BezierLine(startPose, startPose))
                .setLinearHeadingInterpolation(startPose.getHeading(), startPose.getHeading())
                .addParametricCallback(0.75, () -> {shooter.shoot3();})
                .build();

        set2 = follower.pathBuilder()
                .addPath(new BezierLine(startPose, grabPose1))
                .setLinearHeadingInterpolation(startPose.getHeading(), grabPose1.getHeading())
                .build();
        jerk2One = follower.pathBuilder()
                .addPath(new BezierLine(grabPose1, grabPose1One))
                .setLinearHeadingInterpolation(grabPose1.getHeading(), grabPose1One.getHeading())
                .build();
        jerk2Two = follower.pathBuilder()
                .addPath(new BezierLine(grabPose1One, grabPose1Two))
                .setLinearHeadingInterpolation(grabPose1One.getHeading(), grabPose1Two.getHeading())
                .build();

        grab2 = follower.pathBuilder()
                .addPath(new BezierLine(grabPose1Two, grabbed1))
                .setLinearHeadingInterpolation(grabPose1Two.getHeading(), grabbed1.getHeading())
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
        jerk3One = follower.pathBuilder()
                .addPath(new BezierLine(grabPose2, grabPose2One))
                .setLinearHeadingInterpolation(grabPose2.getHeading(), grabPose2One.getHeading())
                .build();
        jerk3Two = follower.pathBuilder()
                .addPath(new BezierLine(grabPose2One, grabPose2Two))
                .setLinearHeadingInterpolation(grabPose2One.getHeading(), grabPose2Two.getHeading())
                .build();
        grab3 = follower.pathBuilder()
                .addPath(new BezierLine(grabPose2Two, grabbed2))
                .setLinearHeadingInterpolation(grabPose2Two.getHeading(), grabbed2.getHeading())
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

