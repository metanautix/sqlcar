/* Copyright 2014 Metanautix, Inc. */

package com.metanautix.ev3.server;

import com.metanautix.ev3.common.Command;
import com.metanautix.ev3.common.MovementEvent;
import com.metanautix.ev3.common.SocketWrapper;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.util.List;

/**
 * A server for socket-provided commands.
 */
public class CommandServer
    extends Thread
{
    private Recorder mRecorder;
    private Player mPlayer;

    public CommandServer
        (Recorder recorder,
         Player player)
    {
        setDaemon(true);
        mRecorder=recorder;
        mPlayer=player;
    }

    @Override
    public void run()
    {
        ServerSocket serverSocket;
        try {
            serverSocket=new ServerSocket(Command.PORT);
        } catch (IOException ex) {
            throw new IllegalArgumentException
                ("Could not start server",ex);
        }
        while (true) {
            try {
                (new SocketWrapper<Void>() {
                    @Override
                    @SuppressWarnings("unchecked")
                    public Void communicate
                        (ObjectInputStream in,
                         ObjectOutputStream out)
                        throws ClassNotFoundException,
                               IOException
                    {
                        long password=in.readLong();
                        if (password!=Command.PASSWORD) {
                            Utils.waitRandom(100,300);
                            throw new IllegalStateException
                                ("Bad password");
                        }
                        Command command=(Command)in.readObject();
                        switch (command) {
                        case SET_TAG:
                            mPlayer.setTag((String)in.readObject());
                            out.writeInt(Command.ACK);
                            break;
                        case CALIBRATE_SPEED:
                            mPlayer.calibrate(in.readFloat());
                            out.writeInt(Command.ACK);
                            break;
                        case CALIBRATE:
                            mPlayer.calibrate();
                            out.writeInt(Command.ACK);
                            break;
                        case GET_MOVEMENT_EVENTS:
                            out.writeObject(mRecorder.getMovementEvents());
                            break;
                        case GET_MOTOR_STATE_EVENTS:
                            out.writeObject(mRecorder.getMotorStateEvents());
                            break;
                        case GET_DISTANCE_EVENTS:
                            out.writeObject(mRecorder.getDistanceEvents());
                            break;
                        case GET_COLOR_EVENTS:
                            out.writeObject(mRecorder.getColorEvents());
                            break;
                        case PLAY_MOVEMENT_EVENTS:
                            mPlayer.stopAutopilot();
                            mPlayer.startAutopilot
                                ((List<MovementEvent>)in.readObject());
                            out.writeInt(Command.ACK);
                            break;
                        case TERMINATE:
                            Utils.terminate();
                            break;
                        default:
                            throw new IllegalStateException
                                ("Unknown command: "+command);
                        }
                        out.flush();
                        return null;
                    }
                }).connect(serverSocket.accept());
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }
}
