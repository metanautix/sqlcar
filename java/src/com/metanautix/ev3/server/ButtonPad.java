/* Copyright 2014 Metanautix, Inc. */

package com.metanautix.ev3.server;

import java.util.HashMap;
import lejos.hardware.Key;
import lejos.hardware.KeyListener;
import lejos.hardware.ev3.EV3;

/**
 * Button pad controller.
 */
public class ButtonPad
{
    public static enum Button
    {
        ENTER(0,"Enter"),
        ESCAPE(1,"Escape"),
        LEFT(2,"Left"),
        RIGHT(3,"Right"),
        UP(4,"Up"),
        DOWN(5,"Down");

        public static final int SIZE=
            Button.values().length;

        private final int mId;
        private final String mName;
        private Key mKey;

        private Button
            (int id,
             String name)
        {
            mId=id;
            mName=name;
        }

        public int getId()
        {
            return mId;
        }

        public String getName()
        {
            return mName;
        }

        public void setKey
            (Key key)
        {
            mKey=key;
        }

        public Key getKey()
        {
            return mKey;
        }
    }

    private class ConverterListener
        implements KeyListener
    {
        private ButtonListener mButtonListener;

        public ConverterListener
            (ButtonListener buttonListener)
        {
            mButtonListener=buttonListener;
        }

        @Override
        public void keyPressed
            (Key k)
        {
            mButtonListener.buttonPressed(mKeyMap.get(k));
        }

        @Override
        public void keyReleased
            (Key k)
        {
            mButtonListener.buttonReleased(mKeyMap.get(k));
        }
    }

    private HashMap<Key,Button> mKeyMap;

    public ButtonPad
        (EV3 ev3)
    {
        ev3.getKeys().setKeyClickTone(0,0);
        mKeyMap=new HashMap<Key,Button>(Button.SIZE);
        for (Button button:Button.values()) {
            button.setKey(ev3.getKey(button.getName()));
            mKeyMap.put(button.getKey(),button);
        }
    }

    public void addListener
        (Button button,
         ButtonListener listener)
    {
        button.getKey().addKeyListener
            (new ConverterListener(listener));
    }
}
