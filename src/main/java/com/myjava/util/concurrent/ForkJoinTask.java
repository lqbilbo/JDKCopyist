package com.myjava.util.concurrent;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.concurrent.*;

/**
 * 在{@link java.util.concurrent.ForkJoinPool}中运行的基类
 * ForkJoinTask是一种具备线程特性的实体对象，它比一般的线程要轻量的多。少量的
 * 由于使用限制方面的考量，大量的任务和其子任务都寄宿在了ForkJoinPool的真实线程中。
 * 当显式地提交到ForkJoinPool时，main开始执行；如果还没有包含在ForkJoin计算中，
 * 就会通过fork，invoke或者相关的方法使得它出现在ForkJoinPool#commonPool中。
 * 一旦开始之后，它就会引发其他的子任务的执行。就像它的类名一样，许多程序都使用
 * ForkJoinTask的join和fork方法，或者派生如invokeAll。然而，这个类还提供了其他
 * 进阶的用法，比如支持fork/join进程的扩展机制
 *
 * 最典型的用法就是并行遍历函数中用于调用和返回的例子。
 *
 * @param <V>
 */
public abstract class ForkJoinTask<V> implements Future<V>, Serializable {
    private static final long serialVersionUID = -701814437954021343L;

    volatile int status;

    private static final int DONE = 1 << 31;
    private static final int ABNORMAL = 1 << 18;
    private static final int THROWN = 1 << 17;
    private static final int SIGNAL = 1 << 16;
    private static final int SMASK = 0xffff;

    static boolean isExceptionalStatus(int s) {
        return (s & THROWN) != 0;
    }

    // VarHandle mechanics
    private static final VarHandle STATUS;
    static {
        try {
            MethodHandles.Lookup l = MethodHandles.lookup();
            STATUS = l.findVarHandle(ForkJoinTask.class, "status", int.class);
        } catch (ReflectiveOperationException e) {
            throw new Error(e);
        }
    }

    private int setDone() {
        return 0;
    }

    private int abnormalCompletion(int completion) {
        return 0;
    }

    final int doExec() {
        return 0;
    }

    final void internalWait(long timeout) {

    }

}
