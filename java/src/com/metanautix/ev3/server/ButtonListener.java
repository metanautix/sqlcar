package com.metanautix.ev3.server;

/**
 * A listener for button pad activity.
 */
public interface ButtonListener
{
    public static class Empty
        implements ButtonListener
    {
        @Override
        public void buttonPressed
            (ButtonPad.Button button) {}

        @Override
        public void buttonReleased
            (ButtonPad.Button button) {}
    }

    public void buttonPressed
        (ButtonPad.Button button);

    public void buttonReleased
        (ButtonPad.Button button);
}
