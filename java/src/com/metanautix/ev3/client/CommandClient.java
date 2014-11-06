package com.metanautix.ev3.client;

import com.metanautix.ev3.common.ColorEvent;
import com.metanautix.ev3.common.Command;
import com.metanautix.ev3.common.DistanceEvent;
import com.metanautix.ev3.common.MotorStateEvent;
import com.metanautix.ev3.common.MovementEvent;
import com.metanautix.ev3.common.SocketWrapper;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

/**
 * A client for socket-provided commands.
 */
public class CommandClient
{
    private String mHost;

    public CommandClient
        (String host)
    {
        mHost=host;
    }

    private Socket getSocket()
    {
        try {
            return new Socket(mHost,Command.PORT);
        } catch (IOException ex) {
            throw new IllegalArgumentException
                ("Could not connect to host "+mHost,ex);
        }
    }

    public void setTag
        (final String tag)
    {
        (new SocketWrapper<Void>() {
            @Override
            public Void communicate
                (ObjectInputStream in,
                 ObjectOutputStream out)
                throws IOException
            {
                out.writeLong(Command.PASSWORD);
                out.writeObject(Command.SET_TAG);
                out.writeObject(tag);
                out.flush();
                in.readInt();
                return null;
            }
        }).connect(getSocket());
    }

    public void calibrate
        (final float maxSpeed)
    {
        (new SocketWrapper<Void>() {
            @Override
            public Void communicate
                (ObjectInputStream in,
                 ObjectOutputStream out)
                throws IOException
            {
                out.writeLong(Command.PASSWORD);
                out.writeObject(Command.CALIBRATE_SPEED);
                out.writeFloat(maxSpeed);
                out.flush();
                in.readInt();
                return null;
            }
        }).connect(getSocket());
    }

    public void calibrate()
    {
        (new SocketWrapper<Void>() {
            @Override
            public Void communicate
                (ObjectInputStream in,
                 ObjectOutputStream out)
                throws IOException
            {
                out.writeLong(Command.PASSWORD);
                out.writeObject(Command.CALIBRATE);
                out.flush();
                in.readInt();
                return null;
            }
        }).connect(getSocket());
    }

    public List<MovementEvent> getMovementEvents()
    {
        return (new SocketWrapper<List<MovementEvent>>() {
            @Override
            @SuppressWarnings("unchecked")
            public List<MovementEvent> communicate
                (ObjectInputStream in,
                 ObjectOutputStream out)
                throws ClassNotFoundException,
                       IOException
            {
                out.writeLong(Command.PASSWORD);
                out.writeObject(Command.GET_MOVEMENT_EVENTS);
                out.flush();
                return (List<MovementEvent>)in.readObject();
            }
        }).connect(getSocket());
    }

    public List<MotorStateEvent> getMotorStateEvents()
    {
        return (new SocketWrapper<List<MotorStateEvent>>() {
            @Override
            @SuppressWarnings("unchecked")
            public List<MotorStateEvent> communicate
                (ObjectInputStream in,
                 ObjectOutputStream out)
                throws ClassNotFoundException,
                       IOException
            {
                out.writeLong(Command.PASSWORD);
                out.writeObject(Command.GET_MOTOR_STATE_EVENTS);
                out.flush();
                return (List<MotorStateEvent>)in.readObject();
            }
        }).connect(getSocket());
    }

    public List<DistanceEvent> getDistanceEvents()
    {
        return (new SocketWrapper<List<DistanceEvent>>() {
            @Override
            @SuppressWarnings("unchecked")
            public List<DistanceEvent> communicate
                (ObjectInputStream in,
                 ObjectOutputStream out)
                throws ClassNotFoundException,
                       IOException
            {
                out.writeLong(Command.PASSWORD);
                out.writeObject(Command.GET_DISTANCE_EVENTS);
                out.flush();
                return (List<DistanceEvent>)in.readObject();
            }
        }).connect(getSocket());
    }

    public List<ColorEvent> getColorEvents()
    {
        return (new SocketWrapper<List<ColorEvent>>() {
            @Override
            @SuppressWarnings("unchecked")
            public List<ColorEvent> communicate
                (ObjectInputStream in,
                 ObjectOutputStream out)
                throws ClassNotFoundException,
                       IOException
            {
                out.writeLong(Command.PASSWORD);
                out.writeObject(Command.GET_COLOR_EVENTS);
                out.flush();
                return (List<ColorEvent>)in.readObject();
            }
        }).connect(getSocket());
    }

    public void playMovementEvents
        (final List<MovementEvent> events)
    {
        (new SocketWrapper<Void>() {
            @Override
            public Void communicate
                (ObjectInputStream in,
                 ObjectOutputStream out)
                throws IOException
            {
                out.writeLong(Command.PASSWORD);
                out.writeObject(Command.PLAY_MOVEMENT_EVENTS);
                out.writeObject(events);
                out.flush();
                in.readInt();
                return null;
            }
        }).connect(getSocket());
    }

    public void playMovementEvent
        (MovementEvent event)
    {
        LinkedList<MovementEvent> list=new LinkedList<MovementEvent>();
        list.add(event);
        playMovementEvents(list);
    }

    public void terminate()
    {
        (new SocketWrapper<Void>() {
            @Override
            public Void communicate
                (ObjectInputStream in,
                 ObjectOutputStream out)
                throws IOException
            {
                out.writeLong(Command.PASSWORD);
                out.writeObject(Command.TERMINATE);
                out.flush();
                return null;
            }
        }).connect(getSocket());
    }
}
