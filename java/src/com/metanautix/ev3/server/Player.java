/* Copyright 2014 Metanautix, Inc. */

package com.metanautix.ev3.server;

import com.metanautix.ev3.common.MovementEvent;
import java.util.List;

/**
 * A player of recorded (or other) events.
 */
public class Player
{
    private class Autopilot
        extends ThreadWrapper
    {
        private List<MovementEvent> mEvents;

        public Autopilot
            (List<MovementEvent> events)
        {
            mEvents=events;
        }

        @Override
        public void runWrapped()
        {
            mCar.setAutopilotMode(true);
            try {
                boolean first=true;
                long offset=0L;
                for (MovementEvent event:mEvents) {
                    if (shouldStop()) {
                        return;
                    }
                    if (first) {
                        offset=System.currentTimeMillis()-event.getTimeStamp();
                        first=false;
                    } else {
                        long delay=(event.getTimeStamp()+offset-
                                    System.currentTimeMillis());
                        if (delay>0) {
                            try {
                                Thread.sleep(delay);
                            } catch (InterruptedException ex) {
                                continue;
                            }
                        }
                    }
                    mCar.power(event.getAngle(),event.getDirection());
                }
            } finally {
                mCar.setAutopilotMode(false);
            }
        }
    }

    private Car mCar;
    private Autopilot mAutopilot;

    public Player
        (Car car)
    {
        mCar=car;
    }

    public void setTag
        (String tag)
    {
        mCar.setTag(tag);
    }

    public void calibrate
        (float maxSpeed)
    {
        mCar.calibrate(false,maxSpeed);
    }

    public void calibrate()
    {
        mCar.calibrate(false);
    }

    public void startAutopilot
        (List<MovementEvent> events)
    {
        if (mAutopilot!=null) {
            return;
        }
        mAutopilot=new Autopilot(events);
        mAutopilot.start();
        
    }

    public void stopAutopilot()
    {
        if (mAutopilot==null) {
            return;
        }
        mAutopilot.terminate();
        mAutopilot=null;
    }
}
