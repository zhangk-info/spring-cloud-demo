package com.zk.es.service.impl;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.zk.es.entity.Documents;
import com.zk.es.repository.DocumentsRepository;
import com.zk.es.service.IDocumentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author
 */
@Service
public class DocumentsServiceImpl implements IDocumentsService {

    @Autowired
    private DocumentsRepository documentsRepository;

    @Override
    public void save(Documents documents) {
        documents.setCreateDateTime(LocalDateTimeUtil.now());
        documentsRepository.save(documents);
    }

    @Override
    public void removeByFileDataId(String fileDataId) {
        List<Documents> documents = documentsRepository.findAllByFileDataId(Long.parseLong(fileDataId));
        documents.forEach(t -> {
            documentsRepository.deleteById(t.getId());
        });
    }

    @Override
    public Documents getLastCleanDataByDataIdAndCategory(String dataId, String category) {
        return documentsRepository.getLastCleanDataByDataIdAndCategory(dataId, category);
    }

    @Override
    public Page<Documents> findAllByFileData_IdAndStatus(String fileDataId, String status, String page, String size) {
        Pageable pageable = PageRequest.of(Integer.valueOf(page), Integer.valueOf(size));
        return documentsRepository.findAllByFileDataIdAndStatus(Long.parseLong(fileDataId), status, pageable);
    }
}
