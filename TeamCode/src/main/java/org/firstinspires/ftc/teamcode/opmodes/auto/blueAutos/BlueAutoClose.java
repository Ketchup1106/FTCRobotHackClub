package org.firstinspires.ftc.teamcode.opmodes.auto.blueAutos;

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
import org.firstinspires.ftc.teamcode.subsystems.Turret;
import org.firstinspires.ftc.teamcode.subsystems.testShooter;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

@Autonomous(name = "blueAutoClose", group = "Autonomous")
@Disabled

public class BlueAutoClose extends LinearOpMode {

    Follower follower;
    testShooter shooter = new testShooter();
    Intake intake = new Intake();

    Turret turret = new Turret();
    AprilTagStuff aprilTagStuff = new AprilTagStuff();
    // Paths
    PathChain preload, set2, grab2, emptyRamp, shoot2, set3, grab3, shoot3, set4, grab4, shoot4, park;
    ElapsedTime waiter = new ElapsedTime();
    int step = 0;
    int stateVar = 0;
    double targetVel = 0;
    double goalX = 12;
    double goalY = 136;
    double goalDist;
    double goalAngle;
    String order = null;

    double disX;
    double disY;

    public double robotHeading;

    int desiredTurretAngle;

    @Override
    public void runOpMode() {

        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(new Pose(32, 135.5,  Math.toRadians(90)));

        shooter.init(hardwareMap, telemetry);
        intake.init(hardwareMap);
        aprilTagStuff.init(hardwareMap, telemetry);

        buildPaths();   // ← ONLY builds, does NOT run anything

        telemetry.addLine("Ready for start");
        telemetry.update();

        waitForStart();
        ElapsedTime delay = new ElapsedTime();
        ElapsedTime aprilTimer = new ElapsedTime();
        if (isStopRequested()) return;

        // Start first path
        follower.followPath(preload);
        delay.reset();
        aprilTimer.reset();
        while (opModeIsActive()) {
            follower.poseTracker.update();
            disX = goalX - follower.getPose().getX();
            disY = goalY - follower.getPose().getY();
            robotHeading = follower.getHeading(); //will always be something plus that starting of 90

            goalDist = Math.sqrt(Math.pow(disX, 2) + Math.pow(disY, 2)); //pythagorean theorem
            goalAngle = Math.abs(Math.atan2(disX, disY)) + Math.toRadians(90); //simple inverse trig with compensation for robot's extra 90 degrees

            if(step != 17){
                desiredTurretAngle = turret.calculateTurnBlue(goalAngle, robotHeading);
                turret.rotateToGoal(desiredTurretAngle);
            }
            targetVel = shooter.setVel(goalDist);
            aprilTagStuff.update();
            AprilTagDetection id21 = aprilTagStuff.getTagById(21); //gpp
            aprilTagStuff.displayDetection(id21);
            AprilTagDetection id22 = aprilTagStuff.getTagById(22); //pgp
            aprilTagStuff.displayDetection(id22);
            AprilTagDetection id23 = aprilTagStuff.getTagById(23); //ppg
            aprilTagStuff.displayDetection(id23);
            if (aprilTagStuff.patternID == 21) {
                order = "GPP";
                aprilTagStuff.stop();
            }
            if (aprilTagStuff.patternID == 22) {
                order = "PGP";
                aprilTagStuff.stop();
            }
            if (aprilTagStuff.patternID == 23) {
                order = "PPG";
                aprilTagStuff.stop();
            }
            if (aprilTimer.seconds() > 5 && (order == null)){
                order = "No order detected!";
                aprilTagStuff.stop();
            }
            telemetry.addData("Order: ", order);

            targetVel = shooter.setVel(goalDist);
            switch (step) {

                case 0:
                    if(shooter.isActive){
                        shooter.updateState(targetVel);
                        break;
                    }
                    step++;
                    break;

                case 1:
                    follower.followPath(set2);
                    step++;
                    break;

                case 2:
                    if (!follower.isBusy()) {
                        intake.runReverse();
                        follower.followPath(grab2);
                        step++;
                    }
                    break;

                case 3:
                    if (!follower.isBusy()) {
                        intake.stop();
                        follower.followPath(shoot2);
                        step++;
                    }
                    break;

                case 4:
                    if(shooter.isActive){
                        shooter.updateState(targetVel);
                        break;
                    }
                    step++;
                    break;

                case 5:
                    follower.followPath(set3);
                    step++;
                    break;

                case 6:
                    if (!follower.isBusy()) {
                        intake.runReverse();
                        follower.followPath(grab3);
                        step++;
                    }
                    break;

                case 7:
                    if (!follower.isBusy()) {
                        intake.stop();
                        follower.followPath(shoot3);
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
                    if (!follower.isBusy()) {
                        follower.followPath(set4);
                        step++;
                    }
                    break;

                case 10:
                    if (!follower.isBusy()) {
                        intake.runReverse();
                        follower.followPath(grab4);
                        step++;
                    }
                    break;

                case 11:
                    if (!follower.isBusy()) {
                        intake.stop();
                        follower.followPath(shoot4);
                        step++;
                    }
                    break;

                case 12:
                    if(shooter.isActive){
                        shooter.updateState(targetVel);
                        break;
                    }
                    step++;
                    break;

                case 13:
                    follower.followPath(park);
                    step++;
                    break;

                case 14: //May have to add Parametric Call back if not enough time to reach this
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
            telemetry.update();
        }
    }

    // ---------------------------------------------------------------------
    // PATH GENERATION
    // ---------------------------------------------------------------------
    public void buildPaths() {

        final Pose startPose = new Pose(32, 135.5,  Math.toRadians(90));
        //final Pose shootPoseFar = new Pose(50, 90, Math.toRadians(132.5));
        final Pose shootPoseClose = new Pose(48, 96, Math.toRadians(180)); //Change Coordinates
        final Pose grabPose1 = new Pose(40, 84, Math.toRadians(180));
        final Pose grabbed1 = new Pose(23, 84, Math.toRadians(180));
        final Pose rampMid = new Pose(21, 74, Math.toRadians(225));
        final Pose ramp = new Pose(15, 70, Math.toRadians(180));
        final Pose grabPose2 = new Pose(40, 60, Math.toRadians(180));
        final Pose grabbed2 = new Pose(23, 60, Math.toRadians(180));
        final Pose grabPose3 = new Pose(40, 36, Math.toRadians(180));
        final Pose grabbed3 = new Pose(23, 36, Math.toRadians(180));
        final Pose parkPose = new Pose(24, 72,   Math.toRadians(180));

        preload = follower.pathBuilder()
                .addPath(new BezierLine(startPose, shootPoseClose))
                .setLinearHeadingInterpolation(startPose.getHeading(), shootPoseClose.getHeading())
                .addParametricCallback(0.75, () -> {shooter.shoot3();})
                .build();

        set2 = follower.pathBuilder()
                .addPath(new BezierLine(shootPoseClose, grabPose1))
                .setLinearHeadingInterpolation(shootPoseClose.getHeading(), grabPose1.getHeading())
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
                .addPath(new BezierLine(ramp, shootPoseClose))
                .setLinearHeadingInterpolation(ramp.getHeading(), shootPoseClose.getHeading())
                .addParametricCallback(0.75, () -> {shooter.shoot3();})
                .build();
        set3 = follower.pathBuilder()
                .addPath(new BezierLine(shootPoseClose, grabPose2))
                .setLinearHeadingInterpolation(shootPoseClose.getHeading(), grabPose2.getHeading())
                .build();

        grab3 = follower.pathBuilder()
                .addPath(new BezierLine(grabPose2, grabbed2))
                .setLinearHeadingInterpolation(grabPose2.getHeading(), grabbed2.getHeading())
                .build();

        shoot3 = follower.pathBuilder()
                .addPath(new BezierLine(grabbed2, shootPoseClose))
                .setLinearHeadingInterpolation(grabbed2.getHeading(), shootPoseClose.getHeading())
                .addParametricCallback(0.75, () -> {shooter.shoot3();})
                .build();
        set4 = follower.pathBuilder()
                .addPath(new BezierLine(shootPoseClose, grabPose3))
                .setLinearHeadingInterpolation(shootPoseClose.getHeading(), grabPose3.getHeading())
                .build();
        grab4 = follower.pathBuilder()
                .addPath(new BezierLine(grabPose3, grabbed3))
                .setLinearHeadingInterpolation(grabPose3.getHeading(), grabbed3.getHeading())
                .build();
        shoot4 = follower.pathBuilder()
                .addPath(new BezierLine(grabbed3, shootPoseClose))
                .setLinearHeadingInterpolation(grabbed3.getHeading(), shootPoseClose.getHeading())
                .addParametricCallback(0.75, () -> {shooter.shoot3();})
                .build();
        park = follower.pathBuilder()
                .addPath(new BezierLine(shootPoseClose, parkPose))
                .setLinearHeadingInterpolation(shootPoseClose.getHeading(), parkPose.getHeading())
                .build();
    }
}

