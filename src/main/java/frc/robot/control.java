package frc.robot;

import java.lang.Math;

import edu.wpi.first.wpilibj.Joystick;

public class Control {
    private Joystick stick = new Joystick(0);

    private int state = 0;
    private Boolean prevGrab;
    private Boolean prevExtend;
    private Boolean prevView;

    private static final int axisSpin = 4;
    private static final int axisSpeed = 2;
    private static final int buttonView = 1;  // camera to view
    private static final int buttonGrab = 4; // grab/release hatch cover
    private static final int buttonExtend = 8; // extend/retract hatch handler 

    private static final double deadbandX = 0.075;
    private static final double deadbandY = 0.075;
    private static final double deadbandR = 0.075;
    private static final double speedMin = 0.5;
    
    public static final int modeHatch = 1;
    public static final int modeGrab = 2;
    public static final int modeExtend = 4;

    public Control() {
        prevGrab = false;
        prevView = false;
        prevExtend = false;
    }

    private double xferDeadband(double value, double width) {
        double speed = stick.getRawAxis(axisSpeed) * (1.0 - speedMin) + speedMin;
        if (Math.abs(value) < width) {
            return 0.0;
        }
        return value * speed;
    }

    public double getDriveX() {
        return xferDeadband(stick.getX(), deadbandX);
    }

    public double getDriveY() {
        return xferDeadband(stick.getY(), deadbandY);
    }

    public double getDriveR() {
        return xferDeadband(stick.getRawAxis(axisSpin), deadbandR);
    }

    public Boolean getHatchView() {
        return (state & modeHatch) == modeHatch;
    }

    public Boolean getGrabbing() {
        return (state & modeGrab) == modeGrab;
    }

    public Boolean getExtended() {
        return (state & modeExtend) == modeExtend;
    }

    public void setHatchMode() {
        state |= modeHatch;
    }

    public void setCargoMode() {
        state &= ~modeHatch;
    }

    public void setHatchGrabbed() {
        state |= modeGrab;
    }
    
    public void setHatchReleased() {
        state &= ~modeGrab;
    }

    public void setHatchExtended() {
        state |= modeExtend;
    }

    public void setHatchRetracted() {
        state &= ~modeExtend;
    }

    private Boolean handleButton(int mode, int button, Boolean previous, String nameTrue, String nameFalse) {
        Boolean current = stick.getRawButton(button);
        if (current & !previous) {
            state ^= mode;

            System.out.print("Button ");
            System.out.print(button);
            System.out.print(" pressed, state now ");
            if ((state & mode) == mode) {
                System.out.println(nameTrue);
            } else {
                System.out.println(nameFalse);
            }
        }
        return current;
    }

    public void periodic() {
        prevView = handleButton(modeHatch, buttonView, prevView, "hatchView", "fieldView");
        prevGrab = handleButton(modeGrab, buttonGrab, prevGrab, "hatchGrabbed", "hatchReleased");
        prevExtend = handleButton(modeExtend, buttonExtend, prevExtend, "hatchExtended", "hatchRetracted");
    }
}