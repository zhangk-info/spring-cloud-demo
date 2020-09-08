package com.zk.storage;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        value = "storage-server",
        contextId = "StorageService",
        path = "storage/"
)
public interface StorageService {

    @PostMapping("storage-tbl/deduct")
    void deduct(@RequestParam(name = "commodityCode") String commodityCode, @RequestParam(name = "count") int count);
}
