package com.metanautix.ev3.server;

import lejos.hardware.lcd.TextLCD;

/**
 * A widget which renders some text on the screen when in graphics
 * mode and on the given page, but using the screen's textLcd
 * interface. The first text row available for use is firstRow.
 */
public interface Widget
{
    public void render
        (TextLCD textLcd,
         int page,
         int firstRow);
}
