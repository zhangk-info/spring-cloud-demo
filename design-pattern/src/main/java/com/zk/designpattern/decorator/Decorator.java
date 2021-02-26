package com.zk.designpattern.decorator;

import javax.servlet.http.HttpServletRequestWrapper;

interface Component {

    void operation();
}

class DecoratorTest {

    public static void main(String[] args) {
        Component component = new ConCreteComponent();

        Component decorator1 = new ConCreteDecorator1(component);
//        decorator2.operation();

        Component decorator2 = new ConCreteDecorator2(decorator1);
        decorator2.operation();

        // 其他示例
        HttpServletRequestWrapper wrapper;
    }
}

/**
 * 装饰器模式
 * 在不改变原有对象的基础上，将功能附加到对象上
 * 非常经典的开闭原则设计模式
 */
public abstract class Decorator implements Component {

    Component component;

    public Decorator(Component component) {
        this.component = component;
    }
}

class ConCreteComponent implements Component {

    @Override
    public void operation() {
        System.out.println("拍照");
    }
}

class ConCreteDecorator1 extends Decorator {

    public ConCreteDecorator1(Component component) {
        super(component);
    }

    @Override
    public void operation() {
        component.operation();
        System.out.println("美颜");
    }
}

class ConCreteDecorator2 extends Decorator {

    public ConCreteDecorator2(Component component) {
        super(component);
    }

    @Override
    public void operation() {
        component.operation();
        System.out.println("滤镜");
    }
}