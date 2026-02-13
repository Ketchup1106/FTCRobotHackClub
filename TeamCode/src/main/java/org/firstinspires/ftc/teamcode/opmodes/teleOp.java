package org.firstinspires.ftc.teamcode.opmodes;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.subsystems.AprilTagStuff;
import org.firstinspires.ftc.teamcode.subsystems.ArcadeDrive;
import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.SpinDexer;
import org.firstinspires.ftc.teamcode.subsystems.testShooter;
import org.firstinspires.ftc.teamcode.subsystems.Turret;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;


@TeleOp(name = "TeleOp")
public class teleOp extends OpMode {
    int aprilTagTelemtryIndex = 0;

    ElapsedTime runtime = new ElapsedTime();
    ArcadeDrive drive = new ArcadeDrive();
    Follower follower;
    testShooter shooter = new testShooter();

    Turret turret = new Turret();

    SpinDexer spindexer = new SpinDexer();
    double goalX = 12;

    double goalY = 136;

    Intake intake = new Intake();

    //TouchySensor touchy1 = new TouchySensor();
    AprilTagStuff aprilTagStuff = new AprilTagStuff();


    boolean automatedDrive = false;

    double goalDist;
    double goalAngle;
    String order;
    double powerSetter = .2;
    double targetVel = 0;
    int shoot = 0;

    boolean slowMode = false;
    boolean turningToShoot = false;

    @Override
    public void init(){
        drive.init(hardwareMap);
        shooter.init(hardwareMap, telemetry);
        intake.init(hardwareMap);
        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(new Pose(32, 135.5,  Math.toRadians(90)));
        follower.update();
        //touchy1.init(hardwareMap);
        aprilTagStuff.init(hardwareMap, telemetry);

    }

    @Override
    public void loop(){
//        if(gamepad1.dpadRightWasPressed()){
//            automatedDrive = true;
//        }
//        if(gamepad1.dpadRightWasReleased()){
//            automatedDrive = false;
//        }
//        if(automatedDrive){
//            automatedDrive(follower.getPose());
//        }
        follower.update();

        goalDist = Math.sqrt(Math.pow(goalX - follower.getPose().getX(), 2) + Math.pow(goalY - follower.getPose().getY(), 2));
        goalAngle = Math.abs(Math.asin((goalX - follower.getPose().getX()) / goalDist)) + Math.toRadians(90);

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
        telemetry.addData("Order: ", order);

        drive.drive(-gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x, powerSetter);
        //if (gamepad2.bWasPressed()) { //shoot 1
         //   shooter.shoot1();
        //}
        if (gamepad2.yWasPressed()) { // shoot 3
            shooter.shoot3();
        }
        if (gamepad1.right_trigger > 0.1) { //
            intake.runFront();
        } else {
            //intake.stop();
        }
        if (gamepad1.left_trigger > 0.1) { //
            intake.runFrontReverse();
        } else {
            //intake.stop();
        }
        if (gamepad1.leftBumperWasPressed()) {
            slowMode = !slowMode;
        }
        if(slowMode == true){
            powerSetter = 0.2;
        }
        else{
            powerSetter = 0.75;
        }
        /*
        if (gamepad2.dpadUpWasPressed()) {
            shooter.startFeeder();
        }
        if (gamepad2.dpadDownWasPressed()) {
            shooter.stopFeeder();
        }
        */


        if (shooter.isActive) {
            //shooter.updateState(targetVel, tes, spindexer.targetPos);
        }

//        if(spindexer.frontTouchyActive()){
//            spindexer.rotateToFront();
//        }
//
//        if(spindexer.rearTouchyActive()){
//            spindexer.rotateToBack();
//        }


        targetVel = shooter.setVel(goalDist);




        //telemetry.addData("Button status: ", touchy1.detectTouch());
        telemetry.addData("angle difference from goal", Math.toDegrees(goalAngle) - Math.toDegrees(follower.getHeading()));

        telemetry.addData("shooter target velocity: ", targetVel);
        telemetry.addData("shootervel: ", shooter.getVelocity1());
        telemetry.addData("state: ", shooter.getLauunchState());
        //telemetry.addData("tuningservo pos", shooter.getServo());
        telemetry.addData("Amount to Shoot: ", shooter.getAmountTOShoot());
        telemetry.addData("Follower X: ", follower.getPose().getX());
        telemetry.addData("Follower Y ", follower.getPose().getY());
        telemetry.addData("Goal Dist: ", goalDist);
        telemetry.addData("current robot angle", Math.toDegrees(follower.getHeading()));
        telemetry.addData("angle from robot to goal", Math.toDegrees(goalAngle));
        telemetry.addData("is turning?", turningToShoot);
        telemetry.addData("automated drive?", automatedDrive);
        telemetry.update();
    }//

}
