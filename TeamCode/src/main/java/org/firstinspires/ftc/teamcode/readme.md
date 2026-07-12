# FTC Robotics

## Overview

FIRST Tech Challenge (FTC) is a student robotics program where teams design, build, test, and program robots to compete in a dynamic game each season. The process combines mechanical design, electrical integration, software development, and strategy, while also emphasizing iteration, driver practice, and teamwork. FTC teaches students to think like engineers by turning ideas into a working robot that can perform reliably under competition pressure [web:13][web:19].

## Purpose

This robot was built for the FTC game Decode. The goal of our robot was to score game elements efficiently, respond quickly to changing field conditions, and perform consistently in both autonomous and TeleOp. Every subsystem and software decision was made to help us complete game tasks faster, score more points, and reduce driver workload during matches.

## Components

Our robot was built using a Logitech camera, a continuous rotation servo, 8 GoBilda motors, 1 five-turn servo, 2 color sensors, an odometry pod, a control hub with an IMU, a through bore encoder, PLA, and aluminum. These parts worked together to support movement, sensing, scoring, and structural reliability. The control hub served as the robot’s main microcontroller, while the odometry system and encoder helped track position and movement accurately.

## Subsystems

The shooter used 2 motors to spin up internal flywheels to a target speed based on the robot’s distance from the goal, which was tracked using the odometry pods. This let us adjust power dynamically instead of relying on one fixed shot setting. The dual rubber band intake was one of the more unique parts of our robot in our league, and it allowed us to collect game materials without needing to turn the robot around.

Inside the robot, the spindexer sorted balls so they could be shot in an order that earned more points. The turret rotated the shooter and continuously tracked the goal, helping maintain alignment while the robot moved. The hood adjusted the angle of the shot based on distance so the balls landed in the goal area that was least likely to bounce out.

## Software

We built a camera pipeline to detect the ball pattern associated with extra points from an AprilTag-like visual marker. For autonomous movement, we used PedroPathing, which helped us generate and follow smooth paths on the field [web:12][web:15][web:20]. We also kept the code modular and organized by creating a separate class for each subsystem, which made the code easier to test, update, and debug.

Our software focused heavily on automation. Instead of drivers needing to press many buttons to repeat the same sequence of actions, we designed controls so one input could trigger a full chain of movements automatically. This made TeleOp faster, easier, and more consistent for the drivers. We also collected data from different field positions and used it to build algorithms for hood angle and flywheel speed based on distance from the goal.