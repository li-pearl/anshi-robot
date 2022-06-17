package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.BalltrackSubsystem;

public class IntakeBallsCommand extends CommandBase{
    private BalltrackSubsystem balltrackSubsystem;

    public IntakeBallsCommand(BalltrackSubsystem balltrackSubsystem) {
        this.balltrackSubsystem = balltrackSubsystem;
    }

    public void execute() {
        balltrackSubsystem.intakeBallsMode();
    }

    public void end(boolean interrupted) {
        balltrackSubsystem.disableBalltrack();
    }
}
