package com.zk.designpattern.strategy;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * 策略者模式
 * 定义了算法族，分别封装起来，让他们之间可以互相替换，此模式的变化独立于算法的使用者。
 */
public class Strategy {

    public static void main(String[] args) {
        // 示例
        ArrayList<Person> personList = new ArrayList<Person>();
        personList.add(new Person(1, 3));
        personList.add(new Person(2, 2));
        personList.add(new Person(3, 1));

        // 排序算法族
        personList.sort(new SortByAge());
        System.out.println("args = [" + personList + "]");

        // 其他示例，oauth2定义登录方式（定义了算法族，分别封装起来），不同方式使用不同的登录算法
    }

}

class SortByAge implements Comparator<Person> {

    @Override
    public int compare(Person o1, Person o2) {
        if (o1.age > o2.age) {
            return 1;
        } else {
            return -1;
        }
    }
}

class SortByHeight implements Comparator<Person> {

    @Override
    public int compare(Person o1, Person o2) {
        if (o1.height > o2.height) {
            return 1;
        } else {
            return -1;
        }
    }
}

@Data
@AllArgsConstructor
class Person {

    int age;// 年龄
    int height;// 身高
}

