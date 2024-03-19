// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.commands.PathPlannerAuto;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;

import com.pathplanner.lib.auto.NamedCommands;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;

import frc.robot.commands.TankDrive;


import edu.wpi.first.wpilibj2.command.button.CommandJoystick;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import frc.robot.commands.Churro;
import frc.robot.commands.GearShift;
import frc.robot.commands.Intake;
import frc.robot.commands.Shooter;
import frc.robot.commands.TankDrive;
import frc.robot.commands.auto.SpeakerOffline;
import frc.robot.Constants.OperatorConstants;
import frc.robot.subsystems.ChurroSubsystem;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.IntakeSubsystem;
import frc.robot.subsystems.LightingSubsystem;
import frc.robot.subsystems.ShooterSubsystem;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.subsystems.GearShiftSubsystem;


import edu.wpi.first.wpilibj2.command.button.JoystickButton;


public class RobotContainer {

  private final DriveSubsystem driveSubsystem = new DriveSubsystem();
  private final ShooterSubsystem shooterSubsystem = new ShooterSubsystem();
  public final IntakeSubsystem intakeSubsystem = new IntakeSubsystem();
  private final GearShiftSubsystem gearShiftSubsystem = new GearShiftSubsystem();
  private final ChurroSubsystem churroSubsystem = new ChurroSubsystem();
  public final LightingSubsystem lightingSubsystem = new LightingSubsystem();
  
  private CommandJoystick leftJoystick = new CommandJoystick(OperatorConstants.LEFT_JOYSTICK_PORT);
  private CommandJoystick rightJoystick = new CommandJoystick(OperatorConstants.RIGHT_JOYSTICK_PORT);

  private CommandXboxController xboxController = new CommandXboxController(OperatorConstants.XBOX_CONTROLLER_PORT);

  private SendableChooser<Command> autoChooser = new SendableChooser<>();

  public ShuffleboardTab teleopTab = Shuffleboard.getTab("Teleoperated");
  public ShuffleboardTab autoTab = Shuffleboard.getTab("Autonomous");


  public RobotContainer() {
    driveSubsystem.setDefaultCommand(
      new TankDrive(
        () -> leftJoystick.getY(), 
        () -> rightJoystick.getY(), 
        driveSubsystem
        )
    );


    intakeSubsystem.setDefaultCommand(
      new Intake(
        intakeSubsystem, 
        false, 
        false
      )
    );

    configureBindings();
    setElastic();

  }

  private void configureBindings() {

    xboxController.rightBumper().onTrue(shoot());
    
    xboxController.x().whileTrue(new Intake(intakeSubsystem, true, true));
    xboxController.b().onTrue(new Intake(intakeSubsystem, false, false));

    xboxController.rightTrigger().whileTrue(new Churro(churroSubsystem, true));
    xboxController.leftTrigger().whileFalse(new Churro(churroSubsystem, false));


    leftJoystick.button(11).whileTrue(new GearShift(gearShiftSubsystem, true))
                          .whileFalse(new GearShift(gearShiftSubsystem, false));


  }


  public void setElastic() {
    teleopTab.addBoolean("External Sensor", () -> !intakeSubsystem.getExternalNoteDetector());
    teleopTab.addBoolean("Internal Sensor", () -> !intakeSubsystem.getInternalNoteDetector());
    teleopTab.addBoolean("Left IR", () -> !intakeSubsystem.getLeftVerticalIntakeSensor());
    teleopTab.addBoolean("Right IR", () -> !intakeSubsystem.getRightVerticalIntakeSensor());

    //no clue if this will work but why not try it to see what it does
    teleopTab.add("Command Scheduler", CommandScheduler.getInstance());

    autoChooser.setDefaultOption("No Auto", new InstantCommand());
    autoChooser.addOption("Speaker Offline", new SpeakerOffline(driveSubsystem, shooterSubsystem, intakeSubsystem, churroSubsystem));
    autoTab.add("Auto Chooser", autoChooser);
  }


  private Command shoot() {
    return (new Shooter(1, shooterSubsystem))
            .alongWith(new WaitCommand(1))
              .andThen(new Intake(intakeSubsystem, false, true));
  }

  public void setNamedCommands() {
    NamedCommands.registerCommand("Shoot", shoot());
    NamedCommands.registerCommand("Intake", new Intake(intakeSubsystem, false, false));
  }
 
  public Command getAutonomousCommand() {
    return autoChooser.getSelected();
  }







 


}
