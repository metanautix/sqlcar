/* Copyright 2014 Metanautix, Inc. */

package com.metanautix.ev3.common;

import java.io.Serializable;

/**
 * A generic event recording an action.
 */
public class Event
    implements Serializable
{
    private static final long serialVersionUID=
        0L;
    
    private long mTimeStamp;

    public Event
        (long timeStamp)
    {
        mTimeStamp=timeStamp;
    }

    public Event()
    {
        this(System.currentTimeMillis());
    }

    public long getTimeStamp()
    {
        return mTimeStamp;
    }
}
