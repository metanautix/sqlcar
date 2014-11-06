package com.metanautix.ev3.common;

/**
 * An event recording a movement action.
 */
public class MovementEvent
    extends Event
{
    private static final long serialVersionUID=
        0L;

    private Angle mAngle;
    private Direction mDirection;

    private void init
        (Angle angle,
         Direction direction)
    {
        mAngle=angle;
        mDirection=direction;
    }

    public MovementEvent
        (long timeStamp,
         Angle angle,
         Direction direction)
    {
        super(timeStamp);
        init(angle,direction);
    }

    public MovementEvent
        (Angle angle,
         Direction direction)
    {
        init(angle,direction);
    }

    public Angle getAngle()
    {
        return mAngle;
    }

    public Direction getDirection()
    {
        return mDirection;
    }
}
