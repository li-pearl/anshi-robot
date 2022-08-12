package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.TalonFXFeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonFXConfiguration;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.RobotController;
import frc.robot.Constants;

public class ShooterSubsystem extends SubsystemBase{
    private WPI_TalonFX rearMotor = new WPI_TalonFX(Constants.SHOOTER_REAR_MOTOR_PORT);
    private WPI_TalonFX frontMotor = new WPI_TalonFX(Constants.SHOOTER_FRONT_MOTOR_PORT);

    @Override
    public void periodic() {
    }

    public enum currentShooterMode {
        
    }
}
