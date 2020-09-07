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
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

/**
 * <p>默认缓存实现</p>
 *
 * @package: com.yunmel.commons.cache
 * @class: TimerCache
 * @date: 2018/2/10
 * @author: xuyq(pazsolr @ gmail.com)
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
