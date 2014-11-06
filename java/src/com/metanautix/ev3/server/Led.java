package com.metanautix.ev3.server;

import lejos.hardware.LED;
import lejos.hardware.ev3.EV3;

/**
 * LED controller.
 */
public class Led
{
    public static enum Pattern
    {
        OFF(0),
        STATIC_GREEN(1),
        STATIC_RED(2),
        STATIC_ORANGE(3),
        NORMAL_BLINKING_GREEN(4),
        NORMAL_BLINKING_RED(5),
        NORMAL_BLINKING_ORANGE(6),
        FAST_BLINKING_GREEN(7),
        FAST_BLINKING_RED(8),
        FAST_BLINKING_ORANGE(9);

        private final int mValue;

        private Pattern
            (int value)
        {
            mValue=value;
        }

        public int getValue()
        {
            return mValue;
        }
    }

    private LED mLed;
    private Pattern mActivePattern;

    public Led
        (EV3 ev3)
    {
        mLed=ev3.getLED();
    }

    public synchronized void setPattern
        (Pattern pattern)
    {
        if (pattern==mActivePattern) {
            return;
        }
        mLed.setPattern(pattern.getValue());
        mActivePattern=pattern;
    }
}
