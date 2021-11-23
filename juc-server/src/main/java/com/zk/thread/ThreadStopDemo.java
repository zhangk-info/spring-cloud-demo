package com.zk.thread;

/**
 * 正确停止线程
 * <p>
 * 想要正确停止线程 需要请求方（接收异常）、被请求方（正确位置catch异常、正确位置判断interrupted状态）、被请求方子方法（正确抛出异常）相互配合调用
 * stop/resume/suspend等已经弃用
 * stop会强制结束线程（可能导致一个任务无法正确的结束）
 * volatile和boolean的方式无法处理长时间阻塞的情况，即无法在阻塞（sleep）时正确的抛出InterruptedException异常导致阻塞等待
 * <p>
 * 在不可中断阻塞时如何正确停止？ 需要在被请求方（造成不可中断阻塞的方法）使用对应的Interruptibly（可中断的）方法，如下锁阻塞时调用中断方法，需要使锁支持可中断。
 * ReentrantLock reentrantLock = new ReentrantLock();
 * reentrantLock.lockInterruptibly();
 */
public class ThreadStopDemo {

    public static void main(String[] args) {
        ThreadStopDemo demo = new ThreadStopDemo();
        demo.interrupt();
    }

    private void interrupt() {
        Thread t = new Thread(() -> {
            try {
                // interrupted()会清理中断状态ClearInterrupted  isInterrupted()不会
                // 注意：interrupted()内部是currentThread().isInterrupted(true)当前线程，所以不管调起者（调起对象）是任何线程，只会得到当前调起位置所在线程的interrupted状态
                while (!Thread.interrupted()) {
                    /* 错误try catch，虽然捕获了异常，但是并没有终止While循环 go会一直输出
                     那是因为抛出InterruptedException异常之后线程中断状态会被清空
                     所以捕获InterruptedException异常应该在判断线程是否终止判断(!Thread.interrupted())的外面 */
//                try {
                    System.out.println("go");
                    throwInMethod();
//                } catch (InterruptedException e) {
                    /*          @throws  InterruptedException
                     *          if any thread has interrupted the current thread. The
                     *          <i>interrupted status</i> of the current thread is
                     *          cleared when this exception is thrown.*/
//                    e.printStackTrace();
//                }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        // 启动线程
        t.start();
        try {
            // 睡一秒
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 调起中断 因为线程会睡2秒，此时调起中断会抛出中断异常
        t.interrupt();
    }

    private void throwInMethod() throws InterruptedException {
        Thread.sleep(200);
    }
}
