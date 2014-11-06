package com.metanautix.ev3.server;

import lejos.hardware.motor.BaseRegulatedMotor;

/**
 * A display widget with renders information on a regulated motor.
 */
public class BaseRegulatedMotorWidget
    extends PositionedWidget
{
    private BaseRegulatedMotor mMotor;

    public BaseRegulatedMotorWidget
        (BaseRegulatedMotor motor,
         String name,
         int page,
         int column,
         int row)
    {
        super(name,page,column,row);
        mMotor=motor;
    }

    @Override
    protected boolean addText
        (StringBuilder builder)
    {
        builder.append(Integer.toString(mMotor.getTachoCount()));
        builder.append(' ');
        builder.append(Integer.toString(mMotor.getRotationSpeed()));
        builder.append(' ');
        if (mMotor.isMoving()) {
            builder.append('M');
        }
        if (mMotor.isStalled()) {
            builder.append('S');
        }
        return true;
    }
}
