package com.metanautix.ev3.common;

/**
 * An event recording a color detection action.
 */
public class ColorEvent
    extends Event
{
    private static final long serialVersionUID=
        0L;

    private Color mColor;

    private void init
        (Color color)
    {
        mColor=color;
    }

    public ColorEvent
        (long timeStamp,
         Color color)
    {
        super(timeStamp);
        init(color);
    }

    public ColorEvent
        (Color color)
    {
        init(color);
    }

    public Color getColor()
    {
        return mColor;
    }
}
