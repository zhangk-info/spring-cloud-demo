package com.zk.es.repository;

import com.zk.es.entity.Documents;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author
 * @since 2020-09-08
 */
public interface DocumentsRepository extends ElasticsearchRepository<Documents, Long> {

    // 支持自定义方法
    // 具体使用和更多的使用方法参照：https://docs.spring.io/spring-data/elasticsearch/docs/3.1.3.RELEASE/api/
    public List<Documents> findByCategory(String category);

    Page<Documents> findAllByFileData_IdAndStatus(Long fileDataId, String status, Pageable pageable);

    List<Documents> findAllByFileData_IdAndStatus(Long fileDataId, String status);

    List<Documents> findAllByFileData_Id(Long fileDataId);

    @Transactional
    @Query(value = "delete from t_file_data_records where file_data_id = :fileDataId")
    void deleteAllByFileData_Id(@Param("fileDataId") Long fileDataId);

    long countAllByStatusAndFileData_Id(String status, Long fileDataId);

    long countAllByFileData_Id(Long fileDataId);

    Optional<Documents> findDocumentsById(String id);

    @Query(value = "select d.id from t_file_data_records d where d.status = 'WAITTING' and d.file_data_id = :fileDataId limit 1")
    @Transactional
    Long findOnByFailedOfServer(@Param("fileDataId") Long fileDataId);

    /**
     * 当dataId不在表中存在的时候会全量扫描查询出来的数据集导致查询很慢，所以过滤了只找7天的
     * todo 如果10天都没有这个类型的文件下发 这里就成了一个bug
     *
     * @param dataId
     * @param category
     * @return
     */
    @Query(value = "select * from t_file_data_records d where d.data_id = :dataId and category = :category  and to_days(now())-to_days(d.created_date_time) <=10 order by created_date_time desc limit 1")
    @Transactional
    Documents getLastCleanDataByDataIdAndCategory(@Param("dataId") String dataId, @Param("category") String category);

}
