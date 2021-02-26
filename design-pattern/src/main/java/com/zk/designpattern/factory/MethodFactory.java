package com.zk.designpattern.factory;


/**
 * 工厂方法模式
 * <p>
 * 将对象的创建延迟到子类
 * 子类通过工厂方法自己创建自己
 */
abstract class MethodFactory {

    abstract Product createProduct();

    Product getProduct() {
        return createProduct();
    }
}

/**
 * 生产不同的产品
 * 子类继承工厂，并实现其工厂方法
 */
class ProductC extends MethodFactory implements Product {

    @Override
    Product createProduct() {
        return new ProductC();
    }

    @Override
    public void doSomeThing() {

    }
}

/**
 * 生产不同的产品
 * 子类继承工厂，并实现其工厂方法
 */
class ProductD extends MethodFactory implements Product {

    @Override
    Product createProduct() {
        return new ProductD();
    }

    @Override
    public void doSomeThing() {

    }
}

class MethodFactoryMethod {

    public static void main(String[] args) {
        MethodFactory factory = new ProductC();
        Product product = factory.getProduct();

        // 其他示例
//        URLStreamHandlerFactory
    }
}
