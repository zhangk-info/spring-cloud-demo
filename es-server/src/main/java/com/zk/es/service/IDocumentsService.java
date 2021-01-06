package com.zk.es.service;

import com.zk.es.entity.Documents;
import org.springframework.data.domain.Page;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author
 */
public interface IDocumentsService {

    void save(Documents documents);

    void removeByFileDataId(String fileDataId);

    Documents getLastCleanDataByDataIdAndCategory(String dataId, String category);

    Documents getLastCleanDataByDataIdAndCategory2(String dataId, String category);

    Page<Documents> findAllByFileData_IdAndStatus(String fileDataId, String status, String page, String size);
}
