package com.metanautix.ev3.server;

import java.io.File;
import lejos.hardware.Audio;
import lejos.hardware.ev3.EV3;

/**
 * Speaker controller.
 */
public class Speaker
{
    public static enum Sample
    {
        BLACK("black"),
        BLUE("blue"),
        GREEN("green"),
        YELLOW("yellow"),
        RED("red"),
        WHITE("white"),
        BROWN("brown");

        private File mFile;

        private Sample
            (String file)
        {
            file=file+".wav";
            mFile=Utils.copyToTempFile
                (getClass().getResourceAsStream(file),file);
        }

        public File getFile()
        {
            return mFile;
        }
    }

    public static class SystemSound
    {
        public static final long NO_REPEAT=
            -1L;
        public static final SystemSound SINGLE_ASCENDING=
            new SystemSound(Audio.ASCENDING,NO_REPEAT);
        public static final SystemSound SINGLE_BEEP=
            new SystemSound(Audio.BEEP,NO_REPEAT);
        public static final SystemSound REPEAT_BEEP_500=
            new SystemSound(Audio.BEEP,500L);

        private int mSound;
        private long mDelay;

        private SystemSound
            (int sound,
             long delay)
        {
            mSound=sound;
            mDelay=delay;
        }

        public int getSound()
        {
            return mSound;
        }

        public long getDelay()
        {
            return mDelay;
        }
    }

    private class Player
        extends Repeater
    {
        private SystemSound mSystemSound;

        public Player
            (SystemSound systemSound)
        {
            super(systemSound.getDelay());
            mSystemSound=systemSound;
        }

        @Override
        public void repeat()
        {
            playUnsafe(mSystemSound.getSound());
        }

        public SystemSound getSystemSound()
        {
            return mSystemSound;
        }
    }

    private Audio mAudio;
    private volatile boolean mSuppressSystemSound;
    private Player mPlayer;

    public Speaker
        (EV3 ev3)
    {
        mAudio=ev3.getAudio();
    }

    private void playUnsafe
        (int sound)
    {
        if (mSuppressSystemSound) {
            return;
        }
        mAudio.systemSound(sound);
    }

    public synchronized void play
        (Sample sample)
    {
        stop();
        mAudio.playSample(sample.getFile());
    }

    public synchronized void play
        (SystemSound systemSound)
    {
        if (mPlayer!=null) {
            if (systemSound==mPlayer.getSystemSound()) {
                return;
            }
            stop();
        }
        if (systemSound.getDelay()==SystemSound.NO_REPEAT) {
            playUnsafe(systemSound.getSound());
            return;
        }
        mPlayer=new Player(systemSound);
        mPlayer.start();
    }

    public synchronized void stop()
    {
        if (mPlayer==null) {
            return;
        }
        mPlayer.terminate();
        mPlayer=null;
    }

    public void setSuppressSystemSound
        (boolean suppressSystemSound)
    {
        mSuppressSystemSound=suppressSystemSound;
    }

    public boolean getSuppressSystemSound()
    {
        return mSuppressSystemSound;
    }
}
