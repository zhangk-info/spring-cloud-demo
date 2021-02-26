package com.zk.auth.captch.cache;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

/**
 * <p>默认缓存实现</p>
 *
 * @class: TimerCache
 * @date: 2018/2/10
 * @since 1.0
 */
public class DefaultCache<K, V> extends AbstractCacheMap<K, V> {

    public DefaultCache(long timeout) {
        this.cacheSize = 0;
        this.timeout = timeout;
        cacheMap = new HashMap<>();
    }

    // ---------------------------------------------------------------- prune

    /**
     * Prunes expired elements from the cache. Returns the number of removed objects.
     */
    @Override
    protected int pruneCache() {
        int count = 0;
        Iterator<CacheObject<K, V>> values = cacheMap.values().iterator();
        while (values.hasNext()) {
            CacheObject co = values.next();
            if (co.isExpired()) {
                values.remove();
                count++;
            }
        }
        return count;
    }

    // ---------------------------------------------------------------- auto prune

    protected Timer pruneTimer;

    /**
     * Schedules prune.
     */
    public void schedulePrune(long delay) {
        if (pruneTimer != null) {
            pruneTimer.cancel();
        }
        pruneTimer = new Timer();
        pruneTimer.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        prune();
                    }
                }, delay, delay
        );
    }

    /**
     * Cancels prune schedules.
     */
    public void cancelPruneSchedule() {
        if (pruneTimer != null) {
            pruneTimer.cancel();
            pruneTimer = null;
        }
    }

}
