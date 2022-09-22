package com.zk.commons.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @date 2017年11月9日23:33:45
 */
@Data
@ApiModel(value = "树形节点")
public class TreeNode<T> {
    @ApiModelProperty(value = "当前节点id")
    protected String id;
    @ApiModelProperty(value = "父节点id")
    protected String parentId;
    @ApiModelProperty(value = "树状结构id，当层级结构不是同一对象，id会重复时，可使用此字段")
    protected Integer treeId;
    @ApiModelProperty(value = "父级树状结构id")
    protected Integer parentTreeId;
    @ApiModelProperty(value = "子节点列表")
    protected List<T> children = new ArrayList<>();

    private Integer level;
    private Boolean hasChild;

    private String parentIds;

    public void addChild(T node) {
        children.add(node);
    }
}
