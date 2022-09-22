package com.zk.account.service;

import com.zk.account.entity.AccountTbl;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author
 * @since 2020-09-08
 */
public interface IAccountTblService extends IService<AccountTbl> {

    /**
     * 从用户账户中借出
     */
    void debit(String userId, int money);
}
