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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.StampedLock;

/**
 * <p>TODO(这里用一句话描述这个类的作用)<p/>
 * <br><b>package:</b> com.yunmel.commons.cache
 * <br><b>class:</b> AbsCache
 * <br><b>date:</b> 2017/12/3
 *
 * @author: xuyq(pazsolr @ gmail.com)
 * @since 1.0
 */
public abstract class AbstractCacheMap<K, V> implements ICache<K, V> {

    class CacheObject<K2, V2> {
        CacheObject(K2 key, V2 object, long ttl) {
            this.key = key;
            this.cachedObject = object;
            this.ttl = ttl;
            this.lastAccess = System.currentTimeMillis();
        }

        final K2 key;
        final V2 cachedObject;
        long lastAccess;        // time of last access
        long accessCount;        // number of accesses
        long ttl;                // objects timeout (time-to-live), 0 = no timeout

        boolean isExpired() {
            if (ttl == 0) {
                return false;
            }
            return lastAccess + ttl < System.currentTimeMillis();
        }

        V2 getObject() {
            lastAccess = System.currentTimeMillis();
            accessCount++;
            return cachedObject;
        }
    }

    protected Map<K, CacheObject<K, V>> cacheMap;
    private final StampedLock lock = new StampedLock();

    // ---------------------------------------------------------------- properties

    protected int cacheSize;      // max cache size, 0 = no limit

    /**
     * {@inheritDoc}
     */
    public int limit() {
        return cacheSize;
    }

    protected long timeout;     // default timeout, 0 = no timeout

    /**
     * Returns default cache timeout or <code>0</code> if it is not set.
     * Timeout can be set individually for each object.
     */
    public long timeout() {
        return timeout;
    }

    /**
     * Identifies if objects has custom timeouts.
     * Should be used to determine if prune for existing objects is needed.
     */
    protected boolean existCustomTimeout;

    /**
     * Returns <code>true</code> if prune of expired objects should be invoked.
     * For internal use.
     */
    protected boolean isPruneExpiredActive() {
        return (timeout != 0) || existCustomTimeout;
    }


    // ---------------------------------------------------------------- put


    /**
     * {@inheritDoc}
     */
    public void put(K key, V object) {
        put(key, object, timeout);
    }


    /**
     * {@inheritDoc}
     */
    public void put(K key, V object, long timeout) {
        final long stamp = lock.writeLock();

        try {
            CacheObject<K, V> co = new CacheObject<>(key, object, timeout);
            if (timeout != 0) {
                existCustomTimeout = true;
            }
            if (isReallyFull(key)) {
                pruneCache();
            }
            cacheMap.put(key, co);
        } finally {
            lock.unlockWrite(stamp);
        }
    }


    // ---------------------------------------------------------------- get

    protected int hitCount;
    protected int missCount;

    /**
     * Returns hit count.
     */
    public int getHitCount() {
        return hitCount;
    }

    /**
     * Returns miss count.
     */
    public int getMissCount() {
        return missCount;
    }

    /**
     * {@inheritDoc}
     */
    public V get(K key) {
        long stamp = lock.readLock();

        try {
            CacheObject<K, V> co = cacheMap.get(key);
            if (co == null) {
                missCount++;
                return null;
            }
            if (co.isExpired()) {
                final long newStamp = lock.tryConvertToWriteLock(stamp);

                if (newStamp != 0L) {
                    stamp = newStamp;
                    // lock is upgraded to write lock
                } else {
                    // manually upgrade lock to write lock
                    lock.unlockRead(stamp);
                    stamp = lock.writeLock();
                }

                CacheObject<K, V> removedCo = cacheMap.remove(key);
                if (removedCo != null) {
                    onRemove(removedCo.key, removedCo.cachedObject);
                }

                missCount++;
                return null;
            }

            hitCount++;
            return co.getObject();
        } finally {
            lock.unlock(stamp);
        }
    }

    // ---------------------------------------------------------------- prune

    /**
     * Prune implementation.
     */
    protected abstract int pruneCache();

    /**
     * {@inheritDoc}
     */
    public final int prune() {
        final long stamp = lock.writeLock();
        try {
            return pruneCache();
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    // ---------------------------------------------------------------- common

    /**
     * {@inheritDoc}
     */
    public boolean isFull() {
        if (cacheSize == 0) {
            return false;
        }
        return cacheMap.size() >= cacheSize;
    }

    protected boolean isReallyFull(K key) {
        if (cacheSize == 0) {
            return false;
        }
        if (cacheMap.size() >= cacheSize) {
            return !cacheMap.containsKey(key);
        } else {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void remove(K key) {
        final long stamp = lock.writeLock();
        try {
            CacheObject<K, V> co = cacheMap.remove(key);
            if (co != null) {
                onRemove(co.key, co.cachedObject);
            }
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void clear() {
        final long stamp = lock.writeLock();
        try {
            cacheMap.clear();
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    /**
     * {@inheritDoc}
     */
    public int size() {
        return cacheMap.size();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<K, V> snapshot() {
        final long stamp = lock.writeLock();
        try {
            Map<K, V> map = new HashMap<>(cacheMap.size());
            cacheMap.forEach((key, cacheValue) -> map.put(key, cacheValue.getObject()));
            return map;
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    // ---------------------------------------------------------------- protected

    /**
     * Callback called on item removal. The cache is still locked.
     */
    protected void onRemove(K key, V cachedObject) {
    }


    @Override
    public boolean containsKey(K key) {
        return cacheMap.containsKey(key);
    }
}
