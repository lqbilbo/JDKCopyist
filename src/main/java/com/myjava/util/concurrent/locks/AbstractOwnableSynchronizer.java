package com.myjava.util.concurrent.locks;

import java.io.Serializable;

public abstract class AbstractOwnableSynchronizer
    implements Serializable {

    private static final long serialVersionUID = 1518463135344032991L;

    protected AbstractOwnableSynchronizer() { }

    private transient Thread exclusiveOwnerThread;

    protected final Thread getExclusiveOwnerThread() {
        return exclusiveOwnerThread;
    }

    public final void setExclusiveOwnerThread(Thread thread) {
        this.exclusiveOwnerThread = thread;
    }
}
