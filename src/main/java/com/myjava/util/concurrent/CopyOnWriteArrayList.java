package com.myjava.util.concurrent;

import java.util.*;
import java.util.function.UnaryOperator;

/**
 * ArrayList的线程安全变体
 * 在实现遍历的操作时有了极大的改善，而且当你不希望同步遍历时也是很有用的
 * 这种镜像模式的迭代器方法使用了一个到迭代器创建的数组状态的引用
 * 数组在迭代器的生命周期中从不改变，所以会保证不抛出ConcurrentModificationException
 * 迭代器一旦创建就不会影响列表的增删改。而且迭代器上的remove，set和add是不被允许的
 * 这会抛出UnsupportedOperationException异常
 */
public class CopyOnWriteArrayList<E>
implements List<E>, RandomAccess, Cloneable, java.io.Serializable {
    private static final long serialVersionUID = -426732548720061309L;

    /** Doug大叔更偏爱这种内置锁而不像别人用ReentrantLock */
    final transient Object lock = new Object();

    private transient volatile Object[] array;

    final Object[] getArray() {
        return array;
    }

    final void setArray(Object[] a) {
        array = a;
    }
    public CopyOnWriteArrayList() {
        setArray(new Object[0]);
    }

    public CopyOnWriteArrayList(Collection<? extends E> c) {
        //doSomething
    }

    public CopyOnWriteArrayList(E[] toCopyIn) {
        setArray(Arrays.copyOf(toCopyIn, toCopyIn.length, Object[].class));
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean contains(Object o) {
        return false;
    }

    @Override
    public Iterator<E> iterator() {
        return null;
    }

    @Override
    public Object[] toArray() {
        return new Object[0];
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return null;
    }

    @Override
    public boolean add(E e) {
        return false;
    }

    @Override
    public boolean remove(Object o) {
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        return false;
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return false;
    }

    @Override
    public void clear() {

    }

    @Override
    public E get(int index) {
        return null;
    }

    @Override
    public E set(int index, E element) {
        return null;
    }

    @Override
    public void add(int index, E element) {

    }

    @Override
    public E remove(int index) {
        return null;
    }

    @Override
    public int indexOf(Object o) {
        return 0;
    }

    @Override
    public int lastIndexOf(Object o) {
        return 0;
    }

    @Override
    public ListIterator<E> listIterator() {
        return null;
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        return null;
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        return null;
    }

    @SuppressWarnings("unchecked")
    static <E> E elementAt(Object[] a, int index) {
        return (E) a[index];
    }

    public void replaceAll(UnaryOperator<E> operator) {

    }

    void replaceAll(UnaryOperator<E> operator, int i, int end) {
        assert Thread.holdsLock(lock);
        final Object[] es = getArray().clone();
        for (; i < end; i++)
            es[i] = operator.apply(elementAt(es, i));
        setArray(es);
    }

    @Override
    public void sort(Comparator<? super E> c) {
    }

    @SuppressWarnings("unchecked")
    void sort(Comparator<? super E> c, int i, int end) {
        final Object[] es = getArray().clone();
        Arrays.sort(es, i, end, (Comparator<Object>)c);
        setArray(es);
    }

    /**
     * 当spliterator被构建时Spliterator提供了列表的状态的镜像
     * 当操作时没有进行同步
     * @return
     */
    @Override
    public Spliterator<E> spliterator() {
        return Spliterators.spliterator(
                getArray(), Spliterator.IMMUTABLE | Spliterator.ORDERED);
    }

    public int indexOf(E e, int index) { return 0; }

    public int lastIndexOf(E e, int index) { return 0; }

    public boolean addIfAbsent(E e) {
        return false;
    }

    public int addAllAbsent(Collection<? extends E> c) {
        //doSomething
        return 0;
    }

    static String outOfBounds(int index, int size) {
        return "Index: " + index + ", Size: " + size;
    }

    void removeRange(int fromIndex, int toIndex) {
        //doSomething
    }



}
