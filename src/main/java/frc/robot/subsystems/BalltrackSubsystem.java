package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class BalltrackSubsystem extends SubsystemBase {
    private WPI_TalonSRX conveyorMotor = new WPI_TalonSRX(Constants.BALLTRACK_CONVEYOR_MOTOR_PORT);
    private WPI_TalonSRX chamberMotor = new WPI_TalonSRX(Constants.BALLTRACK_CHAMBER_MOTOR_PORT);
    private WPI_TalonSRX intakeMotor = new WPI_TalonSRX(Constants.BALLTRACK_INTAKE_MOTOR_PORT);

    private DoubleSolenoid intakePiston = new DoubleSolenoid(Constants.PCM_ID, PneumaticsModuleType.REVPH, Constants.BALLTRACK_INTAKE_SOLENOID_FORWARD_CHANNEL, Constants.BALLTRACK_INTAKE_SOLENOID_REVERSE_CHANNEL);

    private AnalogInput conveyorSensor = new AnalogInput(Constants.BALLTRACK_CONVEYOR_SENSOR_PORT);
    private AnalogInput chamberSensor = new AnalogInput(Constants.BALLTRACK_CHAMBER_SENSOR_PORT);

    private final double PROXIMITY_SENSOR_THRESHOLD = 50;

    private final double INTAKE_MOTOR_PERCENT = 0.5;

    private final double CONVEYOR_MOTOR_PERCENT = 0.5;
    private final double CHAMBER_MOTOR_PERCENT = 0.5;

    private boolean isBallInConveyor = false;
    private boolean isBallInChamber = false;

    private BalltrackMode currentBalltrackMode = BalltrackMode.DISABLED;

    private NetworkTableEntry conveyorProximitySensorEntry;
    private NetworkTableEntry chamberProximitySensorEntry;
    private NetworkTableEntry balltrackModeEntry;

    private NetworkTable balltrackNetworkTable;

    private double testConveyorMotorSpeed = CONVEYOR_MOTOR_PERCENT;
    private double testChamberMotorSpeed = CHAMBER_MOTOR_PERCENT;

    private NetworkTableEntry testConveyorMotorSpeedEntry;
    private NetworkTableEntry testChamberMotorSpeedEntry;

    public BalltrackSubsystem() {
        conveyorMotor.setInverted(true);
        chamberMotor.setInverted(true);
        intakeMotor.setInverted(true);

        balltrackNetworkTable = NetworkTableInstance.getDefault().getTable("balltrack");

        conveyorProximitySensorEntry = balltrackNetworkTable.getEntry("isBallInConveyor");
        chamberProximitySensorEntry = balltrackNetworkTable.getEntry("isBallInChamber");
        balltrackModeEntry = balltrackNetworkTable.getEntry("balltrackMode");

        testConveyorMotorSpeedEntry = balltrackNetworkTable.getEntry("conveyorMotorSpeed");
        testChamberMotorSpeedEntry = balltrackNetworkTable.getEntry("chamberMotorSpeed");

        testConveyorMotorSpeedEntry.setDouble(CONVEYOR_MOTOR_PERCENT);
        testChamberMotorSpeedEntry.setDouble(CHAMBER_MOTOR_PERCENT);
    }

    public void update() {
        isBallInConveyor = isBallInConveyor();
        isBallInChamber = isBallInChamber();

        switch (currentBalltrackMode) {
            case TESTING:
                if (isBalltrackFull()) {
                    stopBalltrack();
                }
                else if (isBallInChamber) {
                    testRunConveyor();
                }
                else {
                    testRunBalltrack();
                }
                break;
            case INTAKE:
                extendIntake();
                intakeBalls();
                break;
            case SHOOT:
                retractIntake();
                stopIntakeMotor();
                //do shooting stuff
                break;
            case INTAKE_AND_SHOOT:
                extendIntake();
                intakeBalls();
                //do shooting stuff
                break;
            case PREPARE:
                if (isBalltrackFull()) {
                    stopBalltrack();
                } else if (isBallInChamber) {
                    intakeWithConveyor();
                    stopChamber();
                } else {
                    intakeWithConveyor();
                    intakeWithChamber();
                }
                break;
            case REVERSE:
                reverseBalltrack();
                reverseIntake();
                break;
            case DISABLED:
                stopBalltrack();
                retractIntake();
                stopIntakeMotor();
                break;
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

    public void disableBalltrack() {
        currentBalltrackMode = BalltrackMode.DISABLED;
    }

    public void testBalltrack() {
        currentBalltrackMode = BalltrackMode.TESTING;
    }

    public void intakeBallsMode() {
        currentBalltrackMode = BalltrackMode.INTAKE;
    }

    public void intakeAndShootMode() {
        currentBalltrackMode = BalltrackMode.INTAKE_AND_SHOOT;
    }

    public void reverseBalltrackMode() {
        currentBalltrackMode = BalltrackMode.REVERSE;
    }

    public void prepareBallsMode() {
        currentBalltrackMode = BalltrackMode.PREPARE;
    }

    public void shootBallsMode() {
        currentBalltrackMode = BalltrackMode.SHOOT;
    }

    public void intakeBalls() {
        if (isBalltrackFull()) {
            stopBalltrack();
            stopIntakeMotor();
        } else if (isBallInChamber) {
            intakeWithConveyor();
            stopChamber();
            runIntakeMotor();
        } else {
            intakeWithConveyor();
            intakeWithChamber();
            runIntakeMotor();
        }
    }

    public void reverseBalltrack() {
        chamberMotor.set(-CHAMBER_MOTOR_PERCENT);
        conveyorMotor.set(-CONVEYOR_MOTOR_PERCENT);
    }

    public void reverseIntake() {
        intakeMotor.set(-INTAKE_MOTOR_PERCENT);
    }

    public void runIntakeMotor() {
        intakeMotor.set(INTAKE_MOTOR_PERCENT);
    }

    public void stopIntakeMotor() {
        intakeMotor.stopMotor();
    }

    public void intakeWithConveyor() {
        runIntakeMotor();
        runConveyor();
    }

    public void intakeWithChamber() {
        runIntakeMotor();
        runConveyor();
    }

    public void extendIntake() {
        intakePiston.set(Value.kForward);
    }

    public void retractIntake() {
        intakePiston.set(Value.kReverse);
    }

    public void runBalltrack() {
        conveyorMotor.set(CONVEYOR_MOTOR_PERCENT);
        chamberMotor.set(CHAMBER_MOTOR_PERCENT);
    }

    public void stopBalltrack() {
        conveyorMotor.stopMotor();
        chamberMotor.stopMotor();
    }

    public void runConveyor() {
        conveyorMotor.set(CONVEYOR_MOTOR_PERCENT);
    }

    public void runChamber() {
        chamberMotor.set(CHAMBER_MOTOR_PERCENT);
    }

    public void stopChamber() {
        chamberMotor.stopMotor();
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
        DISABLED,
        INTAKE,
        SHOOT,
        INTAKE_AND_SHOOT,
        TESTING,
        REVERSE,
        PREPARE
    }
}