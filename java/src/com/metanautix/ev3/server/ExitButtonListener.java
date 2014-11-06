/* Copyright 2014 Metanautix, Inc. */

package com.metanautix.ev3.server;

/**
 * A listener for button pad activity which causes the application to
 * exit if a button is pressed twice within a second.
 */
public class ExitButtonListener
    extends ButtonListener.Empty
{
    public static final long CONFIRM_DELAY_MS=
        1000L;

    private long mLastPressed;

    @Override
    public void buttonPressed
        (ButtonPad.Button button)
    {
        long t=System.currentTimeMillis();
        if (t-mLastPressed<CONFIRM_DELAY_MS) {
            Utils.terminate();
        }
        mLastPressed=t;
    }
}
