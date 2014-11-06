package com.metanautix.ev3.server;

/**
 * A listener for the infrared sensor's associated remote control.
 */
public interface RemoteListener
{
    public static class Empty
        implements RemoteListener
    {
        @Override
        public void buttonChanged
            (IrSensor.Button oldButton,
             IrSensor.Button newButton) {}
    }

    public void buttonChanged
        (IrSensor.Button oldButton,
         IrSensor.Button newButton);
}
