/* Copyright 2014 Metanautix, Inc. */

package com.metanautix.ev3.server;

import lejos.hardware.lcd.TextLCD;

/**
 * A display widget with renders its text (which contains a fixed
 * prefix followed by a colon) at the same (absolute or relative)
 * position every time.
 */
public abstract class PositionedWidget
    implements Widget
{
    private String mPrefix;
    private int mPage;
    private int mColumn;
    private int mRow;

    public PositionedWidget
        (String name,
         int page,
         int column,
         int row)
    {
        if (name!=null) {
            mPrefix=name+':';
        }
        mPage=page;
        mColumn=column;
        mRow=row;
    }

    public synchronized void setPage
        (int page)
    {
        mPage=page;
    }

    public synchronized void setColumn
        (int column)
    {
        mColumn=column;
    }

    public synchronized void setRow
        (int row)
    {
        mRow=row;
    }

    protected abstract boolean addText
        (StringBuilder builder);

    @Override
    public synchronized void render
        (TextLCD textLcd,
         int page,
         int firstRow)
    {
        if ((mPage>=0) && (page!=mPage)) {
            return;
        }
        StringBuilder builder=new StringBuilder();
        if (mPrefix!=null) {
            builder.append(mPrefix);
        }
        if (!addText(builder)) {
            return;
        }
        textLcd.drawString(builder.toString(),
                           ((mColumn>=0)?mColumn:
                            (textLcd.getTextWidth()+mColumn)),
                           ((mRow>=0)?(firstRow+mRow):
                            textLcd.getTextHeight()+mRow));
    }
}
