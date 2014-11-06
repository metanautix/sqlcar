/* Copyright 2014 Metanautix, Inc. */

package com.metanautix.ev3.server;

/**
 * A simple daemon thread invoking its runWrapped() method within
 * exception reporting protection, and providing utilities for
 * asynchronous termination.
 */
public abstract class ThreadWrapper
    extends Thread
{
    private volatile boolean mStop;

    public ThreadWrapper()
    {
        setDaemon(true);
    }

    protected boolean shouldStop()
    {
        return mStop;
    }

    protected abstract void runWrapped();

    @Override
    public void run()
    {
        try {
            runWrapped();
        } catch (Throwable t) {
            t.printStackTrace();
            throw t;
        }
    }

    public void terminate()
    {
        mStop=true;
        interrupt();
        try {
            join();
        } catch (InterruptedException ex) {
            throw new IllegalStateException("Unexpected interruption",ex);
        }
    }
}
