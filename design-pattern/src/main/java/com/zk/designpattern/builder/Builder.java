package com.zk.designpattern.builder;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;

/**
 * 建造者模式
 * <p>
 * 用于对复杂对象，使用不同的建造者进行初始化
 */
interface ProductBuilder {

    void builderName(String name);

    void builderCode(String code);

    ProductG build();
}

public class Builder {

    public static void main(String[] args) {
//        ProductBuilder productBuilder = new ConcreteProductBuilder();
        ProductBuilder productBuilder = new ConcreteProductBuilder2();
        Director director = new Director(productBuilder);
        ProductG productG = director.makeProduct("", "");


        ProductH.ConcreteProductHBuilder builder = new ProductH.ConcreteProductHBuilder();
        // 链式编程
        ProductH productH = builder.builderName("")
                .builderCode("")
                .build();

        // 其他示例
        BeanDefinitionBuilder beanDefinitionBuilder;
    }
}

/**
 * 创建指导者
 */
class Director {

    private ProductBuilder productBuilder;

    Director(ProductBuilder productBuilder) {
        this.productBuilder = productBuilder;
    }

    ProductG makeProduct(String name, String code) {
        productBuilder.builderName(name);
        productBuilder.builderCode(code);

        return productBuilder.build();
    }
}

/**
 * 建造者
 */
class ConcreteProductBuilder implements ProductBuilder {

    private String name;
    private String code;

    @Override
    public void builderName(String name) {
        this.name = name;
    }

    @Override
    public void builderCode(String code) {
        this.code = code;
    }

    @Override
    public ProductG build() {
        return new ProductG(name, code);
    }
}

/**
 * 建造者2
 */
class ConcreteProductBuilder2 implements ProductBuilder {

    private String name;
    private String code;

    @Override
    public void builderName(String name) {
        this.name = name;
    }

    @Override
    public void builderCode(String code) {
        this.code = code;
    }

    @Override
    public ProductG build() {
        return new ProductG(name, code);
    }
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class ProductG {

    private String name;
    private String code;
}

/**
 * 建造者模式 - 变种：静态内部类 - 内部建造者
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
class ProductH {

    private String name;
    private String code;


    public static class ConcreteProductHBuilder {

        private String name;
        private String code;

        public ConcreteProductHBuilder builderName(String name) {
            this.name = name;
            return this;
        }

        public ConcreteProductHBuilder builderCode(String code) {
            this.code = code;
            return this;
        }

        public ProductH build() {
            return new ProductH(name, code);
        }
    }

}