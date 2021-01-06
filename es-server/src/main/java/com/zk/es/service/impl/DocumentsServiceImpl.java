package com.zk.es.service.impl;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.zk.es.entity.Documents;
import com.zk.es.repository.DocumentsRepository;
import com.zk.es.service.IDocumentsService;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

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
    ElasticsearchRestTemplate template;
    @Autowired
    private DocumentsRepository documentsRepository;
    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

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
        // 用@query实现不了order by的功能
        return documentsRepository.getLastCleanDataByDataIdAndCategory(dataId, category);
    }

    @Override
    public Documents getLastCleanDataByDataIdAndCategory2(String dataId, String category) {
        return this.searchData(dataId, category);
    }

    /**
     * 检索数据
     *
     * @throws IOException
     */
    public Documents searchData(String dataId, String category) {
        QueryBuilder queryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.matchQuery("data_id",dataId))
                .must(QueryBuilders.matchQuery("category",category));
        Iterable<Documents> iterable = documentsRepository.search(queryBuilder);
        AtomicReference<Documents> documents = null;
        iterable.forEach(t -> {
            documents.set(t);
        });
        return documents.get();
    }

    @Override
    public Page<Documents> findAllByFileData_IdAndStatus(String fileDataId, String status, String page, String size) {
        Pageable pageable = PageRequest.of(Integer.valueOf(page), Integer.valueOf(size));
        return documentsRepository.findAllByFileDataIdAndStatus(Long.parseLong(fileDataId), status, pageable);
    }
}
