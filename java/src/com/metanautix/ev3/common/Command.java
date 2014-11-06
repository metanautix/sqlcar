/* Copyright 2014 Metanautix, Inc. */

package com.metanautix.ev3.common;

/**
 * A socket-provided command.
 */
public enum Command
{
    SET_TAG,
    CALIBRATE_SPEED,
    CALIBRATE,
    GET_MOVEMENT_EVENTS,
    GET_MOTOR_STATE_EVENTS,
    GET_DISTANCE_EVENTS,
    GET_COLOR_EVENTS,
    PLAY_MOVEMENT_EVENTS,
    TERMINATE;

    public static final int PORT=
        9801;
    public static final long PASSWORD=
        0xA9213BDA78920DFAL;
    public static final int ACK=
        0;
}
