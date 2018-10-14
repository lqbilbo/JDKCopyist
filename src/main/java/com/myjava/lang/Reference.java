package com.myjava.lang;

import java.lang.ref.Cleaner;
import java.lang.ref.ReferenceQueue;

/**
 * @author luoqi04
 * @version $Id: Reference.java, v 0.1 2018/10/14 下午11:19 luoqi Exp $
 */
public abstract class Reference<T> {

    /*
     * Transitions:
     *                            clear
     *   [active/registered]     ------->   [inactive/registered]
     *          |                                 |
     *          |                                 | enqueue [2]
     *          | GC              enqueue [2]     |
     *          |                -----------------|
     *          |                                 |
     *          v                                 |
     *   [pending/registered]    ---              v
     *          |                   | ReferenceHandler
     *          | enqueue [2]       |--->   [inactive/enqueued]
     *          v                   |             |
     *   [pending/enqueued]      ---              |
     *          |                                 | poll/remove
     *          | poll/remove                     |
     *          |                                 |
     *          v            ReferenceHandler     v
     *   [pending/dequeued]      ------>    [inactive/dequeued]
     *
     *
     *                           clear/enqueue/GC [3]
     *   [active/unregistered]   ------
     *          |                      |
     *          | GC                   |
     *          |                      |--> [inactive/unregistered]
     *          v                      |
     *   [pending/unregistered]  ------
     *                           ReferenceHandler
     */
    private T referent;

    volatile ReferenceQueue<? super T> queue;

    volatile Reference next;

    private transient Reference<T> discovered;

    private static class ReferenceHandler extends Thread {
        private static void ensureClassInitialized(Class<?> clazz) {
            try {
                Class.forName(clazz.getName(), true, clazz.getClassLoader());
            } catch (ClassNotFoundException e) {
                throw(Error) new NoClassDefFoundError(e.getMessage()).initCause(e);
            }
        }

        static {
            ensureClassInitialized(Cleaner.class);
        }

        ReferenceHandler(ThreadGroup g, String name) {
            super(g, null, name, 0, false);
        }

        public void run() {
            while (true) {
                processPendingReferences();
            }
        }
    }

    private static native Reference<Object> getAndClearReferencePendingList();

    private static native boolean hasReferencePendingList();

    private static native void waitForReferencePendingList();

    private static final Object processPendingLock = new Object();

    private static boolean processPendingActive = false;

    private static void processPendingReferences() {
    }

    private static boolean waitForReferenceProcessing() throws InterruptedException {
        synchronized (processPendingLock) {
            if (processPendingActive || hasReferencePendingList()) {
                // Wait for progress, not necessarily completion.
                processPendingLock.wait();
                return true;
            } else {
                return false;
            }
        }
    }

    public T get() {
        return this.referent;
    }


    public void clear() {
        this.referent = null;
    }

    public boolean isEnqueued() {
        //do sth
        return false;
    }

    public boolean enqueue() {
        //do sth
        return false;
    }

    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    public Reference(T referent) {
        this(referent, null);
    }

    public Reference(T referent, ReferenceQueue<? super T> queue) {
        this.referent = referent;
//        this.queue = (queue == null) ? ReferenceQueue.NULL : queue;
    }

    public static void reachabilityFence(Object ref) {
        // Does nothing. This method is annotated with @ForceInline to eliminate
        // most of the overhead that using @DontInline would cause with the
        // HotSpot JVM, when this fence is used in a wide variety of situations.
        // HotSpot JVM retains the ref and does not GC it before a call to
        // this method, because the JIT-compilers do not have GC-only safepoints.
    }
}
