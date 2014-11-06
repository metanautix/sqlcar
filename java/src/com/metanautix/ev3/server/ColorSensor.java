/* Copyright 2014 Metanautix, Inc. */

package com.metanautix.ev3.server;

import com.metanautix.ev3.common.Color;
import java.util.ArrayList;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;

/**
 * Color sensor controller.
 */
public class ColorSensor
{
    private static final long POLL_INTERVAL=
        300L;
    private static final int CONFIRMATION_REPETITIONS=
        3;

    private EV3ColorSensor mSensor;
    private ArrayList<ColorListener> mListeners;
    private volatile Color mColor;
    private Color mOldColor;
    private int mRepetitions;
    private Repeater mPoller;

    public ColorSensor
        (Port port)
    {
        mSensor=new EV3ColorSensor(port);
        mListeners=new ArrayList<ColorListener>();
        mColor=Color.NONE;
        mOldColor=Color.NONE;
    }

    public Color getColor()
    {
        return mColor;
    }

    public void addColorListener
        (ColorListener listener)
    {
        mListeners.add(listener);
    }

    public synchronized void startPoller()
    {
        if (mPoller!=null) {
            return;
        }
        mPoller=new Repeater(POLL_INTERVAL) {
            @Override
            public void repeat()
            {
                poll();
            }
        };
        mPoller.start();
    }

    public synchronized void stopPoller()
    {
        if (mPoller==null) {
            return;
        }
        mPoller.terminate();
        mPoller=null;
    }

    private void poll()
    {
        Color newColor=Color.FROM_ID.get(mSensor.getColorID());
        if (mColor!=newColor) {
            mOldColor=mColor;
            mColor=newColor;
            mRepetitions=0;
        } else {
            ++mRepetitions;
        }
        if ((mOldColor!=mColor) &&
            (mRepetitions==CONFIRMATION_REPETITIONS)) {
            for (ColorListener listener:mListeners) {
                listener.colorChanged(mOldColor,mColor);
            }
        }
    }
}
