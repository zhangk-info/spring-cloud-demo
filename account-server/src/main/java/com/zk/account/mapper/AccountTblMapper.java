package com.zk.account.mapper;

import com.zk.account.entity.AccountTbl;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 
 * @since 2020-09-08
 */
public interface AccountTblMapper extends BaseMapper<AccountTbl> {

    void debit(String userId, int money);

    int getLeftMonty(String userId, int money);
}
