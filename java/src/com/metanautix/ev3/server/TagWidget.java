package com.metanautix.ev3.server;

import lejos.hardware.lcd.TextLCD;

/**
 * A display widget which renders its text.
 */
public class TagWidget
    extends PositionedWidget
{
    private String mTag;

    public TagWidget
        (int row)
    {
        super(null,-1,0,row);
    }

    public synchronized void setTag
        (String tag)
    {
        mTag=tag;
        if (mTag!=null) {
            setColumn(-mTag.length());
        }
    }

    @Override
    protected synchronized boolean addText
        (StringBuilder builder)
    {
        if (mTag==null) {
            return false;
        }
        builder.append(mTag);
        return true;
    }
}
