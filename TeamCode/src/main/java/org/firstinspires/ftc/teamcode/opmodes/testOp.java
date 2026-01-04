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
//import org.firstinspires.ftc.teamcode.subsystems.Shooter;
import org.firstinspires.ftc.teamcode.subsystems.Turret;
import org.firstinspires.ftc.teamcode.subsystems.testShooter;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;


@TeleOp(name = "testOp")
public class testOp extends OpMode {
    ElapsedTime runtime = new ElapsedTime();
    ArcadeDrive drive = new ArcadeDrive();
    Follower follower;
    testShooter shooter = new testShooter();

    Turret turret = new Turret();

    //SpinDexer spindexer = new SpinDexer();
    double goalX = 12;

    double goalY = 136;

    double disX;
    double disY;

    Intake intake = new Intake();

    //TouchySensor touchy1 = new TouchySensor();
    AprilTagStuff aprilTagStuff = new AprilTagStuff();


    boolean automatedDrive = false;

    double goalDist;
    double goalAngle;
    public String order;
    double powerSetter = .2;
    double targetVel = 0;
    int shoot = 0;
    public double robotHeading;

    double desiredTurretAngle;


    boolean slowMode = false;
    boolean turningToShoot = false;
    boolean isCheckingForApril = true;
    ElapsedTime aprilTimer = new ElapsedTime();
    boolean doesAprilTimerHaveToReset = true;
    boolean homing = false;

    @Override
    public void init(){
        drive.init(hardwareMap);
        shooter.init(hardwareMap, telemetry);
        intake.init(hardwareMap);
        follower = Constants.createFollower(hardwareMap);
        //spindexer.init(hardwareMap);
        //follower.setStartingPose(new Pose(32, 135.5,  Math.toRadians(90)));
        follower.setStartingPose(new Pose(8, 8,  Math.toRadians(90)));
        follower.update();
        //touchy1.init(hardwareMap);
        aprilTagStuff.init(hardwareMap, telemetry);
        turret.init(hardwareMap);


    }
    @Override
    public void start() {
        //The parameter controls whether the Follower should use break mode on the motors (using it is recommended).
        //In order to use float mode, add .useBrakeModeInTeleOp(true); to your Drivetrain Constants in Constant.java (for Mecanum)
        //If you don't pass anything in, it uses the default (false)
        follower.startTeleopDrive();
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
        follower.poseTracker.update();

        if(doesAprilTimerHaveToReset){
            aprilTimer.reset();
            doesAprilTimerHaveToReset = false;
        }

        disX = goalX - follower.getPose().getX();
        disY = goalY - follower.getPose().getY();
        robotHeading = follower.getHeading(); //will always be something plus that starting of 90

        goalDist = Math.sqrt(Math.pow(disX, 2) + Math.pow(disY, 2)); //pythagorean theorem
        goalAngle = Math.atan2(disY, disX) + Math.toRadians(90); //simple inverse trig with compensation for robot's extra 90 degrees
        turret.rotateToGoal(goalAngle, robotHeading);




        if (isCheckingForApril){
            aprilTagStuff.update();
            AprilTagDetection id21 = aprilTagStuff.getTagById(21); //gpp
            aprilTagStuff.displayDetection(id21);
            AprilTagDetection id22 = aprilTagStuff.getTagById(22); //pgp
            aprilTagStuff.displayDetection(id22);
            AprilTagDetection id23 = aprilTagStuff.getTagById(23); //ppg
            aprilTagStuff.displayDetection(id23);
        }

        if (aprilTagStuff.patternID == 21) {
            isCheckingForApril = false;
            order = "GPP";
            shooter.setOrder(order);
            aprilTagStuff.stop();
        }
        if (aprilTagStuff.patternID == 22) {
            isCheckingForApril = false;
            order = "PGP";
            shooter.setOrder(order);
            aprilTagStuff.stop();
        }
        if (aprilTagStuff.patternID == 23) {
            isCheckingForApril = false;
            order = "PPG";
            shooter.setOrder(order);
            aprilTagStuff.stop();
        }
        if(aprilTimer.seconds() > 15 && isCheckingForApril){
            isCheckingForApril = false;
            order = "No order was detected within 15 seconds!";
            shooter.setOrder(order);
            aprilTagStuff.stop();
        }
        telemetry.addData("Order: ", order);
        //follower.setTeleOpDrive(-gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);
        drive.drive(-gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x * .5, 1);
        //drive.te(-gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x, powerSetter);
//        if (gamepad2.bWasPressed()) { //shoot 1
//            shooter.shoot1();
//        }
        if (gamepad2.yWasPressed()) { // shoot 3
            //turret.rotateToGoal(goalAngle);
            //turningToShoot = true;
            shooter.shoot3();
        }
        /*
        if(turningToShoot == true){
            if(turret.getCurrentPos() == goalAngle){
                shooter.shoot3();
                turningToShoot = false;
                turret.stop();
            }
        }
         */
        if (gamepad1.right_trigger > 0.1) { //
            intake.run();
        }
        else if(gamepad1.left_trigger > 0.1){
            intake.runReverse();
            shooter.setIntakeState();
        }
        else {
            intake.stop();
        }
        if(gamepad2.dpadRightWasPressed()){
            shooter.setFrontOrBack("front");
        }
        if(gamepad2.dpadLeftWasPressed()){
            shooter.setFrontOrBack("back");
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
//        if (gamepad2.dpadUpWasPressed()) {
//            shooter.startFeeder();
//        }
//        if (gamepad2.dpadDownWasPressed()) {
//            shooter.stopFeeder();
//        }


        shooter.updateState(targetVel);
        if(gamepad1.dpadDownWasPressed()){ //corner calibration
            follower.setPose(new Pose(8, 8, 90));
            homing = true;
            turret.isHomed = false;
        }
        if(homing){
            turret.home();
            if(turret.isHomed){
                homing = false;
            }
        }

//        if(spindexer.frontTouchyActive()){
//            spindexer.rotateToFront();
//        }
//
//        if(spindexer.rearTouchyActive()){
//            spindexer.rotateToBack();
//        }


        targetVel = shooter.setVel(goalDist);



        ;
        //telemetry.addData("Button status: ", touchy1.detectTouch());
        telemetry.addData("angle difference from goal", Math.toDegrees(goalAngle) - Math.toDegrees(follower.getHeading()));
        telemetry.addData("shooter target velocity: ", targetVel);
        telemetry.addData("shootervel: ", shooter.getVelocity1());
        telemetry.addData("shooter state: ", shooter.getLauunchState());
        //telemetry.addData("servo pos", shooter.getServo());
        telemetry.addData("Amount to Shoot: ", shooter.getAmountTOShoot());
        telemetry.addData("Follower X: ", follower.getPose().getX());
        telemetry.addData("Follower Y ", follower.getPose().getY());
        telemetry.addData("Follower heading rads ", follower.getPose().getHeading());
        telemetry.addData("Follower heading degs ", Math.toDegrees(follower.getPose().getHeading()));
        telemetry.addData("Goal Dist: ", goalDist);

        telemetry.addData("angle from robot to goal", Math.toDegrees(goalAngle));
        telemetry.addData("is turning?", turningToShoot);
        telemetry.addData("turret angle: ", turret.getPosWithoutSubtractionFactor());
        telemetry.addData("turnneeded", turret.turnNeeded);

        telemetry.update();
    }//

}
