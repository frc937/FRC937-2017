/*
 * Do not harm humans
 * Follow commands by humans
 * Protect self
 */
package org.usfirst.frc.team937.robot;

//imports
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.cscore.AxisCamera;
import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.IterativeRobot;
import org.opencv.core.Mat;

public class Robot extends IterativeRobot {
	// variable declarations
	Joystick controller = new Joystick(0);
    Timer timer = new Timer();
	Spark leftDrive = new Spark(0);
	Spark rightDrive = new Spark(1);
	Talon ballShooter = new Talon(3);
	Relay ballCollector = new Relay(0);
	Talon climber1 = new Talon(2);
	Talon climber2 = new Talon(4);
	
	RobotDrive drive = new RobotDrive(leftDrive, rightDrive);

	Thread visionThread;

	// initialization
	public void robotInit() {
		visionThread = new Thread(() -> {
			AxisCamera camera = CameraServer.getInstance().addAxisCamera("10.9.37.104");
			camera.setResolution(640, 480);
			CvSink cvSink = CameraServer.getInstance().getVideo();
			CvSource outputStream = CameraServer.getInstance().putVideo("Rectangle", 640, 480);
			Mat mat = new Mat();
			while (!Thread.interrupted()) {
				if (cvSink.grabFrame(mat) == 0) {
					outputStream.notifyError(cvSink.getError());
					continue;
				}
				outputStream.putFrame(mat);
			}
		});
		visionThread.setDaemon(true);
		visionThread.start();
	}

	public void disabledPeriodic() {
		ballCollector.set(Relay.Value.kOff);
	}
	
	// autonomous loop
	public void autonomousInit() {
		final long time = 900000000;
		long startTime=System.nanoTime();
		long endTime=0l;
		do{
			endTime=System.nanoTime();
			drive.tankDrive(1, 1);
		} while(startTime+time>=endTime);
		drive.tankDrive(0, 0);
}

	// teleoperated loop
	public void teleopPeriodic() {
		/*
		 * Easy input names for xbox360 wired controller
		 * 
		 * stick values will range from -1 to 1 D-pad values will be -1 -0.5 0
		 * 0.5 or 1 based on how the D-pad is pressed buttons will be either 0
		 * or 1 They will all be anywhere from -1 to 1 for easy motor control
		 */
		// axes
		//double leftX = controller.getRawAxis(0);
		double leftY = -1 * controller.getRawAxis(1);
		//double rightX = controller.getRawAxis(4);
		double rightY = -1 * controller.getRawAxis(5);
		// triggers
		double rightTrigger = controller.getRawAxis(3);
		//double leftTrigger = controller.getRawAxis(2);
		// D-pad
		int dpadAngle = controller.getPOV(0);
		double dpadX = 0;
		double dpadY = 0;
		switch (dpadAngle) {
		case 0:
			dpadX = 0;
			dpadY = 1;
			break;
		case 45:
			dpadX = 0.5;
			dpadY = 0.5;
			break;
		case 90:
			dpadX = 1;
			dpadY = 0;
			break;
		case 135:
			dpadX = 0.5;
			dpadY = -0.5;
			break;
		case 180:
			dpadX = 0;
			dpadY = -1;
			break;
		case 225:
			dpadX = -0.5;
			dpadY = -0.5;
			break;
		case 270:
			dpadX = -1;
			dpadY = 0;
			break;
		case 315:
			dpadX = -0.5;
			dpadY = 0.5;
			break;
		default:
			dpadX = 0;
			dpadY = 0;
			break;
		}
		// buttons
		//int buttonA = controller.getRawButton(1) ? 1 : 0;
		//int buttonB = controller.getRawButton(2) ? 1 : 0;
		//int buttonX = controller.getRawButton(3) ? 1 : 0;
		//int buttonY = controller.getRawButton(4) ? 1 : 0;
		//int buttonLB = controller.getRawButton(5) ? 1 : 0;
		//int buttonRB = controller.getRawButton(6) ? 1 : 0;
		//int buttonBACK = controller.getRawButton(7) ? 1 : 0;
		//int buttonSTART = controller.getRawButton(8) ? 1 : 0;
		//int buttonLS = controller.getRawButton(9) ? 1 : 0;
		//int buttonRS = controller.getRawButton(10) ? 1 : 0;
		/*
		 * End easy input
		 */
		// motorname.set(double -1..1) sets motor speed
		drive.tankDrive(leftY, rightY, false);
		ballShooter.set(-rightTrigger);
		
		climber1.set(1);
		//climber1.set(dpadY*2/3);
		//climber2.set(dpadY*2/3);
		ballCollector.set(Relay.Value.kOn);
		ballCollector.set(Relay.Value.kReverse);
	}
}
