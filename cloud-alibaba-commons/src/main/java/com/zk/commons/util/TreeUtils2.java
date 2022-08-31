package com.zk.commons.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.json.JSONUtil;
import com.zk.commons.entity.TreeNode;
import com.zk.commons.exception.ServiceException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author lengleng、zhank
 * @date 2017年11月9日23:34:11
 * @update 2022/08/18
 */
@UtilityClass
@Slf4j
public class TreeUtils2 {

    /**
     * 两层循环实现建树
     *
     * @param treeNodeList    传入的树节点列表
     * @param rootParentValue 最上层父级id的值
     * @return
     */
    public <T extends TreeNode<T>> List<T> buildWithTwoLayer(List<T> treeNodeList, Object rootParentValue) {
        AtomicInteger atomicInteger = new AtomicInteger(1000);
        List<T> trees = new ArrayList<>();
        for (T treeNode : treeNodeList) {
            if (rootParentValue.equals(treeNode.getParentId())) {
                //设置树状结构id
                treeNode.setTreeId(atomicInteger.getAndIncrement());
                treeNode.setParentTreeId(atomicInteger.getAndIncrement());
                trees.add(treeNode);
            }
            for (T it : treeNodeList) {
                if (treeNode.getId().equals(it.getParentId())) {
                    if (treeNode.getChildren() == null) {
                        treeNode.setChildren(new ArrayList<>());
                    }
                    it.setTreeId(atomicInteger.getAndIncrement());
                    //设置父级树状结构id
                    it.setParentTreeId(treeNode.getTreeId());
                    treeNode.addChild(it);
                }
            }
        }
        return trees;
    }
//
//    /**
//     * 使用递归方法建树
//     *
//     * @param treeNodeList
//     */
//    public <T extends TreeNode> List<T> buildByRecursive(List<T> treeNodeList) {
//        AtomicInteger atomicInteger = new AtomicInteger(1000);
//        List<T> trees = new ArrayList<T>();
//        //获取顶部的节点
//        List<T> headNodes = new ArrayList<>();
//        for (T treeNode : treeNodeList) {
//            if (treeNodeList.stream().noneMatch(node -> node.getId().equals(treeNode.getParentId()))) {
//                headNodes.add(treeNode);
//            }
//        }
//        List<String> headIdList = headNodes.stream().map(node -> node.getId()).collect(Collectors.toList());
//        List<T> exHeadList = treeNodeList.stream().filter(node -> !headIdList.contains(node.getId())).collect(Collectors.toList());
//        for (T headNode : headNodes) {
//            //设置树状结构id
//            headNode.setTreeId(atomicInteger.getAndIncrement());
//            List<T> childList = exHeadList.stream().filter(node -> headNode.getId().equals(node.getParentId())).collect(Collectors.toList());
//            if (CollectionUtil.isNotEmpty(childList)) {
//                trees.add(findChildren(headNode, exHeadList));
//            } else {
//                trees.add(headNode);
//            }
//        }
//        return trees;
//    }
//
//    /**
//     * 使用递归方法建树
//     *
//     * @param treeNodeList
//     * @param rootParentValue 最上层父级id的值
//     * @return
//     */
//    public <T extends TreeNode> List<T> buildByRecursive(List<T> treeNodeList, Object rootParentValue) {
//        List<T> trees = new ArrayList<T>();
//        List<T> matchList = treeNodeList.stream().filter(node -> rootParentValue.equals(node.getParentId())).collect(Collectors.toList());
//        List<T> notMatchList = treeNodeList.stream().filter(node -> !rootParentValue.equals(node.getParentId())).collect(Collectors.toList());
//        for (T treeNode : matchList) {
//            List<T> childList = notMatchList.stream().filter(node -> treeNode.getId().equals(node.getParentId())).collect(Collectors.toList());
//            if (CollectionUtil.isNotEmpty(childList)) {
//                trees.add(findChildren(treeNode, notMatchList));
//            } else {
//                trees.add(treeNode);
//            }
//        }
//        //设置treeId
//        setTreeId(trees);
//        return trees;
//    }
//
//    /**
//     * 递归查找子节点
//     *
//     * @param treeNodeList
//     * @return
//     */
//    private <T extends TreeNode> T findChildren(T treeNode, List<T> treeNodeList) {
//        List<T> childList = treeNodeList.stream().filter(node -> treeNode.getId().equals(node.getParentId())).collect(Collectors.toList());
//        if (treeNode.getChildren() == null) {
//            treeNode.setChildren(new ArrayList<>());
//        }
//        List<T> list = new ArrayList<>();
//        for (T it : childList) {
//            T children = findChildren(it, treeNodeList);
//            list.add(children);
//        }
//        treeNode.setChildren(list);
//        return treeNode;
//    }
//

    /**
     * 设置treeId
     *
     * @param treeNodeList
     * @param <T>
     */
    private <T extends TreeNode> void setTreeIdAndParentIds(List<T> treeNodeList, String rootParentValue) {
        AtomicInteger atomicInteger = new AtomicInteger(1000);
        ArrayDeque<T> nodeQueue = new ArrayDeque<>();
        nodeQueue.addAll(treeNodeList);
        while (!nodeQueue.isEmpty()) {
            T t = nodeQueue.pop();
            if (Objects.isNull(t.getParentIds())) {
                t.setParentIds(t.getParentId());
            }
            // 设置treeId
            t.setTreeId(atomicInteger.getAndIncrement());
            List<T> children = t.getChildren();
            if (CollectionUtil.isNotEmpty(children)) {
                for (T child : children) {
                    // 设置ParentTreeId
                    child.setParentTreeId(t.getTreeId());
                    // 如果上级不是顶层
                    if (Objects.nonNull(t.getId())) {
                        child.setParentIds(Objects.isNull(t.getParentIds()) ? rootParentValue : t.getParentIds() + "," + t.getId());
                    } else {
                        child.setParentIds(rootParentValue);
                    }
                    nodeQueue.addLast(child);
                }
            }
        }
    }

    /**
     * 转换成List形式树结构 (如果是缓存的list，请务必深度copy一个)
     *
     * @param source
     * @return
     */
    public static <T extends TreeNode<T>> List<T> buildByRecursive(List<T> source) {
        // 没有找到父的会直接放到顶层
        return buildByRecursive(source, null, "-1");
    }

    /**
     * 转换成List形式树结构 (如果是缓存的list，请务必深度copy一个)
     *
     * @param source
     * @param rootParentValue
     * @return
     */
    public static <T extends TreeNode<T>> List<T> buildByRecursive(List<T> source, String rootParentValue) {
        return buildByRecursive(source, null, rootParentValue);
    }

    /**
     * 转换成List形式树结构
     *
     * @param sourceList      源数据列表，经过该方法后会变形, (如果要保留原数据，请务必深度copy一个)
     * @param bean            类信息，可不传入，会根据原数据泛型自行判断
     * @param rootParentValue 根节点id，必须
     * @return
     */
    public static <T extends TreeNode<T>> List<T> buildByRecursive(List<T> sourceList, Class<T> bean, String rootParentValue) {
        if (Objects.isNull(rootParentValue)) {
            throw new ServiceException("rootParentValue必须");
        }
        if (CollectionUtil.isEmpty(sourceList)) {
            return new ArrayList<>();
        }
        if (Objects.isNull(bean)) {
            try {
                T temp = sourceList.get(0);
                // ParameterizedType参数化类型，即泛型
                ParameterizedType p = ((ParameterizedType) temp.getClass()
                        .getGenericSuperclass());
                //getActualTypeArguments获取参数化类型的数组，泛型可能有多个
                bean = (Class<T>) p.getActualTypeArguments()[0];
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new ServiceException("获取List泛型的Class失败");
            }
        }

        final Map<Object, T> nodeMap = new HashMap<>(sourceList.size());

        // 深度copy一个，防止源list内部结构改变 暂时不需要
        List<T> list = sourceList;//JSONUtil.toList(JSONUtil.toJsonStr(sourceList), bean);

        // 所有节点记录下来
        for (T node : list) {
            node.setLevel(-1);
            node.setHasChild(false);
            nodeMap.put(node.getId(), node);
        }

        final T root;
        try {
            root = bean.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        int maxLevel = 0;
        root.setLevel(maxLevel);
        root.setChildren(new ArrayList<>());
        nodeMap.put(rootParentValue, root);

        for (T node : list) {
            // 得到当前node的上级
            final T parent = nodeMap.get(node.getParentId());
            // 如果当前node的上级没找到，直接放入顶层
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
//        // 设置level 暂时不需要
//        for (T node : list) {
//            maxLevel = Math.max(resolveLevel(node, nodeMap), maxLevel);
//        }
        setTreeIdAndParentIds(root.getChildren(), rootParentValue);

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
    private static <T extends TreeNode<T>> int resolveLevel(final T node, final Map<Object, T> nodes) {
        int level = 1;
        if (node != null) {
            level = node.getLevel();
            if (level == -2) {
                throw new RuntimeException("Node循环了, id=" + node.getId());
            }
            if (level == -1) {
                node.setLevel(-2);
                level = resolveLevel(nodes.get(node.getParentId()), nodes) + 1;
                node.setLevel(level);
            } else {
                node.setHasChild(true);
            }
        }
        return level;
    }

    /**
     * 通过tree和命中的节点id，过滤并生成树，默认包含祖辈和孙辈不包含同辈
     *
     * @see TreeUtils2#buildAndFileterByTree(List, String, List, Boolean, Boolean, Boolean)
     */
    public static <T extends TreeNode<T>> List<T> buildAndFileterByTree(List<T> sourceTree, String rootParentValue, List<String> hitIds) {
        // 设置树的parentIds
        setTreeIdAndParentIds(sourceTree, rootParentValue);
        // 展开树后继续
        return buildAndFileter(treeToList(sourceTree), rootParentValue, hitIds, true, true, false, false);
    }

    /**
     * 通过list和命中的节点id，过滤并生成树，默认包含祖辈和孙辈不包含同辈
     *
     * @see TreeUtils2#buildAndFileterByList(List, String, List, Boolean, Boolean, Boolean, Boolean)
     */
    public static <T extends TreeNode<T>> List<T> buildAndFileterByList(List<T> sourceList, String rootParentValue, List<String> hitIds, Boolean returnList) {
        List<T> sourceTree = buildByRecursive(sourceList, null, rootParentValue);
        // 展开树后继续
        return buildAndFileter(treeToList(sourceTree), rootParentValue, hitIds, true, true, false, returnList);
    }

    /**
     * 构造树然后展开
     * 展开后的列表可用于导出
     * 展开后如下：["1i","1j","1j1k","1j2k","2j","2j1k","2j2k"] 可直接导出用
     *
     * @see TreeUtils2#buildAndExpand(List, String)
     */
    public static <T extends TreeNode<T>> List<T> buildAndExpand(List<T> sourceList, String rootParentValue) {
        List<T> sourceTree = buildByRecursive(sourceList, null, rootParentValue);
        // 展开树后继续
        return treeToList(sourceTree);
    }

    /**
     * 通过tree和命中的节点id，过滤并生成树
     * <p>
     * 实现思路:
     * 1. 设置树的parentIds
     * 2. 展开tree后进行buildAndFileter
     *
     * @see TreeUtils2#buildAndFileter(List, String, List, Boolean, Boolean, Boolean, Boolean)
     */
    public static <T extends TreeNode<T>> List<T> buildAndFileterByTree(List<T> sourceTree, String rootParentValue, List<String> hitIds, Boolean containAncestors, Boolean containDescendants, Boolean containBrothers) {
        // 设置树的parentIds
        setTreeIdAndParentIds(sourceTree, rootParentValue);
        // 展开树后继续
        return buildAndFileter(treeToList(sourceTree), rootParentValue, hitIds, containAncestors, containDescendants, containBrothers, false);
    }

    /**
     * 通过list和命中的节点id，过滤并生成树
     * <p>
     * 实现思路:
     * 1. 将list转成tree,此时会生成parentIds
     * 2. 展开tree后进行buildAndFileter
     *
     * @see TreeUtils2#buildAndFileter(List, String, List, Boolean, Boolean, Boolean, Boolean)
     */
    public static <T extends TreeNode<T>> List<T> buildAndFileterByList(List<T> sourceList, String rootParentValue, List<String> hitIds, Boolean containAncestors, Boolean containDescendants, Boolean containBrothers, Boolean returnList) {
        List<T> sourceTree = buildByRecursive(sourceList, null, rootParentValue);
        // 展开树后继续
        return buildAndFileter(treeToList(sourceTree), rootParentValue, hitIds, containAncestors, containDescendants, containBrothers, returnList);
    }

    /**
     * 通过list和命中的节点id，过滤并生成树
     * <p>
     * 实现思路：传入的每个节点必须包含parentIds
     * 1. 通过命中节点的parentIds.split(",")得到其父级别
     * 2. 通过命中节点的parentIds + ","被包含在某个节点的getParentIds()中确定某个节点是其子孙，得到其子集
     * 3. 通过命中节点的parentIds等于某个节点的parentIds,得到其同级
     *
     * @param sourceList         每个节点必须包含parentIds
     * @param hitIds             过滤条件命中的节点ids
     * @param containAncestors   包含祖辈（其中包括父辈）
     * @param containDescendants 包含孙辈（其中包括儿子）
     * @param containBrothers    包含同辈
     * @param returnList         是否直接返回列表， 不组装成树形
     */
    public static <T extends TreeNode<T>> List<T> buildAndFileter(List<T> sourceList, String rootParentValue, List<String> hitIds, Boolean containAncestors, Boolean containDescendants, Boolean containBrothers, Boolean returnList) {
        // 命中节点id转map，增加查询效率
        Map<String, String> hitIdMap = hitIds.stream().collect(Collectors.toMap(t -> t, t -> t, (a, b) -> a));
        // 得到命中节点
        List<T> hitNodes = sourceList.stream().filter(t -> hitIdMap.containsKey(t.getId())).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(hitNodes)) {
            return null;
        }

        // 所有命中节点
        Map<String, String> allHitNodeIds = new HashMap<>();

        // 包含自己
        for (T hitNode : hitNodes) {
            allHitNodeIds.putIfAbsent(hitNode.getId(), hitNode.getId());
        }

        // 包含其父级
        if (Objects.nonNull(containAncestors) && containAncestors) {
            for (T hitNode : hitNodes) {
                String parentIds = hitNode.getParentIds();
                for (String s : parentIds.split(",")) {
                    allHitNodeIds.putIfAbsent(s, s);
                }
            }
        }

        // 包含其子级
        if (Objects.nonNull(containDescendants) && containDescendants) {
            for (T hitNode : hitNodes) {
                String parentIds = hitNode.getParentIds();
                String currentNodeId = parentIds + "," + hitNode.getId() + ",";
                for (T t : sourceList) {
                    if (t.getParentIds().contains(currentNodeId)) {
                        allHitNodeIds.putIfAbsent(t.getId(), t.getId());
                    }
                }
            }
        }

        // 包含其同级
        if (Objects.nonNull(containBrothers) && containBrothers) {
            for (T hitNode : hitNodes) {
                String parentIds = hitNode.getParentIds();
                for (T t : sourceList) {
                    if (t.getParentIds().equals(parentIds)) {
                        allHitNodeIds.putIfAbsent(t.getId(), t.getId());
                    }
                }
            }
        }

        // 过滤，并生成树形
        sourceList = sourceList.stream().filter(t -> allHitNodeIds.containsKey(t.getId())).collect(Collectors.toList());
        if (returnList) {
            return sourceList;
        }
        return buildByRecursive(sourceList, null, rootParentValue);
    }

//    private <T extends TreeNode> List<T> expandTree(List<T> treeNodeList) {
//        return expandTree(treeNodeList, null);
//    }
//
//    /**
//     * 展开树形成列表
//     * 展开后如下：["1i","1j","2j","1j1k","1j2k","2j1k","2j2k"] 不一定是想要的结果
//     *
//     * @param treeNodeList
//     * @param <T>
//     */
//    private <T extends TreeNode> List<T> expandTree(List<T> treeNodeList, Class bean) {
//
//        if (Objects.isNull(bean)) {
//            try {
//                T temp = treeNodeList.get(0);
//                // ParameterizedType参数化类型，即泛型
//                ParameterizedType p = ((ParameterizedType) temp.getClass()
//                        .getGenericSuperclass());
//                //getActualTypeArguments获取参数化类型的数组，泛型可能有多个
//                bean = (Class<T>) p.getActualTypeArguments()[0];
//            } catch (Exception e) {
//                throw new ServiceException("获取List泛型的Class失败");
//            }
//        }
//
//
//        List<T> expandedList = new ArrayList<>();
//        Map<String, String> temp = new HashMap<>();
//        AtomicInteger atomicInteger = new AtomicInteger(1000);
//        ArrayDeque<T> nodeQueue = new ArrayDeque<>();
//        nodeQueue.addAll(treeNodeList);
//        try {
//            while (!nodeQueue.isEmpty()) {
//                T t = nodeQueue.pop();
//                List<T> children = t.getChildren();
//                // 深度copy一个当前node并将children置空
//                T n = (T) BeanUtil.copyProperties(t, bean, "children");
//                if (!temp.containsKey(n.getId())) {
//                    expandedList.add(n);
//                    temp.putIfAbsent(n.getId(), n.getId());
//                }
//                if (CollectionUtil.isNotEmpty(children)) {
//                    for (T child : children) {
//                        // 继续往队列放数据
//                        nodeQueue.addLast(child);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            log.error(e.getMessage(), e);
//            throw e;
//        }
//        return expandedList;
//    }

    public static <T extends TreeNode>  List<T> treeToList(List<T> treeNodeList) {
        return treeToList(treeNodeList, null);
    }

    /**
     * 展开树形成列表
     * 展开后如下：["1i","1j","1j1k","1j2k","2j","2j1k","2j2k"] 可直接导出用
     *
     * @param treeNodeList
     * @param <T>
     */
    public static <T extends TreeNode> List<T> treeToList(List<T> treeNodeList, Class bean) {
        if (Objects.isNull(bean)) {
            try {
                T temp = treeNodeList.get(0);
                // ParameterizedType参数化类型，即泛型
                ParameterizedType p = ((ParameterizedType) temp.getClass()
                        .getGenericSuperclass());
                //getActualTypeArguments获取参数化类型的数组，泛型可能有多个
                bean = (Class<T>) p.getActualTypeArguments()[0];
            } catch (Exception e) {
                throw new ServiceException("获取List泛型的Class失败");
            }
        }
        List<T> expandedList = new ArrayList<>();
        Class finalBean = bean;
        treeNodeList.stream().forEach(node -> {
            getChild(node, expandedList, finalBean);
        });
        return expandedList;
    }

    public <T extends TreeNode> void getChild(T t, List<T> expandedList, Class bean) {

        List<T> children = t.getChildren();
        // 深度copy一个当前node并将children置空
        T n = (T) BeanUtil.copyProperties(t, bean, "children");
        expandedList.add(n);
        if (CollectionUtil.isNotEmpty(children)) {
            children.stream().forEach(node -> {
                getChild(node, expandedList, bean);
            });
        }
    }

    public static void test(String[] args) {
        List<TreeNode> list = new ArrayList<>();
        for (int i = 1; i < 2; i++) {
            TreeNode treeNode = new TreeNode();
            treeNode.setId(i + "i");
            for (int j = 1; j < 3; j++) {
                TreeNode treeNode2 = new TreeNode();
                treeNode2.setId(j + "j");
                treeNode2.setParentId(i + "i");
                list.add(treeNode2);
                for (int k = 1; k < 3; k++) {
                    TreeNode treeNode3 = new TreeNode();
                    treeNode3.setId(String.valueOf(j) + "j" + k + "k");
                    treeNode3.setParentId(j + "j");
                    list.add(treeNode3);
                }
            }
            list.add(treeNode);
        }
        System.out.println("0 ---- " + JSONUtil.toJsonStr(list));
        List<TreeNode> tree = TreeUtils2.buildByRecursive(list, TreeNode.class, "0");

        System.out.println("1 ---- " + JSONUtil.toJsonStr(tree));

        List<TreeNode> list2 = TreeUtils2.treeToList(tree, TreeNode.class);
        System.out.println("2 ---- " + JSONUtil.toJsonStr(list2.stream().map(t -> t.getId()).collect(Collectors.toList())));

//        List<TreeNode> list3 = TreeUtil.expandTree(tree, TreeNode.class);
//        System.out.println("3 ---- " + JSONUtil.toJsonStr(list3.stream().map(t -> t.getId()).collect(Collectors.toList())));
    }

}
