/**
 * Copyright (c) 2017 yunmel Co., Ltd. Ltd . All rights reserved.
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * <p>
 * The above copyright notice and this authentication notice shall be
 * included in all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.zk.auth.captch.cache;

import java.util.Map;

/**
 * <p>缓存接口</p>
 *
 * @package: com.yunmel.commons.cache
 * @class: ICache
 * @date: 2018/2/10
 * @author: xuyq(pazsolr @ gmail.com)
 * @since 1.0
 */
public interface ICache<K, V> {
    /**
     * Returns cache size or <code>0</code> if there is no size limit.
     */
    int limit();

    /**
     * Returns default timeout or <code>0</code> if it is not set.
     */
    long timeout();

    /**
     * Adds an object to the cache with default timeout.
     *
     * @see ICache#put(Object, Object, long)
     */
    void put(K key, V object);

    /**
     * Adds an object to the cache with specified timeout after which it becomes expired.
     * If cache is full, {@link #prune()} is invoked to make room for new object.
     */
    void put(K key, V object, long timeout);

    /**
     * Retrieves an object from the cache. Returns <code>null</code> if object
     * is not longer in cache or if it is expired.
     */
    V get(K key);

    /**
     * Prunes objects from cache and returns the number of removed objects.
     * Used strategy depends on cache implementation.
     */
    int prune();

    /**
     * Returns <code>true</code> if max cache capacity has been reached
     * only if cache is size limited.
     */
    boolean isFull();

    /**
     * Removes an object from the cache.
     */
    void remove(K key);

    /**
     * Clears current cache.
     */
    void clear();

    /**
     * Returns current cache size.
     */
    int size();

    /**
     * Returns <code>true</code> if cache is empty.
     */
    boolean isEmpty();

    /**
     * Creates a snapshot from current cache values. Returned values may not
     * longer be valid or they might be already expired! Cache is locked during
     * the snapshot creation.
     */
    Map<K, V> snapshot();

    boolean containsKey(K key);
}
