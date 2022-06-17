package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.BalltrackSubsystem;

public class PrepareBallsCommand extends CommandBase {
    private BalltrackSubsystem balltrackSubsystem;

    public PrepareBallsCommand(BalltrackSubsystem balltrackSubsystem) {
        this.balltrackSubsystem = balltrackSubsystem;
    }

    public void execute() {
        balltrackSubsystem.prepareBallsMode();
    }

    public void end(boolean interrupted) {
        balltrackSubsystem.disableBalltrack();
    }
}