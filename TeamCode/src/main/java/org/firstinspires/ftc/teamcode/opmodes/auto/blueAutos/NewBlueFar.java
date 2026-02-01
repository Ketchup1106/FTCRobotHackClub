package org.firstinspires.ftc.teamcode.opmodes.auto.blueAutos;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.math.MathFunctions;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.subsystems.AprilTagStuff;
import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.TestDexer;
import org.firstinspires.ftc.teamcode.subsystems.Turret;
import org.firstinspires.ftc.teamcode.subsystems.testShooter;

@Autonomous(name = "blue far test", group = "Autonomous")
public class NewBlueFar extends LinearOpMode {

    Follower follower;
    ElapsedTime spindexerDelayTimer = new ElapsedTime();

    ElapsedTime runtime = new ElapsedTime();
    testShooter shooter = new testShooter();
    Intake intake = new Intake();
    TestDexer testDexer = new TestDexer();
    Turret turret = new Turret();
    AprilTagStuff aprilTagStuff = new AprilTagStuff();
    // Paths
    PathChain preload, set2, grab2, emptyRamp, shoot2, set3, grab3, shoot3, set4, grab4, shoot4, park;

    int step = 0;
    int stateVar = 0;
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

    int desiredTurretAngle;

    boolean grabbing;

    boolean reset = true;

    @Override
    public void runOpMode() {
        int spinPos;

        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(new Pose(56, 8,  Math.toRadians(180)));
        turret.init(hardwareMap);
        testDexer.init(hardwareMap);
        shooter.init(hardwareMap, telemetry);
        intake.init(hardwareMap);
        aprilTagStuff.init(hardwareMap, telemetry);

        buildPaths();   // ← ONLY builds, does NOT runFront anything
        testDexer.currentOrder = "PPG";
        //init sequence - get servios up to speed, reset pos, set up for shot
        runtime.reset();
        while(runtime.seconds() < 3){
            testDexer.s1.setPower(-1);
            testDexer.s2.setPower(-1);
            telemetry.addLine("WAIT");
        }
        spinPos = testDexer.updatePos();
        double differenceFromZero = 8192 - Math.abs(spinPos)%8192;
        testDexer.targetPos = spinPos - differenceFromZero;
        while (spinPos < testDexer.getTargetPos() -150){
            spinPos = testDexer.updatePos();
            testDexer.setPowerToPosition2(spinPos, runtime.seconds());
            telemetry.addData("pose", spinPos);
        }
        while(!(MathFunctions.roughlyEquals(spinPos, 0, 130))){
            testDexer.setPowerToPosition2(spinPos, runtime.seconds());
        }
        testDexer.encoder.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        testDexer.encoder.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        while(reset){
            if(gamepad1.dpadDownWasPressed()){
                follower.setStartingPose(new Pose(56, 8,  Math.toRadians(180)));
                reset = false;
            }
        }

        grabbing = false;
        testDexer.s1.setPower(0);
        testDexer.s2.setPower(0);
        testDexer.currentOrder = "PPG";
        telemetry.addLine("Ready for start");
        telemetry.update();


        ElapsedTime aprilTimer = new ElapsedTime();
        waitForStart();

        if (isStopRequested()) return;

        // Start first path
        follower.followPath(preload);
        runtime.reset();
        aprilTimer.reset();
        runtime.reset();
        spindexerDelayTimer.reset();
        while (opModeIsActive()) {
            disX = goalX - follower.getPose().getX();
            disY = goalY - follower.getPose().getY();

            robotHeading = follower.getHeading(); //will always be something plus that starting of 90
            double turretXOffset = 3.175 * Math.sin((robotHeading));
            double turretYOffset = 3.175*Math.cos((robotHeading));
            disX += turretXOffset;
            disY -= turretYOffset;
            goalDist = Math.sqrt(Math.pow(disX, 2) + Math.pow(disY, 2)); //pythagorean theorem
            goalAngle = Math.abs(Math.atan2(disX, disY)) + Math.toRadians(90); //simple inverse trig with compensation for robot's extra 90 degrees
            targetVel = shooter.setVel(goalDist);
            shooter.setHood(goalDist);

            spinPos = testDexer.updatePos();
            if(testDexer.getSpinState() != TestDexer.SpinState.SHOOT){
                testDexer.setPowerToPosition2(spinPos, runtime.seconds());
            }
            follower.update();
            switch (testDexer.getSpinState()){
                case MOVE_TO_INTAKE:
                    if(intakeSide.equals("front")){
                        testDexer.spinToFront(ballCount);
                        testDexer.setSpinState(2);
                    }else{
                        testDexer.spinToBack(ballCount);
                        testDexer.setSpinState(2);

                    }
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
                case PREPARE_FOR_SHOT:
                    testDexer.setUpForShooting(order);
                    testDexer.setSpinState(0);
                case SHOOT:
                    if(shooter.getLauunchState() == testShooter.LaunchState.LAUNCH && !launched) {
                        //testDexer.shoot(); re-add later
                        testDexer.s1.setPower(.2);
                        testDexer.s2.setPower(.2);
                        launched = true;
                    }
                    if(shooter.getLauunchState() == testShooter.LaunchState.RECOVER){
                        testDexer.setSpinState(0);
                        testDexer.s1.setPower(0);
                        testDexer.s2.setPower(0);
                        ballCount = 0;
                        launched = false;
                    }
                //if idle just dont change anything
            }
            switch (step) {
                ///////////////////////////////////////////////////////////////////
                /// Step 1: shoot preload and rotate turret to goal
                ///////////////////////////////////////////////////////////////////
                case 0:
//                    shooter.shoot3();
                    desiredTurretAngle = turret.calculateTurnBlue(goalAngle, robotHeading);
                    turret.rotateToGoal(desiredTurretAngle);
                    //condition that waits for shooter to turn on
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
                        desiredTurretAngle = 0;
                        turret.rotateToGoal(desiredTurretAngle);
                        follower.followPath(set2);
                        step++;
                    }
                    break;
                ///////////////////////////////////////////////////////////////////
                /// Step 4: intake set
                ///////////////////////////////////////////////////////////////////
                case 3:
                    if (!follower.isBusy()) {
                        intake.runFrontReverse();
                        intakeSide = "front";
                        if(testDexer.getSpinState() == TestDexer.SpinState.IDLE){
                            testDexer.setSpinState(1);
                        }

                        if(MathFunctions.roughlyEquals(spinPos, testDexer.getTargetPos(), 130)){
                            testDexer.setSpinState(2);
                        }
                        else{
                            break;
                        }
                        follower.followPath(grab2);
                        step++;
                    }
                    break;
                ///////////////////////////////////////////////////////////////////
                /// Step 5: go back to shoot
                ///////////////////////////////////////////////////////////////////
                case 4:
                    if (!follower.isBusy()) {
                        intake.stopFront();
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
                        desiredTurretAngle = turret.calculateTurnBlue(goalAngle, robotHeading);
                        turret.rotateToGoal(desiredTurretAngle);
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
                    desiredTurretAngle = 0;
                    turret.rotateToGoal(desiredTurretAngle);
                    follower.followPath(set3);
                    step++;
                    break;
                ///////////////////////////////////////////////////////////////////
                /// Step 8: intake
                ///////////////////////////////////////////////////////////////////
                case 7:
                    if (!follower.isBusy()) {
                        intake.runFrontReverse();
                        intakeSide = "front";
                        testDexer.setSpinState(1);
                        if(MathFunctions.roughlyEquals(spinPos, testDexer.getTargetPos(), 130)){
                            testDexer.setSpinState(2);
                        }
                        else{
                            break;
                        }
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
                        follower.followPath(shoot3);
                        step++;
                    }
                    break;
                ///////////////////////////////////////////////////////////////////
                /// Step 10: wait for shot
                ///////////////////////////////////////////////////////////////////
                case 9:
                    if(shooter.isActive){
                        desiredTurretAngle = turret.calculateTurnBlue(goalAngle, robotHeading);
                        turret.rotateToGoal(desiredTurretAngle);
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
                case 10:
                    if (!follower.isBusy()) {
                        desiredTurretAngle = 0;
                        turret.rotateToGoal(desiredTurretAngle);
                        follower.followPath(set4);
                        step++;
                    }
                    break;

                case 11:
                    if (!follower.isBusy()) {
                        intake.runFrontReverse();
                        intakeSide = "front";
                        testDexer.setSpinState(1);
                        if(MathFunctions.roughlyEquals(spinPos, testDexer.getTargetPos(), 130)){
                            testDexer.setSpinState(2);
                        }
                        else{
                            break;
                        }
                        follower.followPath(grab4);
                        step++;
                    }
                    break;

                case 12:
                    if (!follower.isBusy()) {
                        intake.stopFront();
                        follower.followPath(shoot4);
                        step++;
                    }
                    break;

                case 13:
                    if(shooter.isActive){
                        desiredTurretAngle = turret.calculateTurnBlue(goalAngle, robotHeading);
                        turret.rotateToGoal(desiredTurretAngle);
                        shooter.updateState(targetVel);
                        if(shooter.getLauunchState() == testShooter.LaunchState.LAUNCH && !launched){
                            testDexer.setSpinState(4);
                        }
                        break;
                    }
                    testDexer.setSpinState(0);
                    step++;
                    break;

                case 14:
                    follower.followPath(park);
                    step++;
                    break;

                case 15: //May have to add Parametric Call back if not enough time to reach this
                    if(!follower.isBusy()) {
                        desiredTurretAngle = 0;
                        turret.rotateToGoal(desiredTurretAngle);
                    }
                    break;
            }

            // Debug Info
            telemetry.addData("detectedColor Front: ", testDexer.getDetectedColorFront());
            telemetry.addData("detectedColor Back: ", testDexer.getDetectedColorBack());
            telemetry.addData("Difference: ", testDexer.difference);
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
    }

    // ---------------------------------------------------------------------
    // PATH GENERATION
    // ---------------------------------------------------------------------
    public void buildPaths() {

        final Pose startPose = new Pose(56, 8,  Math.toRadians(180));
//        final Pose shootMid = new Pose(60, 8,  Math.toRadians(180));
        final Pose shootMid2 = new Pose(50, 50,  Math.toRadians(180));
        final Pose shootPoseFar = new Pose(60, 16, Math.toRadians(180)); //Change Coordinates
        final Pose grabPose1 = new Pose(40, 56, Math.toRadians(180));
        final Pose grabbed1 = new Pose(23, 56, Math.toRadians(180));
        final Pose rampMid = new Pose(22, 67, Math.toRadians(225));
        final Pose ramp = new Pose(15, 70, Math.toRadians(180));
        final Pose grabPose2 = new Pose(40, 32, Math.toRadians(180));
        final Pose grabbed2 = new Pose(23, 32, Math.toRadians(180));
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

