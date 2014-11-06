/* Copyright 2014 Metanautix, Inc. */

package com.metanautix.ev3.server;

/**
 * A listener for a sensor's distance measurements.
 */
public interface DistanceListener
{
    public static class Empty
        implements DistanceListener
    {
        @Override
        public void distanceChanged
            (float oldDistance,
             float newDistance) {}
    }
    
    public void distanceChanged
        (float oldDistance,
         float newDistance);
}
