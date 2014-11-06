package com.metanautix.ev3.common;

import java.util.HashMap;

/**
 * A color detectable by the color sensor.
 */
public enum Color
{
    NONE(lejos.robotics.Color.NONE),
    BLACK(lejos.robotics.Color.BLACK),
    BLUE(lejos.robotics.Color.BLUE),
    GREEN(lejos.robotics.Color.GREEN),
    YELLOW(lejos.robotics.Color.YELLOW),
    RED(lejos.robotics.Color.RED),
    WHITE(lejos.robotics.Color.WHITE),
    BROWN(lejos.robotics.Color.BROWN);

    // Map from ID to Color.
    public static final HashMap<Integer,Color> FROM_ID;

    static {
        FROM_ID=new HashMap<Integer,Color>();
        for (Color c:Color.values()) {
            FROM_ID.put(c.getId(),c);
        }
    }

    private final int mId;

    private Color
        (int id)
    {
        mId=id;
    }

    public int getId()
    {
        return mId;
    }
}
