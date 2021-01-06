package com.zk.es.controller;


import com.zk.es.entity.Documents;
import com.zk.es.service.IDocumentsService;
import org.springframework.data.domain.Page;
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
@RequestMapping("/api/v1/")
public class EsController {
    @Resource
    private IDocumentsService documentService;

    /**
     * 保存
     * @param documents
     */
    @PostMapping("es/save")
    void save(Documents documents) {
        documentService.save(documents);
    }

    /**
     * 分页读取
     * @param fileDataId
     * @param status
     * @param page
     * @param size
     * @return
     */
    @GetMapping("es")
    Page<Documents> getByFileDataIdAndStatus(String fileDataId, String status, String page, String size) {
        return documentService.findAllByFileData_IdAndStatus(fileDataId, status, page, size);
    }

    /**
     * 批量删除
     * @param fileDataId
     */
    @GetMapping("es/remove-by-file-data-id")
    void removeByFileDataId(String fileDataId) {
        documentService.removeByFileDataId(fileDataId);
    }

    /**
     * 通过@query的方式查询
     * @param dataId
     * @param category
     * @return
     */
    @GetMapping("es/last-clean")
    Documents getLastCleanDataByDataIdAndCategory(String dataId, String category) {
        return documentService.getLastCleanDataByDataIdAndCategory(dataId, category);
    }

    /**
     * 通过@query的方式查询
     * @param dataId
     * @param category
     * @return
     */
    @GetMapping("es/last-clean2")
    Documents getLastCleanDataByDataIdAndCategory2(String dataId, String category) {
        return documentService.getLastCleanDataByDataIdAndCategory2(dataId, category);
    }

}
