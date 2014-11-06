package com.metanautix.ev3.server;

import lejos.hardware.Power;

/**
 * A display widget with renders information on a battery.
 */
public class BatteryWidget
    extends PositionedWidget
{
    public static final float MAXIMUM_VOLTAGE=
        9.0f;

    private Power mPower;

    public BatteryWidget
        (Power power,
         String name,
         int page,
         int column,
         int row)
    {
        super(name,page,column,row);
        mPower=power;
    }

    @Override
    protected boolean addText
        (StringBuilder builder)
    {
        builder.append(Integer.toString((int)(mPower.getVoltage()/
                                              MAXIMUM_VOLTAGE*100)));
        return true;
    }
}
