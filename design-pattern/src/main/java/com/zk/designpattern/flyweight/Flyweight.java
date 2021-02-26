package com.zk.designpattern.flyweight;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 享元模式
 * 对于可重复使用的大量数据进行共享，而不是多次创建
 */
public class Flyweight {

    public static void main(String[] args) {
        TreeNode treeNode1 = new TreeNode(0, 4, TreeFactory.getTree("红杉"));
        TreeNode treeNode2 = new TreeNode(2, 4, TreeFactory.getTree("松树"));
        TreeNode treeNode3 = new TreeNode(3, 5, TreeFactory.getTree("红杉"));
        TreeNode treeNode4 = new TreeNode(2, 5, TreeFactory.getTree("红杉"));
        //TreeFactory.getTree("红杉") 红杉不管在多少个坐标，只会创建一个Tree对象

        // 其他示例
        String s;
        Long l;
        Integer i;
    }
}


@Data
class Tree {

    private final String name;
}

@Data
@AllArgsConstructor
class TreeNode {

    private Integer x;
    private Integer y;
    private Tree tree;
}

class TreeFactory {

    private static Map<String, Tree> map = new ConcurrentHashMap<>();

    public static Tree getTree(String name) {
        if (map.containsKey(name)) {
            return map.get(name);
        } else {
            Tree tree = new Tree(name);
            map.put(name, tree);
            return tree;
        }
    }

}