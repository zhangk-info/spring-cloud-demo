package com.zk.designpattern.factory;

import java.text.NumberFormat;
import java.util.Calendar;

/**
 * 简单工厂模式
 */
public class SimpleFactory {

    public Product getInstance(String type) {
        if (type.equals("A")) {
            return new ProductA();
        } else if (type.equals("B")) {
            return new ProductB();
        } else {
            throw new RuntimeException("错误的生产对象");
        }
    }
}

class ProductA implements Product {

    @Override
    public void doSomeThing() {

    }
};

class ProductB implements Product {

    @Override
    public void doSomeThing() {

    }
};

class SimpleFactoryMethod {

    public static void main(String[] args) {
        SimpleFactory factory = new SimpleFactory();
        Product product = factory.getInstance("A");

        // 其他示例
        Calendar.getInstance();
        NumberFormat.getInstance();
    }
}