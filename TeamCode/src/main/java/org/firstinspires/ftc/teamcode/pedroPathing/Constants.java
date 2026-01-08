package org.firstinspires.ftc.teamcode.pedroPathing;

import com.pedropathing.control.FilteredPIDFCoefficients;
import com.pedropathing.control.PIDFCoefficients;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.ftc.FollowerBuilder;
import com.pedropathing.ftc.drivetrains.MecanumConstants;
import com.pedropathing.ftc.localization.constants.PinpointConstants;
import com.pedropathing.paths.PathConstraints;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class Constants {
    private static double robotMass = 14.69639;  //Robot mass in KG
    private static double maxRobotPower = 1.0; // Should be between 0.0 and 1.0

    public static FollowerConstants followerConstants = new FollowerConstants()
            .mass(robotMass)
            .forwardZeroPowerAcceleration(-39.518659987366554)
            //-39.518659987366554
            .lateralZeroPowerAcceleration(-72.614383729009504)
            .translationalPIDFCoefficients(new PIDFCoefficients(.095, 0, .005, .028))
            .headingPIDFCoefficients(new PIDFCoefficients(.75, 0, .03, .0002))
            .drivePIDFCoefficients(new FilteredPIDFCoefficients(.025, 0, .01, .00001, .6));
    //-72.12012985028127
    //p .75 d .029


    //og p value: 0.025
    public static PathConstraints pathConstraints = new PathConstraints(
            0.99,
            100,
            .8,
            1.4);

    //Check the motor names and directions
    public static MecanumConstants driveConstants = new MecanumConstants()
            .maxPower(maxRobotPower)
            .rightFrontMotorName("rf")
            .rightRearMotorName("rb")
            .leftRearMotorName("lb")
            .leftFrontMotorName("lf")
            .leftFrontMotorDirection(DcMotorSimple.Direction.REVERSE)
            .leftRearMotorDirection(DcMotorSimple.Direction.REVERSE)
            .rightFrontMotorDirection(DcMotorSimple.Direction.FORWARD)
            .rightRearMotorDirection(DcMotorSimple.Direction.FORWARD)
            .xVelocity(82.69202477162278)
            .yVelocity(66.90565743033342);



    public static PinpointConstants localizerConstants = new PinpointConstants()
            .forwardPodY(2.5)
            .strafePodX(-1)
            .distanceUnit(DistanceUnit.INCH)
            .hardwareMapName("pinpoint")
            .encoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD)
            .forwardEncoderDirection(GoBildaPinpointDriver.EncoderDirection.REVERSED)
            .strafeEncoderDirection(GoBildaPinpointDriver.EncoderDirection.FORWARD);


    public static class Shooter {
        public static String SHOOTER_MOTOR_NAME = "shooter";
        public static double kP = 0.01;
        public static double kI = 0.0;
        public static double kD = 0.0001;
        public static double kF = 0.05;
        public static double TARGET_VELOCITY = 2200.0;
        public static double VELOCITY_TOLERANCE = 50.0;
    }

    public static class Intake {
        public static String INTAKE_MOTOR_NAME = "intake";
    }


    public static Follower createFollower(HardwareMap hardwareMap) {
        return new FollowerBuilder(followerConstants, hardwareMap)
                .mecanumDrivetrain(driveConstants)
                .pinpointLocalizer(localizerConstants)
                .pathConstraints(pathConstraints)
                .build();
    }
}
