package com.zk.designpattern.sigleton;

/**
 * 单例模式 - 饿汉式
 */
public class HungrySigleton {

    /**
     * 基于jvm的类加载机制保证实例唯一性
     * 类加载过程：
     * 1. 加载二进制文件到内存中，生成对应class数据结构
     * 2. 连接 a.验证 b.准备（给类的静态成员变量赋默认值） c.解析
     * 3. 初始化 （给类的静态成员变量赋初始值）
     *
     * 默认值： int:0 boolean:false 引用对象:null
     */
    private static HungrySigleton instance = new HungrySigleton();

    private HungrySigleton() {
    }

    private HungrySigleton getInstance() {
        return instance;
    }

}
