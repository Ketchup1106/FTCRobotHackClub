//package org.firstinspires.ftc.teamcode.opmodes.auto.blueAutos;
//
//import com.pedropathing.follower.Follower;
//import com.pedropathing.geometry.BezierCurve;
//import com.pedropathing.geometry.BezierLine;
//import com.pedropathing.geometry.Pose;
//import com.pedropathing.paths.PathChain;
//import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
//import com.qualcomm.robotcore.eventloop.opmode.Disabled;
//import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
//import com.qualcomm.robotcore.util.ElapsedTime;
//
//import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
//import org.firstinspires.ftc.teamcode.subsystems.AprilTagStuff;
//import org.firstinspires.ftc.teamcode.subsystems.Intake;
//import org.firstinspires.ftc.teamcode.subsystems.TestDexer;
//import org.firstinspires.ftc.teamcode.subsystems.Turret;
//import org.firstinspires.ftc.teamcode.subsystems.testShooter;
//import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
//
//@Autonomous(name = "blue far final", group = "Autonomous")
//@Disabled
//public class BlueFarFinal extends LinearOpMode {
//
//    Follower follower;
//    testShooter shooter = new testShooter();
//    Intake intake = new Intake();
//    TestDexer testDexer = new TestDexer();
//    Turret turret = new Turret();
//    AprilTagStuff aprilTagStuff = new AprilTagStuff();
//    // Paths
//    PathChain preload, set2, grab2, emptyRamp, shoot2, set3, grab3, shoot3, set4, grab4, shoot4, park;
//    ElapsedTime waiter = new ElapsedTime();
//    int step = 0;
//    int stateVar = 0;
//    double targetVel = 0;
//    double goalX = 12;
//    double goalY = 136;
//    double goalDist;
//    double goalAngle;
//    String order = "null";
//    double disX;
//    double disY;
//    String intakeSide = "front";
//    int ballCount = 3;
//    boolean launched = false;
//
//    public double robotHeading;
//
//    int desiredTurretAngle;
//
//    @Override
//    public void runOpMode() {
//        int spinPos;
//
//        follower = Constants.createFollower(hardwareMap);
//        follower.setStartingPose(new Pose(56, 8,  Math.toRadians(180)));
//        turret.init(hardwareMap);
//        testDexer.init(hardwareMap);
//        shooter.init(hardwareMap, telemetry);
//        intake.init(hardwareMap);
//        aprilTagStuff.init(hardwareMap, telemetry);
//
//        buildPaths();   // ← ONLY builds, does NOT runFront anything
//        ElapsedTime runtime = new ElapsedTime();
//        testDexer.currentOrder = "PPG";
//        //init sequence - get servios up to speed, reset pos, set up for shot
//        while(runtime.seconds() < 3){
//            testDexer.s1.setPower(-1);
//            testDexer.s2.setPower(-1);
//            telemetry.addLine("WAIT");
//        }
//        spinPos = testDexer.updatePos();
//        while (spinPos < testDexer.getTargetPos() -150){
//            spinPos = testDexer.updatePos();
//            testDexer.setPowerToPosition2(spinPos, runtime.seconds());
//            telemetry.addData("pose", spinPos);
//        }
//
//        testDexer.s1.setPower(0);
//        testDexer.s2.setPower(0);
//        testDexer.currentOrder = "PPG";
//        telemetry.addLine("Ready for start");
//        telemetry.update();
//
//
//        ElapsedTime aprilTimer = new ElapsedTime();
//        ElapsedTime spinDelay = new ElapsedTime();
//        waitForStart();
//
//        if (isStopRequested()) return;
//
//        // Start first path
//        follower.followPath(preload);
//        runtime.reset();
//        aprilTimer.reset();
//        spinDelay.reset();
//        while (opModeIsActive()) {
//            spinPos = testDexer.updatePos();
//            follower.poseTracker.update();
//            disX = goalX - follower.getPose().getX();
//            disY = goalY - follower.getPose().getY();
//            robotHeading = follower.getHeading(); //will always be something plus that starting of 90
//            goalDist = Math.sqrt(Math.pow(disX + 2.5, 2) + Math.pow(disY, 2)); //pythagorean theorem
//            goalAngle = Math.abs(Math.atan2(disX + 2.5, disY)) + Math.toRadians(90); //simple inverse trig with compensation for robot's extra 90 degrees
//            if(step != 17){
//                desiredTurretAngle = turret.calculateTurnBlue(goalAngle, robotHeading);
//                turret.rotateToGoal(desiredTurretAngle);
//            } //turret
//            targetVel = shooter.setVel(goalDist);
//            shooter.setHood(goalDist);
//            //apriltag --------------------------------------------------------
////            aprilTagStuff.update();
////            AprilTagDetection id21 = aprilTagStuff.getTagById(21); //gpp
////            aprilTagStuff.displayDetection(id21);
////            AprilTagDetection id22 = aprilTagStuff.getTagById(22); //pgp
////            aprilTagStuff.displayDetection(id22);
////            AprilTagDetection id23 = aprilTagStuff.getTagById(23); //ppg
////            aprilTagStuff.displayDetection(id23);
////            if (aprilTagStuff.patternID == 21) {
////                order = "GPP";
////                aprilTagStuff.stop();
////            }
////            if (aprilTagStuff.patternID == 22) {
////                order = "PGP";
////                aprilTagStuff.stop();
////            }
////            if (aprilTagStuff.patternID == 23) {
////                order = "PPG";
////                aprilTagStuff.stop();
////            }
//            if (aprilTimer.seconds() > 5 && (order.equals("null"))){
//                order = "No order detected!";
//                aprilTagStuff.stop();
//            }
//            telemetry.addData("Order: ", order);
//            //-------------------------------------------------------------------------
//
//            if(step > 2){
//                testDexer.setPowerToPosition2(spinPos, runtime.seconds());
//            }
////            //step 1 select intake
////            if(intakeSide.equals("front")){
////                if(testDexer.getSpinState() == TestDexer.SpinState.MOVE_TO_INTAKE){
////                    testDexer.spinToFront(ballCount);
////                    testDexer.setSpinState(2);
////                }
////            }else{
////                if(testDexer.getSpinState() == TestDexer.SpinState.MOVE_TO_INTAKE){ //if we are prepping for shot then dont spin to intake
////                    testDexer.spinToBack(ballCount);
////                    testDexer.setSpinState(2);
////                }
////            }
////
////            //step 2 sort balls
////            if(testDexer.getSpinState() == TestDexer.SpinState.INTAKING){
////                //if intaking and not full, check for color. if color, move to next slot. repeat until full.
////                if(ballCount < 3){
////                    testDexer.checkForBalls();
////                    if((testDexer.checkForColorAtSpot('P', ballCount +1) || testDexer.checkForColorAtSpot('G', ballCount +1)) && spinDelay.seconds() > .25) { //wait until color
////                        ballCount++;
////                        testDexer.spinToNext(intakeSide);
////                        spinDelay.reset();
////                    }
////                }
////                else{
////                    testDexer.setSpinState(3);
////                }
////            }
////            //step 3 set up for shot
////            if(testDexer.getSpinState() == TestDexer.SpinState.PREPARE_FOR_SHOT){
////                testDexer.setUpForShooting(order);
////                testDexer.setSpinState(0);
////            }
//            //step 4 shoot
////            if(testDexer.getSpinState() == TestDexer.SpinState.SHOOT && shooter.getLauunchState() == testShooter.LaunchState.LAUNCH && !launched){
////                testDexer.shoot();
////                launched = true;
////            }if(shooter.getLauunchState() == testShooter.LaunchState.RECOVER){
////                testDexer.setSpinState(0);
////                ballCount = 0;
////                launched = false;
////            }
//            switch (step) {
//                //step 1 - shoot3
//                case 0:
//                    shooter.shoot3();
//                    testDexer.setSpinState(4);
//                    step++;
//                    break;
//                case 1:
//                    if (shooter.isActive) {
//                        shooter.updateState(targetVel);
//                        if (shooter.getLauunchState() == testShooter.LaunchState.LAUNCH && testDexer.getSpinState() == TestDexer.SpinState.SHOOT && !launched) {
//                            testDexer.s1.setPower(.2);
//                            testDexer.s2.setPower(.2);
//                            launched = true;
//                            spinDelay.reset();
//                        }
//                        break;
//                    }
//                    testDexer.s1.setPower(0);
//                    testDexer.s2.setPower(0);
//                    testDexer.setSpinState(0);
//                    launched = false;
//                    testDexer.shooting = false;
//                    ballCount = 0;
//                    step++;
//                    break;
//
//                case 2:
//                    if(!follower.isBusy()){
//                        follower.followPath(park);
//                        step++;
//                    }
//                    break;
//            }
//            // Debug Info
//            telemetry.addData("Step", step);
//            telemetry.addData("Pose", follower.getPose());
//            telemetry.addData("Busy", follower.isBusy());
//            telemetry.addData("spindexer pos", spinPos);
//            telemetry.addData("target", testDexer.getTargetPos());
//            telemetry.update();
//        }
//    }
//
//    // ---------------------------------------------------------------------
//    // PATH GENERATION
//    // ---------------------------------------------------------------------
//    public void buildPaths() {
//
//        final Pose startPose = new Pose(56, 8,  Math.toRadians(180));
//
//        final Pose shootPose = new Pose(57, 13,  Math.toRadians(140));
//
//
////        final Pose midPose = new Pose(108, 13,  Math.toRadians(90));
//
//
//        final Pose parkPose = new Pose(36, 10,   Math.toRadians(90));
//
//        preload = follower.pathBuilder()
//                .addPath(new BezierLine(startPose, shootPose))
//                .setLinearHeadingInterpolation(startPose.getHeading(), shootPose.getHeading())
//                .addParametricCallback(0.75, () -> {shooter.shoot3();})
//                .build();
//
////        gotToPark = follower.pathBuilder()
////                .addPath(new BezierLine(shootPose, midPose))
////                .setLinearHeadingInterpolation(shootPose.getHeading(), midPose.getHeading())
////                .build();
//        park = follower.pathBuilder()
//                .addPath(new BezierLine(shootPose, parkPose))
//                .setLinearHeadingInterpolation(shootPose.getHeading(), parkPose.getHeading())
//                .build();
//
//
//
//
//
//    }
//}
//
