package com.metanautix.ev3.server;

import com.metanautix.ev3.common.Angle;
import com.metanautix.ev3.common.Color;
import com.metanautix.ev3.common.ColorEvent;
import com.metanautix.ev3.common.Direction;
import com.metanautix.ev3.common.DistanceEvent;
import com.metanautix.ev3.common.MotorStateEvent;
import com.metanautix.ev3.common.MovementEvent;
import lejos.hardware.Audio;
import lejos.hardware.ev3.EV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.utility.Delay;

/**
 * A drivable car.
 */
public class Car
{
    public static class DistancePattern
    {
        private float mDistance;
        private Led.Pattern mPattern;

        public DistancePattern
            (float distance,
             Led.Pattern pattern)
        {
             mDistance=distance;
             mPattern=pattern;
        }

        public float getDistance()
        {
            return mDistance;
        }

        public Led.Pattern getPattern()
        {
            return mPattern;
        }
    }

    private static final DistancePattern[] UNSAFE=new DistancePattern[] {
        new DistancePattern(15.0f,Led.Pattern.STATIC_ORANGE),
        new DistancePattern(25.0f,Led.Pattern.FAST_BLINKING_ORANGE),
        new DistancePattern(45.0f,Led.Pattern.NORMAL_BLINKING_ORANGE)
    };

    private static final Led.Pattern CALIBRATION_PATTERN=
        Led.Pattern.NORMAL_BLINKING_ORANGE;
    private static final Led.Pattern FORWARD_PATTERN=
        Led.Pattern.NORMAL_BLINKING_GREEN;
    private static final Led.Pattern STOPPED_PATTERN=
        Led.Pattern.OFF;
    private static final Led.Pattern REVERSE_PATTERN=
        Led.Pattern.NORMAL_BLINKING_RED;

    private static final Speaker.SystemSound CALIBRATION_COMPLETE_SOUND=
        Speaker.SystemSound.SINGLE_ASCENDING;
    private static final Speaker.SystemSound FORWARD_SOUND=
        null;
    private static final Speaker.SystemSound STOPPED_SOUND=
        null;
    private static final Speaker.SystemSound REVERSE_SOUND=
        Speaker.SystemSound.REPEAT_BEEP_500;

    private static final int CALIBRATION_SPEED=
        60;
    private static final long CALIBRATION_POLL_INTERVAL=
        50L;
    private static final float DRIVING_SPEED_SLOWDOWN_FACTOR=
        0.9f;
    // Experimentally determined by measuring car geometry.
    private static final float REAR_WHEEL_DISTANCE_CM=
        13.5f;
    private static final float INNER_WHEEL_TURN_RADIUS_CM=
        30.5f;
    // Turning geometry calculation.
    private static final float INNER_WHEEL_SLOWDOWN_FACTOR=
        (INNER_WHEEL_TURN_RADIUS_CM/
         (INNER_WHEEL_TURN_RADIUS_CM+REAR_WHEEL_DISTANCE_CM));

    private Recorder mRecorder;
    private Player mPlayer;
    private Screen mScreen;
    private Speaker mSpeaker;
    private Led mLed;
    private ButtonPad mButtonPad;
    private ColorSensor mColorSensor;
    private IrSensor mIrSensor;
    private EV3MediumRegulatedMotor mSteeringMotor;
    private EV3LargeRegulatedMotor mLeftMotor;
    private EV3LargeRegulatedMotor mRightMotor;
    private TagWidget mTagWidget;
    private volatile Led.Pattern mSafePattern;
    private volatile Speaker.Sample mSample;
    private volatile Speaker.SystemSound mSystemSound;
    private volatile int mMaxSteeringAngle;
    private volatile int mCenterSpeed;
    private volatile int mTurningSpeed;

    public Car
        (EV3 ev3)
    {
        mRecorder=new Recorder();
        mPlayer=new Player(this);

        mScreen=new Screen(ev3);
        mSpeaker=new Speaker(ev3);
        mLed=new Led(ev3);
        mButtonPad=new ButtonPad(ev3);
        mColorSensor=new ColorSensor(SensorPort.S3);
        mIrSensor=new IrSensor(SensorPort.S4);

        mSteeringMotor=new EV3MediumRegulatedMotor(MotorPort.A);
        mLeftMotor=new EV3LargeRegulatedMotor(MotorPort.B);
        mRightMotor=new EV3LargeRegulatedMotor(MotorPort.C);

        mTagWidget=new TagWidget(0);

        mScreen.addWidget(mTagWidget);
        mScreen.addWidget(new BatteryWidget
                          (ev3.getPower(),"B",0,0,0));
        mScreen.addWidget(new ColorSensorWidget
                          (mColorSensor,"C",0,0,1));
        mScreen.addWidget(new IrSensorWidget
                          (mIrSensor,IrSensor.Channel.CH1,"I",0,0,2));
        mScreen.addWidget(new BaseRegulatedMotorWidget
                          (mSteeringMotor,"S",1,0,0));
        mScreen.addWidget(new BaseRegulatedMotorWidget
                          (mLeftMotor,"L",1,0,1));
        mScreen.addWidget(new BaseRegulatedMotorWidget
                          (mRightMotor,"R",1,0,2));
        mScreen.setTextMode(false);

        mButtonPad.addListener(ButtonPad.Button.ESCAPE,
                               new ExitButtonListener());
        mButtonPad.addListener(ButtonPad.Button.ENTER,
                               new ButtonListener.Empty() {
            @Override
            public void buttonPressed
                (ButtonPad.Button button)
            {
                mScreen.setFreezeMode(false);
                mScreen.setPage
                    ((mScreen.getPage()+1)%2);
            }
        });
        mButtonPad.addListener(ButtonPad.Button.LEFT,
                               new ButtonListener.Empty() {
            @Override
            public void buttonPressed
                (ButtonPad.Button button)
            {
                mScreen.setFreezeMode
                    (!mScreen.getFreezeMode());
            }
        });
        mButtonPad.addListener(ButtonPad.Button.RIGHT,
                               new ButtonListener.Empty() {
            @Override
            public void buttonPressed
                (ButtonPad.Button button)
            {
                mSpeaker.setSuppressSystemSound
                    (!mSpeaker.getSuppressSystemSound());
            }
        });
        mButtonPad.addListener(ButtonPad.Button.DOWN,
                               new ButtonListener.Empty() {
            @Override
            public void buttonPressed
                (ButtonPad.Button button)
            {
                calibrate(false);
            }
        });
        mButtonPad.addListener(ButtonPad.Button.UP,
                               new ButtonListener.Empty() {
            @Override
            public void buttonPressed
                (ButtonPad.Button button)
            {
                calibrate(true);
            }
        });

        mColorSensor.addColorListener(new ColorListener.Empty() {
            @Override
            public void colorChanged
                (Color oldColor,
                 Color newColor)
            {
                mRecorder.record(new ColorEvent(newColor));
                mSample=Utils.COLOR_SAMPLE_MAP.get(newColor);
                updateLedSpeaker();
            }
        });

        mIrSensor.addRemoteListener(IrSensor.Channel.CH1,
                                    new RemoteListener.Empty() {
            @Override
            public void buttonChanged
                (IrSensor.Button oldButton,
                 IrSensor.Button newButton)
            {
                switch (newButton) {
                case TL:
                    power(Angle.LEFT,Direction.FORWARD);
                    break;
                case TR:
                    power(Angle.RIGHT,Direction.FORWARD);
                    break;
                case TL_TR:
                    power(Angle.CENTER,Direction.FORWARD);
                    break;
                case BL:
                    power(Angle.LEFT,Direction.REVERSE);
                    break;
                case BR:
                    power(Angle.RIGHT,Direction.REVERSE);
                    break;
                case BL_BR:
                    power(Angle.CENTER,Direction.REVERSE);
                    break;
                case NONE:
                    power(null,Direction.STOPPED);
                    break;
                }
            }
        });
        mIrSensor.addDistanceListener(new DistanceListener.Empty() {
            @Override
            public void distanceChanged
                (float oldDistance,
                 float newDistance)
            {
                mRecorder.record(new DistanceEvent(newDistance));
                updateLedSpeaker();
            }
        });

        calibrate(true);
    }

    public Recorder getRecorder()
    {
        return mRecorder;
    }

    public Player getPlayer()
    {
        return mPlayer;
    }

    public void setTag
        (String tag)
    {
        mTagWidget.setTag(tag);
    }

    public void setAutopilotMode
        (boolean autopilotMode)
    {
        mIrSensor.setDistanceModeActive(autopilotMode);
        updateLedSpeaker();
    }

    public synchronized void calibrate
        (boolean steering,
         float maxSpeed)
    {
        mPlayer.stopAutopilot();
        mIrSensor.stopPoller();
        mColorSensor.stopPoller();
        mSample=null;
        // Also clears the Led and Speaker.
        power(null,Direction.STOPPED);
        mSpeaker.setSuppressSystemSound(false);
        mTagWidget.setTag(null);

        if (steering) {
            mScreen.setPage(1);
            mLed.setPattern(CALIBRATION_PATTERN);

            mSteeringMotor.setSpeed(CALIBRATION_SPEED);

            mSteeringMotor.forward();
            while (!mSteeringMotor.isStalled()) {
                Delay.msDelay(CALIBRATION_POLL_INTERVAL);
            }
            int leftEdge=mSteeringMotor.getTachoCount();

            mSteeringMotor.backward();
            while (!mSteeringMotor.isStalled()) {
                Delay.msDelay(CALIBRATION_POLL_INTERVAL);
            }
            int rightEdge=mSteeringMotor.getTachoCount();

            mSteeringMotor.rotateTo((leftEdge+rightEdge)/2);
            mSteeringMotor.resetTachoCount();
            mMaxSteeringAngle=(leftEdge-rightEdge)/2;

            mSteeringMotor.setSpeed(mSteeringMotor.getMaxSpeed());
        }
        mCenterSpeed=(int)(maxSpeed+0.5f);
        mTurningSpeed=(int)(maxSpeed*INNER_WHEEL_SLOWDOWN_FACTOR+0.5f);

        // Also clears the Led (if in a calibration pattern).
        power(Angle.CENTER,Direction.STOPPED);
        // Also clears events created by above call to power().
        mRecorder.clear();
        mLeftMotor.resetTachoCount();
        mRightMotor.resetTachoCount();

        mSpeaker.play(CALIBRATION_COMPLETE_SOUND);
        mScreen.setPage(0);
        mColorSensor.startPoller();
        mIrSensor.startPoller();
    }

    public void calibrate
        (boolean steering)
    {
        calibrate(steering,
                  DRIVING_SPEED_SLOWDOWN_FACTOR*
                  Math.min(mLeftMotor.getMaxSpeed(),
                           mRightMotor.getMaxSpeed()));
    }

    public void updateLedSpeaker()
    {
        Led.Pattern pattern=mSafePattern;
        if (mIrSensor.getDistanceModeActive()) {
            for (int i=0;i<UNSAFE.length;++i) {
                if (mIrSensor.getDistance()<UNSAFE[i].getDistance()) {
                    pattern=UNSAFE[i].getPattern();
                    break;
                }
            }
        }
        mLed.setPattern(pattern);
        synchronized (mSpeaker) {
            if (mSample!=null) {
                mSpeaker.play(mSample);
                mSample=null;
            }
            if (mSystemSound==null) {
                mSpeaker.stop();
            } else {
                mSpeaker.play(mSystemSound);
            }
        }
    }

    public synchronized void power
        (Angle angle,
         Direction direction)
    {
        mRecorder.record(new MovementEvent(angle,direction));
        mRecorder.record(new MotorStateEvent(mSteeringMotor.getTachoCount(),
                                             mLeftMotor.getTachoCount(),
                                             mRightMotor.getTachoCount(),
                                             mCenterSpeed));
        if (angle==null) {
            if (direction!=Direction.STOPPED) {
                throw new IllegalArgumentException
                    ("Null angle requires stopped direction: "+direction);
            }
        } else {
            switch (angle) {
            case LEFT:
                mSteeringMotor.rotateTo(mMaxSteeringAngle);
                mLeftMotor.setSpeed(mTurningSpeed);
                mRightMotor.setSpeed(mCenterSpeed);
                break;
            case CENTER:
                mSteeringMotor.rotateTo(0);
                mLeftMotor.setSpeed(mCenterSpeed);
                mRightMotor.setSpeed(mCenterSpeed);
                break;
            case RIGHT:
                mSteeringMotor.rotateTo(-mMaxSteeringAngle);
                mLeftMotor.setSpeed(mCenterSpeed);
                mRightMotor.setSpeed(mTurningSpeed);
                break;
            }
        }
        switch (direction) {
        case FORWARD:
            mSafePattern=FORWARD_PATTERN;
            mSystemSound=FORWARD_SOUND;
            mLeftMotor.backward();
            mRightMotor.backward();
            break;
        case STOPPED:
            mSafePattern=STOPPED_PATTERN;
            mSystemSound=STOPPED_SOUND;
            mLeftMotor.stop();
            mLeftMotor.flt();
            mRightMotor.stop();
            mRightMotor.flt();
            mSteeringMotor.flt();
            break;
        case REVERSE:
            mSafePattern=REVERSE_PATTERN;
            mSystemSound=REVERSE_SOUND;
            mLeftMotor.forward();
            mRightMotor.forward();
            break;
        }
        updateLedSpeaker();
    }
}
