/* Copyright 2014 Metanautix, Inc. */

package com.metanautix.ev3.common;

/**
 * An event recording a distance measurement action.
 */
public class DistanceEvent
    extends Event
{
    private static final long serialVersionUID=
        0L;

    private float mDistance;

    private void init
        (float distance)
    {
        mDistance=distance;
    }

    public DistanceEvent
        (long timeStamp,
         float distance)
    {
        super(timeStamp);
        init(distance);
    }

    public DistanceEvent
        (float distance)
    {
        init(distance);
    }

    public float getDistance()
    {
        return mDistance;
    }
}
