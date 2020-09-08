package com.zk.account;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(
        value = "account-server",
        contextId = "AccountService",
        path = "account/"
)
public interface AccountService {

    @PostMapping("account-tbl/debit")
    public void debit(@RequestParam(name = "userId") String userId, @RequestParam(name = "money") int money);
}
