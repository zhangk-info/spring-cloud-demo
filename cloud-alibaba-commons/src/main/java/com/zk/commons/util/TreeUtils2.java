package com.zk.commons.util;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.zk.commons.entity.BaseTree;
import com.zk.commons.exception.ServiceException;

import java.lang.reflect.ParameterizedType;
import java.util.*;

public class TreeUtils2 {

    /**
     * 转换成List形式树结构 (如果是缓存的list，请务必深度copy一个)
     *
     * @param source
     * @return
     */
    public static <S, T extends BaseTree<T>> List<T> toBaseTreeList(List<S> source, Class<T> bean, Object rootParentValue) {
        if (CollectionUtil.isEmpty(source)) {
            return new ArrayList<>();
        }
        if (Objects.isNull(bean)) {
            try {
                S temp = source.get(0);
                // ParameterizedType参数化类型，即泛型
                ParameterizedType p = ((ParameterizedType) temp.getClass()
                        .getGenericSuperclass());
                //getActualTypeArguments获取参数化类型的数组，泛型可能有多个
                bean = (Class<T>) p.getActualTypeArguments()[0];
            } catch (Exception e) {
                throw new ServiceException("获取List泛型的Class失败");
            }
        }

        final Map<Object, T> nodes = new HashMap<>(source.size());

        // 深度copy一个，防止源list内部结构改变,甚至内存溢出
        List<T> list = JSONUtil.toList(JSONUtil.toJsonStr(source), bean);

        // 所有节点记录下来
        for (T node : list) {
            node.setLevel(-1);
            node.setHasChild(false);
            nodes.put(node.getId(), node);
        }

        final T root;
        try {
            root = bean.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        root.setLevel(0);
        root.setChildren(new ArrayList<>());
        nodes.put(rootParentValue, root);

        for (T node : list) {
            final T parent = nodes.get(node.getPid());
            if (parent == null) {
                root.getChildren().add(node);
                // throw new RuntimeException("子节点有父级id，却没有找到此父级的对象");
            } else {
                // 添加子节点
                if (null == parent.getChildren()) {
                    parent.setChildren(new ArrayList<>());
                }
                parent.getChildren().add(node);
            }
        }

        int max = 0;
        for (T node : list) {
            max = Math.max(resolveLevel(node, nodes), max);
        }

        return root.getChildren();
    }

    /**
     * 递归找level
     *
     * @param node
     * @param nodes
     * @param <T>
     * @return
     */
    private static <T extends BaseTree<T>> int resolveLevel(final T node, final Map<Object, T> nodes) {
        int level = 1;
        if (node != null) {
            level = node.getLevel();
            if (level == -2) {
                throw new RuntimeException("Node循环了, id=" + node.getId());
            }
            if (level == -1) {
                node.setLevel(-2);
                level = resolveLevel(nodes.get(node.getPid()), nodes) + 1;
                node.setLevel(level);
            } else {
                node.setHasChild(true);
            }
        }
        return level;
    }
}
