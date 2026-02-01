package org.firstinspires.ftc.teamcode.opmodes.auto.redAutos;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.subsystems.AprilTagStuff;
import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.TestDexer;
import org.firstinspires.ftc.teamcode.subsystems.Turret;
import org.firstinspires.ftc.teamcode.subsystems.testShooter;

@Autonomous(name = "red far", group = "Autonomous")
@Disabled
public class RedAutoFar extends LinearOpMode {

    Follower follower;
    ElapsedTime spindexerDelayTimer = new ElapsedTime();

    ElapsedTime runtime = new ElapsedTime();
    testShooter shooter = new testShooter();
    Intake intake = new Intake();
    TestDexer testDexer = new TestDexer();
    Turret turret = new Turret();
    AprilTagStuff aprilTagStuff = new AprilTagStuff();
    // Paths
    PathChain preload, set2, grab2One, grab2Two, grab2Three, grab2Four, grab2Five, grab2Six, emptyRamp, shoot2, set3, grab3One, grab3Two, grab3Three, grab3Four, grab3Five, grab3Six, shoot3, set4, grab4One, grab4Two, grab4Three, grab4Four, grab4Five, grab4Six, shoot4, park;
    ElapsedTime waiter = new ElapsedTime();
    int step = 0;
    int stateVar = 0;
    double targetVel = 0;
    double goalX = 132;
    double goalY = 136;
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

    @Override
    public void runOpMode() {
        int spinPos;

        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(new Pose(88, 8,  Math.toRadians(180)));
        turret.init(hardwareMap);
        testDexer.init(hardwareMap);
        shooter.init(hardwareMap, telemetry);
        intake.init(hardwareMap);
        aprilTagStuff.init(hardwareMap, telemetry);

        buildPaths();   // ← ONLY builds, does NOT runFront anything
        ElapsedTime runtime = new ElapsedTime();
        testDexer.currentOrder = "PPG";
        //init sequence - get servios up to speed, reset pos, set up for shot
        while(runtime.seconds() < 3){
            testDexer.s1.setPower(-1);
            testDexer.s2.setPower(-1);
            telemetry.addLine("WAIT");
        }
        spinPos = testDexer.updatePos();
        while (spinPos < testDexer.getTargetPos() -150){
            spinPos = testDexer.updatePos();
            testDexer.setPowerToPosition2(spinPos, runtime.seconds());
            telemetry.addData("pose", spinPos);
        }

        grabbing = false;
        testDexer.s1.setPower(0);
        testDexer.s2.setPower(0);
        testDexer.currentOrder = "PPG";
        telemetry.addLine("Ready for start");
        telemetry.update();


        ElapsedTime aprilTimer = new ElapsedTime();
        ElapsedTime spinDelay = new ElapsedTime();
        waitForStart();

        if (isStopRequested()) return;

        // Start first path
        follower.followPath(preload);
        runtime.reset();
        aprilTimer.reset();
        spinDelay.reset();
        runtime.reset();
        spindexerDelayTimer.reset();
        while (opModeIsActive()) {
            spinPos = testDexer.updatePos();
            follower.poseTracker.update();
            disX = goalX - follower.getPose().getX();
            disY = goalY - follower.getPose().getY();
            robotHeading = follower.getHeading(); //will always be something plus that starting of 90
            goalDist = Math.sqrt(Math.pow(disX + 2, 2) + Math.pow(disY, 2)); //pythagorean theorem
            goalAngle = Math.abs(Math.atan2(disY, disX + 2.5)); //simple inverse trig with compensation for robot's extra 90 degrees
            if(step != 17){
                desiredTurretAngle = turret.calculateTurnRed(goalAngle, robotHeading);
                turret.rotateToGoal(desiredTurretAngle);
            } //turret
            targetVel = shooter.setVel(goalDist);
            shooter.setHood(goalDist);
            //apriltag --------------------------------------------------------
//            aprilTagStuff.update();
//            AprilTagDetection id21 = aprilTagStuff.getTagById(21); //gpp
//            aprilTagStuff.displayDetection(id21);
//            AprilTagDetection id22 = aprilTagStuff.getTagById(22); //pgp
//            aprilTagStuff.displayDetection(id22);
//            AprilTagDetection id23 = aprilTagStuff.getTagById(23); //ppg
//            aprilTagStuff.displayDetection(id23);
//            if (aprilTagStuff.patternID == 21) {
//                order = "GPP";
//                aprilTagStuff.stop();
//            }
//            if (aprilTagStuff.patternID == 22) {
//                order = "PGP";
//                aprilTagStuff.stop();
//            }
//            if (aprilTagStuff.patternID == 23) {
//                order = "PPG";
//                aprilTagStuff.stop();
//            }
            if (aprilTimer.seconds() > 5 && (order.equals("null"))){
                order = "No order detected!";
                aprilTagStuff.stop();
            }
            telemetry.addData("Order: ", order);
            //-------------------------------------------------------------------------

            if(step > 2){
                testDexer.setPowerToPosition2(spinPos, runtime.seconds());
            }
//            //step 1 select intake
//            if(intakeSide.equals("front")){
//                if(testDexer.getSpinState() == TestDexer.SpinState.MOVE_TO_INTAKE){
//                    testDexer.spinToFront(ballCount);
//                    testDexer.setSpinState(2);
//                }
//            }else{
//                if(testDexer.getSpinState() == TestDexer.SpinState.MOVE_TO_INTAKE){ //if we are prepping for shot then dont spin to intake
//                    testDexer.spinToBack(ballCount);
//                    testDexer.setSpinState(2);
//                }
//            }
//
//            //step 2 sort balls
//            if(testDexer.getSpinState() == TestDexer.SpinState.INTAKING){
//                //if intaking and not full, check for color. if color, move to next slot. repeat until full.
//                if(ballCount < 3){
//                    testDexer.checkForBalls();
//                    if((testDexer.checkForColorAtSpot('P', ballCount +1) || testDexer.checkForColorAtSpot('G', ballCount +1)) && spinDelay.seconds() > .25) { //wait until color
//                        ballCount++;
//                        testDexer.spinToNext(intakeSide);
//                        spinDelay.reset();
//                    }
//                }
//                else{
//                    testDexer.setSpinState(3);
//                }
//            }
//            //step 3 set up for shot
//            if(testDexer.getSpinState() == TestDexer.SpinState.PREPARE_FOR_SHOT){
//                testDexer.setUpForShooting(order);
//                testDexer.setSpinState(0);
//            }
            //step 4 shoot
//            if(testDexer.getSpinState() == TestDexer.SpinState.SHOOT && shooter.getLauunchState() == testShooter.LaunchState.LAUNCH && !launched){
//                testDexer.shoot();
//                launched = true;
//            }if(shooter.getLauunchState() == testShooter.LaunchState.RECOVER){
//                testDexer.setSpinState(0);
//                ballCount = 0;
//                launched = false;
//            }
            follower.update();
            if(testDexer.getSpinState() == TestDexer.SpinState.INTAKING){
                //if intaking and not full, check for color. if color, move to next slot. repeat until full.
                if(ballCount < 3){
                    testDexer.checkForBalls();
                    if((testDexer.checkForColorAtSpot('P', ballCount +1) || testDexer.checkForColorAtSpot('G', ballCount +1)) && spindexerDelayTimer.seconds() > .5) { //wait until color
                        ballCount++;
                        testDexer.spinToNext("back");

                        spindexerDelayTimer.reset();
                    }
                }
                else{
                    ballCount = 0;
                    testDexer.setSpinState(3);
                }
            }
            if(testDexer.getSpinState() == TestDexer.SpinState.PREPARE_FOR_SHOT){
                testDexer.setUpForShooting(order);
                testDexer.setSpinState(0);
            }
            switch (step) {
                //step 1 - shoot3
                case 0:
                    shooter.shoot3();
                    testDexer.setSpinState(4);
                    step++;
                    break;
                case 1:
                    if(shooter.isActive){
                        shooter.updateState(targetVel);
                        if(shooter.getLauunchState() == testShooter.LaunchState.LAUNCH && testDexer.getSpinState() == TestDexer.SpinState.SHOOT && !launched){
                            testDexer.s1.setPower(.3);
                            testDexer.s2.setPower(.3);
                            launched = true;
                            spinDelay.reset();
                        }
                        break;
                    }
                    testDexer.s1.setPower(0);
                    testDexer.s2.setPower(0);
                    testDexer.setSpinState(0);
                    launched = false;
                    testDexer.shooting = false;
                    ballCount = 0;
                    step++;
                    break;

                case 2:
                    follower.followPath(set2);
                    testDexer.spinToBack(ballCount);
                    testDexer.setSpinState(2);
                    step++;
                    break;
                case 3:
//                    testDexer.setSpinState(2);
                    step++;

                case 4:
                    if (!follower.isBusy()) {
                        intake.runFront();
                        follower.followPath(grab2One);
                        step++;
                    }
                    break;
                case 5:
                    if (!follower.isBusy()) {
                        intake.runFront();
                        follower.followPath(grab2Two);
                        step++;
                    }
                case 6:
                    if (!follower.isBusy()) {
                        intake.runFront();
                        follower.followPath(grab2Three);
                        step++;
                    }
                case 7:
                    if (!follower.isBusy()) {
                        intake.stopFront();
                        follower.followPath(shoot2);
                        step++;
                    }
                    break;

                case 8:
                    if(shooter.isActive){
                        shooter.updateState(targetVel);
                        break;
                    }
                    step++;
                    break;

                case 9:
                    follower.followPath(set3);
                    step++;
                    break;

//                case 7:
//                    if (!follower.isBusy()) {
//                        intake.runFrontReverse();
//                        follower.followPath(grab3);
//                        step++;
//                    }
//                    break;
                case 10:
                    testDexer.setSpinState(2);
                    step++;
                case 11:
                    if (!follower.isBusy()) {
                        intake.runFront();
                        follower.followPath(grab3One);
                        step++;
                    }
                    break;
                case 12:
                    if (!follower.isBusy()) {
                        intake.runFront();
                        follower.followPath(grab3Two);
                        step++;
                    }
                case 13:
                    if (!follower.isBusy()) {
                        intake.runFront();
                        follower.followPath(grab3Three);
                        step++;
                    }

                case 14:
                    if (!follower.isBusy()) {
                        intake.stopFront();
                        follower.followPath(shoot3);
                        step++;
                    }
                    break;

                case 15:
                    if(shooter.isActive){
                        shooter.updateState(targetVel);
                        break;
                    }
                    step++;
                    break;

                case 16:
                    if (!follower.isBusy()) {
                        follower.followPath(set4);
                        step++;
                    }
                    break;

//                case 11:
//                    if (!follower.isBusy()) {
//                        intake.runFrontReverse();
//                        follower.followPath(grab4);
//                        step++;
//                    }
//                    break;
                case 17:
                    testDexer.setSpinState(2);
                    step++;
                case 18:
                    if (!follower.isBusy()) {
                        intake.runFront();
                        follower.followPath(grab4One);
                        step++;
                    }
                    break;
                case 19:
                    if (!follower.isBusy()) {
                        intake.runFront();
                        follower.followPath(grab4Two);
                        step++;
                    }
                case 20:
                    if (!follower.isBusy()) {
                        intake.runFront();
                        follower.followPath(grab4Three);
                        step++;
                    }

                case 21:
                    if (!follower.isBusy()) {
                        intake.stopFront();
                        follower.followPath(shoot4);
                        step++;
                    }
                    break;

                case 22:
                    if(shooter.isActive){
                        shooter.updateState(targetVel);
                        break;
                    }
                    step++;
                    break;

                case 23:
                    follower.followPath(park);
                    step++;
                    break;

                case 24: //May have to add Parametric Call back if not enough time to reach this
                    if(!follower.isBusy()) {
                        desiredTurretAngle = 0;
                        turret.rotateToGoal(desiredTurretAngle);
                    }
                    break;
            }

            // Debug Info
            telemetry.addData("Step", step);
            telemetry.addData("Pose", follower.getPose());
            telemetry.addData("Busy", follower.isBusy());
            telemetry.addData("spindexer pos", spinPos);
            telemetry.addData("target", testDexer.getTargetPos());
            telemetry.update();
        }
    }

    // ---------------------------------------------------------------------
    // PATH GENERATION
    // ---------------------------------------------------------------------
    public void buildPaths() {

        final Pose startPose = new Pose(88, 8,  Math.toRadians(180));
        final Pose shootMid2 = new Pose(96, 54,  Math.toRadians(180));
        final Pose shootPoseFar = new Pose(84, 16, Math.toRadians(180)); //Change Coordinates
        final Pose grabPose1 = new Pose(96, 56, Math.toRadians(180));
        //final Pose grabbed1 = new Pose(121, 56, Math.toRadians(180));
        final Pose grabbed1One = new Pose(100.17, 56, Math.toRadians(180));
        final Pose grabbed1Two = new Pose(104.34, 56, Math.toRadians(180));
        final Pose grabbed1Three = new Pose(108.51, 56, Math.toRadians(180));
        final Pose grabbed1Four = new Pose(112.68, 56, Math.toRadians(180));
        final Pose grabbed1Five = new Pose(116.85, 56, Math.toRadians(180));
        final Pose grabbed1Six = new Pose(121, 56, Math.toRadians(180));

        final Pose rampMid = new Pose(122, 67, Math.toRadians(225));
        final Pose ramp = new Pose(129, 70, Math.toRadians(180));
        final Pose grabPose2 = new Pose(96, 36, Math.toRadians(180));
        //final Pose grabbed2 = new Pose(121, 36, Math.toRadians(180));
        final Pose grabbed2One = new Pose(109.67, 36, Math.toRadians(180));
        final Pose grabbed2Two = new Pose(115.34, 36, Math.toRadians(180));
        final Pose grabbed2Three = new Pose(121, 36, Math.toRadians(180));
        final Pose grabbed2Four = new Pose(121, 36, Math.toRadians(180));
        final Pose grabbed2Five = new Pose(121, 36, Math.toRadians(180));
        final Pose grabbed2Six = new Pose(121, 36, Math.toRadians(180));

        final Pose grabPose3 = new Pose(112, 16, Math.toRadians(180));
        //final Pose grabbed3 = new Pose(134, 16, Math.toRadians(180));
        final Pose grabbed3One = new Pose(119.3, 16, Math.toRadians(180));
        final Pose grabbed3Two = new Pose(126.6, 16, Math.toRadians(180));
        final Pose grabbed3Three = new Pose(134, 16, Math.toRadians(180));
        final Pose grabbed3Four = new Pose(134, 16, Math.toRadians(180));
        final Pose grabbed3Five = new Pose(134, 16, Math.toRadians(180));
        final Pose grabbed3Six = new Pose(134, 16, Math.toRadians(180));

        final Pose parkPose = new Pose(120, 70,   Math.toRadians(180));

        preload = follower.pathBuilder()
                .addPath(new BezierLine(startPose, shootPoseFar))
                .setLinearHeadingInterpolation(startPose.getHeading(), shootPoseFar.getHeading())
                .addParametricCallback(0.75, () -> {shooter.shoot3();})
                .build();

        set2 = follower.pathBuilder()
                .addPath(new BezierLine(shootPoseFar, grabPose1))
                .setLinearHeadingInterpolation(shootPoseFar.getHeading(), grabPose1.getHeading())
                .build();

//        grab2 = follower.pathBuilder()
//                .addPath(new BezierLine(grabPose1, grabbed1))
//                .setLinearHeadingInterpolation(grabPose1.getHeading(), grabbed1.getHeading())
//                .setVelocityConstraint(0)
//                .build();
        grab2One = follower.pathBuilder()
                .addPath(new BezierLine(grabPose1, grabbed1One))
                .setLinearHeadingInterpolation(grabPose1.getHeading(), grabbed1One.getHeading())
                .setVelocityConstraint(0)
                .build();
        grab2Two = follower.pathBuilder()
                .addPath(new BezierLine(grabPose1, grabbed1Two))
                .setLinearHeadingInterpolation(grabPose1.getHeading(), grabbed1Two.getHeading())
                .setVelocityConstraint(0)
                .build();
        grab2Three = follower.pathBuilder()
                .addPath(new BezierLine(grabPose1, grabbed1Three))
                .setLinearHeadingInterpolation(grabPose1.getHeading(), grabbed1Three.getHeading())
                .setVelocityConstraint(0)
                .build();
        grab2Four = follower.pathBuilder()
                .addPath(new BezierLine(grabPose1, grabbed1Three))
                .setLinearHeadingInterpolation(grabPose1.getHeading(), grabbed1Three.getHeading())
                .setVelocityConstraint(0)
                .build();
        grab2Five = follower.pathBuilder()
                .addPath(new BezierLine(grabPose1, grabbed1Three))
                .setLinearHeadingInterpolation(grabPose1.getHeading(), grabbed1Three.getHeading())
                .setVelocityConstraint(0)
                .build();
        grab2Six = follower.pathBuilder()
                .addPath(new BezierLine(grabPose1, grabbed1Three))
                .setLinearHeadingInterpolation(grabPose1.getHeading(), grabbed1Three.getHeading())
                .setVelocityConstraint(0)
                .build();

        emptyRamp = follower.pathBuilder()
                .addPath(new BezierCurve(grabbed1Three, rampMid, ramp))
                .setLinearHeadingInterpolation(grabbed1Three.getHeading(), rampMid.getHeading(), ramp.getHeading())
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

//        grab3 = follower.pathBuilder()
//                .addPath(new BezierLine(grabPose2, grabbed2))
//                .setLinearHeadingInterpolation(grabPose2.getHeading(), grabbed2.getHeading())
//                .build();
        grab3One = follower.pathBuilder()
                .addPath(new BezierLine(grabPose2, grabbed2One))
                .setLinearHeadingInterpolation(grabPose2.getHeading(), grabbed2One.getHeading())
                .build();
        grab3Two = follower.pathBuilder()
                .addPath(new BezierLine(grabPose2, grabbed2Two))
                .setLinearHeadingInterpolation(grabPose2.getHeading(), grabbed2Two.getHeading())
                .build();
        grab3Three = follower.pathBuilder()
                .addPath(new BezierLine(grabPose2, grabbed2Three))
                .setLinearHeadingInterpolation(grabPose2.getHeading(), grabbed2Three.getHeading())
                .build();
        grab3Four = follower.pathBuilder()
                .addPath(new BezierLine(grabPose2, grabbed2Three))
                .setLinearHeadingInterpolation(grabPose2.getHeading(), grabbed2Three.getHeading())
                .build();
        grab3Five = follower.pathBuilder()
                .addPath(new BezierLine(grabPose2, grabbed2Three))
                .setLinearHeadingInterpolation(grabPose2.getHeading(), grabbed2Three.getHeading())
                .build();
        grab3Six = follower.pathBuilder()
                .addPath(new BezierLine(grabPose2, grabbed2Three))
                .setLinearHeadingInterpolation(grabPose2.getHeading(), grabbed2Three.getHeading())
                .build();

        shoot3 = follower.pathBuilder()
                .addPath(new BezierLine(grabbed2Three, shootPoseFar))
                .setLinearHeadingInterpolation(grabbed2Three.getHeading(), shootPoseFar.getHeading())
                .addParametricCallback(0.75, () -> {shooter.shoot3();})
                .build();
        set4 = follower.pathBuilder()
                .addPath(new BezierLine(shootPoseFar, grabPose3))
                .setLinearHeadingInterpolation(shootPoseFar.getHeading(), grabPose3.getHeading())
                .build();
//        grab4 = follower.pathBuilder()
//                .addPath(new BezierLine(grabPose3, grabbed3))
//                .setLinearHeadingInterpolation(grabPose3.getHeading(), grabbed3.getHeading())
//                .build();
        grab4One = follower.pathBuilder()
                .addPath(new BezierLine(grabPose3, grabbed3One))
                .setLinearHeadingInterpolation(grabPose3.getHeading(), grabbed3One.getHeading())
                .build();
        grab4Two = follower.pathBuilder()
                .addPath(new BezierLine(grabPose3, grabbed3Two))
                .setLinearHeadingInterpolation(grabPose3.getHeading(), grabbed3Two.getHeading())
                .build();
        grab4Three = follower.pathBuilder()
                .addPath(new BezierLine(grabPose3, grabbed3Three))
                .setLinearHeadingInterpolation(grabPose3.getHeading(), grabbed3Three.getHeading())
                .build();
        grab4Four = follower.pathBuilder()
                .addPath(new BezierLine(grabPose3, grabbed3Three))
                .setLinearHeadingInterpolation(grabPose3.getHeading(), grabbed3Three.getHeading())
                .build();
        grab4Five = follower.pathBuilder()
                .addPath(new BezierLine(grabPose3, grabbed3Three))
                .setLinearHeadingInterpolation(grabPose3.getHeading(), grabbed3Three.getHeading())
                .build();
        grab4Six = follower.pathBuilder()
                .addPath(new BezierLine(grabPose3, grabbed3Three))
                .setLinearHeadingInterpolation(grabPose3.getHeading(), grabbed3Three.getHeading())
                .build();
        shoot4 = follower.pathBuilder()
                .addPath(new BezierLine(grabbed3Three, shootPoseFar))
                .setLinearHeadingInterpolation(grabbed3Three.getHeading(), shootPoseFar.getHeading())
                .addParametricCallback(0.75, () -> {shooter.shoot3();})
                .build();
        park = follower.pathBuilder()
                .addPath(new BezierLine(shootPoseFar, parkPose))
                .setLinearHeadingInterpolation(shootPoseFar.getHeading(), parkPose.getHeading())
                .build();
    }
}

