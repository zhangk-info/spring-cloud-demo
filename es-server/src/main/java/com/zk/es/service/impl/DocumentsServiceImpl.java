package com.zk.es.service.impl;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.zk.es.entity.Documents;
import com.zk.es.repository.DocumentsRepository;
import com.zk.es.service.IDocumentsService;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

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
        // 简单的查询 全匹配
//        Criteria miller = new Criteria();
//        miller.and("data_id").is(dataId);
//        miller.and(
//                        new Criteria().or("category").is(category)
//                                .or("category").is(category)
//                );
//        Query query = new CriteriaQuery(miller);


        // 复杂的查询 match等
        QueryBuilder queryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.matchQuery("data_id", dataId))
                .must(QueryBuilders.matchQuery("category", category));

        SortBuilder sortBuilder = SortBuilders.fieldSort("created_date_time").order(SortOrder.DESC);

        // 使用NativeSearchQuery来完成 具有复杂查询或无法使用CriteriaAPI表示的查询（例如，在构建查询和使用聚合时）
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(queryBuilder)
                .withPageable(PageRequest.of(0, 10))
                .withSort(sortBuilder)
                .build();

        // search弃用 since 4.0, use #searchQuery(Query), standard repository method naming or @Query annotated methods, or ElasticsearchOperations.
//        Page<Documents> page = documentsRepository.search(searchQuery);
//        List<Documents> list = page.getContent();
//        AtomicReference<Documents> documents = new AtomicReference<>();
//        list.forEach(t -> {
//            documents.set(t);
//        });
//        return documents.get();
        SearchHits<Documents> searchHits = elasticsearchOperations.search(searchQuery, Documents.class);
//        List<SearchHit<Documents>> hits = searchHits.getSearchHits(); // 内围的 hits 数组
        List<Documents> list = searchHits.stream().map(t -> t.getContent()).collect(Collectors.toList());
        if (list != null) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public Page<Documents> findAllByFileData_IdAndStatus(String fileDataId, String status, String page, String size) {
        Pageable pageable = PageRequest.of(Integer.valueOf(page), Integer.valueOf(size));
        return documentsRepository.findAllByFileDataIdAndStatus(Long.parseLong(fileDataId), status, pageable);
    }
}
