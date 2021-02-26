package com.zk.designpattern.prototype;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.*;

/**
 * 原型模式
 * 对象提供拷贝方法生成数据一致，但是不同对象的新对象
 */
public class Prototype {

    public static void main(String[] args) {
        ProductI productI = new ProductI("name", "code", new VariableObj("t"));
        try {
            ProductI productI2 = productI.clone();
            System.out.println(productI);
            System.out.println(productI2);
            System.out.println(productI == productI2);
            System.out.println(productI.getT() == productI2.getT());
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }
}

@Data
@AllArgsConstructor
class VariableObj implements Cloneable, Serializable {

    private String t;

    @Override
    public VariableObj clone() throws CloneNotSupportedException {
        return (VariableObj) super.clone();
    }
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class ProductI implements Cloneable, Serializable {

    private String name;
    private String code;
    private VariableObj t;// 强引用的可变类型

    /**
     * 注意 如果不对可变对象进行拷贝这里是浅拷贝 当属性值是可变类型的时候，复制后两个对象的该属性时同一个引用
     * 另外，对于List等数据对象实现clone()需要使用序列化或其他方式
     *
     * @return
     * @throws CloneNotSupportedException
     */
    @Override
    public ProductI clone() throws CloneNotSupportedException {
//        ProductI productI = ((ProductI) super.clone());
//        // 可变对象深拷贝 需要 对可变对象进行拷贝
//        productI.setT(productI.getT().clone());
//        return productI;

        // 方式2 序列化对象Serializable之后通过输入输出流进行深拷贝
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(byteArrayOutputStream)) {
            oos.writeObject(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        try (ObjectInputStream ois = new ObjectInputStream(byteArrayInputStream)) {
            ProductI productI = (ProductI) ois.readObject();
            return productI;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;

    }

}