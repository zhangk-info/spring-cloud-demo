package com.zk.storage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zk.storage.entity.StorageTbl;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author
 * @since 2020-09-08
 */
public interface StorageTblMapper extends BaseMapper<StorageTbl> {

    int getLeftCount(String commodityCode, int count);

    void deduct(String commodityCode, int count);
}
