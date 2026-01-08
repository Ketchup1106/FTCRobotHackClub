package org.firstinspires.ftc.teamcode.autos;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.subsystems.AprilTagStuff;
import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.testShooter;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

@Autonomous(name = "Blue Far", group = "Autonomous")
public class blueFarAuto extends LinearOpMode {

    Follower follower;
    testShooter shooter = new testShooter();
    Intake intake = new Intake();
    AprilTagStuff aprilTagStuff = new AprilTagStuff();
    double goalDist;
    double goalAngle;

    double goalX = 12;

    double goalY = 136;
    double targetVel = 0;
    String order = null;

    // Paths
    PathChain preload, set2, grab2, shoot2, set3, grab3, shoot3, park;
    int step = 0;

    @Override
    public void runOpMode() {

        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(new Pose(56, 8, Math.toRadians(180)));
        aprilTagStuff.init(hardwareMap, telemetry);
        shooter.init(hardwareMap, telemetry);
        intake.init(hardwareMap);

        buildPaths();   //ONLY builds, does NOT run anything

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

            follower.update();

            goalDist = Math.sqrt(Math.pow(goalX - follower.getPose().getX(), 2) + Math.pow(goalY - follower.getPose().getY(), 2));
            goalAngle = Math.abs(Math.asin((goalX - follower.getPose().getX()) / goalDist)) + Math.toRadians(90);
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

            switch (step) {
                // ---------------------------------------------------------
                // Step 0: PRELOAD → SHOOT
                // ---------------------------------------------------------
                case 0:
                    if (!follower.isBusy()) {
                        shooter.shoot3();
                        step++;
                    }
                    break;
                // ---------------------------------------------------------
                // Step 1: cycle updateState
                // ---------------------------------------------------------
                case 1:

                    if(shooter.isActive){
                        shooter.updateState(targetVel);
                        break;
                    }
                    step++;
                    break;
                // ---------------------------------------------------------
                // Step 2: LINE UP WITH FIRST SET
                // ---------------------------------------------------------
                case 2:

                    follower.followPath(set2);
                    step++;
                    break;
                // ---------------------------------------------------------
                // Step 3: DRIVE TO FIRST GRAB
                // ---------------------------------------------------------
                case 3:
                    if (!follower.isBusy()) {
                        intake.runReverse();
                        follower.followPath(grab2);
                        step++;
                    }
                    break;

                // ---------------------------------------------------------
                // Step 4: RETURN TO SHOOT POSITION
                // ---------------------------------------------------------
                case 4:
                    if (!follower.isBusy()) {
                        intake.stop();
                        follower.followPath(shoot2);
                        step++;
                    }
                    break;

                // ---------------------------------------------------------
                // Step 5: SHOOT SECOND SET
                // ---------------------------------------------------------
                case 5:
                    if (!follower.isBusy()) {
                        shooter.shoot3();
                        step++;
                    }
                    break;
                // ---------------------------------------------------------
                // Step 6: cycle updatestate
                // ---------------------------------------------------------
                case 6:
                    if(shooter.isActive){
                        shooter.updateState(targetVel); //CHANGE LATER
                        break;
                    }
                    step++;
                    break;

                // ---------------------------------------------------------
                // Step 7: LINE UP TO GRAB SET 3
                // ---------------------------------------------------------
                case 7:
                    follower.followPath(set3);
                    step++;
                    break;
                // ---------------------------------------------------------
                // Step 8: DRIVE TO SECOND GRAB
                // ---------------------------------------------------------
                case 8:
                    if (!follower.isBusy()) {
                        intake.runReverse();
                        follower.followPath(grab3);
                        step++;
                    }
                    break;

                // ---------------------------------------------------------
                // Step 9: RETURN TO SHOOT AGAIN
                // ---------------------------------------------------------
                case 9:
                    if (!follower.isBusy()) {
                        intake.stop();
                        follower.followPath(shoot3);
                        step++;
                    }
                    break;

                // ---------------------------------------------------------
                // Step 10: SHOOT THIRD SET
                // ---------------------------------------------------------
                case 10:
                    if (!follower.isBusy()) {
                        shooter.shoot3();
                        step++;
                    }
                    break;
                // ---------------------------------------------------------
                // Step 11: cycle updatestate
                // ---------------------------------------------------------
                case 11:
                    if(shooter.isActive){
                        shooter.updateState(targetVel); //CHANGE LATER
                        break;
                    }
                    step++;
                    break;

                // ---------------------------------------------------------
                // Step 12: PARK → AUTO DONE
                // ---------------------------------------------------------
                case 12:
                    // once park is done, stop doing anything
                    follower.followPath(park);
                    break;
            }

            // Debug Info
            telemetry.addData("Step", step);
            telemetry.addData("Pose", follower.getPose());
            telemetry.addData("Busy", follower.isBusy());
            telemetry.addData("Launch state: ", shooter.getLauunchState());
            telemetry.update();
        }
    }

    // ---------------------------------------------------------------------
    // PATH GENERATION
    // ---------------------------------------------------------------------
    public void buildPaths() {

        final Pose startPose   = new Pose(56, 8,  Math.toRadians(180));
        final Pose shootPose   = new Pose(56, 8, Math.toRadians(180));
        final Pose grabPose1   = new Pose(50, 35, Math.toRadians(180));
        final Pose grabbed1    = new Pose(10, 35, Math.toRadians(180));
        final Pose grabPose2   = new Pose(64, 58, Math.toRadians(180)); //change these poses
        final Pose grabbed2    = new Pose(8, 58, Math.toRadians(180));
        final Pose parkPose    = new Pose(36, 72,   Math.toRadians(180));

        preload = follower.pathBuilder()
                .addPath(new BezierLine(startPose, shootPose))
                .setLinearHeadingInterpolation(startPose.getHeading(), shootPose.getHeading())
                .build();

        set2 = follower.pathBuilder()
                .addPath(new BezierLine(shootPose, grabPose1))
                .setLinearHeadingInterpolation(shootPose.getHeading(), grabPose1.getHeading())
                .build();

        grab2 = follower.pathBuilder()
                .addPath(new BezierLine(grabPose1, grabbed1))
                .setLinearHeadingInterpolation(grabPose1.getHeading(), grabbed1.getHeading())
                .setVelocityConstraint(5)
                .build();

        shoot2 = follower.pathBuilder()
                .addPath(new BezierLine(grabbed1, shootPose))
                .setLinearHeadingInterpolation(grabbed1.getHeading(), shootPose.getHeading())
                .build();

        set3 = follower.pathBuilder()
                .addPath(new BezierLine(shootPose, grabPose2))
                .setLinearHeadingInterpolation(shootPose.getHeading(), grabPose2.getHeading())
                .build();

        grab3 = follower.pathBuilder()
                .addPath(new BezierLine(grabPose2, grabbed2))
                .setLinearHeadingInterpolation(grabPose2.getHeading(), grabbed2.getHeading())
                .build();

        shoot3 = follower.pathBuilder()
                .addPath(new BezierLine(grabbed2, shootPose))
                .setLinearHeadingInterpolation(grabbed2.getHeading(), shootPose.getHeading())
                .build();

        park = follower.pathBuilder()
                .addPath(new BezierLine(shootPose, parkPose))
                .setLinearHeadingInterpolation(shootPose.getHeading(), parkPose.getHeading())
                .build();
    }
}
