package com.metanautix.ev3.server;

import com.metanautix.ev3.common.Color;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import lejos.hardware.lcd.Image;
import lejos.hardware.sensor.SensorModes;
import lejos.utility.Delay;

/**
 * General-purpose utilities.
 */
public final class Utils
{
    public static final Random RANDOM=
        new Random();
    public static final int BUFFER_SIZE=
        8192;
    public static final String TMP_DIR=
        System.getProperty("java.io.tmpdir");
    public static final Map<Color,Speaker.Sample> COLOR_SAMPLE_MAP=
        new HashMap<Color,Speaker.Sample>();
    public static final Map<Speaker.Sample,Color> SAMPLE_COLOR_MAP=
        new HashMap<Speaker.Sample,Color>();

    static {
        COLOR_SAMPLE_MAP.put(Color.BLACK,Speaker.Sample.BLACK);
        SAMPLE_COLOR_MAP.put(Speaker.Sample.BLACK,Color.BLACK);
        COLOR_SAMPLE_MAP.put(Color.BLUE,Speaker.Sample.BLUE);
        SAMPLE_COLOR_MAP.put(Speaker.Sample.BLUE,Color.BLUE);
        COLOR_SAMPLE_MAP.put(Color.GREEN,Speaker.Sample.GREEN);
        SAMPLE_COLOR_MAP.put(Speaker.Sample.GREEN,Color.GREEN);
        COLOR_SAMPLE_MAP.put(Color.YELLOW,Speaker.Sample.YELLOW);
        SAMPLE_COLOR_MAP.put(Speaker.Sample.YELLOW,Color.YELLOW);
        COLOR_SAMPLE_MAP.put(Color.RED,Speaker.Sample.RED);
        SAMPLE_COLOR_MAP.put(Speaker.Sample.RED,Color.RED);
        COLOR_SAMPLE_MAP.put(Color.WHITE,Speaker.Sample.WHITE);
        SAMPLE_COLOR_MAP.put(Speaker.Sample.WHITE,Color.WHITE);
        COLOR_SAMPLE_MAP.put(Color.BROWN,Speaker.Sample.BROWN);
        SAMPLE_COLOR_MAP.put(Speaker.Sample.BROWN,Color.BROWN);
    }

    public static void terminate()
    {
        System.exit(0);
    }

    public static Image readImage
        (InputStream is)
        throws IOException
    {
        DataInputStream dis=new DataInputStream(is);
        int p=dis.readInt();
        if (p!=0x4c4e4930)
            throw new IOException("File format error");
        int w=dis.readUnsignedShort();
        int h=dis.readUnsignedShort();
        // Fix for incorrect formula, w*((h+7)/8), used in Image class
        // constructor.
        byte[] imageData=new byte[h*((w+7)/8)];
        dis.readFully(imageData);
        return new Image(w,h,imageData);
    }

    public static void showAvailableModes
        (Screen screen,
         String name,
         SensorModes modes)
    {
        screen.debug(name);
        for (String mode:modes.getAvailableModes()) {
            screen.debug(" "+mode);
        }
    }

    public static void waitRandom
        (int minWait,
         int maxWait)
    {
        Delay.msDelay(minWait+RANDOM.nextInt(maxWait-minWait+1));
    }

    public static void waitForever()
    {
        Object waiter=new Object();
        synchronized (waiter) {
            try {
                waiter.wait();
            } catch (InterruptedException ex) {
                throw new IllegalStateException("Unexpected interruption",ex);
            }
        }
    }

    public static File copyToTempFile
        (InputStream in,
         String name)
    {
        File file=new File(TMP_DIR,name);
        try {
            try {
                FileOutputStream out=new FileOutputStream(file);
                try {
                    byte[] buffer=new byte[BUFFER_SIZE];
                    while (true) {
                        int count=in.read(buffer);
                        if (count==-1) {
                            break;
                        }
                        out.write(buffer,0,count);
                    }
                } finally {
                    out.close();
                }
            } finally {
                in.close();
            }
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
        return file;
    }
}
