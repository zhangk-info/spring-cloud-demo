package com.zk.designpattern.factory;


import java.sql.Connection;

/**
 * 抽象工厂模式
 * 一系列工厂方法组成的工厂模式
 * 提供一系列工厂方法 不同系列的生产工厂自己去继承并生产自己系列的产品
 * <p>
 * 将对象的创建延迟到子类
 * 子类自己创建自己
 */
interface AbstractFactory {

    ProductE createE();

    ProductF createF();
}


class ProductE1 implements ProductE {

    @Override
    public void doSomeThing() {

    }
}

class ProductE2 implements ProductE {

    @Override
    public void doSomeThing() {

    }
}


class ProductF1 implements ProductF {

    @Override
    public void doSomeThing() {

    }
}

class ProductF2 implements ProductF {

    @Override
    public void doSomeThing() {

    }
}

/**
 * 生产不同的产品
 * 子类工厂集成抽象工厂，并实现其工厂方法
 */
class ConcreteFactory1 implements AbstractFactory {

    @Override
    public ProductE createE() {
        return new ProductE1();
    }

    @Override
    public ProductF createF() {
        return new ProductF1();
    }
}

/**
 * 生产不同的产品
 * 子类工厂集成抽象工厂，并实现其工厂方法
 */
class ConcreteFactory2 implements AbstractFactory {

    @Override
    public ProductE createE() {
        return new ProductE2();
    }

    @Override
    public ProductF createF() {
        return new ProductF2();
    }
}

class AbstractFactoryMethod {

    public static void main(String[] args) {
        // 用1系列的工厂生产1系列的产品
        AbstractFactory factory = new ConcreteFactory1();
        ProductE productE = factory.createE();
        ProductF productF = factory.createF();

        // 其他示例
        Connection connection;
    }
}
