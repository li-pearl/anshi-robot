package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.robot.subsystems.BalltrackSubsystem;
import frc.robot.commands.PrepareBallsCommand;
import frc.robot.commands.TestBalltrackCommand;
import frc.robot.commands.IntakeBallsCommand;

public class RobotContainer {

    private final Joystick joystick = new Joystick(Constants.JOYSTICK);

    private final JoystickButton leftThumbButton = new JoystickButton(joystick, Constants.LEFT_THUMB_BUTTON);
    private final JoystickButton bottomThumbButton = new JoystickButton(joystick, Constants.BOTTOM_THUMB_BUTTON);
    private final JoystickButton rightThumbButton = new JoystickButton(joystick, Constants.RIGHT_THUMB_BUTTON);

    private final BalltrackSubsystem balltrackSubsystem = new BalltrackSubsystem();
    
    public RobotContainer() {
        CommandScheduler.getInstance().registerSubsystem(balltrackSubsystem);

        configureJoystickControls();
    }

    public BalltrackSubsystem getBalltrackSubsystem() {
        return balltrackSubsystem;
    }

    public void configureJoystickControls() {
        leftThumbButton.whileHeld(new PrepareBallsCommand(balltrackSubsystem));
        bottomThumbButton.whileHeld(new TestBalltrackCommand(balltrackSubsystem));
        rightThumbButton.whileHeld(new IntakeBallsCommand(balltrackSubsystem));
    }
}