package com.zk.account.controller;


import com.zk.account.service.IAccountTblService;
import com.zk.commons.exception.ErrorCode;
import com.zk.commons.exception.ServiceException;
import org.springframework.web.bind.annotation.GetMapping;
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
@RequestMapping("/account/account-tbl")
public class AccountTblController {
    @Resource
    private IAccountTblService accountTblService;

    @PostMapping("/debit")
    public void debit(String userId, int money) {

        if (!userId.equals("1") || money < 0) {
            throw new ServiceException(ErrorCode.PARAMS_ERR, "用户或者金额");
        }
        accountTblService.debit(userId, money);
    }
}
