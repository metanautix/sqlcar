package com.metanautix.ev3.server;

/**
 * A display widget with renders information on an infrared sensor's
 * channel.
 */
public class IrSensorWidget
    extends PositionedWidget
{
    private IrSensor mSensor;
    private IrSensor.Channel mChannel;

    public IrSensorWidget
        (IrSensor sensor,
         IrSensor.Channel channel,
         String name,
         int page,
         int column,
         int row)
    {
        super(name,page,column,row);
        mSensor=sensor;
        mChannel=channel;
    }

    @Override
    protected boolean addText
        (StringBuilder builder)
    {
        if (mSensor.getDistanceModeActive()) {
            builder.append(mSensor.getDistance());
            return true;
        }
        int state=mSensor.getButtonMask(mChannel).getValue();
        if ((state&IrSensor.ButtonMask.TL.getValue())!=0) {
            builder.append('L');
        }
        if ((state&IrSensor.ButtonMask.TR.getValue())!=0) {
            builder.append('R');
        }
        if ((state&IrSensor.ButtonMask.BL.getValue())!=0) {
            builder.append('l');
        }
        if ((state&IrSensor.ButtonMask.BR.getValue())!=0) {
            builder.append('r');
        }
        if ((state&IrSensor.ButtonMask.BEACON.getValue())!=0) {
            builder.append('B');
        }
        return true;
    }
}
