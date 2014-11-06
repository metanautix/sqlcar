/* Copyright 2014 Metanautix, Inc. */

package com.metanautix.ev3.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import lejos.hardware.ev3.EV3;
import lejos.hardware.lcd.Font;
import lejos.hardware.lcd.GraphicsLCD;
import lejos.hardware.lcd.Image;
import lejos.hardware.lcd.TextLCD;

/**
 * Screen controller.
 */
public class Screen
{
    private static final int SCREEN_FONT_SIZE=
        Font.SIZE_MEDIUM;
    private static final String BACKGROUND_IMAGE=
        "metanautix.lejos";
    private static final int MINIMUM_ROWS_FOR_WIDGETS=
        3;
    private static final long REFRESH_INTERVAL=
        1000L;

    private GraphicsLCD mGraphicsLcd;
    private int mGraphicsLcdWidth;
    private int mGraphicsLcdHeight;
    private TextLCD mTextLcd;
    private int mTextLcdLastRow;
    private Image mImage;
    private int mGraphicsLcdFirstRow;
    private int mTextLcdFirstRow;
    private ArrayList<Widget> mWidgets;
    private Repeater mUpdater;
    private volatile boolean mTextMode;
    private volatile boolean mFreezeMode;
    private volatile int mPage;

    public Screen
        (EV3 ev3)
    {
        mGraphicsLcd=ev3.getGraphicsLCD();
        mGraphicsLcdWidth=mGraphicsLcd.getWidth();
        mGraphicsLcdHeight=mGraphicsLcd.getHeight();

        mTextLcd=ev3.getTextLCD(Font.getFont(0,0,SCREEN_FONT_SIZE));
        mTextLcdLastRow=mTextLcd.getTextHeight()-1;

        InputStream is=getClass().getResourceAsStream(BACKGROUND_IMAGE);
        try {
            try {
                mImage=Utils.readImage(is);
            } finally {
                is.close();
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Unexpected I/O error",ex);
        }

        // Find last non-empty row.
        byte b[]=mImage.getData();
        mGraphicsLcdFirstRow=mImage.getHeight()-1;
        for (int i=b.length-1;i>=0;--i) {
            if (b[i]!=0) {
                mGraphicsLcdFirstRow=(i/(b.length/mImage.getHeight()));
                break;
            }
        }
        // This is the first empty row below last non-empty row.
        ++mGraphicsLcdFirstRow;

        int fontHeight=mTextLcd.getFont().height;
        mTextLcdFirstRow=(mGraphicsLcdFirstRow+fontHeight-1)/fontHeight;
        if (mTextLcdLastRow-mTextLcdFirstRow+1<MINIMUM_ROWS_FOR_WIDGETS) {
            throw new IllegalStateException
                ("Not enough rows left for widgets; first row is "+
                 mTextLcdFirstRow+" last is "+mTextLcdLastRow);
        }

        mWidgets=new ArrayList<Widget>();
        enterTextMode();
    }

    public void addWidget
        (Widget widget)
    {
        mWidgets.add(widget);
    }

    private void update()
    {
        if (mTextMode || mFreezeMode) {
            return;
        }
        mGraphicsLcd.bitBlt
            (null,
             mGraphicsLcdWidth,mGraphicsLcdHeight,0,0,
             0,mGraphicsLcdFirstRow,
             mGraphicsLcdWidth,mGraphicsLcdHeight-mGraphicsLcdFirstRow,
             GraphicsLCD.ROP_CLEAR);
        final int page=mPage;
        for (Widget widget:mWidgets) {
            widget.render(mTextLcd,page,mTextLcdFirstRow);
        }
    }

    private void startGraphicsUpdates()
    {
        if (mUpdater!=null) {
            return;
        }
        mUpdater=new Repeater(REFRESH_INTERVAL) {
            @Override
            public void repeat()
            {
                update();
            }
        };
        mUpdater.start();
    }

    private void stopGraphicsUpdates()
    {
        if (mUpdater==null) {
            return;
        }
        mUpdater.terminate();
        mUpdater=null;
    }

    private void enterGraphicsMode()
    {
        mGraphicsLcd.clear();
        mGraphicsLcd.drawImage(mImage,0,0,(GraphicsLCD.TOP|GraphicsLCD.LEFT));
        mTextMode=false;
        startGraphicsUpdates();
    }

    private void exitGraphicsMode()
    {
        stopGraphicsUpdates();
    }

    private void enterTextMode()
    {
        mTextLcd.clear();
        mTextMode=true;
    }

    private void exitTextMode() {}

    public synchronized void setTextMode
        (boolean textMode)
    {
        if (mFreezeMode ||
            (textMode==mTextMode)) {
            return;
        }
        if (textMode) {
            exitGraphicsMode();
            enterTextMode();
        } else {
            exitTextMode();
            enterGraphicsMode();
        }
    }

    public boolean getTextMode()
    {
        return mTextMode;
    }

    public void setPage
        (int page)
    {
        if (mTextMode || mFreezeMode) {
            return;
        }
        mPage=page;
    }

    public int getPage()
    {
        return mPage;
    }

    public synchronized void setFreezeMode
        (boolean freezeMode)
    {
        if (freezeMode==mFreezeMode) {
            return;
        }
        if (!mTextMode) {
            if (freezeMode) {
                stopGraphicsUpdates();
            } else {
                startGraphicsUpdates();
            }
        }
        mFreezeMode=freezeMode;
    }

    public boolean getFreezeMode()
    {
        return mFreezeMode;
    }

    public synchronized void debug
        (String text)
    {
        if (!mTextMode || mFreezeMode) {
            return;
        }
        mTextLcd.scroll();
        mTextLcd.drawString(text,0,mTextLcdLastRow);
    }
}
