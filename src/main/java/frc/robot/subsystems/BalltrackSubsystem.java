package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class BalltrackSubsystem extends SubsystemBase {
    private WPI_TalonSRX conveyorMotor = new WPI_TalonSRX(Constants.BALLTRACK_CONVEYOR_MOTOR_PORT);
    private WPI_TalonSRX chamberMotor = new WPI_TalonSRX(Constants.BALLTRACK_CHAMBER_MOTOR_PORT);

    private AnalogInput conveyorSensor = new AnalogInput(Constants.BALLTRACK_CONVEYOR_SENSOR_PORT);
    private AnalogInput chamberSensor = new AnalogInput(Constants.BALLTRACK_CHAMBER_SENSOR_PORT);

    private final double PROXIMITY_SENSOR_THRESHOLD = 50;
    private final double MOTOR_PERCENT = 0.3;

    private boolean isBallInConveyor = false;
    private boolean isBallInChamber = false;

    private BalltrackMode currentBalltrackMode = BalltrackMode.DISABLED;

    private NetworkTableEntry conveyorProximitySensorEntry;
    private NetworkTableEntry chamberProximitySensorEntry;
    private NetworkTableEntry balltrackModeEntry;

    private NetworkTable balltrackNetworkTable;

    private double testConveyorMotorSpeed = MOTOR_PERCENT;
    private double testChamberMotorSpeed = MOTOR_PERCENT;

    private NetworkTableEntry testConveyorMotorSpeedEntry;
    private NetworkTableEntry testChamberMotorSpeedEntry;

    public BalltrackSubsystem() {
        conveyorMotor.setInverted(true);
        chamberMotor.setInverted(true);

        balltrackNetworkTable = NetworkTableInstance.getDefault().getTable("balltrack");

        conveyorProximitySensorEntry = balltrackNetworkTable.getEntry("isBallInConveyor");
        chamberProximitySensorEntry = balltrackNetworkTable.getEntry("isBallInChamber");
        balltrackModeEntry = balltrackNetworkTable.getEntry("balltrackMode");

        testConveyorMotorSpeedEntry = balltrackNetworkTable.getEntry("conveyorMotorSpeed");
        testChamberMotorSpeedEntry = balltrackNetworkTable.getEntry("chamberMotorSpeed");

        testConveyorMotorSpeedEntry.setDouble(MOTOR_PERCENT);
        testChamberMotorSpeedEntry.setDouble(MOTOR_PERCENT);
    }

    public void update() {
        isBallInConveyor = isBallInConveyor();
        isBallInChamber = isBallInChamber();

        if (currentBalltrackMode == BalltrackMode.ENABLED) {
            if (isBalltrackFull()) {
                stopBalltrack();
            } 
            else if (isBallInChamber) {
                runConveyor();
            }
            else {
                runBalltrack();
            }
        }
        else if (currentBalltrackMode == BalltrackMode.TESTING) {
            if (isBalltrackFull()) {
                stopBalltrack();
            }
            else if (isBallInChamber) {
                testRunConveyor();
            }
            else {
                testRunBalltrack();
            }
        }
        else {
            stopBalltrack();
        }
    }

    @Override
    public void periodic() {
        conveyorProximitySensorEntry.setBoolean(isBallInConveyor);
        chamberProximitySensorEntry.setBoolean(isBallInChamber);
        balltrackModeEntry.setString(currentBalltrackMode.name());
    }

    public boolean isBallInConveyor() {
        return conveyorSensor.getValue() < PROXIMITY_SENSOR_THRESHOLD;
    }

    public boolean isBallInChamber() {
        return chamberSensor.getValue() < PROXIMITY_SENSOR_THRESHOLD;
    }

    public boolean isBalltrackFull() {
        return isBallInConveyor && isBallInChamber;
    }

    public void enableBalltrack() {
        currentBalltrackMode = BalltrackMode.ENABLED;
    }

    public void disableBalltrack() {
        currentBalltrackMode = BalltrackMode.DISABLED;
    }

    public void testBalltrack() {
        currentBalltrackMode = BalltrackMode.TESTING;
    }

    public void runBalltrack() {
        conveyorMotor.set(MOTOR_PERCENT);
        chamberMotor.set(MOTOR_PERCENT);
    }

    public void stopBalltrack() {
        conveyorMotor.stopMotor();
        chamberMotor.stopMotor();
    }

    public void runConveyor() {
        conveyorMotor.set(MOTOR_PERCENT);
    }

    public void testRunBalltrack() {
        conveyorMotor.set(testConveyorMotorSpeed);
        chamberMotor.set(testChamberMotorSpeed);
    }

    public void testRunConveyor() {
        conveyorMotor.set(testConveyorMotorSpeed);
        chamberMotor.stopMotor();
    }

    private enum BalltrackMode {
        ENABLED,
        DISABLED,
        TESTING
    }
}