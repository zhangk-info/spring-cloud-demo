package com.zk.configuration.sync;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.security.concurrent.DelegatingSecurityContextExecutorService;

import java.util.concurrent.*;

/**
 * 重写AsyncConfigurerSupport
 * 为的是@async使用权限框架自带的包装线程池  保证权限信息的传递
 */
@Configuration
public class AsyncConfig extends AsyncConfigurerSupport {

    /**
     * 异步执行需要使用权限框架自带的包装线程池  保证权限信息的传递
     */
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5, 10, 30, TimeUnit.MINUTES, new LinkedBlockingQueue<>(200), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setName("@async线程池创建的线程");
                return t;
            }
        });
        return new DelegatingSecurityContextExecutorService(threadPoolExecutor);
    }

}