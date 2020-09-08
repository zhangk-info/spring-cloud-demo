package com.zk.storage.service;

import com.zk.storage.entity.StorageTbl;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 
 * @since 2020-09-08
 */
public interface IStorageTblService extends IService<StorageTbl> {

    /**
     * 扣除存储数量
     */
    void deduct(String commodityCode, int count);
}
