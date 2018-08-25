package com.myjava.util.concurrent;

import java.util.NavigableMap;
import java.util.NavigableSet;

/**
 * 支持NavigableMap操作的ConcurrentMap
 * @param <K>
 * @param <V>
 */
public interface ConcurrentNavigableMap<K,V>
    extends ConcurrentMap<K,V>, NavigableMap<K,V> {

    ConcurrentNavigableMap<K, V> subMap(K fromKey, boolean fromInclusive,
                                        K toKey, boolean toInclusive);

    ConcurrentNavigableMap<K, V> headMap(K toKey, boolean inclusive);

    ConcurrentNavigableMap<K, V> tailMap(K fromKey, boolean inclusive);

    ConcurrentNavigableMap<K, V> subMap(K fromKey, K toKey);

    ConcurrentNavigableMap<K, V> headMap(K toKey);

    ConcurrentNavigableMap<K, V> tailMap(K fromKey);

    /**
     * 返回一个逆序的map。它是map的拷贝，所以对于map的变更也会反映到这里
     *
     * m.descendingMap().descendingMap()和m是等效的
     */
    ConcurrentNavigableMap<K, V> descendingMap();

    NavigableSet<K> navigableKeySet();

    /**
     * 弱一致性
     * 不支持add和addAll
     */
    NavigableSet<K> keySet();

    NavigableSet<K> descendingKeySet();
}
