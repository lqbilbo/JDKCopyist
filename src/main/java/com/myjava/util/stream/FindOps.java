package com.myjava.util.stream;

import java.util.Optional;

/**
 * 在一个stream pipeline中搜索元素的短循环的工厂实例，当找到一个之后立刻终止。
 * 支持包括find-first和find-any
 */
final class FindOps {

    private FindOps() {}

    public static <T> TerminalOp<T, Optional<T>> makeRef(boolean mustFindFirst) {
//        return (TerminalOp<T, Optional<T>>)
//                (mustFindFirst ? FindSink)
        return null;
    }

//    private abstract static class FindSink<T, O>
}
