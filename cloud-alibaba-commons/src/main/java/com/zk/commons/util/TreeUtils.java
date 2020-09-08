/**
 * Copyright (c) 2017 yunmel Co., Ltd. Ltd . All rights reserved.
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * <p>
 * The above copyright notice and this authentication notice shall be
 * included in all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.zk.commons.util;


import com.zk.commons.entity.BaseTree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <b>TODO(这里用一句话描述这个类的作用)</b><br/>
 * <br><b>package:</b> com.yunget.config.util
 * <br><b>class:</b> TreeUtils
 * <br><b>date:</b> 2017年11月28日 下午7:56:41
 *
 * @author: xuyq(pazsolr @ gmail.com)
 * @since 1.0
 */
public class TreeUtils {
    /**
     * 转换成List形式树结构 (如果是缓存的list，请务必深度copy一个)
     *
     * @param source
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <S, T extends BaseTree> List<T> toTreeNodeList(List<S> source, Class<T> bean) {

        final Map<Long, T> nodes = new HashMap<>();

//         ConvertUtils.register(new Converter() {
//        
//         @Override
//         public <T> T convert(Class<T> arg0, Object arg1) {
//         // TODO Auto-generated method stub
//         return null;
//         }
//         }, java.util.Date.class);


        // 深度copy一个，防止源list内部结构改变
        List<T> list = BeanConvertUtils.convert(source, bean);// Collections3.copyTo(source, bean);

        // 所有节点记录下来
        for (T node : list) {
            node.setLevel(-1);
            node.setHasChild(false);
            // node.put("nodes", new ArrayList<T>());
            nodes.put(node.getId(), node);
        }

        final BaseTree root = new BaseTree();
        root.setLevel(0);
        root.setChildren(new ArrayList<BaseTree>());
        nodes.put(0L, (T) root);

        for (T node : list) {
            final T parent = nodes.get(node.getPid());
            if (parent == null) {
                ((ArrayList<T>) root.getChildren()).add(node);
                continue;
                // throw new RuntimeException("子节点有父级id，却没有找到此父级的对象");
            } else {
                // 添加子节点
                if (null == parent.getChildren()) {
                    parent.setChildren(new ArrayList<BaseTree>());
                }
                ((List<T>) parent.getChildren()).add(node);
            }
        }

        int max = 0;
        for (T node : list) {
            max = Math.max(resolveLevel(node, nodes), max);
        }

        return (List<T>) root.getChildren();
    }

    // 递归找level
    private static <T extends BaseTree> int resolveLevel(final T node, final Map<Long, T> nodes) {
        // System.out.println(node.getIntValue("level"));
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
