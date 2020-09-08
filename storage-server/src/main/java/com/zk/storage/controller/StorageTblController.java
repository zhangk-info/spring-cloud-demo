package com.zk.storage.controller;


import com.zk.commons.exception.ErrorCode;
import com.zk.commons.exception.ServiceException;
import com.zk.storage.service.IStorageTblService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author
 * @since 2020-09-08
 */
@RestController
@RequestMapping("/storage/storage-tbl")
public class StorageTblController {
    @Resource
    private IStorageTblService storageTblService;

    @PostMapping("deduct")
    void deduct(String commodityCode, int count) {
        if (!commodityCode.equals("1") || count < 0) {
            throw new ServiceException(ErrorCode.PARAMS_ERR, "货物编号或数量");
        }
        storageTblService.deduct(commodityCode, count);
    }
}
