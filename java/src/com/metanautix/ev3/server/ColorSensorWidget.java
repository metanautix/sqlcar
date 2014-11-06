package com.metanautix.ev3.server;

/**
 * A display widget with renders information on a color sensor.
 */
public class ColorSensorWidget
    extends PositionedWidget
{
    private ColorSensor mSensor;

    public ColorSensorWidget
        (ColorSensor sensor,
         String name,
         int page,
         int column,
         int row)
    {
        super(name,page,column,row);
        mSensor=sensor;
    }

    @Override
    protected boolean addText
        (StringBuilder builder)
    {
        builder.append(mSensor.getColor().toString());
        return true;
    }
}
