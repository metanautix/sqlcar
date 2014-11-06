package com.metanautix.ev3.common;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * A simple wrapper around a client or server socket which provides
 * exception type conversion, socket and stream closure, and
 * object-oriented I/O.
 */
public abstract class SocketWrapper<T>
{
    public abstract T communicate
        (ObjectInputStream in,
         ObjectOutputStream out)
        throws ClassNotFoundException,
               IOException;

    public T connect
        (Socket socket)
    {
        try {
            try {
                ObjectOutputStream out=new ObjectOutputStream
                    (socket.getOutputStream());
                try {
                    // The flushing below is necessary so that the
                    // other party's ObjectInputStream() constructor
                    // can complete, because that constructor blocks
                    // until the above stream has written and flushed
                    // its serialization stream header.
                    out.flush();
                    ObjectInputStream in=new ObjectInputStream
                        (socket.getInputStream());
                    try {
                        return communicate(in,out);
                    } finally {
                        in.close();
                    }
                } finally {
                    out.close();
                }
            } finally {
                socket.close();
            }
        } catch (ClassNotFoundException|IOException ex) {
            throw new IllegalStateException(ex);
        }
    }
}
