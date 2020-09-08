package com.zk.storage.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zk.commons.exception.ErrorCode;
import com.zk.commons.exception.ServiceException;
import com.zk.storage.entity.StorageTbl;
import com.zk.storage.mapper.StorageTblMapper;
import com.zk.storage.service.IStorageTblService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author
 * @since 2020-09-08
 */
@Service
public class StorageTblServiceImpl extends ServiceImpl<StorageTblMapper, StorageTbl> implements IStorageTblService {

    @Override
    public void deduct(String commodityCode, int count) {
        int leftCount = this.baseMapper.getLeftCount(commodityCode, count);
        //扣除之后金额小于0，抛出异常
        if (leftCount < 0) {
            throw new ServiceException(ErrorCode.STORAGE_IS_NOT_ENOUGH);
        }
        this.baseMapper.deduct(commodityCode, count);
    }
}
