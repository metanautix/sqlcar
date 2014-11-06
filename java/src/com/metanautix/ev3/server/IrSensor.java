/* Copyright 2014 Metanautix, Inc. */

package com.metanautix.ev3.server;

import java.util.ArrayList;
import java.util.Arrays;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3IRSensor;
import lejos.hardware.sensor.SensorMode;

/**
 * Infrared sensor controller.
 */
public class IrSensor
{
    private static final long POLL_INTERVAL=
        100L;

    public static enum Channel
    {
        CH1(0),
        CH2(1),
        CH3(2),
        CH4(3);

        public static final int SIZE=
            Channel.values().length;

        private final int mId;

        private Channel
            (int id)
        {
            mId=id;
        }

        public int getId()
        {
            return mId;
        }
    }

    public static enum Button
    {
        NONE(0),
        TL(1),
        BL(2),
        TR(3),
        BR(4),
        TL_TR(5),
        TL_BL(10),
        TL_BR(6),
        TR_BL(7),
        TR_BR(11),
        BL_BR(8),
        BEACON(9);

        // Map from ID to Button.
        public static final Button[] FROM_ID=
            new Button[] {
                NONE,TL,BL,TR,BR,TL_TR,TL_BR,TR_BL,BL_BR,BEACON,TL_BL,TR_BR
            };

        private final int mId;

        private Button
            (int id)
        {
            mId=id;
        }

        public int getId()
        {
            return mId;
        }
    }

    public static enum ButtonMask
    {
        NONE(0),
        TL(0x1),
        TR(0x2),
        BL(0x4),
        BR(0x8),
        BEACON(0x10),
        TL_TR(TL.getValue()|TR.getValue()),
        TL_BL(TL.getValue()|BL.getValue()),
        TL_BR(TL.getValue()|BR.getValue()),
        TR_BL(TR.getValue()|BL.getValue()),
        TR_BR(TR.getValue()|BR.getValue()),
        BL_BR(BL.getValue()|BR.getValue());

        // Map from Button ID to ButtonMask.
        public static final ButtonMask[] FROM_BUTTON_ID=
            new ButtonMask[] {
                NONE,TL,BL,TR,BR,TL_TR,TL_BR,TR_BL,BL_BR,BEACON,TL_BL,TR_BR
            };

        private final int mValue;

        private ButtonMask
            (int value)
        {
            mValue=value;
        }

        public int getValue()
        {
            return mValue;
        }
    }

    private EV3IRSensor mSensor;
    private SensorMode mDistanceMode;
    private volatile boolean mDistanceModeActive;
    private ArrayList<ArrayList<RemoteListener>> mRemoteListeners;
    private ArrayList<DistanceListener> mDistanceListeners;
    private volatile Button[] mButton;
    private byte[] mNewButton;
    private volatile float mDistance;
    private float[] mNewDistance;
    private Repeater mPoller;

    public IrSensor
        (Port port)
    {
        mSensor=new EV3IRSensor(port);
        mDistanceMode=mSensor.getDistanceMode();
        mRemoteListeners=new ArrayList<ArrayList<RemoteListener>>
            (Channel.SIZE);
        for (int i=0;i<Channel.SIZE;++i) {
            mRemoteListeners.add(new ArrayList<RemoteListener>());
        }
        mDistanceListeners=new ArrayList<DistanceListener>();
        mButton=new Button[Channel.SIZE];
        Arrays.fill(mButton,Button.NONE);
        mNewButton=new byte[Channel.SIZE];
        if (mDistanceMode.sampleSize()!=1) {
            throw new IllegalStateException
                ("Infrared sensor is expected to yield one distance sample"+
                 " but yields "+mDistanceMode.sampleSize());
        }
        mNewDistance=new float[1];
    }

    public void addRemoteListener
        (Channel channel,
         RemoteListener listener)
    {
        mRemoteListeners.get(channel.getId()).add(listener);
    }

    public void addDistanceListener
        (DistanceListener listener)
    {
        mDistanceListeners.add(listener);
    }

    private void poll()
    {
        if (mDistanceModeActive) {
            mDistanceMode.fetchSample(mNewDistance,0);
            if (mDistance!=mNewDistance[0]) {
                for (DistanceListener listener:mDistanceListeners) {
                    listener.distanceChanged(mDistance,mNewDistance[0]);
                }
                mDistance=mNewDistance[0];
            }
            return;
        }
        mSensor.getRemoteCommands(mNewButton,0,Channel.SIZE);
        int channel=0;
        for (ArrayList<RemoteListener> listeners:mRemoteListeners) {
            Button newButton=Button.FROM_ID[mNewButton[channel]];
            if (mButton[channel]!=newButton) {
                for (RemoteListener listener:listeners) {
                    listener.buttonChanged(mButton[channel],newButton);
                }
                mButton[channel]=newButton;
            }
            ++channel;
        }
    }

    public synchronized void startPoller()
    {
        if (mPoller!=null) {
            return;
        }
        mPoller=new Repeater(POLL_INTERVAL) {
            @Override
            public void repeat()
            {
                poll();
            }
        };
        mPoller.start();
    }

    public synchronized void stopPoller()
    {
        if (mPoller==null) {
            return;
        }
        mPoller.terminate();
        mPoller=null;
    }

    public void setDistanceModeActive
        (boolean distanceModeActive)
    {
        mDistanceModeActive=distanceModeActive;
    }

    public boolean getDistanceModeActive()
    {
        return mDistanceModeActive;
    }

    public Button getButton
        (Channel channel)
    {
        return mButton[channel.getId()];
    }

    public ButtonMask getButtonMask
        (Channel channel)
    {
        return ButtonMask.FROM_BUTTON_ID[getButton(channel).getId()];
    }

    public float getDistance()
    {
        return mDistance;
    }
}
