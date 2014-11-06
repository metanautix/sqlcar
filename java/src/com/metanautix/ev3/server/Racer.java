package com.metanautix.ev3.server;

import lejos.hardware.BrickFinder;
import lejos.hardware.ev3.EV3;

/**
 * Main program: set up a car and associated socket-provided command
 * server, and wait forever (i.e. until the user terminates the
 * application).
 */
public class Racer
{
    public static void main
        (String[] args)
    {
        Car car=new Car((EV3)BrickFinder.getLocal());
        (new CommandServer(car.getRecorder(),car.getPlayer())).start();
        Utils.waitForever();
    }
}
