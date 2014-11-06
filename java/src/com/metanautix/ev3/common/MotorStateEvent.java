package com.metanautix.ev3.common;

/**
 * An event recording a motor state measurement action.
 */
public class MotorStateEvent
    extends Event
{
    private static final long serialVersionUID=
        0L;

    private int mSteering;
    private int mLeft;
    private int mRight;
    private int mSpeed;

    private void init
        (int steering,
         int left,
         int right,
         int speed)
    {
        mSteering=steering;
        mLeft=left;
        mRight=right;
        mSpeed=speed;
    }

    public MotorStateEvent
        (long timeStamp,
         int steering,
         int left,
         int right,
         int speed)
    {
        super(timeStamp);
        init(steering,left,right,speed);
    }

    public MotorStateEvent
        (int steering,
         int left,
         int right,
         int speed)
    {
        init(steering,left,right,speed);
    }

    public int getSteering()
    {
        return mSteering;
    }

    public int getLeft()
    {
        return mLeft;
    }

    public int getRight()
    {
        return mRight;
    }

    public int getSpeed()
    {
        return mSpeed;
    }
}
