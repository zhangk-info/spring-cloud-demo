package com.zk.es.service.impl;

import com.zk.es.entity.Documents;
import com.zk.es.repository.DocumentsRepository;
import com.zk.es.service.IDocumentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        documentsRepository.save(documents);
    }
}
