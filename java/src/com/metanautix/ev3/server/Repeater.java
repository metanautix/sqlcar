/* Copyright 2014 Metanautix, Inc. */

package com.metanautix.ev3.server;

/**
 * A simple thread invoking its repeat() method at regular intervals.
 */
public abstract class Repeater
    extends ThreadWrapper
{
    private long mDelay;

    public Repeater
        (long delay)
    {
        mDelay=delay;
    }

    protected abstract void repeat();

    @Override
    public void runWrapped()
    {
        while (!shouldStop()) {
            repeat();
            try {
                Thread.sleep(mDelay);
            } catch (InterruptedException ex) {
                continue;
            }
        }
    }
}
