package com.zk.account.entity;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import com.zk.commons.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("account_tbl")
@ApiModel(value="AccountTbl对象", description="")
public class AccountTbl extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String userId;

    private Integer money;

}
