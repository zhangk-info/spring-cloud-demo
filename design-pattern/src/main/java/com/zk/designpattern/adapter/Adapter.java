package com.zk.designpattern.adapter;

/**
 * 适配器目标
 */
interface Target {

    int output5V();
}


/**
 * 适配器模式
 * 将一个类的接口转换成客户希望的另一个接口
 * 使得原本由于接口不兼容而不能一起工作的那些类可以一起工作
 */
public class Adapter {

    public static void main(String[] args) {

        Adapter1 adapter = new Adapter1(new Adaptee());
        adapter.output5V();
    }

}

/**
 * 适配器
 * 1. ObjectAdapter 对象适配器模式 - 组合
 */
class Adapter1 implements Target {

    private Adaptee adaptee;

    public Adapter1(Adaptee adaptee) {
        this.adaptee = adaptee;
    }

    @Override
    public int output5V() {
        int i = adaptee.output220V();
        //将220V 处理成 5V
        i = 5;
        return i;
    }
}

/**
 * 适配器
 * 2. ClassAdapter 类的适配器模式 - 继承
 * （可以看到父类的220V方法）违背了（迪米特法则）最少知道原则
 */
class Adapter2 extends Adaptee implements Target {

    @Override
    public int output5V() {
        int i = output220V();
        //将220V 处理成 5V
        i = 5;
        return i;
    }
}

/**
 * 额定电压220V
 */
class Adaptee {

    public int output220V() {
        return 220;
    }
}

