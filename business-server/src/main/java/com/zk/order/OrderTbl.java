package com.zk.order;

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

    private String userId;

    private String commodityCode;

    private Integer count;

    private Integer money;

}
