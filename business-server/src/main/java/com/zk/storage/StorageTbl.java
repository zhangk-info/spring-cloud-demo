package com.zk.storage;

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
@TableName("storage_tbl")
@ApiModel(value = "StorageTbl对象", description = "")
public class StorageTbl extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String commodityCode;

    private Integer count;

}
