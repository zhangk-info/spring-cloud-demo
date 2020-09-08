package com.zk.order.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.zk.commons.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 *
 * </p>
 *
 * @author
 * @since 2020-09-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("order_tbl")
@ApiModel(value = "OrderTbl对象", description = "")
public class OrderTbl extends BaseEntity {


    private static final long serialVersionUID = 1L;

    public String userId;

    public String commodityCode;

    public Integer count;

    public Integer money;

}
