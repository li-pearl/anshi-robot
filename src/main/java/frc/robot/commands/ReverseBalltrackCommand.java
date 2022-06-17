package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.BalltrackSubsystem;

public class ReverseBalltrackCommand extends CommandBase {
    private BalltrackSubsystem balltrackSubsystem;

    public ReverseBalltrackCommand(BalltrackSubsystem balltrackSubsystem) {
        this.balltrackSubsystem = balltrackSubsystem;
    }

    public void execute() {
        balltrackSubsystem.reverseBalltrackMode();
    }

    public void end(boolean interrupted) {
        balltrackSubsystem.disableBalltrack();
    }
}