package com.metanautix.ev3.server;

import com.metanautix.ev3.common.ColorEvent;
import com.metanautix.ev3.common.DistanceEvent;
import com.metanautix.ev3.common.MotorStateEvent;
import com.metanautix.ev3.common.MovementEvent;
import java.util.LinkedList;
import java.util.List;

/**
 * A recorder of events.
 */
public class Recorder
{
    private static class EventList<T>
    {
        public static final int MAX_EVENT_COUNT=
            1000;

        private LinkedList<T> mEvents;

        private void init()
        {
            mEvents=new LinkedList<T>();
        }

        public EventList()
        {
            init();
        }

        public synchronized void clear()
        {
            mEvents.clear();
        }

        public synchronized void record
            (T event)
        {
            mEvents.add(event);
            if (mEvents.size()>MAX_EVENT_COUNT) {
                mEvents.removeFirst();
            }
        }

        public synchronized List<T> getEvents()
        {
            List<T> result=mEvents;
            init();
            return result;
        }
    }

    private EventList<MovementEvent> mMovementEvents;
    private EventList<MotorStateEvent> mMotorStateEvents;
    private EventList<DistanceEvent> mDistanceEvents;
    private EventList<ColorEvent> mColorEvents;

    public Recorder()
    {
        mMovementEvents=new EventList<MovementEvent>();
        mMotorStateEvents=new EventList<MotorStateEvent>();
        mDistanceEvents=new EventList<DistanceEvent>();
        mColorEvents=new EventList<ColorEvent>();
    }

    public void clear()
    {
        mMovementEvents.clear();
        mMotorStateEvents.clear();
        mDistanceEvents.clear();
        mColorEvents.clear();
    }

    public void record
        (MovementEvent movementEvent)
    {
        mMovementEvents.record(movementEvent);
    }

    public void record
        (MotorStateEvent motorStateEvent)
    {
        mMotorStateEvents.record(motorStateEvent);
    }

    public void record
        (DistanceEvent distanceEvent)
    {
        mDistanceEvents.record(distanceEvent);
    }

    public void record
        (ColorEvent colorEvent)
    {
        mColorEvents.record(colorEvent);
    }

    public List<MovementEvent> getMovementEvents()
    {
        return mMovementEvents.getEvents();
    }

    public List<MotorStateEvent> getMotorStateEvents()
    {
        return mMotorStateEvents.getEvents();
    }

    public List<DistanceEvent> getDistanceEvents()
    {
        return mDistanceEvents.getEvents();
    }

    public List<ColorEvent> getColorEvents()
    {
        return mColorEvents.getEvents();
    }
}
