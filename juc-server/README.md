# JUV : Java util concurrent
* java并发编程工具类

## java多线程使用口诀 
1. 在高内聚 低耦合 的前提下  线程 操作 资源类
    * 高内聚：所有操作高度内聚在资源类中
    * 低耦合: 线程避免直接操作资源内容，使用资源类暴露的方法（操作）来操作资源内容
2. 交互过程: 判断、干活、通知
3. 多线程交互中，必须要防止多线程的虚假唤醒,也即,多线程判断中不许用if,只能用while
    * 交互: wait(); notify();
    * 虚假唤醒: 唤醒了，但是条件并没有再次校验.
    * 由于多线程唤醒之后,所有线程都会唤醒。比如刚好两个线程都在等待，两个线程都被唤醒，两个线程都生成/消费。但是条件并没有再次校验
4. 标志位
    * synchronized wait notify -> lock await signal
    


## java多线程实现的4中方式
* extends Thread / new Thread
* implements Runnable
* implements Callable<T> 通过FutureTask包装器来创建
* Executors工具类 线程池 TreadPoolExecutor类

#### 例子
* SaleTicket 竞争模式
* ThreadWaitNotifyDemo 生产消费模式


#### 关键字

线程状态：new runnable blocked waiting timed_waiting terminated 
实现线程的4中方式：Thread Runnable Callable(FutureTask) ThreadPoolExecutor.execute
锁：synchronized ReentrantLock ReadWriteLock
锁通信： wait notify condition await signal 
多线程辅助类： countDownLatch cyclicBarrier semaphore
线程池：ThreadPoolExecutor Runtime.getRuntime().availableProcessors() LinkedBlockQueue
线程池的7个属性： corePoolSize maximumPoolSize keepAliveTime BlockQueue threadFactory rejectedExecutionHandler
线程池拒绝策略（RejectedExecutionHandler）: Abort/CallerRuns/DiscardOld/Discard

线程状态：
实现线程的4中方式：
锁：
锁通信：
多线程辅助类： 
线程池：
线程池的7个属性： 
线程池拒绝策略（）: 


线程状态：new runnable blocked waiting timed_waiting terminated
实现线程的4中方式：Thread Runnable Callable(FutureTask) ThreadPoolExecutor.execute
锁：synchronized ReentrantLock ReadWriteLock
锁通信：wait notify condition await signal
多线程辅助类： countDownLatch cyclicBarrier semaphore
线程池：ThreadPoolExecutor Runtime.getRuntime().availableProcessors() LinkedBlockQueue
线程池的7个属性： corePoolSize maximumPoolSize keepAliveTime BlockQueue threadFactory RejectedExecutionHandler
线程池拒绝策略（RejectedExecutionHandler/Policy）: Abort/CallerRuns/DiscardOld/Discard


