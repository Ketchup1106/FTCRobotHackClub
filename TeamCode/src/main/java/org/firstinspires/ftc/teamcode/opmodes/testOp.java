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
//import org.firstinspires.ftc.teamcode.subsystems.Shooter;
import org.firstinspires.ftc.teamcode.subsystems.Turret;
import org.firstinspires.ftc.teamcode.subsystems.TestDexer;
import org.firstinspires.ftc.teamcode.subsystems.testShooter;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;


@TeleOp(name = "testOpBlue")
public class testOp extends OpMode {
    ElapsedTime runtime = new ElapsedTime();
    ArcadeDrive drive = new ArcadeDrive();
    Follower follower;
    testShooter shooter = new testShooter();
    TestDexer testDexer = new TestDexer();
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
    public String order = "None yet";
    double powerSetter = .2;
    double targetVel = 0;
    int ballCount = 0;
    int shoot = 0;
    public double robotHeading;
    double hoodAngle;
    double velocity;

    int desiredTurretAngle;
    String intakeSide = "front";


    boolean slowMode = false;
    boolean turningToShoot = false;
    boolean isCheckingForApril = true;
    ElapsedTime aprilTimer = new ElapsedTime();
    ElapsedTime spindexerDelayTimer = new ElapsedTime();
    boolean doesAprilTimerHaveToReset = true;
    boolean homing = false;


    @Override
    public void init(){
        drive.init(hardwareMap);
        shooter.init(hardwareMap, telemetry);
        intake.init(hardwareMap);
        follower = Constants.createFollower(hardwareMap);
        //follower.setStartingPose(new Pose(32, 135.5,  Math.toRadians(90)));
        follower.setStartingPose(new Pose(8, 8,  Math.toRadians(90)));
        follower.update();
        //touchy1.init(hardwareMap);
        aprilTagStuff.init(hardwareMap, telemetry);
        turret.init(hardwareMap);
        testDexer.init(hardwareMap);


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
        goalAngle = Math.abs(Math.atan2(disX, disY)) + Math.toRadians(90); //simple inverse trig with compensation for robot's extra 90 degrees
        desiredTurretAngle = turret.calculateTurnBlue(goalAngle, robotHeading);


        if(gamepad1.dpadDownWasPressed()){ //corner calibration
            follower.setPose(new Pose(33, 135, 90));
        }
        turret.rotateToGoal(desiredTurretAngle);


//        velocity = (0.00831624*Math.pow(goalDist, 2)) +
//                    (4.13011*goalDist) +
//                    (877.46841);
//        hoodAngle = (-(2.47416*(1/(Math.pow(10, 8)))) * Math.pow(goalDist, 4)) +
//                    (0.00000985354* Math.pow(goalDist, 3)) -
//                    (0.00136643*Math.pow(goalDist, 2)) +
//                    (0.0833032*goalDist) -
//                    (1.41432);



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
            aprilTagStuff.stop();
        }
        if (aprilTagStuff.patternID == 22) {
            isCheckingForApril = false;
            order = "PGP";
            aprilTagStuff.stop();
        }
        if (aprilTagStuff.patternID == 23) {
            isCheckingForApril = false;
            order = "PPG";
            aprilTagStuff.stop();
        }
        if(aprilTimer.seconds() > 15 && isCheckingForApril){
            isCheckingForApril = false;
            order = "No order was detected within 15 seconds!";
            aprilTagStuff.stop();
        }
        telemetry.addData("Order: ", order);
        //follower.setTeleOpDrive(-gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);
        drive.drive(-gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x * .5, 1);
        //drive.te(-gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x, powerSetter);
//        if (gamepad2.bWasPressed()) { //shoot 1
//            shooter.shoot1();
//        }



        //NEW CONTROLS _______________________________________________________________________________

        if(gamepad1.right_trigger > 0.1){
            intake.run();
            testDexer.setSpinState(1);
        }
        else if(gamepad1.rightBumperWasPressed()){
            intake.runReverse();
        }
        else{
            intake.stop();
        }
        if(gamepad1.right_stick_button){
            slowMode = !slowMode;
        }
        //step 1 select intake
        if(gamepad1.dpadUpWasPressed()){
            intakeSide = "front";
            if(ballCount < 3){
                testDexer.setSpinState(1);
            }

        }
        if(gamepad2.dpadDownWasPressed()){
            intakeSide = "back";
            if(ballCount < 3){
                testDexer.setSpinState(1);
            }

        }
        if(intakeSide.equals("front")){
            if(testDexer.getSpinState() == TestDexer.SpinState.MOVE_TO_INTAKE){
                testDexer.spinToFront();
                testDexer.setSpinState(2);
            }
        }else{
            if(testDexer.getSpinState() == TestDexer.SpinState.MOVE_TO_INTAKE){ //if we are prepping for shot then dont spin to intake
                testDexer.spinToBack();
                testDexer.setSpinState(2);
            }
        }

        //step 2 sort balls
        if(testDexer.getSpinState() == TestDexer.SpinState.INTAKING){
            //if intaking and not full, check for color. if color, move to next slot. repeat until full.
            if(ballCount < 3){
                testDexer.checkForBalls();
                if(!testDexer.getPatternOrSpots(String.valueOf(ballCount)).equals("U") && spindexerDelayTimer.seconds() > .5) { //wait until color
                    ballCount++;
                    testDexer.spinToNext(intakeSide);
                    spindexerDelayTimer.reset();
                }
            }
            else{
                testDexer.setSpinState(3);
            }
        }
        //step 3 set up for shot
        if(testDexer.getSpinState() == TestDexer.SpinState.PREPARE_FOR_SHOT){
            testDexer.setUpForShooting(order);
            testDexer.setSpinState(0);
        }
        //step 4 shoot
        if(gamepad2.left_trigger > 0.1){ //Need to edit or change button
            shooter.shoot3();
            testDexer.setSpinState(4);
        }
        if(testDexer.getSpinState() == TestDexer.SpinState.SHOOT && shooter.getLauunchState() == testShooter.LaunchState.LAUNCH){
            testDexer.shoot();


        }if(shooter.getLauunchState() == testShooter.LaunchState.RECOVER){
            testDexer.setSpinState(0);
            ballCount = 0;
        }
        //        if(gamepad2.right_stick_x > 0){ not necessary due to auto tracking
//            turret.rotate(gamepad2.right_stick_x);
//        }
//        if(gamepad2.leftBumperWasPressed()){
//            //add turret slowMode
//        }

        if(gamepad2.right_trigger > 0.1){
            //add shoot 3 in pattern;
        }
        if(gamepad1.dpadRightWasPressed()){
            testDexer.spinToNextManual("front");
        }
        /*OLD CONTROLS ------------------------------------------------------------------------------
        if (gamepad2.yWasPressed()) { // shoot 3
            shooter.shoot3();
        }
        if(gamepad1.yWasPressed()){
            shooter.setShootState();
        }

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
            shooter.spinManual("front");
        }
        if(gamepad2.dpadLeftWasPressed()){
            shooter.setFrontOrBack("back");
            shooter.spinManual("back");
        }
        --------------------------------------------------------------*/

        if(slowMode){
            powerSetter = 0.2;
        }
        else{
            powerSetter = 0.75;
        }
        int spinPos = testDexer.updatePos();
        testDexer.setPowerToPosition(spinPos);
        shooter.updateState(targetVel);
        targetVel = shooter.setVel(goalDist);
        shooter.setHood(goalDist);




        ;
        //telemetry.addData("Button status: ", touchy1.detectTouch());
        telemetry.addData("angle difference from goal", Math.toDegrees(goalAngle) - Math.toDegrees(follower.getHeading()));
        telemetry.addData("shooter target velocity: ", targetVel);
        telemetry.addData("shootervel: ", shooter.getVelocity1());
        telemetry.addData("shooter state: ", shooter.getLauunchState());
        telemetry.addData("spindexer pos", spinPos);
        telemetry.addData("spinstate ", testDexer.getSpinState());
        telemetry.addData("spindexer target ", testDexer.getTargetPos());

        //telemetry.addData("tuningservo pos", shooter.getServo());
        telemetry.addData("Amount to Shoot: ", shooter.getAmountTOShoot());
        telemetry.addData("Follower X: ", follower.getPose().getX());
        telemetry.addData("Follower Y ", follower.getPose().getY());
        telemetry.addData("Follower heading rads ", follower.getPose().getHeading());
        telemetry.addData("Follower heading degs ", Math.toDegrees(follower.getPose().getHeading()));
        telemetry.addData("Goal Dist: ", goalDist);
        telemetry.addData("difference of turret to goal", Math.toDegrees(goalAngle) - Math.toDegrees(turret.getPosWithoutSubtractionFactor()) + 90 );
        telemetry.addData("angle from robot to goal", Math.toDegrees(goalAngle));
        telemetry.addData("is turning?", turningToShoot);
        telemetry.addData("is busy?", turret.getTurretStatus());
        telemetry.addData("turret angle: ", turret.getCurrentPos()/turret.ticksPerDegree);
        telemetry.addData("turret angle: ", turret.getCurrentPos());
        telemetry.addData("turnneeded", Math.toDegrees(turret.turnNeeded/turret.ticksPerRadian));

        telemetry.update();
    }//

}
