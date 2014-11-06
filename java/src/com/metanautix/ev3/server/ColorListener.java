package com.metanautix.ev3.server;

import com.metanautix.ev3.common.Color;

/**
 * A listener for the color sensor's associated color detector.
 */
public interface ColorListener
{
    public static class Empty
        implements ColorListener
    {
        @Override
        public void colorChanged
            (Color oldColor,
             Color newColor) {}
    }

    public void colorChanged
        (Color oldColor,
         Color newColor);
}
