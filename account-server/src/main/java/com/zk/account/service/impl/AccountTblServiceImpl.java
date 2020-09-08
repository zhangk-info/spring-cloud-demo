package com.zk.account.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zk.account.entity.AccountTbl;
import com.zk.account.mapper.AccountTblMapper;
import com.zk.account.service.IAccountTblService;
import com.zk.commons.exception.ErrorCode;
import com.zk.commons.exception.ServiceException;
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
public class AccountTblServiceImpl extends ServiceImpl<AccountTblMapper, AccountTbl> implements IAccountTblService {

    /**
     * 从账户扣除储值
     *
     * @param userId
     * @param money
     */
    @Override
    public void debit(String userId, int money) {
        int leftMoney = this.baseMapper.getLeftMonty(userId, money);
        //扣除之后金额小于0，抛出异常
        if (leftMoney < 0) {
            throw new ServiceException(ErrorCode.ACCOUNT_MONEY_IS_NOT_ENOUGH);
        }
        this.baseMapper.debit(userId, money);
    }
}
