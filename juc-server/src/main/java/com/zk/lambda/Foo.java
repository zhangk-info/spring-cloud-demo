package com.zk.lambda;

/**
 *
 *  1.8新特效
 *
 * 1.lambda express () -> {}
 * 2.@FunctionalInterface 函数式接口：有且仅有一个接口方法
 * 3.default 默认方法实现
 * 4.static 静态方法实现
 *
 */
@FunctionalInterface
public interface Foo {

    int add(int x, int y);

    default int div(int x, int y) {
        return x / y;
    }

    default int mv(int x, int y) {
        return x * y;
    }

    static int div2(int x, int y) {
        return x / y;
    }

    static int mv2(int x, int y) {
        return x * y;
    }
}
