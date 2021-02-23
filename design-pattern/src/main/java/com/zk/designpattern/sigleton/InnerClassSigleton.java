package com.zk.designpattern.sigleton;

/**
 * 单例模式 - 静态内部类式
 */
public class InnerClassSigleton {

    private InnerClassSigleton() {

    }

    private static InnerClassSigleton getInstance() {
        /**
         * 其实也是基于jvm类加载模式保证单例
         * 区别是 这里是懒加载方式
         * 只有在调用getInstance()的时候才会导致InnerClassHolder的初始化
         */
        return InnerClassHolder.instance;
    }

    private static class InnerClassHolder {

        private static InnerClassSigleton instance = new InnerClassSigleton();
    }
}
