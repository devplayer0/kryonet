package com.esotericsoftware.kryonet.adapters;

import com.esotericsoftware.kryonet.Connection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.esotericsoftware.minlog.Log.*;

/**
 * Created by Evan on 6/9/16.
 */

public class ConnectionAdapter<T extends Connection> implements Listener<T> {

    @Override
    public void connected(T connection) {

    }

    @Override
    public void disconnected(T connection) {

    }

    @Override
    public void received(T connection, Object object) {

    }

    @Override
    public void idle(T connection) {

    }




    /** Uses reflection to called "received(Connection, XXX)" on the listener, where XXX is the received object type. Note this
     * class uses a HashMap lookup and (cached) reflection, so is not as efficient as writing a series of "instanceof" statements. */
    static public class ReflectionListener<T extends Connection> extends ConnectionAdapter<T> {
        private final HashMap<Class, Method> classToMethod = new HashMap<>();

        @Override
        public void received (Connection connection, Object object) {
            Class type = object.getClass();
            Method method = classToMethod.get(type);
            if (method == null) {
                if (classToMethod.containsKey(type)) return; // Only fail on the first attempt to find the method.
                try {
                    method = getClass().getMethod("received", Connection.class, type);
                    method.setAccessible(true);
                } catch (SecurityException ex) {
                    if (ERROR) error("kryonet", "Unable to access method: received(Connection, " + type.getName() + ")", ex);
                    return;
                } catch (NoSuchMethodException ex) {
                    if (DEBUG)
                        debug("kryonet",
                                "Unable to find listener method: " + getClass().getName() + "#received(Connection, " + type.getName() + ")");
                    return;
                } finally {
                    classToMethod.put(type, method);
                }
            }
            try {
                method.invoke(this, connection, object);
            } catch (Throwable ex) {
                if (ex instanceof InvocationTargetException && ex.getCause() != null) ex = ex.getCause();
                if (ex instanceof RuntimeException) throw (RuntimeException)ex;
                throw new RuntimeException("Error invoking method: " + getClass().getName() + "#received(Connection, "
                        + type.getName() + ")", ex);
            }
        }
    }

    /** Wraps a listener and queues notifications as {@link Runnable runnables}. This allows the runnables to be processed on a
     * different thread, preventing the connection's update thread from being blocked. */
    static public abstract class QueuedListener<T extends Connection> extends ConnectionAdapter<T> {
        final Listener listener;

        public QueuedListener (Listener listener) {
            if (listener == null) throw new IllegalArgumentException("listener cannot be null.");
            this.listener = listener;
        }

        @Override
        public void connected (final T connection) {
            queue(new Runnable() {
                public void run () {
                    listener.connected(connection);
                }
            });
        }

        @Override
        public void disconnected (final T connection) {
            queue(new Runnable() {
                public void run () {
                    listener.disconnected(connection);
                }
            });
        }

        @Override
        public void received (final T connection, final Object object) {
            queue(new Runnable() {
                public void run () {
                    listener.received(connection, object);
                }
            });
        }

        @Override
        public void idle (final T connection) {
            queue(new Runnable() {
                public void run () {
                    listener.idle(connection);
                }
            });
        }

        abstract protected void queue (Runnable runnable);
    }

    /** Wraps a listener and processes notification events on a separate thread. */
    static public class ThreadedListener<T extends Connection> extends QueuedListener<T> {
        protected final ExecutorService threadPool;

        /** Creates a single thread to process notification events. */
        public ThreadedListener (Listener listener) {
            this(listener, Executors.newFixedThreadPool(1));
        }

        /** Uses the specified threadPool to process notification events. */
        public ThreadedListener (Listener listener, ExecutorService threadPool) {
            super(listener);
            if (threadPool == null) throw new IllegalArgumentException("threadPool cannot be null.");
            this.threadPool = threadPool;
        }

        @Override
        public void queue (Runnable runnable) {
            threadPool.execute(runnable);
        }
    }

    /** Delays the notification of the wrapped listener to simulate lag on incoming objects. Notification events are processed on a
     * separate thread after a delay. Note that only incoming objects are delayed. To delay outgoing objects, use a LagListener at
     * the other end of the connection. */
    static public class LagListener<T extends Connection> extends QueuedListener<T> {
        private final ScheduledExecutorService threadPool;
        private final int lagMillisMin, lagMillisMax;
        final LinkedList<Runnable> runnables = new LinkedList<>();

        public LagListener (int lagMillisMin, int lagMillisMax, Listener listener) {
            super(listener);
            this.lagMillisMin = lagMillisMin;
            this.lagMillisMax = lagMillisMax;
            threadPool = Executors.newScheduledThreadPool(1);
        }

        @Override
        public void queue (Runnable runnable) {
            synchronized (runnables) {
                runnables.addFirst(runnable);
            }
            int lag = lagMillisMin + (int)(Math.random() * (lagMillisMax - lagMillisMin));
            threadPool.schedule(new Runnable() {
                public void run () {
                    Runnable runnable;
                    synchronized (runnables) {
                        runnable = runnables.removeLast();
                    }
                    runnable.run();
                }
            }, lag, TimeUnit.MILLISECONDS);
        }
    }
}
