package com.zk.designpattern.sigleton;

/**
 * 单例模式 - 懒汉式
 */
public class LazySigleton {

    private volatile static LazySigleton instance;

    private LazySigleton() {
    }

    public static LazySigleton getInstance() {
        if (instance == null) {
            // 线程安全
            synchronized (LazySigleton.class) {
                // double check 防止多个线程通过了为空判断进入了synchronized代码块
                if (instance == null) {
                    // 字节码层 该行代码执行内容
                    // 1. 分配空间
                    // 2. 初始化
                    // 3. 引用赋值
                    // java的即时编译 JIT:just in time 或者 CPU 可能会对这行代码进行指令重排序
                    // 造成2,3执行顺序的颠倒，多线程下造成返回的instance未初始化完成 volatile禁止指令重排序
                    instance = new LazySigleton();
                }
            }
        }
        return instance;
    }

}
