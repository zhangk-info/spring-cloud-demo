package com.zk.es.repository;

import com.zk.es.entity.Documents;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
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
public interface DocumentsRepository extends ElasticsearchRepository<Documents, String> {

    // 支持自定义方法
    // 具体使用和更多的使用方法参照：https://docs.spring.io/spring-data/elasticsearch/docs/3.1.3.RELEASE/api/
//    public List<Documents> findByCategory(String category);

    Page<Documents> findAllByFileDataIdAndStatus(Long fileDataId, String status, Pageable pageable);

    List<Documents> findAllByFileDataIdAndStatus(Long fileDataId, String status);

    List<Documents> findAllByFileDataId(Long fileDataId);

//    long countAllByStatusAndFileDataId(String status, Long fileDataId);
//
//    long countAllByFileData_Id(Long fileDataId);

    Optional<Documents> findDocumentsById(String id);

    //    @Query(value = "select d.id from t_file_data_records d where d.status = 'WAITTING' and d.file_data_id = :fileDataId limit 1")

    /**
     * @param dataId
     * @param category
     * @return
     */
//    @Query(value = "select * from t_file_data_records d where d.data_id = :dataId and category = :category order by created_date_time desc limit 1")
    @Transactional
    @Query(value = "" +
            "           {\"bool\":" +
            "               {\"must\":" +
            "                   [ " +
            "                       {\"term\":{\"data_id\":\"dataId\"} }" + // 和jpa不同 下标是从0开始的 这里还有问题 传入值是dataId找不到，直接写dataId就能找到
            "                       ,{\"term\":{\"category\":\"category\"} }" +
            "                   ] " +
            "               }" +
            "           }" +
            // 已测试 声明式查询仅仅只是设置查询时候query:属性的值 所以不能进行排序，聚合，分页等操作
//            "       ,\"from\":\"0\"" +
//            "       ,\"to\":\"1\"" +
//            "       ,\"sort\": [" +
//            "           {\"created_date_time\":{\"order\":\"desc\"}}" +
//            "       ]" +
            "   ")
//    @Query(value = "{\"match_all\":{}}")
    Documents getLastCleanDataByDataIdAndCategory( String dataId, String category);

    @Transactional
    @Query(value = "{\"bool\":{\"must\":[{\"term\":{\"status\": \"WAITTING\"} },{\"term\":{\"file_data_id\": ?1} } ]}} " +
            "       ,\"from\":\"0\"" +
            "       ,\"to\":\"1\"")
    Long findOnByFailedOfServer(Long fileDataId);

}
