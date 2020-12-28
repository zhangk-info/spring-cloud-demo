package com.zk.es.controller;


import com.zk.es.entity.Documents;
import com.zk.es.service.IDocumentsService;
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
@RequestMapping("/api/v1/")
public class EsController {
    @Resource
    private IDocumentsService documentService;

    @PostMapping("save")
    void save(Documents documents) {
        documentService.save(documents);
    }
}
